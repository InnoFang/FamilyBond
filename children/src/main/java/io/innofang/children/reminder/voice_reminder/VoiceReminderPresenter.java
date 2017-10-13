package io.innofang.children.reminder.voice_reminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.SdCardUtil;
import io.innofang.base.widget.custom_popup_window.CustomPopupWindow;
import io.innofang.children.R;
import io.innofang.children.settings.SettingsActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 16:10
 * Description:
 */


public class VoiceReminderPresenter implements VoiceReminderContract.Presenter {

    private Context mContext;
    private VoiceReminderContract.View mView;
    private BmobRecordManager mRecordManager;

    private ImageView mRecordImageView;
    private RelativeLayout mRecordLayout;
    private TextView mVoiceTipsTextView;
    private FloatingActionButton mSpeakFab;

    private Drawable[] mVoiceAnimDrawables;// 话筒动画
    private List<BmobIMConversation> mIMConversations = new ArrayList<>();

    private BmobIMConversation mConversationManager;

    public VoiceReminderPresenter(VoiceReminderContract.View view) {
        mView = view;
        mView.setPresenter(this);

        mContext = mView.getContext();
        mRecordImageView = mView.getRecordImageView();
        mRecordLayout = mView.getVoiceLayout();
        mVoiceTipsTextView = mView.getVoiceTipsTextView();
        mSpeakFab = mView.getVoiceRecordFab();
        mVoiceAnimDrawables = mView.getVoiceAnimDrawable();

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (null != list) {
            mIMConversations.addAll(list);
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void chooseContact() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user.getContact().isEmpty()) {
            new AlertDialog.Builder(mContext)
                    .setMessage("联系人为空，点击确定添加")
                    .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else {
            final CustomPopupWindow popupWindow = new CustomPopupWindow.Builder(mContext)
                    .setContentView(R.layout.layout_add_contact)
                    .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setFocus(true) // 设置是否可以获取焦点
                    .setOutsideCancel(true)  // 设置点击外部取消
                    .setElevation(5)
                    .setAnimStyle(R.style.PopupWindowAnimStyle)
                    .build()
                    .showAtLocation(R.layout.activity_settings, Gravity.CENTER, 0, 0);

            final ListView listView = (ListView) popupWindow.findView(R.id.select_item_list_view);

            final List<String> usernames = new ArrayList<>();
            for (User contact : user.getContact()) {
                usernames.add(contact.getUsername());
            }

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    mContext, android.R.layout.simple_list_item_1, usernames);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String username = usernames.get(position);
                    mView.setContact(username);
                    checkConversations(username);
                    popupWindow.dismiss();
                }
            });

            popupWindow.findView(R.id.input_contact_title_text_view).setVisibility(View.GONE);
            popupWindow.findView(R.id.popup_edit_text).setVisibility(View.GONE);
            popupWindow.findView(R.id.ok_button).setVisibility(View.GONE);
        }
    }


    @Override
    public void initRecordManager() {
        mRecordManager = BmobRecordManager.getInstance(mContext);
        mRecordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumeChanged(int value) {
                mRecordImageView.setImageDrawable(mVoiceAnimDrawables[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                Log.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    mSpeakFab.setPressed(false);
                    mSpeakFab.setClickable(false);
                    // 取消录音框
                    mRecordLayout.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mSpeakFab.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    public class VoiceTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!SdCardUtil.checkSdCard()) {
                        mView.showInfo("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        mRecordLayout.setVisibility(View.VISIBLE);
                        mView.setVoiceTipsText(R.string.voice_cancel_tips);
                        // 开始录音
                        mRecordManager.startRecording(mConversationManager.getConversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        mView.setVoiceTipsText(R.string.voice_cancel_tips);
                        mVoiceTipsTextView.setTextColor(Color.RED);
                    } else {
                        mView.setVoiceTipsText(R.string.voice_up_tips);
                        mVoiceTipsTextView.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    mRecordLayout.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            mRecordManager.cancelRecording();
                            Log.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = mRecordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(mRecordManager.getRecordFilePath(mConversationManager.getConversationId()), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                mRecordLayout.setVisibility(View.GONE);
                                mView.showVoiceShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }


    private void sendVoiceMessage(String local, int length) {
        // 发送消息：发送本地音频文件消息
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        //可设置额外信息-开发者设置的额外信息，需要开发者自己从extra中取出来
        Map<String, Object> map = new HashMap<>();
        map.put("level", "voice");
        // 自定义消息： 给消息设置额外信息
        audio.setExtraMap(map);
        //设置语音文件时长：可选
//        audio.setDuration(length);
        mConversationManager.sendMessage(audio, listener);
    }

    private void checkConversations(String username) {
        if (null != mIMConversations && !mIMConversations.isEmpty()) {

            for (BmobIMConversation conversationEntrance : mIMConversations) {
                if (conversationEntrance.getConversationTitle().equals(username)) {
                    mConversationManager = BmobIMConversation.obtain(
                            BmobIMClient.getInstance(), conversationEntrance);
                }
            }
        } else {
            BmobUtil.query(username, new BmobEvent.onQueryListener() {
                @Override
                public boolean beforeQuery() {
                    return true;
                }

                @Override
                public void querySuccessful(final List<User> list) {
                    BmobUtil.connect(list.get(0), new BmobEvent.onConnectListener() {
                        @Override
                        public void connectSuccessful(User user) {
                            BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), null);
                            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                            mIMConversations.add(conversationEntrance);
                            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
                        }

                        @Override
                        public void connectFailed(String error) {
                            mView.showInfo(error);
                        }
                    });
                }

                @Override
                public void queryFailed(BmobException e) {
                    mView.showInfo(e.getMessage());
                }
            });

        }
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
//            Log.i("tag", "onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
            if (e != null) {
                mView.showInfo("Error " + e.getMessage());
            } else {
                mView.showInfo("发送成功");
            }
        }
    };


}

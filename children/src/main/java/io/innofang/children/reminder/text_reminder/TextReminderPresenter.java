package io.innofang.children.reminder.text_reminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.widget.custom_popup_window.CustomPopupWindow;
import io.innofang.children.R;
import io.innofang.children.settings.SettingsActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 15:13
 * Description:
 */


public class TextReminderPresenter implements TextReminderContract.Presenter {

    private TextReminderContract.View mView;
    private FloatingActionButton mFab;
    private Context mContext;

    private List<BmobIMConversation> mIMConversations = new ArrayList<>();

    private BmobIMConversation mConversationManager;

    public TextReminderPresenter(TextReminderContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mFab = mView.getSendReminderButton();
        mContext = mView.getContext();

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
                    checkConversations(username);
                    popupWindow.dismiss();
                    sendReminder();
                }
            });

            popupWindow.findView(R.id.input_contact_title_text_view).setVisibility(View.GONE);
            popupWindow.findView(R.id.popup_edit_text).setVisibility(View.GONE);
            popupWindow.findView(R.id.ok_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void sendReminder() {
        String text = mView.getReminderText();
        if (!TextUtils.isEmpty(text)) {
            BmobIMTextMessage msg = new BmobIMTextMessage();
            msg.setContent(text);
            //可随意设置额外信息
            Map<String, Object> map = new HashMap<>();
            map.put("level", "text");
            msg.setExtraMap(map);
            mConversationManager.sendMessage(msg, listener);
        } else {
            mView.showInfo("文本不能为空");
        }
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
        public void done(BmobIMMessage msg, BmobException e) {
            if (e != null) {
                mView.showInfo("Error " + e.getMessage());
            } else {
                mView.showInfo("发送成功");
            }
        }
    };
}

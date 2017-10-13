package io.innofang.children;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.SMS;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.SMSMessage;
import io.innofang.base.util.bmob.BmobEvent;
import io.innofang.base.util.bmob.BmobUtil;
import io.innofang.base.util.common.L;
import io.innofang.base.util.common.RequestPermissions;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.reminder.ReminderActivity;
import io.innofang.children.settings.SettingsActivity;
import io.innofang.medically.heat_beat.HeartBeatActivity;

@Route(path = "/children/1")
public class ChildrenActivity extends BaseActivity {


    @BindView(R2.id.card_view_pager)
    ViewPager mCardViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        ButterKnife.bind(this);

        init();
        showAddContactTip();
        checkConnect();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.card_medically_exam, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.message_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.card_communication, R.drawable.communication));
        mCardAdapter.addCardItem(new CardItem(R.string.common_settings, R.drawable.settings));

        mCardShadowTransformer = new ShadowTransformer(mCardViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mCardViewPager.setAdapter(mCardAdapter);
        mCardViewPager.setPageTransformer(false, mCardShadowTransformer);
        mCardViewPager.setOffscreenPageLimit(3);
        mCardViewPager.setCurrentItem(1);
        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        RequestPermissions.requestRuntimePermission(
                                new String[]{Manifest.permission.CAMERA}, new RequestPermissions.OnRequestPermissionsListener() {
                                    @Override
                                    public void onGranted() {
//                                        ARouter.getInstance().build("/heart_beat/1").navigation();
                                        startActivity(new Intent(ChildrenActivity.this, HeartBeatActivity.class));
                                    }

                                    @Override
                                    public void onDenied(List<String> deniedPermission) {

                                    }
                                }
                        );
                        break;
                    case 1:
                        startActivity(new Intent(ChildrenActivity.this, ReminderActivity.class));
                        break;
                    case 2:
                        toast("与父母交流");
                        break;
                    case 3:
                        startActivity(new Intent(ChildrenActivity.this, SettingsActivity.class));
                        break;
                }
            }
        });
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(MessageEvent event) {
        L.i("onHandleMessageEvent: is called");
        handleMessage(event);
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
//    @Subscribe
//    public void onHandleMessageEvent(OfflineMessageEvent event) {
//        L.i("onHandleMessageEvent: is called");
//        handleMessage(event);
//    }


    private void handleMessage(MessageEvent event) {
        L.i("handle message");
//        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
//        BmobIMMessage message = list.get(0).getMessages().get(0);
        BmobIMMessage message = event.getMessage();
//        if (null != list) {
            if (message.getMsgType().equals(SMSMessage.SMS)) {
                toast(message.getContent());
                L.i(event.getConversation().getConversationTitle() + "发来可以短信拦截提示");

                SMS sms = SMSMessage.convert(message);
                StringBuilder sb = new StringBuilder();
                String s = sb.append("时间：").append(sms.getTime()).append("\n")
                        .append("地址：").append(sms.getAddress()).append("\n")
                        .append("内容：").append(sms.getContent()).append("\n")
                        .append("概率：").append(sms.getProbability() + "").append("\n")
                        .toString();
                Log.i("sms", message.getContent());
                Log.i("sms", s);

            }
//        }
    }

    private void showAddContactTip() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user.getContact().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.add_contact)
                    .setMessage(R.string.tip_of_add_contact)
                    .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ChildrenActivity.this, SettingsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }

    private void checkConnect() {

        BmobUtil.connect(BmobUser.getCurrentUser(User.class), new BmobEvent.onConnectListener() {
            @Override
            public void connectSuccessful(User user) {
                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                EventBus.getDefault().post(new BmobIMMessage());
                //会话： 更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                BmobIM.getInstance().
                        updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                user.getUsername(), null));
            }

            @Override
            public void connectFailed(String error) {
                toast(error);
            }
        });
        // 连接： 监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast(status.getMsg());
                L.i(BmobIM.getInstance().getCurrentStatus().getMsg());
            }
        });

    }



}

package io.innofang.children;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.SMSMessage;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.bean.greendao.SMS;
import io.innofang.base.bean.greendao.SMSDao;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.NotificationUtil;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.map.MapActivity;
import io.innofang.children.medically_exam.MedicallyExamActivity;
import io.innofang.children.reminder.ReminderActivity;
import io.innofang.children.settings.SettingsActivity;
import io.innofang.children.sms_intercept.SMSInterceptionActivity;

@Route(path = "/children/1")
public class ChildrenActivity extends BaseActivity {


    @BindView(R2.id.card_view_pager)
    ViewPager mCardViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private DaoSession mSession;
    private SMSDao mSmsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        ButterKnife.bind(this);

        init();
        showAddContactTip();
        checkConnect();
        mSession = GreenDaoConfig.getInstance().getDaoSession();
        mSmsDao = mSession.getSMSDao();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.sms_interception, R.drawable.sms_interception));
        mCardAdapter.addCardItem(new CardItem(R.string.message_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.medically_exam_report, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.location, R.drawable.map));
        mCardAdapter.addCardItem(new CardItem(R.string.common_settings, R.drawable.settings));

        mCardShadowTransformer = new ShadowTransformer(mCardViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mCardViewPager.setAdapter(mCardAdapter);
        mCardViewPager.setPageTransformer(false, mCardShadowTransformer);
        mCardViewPager.setOffscreenPageLimit(3);
        mCardViewPager.setCurrentItem(2);
        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(ChildrenActivity.this, SMSInterceptionActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(ChildrenActivity.this, ReminderActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(ChildrenActivity.this, MedicallyExamActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(ChildrenActivity.this, MapActivity.class));
                        break;
                    case 4:
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
        BmobIMMessage message = event.getMessage();
        if (message.getMsgType().equals(SMSMessage.SMS)) {
            toast(message.getContent());
            L.i(event.getConversation().getConversationTitle() + "发来可疑短信拦截提示");

            SMS sms = SMSMessage.convert(message);
            mSmsDao.insert(sms);

            String s = "时间：" + sms.getTime() + "\n" +
                    "地址：" + sms.getAddress() + "\n" +
                    "内容：" + sms.getContent() + "\n" +
                    "概率：" + sms.getProbability() + "\n";
            L.i(s);
            L.i("sms list size: " + mSmsDao.queryBuilder().build().list().size());

            NotificationUtil.create(
                    this,
                    1,
                    new Intent(this, SMSInterceptionActivity.class),
                    R.mipmap.ic_launcher,
                    event.getMessage().getContent(),
                    sms.getContent()
            );
        }
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

package io.innofang.children;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.BpmMessage;
import io.innofang.base.bean.bmob.SMSMessage;
import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.bean.greendao.SMS;
import io.innofang.base.bean.greendao.SMSDao;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.NotificationUtil;
import io.innofang.children.medically_exam_report.MedicallyExamReportActivity;
import io.innofang.children.sms_intercept.SMSInterceptionActivity;

/**
 * Author: Inno Fang
 * Time: 2017/10/14 20:19
 * Description:
 */


public class HandleMessageService extends Service {

    private DaoSession mSession;
    private SMSDao mSmsDao;
    private BpmDao mBpmDao;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = GreenDaoConfig.getInstance().getDaoSession();
        mSmsDao = mSession.getSMSDao();
        mBpmDao = mSession.getBpmDao();
        checkConnect();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                Toast.makeText(HandleMessageService.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        // 连接： 监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                Toast.makeText(HandleMessageService.this, status.getMsg(), Toast.LENGTH_SHORT).show();
                L.i(BmobIM.getInstance().getCurrentStatus().getMsg());
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

    private void handleMessage(MessageEvent event) {
        L.i("handle message");
        BmobIMMessage message = event.getMessage();
        if (message.getMsgType().equals(SMSMessage.SMS)) {
            Toast.makeText(this, message.getContent(), Toast.LENGTH_SHORT).show();
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
        } else if (message.getMsgType().equals(BpmMessage.BPM)) {
            Toast.makeText(this, message.getContent(), Toast.LENGTH_SHORT).show();
            L.i(event.getConversation().getConversationTitle() + "发来心率报告提示");

            Bpm bpm = BpmMessage.convert(message);
            mBpmDao.insert(bpm);

            String s = "时间：" + bpm.getTime() + "\n" +
                    "心率：" + bpm.getBpm() + "\n" +
                    "描述：" + bpm.getDescription() + "\n";
            L.i(s);
            L.i("sms list size: " + mSmsDao.queryBuilder().build().list().size());

            NotificationUtil.create(
                    this,
                    2,
                    new Intent(this, MedicallyExamReportActivity.class),
                    R.mipmap.ic_launcher,
                    event.getMessage().getContent(),
                    "心率检测报告：" + bpm.getBpm() + "bpm，" + bpm.getDescription()
            );

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

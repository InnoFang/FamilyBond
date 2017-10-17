package io.innofang.protectplus;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.BpmMessage;
import io.innofang.base.bean.bmob.SMSMessage;
import io.innofang.base.bean.bmob.ShareMapMessage;
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

import static cn.bmob.v3.BmobUser.getCurrentUser;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 12:44
 * Description:
 */


public class IMMessageHandler extends BmobIMMessageHandler {

    private Context mContext;

    private DaoSession mSession;
    private SMSDao mSmsDao;
    private BpmDao mBpmDao;

    public IMMessageHandler(Context context) {
        mContext = context;

        mSession = GreenDaoConfig.getInstance().getDaoSession();
        mSmsDao = mSession.getSMSDao();
        mBpmDao = mSession.getBpmDao();
    }

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
        L.i(getCurrentUser(User.class).getUsername() + " onMessageReceive: is called");
        executeMessage(messageEvent);
//        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
        L.i(getCurrentUser(User.class).getUsername() + "onOfflineReceive: is called");

        Map<String, List<MessageEvent>> map = offlineMessageEvent.getEventMap();

        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            L.i("用户" + entry.getKey() + "发来" + size + "条消息");
            for (int i = 0; i < size; i++) {
                L.i("消息内容：" + list.get(i).getMessage().getContent());
                //处理每条消息
                executeMessage(list.get(i));
            }
        }
    }

    private void executeMessage(final MessageEvent event) {
        BmobUtil.updateUserInfo(event, new BmobEvent.UpdateCacheListener() {
            @Override
            public void done(BmobException e) {
                BmobIMMessage msg = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
                    //自定义消息类型：0
                    processCustomMessage(msg, event);
                } else {
                    //SDK内部内部支持的消息类型
                    processSDKMessage(msg, event);
                }
            }
        });


    }

    private void processCustomMessage(BmobIMMessage message, MessageEvent event) {
        String type = message.getMsgType();
        String client = BmobUser.getCurrentUser(User.class).getClient();
        if (type.equals(SMSMessage.SMS) && client.equals(User.CHILDREN)) {        // 拦截可疑短信
            Toast.makeText(mContext, message.getContent(), Toast.LENGTH_LONG).show();
            L.i(event.getConversation().getConversationTitle() + "发来可疑短信拦截提示");

            SMS sms = SMSMessage.convert(message);
            mSmsDao.insert(sms);


            String s = "时间：" + sms.getTime() + "\n" +
                    "地址：" + sms.getAddress() + "\n" +
                    "内容：" + sms.getContent() + "\n" +
                    "概率：" + sms.getProbability() + "\n";
            L.i(s);
            L.i("sms list size: " + mSmsDao.queryBuilder().build().list().size());
            showSMSNotification(event, sms);
        } else if (type.equals(BpmMessage.BPM) && client.equals(User.CHILDREN)) { // 心率报告
            Toast.makeText(mContext, message.getContent(), Toast.LENGTH_LONG).show();
            L.i(event.getConversation().getConversationTitle() + "发来心率报告提示");

            Bpm bpm = BpmMessage.convert(message);
            mBpmDao.insert(bpm);

            String s = "时间：" + bpm.getTime() + "\n" +
                    "心率：" + bpm.getBpm() + "\n" +
                    "描述：" + bpm.getDescription() + "\n";
            L.i(s);
            L.i("bpm list size: " + mBpmDao.queryBuilder().build().list().size());
            showBpmNotification(event, bpm);
        } else if ((type.equals(ShareMapMessage.MAP))
                && client.equals(User.PARENTS)) {
           EventBus.getDefault().post(message.getContent());
        }

        L.i(getCurrentUser(User.class).getUsername() + " received. type: " + type + ", client: " + client + ", content: " + message.getContent());
    }

    private void showSMSNotification(MessageEvent event, SMS sms) {
        Intent pendingIntent = new Intent(mContext, SMSInterceptionActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //这里可以是应用图标，也可以将聊天头像转成bitmap
        NotificationUtil.create(
                mContext,
                1,
                pendingIntent,
                R.mipmap.ic_launcher,
                event.getMessage().getContent(),
                sms.getContent()
        );
    }

    private void showBpmNotification(MessageEvent event, Bpm bpm) {
        Intent pendingIntent = new Intent(mContext, MedicallyExamReportActivity.class);
        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //这里可以是应用图标，也可以将聊天头像转成bitmap
        NotificationUtil.create(
                mContext,
                2,
                pendingIntent,
                R.mipmap.ic_launcher,
                event.getMessage().getContent(),
                "心率检测报告：" + bpm.getBpm() + "bpm，" + bpm.getDescription()
        );
    }


    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        L.i("processSDKMessage: is called");
        if (msg.getMsgType().equals(BmobIMMessageType.LOCATION.getType())) {
            L.i("location information");
            BmobIMLocationMessage locationMessage = BmobIMLocationMessage.buildFromDB(msg);
            EventBus.getDefault().post(locationMessage);
        } else {
            EventBus.getDefault().post(event);
        }
    }
}

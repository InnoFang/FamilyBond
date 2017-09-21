package io.innofang.bmob;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 12:44
 * Description:
 */


public class IMMessageHandler extends BmobIMMessageHandler {

    private Context mContext;

    public IMMessageHandler(Context context) {
        mContext = context;
    }

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
        executeMessage(messageEvent);
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
        EventBus.getDefault().post(offlineMessageEvent);
    }

    private void executeMessage(final MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
            processCustomMessage(msg, event);
        } else {
            processSDKMessage(msg, event);
        }

    }

    private void processCustomMessage(BmobIMMessage msg, MessageEvent event) {
        EventBus.getDefault().post(event);
    }


    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        if (BmobNotificationManager.getInstance(mContext).isShowNotification()) {
            //如果需要显示通知栏，SDK提供以下两种显示方式：
          /*  Intent pendingIntent = new Intent();
            pendingIntent.setAction(Intent.ACTION_VIEW);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            BmobIMUserInfo info = event.getFromUserInfo();
            //这里可以是应用图标，也可以将聊天头像转成bitmap
            Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            BmobNotificationManager.getInstance(mContext).showNotification(largeIcon,
                    info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);*/
        } else {
            EventBus.getDefault().post(event);
        }


    }
}

package io.innofang.sms_intercept;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Inno Fang
 * Time: 2017/10/3 13:30
 * Description:
 */


public class InterceptReceiver extends BroadcastReceiver {

    private static final String TAG = "InterceptReceiver";
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public InterceptReceiver() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "---------------InterceptReceiver onReceive()----------------");

        if (ACTION_SMS_RECEIVED.equals(intent.getAction())) {
            Bundle carryContent = intent.getExtras();
            if (carryContent != null) {
                StringBuilder sb = new StringBuilder();
                StringBuilder content = new StringBuilder();

                // 通过pdus获取接收到的所有短信息，获取短信内容
                Object[] pdus = (Object[]) carryContent.get("pdus");
                // 构建短信对象数组
                SmsMessage[] mges = new SmsMessage[pdus.length];
                for (int i = 0, len = pdus.length; i < len; i++) {
                    // 获取单条短信内容，以pdu格式存，并生成短信对象
                    mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                for (SmsMessage mge : mges) {
                    // build sms content
                    sb.append("短信来自：").append(mge.getDisplayOriginatingAddress()).append("\n")
                            .append("短信内容：").append(mge.getMessageBody()).append("\n");

                    Date sendDate = new Date(mge.getTimestampMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    sb.append("短信发送时间：").append(format.format(sendDate));

                    content.append(mge.getMessageBody());
                }
                SMSClassifyResult result = SMSIdentification.runLoadModelAndUse(content.toString());
                if (result.isSuspiciousSMS()) {

                    SMSEvent event = new SMSEvent();

                    SmsMessage mge = mges[0];

                    // time
                    Date sendDate = new Date(mge.getTimestampMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    event.time = format.format(sendDate);
                    // address
                    event.address = mge.getDisplayOriginatingAddress();
                    // sms
                    event.sms = content.toString();
                    // probability
                    event.probability = result.getProbability();

                    Log.i(TAG, "It's suspicious sms， prob is " + result.getProbability());
                    EventBus.getDefault().post(event);
                    Log.i(TAG, content.toString()); // 打印日志记录
                }
                this.abortBroadcast(); // 不再往下传播
            }
        }
    }
}

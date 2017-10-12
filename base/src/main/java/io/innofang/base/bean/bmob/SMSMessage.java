package io.innofang.base.bean.bmob;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import io.innofang.base.bean.SMS;
import io.innofang.base.util.common.L;

/**
 * Author: Inno Fang
 * Time: 2017/10/11 22:43
 * Description:
 */


public class SMSMessage extends BmobIMExtraMessage {

    public static final String SMS = "SMS";

    @Override
    public String getMsgType() {
        return SMS;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

    public static io.innofang.base.bean.SMS convert(BmobIMMessage message) {
        io.innofang.base.bean.SMS sms = new SMS();
        String extra = message.getExtra();

        try {
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String time = json.getString("time");
                String address = json.getString("address");
                String content = json.getString("content");
                double probability = json.getDouble("probability");
                sms.setTime(time);
                sms.setAddress(address);
                sms.setContent(content);
                sms.setProbability(probability);
            } else {
                L.i("SMSMessage's extra is null.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sms;
    }

}

package io.innofang.base.bean.bmob;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.utils.common.L;

/**
 * Author: Inno Fang
 * Time: 2017/10/12 14:25
 * Description:
 */


public class BpmMessage extends BmobIMExtraMessage {

    public static final String BPM = "bpm";

    @Override
    public String getMsgType() {
        return BPM;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

    public static Bpm convert(BmobIMMessage message) {
        Bpm bpm = new Bpm();
        String extra = message.getExtra();

        try {
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String mBpm = json.getString("mBpm");
                String mTime = json.getString("mTime");
                String mDescription = json.getString("mDescription");
                bpm.setTime(mTime);
                bpm.setBpm(mBpm);
                bpm.setDescription(mDescription);
            } else {
                L.i("BpmMessage's extra is null.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bpm;
    }
}

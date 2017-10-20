package io.innofang.base.bean.bmob;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import io.innofang.base.bean.Location;
import io.innofang.base.utils.common.L;

/**
 * Author: Inno Fang
 * Time: 2017/10/20 16:33
 * Description:
 */


public class LocationMessage extends BmobIMExtraMessage {

    public static final String LOC = "loc";

    @Override
    public String getMsgType() {
        return LOC;
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    public static Location convert(BmobIMMessage message) {
        Location location = new Location();
        String extra = message.getExtra();

        try {
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String id = json.getString("id");
                String address = json.getString("address");
                double latitude = json.getDouble("latitude");
                double longitude = json.getDouble("longitude");
                location.setId(id);
                location.setAddress(address);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
            } else {
                L.i("LocationMessage's extra is null.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }
}

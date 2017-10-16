package io.innofang.base.bean.bmob;

import cn.bmob.newim.bean.BmobIMExtraMessage;

/**
 * Author: Inno Fang
 * Time: 2017/10/12 14:25
 * Description:
 */


public class ShareMapMessage extends BmobIMExtraMessage {

    public static final String MAP = "map";
    public static final String OPEN = "open";
    public static final String CLOSE = "close";

    @Override
    public String getMsgType() {
        return MAP;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

}

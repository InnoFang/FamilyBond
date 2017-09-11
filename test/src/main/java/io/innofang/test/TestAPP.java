package io.innofang.test;

import android.app.Application;

import io.innofang.bmob.BmobAPI;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 16:05
 * Description:
 */


public class TestAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器
        BmobAPI.init(this);
    }
}

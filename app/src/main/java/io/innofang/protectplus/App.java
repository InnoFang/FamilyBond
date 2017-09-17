package io.innofang.protectplus;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import io.innofang.bmob.BmobAPI;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 15:36
 * Description:
 */


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BmobAPI.init(this);
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}

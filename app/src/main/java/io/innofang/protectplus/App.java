package io.innofang.protectplus;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.TypefaceUtil;
import io.innofang.bmob.BmobAPI;
import io.innofang.xfyun.XFYun;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 15:36
 * Description:
 */


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* change font to Youyuan */
        TypefaceUtil.replaceSystemDefaultFont(this, "fonts/youyuan.ttf");

        /* initialize ARouter */
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

        /* initialize Log */
        L.Debug = true;

        /* initialize XFYun */
        XFYun.init(this);

        /* initialize GreenDao */
        GreenDaoConfig.getInstance().init(this);

          /* initialize Bmob */
        BmobAPI.init(this, new IMMessageHandler(this));
    }
}

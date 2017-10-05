package io.innofang.base.util.common;

import android.util.Log;

/**
 * Author: Inno Fang
 * Description: Log 管理类
 */


public class L {
    private static final String TAG = "Log";

    private L() {
    }

    /* 是否需要打印bug，可以在application的onCreate函数里面初始化 */
    public static boolean Debug = true;

    public static void v(String msg) {
        if (Debug)
            Log.v(TAG, msg);
    }

    public static void d(String msg) {
        if (Debug)
            Log.d(TAG, msg);
    }

    /* 下面四个是默认tag的函数 */
    public static void i(String msg) {
        if (Debug)
            Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (Debug)
            Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable tr) {
        if (Debug)
            Log.e(TAG, msg, tr);
    }


    public static void w(String msg) {
        if (Debug)
            Log.w(TAG, msg);
    }

    /* 下面是传入自定义tag的函数 */
    public static void v(String tag, String msg) {
        if (Debug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (Debug)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (Debug)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (Debug)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Debug)
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (Debug)
            Log.e(tag, msg, tr);
    }
}
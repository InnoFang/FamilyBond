package io.innofang.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 15:29
 * Description:
 */


public class BmobAPI{

    public static void init(Context context) {
        Bmob.initialize(context, "6e93f68a2c1522791bb39accfe44237d");
    }

}

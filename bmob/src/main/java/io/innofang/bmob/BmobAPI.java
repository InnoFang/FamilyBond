package io.innofang.bmob;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.v3.Bmob;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 15:29
 * Description:
 */


public class BmobAPI{

    public static void init(Context context, BmobIMMessageHandler messageHandler) {
        Bmob.initialize(context, "6e93f68a2c1522791bb39accfe44237d");

        /* 集成：初始化IM SDK，并注册消息接收器，只有主进程运行的时候才需要初始化 */
        if (context.getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(context);
            BmobIM.registerDefaultMessageHandler(messageHandler);
        }

    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

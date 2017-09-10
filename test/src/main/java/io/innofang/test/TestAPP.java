package io.innofang.test;

import android.app.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import io.innofang.bmob.BmobAPI;
import io.innofang.test.IM.DemoMessageHandler;

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
        BmobAPI.init(this, new DemoMessageHandler(this));
    }

    /**
     * 获取当前运行的进程名
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

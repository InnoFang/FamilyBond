package io.innofang.base.util.common;

/**
 * Author: Inno Fang
 * Time: 2017/9/17 16:34
 * Description:
 */


public class SdCardUtil {

    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

}

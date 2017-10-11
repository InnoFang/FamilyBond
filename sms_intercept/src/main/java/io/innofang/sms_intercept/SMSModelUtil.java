package io.innofang.sms_intercept;

import java.io.File;

/**
 * Author: Inno Fang
 * Time: 2017/10/10 22:11
 * Description:
 */


public class SMSModelUtil {

    public static final String DIRECTORY = "/storage/emulated/0/SMSModel/";
    public static final String CATEGORY = "category";
    public static final String LEXICON = "lexicon";
    public static final String MAX_FEATURES = "maxFeatures";
    public static final String MODEL = "model";
    public static final String CATEGORY_FILE_PATH = DIRECTORY + CATEGORY;

    public static boolean isModelFilesExit() {
        File dir = new File(DIRECTORY);
        return dir.exists() &&
                new File(dir, "category").exists() &&
                new File(dir, "lexicon").exists() &&
                new File(dir, "maxFeatures").exists() &&
                new File(dir, "model").exists();
    }

    public static String getFileName(String name) {
        if (name.contains(CATEGORY)) return CATEGORY;
        if (name.contains(LEXICON)) return LEXICON;
        if (name.contains(MAX_FEATURES)) return MAX_FEATURES;
        if (name.contains(MODEL)) return MODEL;
        return "";
    }
}

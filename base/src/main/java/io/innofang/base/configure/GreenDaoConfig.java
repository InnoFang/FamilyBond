package io.innofang.base.configure;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import io.innofang.base.bean.greendao.DaoMaster;
import io.innofang.base.bean.greendao.DaoSession;

/**
 * Author: Inno Fang
 * Time: 2017/10/5 10:59
 * Description:
 */


public class GreenDaoConfig {

    private DaoSession daoSession;
    public static final boolean ENCRYPTED = true;

    private GreenDaoConfig(){}

    public static GreenDaoConfig getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final GreenDaoConfig INSTANCE = new GreenDaoConfig();
    }

    public void init(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, ENCRYPTED ? "users-db-encrypted" : "users-db");
        Database db =  helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}

package io.innofang.base.bean.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.SMS;

import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.SMSDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bpmDaoConfig;
    private final DaoConfig sMSDaoConfig;

    private final BpmDao bpmDao;
    private final SMSDao sMSDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bpmDaoConfig = daoConfigMap.get(BpmDao.class).clone();
        bpmDaoConfig.initIdentityScope(type);

        sMSDaoConfig = daoConfigMap.get(SMSDao.class).clone();
        sMSDaoConfig.initIdentityScope(type);

        bpmDao = new BpmDao(bpmDaoConfig, this);
        sMSDao = new SMSDao(sMSDaoConfig, this);

        registerDao(Bpm.class, bpmDao);
        registerDao(SMS.class, sMSDao);
    }
    
    public void clear() {
        bpmDaoConfig.clearIdentityScope();
        sMSDaoConfig.clearIdentityScope();
    }

    public BpmDao getBpmDao() {
        return bpmDao;
    }

    public SMSDao getSMSDao() {
        return sMSDao;
    }

}

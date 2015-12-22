package com.cyou.cma.clockscreen.quicklaunch;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig launchSetDaoConfig;
    private final DaoConfig quickContactDaoConfig;
    private final DaoConfig quickApplicationDaoConfig;
    private final DaoConfig quickFolderDaoConfig;

    private final LaunchSetDao launchSetDao;
    private final QuickContactDao quickContactDao;
    private final QuickApplicationDao quickApplicationDao;
    private final QuickFolderDao quickFolderDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        launchSetDaoConfig = daoConfigMap.get(LaunchSetDao.class).clone();
        launchSetDaoConfig.initIdentityScope(type);

        quickContactDaoConfig = daoConfigMap.get(QuickContactDao.class).clone();
        quickContactDaoConfig.initIdentityScope(type);

        quickApplicationDaoConfig = daoConfigMap.get(QuickApplicationDao.class).clone();
        quickApplicationDaoConfig.initIdentityScope(type);

        quickFolderDaoConfig = daoConfigMap.get(QuickFolderDao.class).clone();
        quickFolderDaoConfig.initIdentityScope(type);

        launchSetDao = new LaunchSetDao(launchSetDaoConfig, this);
        quickContactDao = new QuickContactDao(quickContactDaoConfig, this);
        quickApplicationDao = new QuickApplicationDao(quickApplicationDaoConfig, this);
        quickFolderDao = new QuickFolderDao(quickFolderDaoConfig, this);

        registerDao(LaunchSet.class, launchSetDao);
        registerDao(QuickContact.class, quickContactDao);
        registerDao(QuickApplication.class, quickApplicationDao);
        registerDao(QuickFolder.class, quickFolderDao);
    }
    
    public void clear() {
        launchSetDaoConfig.getIdentityScope().clear();
        quickContactDaoConfig.getIdentityScope().clear();
        quickApplicationDaoConfig.getIdentityScope().clear();
        quickFolderDaoConfig.getIdentityScope().clear();
    }

    public LaunchSetDao getLaunchSetDao() {
        return launchSetDao;
    }

    public QuickContactDao getQuickContactDao() {
        return quickContactDao;
    }

    public QuickApplicationDao getQuickApplicationDao() {
        return quickApplicationDao;
    }

    public QuickFolderDao getQuickFolderDao() {
        return quickFolderDao;
    }

}
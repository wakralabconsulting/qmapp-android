package com.qatarmuseums.qatarmuseumsapp;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableArabic;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableDao;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableEnglish;

@Database(entities = {HomePageTableEnglish.class, HomePageTableArabic.class,
        HeritageListTableEnglish.class, HeritageListTableArabic.class,
        PublicArtsTableEnglish.class, PublicArtsTableArabic.class}, version = 1, exportSchema = false)
public abstract class QMDatabase extends RoomDatabase {

    public abstract HomePageTableDao getHomePageTableDao();

    public abstract HeritageListTableDao getHeritageListTableDao();

    public abstract PublicArtsTableDao getPublicArtsTableDao();

    private static QMDatabase qmDatabase;

    public static QMDatabase getInstance(Context context) {
        if (null == qmDatabase) {
            qmDatabase = buildDatabaseInstance(context);
        }
        return qmDatabase;
    }

    private static QMDatabase buildDatabaseInstance(Context context) {
        QMDatabase qmd = Room.databaseBuilder(context,
                QMDatabase.class,
                "QMDatabase").build();
        return qmd;


    }

    public void cleanUp() {
        qmDatabase = null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}

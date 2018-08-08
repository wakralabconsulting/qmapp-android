package com.qatarmuseums.qatarmuseumsapp;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.qatarmuseums.qatarmuseumsapp.home.HomePageTable;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableDao;

@Database(entities = { HomePageTable.class },version = 1)
public abstract class QMDatabase extends RoomDatabase {

    public abstract HomePageTableDao getHomePageTableDao();

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

    public void cleanUp(){
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

}

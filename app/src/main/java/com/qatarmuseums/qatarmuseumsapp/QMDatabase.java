package com.qatarmuseums.qatarmuseumsapp;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEventsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEventsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableArabic;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableDao;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableDao;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableDao;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableDao;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableEnglish;

@Database(entities = {HomePageTableEnglish.class, HomePageTableArabic.class,
        HeritageListTableEnglish.class, HeritageListTableArabic.class,
        PublicArtsTableEnglish.class, PublicArtsTableArabic.class,
        ParkTableEnglish.class, ParkTableArabic.class, DiningTableEnglish.class,
        DiningTableArabic.class, MuseumCollectionListTableEnglish.class,
        MuseumCollectionListTableArabic.class, ExhibitionListTableEnglish.class,
        ExhibitionListTableArabic.class, MuseumAboutTableEnglish.class,
        MuseumAboutTableArabic.class, CalendarEventsTableEnglish.class, CalendarEventsTableArabic.class},
        version = 1, exportSchema = false)

public abstract class QMDatabase extends RoomDatabase {

    public abstract HomePageTableDao getHomePageTableDao();

    public abstract HeritageListTableDao getHeritageListTableDao();

    public abstract PublicArtsTableDao getPublicArtsTableDao();

    public abstract ParkTableDao getParkTableDao();

    public abstract ExhibitionTableDao getExhibitionTableDao();

    public abstract DiningTableDao getDiningTableDao();

    public abstract MuseumCollectionListTableDao getMuseumCollectionListDao();

    public abstract MuseumAboutTableDao getMuseumAboutDao();

    public abstract CalendarTableDao getCalendarEventsDao();

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

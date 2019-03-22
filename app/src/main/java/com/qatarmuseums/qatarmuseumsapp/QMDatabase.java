package com.qatarmuseums.qatarmuseumsapp;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
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
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTableDao;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserRegistrationDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.culturepass.UserRegistrationDetailsTableDao;
import com.qatarmuseums.qatarmuseumsapp.education.EducationCalendarTableDao;
import com.qatarmuseums.qatarmuseumsapp.education.EducationalCalendarEventsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.education.EducationalCalendarEventsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableDao;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityDetailTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityListTableDao;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTableArabic;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTableDao;
import com.qatarmuseums.qatarmuseumsapp.floormap.ArtifactTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageBannerTableArabic;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageBannerTableDao;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageBannerTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableArabic;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableDao;
import com.qatarmuseums.qatarmuseumsapp.home.HomePageTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableDao;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableDao;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableDao;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.NMoQParkListDetails;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationTableArabic;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationTableDao;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTableDao;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListTableDao;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkTableDao;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableArabic;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableDao;
import com.qatarmuseums.qatarmuseumsapp.park.ParkTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableDao;
import com.qatarmuseums.qatarmuseumsapp.tourdetails.TourDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageArabic;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageDao;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageEnglish;

@Database(entities = {HomePageTableEnglish.class, HomePageTableArabic.class,
        HeritageListTableEnglish.class, HeritageListTableArabic.class,
        PublicArtsTableEnglish.class, PublicArtsTableArabic.class,
        ParkTableEnglish.class, ParkTableArabic.class, DiningTableEnglish.class,
        DiningTableArabic.class, MuseumCollectionListTableEnglish.class,
        MuseumCollectionListTableArabic.class, ExhibitionListTableEnglish.class,
        ExhibitionListTableArabic.class, MuseumAboutTableEnglish.class,
        MuseumAboutTableArabic.class, CalendarEventsTableEnglish.class, CalendarEventsTableArabic.class,
        EducationalCalendarEventsTableEnglish.class, EducationalCalendarEventsTableArabic.class,
        MuseumCollectionDetailTableEnglish.class, MuseumCollectionDetailTableArabic.class,
        TourGuideStartPageEnglish.class, TourGuideStartPageArabic.class, ArtifactTableEnglish.class,
        ArtifactTableArabic.class, NotificationTableEnglish.class, NotificationTableArabic.class,
        HomePageBannerTableEnglish.class, HomePageBannerTableArabic.class, TravelDetailsTableEnglish.class,
        TravelDetailsTableArabic.class, TourListTableEnglish.class, TourListTableArabic.class,
        TourDetailsTableEnglish.class, TourDetailsTableArabic.class, UserRegistrationDetailsTable.class,
        FacilityListTableEnglish.class, FacilityListTableArabic.class,
        FacilityDetailTableEnglish.class, FacilityDetailTableArabic.class,
        NMoQParkTableEnglish.class, NMoQParkTableArabic.class,
        NMoQParkListTableEnglish.class, NMoQParkListTableArabic.class,
        NMoQParkListDetailsTableEnglish.class, NMoQParkListDetailsTableArabic.class},
        version = 2, exportSchema = false)
@TypeConverters({Convertor.class})


public abstract class QMDatabase extends RoomDatabase {

    public abstract HomePageTableDao getHomePageTableDao();

    public abstract HeritageListTableDao getHeritageListTableDao();

    public abstract PublicArtsTableDao getPublicArtsTableDao();

    public abstract ParkTableDao getParkTableDao();

    public abstract ExhibitionTableDao getExhibitionTableDao();

    public abstract DiningTableDao getDiningTableDao();

    public abstract MuseumCollectionListTableDao getMuseumCollectionListDao();

    public abstract MuseumCollectionDetailTableDao getMuseumCollectionDetailDao();

    public abstract MuseumAboutTableDao getMuseumAboutDao();

    public abstract CalendarTableDao getCalendarEventsDao();

    public abstract EducationCalendarTableDao getEducationCalendarEventsDao();

    public abstract TourGuideStartPageDao getTourGuideStartPageDao();

    public abstract ArtifactTableDao getArtifactTableDao();

    public abstract NotificationTableDao getNotificationDao();

    public abstract HomePageBannerTableDao getHomePageBannerTableDao();

    public abstract TravelDetailsTableDao getTravelDetailsTableDao();

    public abstract TourListTableDao getTourListTaleDao();

    public abstract TourDetailsTableDao getTourDetailsTaleDao();

    public abstract UserRegistrationDetailsTableDao getUserRegistrationTaleDao();

    public abstract FacilityListTableDao getFacilitiesListTableDao();

    public abstract FacilityDetailTableDao getFacilitiesDetailTableDao();

    public abstract NMoQParkTableDao getNMoQParkTableDao();

    public abstract NMoQParkListTableDao getNMoQParkListTableDao();

    public abstract NMoQParkListDetailsTableDao getNMoQParkListDetailsTableDao();

    private static QMDatabase qmDatabase;

    public static QMDatabase getInstance(Context context) {
        if (null == qmDatabase) {
            qmDatabase = buildDatabaseInstance(context);
        }
        return qmDatabase;
    }

    private static QMDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), QMDatabase.class,
                "QMDatabase")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void cleanUp() {
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

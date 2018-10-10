package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface TourGuideStartPageDao {

    @Query("SELECT * FROM tourguidestartpageenglish")
    TourGuideStartPageEnglish getAllTourGuideStartDataEnglish();

    @Query("SELECT * FROM tourguidestartpagearabic")
    TourGuideStartPageArabic getAllTourGuideStartDataArabic();

    @Query("SELECT COUNT(museum_entity) FROM tourguidestartpageenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(museum_entity) FROM tourguidestartpagearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(museum_entity) FROM tourguidestartpageenglish WHERE museum_entity = :mEntityFromAPI")
    int checkEnglishIdExist(String mEntityFromAPI);

    @Query("SELECT COUNT(museum_entity) FROM tourguidestartpagearabic WHERE museum_entity = :mEntityFromAPI")
    int checkArabicIdExist(String mEntityFromAPI);

    @Query("SELECT * FROM tourguidestartpageenglish WHERE museum_entity = :mEntityFromAPI")
    TourGuideStartPageEnglish getTourGuideStartPageDataEnglish(String mEntityFromAPI);

    @Query("SELECT * FROM tourguidestartpagearabic WHERE museum_entity = :mEntityFromAPI")
    TourGuideStartPageArabic getTourGuideStartPageDataArabic(String mEntityFromAPI);

    @Query("UPDATE tourguidestartpageenglish SET title = :museumtitleFromApi," +
            "description = :tourguidedescriptionFromApi WHERE museum_entity=:mEntityFromApi")
    void updateTourGuideStartDataEnglish( String museumtitleFromApi, String tourguidedescriptionFromApi,
                                       String mEntityFromApi);
    @Query("UPDATE tourguidestartpagearabic SET title = :museumtitleFromApi," +
            "description = :tourguidedescriptionFromApi WHERE museum_entity=:mEntityFromApi")
    void updateTourGuideStartDataArabic( String museumtitleFromApi, String tourguidedescriptionFromApi,
                                       String mEntityFromApi);

    @Insert
    void insert(TourGuideStartPageEnglish tourGuideStartPageEnglish);

    @Insert
    void insert(TourGuideStartPageArabic tourGuideStartPageArabic);
}

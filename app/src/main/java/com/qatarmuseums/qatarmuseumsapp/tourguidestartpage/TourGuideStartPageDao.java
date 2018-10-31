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

    @Query("SELECT COUNT(nid) FROM tourguidestartpageenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(nid) FROM tourguidestartpagearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(nid) FROM tourguidestartpageenglish WHERE nid = :mNid")
    int checkEnglishIdExist(String mNid);

    @Query("SELECT COUNT(nid) FROM tourguidestartpagearabic WHERE nid = :mNid")
    int checkArabicIdExist(String mNid);

    @Query("SELECT * FROM tourguidestartpageenglish WHERE nid = :mNid")
    TourGuideStartPageEnglish getTourGuideStartPageDataEnglish(String mNid);

    @Query("SELECT * FROM tourguidestartpagearabic WHERE nid = :mNid")
    TourGuideStartPageArabic getTourGuideStartPageDataArabic(String mNid);

    @Query("UPDATE tourguidestartpageenglish SET title = :museumtitleFromApi," +
            "museum_entity = :museumEntry, description = :tourguidedescriptionFromApi WHERE nid=:mNid")
    void updateTourGuideStartDataEnglish(String museumtitleFromApi, String tourguidedescriptionFromApi,
                                         String museumEntry, String mNid);

    @Query("UPDATE tourguidestartpagearabic SET title = :museumtitleFromApi," +
            "museum_entity = :museumEntry, description = :tourguidedescriptionFromApi WHERE nid=:mNid")
    void updateTourGuideStartDataArabic(String museumtitleFromApi, String tourguidedescriptionFromApi,
                                        String museumEntry, String mNid);

    @Insert
    void insert(TourGuideStartPageEnglish tourGuideStartPageEnglish);

    @Insert
    void insert(TourGuideStartPageArabic tourGuideStartPageArabic);
}

package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TourGuideStartPageDao {

    @Query("SELECT * FROM tourguidestartpageenglish WHERE museum_entity = :museumID")
    List<TourGuideStartPageEnglish> getTourGuideDetailsEnglish(String museumID);

    @Query("SELECT * FROM tourguidestartpagearabic WHERE museum_entity = :museumID")
    List<TourGuideStartPageArabic> getTourGuideDetailsArabic(String museumID);

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
            "museum_entity = :museumEntry, description = :tourguidedescriptionFromApi," +
            " images = :imagesFromApi WHERE nid=:mNid")
    void updateTourGuideStartDataEnglish(String museumtitleFromApi, String tourguidedescriptionFromApi,
                                         String museumEntry, String imagesFromApi, String mNid);

    @Query("UPDATE tourguidestartpagearabic SET title = :museumtitleFromApi," +
            "museum_entity = :museumEntry, description = :tourguidedescriptionFromApi," +
            " images = :imagesFromApi WHERE nid=:mNid")
    void updateTourGuideStartDataArabic(String museumtitleFromApi, String tourguidedescriptionFromApi,
                                        String museumEntry, String imagesFromApi, String mNid);

    @Insert
    void insert(TourGuideStartPageEnglish tourGuideStartPageEnglish);

    @Insert
    void insert(TourGuideStartPageArabic tourGuideStartPageArabic);
}

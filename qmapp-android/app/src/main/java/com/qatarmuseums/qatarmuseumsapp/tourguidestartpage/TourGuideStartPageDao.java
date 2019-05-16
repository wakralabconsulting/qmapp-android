package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TourGuideStartPageDao {

    @Query("SELECT * FROM tourGuideStartPage WHERE museum_entity = :museumID AND language = :language")
    List<TourGuideStartPage> getTourGuideDetails(String museumID, String language);

    @Query("SELECT COUNT(nid) FROM tourGuideStartPage WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(nid) FROM tourGuideStartPage WHERE nid = :mNid AND language = :language")
    int checkIdExist(String mNid, String language);

    @Query("UPDATE tourGuideStartPage SET title = :museumTitleFromApi," +
            "museum_entity = :museumEntry, description = :tourGuideDescriptionFromApi," +
            " images = :imagesFromApi WHERE nid = :mNid AND language = :language")
    void updateTourGuideStartData(String museumTitleFromApi, String tourGuideDescriptionFromApi,
                                  String museumEntry, String imagesFromApi, String mNid, String language);

    @Insert
    void insertData(TourGuideStartPage tourGuideStartPage);

}

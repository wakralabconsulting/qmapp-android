package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TourListTableDao {

    @Query("SELECT * FROM tourList WHERE language = :language")
    List<TourListTable> getAllData(String language);

    @Query("SELECT COUNT(tourNid) FROM tourList WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(tourNid) FROM tourList WHERE tourNid = :idFromAPI AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM tourList WHERE isTour = :isTourFromAPI AND language = :language")
    List<TourListTable> getTourList(int isTourFromAPI, String language);

    @Query("UPDATE tourList SET tourDay = :tourDayFromApi," +
            "tourEventDate = :tourEventDateFromApi," +
            "tourSubtitle = :tourSubtitleFromApi," +
            "tourImages = :tourImagesFromApi," +
            "tourSortId = :tourSortIdFromApi," +
            "tourDescription = :tourDescriptionFromApi WHERE tourNid=:idFromApi AND language = :language")
    void updateTourList(String tourDayFromApi, String tourEventDateFromApi,
                        String tourSubtitleFromApi, String tourImagesFromApi,
                        String tourSortIdFromApi, String tourDescriptionFromApi,
                        String idFromApi, String language);


    @Insert
    void insert(TourListTable tourListTable);

}

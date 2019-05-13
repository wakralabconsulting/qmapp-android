package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TourDetailsTableDao {

    @Query("SELECT COUNT(item_id) FROM tourDetailsTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(tour_id) FROM tourDetailsTable WHERE tour_id = :idFromAPI AND language = :language")
    int checkIdExist(String idFromAPI, String language);

    @Query("SELECT * FROM tourDetailsTable WHERE tour_id = :tourIdFromAPI AND language = :language")
    List<TourDetailsTable> getTourDetailsWithId(String tourIdFromAPI, String language);

    @Query("DELETE FROM tourDetailsTable WHERE tour_id = :tourIdFromAPI AND language = :language")
    void deleteTourDetailsWithId(String tourIdFromAPI, String language);

    @Insert
    void insertData(TourDetailsTable tourDetailsTable);

}

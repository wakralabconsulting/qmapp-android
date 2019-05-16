package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ParkTableDao {
    @Query("SELECT * FROM parkTable WHERE language = :language")
    List<ParkTable> getAllDataFromParkTable(String language);

    @Query("SELECT COUNT(sortId) FROM parkTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(sortId) FROM parkTable WHERE mainTitle = :titleFromAPI AND language = :language")
    int checkIdExist(String titleFromAPI, String language);

    @Query("UPDATE parkTable SET sortId = :id," +
            "shortDescription = :shortDescriptionFrmAPI,image = :imageFromApi," +
            "latitude = :latitudeFromAPI,longitude = :longitudeFromApi," +
            "timingInfo = :timingInfoFromAPI WHERE mainTitle = :titleFromAPI AND language = :language")
    void updatePark(String titleFromAPI, String shortDescriptionFrmAPI,
                    String imageFromApi, String latitudeFromAPI,
                    String longitudeFromApi, String timingInfoFromAPI,
                    long id, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertData(ParkTable parkTable);

}

package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TravelDetailsTableDao {

    @Query("SELECT * FROM travelDetails WHERE language = :language")
    List<TravelDetailsTable> getAllData(String language);

    @Query("SELECT COUNT(content_ID) FROM travelDetails WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(content_ID) FROM travelDetails WHERE content_ID = :idFromAPI " +
            "AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE travelDetails SET travel_description = :travelDescriptionFromApi," +
            "travel_image = :travelImageFromApi," +
            "travel_name = :travelNameFromApi," +
            "travel_email = :travelEmailFromApi," +
            "contact_number = :contactNumberFromApi," +
            "promotional_code = :promotionalCodeFromApi WHERE content_ID=:idFromApi AND language = :language")
    void updateTravelDetails(String travelNameFromApi, String travelImageFromApi,
                             String travelDescriptionFromApi, String promotionalCodeFromApi,
                             String contactNumberFromApi, String travelEmailFromApi,
                             String idFromApi, String language);

    @Insert
    void insert(TravelDetailsTable travelDetailsTable);

}

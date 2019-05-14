package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DiningTableDao {

    @Query("SELECT * FROM diningTable WHERE language = :language")
    List<DiningTable> getAllData(String language);

    @Query("SELECT COUNT(dining_id) FROM diningTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(dining_id) FROM diningTable WHERE dining_id = :idFromAPI AND " +
            "language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM diningTable WHERE dining_id = :idFromAPI AND language = :language")
    List<DiningTable> getDiningDetails(int idFromAPI, String language);

    @Query("SELECT * FROM diningTable WHERE museum_id = :museumIdFromAPI AND language = :language")
    List<DiningTable> getDiningDetailsWithMuseumId(int museumIdFromAPI, String language);

    @Query("UPDATE diningTable SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            " dining_sort_id = :sortIdFromApi WHERE dining_id = :id AND language = :language")
    void updateDiningTable(String nameFromApi, String imageFromApi, String id, String sortIdFromApi, String language);

    @Query("UPDATE diningTable SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            "description = :descriptionFromApi, opening_time = :openingTimeFromApi, " +
            "closing_time = :closingTimeFromApi, latitude = :latitudeFromApi, longitude = :longitudeFromApi," +
            " dining_sort_id = :sortIdFromApi WHERE dining_id = :id AND language = :language")
    void updateDiningDetails(String nameFromApi, String imageFromApi, String descriptionFromApi,
                             String openingTimeFromApi, String closingTimeFromApi, String latitudeFromApi,
                             String longitudeFromApi, String id, String sortIdFromApi, String language);


    /*
 * Insert the object in database
 * @param note, object to be inserted
 */
    @Insert
    void insert(DiningTable diningTable);


}

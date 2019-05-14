package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HeritageListTableDao {
    @Query("SELECT * FROM heritageList WHERE language = :language")
    List<HeritageListTable> getAllData(String language);

    @Query("SELECT COUNT(heritage_id) FROM heritageList WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(heritage_id) FROM heritageList WHERE heritage_id = :idFromAPI " +
            "AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM heritageList WHERE heritage_id = :idFromAPI AND language = :language")
    List<HeritageListTable> getHeritageDetails(int idFromAPI, String language);

    @Query("UPDATE heritageList SET heritage_name = :nameFromApi," +
            "heritage_sort_id = :sortIdFromApi, heritage_image = :imageFromApi WHERE heritage_id=:id " +
            "AND language = :language")
    void updateHeritageList(String nameFromApi, String sortIdFromApi,
                            String imageFromApi, String id, String language);


    @Query("UPDATE heritageList SET latitude = :latitudeFromApi,longitude = :longitudeFromApi," +
            "heritage_long_description=:descriptionFromApi," + "heritage_short_description=:shortDescriptionFromApi," +
            "heritage_image=:imageFromApi, heritage_images=:imagesFromApi WHERE heritage_id=:id AND language = :language")
    void updateHeritageDetails(String latitudeFromApi,
                               String longitudeFromApi, String descriptionFromApi,
                               String shortDescriptionFromApi, String imageFromApi,
                               String imagesFromApi, String id, String language);

    @Insert
    void insert(HeritageListTable heritageListTable);

}

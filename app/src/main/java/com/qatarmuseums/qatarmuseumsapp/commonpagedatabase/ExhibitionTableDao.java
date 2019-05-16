package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExhibitionTableDao {

    @Query("SELECT * FROM exhibitionList WHERE language = :language")
    List<ExhibitionListTable> getAllData(String language);

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionList WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionList WHERE exhibition_id = :idFromAPI " +
            "AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM exhibitionList WHERE museum_id = :museumIdFromAPI AND language = :language")
    List<ExhibitionListTable> getExhibitionWithMuseumId(int museumIdFromAPI, String language);

    @Query("SELECT * FROM exhibitionList WHERE exhibition_id = :idFromAPI AND language = :language")
    List<ExhibitionListTable> getExhibitionDetails(int idFromAPI, String language);

    @Query("UPDATE exhibitionList SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi,exhibition_location = :locationFromApi," +
            "museum_id = :museumIdFromApi WHERE exhibition_id=:id AND language = :language")
    void updateExhibitionList(String startDateFromApi, String endDateFromApi,
                              String locationFromApi, String id, String museumIdFromApi, String language);

    @Query("UPDATE exhibitionList SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi," +
            "exhibition_latest_image = :imageFromApi," +
            "exhibition_images = :imagesFromApi," +
            "exhibition_long_description=:descriptionFromApi," +
            "exhibition_latitude=:latitudeFromApi," +
            "exhibition_longitude=:longitudeFromApi," +
            "exhibition_short_description=:shortDescriptionFromApi WHERE exhibition_id=:id AND language = :language")
    void updateExhibitionDetail(String startDateFromApi, String endDateFromApi,
                                String imageFromApi, String descriptionFromApi,
                                String shortDescriptionFromApi, String latitudeFromApi,
                                String longitudeFromApi, String imagesFromApi,
                                String id, String language);

    @Insert
    void insert(ExhibitionListTable exhibitionListTable);

}

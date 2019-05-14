package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MuseumCollectionDetailTableDao {
    @Query("SELECT * FROM museumCollectionDetailTable WHERE category_id = :categoryIdFromAPI AND language = :language")
    List<MuseumCollectionDetailTable> getAllDataFromMuseumDetailTable(String categoryIdFromAPI, String language);

    @Query("SELECT COUNT(detail_title) FROM museumCollectionDetailTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(detail_title) FROM museumCollectionDetailTable WHERE detail_title = :titleFromAPI " +
            "AND language = :language")
    int checkTitleExist(String titleFromAPI, String language);

    @Query("UPDATE museumCollectionDetailTable SET category_id = :categoryIdFromApi," +
            "detail_image1 = :image1FromApi," +
            " detail_about=:aboutFromApi WHERE detail_title=:titleFromApi AND language = :language")
    void updateMuseumDetailTable(String categoryIdFromApi, String image1FromApi,
                                 String aboutFromApi, String titleFromApi, String language);

    @Insert
    void insertData(MuseumCollectionDetailTable museumCollectionDetailTable);

}

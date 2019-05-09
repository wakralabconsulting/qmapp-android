package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HomePageTableDao {
    @Query("SELECT * FROM homePageTable WHERE language = :language")
    List<HomePageTable> getAllDataFromHomePageTable(String language);

    @Query("SELECT COUNT(qatarMuseum_id) FROM homePageTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(qatarMuseum_id) FROM homePageTable WHERE qatarMuseum_id = :idFromAPI AND" +
            " language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE homePageTable SET name = :nameFromApi," +
            "tourGuide_available = :tourGideFromApi,image = :imageFromApi, sortId = :sortIdFromAPI " +
            "WHERE qatarMuseum_id=:id AND language = :language")
    void updateHomePageTable(String nameFromApi, String tourGideFromApi,
                             String imageFromApi, String sortIdFromAPI, String id, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertTable(HomePageTable homePageTable);

}

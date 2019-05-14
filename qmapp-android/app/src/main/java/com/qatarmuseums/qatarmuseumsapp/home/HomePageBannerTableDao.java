package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HomePageBannerTableDao {
    @Query("SELECT * FROM homePageBannerTable WHERE language = :language")
    List<HomePageBannerTable> getAllDataFromHomePageBannerTable(String language);

    @Query("SELECT COUNT(qatarMuseum_id) FROM homePageBannerTable WHERE language = :language")
    int getNumberOfBannerRows(String language);

    @Query("UPDATE homePageBannerTable SET name = :nameFromApi, image = :imageFromApi " +
            "WHERE qatarMuseum_id = :id AND language = :language")
    void updateHomePageBanner(String nameFromApi, String imageFromApi, String id, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertBannerTable(HomePageBannerTable homePageBannerTable);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Query("DELETE FROM homePageBannerTable")
    void nukeBannerTable();

}

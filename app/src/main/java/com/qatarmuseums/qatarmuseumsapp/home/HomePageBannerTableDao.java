package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HomePageBannerTableDao {
    @Query("SELECT * FROM homepagebannertableEnglish")
    List<HomePageBannerTableEnglish> getAllDataFromHomePageBannerEnglishTable();

    @Query("SELECT * FROM homepagebannertableArabic")
    List<HomePageBannerTableArabic> getAllDataFromHomePageBannerArabicTable();


    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagebannertableEnglish")
    int getNumberOfBannerRowsEnglish();

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagebannertableArabic")
    int getNumberOfBannerRowsArabic();

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagebannertableEnglish WHERE qatarmuseum_id = :idFromAPI")
    int checkIdExistBannerEnglish(int idFromAPI);

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagebannertableArabic WHERE qatarmuseum_id = :idFromAPI")
    int checkIdExistBannerArabic(int idFromAPI);

    @Query("UPDATE homepagebannertableEnglish SET name = :nameFromApi AND image = :imageFromApi WHERE qatarmuseum_id=:id")
    void updateHomePageBannerEnglish(String nameFromApi, String imageFromApi, String id);

    @Query("UPDATE homepagebannertableArabic SET name=:arabicNameFromApi AND image = :imageFromApi WHERE qatarmuseum_id=:id")
    void updateHomePageBannerArabic(String arabicNameFromApi, String imageFromApi, String id);


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishBannerTable(HomePageBannerTableEnglish homePageBannerTableEnglish);

    @Insert
    void insertArabicBannerTable(HomePageBannerTableArabic homePageBannerTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishBannerTable(HomePageBannerTableEnglish homePageBannerTableEnglish);

    @Update
    void updateArabicBannerTable(HomePageBannerTableArabic homePageBannerTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishBannerTable(HomePageBannerTableEnglish homePageBannerTableEnglish);

    @Delete
    void deleteArabicBannerTable(HomePageBannerTableArabic homePageBannerTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishBannerTable(HomePageBannerTableEnglish... homePageBannerTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicBannerTable(HomePageBannerTableEnglish... homePageBannerTableEnglishes);      // Note... is varargs, here note is an array


}

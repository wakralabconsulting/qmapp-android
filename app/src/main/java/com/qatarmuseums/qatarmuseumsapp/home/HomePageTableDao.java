package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HomePageTableDao {
    @Query("SELECT * FROM homepagetableEnglish")
    List<HomePageTableEnglish> getAllDataFromHomePageEnglishTable();

    @Query("SELECT * FROM homepagetableArabic")
    List<HomePageTableArabic> getAllDataFromHomePageArabicTable();


    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagetableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagetableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagetableEnglish WHERE qatarmuseum_id = :idFromAPI")
    int checkIdExistEnglish(int idFromAPI);

    @Query("SELECT COUNT(qatarmuseum_id) FROM homepagetableArabic WHERE qatarmuseum_id = :idFromAPI")
    int checkIdExistArabic(int idFromAPI);

    @Query("UPDATE homepagetableEnglish SET name = :nameFromApi," +
            "tourguide_available = :tourGideFromApi,image = :imageFromApi, sortId = :sortIdFromAPI " +
            "WHERE qatarmuseum_id=:id")
    void updateHomePageEnglish(String nameFromApi, String tourGideFromApi,
                               String imageFromApi, String sortIdFromAPI, String id);

    @Query("UPDATE homepagetableArabic SET name=:arabicNameFromApi," +
            "tourguide_available = :tourGideFromApi,image = :imageFromApi, sortId = :sortIdFromAPI" +
            " WHERE qatarmuseum_id=:id")
    void updateHomePageArabic(String arabicNameFromApi, String tourGideFromApi,
                              String imageFromApi, String sortIdFromAPI, String id);


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(HomePageTableEnglish homePageTableEnglish);

    @Insert
    void insertArabicTable(HomePageTableArabic homePageTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(HomePageTableEnglish homePageTableEnglish);

    @Update
    void updateArabicTable(HomePageTableArabic homePageTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(HomePageTableEnglish homePageTableEnglish);

    @Delete
    void deleteArabicTable(HomePageTableArabic homePageTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(HomePageTableEnglish... homePageTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(HomePageTableEnglish... homePageTableEnglishes);      // Note... is varargs, here note is an array


}

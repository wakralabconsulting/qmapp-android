package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TourListTableDao {

    @Query("SELECT * FROM tourlistenglish")
    List<TourListTableEnglish> getAllEnglish();

    @Query("SELECT * FROM tourlistarabic")
    List<TourListTableArabic> getAllArabic();

    @Query("SELECT COUNT(tourNid) FROM tourlistenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(tourNid) FROM tourlistarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(tourNid) FROM tourlistenglish WHERE tourNid = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(tourNid) FROM tourlistarabic WHERE tourNid = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM tourlistenglish WHERE tourNid = :idFromAPI")
    List<TourListTableEnglish> getTourListEnglish(int idFromAPI);

    @Query("SELECT * FROM tourlistarabic WHERE tourNid = :idFromAPI")
    List<TourListTableArabic> getTourListArabic(int idFromAPI);

    @Query("UPDATE tourlistenglish SET tourDay = :tourDayFromApi," +
            "tourEventDate = :tourEventDateFromApi," +
            "tourSubtitle = :tourSubtitleFromApi," +
            "tourImages = :tourImagesFromApi," +
            "tourSortId = :tourSortIdFromApi," +
            "tourDescription = :tourDescriptionFromApi WHERE tourNid=:idFromApi")
    void updateTourlistenglish(String tourDayFromApi, String tourEventDateFromApi,
                               String tourSubtitleFromApi, String tourImagesFromApi,
                               String tourSortIdFromApi, String tourDescriptionFromApi,
                               String idFromApi);

    @Query("UPDATE tourlistarabic SET tourDay = :tourDayFromApi," +
            "tourEventDate = :tourEventDateFromApi," +
            "tourSubtitle = :tourSubtitleFromApi," +
            "tourImages = :tourImagesFromApi," +
            "tourSortId = :tourSortIdFromApi," +
            "tourDescription = :tourDescriptionFromApi WHERE tourNid=:idFromApi")
    void updateTourlistarabic(String tourDayFromApi, String tourEventDateFromApi,
                              String tourSubtitleFromApi, String tourImagesFromApi,
                              String tourSortIdFromApi, String tourDescriptionFromApi,
                              String idFromApi);


    @Insert
    void insert(TourListTableEnglish tourListTableEnglish);

    @Insert
    void insert(TourListTableArabic tourListTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(TourListTableEnglish tourListTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(TourListTableEnglish tourListTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(TourListTableEnglish... tourListTableEnglishes);      // Note... is varargs, here note is an array

}

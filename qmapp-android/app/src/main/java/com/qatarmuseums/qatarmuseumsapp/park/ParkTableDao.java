package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ParkTableDao {
    @Query("SELECT * FROM parktableEnglish")
    List<ParkTableEnglish> getAllDataFromParkEnglishTable();

    @Query("SELECT * FROM parktableArabic")
    List<ParkTableArabic> getAllDataFromParkArabicTable();


    @Query("SELECT COUNT(sortId) FROM parktableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(sortId) FROM parktableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(sortId) FROM parktableEnglish WHERE mainTitle = :titleFromAPI")
    int checkIdExistEnglish(String titleFromAPI);

    @Query("SELECT COUNT(sortId) FROM parktableArabic WHERE mainTitle = :titleFromAPI")
    int checkIdExistArabic(String titleFromAPI);

    @Query("UPDATE parktableEnglish SET sortId = :id," +
            "shortDescription = :shortDescriptionFrmAPI,image = :imageFromApi," +
            "latitude = :latitudeFromAPI,longitude = :longitudeFromApi," +
            "timingInfo = :timingInfoFromAPI WHERE mainTitle=:titleFromAPI")
    void updateParkEnglish(String titleFromAPI, String shortDescriptionFrmAPI,
                               String imageFromApi,String latitudeFromAPI,
                               String longitudeFromApi, String timingInfoFromAPI,
                               long id);

    @Query("UPDATE parktableArabic SET sortId = :id," +
            "shortDescription = :shortDescriptionFrmAPI,image = :imageFromApi," +
            "latitude = :latitudeFromAPI,longitude = :longitudeFromApi," +
            "timingInfo = :timingInfoFromAPI WHERE mainTitle=:titleFromAPI")
    void updateParkArabic(String titleFromAPI, String shortDescriptionFrmAPI,
                           String imageFromApi,String latitudeFromAPI,
                           String longitudeFromApi, String timingInfoFromAPI,
                           long id);
    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(ParkTableEnglish parkTableEnglish);
    @Insert
    void insertArabicTable(ParkTableArabic parkTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(ParkTableEnglish parkTableEnglish);
    @Update
    void updateArabicTable(ParkTableEnglish parkTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(ParkTableEnglish parkTableEnglish);

    @Delete
    void deleteArabicTable(ParkTableArabic parkTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(ParkTableEnglish... parkTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(ParkTableArabic... parkTableArabics);      // Note... is varargs, here note is an array

}

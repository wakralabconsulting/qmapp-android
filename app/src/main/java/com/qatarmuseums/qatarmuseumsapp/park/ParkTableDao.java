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

    @Query("SELECT COUNT(sortId) FROM parktableEnglish WHERE sortId = :idFromAPI")
    int checkIdExistEnglish(int idFromAPI);

    @Query("SELECT COUNT(sortId) FROM parktableArabic WHERE sortId = :idFromAPI")
    int checkIdExistArabic(int idFromAPI);

    @Query("UPDATE parktableEnglish SET mainTitle = :mainTitleFromApi," +
            "shortDescription = :shortDescriptionFrmAPI,image = :imageFromApi," +
            "latitude = :latitudeFromAPI,longitude = :longitudeFromApi," +
            "timingInfo = :timingInfoFromAPI WHERE sortId=:id")
    void updateParkEnglish(String mainTitleFromApi, String shortDescriptionFrmAPI,
                               String imageFromApi,String latitudeFromAPI,
                               String longitudeFromApi, String timingInfoFromAPI,
                               String id);

    @Query("UPDATE parktableArabic SET mainTitle = :mainTitleFromApi," +
            "shortDescription = :shortDescriptionFrmAPI,image = :imageFromApi," +
            "latitude = :latitudeFromAPI,longitude = :longitudeFromApi," +
            "timingInfo = :timingInfoFromAPI WHERE sortId=:id")
    void updateParkArabic(String mainTitleFromApi, String shortDescriptionFrmAPI,
                           String imageFromApi,String latitudeFromAPI,
                           String longitudeFromApi, String timingInfoFromAPI,
                           String id);
    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(ParkTableEnglish parkTableEnglish);
    @Insert
    void insertArabicTable(ParkTableEnglish parkTableEnglish);

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

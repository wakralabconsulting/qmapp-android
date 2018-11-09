package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NotificationTableDao {
    @Query("SELECT * FROM notificationTableEnglish")
    List<NotificationTableEnglish> getAllDataFromEnglishTable();

    @Query("SELECT * FROM notificationTableArabic")
    List<NotificationTableArabic> getAllDataFromArabicTable();


    @Query("SELECT COUNT(number) FROM notificationTableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(number) FROM notificationTableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT * FROM notificationTableEnglish")
    LiveData<List<NotificationModel>> findAllEnglish();

    @Query("SELECT * FROM notificationTableArabic")
    LiveData<List<NotificationModel>> findAllArabic();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEnglish(NotificationTableEnglish notificationTableEnglish);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEnglish(NotificationTableArabic notificationTableArabic);

    @Insert
    void insertEnglishTable(NotificationTableEnglish notificationTableEnglish);

    @Insert
    void insertArabicTable(NotificationTableArabic notificationTableArabic);

    @Update
    void updateEnglishTable(NotificationTableEnglish notificationTableEnglish);

    @Update
    void updateArabicTable(NotificationTableArabic notificationTableArabic);

    @Delete
    void deleteEnglishTable(NotificationTableEnglish notificationTableEnglish);

    @Delete
    void deleteArabicTable(NotificationTableArabic notificationTableArabic);

    @Delete
    void deleteEnglishTable(NotificationTableEnglish... notificationTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(NotificationTableArabic... notificationTableEnglishes);      // Note... is varargs, here note is an array


}

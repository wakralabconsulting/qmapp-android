package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NotificationTableDao {
    @Query("SELECT * FROM notificationTable WHERE language = :language")
    List<NotificationTable> getAllDataFromTable(String language);

    @Query("SELECT COUNT(number) FROM notificationTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT * FROM notificationTable WHERE language = :language")
    LiveData<List<NotificationModel>> findAll(String language);

    @Insert
    void insertTable(NotificationTable notificationTable);

}

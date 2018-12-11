package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserRegistrationDetailsTableDao {
    @Query("SELECT * FROM userRegistrationTable")
    List<UserRegistrationDetailsTable> getAllDataFromTable();

    @Query("SELECT * FROM userRegistrationTable WHERE eventId = :eventID")
    List<UserRegistrationDetailsTable> getEventFromTable(String eventID);

    @Insert
    void insertUserRegistrationTable(UserRegistrationDetailsTable userRegistrationDetailsTable);

    @Query("DELETE FROM userRegistrationTable")
    void nukeRegistrationTable();

    @Delete
    void deleteEnglishBannerTable(UserRegistrationDetailsTable userRegistrationDetailsTable);

    @Delete
    void deleteEnglishBannerTable(UserRegistrationDetailsTable... userRegistrationDetailsTables);
    // Note... is varargs, here note is an array


}

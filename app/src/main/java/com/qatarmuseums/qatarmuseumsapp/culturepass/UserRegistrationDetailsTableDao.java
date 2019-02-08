package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserRegistrationDetailsTableDao {
    @Query("SELECT * FROM userRegistrationDetailsEnglishTable")
    List<UserRegistrationDetailsEnglishTable> getAllEnglishDataFromTable();

    @Query("SELECT * FROM userRegistrationDetailsArabicTable")
    List<UserRegistrationDetailsArabicTable> getAllArabicDataFromTable();

    @Query("SELECT * FROM userRegistrationDetailsEnglishTable WHERE eventId = :eventID")
    List<UserRegistrationDetailsEnglishTable> getEnglishEventFromTable(String eventID);

    @Query("SELECT * FROM userRegistrationDetailsArabicTable WHERE eventId = :eventID")
    List<UserRegistrationDetailsArabicTable> getArabicEventFromTable(String eventID);

    @Query("SELECT registrationID FROM userRegistrationDetailsEnglishTable WHERE eventId = :eventID")
    String getEnglishRegistrationIdFromTable(String eventID);

    @Query("SELECT registrationID FROM userRegistrationDetailsArabicTable WHERE eventId = :eventID")
    String getArabicRegistrationIdFromTable(String eventID);

    @Insert
    void insertUserRegistrationTable(UserRegistrationDetailsEnglishTable userRegistrationDetailsEnglishTable);

    @Insert
    void insertArabicUserRegistrationTable(UserRegistrationDetailsArabicTable userRegistrationDetailsArabicTable);

    @Query("DELETE FROM userRegistrationDetailsEnglishTable")
    void nukeEnglishRegistrationTable();

    @Query("DELETE FROM userRegistrationDetailsArabicTable")
    void nukeArabicRegistrationTable();

    @Query("DELETE FROM userRegistrationDetailsEnglishTable WHERE eventId = :eventID")
    int deleteEventFromEnglishTable(String eventID);

    @Query("DELETE FROM userRegistrationDetailsArabicTable WHERE eventId = :eventID")
    int deleteEventFromArabicTable(String eventID);

    @Delete
    void deleteEnglishBannerTable(UserRegistrationDetailsEnglishTable userRegistrationDetailsEnglishTable);

    @Delete
    void deleteEnglishBannerTable(UserRegistrationDetailsEnglishTable... userRegistrationDetailsEnglishTables);
    // Note... is varargs, here note is an array


}

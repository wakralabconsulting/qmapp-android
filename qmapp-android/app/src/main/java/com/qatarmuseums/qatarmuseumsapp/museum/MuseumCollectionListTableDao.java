package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MuseumCollectionListTableDao {

    @Query("SELECT * FROM museumcollectionlisttableEnglish")
    List<MuseumCollectionListTableEnglish> getAllDataFromMuseumListEnglishTable();

    @Query("SELECT * FROM museumcollectionlisttableArabic")
    List<MuseumCollectionListTableArabic> getAllDataFromMuseumListArabicTable();

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableEnglish WHERE name = :nameFromAPI")
    int checkNameExistEnglish(String nameFromAPI);

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableEnglish WHERE name = :nameFromAPI")
    int checkNameExistArabic(String nameFromAPI);

    @Query("UPDATE museumcollectionlisttableEnglish SET image = :imageFromApi," +
            "museum_referance = :museumReferanceFromApi WHERE name=:name")
    void updateMuseumTableEnglish(String imageFromApi, String museumReferanceFromApi,
                                  String name);

    @Query("UPDATE museumcollectionlisttableArabic SET image = :imageFromApi," +
            "museum_referance = :museumReferanceFromApi WHERE name=:name")
    void updateMuseumTableArabic(String imageFromApi, String museumReferanceFromApi,
                                 String name);


    @Insert
    void insertEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);
    @Insert
    void insertArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Update
    void updateEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);
    @Update
    void updateArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Delete
    void deleteEnglishTable(MuseumCollectionListTableEnglish museumCollectionListTableEnglish);

    @Delete
    void deleteArabicTable(MuseumCollectionListTableArabic museumCollectionListTableArabic);

    @Delete
    void deleteEnglishTable(MuseumCollectionListTableEnglish... museumCollectionListTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(MuseumCollectionListTableArabic... museumCollectionListTableArabics);      // Note... is varargs, here note is an array



}

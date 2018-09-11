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

    @Query("SELECT * FROM museumcollectionlisttableEnglish WHERE museum_id = :museumIdFromApi")
    List<MuseumCollectionListTableEnglish> getDataFromEnglishTableWithReference(String museumIdFromApi);

    @Query("SELECT * FROM museumcollectionlisttableArabic WHERE museum_id = :museumIdFromApi")
    List<MuseumCollectionListTableArabic> getDataFromArabicTableWithReference(String museumIdFromApi);

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableEnglish WHERE name = :nameFromAPI")
    int checkNameExistEnglish(String nameFromAPI);

    @Query("SELECT COUNT(name) FROM museumcollectionlisttableArabic WHERE name = :nameFromAPI")
    int checkNameExistArabic(String nameFromAPI);

    @Query("UPDATE museumcollectionlisttableEnglish SET image = :imageFromApi," +
            "museum_id = :museumIdFromApi,category=:categoryFromApi," +
            " collection_description=:collectionDescriptionFromApi WHERE name=:name")
    void updateMuseumListTableEnglish(String imageFromApi, String museumIdFromApi, long categoryFromApi,
                                      String collectionDescriptionFromApi, String name);

    @Query("UPDATE museumcollectionlisttableArabic SET image = :imageFromApi," +
            "museum_id = :museumIdFromApi,category=:categoryFromApi," +
            " collection_description=:collectionDescriptionFromApi WHERE name=:name")
    void updateMuseumListTableArabic(String imageFromApi, String museumIdFromApi,
                                     long categoryFromApi,
                                     String collectionDescriptionFromApi, String name);


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

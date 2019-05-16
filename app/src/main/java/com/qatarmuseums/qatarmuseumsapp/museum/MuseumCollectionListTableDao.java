package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MuseumCollectionListTableDao {

    @Query("SELECT * FROM museumCollectionListTable WHERE museum_id = :museumIdFromApi AND language = :language")
    List<MuseumCollectionListTable> getDataFromTableWithReference(String museumIdFromApi, String language);

    @Query("SELECT COUNT(name) FROM museumCollectionListTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(name) FROM museumCollectionListTable WHERE name = :nameFromAPI AND language = :language")
    int checkNameExist(String nameFromAPI, String language);

    @Query("UPDATE museumCollectionListTable SET image = :imageFromApi," +
            "museum_id = :museumIdFromApi," +
            " collection_description=:collectionDescriptionFromApi WHERE name = :name AND language = :language")
    void updateMuseumListTable(String imageFromApi, String museumIdFromApi,
                               String collectionDescriptionFromApi, String name, String language);

    @Insert
    void insertTable(MuseumCollectionListTable museumCollectionListTable);

}

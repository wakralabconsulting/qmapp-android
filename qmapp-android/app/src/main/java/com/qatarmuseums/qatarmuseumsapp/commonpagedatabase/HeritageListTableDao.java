package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HeritageListTableDao {
    @Query("SELECT * FROM heritagelist")
    List<HeritageListTable> getAll();

    @Query("SELECT COUNT(heritage_id) FROM heritagelist")
    int getNumberOfRows();

    @Query("SELECT COUNT(heritage_id) FROM heritagelist WHERE heritage_id = :idFromAPI")
    int checkIdExist(int idFromAPI);

    @Query("UPDATE heritagelist SET heritage_name = :nameFromApi," +
            "heritage_sortid = :sortidFromApi,heritage_image = :imageFromApi WHERE heritage_id=:id")
    void updateHeritageListEnglish(String nameFromApi, String sortidFromApi,
                                   String imageFromApi, String id);

    @Query("UPDATE heritagelist SET heritage_name_arabic = :nameFromApi," +
            "heritage_sortid = :sortidFromApi,heritage_image = :imageFromApi WHERE heritage_id=:id")
    void updateHeritageListArabic(String nameFromApi, String sortidFromApi,
                                  String imageFromApi, String id);


    @Insert
    void insert(HeritageListTable heritageListTable);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(HeritageListTable heritageListTable);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(HeritageListTable heritageListTable);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(HeritageListTable... heritageListTables);      // Note... is varargs, here note is an array

}

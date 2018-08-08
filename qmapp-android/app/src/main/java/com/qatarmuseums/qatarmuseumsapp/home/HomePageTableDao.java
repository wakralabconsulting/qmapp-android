package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HomePageTableDao {
    @Query("SELECT * FROM homepagetable")
    List<HomePageTable> getAll();

    @Query("SELECT COUNT(qMuseumId) FROM homepagetable")
    int getNumberOfRows();

    @Query("SELECT COUNT(qMuseumId) FROM homepagetable WHERE qMuseumId = :idFromAPI")
    int checkIdExist(int idFromAPI);

    @Query("UPDATE homepagetable SET name = :nameFromApi," +
            "tourguide_available = :tourGideFromApi,image = :imageFromApi WHERE qMuseumId=:id")
    void updateHomePageEnglish(String nameFromApi, String tourGideFromApi,
                               String imageFromApi,String id);

    @Query("UPDATE homepagetable SET arabic_name=:arabicNameFromApi," +
            "tourguide_available = :tourGideFromApi,image = :imageFromApi WHERE qMuseumId=:id")
    void updateHomePageArabic(String arabicNameFromApi, String tourGideFromApi,
                              String imageFromApi,String id);


    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insert(HomePageTable homePageTable);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(HomePageTable homePageTable);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(HomePageTable homePageTable);

    /*
     * delete list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(HomePageTable... homePageTables);      // Note... is varargs, here note is an array


}

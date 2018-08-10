package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by MoongedePC on 08-Aug-18.
 */

@Dao
public interface PublicArtsTableDao {

    @Query("SELECT * FROM publicartstable")
    List<PublicArtsTable> getAll();

    @Query("SELECT COUNT(public_arts_id) FROM publicartstable")
    int getNumberOfRows();

    @Query("SELECT COUNT(public_arts_id) FROM publicartstable WHERE public_arts_id = :idFromAPI")
    int checkIdExist(int idFromAPI);

    @Query("UPDATE publicartstable SET public_arts_name = :nameFromApi," + "public_arts_image = :imageFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi WHERE public_arts_id=:id")
    void updatePublicArtsEnglish(String nameFromApi, String imageFromApi, String latitudeFromApi, String longitudeFromApi, String id);

    @Query("UPDATE publicartstable SET public_arts_name = :nameFromApi," + "public_arts_image = :imageFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi WHERE public_arts_id=:id")
    void updatePublicArtsArabic(String nameFromApi, String imageFromApi, String latitudeFromApi, String longitudeFromApi, String id);

    /*
 * Insert the object in database
 * @param note, object to be inserted
 */
    @Insert
    void insert(PublicArtsTable publicArtsTable);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(PublicArtsTable publicArtsTable);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(PublicArtsTable publicArtsTable);

    /*
     * delete list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(PublicArtsTable... publicArtsTable);      // Note... is varargs, here note is an array

}

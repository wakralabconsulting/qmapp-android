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

    @Query("SELECT COUNT(qatarmuseum_id) FROM publicartstable")
    int getNumberOfRows();

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

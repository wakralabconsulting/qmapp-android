package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TourDetailsTableDao {

    @Query("SELECT * FROM tourdetailstableenglish")
    List<TourDetailsTableEnglish> getAllEnglish();

    @Query("SELECT * FROM tourdetailstablearabic")
    List<TourDetailsTableArabic> getAllArabic();

    @Query("SELECT COUNT(item_id) FROM tourdetailstableenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(item_id) FROM tourdetailstablearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(tour_id) FROM tourdetailstableenglish WHERE tour_id = :idFromAPI")
    int checkEnglishIdExist(String idFromAPI);

    @Query("SELECT COUNT(tour_id) FROM tourdetailstablearabic WHERE tour_id = :idFromAPI")
    int checkArabicIdExist(String idFromAPI);

    @Query("SELECT * FROM tourdetailstableenglish WHERE tour_id = :tourIdFromAPI")
    List<TourDetailsTableEnglish> getTourDetailsWithIdEnglish(String tourIdFromAPI);

    @Query("SELECT * FROM tourdetailstablearabic WHERE tour_id = :tourIdFromAPI")
    List<TourDetailsTableArabic> getTourDetailsWithIdArabic(String tourIdFromAPI);


    @Query("DELETE FROM tourdetailstableenglish WHERE tour_id = :tourIdFromAPI")
    void deleteEnglishTourDetailsWithId(String tourIdFromAPI);

    @Query("DELETE FROM tourdetailstablearabic WHERE tour_id = :tourIdFromAPI")
    void deleteArabicTourDetailsWithId(String tourIdFromAPI);

    @Insert
    void insert(TourDetailsTableEnglish tourDetailsTableEnglish);

    @Insert
    void insert(TourDetailsTableArabic tourDetailsTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(TourDetailsTableEnglish tourDetailsTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(TourDetailsTableEnglish tourDetailsTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(TourDetailsTableEnglish... tourDetailsTableEnglishes);      // Note... is varargs, here note is an array

}

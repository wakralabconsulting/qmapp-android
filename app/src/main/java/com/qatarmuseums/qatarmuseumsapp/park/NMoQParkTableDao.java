package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NMoQParkTableDao {
    @Query("SELECT * FROM nMoQParkTableEnglish")
    List<NMoQParkTableEnglish> getAllDataFromNMoQParkEnglishTable();

    @Query("SELECT * FROM nMoQParkTableArabic")
    List<NMoQParkTableArabic> getAllDataFromNMoQParkArabicTable();


    @Query("SELECT COUNT(parkNid) FROM nMoQParkTableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkTableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkTableEnglish WHERE parkNid = :idFromAPI")
    int checkIdExistEnglish(int idFromAPI);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkTableArabic WHERE parkNid = :idFromAPI")
    int checkIdExistArabic(int idFromAPI);

    @Query("UPDATE nMoQParkTableEnglish SET parkToolbarTitle = :parkToolbarTitleFromApi," +
            "mainDescription = :mainDescriptionFrmApi, parkTitle = :parkTitleFromApi," +
            "parkTitleDescription = :parkTitleDescriptionFromApi, parkHoursTitle = :parkHoursTitleFromApi," +
            "parksHoursDescription = :parksHoursDescriptionFromApi, parkLocationTitle = :parkLocationTitleFromApi, " +
            "parkLatitude = :parkLatitudeFromApi, parkLongitude = :parkLongitudeFromApi WHERE parkNid=:nid")
    void updateNMoQParkEnglish(String parkToolbarTitleFromApi, String mainDescriptionFrmApi,
                               String parkTitleFromApi, String parkTitleDescriptionFromApi,
                               String parkHoursTitleFromApi, String parksHoursDescriptionFromApi,
                               String parkLocationTitleFromApi, String parkLatitudeFromApi,
                               String parkLongitudeFromApi, long nid);

    @Query("UPDATE nMoQParkTableArabic SET parkToolbarTitle = :parkToolbarTitleFromApi," +
            "mainDescription = :mainDescriptionFrmApi, parkTitle = :parkTitleFromApi," +
            "parkTitleDescription = :parkTitleDescriptionFromApi, parkHoursTitle = :parkHoursTitleFromApi," +
            "parksHoursDescription = :parksHoursDescriptionFromApi, parkLocationTitle = :parkLocationTitleFromApi, " +
            "parkLatitude = :parkLatitudeFromApi, parkLongitude = :parkLongitudeFromApi WHERE parkNid=:nid")
    void updateNMoQParkArabic(String parkToolbarTitleFromApi, String mainDescriptionFrmApi,
                              String parkTitleFromApi, String parkTitleDescriptionFromApi,
                              String parkHoursTitleFromApi, String parksHoursDescriptionFromApi,
                              String parkLocationTitleFromApi, String parkLatitudeFromApi,
                              String parkLongitudeFromApi, long nid);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(NMoQParkTableEnglish nMoQParkTableEnglish);

    @Insert
    void insertArabicTable(NMoQParkTableArabic nMoQParkTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(NMoQParkTableEnglish nMoQParkTableEnglish);

    @Update
    void updateArabicTable(NMoQParkTableArabic nMoQParkTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkTableEnglish nMoQParkTableEnglish);

    @Delete
    void deleteArabicTable(NMoQParkTableArabic nMoQParkTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkTableEnglish... nMoQParkTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(NMoQParkTableArabic... nMoQParkTableArabics);      // Note... is varargs, here note is an array

}

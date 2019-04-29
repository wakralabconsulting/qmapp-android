package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NMoQParkListDetailsTableDao {
    @Query("SELECT * FROM nMoQParkListDetailsTableEnglish")
    List<NMoQParkListDetailsTableEnglish> getAllDataFromNMoQParkListDetailsEnglishTable();

    @Query("SELECT * FROM nMoQParkListDetailsTableArabic")
    List<NMoQParkListDetailsTableArabic> getAllDataFromNMoQParkListDetailsArabicTable();

    @Query("SELECT * FROM nMoQParkListDetailsTableEnglish  WHERE parkNid = :idFromAPI")
    List<NMoQParkListDetailsTableEnglish> getNMoQParkListDetailsEnglishTable(int idFromAPI);

    @Query("SELECT * FROM nMoQParkListDetailsTableArabic  WHERE parkNid = :idFromAPI")
    List<NMoQParkListDetailsTableArabic> getNMoQParkListDetailsArabicTable(int idFromAPI);


    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTableEnglish WHERE parkNid = :idFromAPI")
    int checkIdExistEnglish(int idFromAPI);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTableArabic WHERE parkNid = :idFromAPI")
    int checkIdExistArabic(int idFromAPI);

    @Query("UPDATE nMoQParkListDetailsTableEnglish SET  parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi," +
            "parkDescription = :parkDescriptionFromApi WHERE parkNid=:nid")
    void updateNMoQParkListDetailsEnglish(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                                          String parkSortIdFromApi, String parkDescriptionFromApi,
                                          String nid);

    @Query("UPDATE nMoQParkListDetailsTableArabic SET parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi," +
            "parkDescription = :parkDescriptionFromApi WHERE parkNid=:nid")
    void updateNMoQParkListDetailsArabic(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                                         String parkSortIdFromApi, String parkDescriptionFromApi,
                                         String nid);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish);

    @Insert
    void insertArabicTable(NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish);

    @Update
    void updateArabicTable(NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkListDetailsTableEnglish nMoQParkListDetailsTableEnglish);

    @Delete
    void deleteArabicTable(NMoQParkListDetailsTableArabic nMoQParkListDetailsTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkListDetailsTableEnglish... nMoQParkListDetailsTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(NMoQParkListDetailsTableArabic... nMoQParkListDetailsTableArabics);      // Note... is varargs, here note is an array

}

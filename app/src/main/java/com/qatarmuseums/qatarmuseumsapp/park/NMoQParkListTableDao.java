package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NMoQParkListTableDao {
    @Query("SELECT * FROM nMoQParkListTableEnglish")
    List<NMoQParkListTableEnglish> getAllDataFromNMoQParkListEnglishTable();

    @Query("SELECT * FROM nMoQParkListTableArabic")
    List<NMoQParkListTableArabic> getAllDataFromNMoQParkListArabicTable();

    @Query("SELECT * FROM nMoQParkListTableEnglish  WHERE parkNid = :idFromAPI")
    List<NMoQParkListTableEnglish> getNMoQParkListEnglishTable(int idFromAPI);

    @Query("SELECT * FROM nMoQParkListTableArabic  WHERE parkNid = :idFromAPI")
    List<NMoQParkListTableArabic> getNMoQParkListArabicTable(int idFromAPI);


    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTableEnglish WHERE parkNid = :idFromAPI")
    int checkIdExistEnglish(int idFromAPI);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTableArabic WHERE parkNid = :idFromAPI")
    int checkIdExistArabic(int idFromAPI);

    @Query("UPDATE nMoQParkListTableEnglish SET  parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi WHERE parkNid=:nid")
    void updateNMoQParkListEnglish(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                                   String parkSortIdFromApi, String nid);

    @Query("UPDATE nMoQParkListTableArabic SET parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi WHERE parkNid=:nid")
    void updateNMoQParkListArabic(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                                  String parkSortIdFromApi, String nid);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertEnglishTable(NMoQParkListTableEnglish nMoQParkListTableEnglish);

    @Insert
    void insertArabicTable(NMoQParkListTableArabic nMoQParkListTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void updateEnglishTable(NMoQParkListTableEnglish nMoQParkListTableEnglish);

    @Update
    void updateArabicTable(NMoQParkListTableArabic nMoQParkListTableArabic);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkListTableEnglish nMoQParkListTableEnglish);

    @Delete
    void deleteArabicTable(NMoQParkListTableArabic nMoQParkListTableArabic);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void deleteEnglishTable(NMoQParkListTableEnglish... nMoQParkListTableEnglishes);      // Note... is varargs, here note is an array

    @Delete
    void deleteArabicTable(NMoQParkListTableArabic... nMoQParkListTableArabics);      // Note... is varargs, here note is an array

}

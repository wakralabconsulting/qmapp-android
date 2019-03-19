package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FacilityListTableDao {
    @Query("SELECT * FROM facilitylisttableenglish")
    List<FacilityListTableEnglish> getAllEnglish();

    @Query("SELECT * FROM facilitylisttablearabic")
    List<FacilityListTableArabic> getAllArabic();


    @Query("SELECT COUNT(facilityNid) FROM facilitylisttableenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(facilityNid) FROM facilitylisttablearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(facilityNid) FROM facilitylisttableenglish WHERE facilityNid = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(facilityNid) FROM facilitylisttablearabic WHERE facilityNid = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);


    @Query("SELECT * FROM facilitylisttableenglish WHERE facilityNid = :facilityNidFromAPI")
    List<FacilityListTableEnglish> getFacilityListEnglish(int facilityNidFromAPI);

    @Query("SELECT * FROM facilitylisttablearabic WHERE facilityNid = :facilityNidFromAPI")
    List<FacilityListTableArabic> getFacilityListArabic(int facilityNidFromAPI);

    @Query("UPDATE facilitylisttableenglish SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi WHERE facilityNid=:idFromApi")
    void updateFacilityListEnglish(String sortIdFromApi, String facilityTitleFromApi,
                                   String facilityImageFromApi,String idFromApi);


    @Query("UPDATE facilitylisttablearabic SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi WHERE facilityNid=:idFromApi")
    void updateFacilityListArabic(String sortIdFromApi, String facilityTitleFromApi,
                                  String facilityImageFromApi,
                                  String idFromApi);


    @Insert
    void insertEnglish(FacilityListTableEnglish facilityListTableEnglish);

    @Insert
    void insertArabic(FacilityListTableArabic facilityListTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(FacilityListTableEnglish facilityListTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(FacilityListTableEnglish facilityListTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(FacilityListTableEnglish... facilityListTableEnglishes);      // Note... is varargs, here note is an array



}

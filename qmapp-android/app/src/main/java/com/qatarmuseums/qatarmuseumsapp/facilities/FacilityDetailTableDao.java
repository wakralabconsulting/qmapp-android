package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FacilityDetailTableDao {
    @Query("SELECT * FROM facilitydetailtableenglish")
    List<FacilityDetailTableEnglish> getAllEnglish();

    @Query("SELECT * FROM facilitydetailtablearabic")
    List<FacilityDetailTableArabic> getAllArabic();


    @Query("SELECT COUNT(facilityNid) FROM facilitydetailtableenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(facilityNid) FROM facilitydetailtablearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(facilityNid) FROM facilitydetailtableenglish WHERE facilityNid = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(facilityNid) FROM facilitydetailtablearabic WHERE facilityNid = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);


    @Query("SELECT * FROM facilitydetailtableenglish WHERE facilityCategoryId = :facilityCategoryIdFromAPI")
    List<FacilityDetailTableEnglish> getFacilityDetailEnglish(int facilityCategoryIdFromAPI);

    @Query("SELECT * FROM facilitydetailtablearabic WHERE facilityNid = :facilityCategoryIdFromAPI")
    List<FacilityDetailTableArabic> getFacilityDetailArabic(int facilityCategoryIdFromAPI);

    @Query("UPDATE facilitydetailtableenglish SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi," +
            "facilitySubtitle = :facilitySubtitleFromApi," +
            "facilityDescription = :facilityDescriptionFromApi," +
            "facilityTiming = :facilityTimingFromApi," +
            "facilityLongitude = :facilityLongitudeFromApi," +
            "facilityCategoryId = :facilityCategoryIdFromApi," +
            "facilityLatitude = :facilityLatitudeFromApi," +
            "facilityLocationTitle = :facilityLocationTitleFromApi," +
            "facilityTitleTiming = :facilityTimingTitle WHERE facilityNid=:idFromApi")
    void updateFacilityDetailEnglish(String sortIdFromApi, String facilityTitleFromApi,
                                   String facilityImageFromApi, String facilitySubtitleFromApi,
                                   String facilityDescriptionFromApi, String facilityTimingFromApi,
                                   String facilityLongitudeFromApi, String facilityCategoryIdFromApi,
                                   String facilityLatitudeFromApi, String facilityLocationTitleFromApi,
                                   String facilityTimingTitle,
                                   String idFromApi);


    @Query("UPDATE facilitydetailtablearabic SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi," +
            "facilitySubtitle = :facilitySubtitleFromApi," +
            "facilityDescription = :facilityDescriptionFromApi," +
            "facilityTiming = :facilityTimingFromApi," +
            "facilityLongitude = :facilityLongitudeFromApi," +
            "facilityCategoryId = :facilityCategoryIdFromApi," +
            "facilityLatitude = :facilityLatitudeFromApi," +
            "facilityLocationTitle = :facilityLocationTitleFromApi," +
            "facilityTitleTiming = :facilityTimingTitle WHERE facilityNid=:idFromApi")
    void updateFacilityDetailArabic(String sortIdFromApi, String facilityTitleFromApi,
                                  String facilityImageFromApi, String facilitySubtitleFromApi,
                                  String facilityDescriptionFromApi, String facilityTimingFromApi,
                                  String facilityLongitudeFromApi, String facilityCategoryIdFromApi,
                                  String facilityLatitudeFromApi, String facilityLocationTitleFromApi,
                                  String facilityTimingTitle,
                                  String idFromApi);


    @Insert
    void insertEnglish(FacilityDetailTableEnglish facilityDetailTableEnglish);

    @Insert
    void insertArabic(FacilityDetailTableArabic facilityDetailTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(FacilityDetailTableEnglish facilityDetailTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(FacilityDetailTableEnglish facilityDetailTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(FacilityDetailTableEnglish... facilityDetailTableEnglishes);      // Note... is varargs, here note is an array



}

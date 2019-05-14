package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FacilityDetailTableDao {

    @Query("SELECT COUNT(facilityNid) FROM facilityDetailTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(facilityNid) FROM facilityDetailTable WHERE facilityNid = :idFromAPI" +
            " AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM facilityDetailTable WHERE facilityCategoryId = :facilityCategoryIdFromAPI " +
            "AND language = :language")
    List<FacilityDetailTable> getFacilityDetail(int facilityCategoryIdFromAPI, String language);

    @Query("UPDATE facilityDetailTable SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi," +
            "facilitySubtitle = :facilitySubtitleFromApi," +
            "facilityDescription = :facilityDescriptionFromApi," +
            "facilityTiming = :facilityTimingFromApi," +
            "facilityLongitude = :facilityLongitudeFromApi," +
            "facilityCategoryId = :facilityCategoryIdFromApi," +
            "facilityLatitude = :facilityLatitudeFromApi," +
            "facilityLocationTitle = :facilityLocationTitleFromApi," +
            "facilityTitleTiming = :facilityTimingTitle WHERE facilityNid=:idFromApi AND language = :language")
    void updateFacilityDetail(String sortIdFromApi, String facilityTitleFromApi,
                              String facilityImageFromApi, String facilitySubtitleFromApi,
                              String facilityDescriptionFromApi, String facilityTimingFromApi,
                              String facilityLongitudeFromApi, String facilityCategoryIdFromApi,
                              String facilityLatitudeFromApi, String facilityLocationTitleFromApi,
                              String facilityTimingTitle,
                              String idFromApi, String language);

    @Insert
    void insertData(FacilityDetailTable facilityDetailTable);

}

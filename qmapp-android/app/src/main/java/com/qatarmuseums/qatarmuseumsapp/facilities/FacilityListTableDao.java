package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FacilityListTableDao {
    @Query("SELECT * FROM facilityListTable WHERE language = :language")
    List<FacilityListTable> getAllData(String language);

    @Query("SELECT COUNT(facilityNid) FROM facilityListTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(facilityNid) FROM facilityListTable WHERE facilityNid = :idFromAPI" +
            " AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE facilityListTable SET sortId = :sortIdFromApi," +
            "facilityTitle = :facilityTitleFromApi," +
            "facilityImage = :facilityImageFromApi WHERE facilityNid = :idFromApi AND language = :language")
    void updateFacilityList(String sortIdFromApi, String facilityTitleFromApi,
                            String facilityImageFromApi, String idFromApi, String language);

    @Insert
    void insertData(FacilityListTable facilityListTable);

}

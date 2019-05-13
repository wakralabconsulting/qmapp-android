package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NMoQParkTableDao {
    @Query("SELECT * FROM nMoQParkTable WHERE language = :language")
    List<NMoQParkTable> getAllDataFromNMoQParkTable(String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkTable WHERE parkNid = :idFromAPI AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE nMoQParkTable SET parkToolbarTitle= :parkToolbarTitleFromApi," +
            "mainDescription = :mainDescriptionFrmApi, parkTitle = :parkTitleFromApi," +
            "parkTitleDescription = :parkTitleDescriptionFromApi, parkHoursTitle = :parkHoursTitleFromApi," +
            "parksHoursDescription = :parksHoursDescriptionFromApi, parkLocationTitle = :parkLocationTitleFromApi, " +
            "parkLatitude = :parkLatitudeFromApi, parkLongitude = :parkLongitudeFromApi WHERE" +
            " parkNid = :nid AND language = :language")
    void updateNMoQPark(String parkToolbarTitleFromApi, String mainDescriptionFrmApi,
                        String parkTitleFromApi, String parkTitleDescriptionFromApi,
                        String parkHoursTitleFromApi, String parksHoursDescriptionFromApi,
                        String parkLocationTitleFromApi, String parkLatitudeFromApi,
                        String parkLongitudeFromApi, long nid, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertData(NMoQParkTable nMoQParkTable);

}

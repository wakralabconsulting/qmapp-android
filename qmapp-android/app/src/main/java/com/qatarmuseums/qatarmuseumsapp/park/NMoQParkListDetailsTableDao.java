package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NMoQParkListDetailsTableDao {

    @Query("SELECT * FROM nMoQParkListDetailsTable  WHERE parkNid = :idFromAPI AND language = :language")
    List<NMoQParkListDetailsTable> getNMoQParkListDetailsTable(int idFromAPI, String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListDetailsTable WHERE parkNid = :idFromAPI AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE nMoQParkListDetailsTable SET  parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi," +
            "parkDescription = :parkDescriptionFromApi WHERE parkNid = :nid AND language = :language")
    void updateNMoQParkListDetails(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                                   String parkSortIdFromApi, String parkDescriptionFromApi,
                                   String nid, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertTable(NMoQParkListDetailsTable nMoQParkListDetailsTable);

}

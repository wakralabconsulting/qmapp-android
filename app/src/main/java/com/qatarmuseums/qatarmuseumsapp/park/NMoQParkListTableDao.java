package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NMoQParkListTableDao {
    @Query("SELECT * FROM nMoQParkListTable WHERE language = :language")
    List<NMoQParkListTable> getAllDataFromNMoQParkListTable(String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(parkNid) FROM nMoQParkListTable WHERE parkNid = :idFromAPI AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE nMoQParkListTable SET  parkTitle = :parkTitleFromApi," +
            "parkImages = :parkImagesFromApi, parkSortId = :parkSortIdFromApi WHERE parkNid = :nid" +
            " AND language = :language")
    void updateNMoQParkList(String parkTitleFromApi, ArrayList<String> parkImagesFromApi,
                            String parkSortIdFromApi, String nid, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insertData(NMoQParkListTable nMoQParkListTable);

}

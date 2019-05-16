package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface PublicArtsTableDao {

    @Query("SELECT * FROM publicArtsTable WHERE language = :language")
    List<PublicArtsTable> getAllData(String language);

    @Query("SELECT * FROM publicArtsTable WHERE public_arts_id = :idFromAPI AND language = :language")
    List<PublicArtsTable> getPublicArtsDetails(String idFromAPI, String language);

    @Query("SELECT COUNT(public_arts_id) FROM publicArtsTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(public_arts_id) FROM publicArtsTable WHERE public_arts_id = :idFromAPI " +
            "AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("UPDATE publicArtsTable SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi WHERE public_arts_id=:id " +
            "AND language = :language")
    void updatePublicArts(String nameFromApi, String latitudeFromApi, String longitudeFromApi,
                          String id, String language);

    @Query("UPDATE publicArtsTable SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi," + "description=:descriptionFromApi," +
            "short_description=:shortDescriptionFromApi," + "public_arts_images=:imagesFromApi " +
            " WHERE public_arts_id=:id AND language = :language")
    void updatePublicArtsDetail(String nameFromApi, String latitudeFromApi,
                                String longitudeFromApi, String descriptionFromApi, String shortDescriptionFromApi,
                                String imagesFromApi, String id, String language);

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insert(PublicArtsTable publicArtsTable);

}

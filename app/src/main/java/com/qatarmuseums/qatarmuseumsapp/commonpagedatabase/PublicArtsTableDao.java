package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface PublicArtsTableDao {

    @Query("SELECT * FROM publicartstableEnglish")
    List<PublicArtsTableEnglish> getAllEnglish();

    @Query("SELECT * FROM publicartstableArabic")
    List<PublicArtsTableArabic> getAllArabic();

    @Query("SELECT COUNT(public_arts_id) FROM publicartstableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(public_arts_id) FROM publicartstableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(public_arts_id) FROM publicartstableEnglish WHERE public_arts_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(public_arts_id) FROM publicartstableArabic WHERE public_arts_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("UPDATE publicartstableEnglish SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi WHERE public_arts_id=:id")
    void updatePublicArtsEnglish(String nameFromApi, String latitudeFromApi, String longitudeFromApi, String id);

    @Query("UPDATE publicartstableEnglish SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi," + "description=:descriptionFromApi," +
            "short_description=:shortDescriptionFromApi  WHERE public_arts_id=:id")
    void updatePublicArtsDetailEnglish(String nameFromApi, String latitudeFromApi,
                                       String longitudeFromApi, String descriptionFromApi, String shortDescriptionFromApi,
                                       String id);


    @Query("UPDATE publicartstableArabic SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi WHERE public_arts_id=:id")
    void updatePublicArtsArabic(String nameFromApi, String latitudeFromApi, String longitudeFromApi, String id);

    @Query("UPDATE publicartstableArabic SET public_arts_name = :nameFromApi," +
            "latitude=:latitudeFromApi," + "longitude=:longitudeFromApi," + "description=:descriptionFromApi," +
            "short_description=:shortDescriptionFromApi  WHERE public_arts_id=:id")
    void updatePublicArtsDetailArabic(String nameFromApi, String latitudeFromApi,
                                      String longitudeFromApi, String descriptionFromApi, String shortDescriptionFromApi,
                                      String id);

    /*
 * Insert the object in database
 * @param note, object to be inserted
 */
    @Insert
    void insert(PublicArtsTableEnglish publicArtsTableEnglish);

    @Insert
    void insert(PublicArtsTableArabic publicArtsTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(PublicArtsTableEnglish publicArtsTableEnglish);


    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(PublicArtsTableEnglish publicArtsTableEnglish);


    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(PublicArtsTableEnglish... publicArtsTableEnglish);      // Note... is varargs, here note is an array


}

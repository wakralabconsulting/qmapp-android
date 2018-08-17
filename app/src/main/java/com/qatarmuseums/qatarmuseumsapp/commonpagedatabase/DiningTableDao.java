package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface DiningTableDao {

    @Query("SELECT * FROM diningtableEnglish")
    List<DiningTableEnglish> getAllEnglish();

    @Query("SELECT * FROM diningtableArabic")
    List<DiningTableArabic> getAllArabic();

    @Query("SELECT COUNT(dining_id) FROM diningtableEnglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(dining_id) FROM diningtableArabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(dining_id) FROM diningtableEnglish WHERE dining_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(dining_id) FROM diningtableArabic WHERE dining_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM diningtableEnglish WHERE dining_id = :idFromAPI")
    List<DiningTableEnglish> getDiningDetailsEnglish(int idFromAPI);

    @Query("SELECT * FROM diningtableArabic WHERE dining_id = :idFromAPI")
    List<DiningTableArabic> getDiningDetailsArabic(int idFromAPI);

    @Query("UPDATE diningtableEnglish SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            " dining_sort_id = :sortIdFromApi WHERE dining_id = :id")
    void updateDiningEnglish(String nameFromApi, String imageFromApi, String id, String sortIdFromApi);

    @Query("UPDATE diningtableEnglish SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            "description = :descriptionFromApi, opening_time = :openingTimeFromApi, closing_time = :closingTimeFromApi," +
            "latitude = :latitudeFromApi, longitude = :longitudeFromApi, dining_sort_id = :sortIdFromApi WHERE dining_id = :id")
    void updateDiningDetailsEnglish(String nameFromApi, String imageFromApi, String descriptionFromApi,
                                    String openingTimeFromApi, String closingTimeFromApi, String latitudeFromApi,
                                    String longitudeFromApi, String id, String sortIdFromApi);


    @Query("UPDATE diningtableArabic SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            " dining_sort_id = :sortIdFromApi WHERE dining_id = :id")
    void updateDiningArabic(String nameFromApi, String imageFromApi, String id, String sortIdFromApi);

    @Query("UPDATE diningtableArabic SET dining_name = :nameFromApi, dining_image = :imageFromApi," +
            "description = :descriptionFromApi, opening_time = :openingTimeFromApi, closing_time = :closingTimeFromApi," +
            "latitude = :latitudeFromApi, longitude = :longitudeFromApi, dining_sort_id = :sortIdFromApi WHERE dining_id = :id")
    void updateDiningDetailsArabic(String nameFromApi, String imageFromApi, String descriptionFromApi,
                                   String openingTimeFromApi, String closingTimeFromApi, String latitudeFromApi,
                                   String longitudeFromApi, String id, String sortIdFromApi);


    /*
 * Insert the object in database
 * @param note, object to be inserted
 */
    @Insert
    void insert(DiningTableEnglish diningTableEnglish);

    @Insert
    void insert(DiningTableArabic diningTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(DiningTableEnglish diningTableEnglish);


    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(DiningTableEnglish diningTableEnglish);


    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(DiningTableEnglish... diningTableEnglishes);      // Note... is varargs, here note is an array


}

package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface HeritageListTableDao {
    @Query("SELECT * FROM heritagelistenglish")
    List<HeritageListTableEnglish> getAllEnglish();

    @Query("SELECT * FROM heritagelistarabic")
    List<HeritageListTableArabic> getAllArabic();

    @Query("SELECT COUNT(heritage_id) FROM heritagelistenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(heritage_id) FROM heritagelistarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(heritage_id) FROM heritagelistenglish WHERE heritage_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(heritage_id) FROM heritagelistarabic WHERE heritage_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM heritagelistenglish WHERE heritage_id = :idFromAPI")
    List<HeritageListTableEnglish> getHeritageDetailsEnglish(int idFromAPI);

    @Query("SELECT * FROM heritagelistarabic WHERE heritage_id = :idFromAPI")
    List<HeritageListTableArabic> getHeritageDetailsArabic(int idFromAPI);

    @Query("UPDATE heritagelistenglish SET heritage_name = :nameFromApi," +
            "heritage_sortid = :sortidFromApi,heritage_image = :imageFromApi WHERE heritage_id=:id")
    void updateHeritageListEnglish(String nameFromApi, String sortidFromApi,
                                   String imageFromApi, String id);


    @Query("UPDATE heritagelistenglish SET latitude = :latitudeFromApi,longitude = :longitudeFromApi," +
            "heritage_long_description=:descriptionFromApi," + "heritage_short_description=:shortDescriptionFromApi," +
             "heritage_image=:imagesFromApi WHERE heritage_id=:id")
    void updateHeritageDetailEnglish(String latitudeFromApi,
                                     String longitudeFromApi, String descriptionFromApi,
                                     String shortDescriptionFromApi,String imagesFromApi, String id);

    @Query("UPDATE heritagelistarabic SET heritage_name = :nameFromApi," + "heritage_sortid = :sortidFromApi," +
            "heritage_image = :imageFromApi WHERE heritage_id=:id")
    void updateHeritageListArabic(String nameFromApi, String sortidFromApi,
                                  String imageFromApi, String id);

    @Query("UPDATE heritagelistarabic SET latitude = :latitudeFromApi,longitude = :longitudeFromApi," +
            "heritage_long_description=:descriptionFromApi," +
            "heritage_short_description=:shortDescriptionFromApi," + "heritage_image=:imagesFromApi WHERE heritage_id=:id")
    void updateHeritageDetailArabic( String latitudeFromApi,
                                    String longitudeFromApi, String descriptionFromApi,
                                    String shortDescriptionFromApi,String imagesFromApi, String id);


    @Insert
    void insert(HeritageListTableEnglish heritageListTableEnglish);

    @Insert
    void insert(HeritageListTableArabic heritageListTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(HeritageListTableEnglish heritageListTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(HeritageListTableEnglish heritageListTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(HeritageListTableEnglish... heritageListTableEnglishes);      // Note... is varargs, here note is an array

}

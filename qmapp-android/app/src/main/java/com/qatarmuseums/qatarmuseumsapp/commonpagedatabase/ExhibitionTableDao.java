package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by MoongedePC on 17-Aug-18.
 */

@Dao
public interface ExhibitionTableDao {

    @Query("SELECT * FROM exhibitionlistenglish")
    List<ExhibitionListTableEnglish> getAllEnglish();

    @Query("SELECT * FROM exhibitionlistarabic")
    List<ExhibitionListTableArabic> getAllArabic();

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionlistenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionlistarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionlistenglish WHERE exhibition_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(exhibition_id) FROM exhibitionlistarabic WHERE exhibition_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM exhibitionlistenglish WHERE exhibition_id = :idFromAPI")
    List<ExhibitionListTableEnglish> getExhibitionDetailsEnglish(int idFromAPI);

    @Query("SELECT * FROM exhibitionlistarabic WHERE exhibition_id = :idFromAPI")
    List<ExhibitionListTableArabic> getExhibitionDetailsArabic(int idFromAPI);

    @Query("UPDATE exhibitionlistenglish SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi,exhibition_location = :locationFromApi WHERE exhibition_id=:id")
    void updateExhibitionListEnglish(String startDateFromApi, String endDateFromApi,
                                     String locationFromApi, String id);

    @Query("UPDATE exhibitionlistenglish SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi,exhibition_location = :locationFromApi," +
            "exhibition_latest_image = :imageFromApi," +
            "exhibition_long_description=:descriptionFromApi," +
            "exhibition_short_description=:shortDescriptionFromApi WHERE exhibition_id=:id")
    void updateExhibitionDetailEnglish(String startDateFromApi, String endDateFromApi,String locationFromApi,
                                       String imageFromApi, String descriptionFromApi,
                                       String shortDescriptionFromApi, String id);

    @Query("UPDATE exhibitionlistarabic SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi,exhibition_location = :locationFromApi WHERE exhibition_id=:id")
    void updateExhibitionListArabic(String startDateFromApi, String endDateFromApi,
                                    String locationFromApi, String id);

    @Query("UPDATE exhibitionlistarabic SET exhibition_start_date = :startDateFromApi," +
            "exhibition_end_date = :endDateFromApi,exhibition_location = :locationFromApi," +
            "exhibition_latest_image = :imageFromApi," +
            "exhibition_long_description=:descriptionFromApi," +
            "exhibition_short_description=:shortDescriptionFromApi WHERE exhibition_id=:id")
    void updateExhibitionDetailArabic(String startDateFromApi, String endDateFromApi,String locationFromApi,
                                      String imageFromApi, String descriptionFromApi,
                                      String shortDescriptionFromApi, String id);

    @Insert
    void insert(ExhibitionListTableEnglish exhibitionListTableEnglish);

    @Insert
    void insert(ExhibitionListTableArabic exhibitionListTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(ExhibitionListTableEnglish exhibitionListTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(ExhibitionListTableEnglish exhibitionListTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(ExhibitionListTableEnglish... exhibitionListTableEnglishes);      // Note... is varargs, here note is an array

}

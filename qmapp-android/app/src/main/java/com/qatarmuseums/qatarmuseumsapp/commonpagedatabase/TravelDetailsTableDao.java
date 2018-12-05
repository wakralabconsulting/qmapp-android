package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TravelDetailsTableDao {

    @Query("SELECT * FROM traveldetailsenglish")
    List<TravelDetailsTableEnglish> getAllEnglish();

    @Query("SELECT * FROM traveldetailsarabic")
    List<TravelDetailsTableArabic> getAllArabic();

    @Query("SELECT COUNT(content_ID) FROM traveldetailsenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(content_ID) FROM traveldetailsarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(content_ID) FROM traveldetailsenglish WHERE content_ID = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(content_ID) FROM traveldetailsarabic WHERE content_ID = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM traveldetailsenglish WHERE content_ID = :idFromAPI")
    List<TravelDetailsTableEnglish> getExhibitionDetailsEnglish(int idFromAPI);

    @Query("SELECT * FROM traveldetailsarabic WHERE content_ID = :idFromAPI")
    List<TravelDetailsTableArabic> getExhibitionDetailsArabic(int idFromAPI);

    @Query("UPDATE traveldetailsenglish SET travel_description = :travelDescriptionFromApi," +
            "travel_image = :travelImageFromApi," +
            "travel_name = :travelNameFromApi," +
            "travel_email = :travelEmailFromApi," +
            "contact_number = :contactNumberFromApi," +
            "promotional_code = :promotionalCodeFromApi WHERE content_ID=:idFromApi")
    void updateTraveldetailsenglish(String travelNameFromApi, String travelImageFromApi,
                                    String travelDescriptionFromApi, String promotionalCodeFromApi,
                                    String contactNumberFromApi, String travelEmailFromApi,
                                    String idFromApi);

    @Query("UPDATE traveldetailsarabic SET travel_description = :travelDescriptionFromApi," +
            "travel_image = :travelImageFromApi," +
            "travel_name = :travelNameFromApi," +
            "travel_email = :travelEmailFromApi," +
            "contact_number = :contactNumberFromApi," +
            "promotional_code = :promotionalCodeFromApi WHERE content_ID=:idFromApi")
    void updateTraveldetailsarabic(String travelNameFromApi, String travelImageFromApi,
                                   String travelDescriptionFromApi, String promotionalCodeFromApi,
                                   String contactNumberFromApi, String travelEmailFromApi,
                                   String idFromApi);


    @Insert
    void insert(TravelDetailsTableEnglish travelDetailsTableEnglish);

    @Insert
    void insert(TravelDetailsTableArabic travelDetailsTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(TravelDetailsTableEnglish travelDetailsTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(TravelDetailsTableEnglish travelDetailsTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(TravelDetailsTableEnglish... travelDetailsTableEnglishes);      // Note... is varargs, here note is an array

}

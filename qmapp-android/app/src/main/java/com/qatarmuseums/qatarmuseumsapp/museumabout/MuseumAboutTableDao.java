package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MuseumAboutTableDao {
    @Query("SELECT * FROM museumabouttableenglish")
    List<MuseumAboutTableEnglish> getAllMuseumAboutDataEnglish();

    @Query("SELECT * FROM museumabouttablearabic")
    List<MuseumAboutTableArabic> getAllMuseumAboutDataArabic();

    @Query("SELECT COUNT(museum_id) FROM museumabouttableenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(museum_id) FROM museumabouttablearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(museum_id) FROM museumabouttableenglish WHERE museum_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(museum_id) FROM museumabouttablearabic WHERE museum_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM museumabouttableenglish WHERE museum_id = :idFromAPI")
    List<MuseumAboutTableEnglish> getMuseumAboutDataEnglish(int idFromAPI);

    @Query("SELECT * FROM museumabouttablearabic WHERE museum_id = :idFromAPI")
    List<MuseumAboutTableArabic> getMuseumAboutDataArabic(int idFromAPI);

    @Query("UPDATE museumabouttableenglish SET museum_title = :museumtitleFromApi," +
            "museum_subtitle = :museumSubtitleFromApi,museum_image = :museumImageFromApi, " +
            "museum_short_description = :museumShortDescriptionFromApi,museum_long_description = :museumLongDescriptionFromApi, " +
            "museum_opening_time = :museumOpeningTimeFromApi,exhibition_location = :exhibition_locationFromApi," +
            " museum_contact = :museumContactFromApi,museum_filter = :museumFilterFromApi WHERE museum_id=:idFromApi")
    void updateMuseumAboutDataEnglish(String museumtitleFromApi,
                                      String museumSubtitleFromApi, String museumImageFromApi,
                                      String museumShortDescriptionFromApi,
                                      String museumLongDescriptionFromApi, String museumOpeningTimeFromApi,
                                      String exhibition_locationFromApi, String museumContactFromApi,
                                      String museumFilterFromApi, String idFromApi);

    @Query("UPDATE museumabouttablearabic SET museum_title = :museumtitleFromApi," +
            "museum_subtitle = :museumSubtitleFromApi,museum_image = :museumImageFromApi, " +
            "museum_short_description = :museumShortDescriptionFromApi,museum_long_description = :museumLongDescriptionFromApi, " +
            "museum_opening_time = :museumOpeningTimeFromApi,exhibition_location = :exhibition_locationFromApi," +
            " museum_contact = :museumContactFromApi,museum_filter = :museumFilterFromApi WHERE museum_id=:idFromApi")
    void updateMuseumAboutDataArabic(String museumtitleFromApi,
                                     String museumSubtitleFromApi, String museumImageFromApi,
                                     String museumShortDescriptionFromApi,
                                     String museumLongDescriptionFromApi, String museumOpeningTimeFromApi,
                                     String exhibition_locationFromApi, String museumContactFromApi,
                                     String museumFilterFromApi, String idFromApi);

    @Insert
    void insert(MuseumAboutTableEnglish museumAboutTableEnglish);

    @Insert
    void insert(MuseumAboutTableArabic museumAboutTableArabic);


}

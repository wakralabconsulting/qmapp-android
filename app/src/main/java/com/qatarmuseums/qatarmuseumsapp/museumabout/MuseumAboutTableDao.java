package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface MuseumAboutTableDao {
    @Query("SELECT * FROM museumabouttableenglish")
    MuseumAboutTableEnglish getAllMuseumAboutDataEnglish();

    @Query("SELECT * FROM museumabouttablearabic")
    MuseumAboutTableArabic getAllMuseumAboutDataArabic();

    @Query("SELECT COUNT(museum_id) FROM museumabouttableenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(museum_id) FROM museumabouttablearabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(museum_id) FROM museumabouttableenglish WHERE museum_id = :idFromAPI")
    int checkEnglishIdExist(int idFromAPI);

    @Query("SELECT COUNT(museum_id) FROM museumabouttablearabic WHERE museum_id = :idFromAPI")
    int checkArabicIdExist(int idFromAPI);

    @Query("SELECT * FROM museumabouttableenglish WHERE museum_id = :idFromAPI")
    MuseumAboutTableEnglish getMuseumAboutDataEnglish(int idFromAPI);

    @Query("SELECT * FROM museumabouttablearabic WHERE museum_id = :idFromAPI")
    MuseumAboutTableArabic getMuseumAboutDataArabic(int idFromAPI);

    @Query("UPDATE museumabouttableenglish SET museum_name = :museumnameFromApi," +
            "tourGuide_available = :tourguideAvailableFromApi, short_description = :museumshortDescriptionFromApi," +
            "long_description = :museumLongDescriptionFromApi, " +
            "museum_image = :museumImageFromApi, museum_contact_number = :museumContactNumberFromApi, " +
            "museum_contact_email = :museumContactEmailFromApi, museum_lattitude = :museumLattitude," +
            "museum_longitude = :museumLongitude," +
            "tourguide_availability = :museumtourguideAvailabilityFromApi," +
            "museum_subtitle = :museumSubtitleFromApi," +
            "museum_opening_time = :museumOpeningTimeFromApi WHERE museum_id = :idFromApi")
    void updateMuseumAboutDataEnglish(String museumnameFromApi,
                                      String tourguideAvailableFromApi, String museumshortDescriptionFromApi,
                                      String museumLongDescriptionFromApi,
                                      String museumImageFromApi, String museumContactNumberFromApi,
                                      String museumContactEmailFromApi, String museumLattitude,
                                      String museumLongitude, String museumtourguideAvailabilityFromApi,
                                      String museumSubtitleFromApi, String museumOpeningTimeFromApi, long idFromApi);

    @Query("UPDATE museumabouttablearabic SET museum_name = :museumnameFromApi," +
            "tourGuide_available = :tourguideAvailableFromApi,short_description = :museumShortDescriptionFromApi,long_description = :museumLongDescriptionFromApi, " +
            "museum_image = :museumImageFromApi,museum_contact_number = :museumContactNumberFromApi, " +
            "museum_contact_email = :museumContactEmailFromApi,museum_lattitude = :museumLattitude," +
            "museum_longitude = :museumLongitude," +
            "tourguide_availability = :museumtourguideAvailabilityFromApi," +
            "museum_subtitle = :museumSubtitleFromApi," +
            "museum_opening_time = :museumOpeningTimeFromApi WHERE museum_id = :idFromApi")
    void updateMuseumAboutDataArabic(String museumnameFromApi,
                                     String tourguideAvailableFromApi, String museumShortDescriptionFromApi,
                                     String museumLongDescriptionFromApi,
                                     String museumImageFromApi, String museumContactNumberFromApi,
                                     String museumContactEmailFromApi, String museumLattitude,
                                     String museumLongitude, String museumtourguideAvailabilityFromApi,
                                     String museumSubtitleFromApi, String museumOpeningTimeFromApi, long idFromApi);

    @Insert
    void insert(MuseumAboutTableEnglish museumAboutTableEnglish);

    @Insert
    void insert(MuseumAboutTableArabic museumAboutTableArabic);


}

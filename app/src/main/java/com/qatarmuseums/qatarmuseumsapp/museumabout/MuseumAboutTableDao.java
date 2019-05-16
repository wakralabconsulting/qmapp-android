package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface MuseumAboutTableDao {

    @Query("SELECT COUNT(museum_id) FROM museumAboutTable WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(museum_id) FROM museumAboutTable WHERE museum_id = :idFromAPI AND language = :language")
    int checkIdExist(int idFromAPI, String language);

    @Query("SELECT * FROM museumAboutTable WHERE museum_id = :idFromAPI AND language = :language")
    MuseumAboutTable getMuseumAboutData(int idFromAPI, String language);

    @Query("UPDATE museumAboutTable SET museum_name = :museumNameFromApi," +
            "tour_guide_available = :tourGuideAvailableFromApi, short_description = :museumShortDescriptionFromApi," +
            "long_description = :museumLongDescriptionFromApi, " +
            "museum_image = :museumImageFromApi, museum_contact_number = :museumContactNumberFromApi, " +
            "museum_contact_email = :museumContactEmailFromApi, museum_latitude = :museumLatitude," +
            "museum_longitude = :museumLongitude," +
            "tour_guide_availability = :museumTourGuideAvailabilityFromApi," +
            "museum_subtitle = :museumSubtitleFromApi," +
            "museum_opening_time = :museumOpeningTimeFromApi WHERE museum_id = :idFromApi AND language = :language")
    void updateMuseumAboutData(String museumNameFromApi,
                               String tourGuideAvailableFromApi, String museumShortDescriptionFromApi,
                               String museumLongDescriptionFromApi,
                               String museumImageFromApi, String museumContactNumberFromApi,
                               String museumContactEmailFromApi, String museumLatitude,
                               String museumLongitude, String museumTourGuideAvailabilityFromApi,
                               String museumSubtitleFromApi, String museumOpeningTimeFromApi,
                               long idFromApi, String language);

    @Insert
    void insert(MuseumAboutTable museumAboutTable);

}

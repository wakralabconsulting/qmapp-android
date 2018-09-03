package com.qatarmuseums.qatarmuseumsapp.education;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface EducationCalendarTableDao {
    @Query("SELECT COUNT(event_id) FROM educationcalendareventstenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI")
    int checkEnglishWithEventDateExist(String eventDateFromAPI);

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI")
    int checkArabicWithEventDateExist(String eventDateFromAPI);

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI AND "
            + " event_id = :eventIdFromApi")
    int checkEnglishWithEventIdExist(String eventDateFromAPI, String eventIdFromApi);

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI AND "
            + " event_id = :eventIdFromApi")
    int checkArabicWithEventIdExist(String eventDateFromAPI, String eventIdFromApi);


    @Query("SELECT * FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI AND "
            + " event_institution = :instituionFilterFromApi AND " + " event_age_group = :ageGroupFilter AND "
            + " event_program_type = :programmeFilterApi")
    List<EducationalCalendarEventsTableEnglish> getEventsWithDateEnglish(String eventDateFromAPI, String instituionFilterFromApi,
                                                                         String ageGroupFilter, String programmeFilterApi);


    @Query("SELECT * FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI")
    List<EducationalCalendarEventsTableEnglish> getAllEventsEnglish(String eventDateFromAPI);


    @Query("SELECT * FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI")
    List<EducationalCalendarEventsTableArabic> getAllEventsArabic(String eventDateFromAPI);

    @Query("SELECT * FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI AND "
            + " event_institution = :instituionFilterFromApi AND " + " event_age_group = :ageGroupFilter AND "
            + " event_program_type = :programmeFilterApi")
    List<EducationalCalendarEventsTableArabic> getEventsWithDateArabic(String eventDateFromAPI, String instituionFilterFromApi,
                                                                       String ageGroupFilter, String programmeFilterApi);

    @Query("UPDATE educationcalendareventstenglish SET event_title = :eventTitleFromAPI," +
            "event_start_time = :eventStartTimeFromApi," +
            "event_end_time = :eventEndTimeFromApi," +
            "event_registration=:registrationApi," +
            "max_group_size=:maxGroupSizeApi," +
            "event_short_description=:shortDescriptionApi," +
            "event_long_description=:longDescriptionApi," +
            "location =:locationFromApi," +
            "category=:categoryFromApi WHERE event_id=:id")
    void updateEventsEnglish(String eventTitleFromAPI, String eventStartTimeFromApi,
                             String eventEndTimeFromApi, String registrationApi,
                             String maxGroupSizeApi, String shortDescriptionApi,
                             String longDescriptionApi, String locationFromApi,
                             String categoryFromApi, String id);


    @Query("UPDATE educationcalendareventstarabic SET event_title = :eventTitleFromAPI," +
            "event_start_time = :eventStartTimeFromApi," +
            "event_end_time = :eventEndTimeFromApi," +
            "event_registration=:registrationApi," +
            "max_group_size=:maxGroupSizeApi," +
            "event_short_description=:shortDescriptionApi," +
            "event_long_description=:longDescriptionApi," +
            "location =:locationFromApi," +
            "category=:categoryFromApi WHERE event_id=:id")
    void updateEventsArabic(String eventTitleFromAPI, String eventStartTimeFromApi,
                            String eventEndTimeFromApi, String registrationApi,
                            String maxGroupSizeApi, String shortDescriptionApi,
                            String longDescriptionApi, String locationFromApi,
                            String categoryFromApi, String id);


    @Insert
    void insertEventsTableEnglish(EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish);

    @Insert
    void insertEventsTableArabic(EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic);

    /*
        * updateEnglishTable the object in database
        * @param note, object to be updated
        */
    @Update
    void update(EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(EducationalCalendarEventsTableEnglish... educationalCalendarEventsTableEnglishes);      // Note... is varargs, here note is an array


}

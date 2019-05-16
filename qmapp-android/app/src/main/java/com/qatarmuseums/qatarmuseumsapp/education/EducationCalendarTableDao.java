package com.qatarmuseums.qatarmuseumsapp.education;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EducationCalendarTableDao {
    @Query("SELECT COUNT(event_id) FROM educationCalendarEvents WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(event_id) FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI " +
            "AND language = :language")
    int checkWithEventDateExist(String eventDateFromAPI, String language);

    @Query("SELECT COUNT(event_id) FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND "
            + " event_id = :eventIdFromApi AND language = :language")
    int checkWithEventIdExist(String eventDateFromAPI, String eventIdFromApi, String language);

    @Query("SELECT * FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND "
            + " event_institution = :institutionFilterFromApi AND "
            + " event_program_type = :programmeFilterApi AND language = :language")
    List<EducationalCalendarEventsTable> getEventsWithDate(String eventDateFromAPI, String institutionFilterFromApi,
                                                           String programmeFilterApi, String language);

    @Query("SELECT * FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND "
            + " event_institution = :institutionFilterFromApi AND language = :language")
    List<EducationalCalendarEventsTable> getInstitutionFilterEvents(String eventDateFromAPI,
                                                                    String institutionFilterFromApi,
                                                                    String language);


    @Query("SELECT * FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND language = :language")
    List<EducationalCalendarEventsTable> getAgeGroupFilterEvents(String eventDateFromAPI,
                                                                 String language);

    @Query("SELECT * FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND "
            + " event_program_type = :programmeFilterFromApi AND language = :language")
    List<EducationalCalendarEventsTable> getProgrammeFilterEvents(String eventDateFromAPI,
                                                                  String programmeFilterFromApi,
                                                                  String language);

    @Query("SELECT * FROM educationCalendarEvents WHERE event_date = :eventDateFromAPI AND language = :language")
    List<EducationalCalendarEventsTable> getAllEvents(String eventDateFromAPI,
                                                      String language);


    @Query("UPDATE educationCalendarEvents SET event_title = :eventTitleFromAPI," +
            "event_start_time = :eventStartTimeFromApi," +
            "event_end_time = :eventEndTimeFromApi," +
            "event_registration=:registrationApi," +
            "max_group_size=:maxGroupSizeApi," +
            "event_short_description=:shortDescriptionApi," +
            "event_long_description=:longDescriptionApi," +
            "location =:locationFromApi," +
            "category=:categoryFromApi WHERE event_id=:id AND language = :language")
    void updateEvents(String eventTitleFromAPI, String eventStartTimeFromApi,
                      String eventEndTimeFromApi, String registrationApi,
                      String maxGroupSizeApi, String shortDescriptionApi,
                      String longDescriptionApi, String locationFromApi,
                      String categoryFromApi, String id, String language);

    @Insert
    void insertEventsTable(EducationalCalendarEventsTable educationalCalendarEventsTable);

}

package com.qatarmuseums.qatarmuseumsapp.education;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEventsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarEventsTableEnglish;

import java.util.List;

@Dao
public interface EducationCalendarTableDao {
    @Query("SELECT COUNT(event_id) FROM educationcalendareventstenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI AND "
            + " event_id=:eventIdFromApi")
    int checkEnglishWithEventDateExist(String eventDateFromAPI, String eventIdFromApi);

    @Query("SELECT COUNT(event_id) FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI AND "
            + " event_id=:eventIdFromApi")
    int checkArabicWithEventDateExist(String eventDateFromAPI, String eventIdFromApi);


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


    @Query("DELETE FROM educationcalendareventstenglish WHERE event_date = :eventDateFromAPI")
    void deleteEnglishEventsWithDate(String eventDateFromAPI);

    @Query("DELETE FROM educationcalendareventstarabic WHERE event_date = :eventDateFromAPI")
    void deleteArabicEventsWithDate(String eventDateFromAPI);

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

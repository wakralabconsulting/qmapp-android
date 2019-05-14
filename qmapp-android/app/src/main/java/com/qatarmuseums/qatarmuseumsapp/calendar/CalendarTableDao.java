package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CalendarTableDao {

    @Query("SELECT COUNT(event_id) FROM calendarEvents WHERE language = :language")
    int getNumberOfRows(String language);

    @Query("SELECT COUNT(event_id) FROM calendarEvents WHERE event_date = :eventDateFromAPI AND language = :language")
    int checkEventDateExist(long eventDateFromAPI, String language);

    @Query("SELECT * FROM calendarEvents WHERE event_date = :eventDateFromAPI AND language = :language")
    List<CalendarEventsTable> getEventsWithDate(long eventDateFromAPI, String language);


    @Query("DELETE FROM calendarEvents WHERE event_date = :eventDateFromAPI AND language = :language")
    void deleteEventsWithDate(long eventDateFromAPI, String language);

    @Insert
    void insertEventsTable(CalendarEventsTable calendarEventsTable);

}

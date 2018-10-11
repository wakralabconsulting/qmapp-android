package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface CalendarTableDao {

    @Query("SELECT COUNT(event_id) FROM calendareventstenglish")
    int getNumberOfRowsEnglish();

    @Query("SELECT COUNT(event_id) FROM calendareventstarabic")
    int getNumberOfRowsArabic();

    @Query("SELECT COUNT(event_id) FROM calendareventstenglish WHERE event_date = :eventDateFromAPI")
    int checkEnglishWithEventDateExist(long eventDateFromAPI);

    @Query("SELECT COUNT(event_id) FROM calendareventstarabic WHERE event_date = :eventDateFromAPI")
    int checkArabicWithEventDateExist(long eventDateFromAPI);

    @Query("SELECT * FROM calendareventstenglish WHERE event_date = :eventDateFromAPI")
    List<CalendarEventsTableEnglish> getEventsWithDateEnglish(long eventDateFromAPI);

    @Query("SELECT * FROM calendareventstarabic WHERE event_date = :eventDateFromAPI")
    List<CalendarEventsTableArabic> getEventsWithDateArabic(long eventDateFromAPI);

    @Query("DELETE FROM calendareventstenglish WHERE event_date = :eventDateFromAPI")
    void deleteEnglishEventsWithDate(long eventDateFromAPI);

    @Query("DELETE FROM calendareventstarabic WHERE event_date = :eventDateFromAPI")
    void deleteArabicEventsWithDate(long eventDateFromAPI);

//    public class CalendarConverters {
//        @TypeConverter
//        public ArrayList<String> fromCalendarString(String value) {
//            Type listType = new TypeToken<ArrayList<String>>() {
//            }.getType();
//            return new Gson().fromJson(value, listType);
//        }
//
//        @TypeConverter
//        public String fromCalendarArrayList(ArrayList<String> list) {
//            Gson gson = new Gson();
//            String json = gson.toJson(list);
//            return json;
//        }
//    }

    @Insert
    void insertEventsTableEnglish(CalendarEventsTableEnglish calendarEventsTableEnglish);

    @Insert
    void insertEventsTableArabic(CalendarEventsTableArabic calendarEventsTableArabic);

    /*
     * updateEnglishTable the object in database
     * @param note, object to be updated
     */
    @Update
    void update(CalendarEventsTableEnglish calendarEventsTableEnglish);

    /*
     * deleteEnglishTable the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(CalendarEventsTableEnglish calendarEventsTableEnglish);

    /*
     * deleteEnglishTable list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(CalendarEventsTableEnglish... calendarEventsTableEnglishes);      // Note... is varargs, here note is an array

}

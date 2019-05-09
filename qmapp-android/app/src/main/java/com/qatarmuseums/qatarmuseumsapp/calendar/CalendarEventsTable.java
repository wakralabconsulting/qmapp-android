package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "calendarEvents")
public class CalendarEventsTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey(autoGenerate = true)
    private long sl_number;
    @ColumnInfo()
    private String event_id;
    @ColumnInfo()
    private String event_title;
    @ColumnInfo()
    private String event_date;
    @ColumnInfo()
    private String event_institution;
    @ColumnInfo()
    private String event_program_type;
    @ColumnInfo()
    private String event_start_time;
    @ColumnInfo()
    private String event_end_time;
    @ColumnInfo()
    private String event_registration;
    @ColumnInfo()
    private String max_group_size;
    @ColumnInfo()
    private String event_short_description;
    @ColumnInfo()
    private String event_long_description;
    @ColumnInfo()
    private String location;
    @ColumnInfo()
    private String category;
    @ColumnInfo()
    private String filter;
    @ColumnInfo()
    private String field;
    @ColumnInfo()
    private String age_group;
    @ColumnInfo()
    private String associated_topics;
    @ColumnInfo()
    private String museum;


    CalendarEventsTable(@NonNull String event_id, String event_title,
                        String event_short_description, String event_long_description,
                        String event_institution, String event_program_type,
                        String category, String location, String event_date,
                        String event_start_time, String event_end_time, String event_registration,
                        String filter, String max_group_size, String field,
                        String age_group, String associated_topics, String museum, String language) {
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_short_description = event_short_description;
        this.event_long_description = event_long_description;
        this.event_date = event_date;
        this.event_institution = event_institution;
        this.event_program_type = event_program_type;
        this.category = category;
        this.location = location;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
        this.event_registration = event_registration;
        this.filter = filter;
        this.max_group_size = max_group_size;
        this.field = field;
        this.age_group = age_group;
        this.associated_topics = associated_topics;
        this.museum = museum;
        this.language = language;
    }

    long getSl_number() {
        return sl_number;
    }

    void setSl_number(long sl_number) {
        this.sl_number = sl_number;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    String getEvent_short_description() {
        return event_short_description;
    }

    String getEvent_long_description() {
        return event_long_description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    String getEvent_institution() {
        return event_institution;
    }

    String getEvent_program_type() {
        return event_program_type;
    }

    String getEvent_start_time() {
        return event_start_time;
    }

    String getEvent_end_time() {
        return event_end_time;
    }

    String getEvent_registration() {
        return event_registration;
    }

    String getMax_group_size() {
        return max_group_size;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    String getAssociated_topics() {
        return associated_topics;
    }

    public String getMuseum() {
        return museum;
    }

    public void setMuseum(String museum) {
        this.museum = museum;
    }

    public String getLanguage() {
        return language;
    }
}

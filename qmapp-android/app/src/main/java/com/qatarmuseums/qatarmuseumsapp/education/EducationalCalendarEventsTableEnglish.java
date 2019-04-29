package com.qatarmuseums.qatarmuseumsapp.education;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "educationcalendareventstenglish")
public class EducationalCalendarEventsTableEnglish {
    @NonNull
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


    EducationalCalendarEventsTableEnglish(String event_id, String event_title,
                                          String event_date, String event_institution,
                                          String event_program_type, String event_start_time,
                                          String event_end_time, String event_registration,
                                          String max_group_size, String event_short_description,
                                          String event_long_description,
                                          String location, String category, String filter, String field,
                                          String age_group, String associated_topics, String museum) {
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_date = event_date;
        this.event_institution = event_institution;
        this.event_program_type = event_program_type;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
        this.event_registration = event_registration;
        this.max_group_size = max_group_size;
        this.event_short_description = event_short_description;
        this.event_long_description = event_long_description;
        this.location = location;
        this.category = category;
        this.filter = filter;
        this.field = field;
        this.age_group = age_group;
        this.associated_topics = associated_topics;
        this.museum = museum;
    }

    @NonNull
    public long getSl_number() {
        return sl_number;
    }

    public void setSl_number(@NonNull long sl_number) {
        this.sl_number = sl_number;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_institution() {
        return event_institution;
    }

    public void setEvent_institution(String event_institution) {
        this.event_institution = event_institution;
    }


    public String getEvent_program_type() {
        return event_program_type;
    }

    public void setEvent_program_type(String event_program_type) {
        this.event_program_type = event_program_type;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public void setEvent_end_time(String event_end_time) {
        this.event_end_time = event_end_time;
    }

    public String getEvent_registration() {
        return event_registration;
    }

    public String isEvent_registration() {
        return event_registration;
    }

    public void setEvent_registration(String event_registration) {
        this.event_registration = event_registration;
    }

    public String getMax_group_size() {
        return max_group_size;
    }

    public void setMax_group_size(String max_group_size) {
        this.max_group_size = max_group_size;
    }

    public String getEvent_short_description() {
        return event_short_description;
    }

    public void setEvent_short_description(String event_short_description) {
        this.event_short_description = event_short_description;
    }

    public String getEvent_long_description() {
        return event_long_description;
    }

    public void setEvent_long_description(String event_long_description) {
        this.event_long_description = event_long_description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public String getAssociated_topics() {
        return associated_topics;
    }

    public void setAssociated_topics(String associated_topics) {
        this.associated_topics = associated_topics;
    }

    public String getMuseum() {
        return museum;
    }

    public void setMuseum(String museum) {
        this.museum = museum;
    }
}

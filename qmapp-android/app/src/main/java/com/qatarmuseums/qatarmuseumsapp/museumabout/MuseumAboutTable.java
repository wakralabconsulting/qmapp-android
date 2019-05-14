package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "museumAboutTable")
public class MuseumAboutTable {
    @ColumnInfo()
    private String language;
    @ColumnInfo()
    private String museum_name;
    @PrimaryKey()
    private long museum_id;
    @ColumnInfo()
    private String tour_guide_available;
    @ColumnInfo()
    private String short_description;
    @ColumnInfo()
    private String long_description;
    @ColumnInfo()
    private String museum_image;
    @ColumnInfo()
    private String museum_contact_number;
    @ColumnInfo()
    private String museum_contact_email;
    @ColumnInfo()
    private String museum_latitude;
    @ColumnInfo()
    private String museum_longitude;
    @ColumnInfo()
    private String tour_guide_availability;
    @ColumnInfo()
    private String museum_subtitle;
    @ColumnInfo()
    private String museum_opening_time;
    @ColumnInfo()
    private String event_date;

    public MuseumAboutTable(String museum_name, long museum_id,
                            String tour_guide_available, String short_description,
                            String long_description,
                            String museum_image, String museum_contact_number,
                            String museum_contact_email, String museum_latitude,
                            String museum_longitude, String tour_guide_availability,
                            String museum_subtitle, String museum_opening_time,
                            String event_date, String language) {
        this.museum_name = museum_name;
        this.museum_id = museum_id;
        this.tour_guide_available = tour_guide_available;
        this.short_description = short_description;
        this.long_description = long_description;
        this.museum_image = museum_image;
        this.museum_contact_number = museum_contact_number;
        this.museum_contact_email = museum_contact_email;
        this.museum_latitude = museum_latitude;
        this.museum_longitude = museum_longitude;
        this.tour_guide_availability = tour_guide_availability;
        this.museum_subtitle = museum_subtitle;
        this.museum_opening_time = museum_opening_time;
        this.event_date = event_date;
        this.language = language;
    }

    String getMuseum_name() {
        return museum_name;
    }

    public long getMuseum_id() {
        return museum_id;
    }

    String getTour_guide_available() {
        return tour_guide_available;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getLong_description() {
        return long_description;
    }

    public String getMuseum_image() {
        return museum_image;
    }

    public String getMuseum_contact_number() {
        return museum_contact_number;
    }

    public String getMuseum_contact_email() {
        return museum_contact_email;
    }

    public String getMuseum_latitude() {
        return museum_latitude;
    }

    public String getMuseum_longitude() {
        return museum_longitude;
    }

    String getTour_guide_availability() {
        return tour_guide_availability;
    }

    public String getMuseum_subtitle() {
        return museum_subtitle;
    }

    public String getMuseum_opening_time() {
        return museum_opening_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getLanguage() {
        return language;
    }
}

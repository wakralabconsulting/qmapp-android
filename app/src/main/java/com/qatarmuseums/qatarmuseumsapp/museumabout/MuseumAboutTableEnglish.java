package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "museumabouttableenglish")
public class MuseumAboutTableEnglish {
    @ColumnInfo()
    private String museum_name;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long museum_id;
    @ColumnInfo()
    private String tourguide_available;
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
    private String museum_lattitude;
    @ColumnInfo()
    private String museum_longitude;
    @ColumnInfo()
    private String tourguide_availability;
    @ColumnInfo()
    private String museum_subtitle;
    @ColumnInfo()
    private String museum_opening_time;
    @ColumnInfo()
    private String event_date;

    public MuseumAboutTableEnglish(String museum_name, @NonNull long museum_id,
                                   String tourguide_available, String short_description,
                                   String long_description,
                                   String museum_image, String museum_contact_number,
                                   String museum_contact_email, String museum_lattitude,
                                   String museum_longitude, String tourguide_availability,
                                   String museum_subtitle, String museum_opening_time,
                                   String event_date) {
        this.museum_name = museum_name;
        this.museum_id = museum_id;
        this.tourguide_available = tourguide_available;
        this.short_description = short_description;
        this.long_description = long_description;
        this.museum_image = museum_image;
        this.museum_contact_number = museum_contact_number;
        this.museum_contact_email = museum_contact_email;
        this.museum_lattitude = museum_lattitude;
        this.museum_longitude = museum_longitude;
        this.tourguide_availability = tourguide_availability;
        this.museum_subtitle = museum_subtitle;
        this.museum_opening_time = museum_opening_time;
        this.event_date = event_date;
    }


    public String getMuseum_name() {
        return museum_name;
    }

    @NonNull
    public long getMuseum_id() {
        return museum_id;
    }

    public String getTourguide_available() {
        return tourguide_available;
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

    public String getMuseum_lattitude() {
        return museum_lattitude;
    }

    public String getMuseum_longitude() {
        return museum_longitude;
    }

    public String getTourguide_availability() {
        return tourguide_availability;
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
}

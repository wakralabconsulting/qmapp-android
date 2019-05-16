package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "tourDetailsTable")
public class TourDetailsTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long item_id;
    @ColumnInfo()
    private String tour_title;
    @ColumnInfo()
    private String tour_id;
    @ColumnInfo()
    private String seats_remaining;
    @ColumnInfo()
    private String tour_images;
    @ColumnInfo()
    private String tour_date;
    @ColumnInfo()
    private String tour_contact_email;
    @ColumnInfo()
    private String tour_contact_phone;
    @ColumnInfo()
    private String tour_longitude;
    @ColumnInfo()
    private String tour_latitude;
    @ColumnInfo()
    private String tour_sort_id;
    @ColumnInfo()
    private String tour_body;
    @ColumnInfo()
    private String tour_registered;
    @ColumnInfo()
    private String moderator_name;
    @ColumnInfo()
    private String description_for_moderator;

    public TourDetailsTable(String tour_title, String tour_images,
                            String tour_date, String tour_id,
                            String tour_contact_email, String tour_contact_phone,
                            String tour_latitude,
                            String tour_longitude,
                            String tour_sort_id, String tour_body,
                            String tour_registered,
                            String moderator_name, String description_for_moderator,
                            String seats_remaining, long item_id, String language) {
        this.tour_title = tour_title;
        this.tour_images = tour_images;
        this.tour_date = tour_date;
        this.tour_contact_email = tour_contact_email;
        this.tour_contact_phone = tour_contact_phone;
        this.tour_latitude = tour_latitude;
        this.tour_longitude = tour_longitude;
        this.tour_sort_id = tour_sort_id;
        this.tour_body = tour_body;
        this.tour_registered = tour_registered;
        this.tour_id = tour_id;
        this.moderator_name = moderator_name;
        this.description_for_moderator = description_for_moderator;
        this.seats_remaining = seats_remaining;
        this.item_id = item_id;
        this.language = language;
    }

    public long getItem_id() {
        return item_id;
    }

    public String getTour_title() {
        return tour_title;
    }

    public String getTour_id() {
        return tour_id;
    }

    public String getTour_images() {
        return tour_images;
    }

    public String getTour_date() {
        return tour_date;
    }

    public String getTour_contact_email() {
        return tour_contact_email;
    }

    public String getTour_contact_phone() {
        return tour_contact_phone;
    }

    public String getTour_longitude() {
        return tour_longitude;
    }

    public String getTour_latitude() {
        return tour_latitude;
    }

    public String getTour_sort_id() {
        return tour_sort_id;
    }

    public String getTour_body() {
        return tour_body;
    }

    public String getTour_registered() {
        return tour_registered;
    }


    String getModerator_name() {
        return moderator_name;
    }


    String getDescription_for_moderator() {
        return description_for_moderator;
    }


    public String getSeats_remaining() {
        return seats_remaining;
    }

    public String getLanguage() {
        return language;
    }
}

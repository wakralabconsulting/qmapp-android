package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "tourdetailstableenglish")
public class TourDetailsTableEnglish {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long item_id;
    @ColumnInfo()
    private String tour_title;
    @ColumnInfo()
    private String tour_id;
    @ColumnInfo()
    private String tour_images;
    @ColumnInfo()
    private String tour_date;
    @ColumnInfo()
    private String tour_contact_email;
    @ColumnInfo()
    private String tour_contact_phone;
    @ColumnInfo()
    private String tour_longtitude;
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

    public TourDetailsTableEnglish(String tour_title, String tour_images,
                                   String tour_date, String tour_id,
                                   String tour_contact_email, String tour_contact_phone,
                                   String tour_latitude,
                                   String tour_longtitude,
                                   String tour_sort_id, String tour_body,
                                   String tour_registered,
                                   String moderator_name, String description_for_moderator) {
        this.tour_title = tour_title;
        this.tour_images = tour_images;
        this.tour_date = tour_date;
        this.tour_contact_email = tour_contact_email;
        this.tour_contact_phone = tour_contact_phone;
        this.tour_latitude = tour_latitude;
        this.tour_longtitude = tour_longtitude;
        this.tour_sort_id = tour_sort_id;
        this.tour_body = tour_body;
        this.tour_registered = tour_registered;
        this.tour_id = tour_id;
        this.moderator_name = moderator_name;
        this.description_for_moderator = description_for_moderator;
    }

    @NonNull
    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(@NonNull long item_id) {
        this.item_id = item_id;
    }

    public String getTour_title() {
        return tour_title;
    }

    public void setTour_title(String tour_title) {
        this.tour_title = tour_title;
    }

    public String getTour_id() {
        return tour_id;
    }

    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }

    public String getTour_images() {
        return tour_images;
    }

    public void setTour_images(String tour_images) {
        this.tour_images = tour_images;
    }

    public String getTour_date() {
        return tour_date;
    }

    public void setTour_date(String tour_date) {
        this.tour_date = tour_date;
    }

    public String getTour_contact_email() {
        return tour_contact_email;
    }

    public void setTour_contact_email(String tour_contact_email) {
        this.tour_contact_email = tour_contact_email;
    }

    public String getTour_contact_phone() {
        return tour_contact_phone;
    }

    public void setTour_contact_phone(String tour_contact_phone) {
        this.tour_contact_phone = tour_contact_phone;
    }

    public String getTour_longtitude() {
        return tour_longtitude;
    }

    public void setTour_longtitude(String tour_longtitude) {
        this.tour_longtitude = tour_longtitude;
    }

    public String getTour_latitude() {
        return tour_latitude;
    }

    public void setTour_latitude(String tour_latitude) {
        this.tour_latitude = tour_latitude;
    }

    public String getTour_sort_id() {
        return tour_sort_id;
    }

    public void setTour_sort_id(String tour_sort_id) {
        this.tour_sort_id = tour_sort_id;
    }

    public String getTour_body() {
        return tour_body;
    }

    public void setTour_body(String tour_body) {
        this.tour_body = tour_body;
    }

    public String getTour_registered() {
        return tour_registered;
    }

    public void setTour_registered(String tour_registered) {
        this.tour_registered = tour_registered;
    }

    public String getModerator_name() {
        return moderator_name;
    }

    public void setModerator_name(String moderator_name) {
        this.moderator_name = moderator_name;
    }

    public String getDescription_for_moderator() {
        return description_for_moderator;
    }

    public void setDescription_for_moderator(String description_for_moderator) {
        this.description_for_moderator = description_for_moderator;
    }
}

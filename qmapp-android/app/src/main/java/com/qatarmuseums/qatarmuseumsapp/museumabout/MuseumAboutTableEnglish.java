package com.qatarmuseums.qatarmuseumsapp.museumabout;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Entity(tableName = "museumabouttableenglish")
public class MuseumAboutTableEnglish {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long museum_id;
    @ColumnInfo()
    private String museum_title;
    @ColumnInfo()
    private String museum_subtitle;
    @ColumnInfo()
    private String museum_image;
    @ColumnInfo()
    private String museum_short_description;
    @ColumnInfo()
    private String museum_long_description;
    @ColumnInfo()
    private String museum_opening_time;
    @ColumnInfo()
    private String exhibition_location;
    @ColumnInfo()
    private String museum_contact;
    @ColumnInfo()
    private String museum_filter;

    public MuseumAboutTableEnglish(@NonNull long museum_id, String museum_title,
                                   String museum_subtitle, String museum_image,
                                   String museum_short_description,
                                   String museum_long_description, String museum_opening_time,
                                   String exhibition_location, String museum_contact,
                                   String museum_filter) {
        this.museum_id = museum_id;
        this.museum_title = museum_title;
        this.museum_subtitle = museum_subtitle;
        this.museum_image = museum_image;
        this.museum_short_description = museum_short_description;
        this.museum_long_description = museum_long_description;
        this.museum_opening_time = museum_opening_time;
        this.exhibition_location = exhibition_location;
        this.museum_contact = museum_contact;
        this.museum_filter = museum_filter;
    }

    @NonNull
    public long getMuseum_id() {
        return museum_id;
    }

    public void setMuseum_id(@NonNull long museum_id) {
        this.museum_id = museum_id;
    }

    public String getMuseum_title() {
        return museum_title;
    }

    public void setMuseum_title(String museum_title) {
        this.museum_title = museum_title;
    }

    public String getMuseum_subtitle() {
        return museum_subtitle;
    }

    public void setMuseum_subtitle(String museum_subtitle) {
        this.museum_subtitle = museum_subtitle;
    }

    public String getMuseum_image() {
        return museum_image;
    }

    public void setMuseum_image(String museum_image) {
        this.museum_image = museum_image;
    }

    public String getMuseum_short_description() {
        return museum_short_description;
    }

    public void setMuseum_short_description(String museum_short_description) {
        this.museum_short_description = museum_short_description;
    }

    public String getMuseum_long_description() {
        return museum_long_description;
    }

    public void setMuseum_long_description(String museum_long_description) {
        this.museum_long_description = museum_long_description;
    }

    public String getMuseum_opening_time() {
        return museum_opening_time;
    }

    public void setMuseum_opening_time(String museum_opening_time) {
        this.museum_opening_time = museum_opening_time;
    }

    public String getExhibition_location() {
        return exhibition_location;
    }

    public void setExhibition_location(String exhibition_location) {
        this.exhibition_location = exhibition_location;
    }

    public String getMuseum_contact() {
        return museum_contact;
    }

    public void setMuseum_contact(String museum_contact) {
        this.museum_contact = museum_contact;
    }

    public String getMuseum_filter() {
        return museum_filter;
    }

    public void setMuseum_filter(String museum_filter) {
        this.museum_filter = museum_filter;
    }
}

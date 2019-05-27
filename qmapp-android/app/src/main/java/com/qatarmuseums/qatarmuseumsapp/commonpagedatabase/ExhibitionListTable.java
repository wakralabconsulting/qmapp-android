package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "exhibitionList")
public class ExhibitionListTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long exhibition_id;
    @ColumnInfo()
    private String exhibition_name;
    @ColumnInfo()
    private String museum_id;
    @ColumnInfo()
    private String exhibition_latest_image;
    @ColumnInfo()
    private String exhibition_images;
    @ColumnInfo()
    private String exhibition_start_date;
    @ColumnInfo()
    private String exhibition_end_date;
    @ColumnInfo()
    private String exhibition_display_date;
    @ColumnInfo()
    private String exhibition_location;
    @ColumnInfo()
    private String exhibition_short_description;
    @ColumnInfo()
    private String exhibition_long_description;
    @ColumnInfo()
    private String exhibition_latitude;
    @ColumnInfo()
    private String exhibition_longitude;
    @ColumnInfo()
    private String exhibition_status;


    public ExhibitionListTable(long exhibition_id, String exhibition_name,
                               String exhibition_latest_image, String exhibition_start_date,
                               String exhibition_end_date, String exhibition_location,
                               String exhibition_short_description,
                               String exhibition_long_description,
                               String exhibition_latitude, String exhibition_longitude,
                               String museum_id, String exhibition_status,
                               String exhibition_display_date,
                               String exhibition_images,
                               String language) {
        this.exhibition_id = exhibition_id;
        this.exhibition_name = exhibition_name;
        this.exhibition_latest_image = exhibition_latest_image;
        this.exhibition_start_date = exhibition_start_date;
        this.exhibition_end_date = exhibition_end_date;
        this.exhibition_location = exhibition_location;
        this.exhibition_short_description = exhibition_short_description;
        this.exhibition_long_description = exhibition_long_description;
        this.exhibition_latitude = exhibition_latitude;
        this.exhibition_longitude = exhibition_longitude;
        this.museum_id = museum_id;
        this.exhibition_status = exhibition_status;
        this.exhibition_display_date = exhibition_display_date;
        this.exhibition_images = exhibition_images;
        this.language = language;
    }

    public long getExhibition_id() {
        return exhibition_id;
    }

    public String getExhibition_name() {
        return exhibition_name;
    }

    public String getExhibition_latest_image() {
        return exhibition_latest_image;
    }

    String getExhibition_images() {
        return exhibition_images;
    }

    public String getExhibition_start_date() {
        return exhibition_start_date;
    }

    public String getExhibition_end_date() {
        return exhibition_end_date;
    }

    public String getExhibition_location() {
        return exhibition_location;
    }

    public String getExhibition_short_description() {
        return exhibition_short_description;
    }

    public String getExhibition_long_description() {
        return exhibition_long_description;
    }

    public String getExhibition_latitude() {
        return exhibition_latitude;
    }

    public String getExhibition_longitude() {
        return exhibition_longitude;
    }

    public String getMuseum_id() {
        return museum_id;
    }

    public void setMuseum_id(String museum_id) {
        this.museum_id = museum_id;
    }

    public String getExhibition_status() {
        return exhibition_status;
    }

    public String getExhibition_display_date() {
        return exhibition_display_date;
    }

    public String getLanguage() {
        return language;
    }
}
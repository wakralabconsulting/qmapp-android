package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "heritageList")
public class HeritageListTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long heritage_id;
    @ColumnInfo()
    private String heritage_name;
    @ColumnInfo()
    private String heritage_image;
    @ColumnInfo()
    private String heritage_images;
    @ColumnInfo()
    private String location;
    @ColumnInfo()
    private String latitude;
    @ColumnInfo()
    private String longitude;
    @ColumnInfo()
    private String heritage_short_description;
    @ColumnInfo()
    private String heritage_long_description;
    @ColumnInfo()
    private String heritage_sort_id;

    public HeritageListTable(long heritage_id, String heritage_name, String heritage_image,
                             String location, String latitude, String longitude,
                             String heritage_short_description, String heritage_long_description,
                             String heritage_sort_id, String language) {
        this.heritage_id = heritage_id;
        this.heritage_name = heritage_name;
        this.heritage_image = heritage_image;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heritage_short_description = heritage_short_description;
        this.heritage_long_description = heritage_long_description;
        this.heritage_sort_id = heritage_sort_id;
        this.language = language;
    }

    public long getHeritage_id() {
        return heritage_id;
    }

    public String getHeritage_name() {
        return heritage_name;
    }

    public String getHeritage_image() {
        return heritage_image;
    }

    public String getHeritage_images() {
        return heritage_images;
    }

    void setHeritage_images(String heritage_images) {
        this.heritage_images = heritage_images;
    }

    String getHeritage_sort_id() {
        return heritage_sort_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHeritage_short_description() {
        return heritage_short_description;
    }

    public String getHeritage_long_description() {
        return heritage_long_description;
    }

    public String getLanguage() {
        return language;
    }
}

package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "publicArtsTable")
public class PublicArtsTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long public_arts_id;
    @ColumnInfo()
    private String sort_id;
    @ColumnInfo()
    private String public_arts_name;
    @ColumnInfo()
    private String public_arts_image;
    @ColumnInfo()
    private String public_arts_images;
    @ColumnInfo()
    private String longitude;
    @ColumnInfo()
    private String latitude;
    @ColumnInfo()
    private String description;
    @ColumnInfo()
    private String short_description;


    public PublicArtsTable(long public_arts_id,
                           String public_arts_name,
                           String public_arts_image,
                           String longitude,
                           String latitude,
                           String sort_id, String language) {
        this.public_arts_id = public_arts_id;
        this.public_arts_name = public_arts_name;
        this.public_arts_image = public_arts_image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.sort_id = sort_id;
        this.language = language;
    }

    public long getPublic_arts_id() {
        return public_arts_id;
    }

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    public String getPublic_arts_image() {
        return public_arts_image;
    }

    public String getPublic_arts_images() {
        return public_arts_images;
    }

    public String getPublic_arts_name() {
        return public_arts_name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLanguage() {
        return language;
    }

    public void setPublic_arts_images(String public_arts_images) {
        this.public_arts_images = public_arts_images;
    }
}

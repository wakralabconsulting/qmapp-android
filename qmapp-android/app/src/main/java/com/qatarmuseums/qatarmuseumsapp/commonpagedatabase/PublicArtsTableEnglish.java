package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "publicartstableEnglish")
public class PublicArtsTableEnglish {
    @NonNull
    @PrimaryKey(autoGenerate = false)
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


    public PublicArtsTableEnglish(@NonNull long public_arts_id,
                                  String public_arts_name,
                                  String public_arts_image,
                                  String longitude,
                                  String latitude,
                                  String sortId) {
        this.public_arts_id = public_arts_id;
        this.public_arts_name = public_arts_name;
        this.public_arts_image = public_arts_image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.sort_id = sortId;
    }

    public PublicArtsTableEnglish() {

    }

    @NonNull
    public long getPublic_arts_id() {
        return public_arts_id;
    }

    public void setPublic_arts_id(@NonNull long public_arts_id) {
        this.public_arts_id = public_arts_id;
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

    public void setPublic_arts_image(String public_arts_image) {
        this.public_arts_image = public_arts_image;
    }

    public String getPublic_arts_images() {
        return public_arts_images;
    }

    public void setPublic_arts_images(String public_arts_images) {
        this.public_arts_images = public_arts_images;
    }

    public String getPublic_arts_name() {
        return public_arts_name;
    }

    public void setPublic_arts_name(String public_arts_name) {
        this.public_arts_name = public_arts_name;
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


    @Override
    public String toString() {
        return "PublicArtsTableEnglish{" +
                "name=" + public_arts_name +
                ", qatarmuseum_id='" + public_arts_id
                + '\'' + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PublicArtsTableEnglish)) return false;

        PublicArtsTableEnglish publicArtsTableEnglish = (PublicArtsTableEnglish) obj;

        if (public_arts_id != publicArtsTableEnglish.public_arts_id) return false;
        return public_arts_name != null ? publicArtsTableEnglish.equals(publicArtsTableEnglish.public_arts_name) : publicArtsTableEnglish.public_arts_name == null;
    }
}

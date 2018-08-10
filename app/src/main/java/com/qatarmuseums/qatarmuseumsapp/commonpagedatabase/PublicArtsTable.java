package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;



@Entity(tableName = "publicartstable")
public class PublicArtsTable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long public_arts_id;
    @ColumnInfo()
    private String public_arts_name;
    @ColumnInfo()
    private String public_arts_image;
    @ColumnInfo()
    private String public_arts_arabic_name;
    @ColumnInfo()
    private String longitude;
    @ColumnInfo()
    private String latitude;

    public PublicArtsTable(@NonNull long public_arts_id, String public_arts_name,
                           String public_arts_image, String longitude, String latitude) {
        this.public_arts_id = public_arts_id;
        this.public_arts_name = public_arts_name;
        this.public_arts_image = public_arts_image;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @NonNull
    public long getPublic_arts_id() {
        return public_arts_id;
    }

    public void setPublic_arts_id(@NonNull long public_arts_id) {
        this.public_arts_id = public_arts_id;
    }

    public String getPublic_arts_name() {
        return public_arts_name;
    }

    public void setPublic_arts_name(String public_arts_name) {
        this.public_arts_name = public_arts_name;
    }

    public String getPublic_arts_image() {
        return public_arts_image;
    }

    public void setPublic_arts_image(String public_arts_image) {
        this.public_arts_image = public_arts_image;
    }

    public String getPublic_arts_arabic_name() {
        return public_arts_arabic_name;
    }

    public void setPublic_arts_arabic_name(String public_arts_arabic_name) {
        this.public_arts_arabic_name = public_arts_arabic_name;
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


    @Override
    public String toString() {
        return "PublicArtsTable{" +
                "name=" + public_arts_name +
                ", qatarmuseum_id='" + public_arts_id
                + '\'' + ",image='" + public_arts_image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PublicArtsTable)) return false;

        PublicArtsTable publicArtsTable = (PublicArtsTable) obj;

        if (public_arts_id != publicArtsTable.public_arts_id) return false;
        return public_arts_name != null ? publicArtsTable.equals(publicArtsTable.public_arts_name) : publicArtsTable.public_arts_name == null;
    }
}

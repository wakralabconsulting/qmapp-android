package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Entity(tableName = "homepagetable")
public class HomePageTable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long qMuseumId;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String tourguide_available;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private String arabic_name;

    public String getArabic_name() {
        return arabic_name;
    }

    public void setArabic_name(String arabic_name) {
        this.arabic_name = arabic_name;
    }



    public HomePageTable(long qMuseumId, String name, String tourguide_available, String image) {
        this.qMuseumId = qMuseumId;
        this.name = name;
        this.tourguide_available = tourguide_available;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getqMuseumId() {
        return qMuseumId;
    }

    public void setqMuseumId(long qMuseumId) {
        this.qMuseumId = qMuseumId;
    }

    public String getTourguide_available() {
        return tourguide_available;
    }

    public void setTourguide_available(String tourguide_available) {
        this.tourguide_available = tourguide_available;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HomePageTable)) return false;

        HomePageTable homePageTable = (HomePageTable) obj;

        if (qMuseumId != homePageTable.qMuseumId) return false;
        return name != null ? homePageTable.equals(homePageTable.name) : homePageTable.name == null;

    }



    @Override
    public String toString() {
        return "HomePageTable{" +
                "name=" + name +
                ", qMuseumId='" + qMuseumId + '\'' +
                ", tourguide_available='" + tourguide_available + '\'' +
                ",image='"+image+'\''+
                '}';
    }
}

package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
@Entity(tableName = "homepagetableEnglish")
public class HomePageTableEnglish {
    @NonNull
    public long getQatarmuseum_id() {
        return qatarmuseum_id;
    }

    public void setQatarmuseum_id(@NonNull long qatarmuseum_id) {
        this.qatarmuseum_id = qatarmuseum_id;
    }

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long qatarmuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String tourguide_available;
    @ColumnInfo()
    private String image;


    public HomePageTableEnglish(long qatarmuseum_id, String name, String tourguide_available, String image) {
        this.qatarmuseum_id = qatarmuseum_id;
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
        if (!(obj instanceof HomePageTableEnglish)) return false;

        HomePageTableEnglish homePageTableEnglish = (HomePageTableEnglish) obj;

        if (qatarmuseum_id != homePageTableEnglish.qatarmuseum_id) return false;
        return name != null ? homePageTableEnglish.equals(homePageTableEnglish.name) : homePageTableEnglish.name == null;

    }



    @Override
    public String toString() {
        return "homepagetableEnglish{" +
                "name=" + name +
                ", qatarmuseum_id='" + qatarmuseum_id + '\'' +
                ", tourguide_available='" + tourguide_available + '\'' +
                ",image='"+image+'\''+
                '}';
    }
}

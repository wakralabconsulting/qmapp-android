package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "homepagebannertableArabic")
public class HomePageBannerTableArabic {
    @PrimaryKey()
    private long qatarmuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String image;


    HomePageBannerTableArabic(long qatarmuseum_id, String name, String image) {
        this.qatarmuseum_id = qatarmuseum_id;
        this.name = name;
        this.image = image;
    }

    public long getQatarmuseum_id() {
        return qatarmuseum_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}

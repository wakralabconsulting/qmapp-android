package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "homePageBannerTable")
public class HomePageBannerTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long qatarMuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String image;


    HomePageBannerTable(long qatarMuseum_id, String name, String image, String language) {
        this.qatarMuseum_id = qatarMuseum_id;
        this.name = name;
        this.image = image;
        this.language = language;
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

    long getQatarMuseum_id() {
        return qatarMuseum_id;
    }

    public String getLanguage() {
        return language;
    }
}

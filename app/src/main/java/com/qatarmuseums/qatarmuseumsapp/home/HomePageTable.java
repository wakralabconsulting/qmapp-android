package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "homePageTable")
public class HomePageTable {

    @ColumnInfo()
    private String language;
    @PrimaryKey()
    private long qatarMuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String sortId;
    @ColumnInfo()
    private String tourGuide_available;
    @ColumnInfo()
    private String image;


    public HomePageTable(long qatarMuseum_id, String name, String tourGuide_available, String image,
                         String sortId, String language) {
        this.qatarMuseum_id = qatarMuseum_id;
        this.name = name;
        this.tourGuide_available = tourGuide_available;
        this.image = image;
        this.sortId = sortId;
        this.language = language;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTourGuide_available() {
        return tourGuide_available;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSortId() {
        return sortId;
    }

    public long getQatarMuseum_id() {
        return qatarMuseum_id;
    }

    public String getLanguage() {
        return language;
    }
}

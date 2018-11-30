package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "homepagebannertableEnglish")
public class HomePageBannerTableEnglish {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long qatarmuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String image;


    public HomePageBannerTableEnglish(long qatarmuseum_id, String name, String image) {
        this.qatarmuseum_id = qatarmuseum_id;
        this.name = name;
        this.image = image;
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

    @NonNull
    public long getQatarmuseum_id() {
        return qatarmuseum_id;
    }

    public void setQatarmuseum_id(@NonNull long qatarmuseum_id) {
        this.qatarmuseum_id = qatarmuseum_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HomePageBannerTableEnglish)) return false;

        HomePageBannerTableEnglish homePageTableEnglish = (HomePageBannerTableEnglish) obj;

        if (qatarmuseum_id != homePageTableEnglish.qatarmuseum_id) return false;
        return name != null ? homePageTableEnglish.equals(homePageTableEnglish.name) : homePageTableEnglish.name == null;

    }
}

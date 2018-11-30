package com.qatarmuseums.qatarmuseumsapp.home;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "homepagebannertableArabic")
public class HomePageBannerTableArabic {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long qatarmuseum_id;
    @ColumnInfo()
    private String name;
    @ColumnInfo()
    private String image;


    public HomePageBannerTableArabic(@NonNull long qatarmuseum_id, String name, String image) {
        this.qatarmuseum_id = qatarmuseum_id;
        this.name = name;
        this.image = image;
    }

    @NonNull
    public long getQatarmuseum_id() {
        return qatarmuseum_id;
    }

    public void setQatarmuseum_id(@NonNull long qatarmuseum_id) {
        this.qatarmuseum_id = qatarmuseum_id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HomePageBannerTableArabic)) return false;

        HomePageBannerTableArabic homePageTableArabic = (HomePageBannerTableArabic) obj;

        if (qatarmuseum_id != homePageTableArabic.qatarmuseum_id) return false;
        return name != null ? homePageTableArabic.equals(homePageTableArabic.name) : homePageTableArabic.name == null;

    }

}

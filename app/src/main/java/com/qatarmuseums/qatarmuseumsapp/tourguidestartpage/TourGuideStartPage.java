package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tourGuideStartPage")
public class TourGuideStartPage {

    @ColumnInfo()
    private String language;
    @ColumnInfo()
    private String title;
    @NonNull
    @PrimaryKey()
    private String nid;
    @ColumnInfo()
    private String museum_entity;
    @ColumnInfo()
    private String description;
    @ColumnInfo()
    private String images;

    public TourGuideStartPage(String title, @NonNull String nid, String museum_entity,
                              String description, String images, String language) {
        this.title = title;
        this.nid = nid;
        this.museum_entity = museum_entity;
        this.description = description;
        this.images = images;
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public String getMuseum_entity() {
        return museum_entity;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    public String getNid() {
        return nid;
    }

    public String getImages() {
        return images;
    }

    public String getLanguage() {
        return language;
    }
}

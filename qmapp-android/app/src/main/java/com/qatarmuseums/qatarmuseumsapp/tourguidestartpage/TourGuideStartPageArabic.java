package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tourguidestartpagearabic")
public class TourGuideStartPageArabic {
    @ColumnInfo()
    private String title;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String nid;
    @ColumnInfo()
    private String museum_entity;
    @ColumnInfo()
    private String description;

    public TourGuideStartPageArabic(String title, @NonNull String nid, String museum_entity,
                                    String description) {
        this.title = title;
        this.nid = nid;
        this.museum_entity = museum_entity;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    public String getNid() {
        return nid;
    }

    public String getMuseum_entity() {
        return museum_entity;
    }

    public String getDescription() {
        return description;
    }
}

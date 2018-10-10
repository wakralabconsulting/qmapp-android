package com.qatarmuseums.qatarmuseumsapp.tourguidestartpage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tourguidestartpageenglish")
public class TourGuideStartPageEnglish {
    @ColumnInfo()
    private String title;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String museum_entity;
    @ColumnInfo()
    private String description;

    public TourGuideStartPageEnglish(String title, @NonNull String museum_entity,
                                     String description) {
        this.title = title;
        this.museum_entity = museum_entity;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    public String getMuseum_entity() {
        return museum_entity;
    }

    public String getDescription() {
        return description;
    }
}

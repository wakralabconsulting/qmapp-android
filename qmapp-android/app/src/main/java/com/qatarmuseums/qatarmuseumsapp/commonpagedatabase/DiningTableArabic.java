package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "diningtableArabic")
public class DiningTableArabic {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private long dining_id;
    @ColumnInfo()
    private String dining_name;
    @ColumnInfo()
    private String dining_image;
    @ColumnInfo()
    private String dining_sort_id;

    public DiningTableArabic(@NonNull long dining_id, String dining_name,
                             String dining_image, String dining_sort_id) {
        this.dining_id = dining_id;
        this.dining_name = dining_name;
        this.dining_image = dining_image;
        this.dining_sort_id = dining_sort_id;
    }


    @NonNull
    public long getDining_id() {
        return dining_id;
    }

    public void setDining_id(@NonNull long dining_id) {
        this.dining_id = dining_id;
    }

    public String getDining_name() {
        return dining_name;
    }

    public void setDining_name(String dining_name) {
        this.dining_name = dining_name;
    }

    public String getDining_image() {
        return dining_image;
    }

    public void setDining_image(String dining_image) {
        this.dining_image = dining_image;
    }

    public String getDining_sort_id() {
        return dining_sort_id;
    }

    public void setDining_sort_id(String dining_sort_id) {
        this.dining_sort_id = dining_sort_id;
    }
}


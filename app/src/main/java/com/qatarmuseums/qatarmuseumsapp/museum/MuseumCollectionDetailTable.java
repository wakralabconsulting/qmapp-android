package com.qatarmuseums.qatarmuseumsapp.museum;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "museumCollectionDetailTable")
public class MuseumCollectionDetailTable {
    @ColumnInfo()
    private String language;
    @ColumnInfo()
    private String category_id;
    @NonNull
    @PrimaryKey()
    @ColumnInfo()
    private String detail_title;
    @ColumnInfo()
    private String detail_about;
    @ColumnInfo()
    private String detail_image1;

    public MuseumCollectionDetailTable(String category_id, @NonNull String detail_title,
                                       String detail_image1, String detail_about, String language) {
        this.category_id = category_id;
        this.detail_title = detail_title;
        this.detail_image1 = detail_image1;
        this.detail_about = detail_about;
        this.language = language;
    }

    public String getCategory_id() {
        return category_id;
    }

    @NonNull
    public String getDetail_title() {
        return detail_title;
    }

    public String getDetail_about() {
        return detail_about;
    }

    public String getDetail_image1() {
        return detail_image1;
    }

    public String getLanguage() {
        return language;
    }
}

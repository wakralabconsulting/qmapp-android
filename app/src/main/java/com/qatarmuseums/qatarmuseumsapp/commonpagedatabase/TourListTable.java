package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tourList")
public class TourListTable {

    @ColumnInfo()
    private String language;
    @NonNull
    @PrimaryKey()
    private String tourNid;
    @ColumnInfo()
    private String tourSubtitle;
    @ColumnInfo()
    private String tourDay;
    @ColumnInfo()
    private String tourEventDate;
    @ColumnInfo()
    private String tourImages;
    @ColumnInfo()
    private String tourSortId;
    @ColumnInfo()
    private String tourDescription;
    @ColumnInfo()
    private int isTour;

    public TourListTable(@NonNull String tourNid,
                         String tourDay,
                         String tourEventDate,
                         String tourSubtitle,
                         String tourImages,
                         String tourSortId,
                         String tourDescription,
                         int isTour,
                         String language) {
        this.tourNid = tourNid;
        this.tourDay = tourDay;
        this.tourEventDate = tourEventDate;
        this.tourSubtitle = tourSubtitle;
        this.tourImages = tourImages;
        this.tourSortId = tourSortId;
        this.tourDescription = tourDescription;
        this.isTour = isTour;
        this.language = language;
    }

    @NonNull
    public String getTourNid() {
        return tourNid;
    }

    public String getTourSubtitle() {
        return tourSubtitle;
    }

    public String getTourDay() {
        return tourDay;
    }

    public String getTourEventDate() {
        return tourEventDate;
    }

    public String getTourImages() {
        return tourImages;
    }

    String getTourSortId() {
        return tourSortId;
    }

    String getTourDescription() {
        return tourDescription;
    }

    int isTour() {
        return isTour;
    }

    public String getLanguage() {
        return language;
    }
}

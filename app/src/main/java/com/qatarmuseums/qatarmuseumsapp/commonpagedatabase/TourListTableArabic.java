package com.qatarmuseums.qatarmuseumsapp.commonpagedatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "tourlistarabic")
public class TourListTableArabic {
    @NonNull
    @PrimaryKey(autoGenerate = false)
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


    public TourListTableArabic(@NonNull String tourNid, String tourSubtitle, String tourDay,
                               String tourEventDate, String tourImages,
                               String tourSortId, String tourDescription, int isTour) {
        this.tourNid = tourNid;
        this.tourSubtitle = tourSubtitle;
        this.tourDay = tourDay;
        this.tourEventDate = tourEventDate;
        this.tourImages = tourImages;
        this.tourSortId = tourSortId;
        this.tourDescription = tourDescription;
        this.isTour = isTour;
    }

    @NonNull
    public String getTourNid() {
        return tourNid;
    }

    public void setTourNid(@NonNull String tourNid) {
        this.tourNid = tourNid;
    }

    public String getTourSubtitle() {
        return tourSubtitle;
    }

    public void setTourSubtitle(String tourSubtitle) {
        this.tourSubtitle = tourSubtitle;
    }

    public String getTourDay() {
        return tourDay;
    }

    public void setTourDay(String tourDay) {
        this.tourDay = tourDay;
    }

    public String getTourEventDate() {
        return tourEventDate;
    }

    public void setTourEventDate(String tourEventDate) {
        this.tourEventDate = tourEventDate;
    }

    public String getTourImages() {
        return tourImages;
    }

    public void setTourImages(String tourImages) {
        this.tourImages = tourImages;
    }

    public String getTourSortId() {
        return tourSortId;
    }

    public void setTourSortId(String tourSortId) {
        this.tourSortId = tourSortId;
    }

    public String getTourDescription() {
        return tourDescription;
    }

    public void setTourDescription(String tourDescription) {
        this.tourDescription = tourDescription;
    }

    public int getIsTour() {
        return isTour;
    }

    public void setIsTour(int isTour) {
        this.isTour = isTour;
    }
}

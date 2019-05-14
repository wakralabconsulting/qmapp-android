package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "facilityDetailTable")
public class FacilityDetailTable {

    @ColumnInfo()
    private String language;
    @NonNull
    @PrimaryKey()
    private String facilityNid;
    @ColumnInfo()
    private String sortId;
    @ColumnInfo()
    private String facilityTitle;
    @ColumnInfo()
    private String facilityImage;
    @ColumnInfo()
    private String facilitySubtitle;
    @ColumnInfo()
    private String facilityDescription;
    @ColumnInfo()
    private String facilityTiming;
    @ColumnInfo()
    private String facilityTitleTiming;
    @ColumnInfo()
    private String facilityLongitude;
    @ColumnInfo()
    private String facilityCategoryId;
    @ColumnInfo()
    private String facilityLatitude;
    @ColumnInfo()
    private String facilityLocationTitle;

    public FacilityDetailTable(@NonNull String facilityNid, String sortId, String facilityTitle,
                               String facilityImage, String facilitySubtitle, String facilityDescription,
                               String facilityTiming, String facilityTitleTiming, String facilityLongitude,
                               String facilityCategoryId, String facilityLatitude,
                               String facilityLocationTitle, String language) {
        this.facilityNid = facilityNid;
        this.sortId = sortId;
        this.facilityTitle = facilityTitle;
        this.facilityImage = facilityImage;
        this.facilitySubtitle = facilitySubtitle;
        this.facilityDescription = facilityDescription;
        this.facilityTiming = facilityTiming;
        this.facilityTitleTiming = facilityTitleTiming;
        this.facilityLongitude = facilityLongitude;
        this.facilityCategoryId = facilityCategoryId;
        this.facilityLatitude = facilityLatitude;
        this.facilityLocationTitle = facilityLocationTitle;
        this.language = language;
    }

    @NonNull
    public String getFacilityNid() {
        return facilityNid;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getFacilityTitle() {
        return facilityTitle;
    }

    public String getFacilityImage() {
        return facilityImage;
    }

    public String getFacilitySubtitle() {
        return facilitySubtitle;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public String getFacilityTiming() {
        return facilityTiming;
    }

    public String getFacilityTitleTiming() {
        return facilityTitleTiming;
    }

    public String getFacilityLongitude() {
        return facilityLongitude;
    }

    public String getFacilityCategoryId() {
        return facilityCategoryId;
    }

    public String getFacilityLatitude() {
        return facilityLatitude;
    }

    public String getFacilityLocationTitle() {
        return facilityLocationTitle;
    }

    public String getLanguage() {
        return language;
    }
}

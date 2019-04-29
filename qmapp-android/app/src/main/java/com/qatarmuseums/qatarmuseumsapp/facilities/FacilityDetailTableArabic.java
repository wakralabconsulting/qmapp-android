package com.qatarmuseums.qatarmuseumsapp.facilities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "facilitydetailtablearabic")
public class FacilityDetailTableArabic {

    @NonNull
    @PrimaryKey(autoGenerate = false)
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



    public FacilityDetailTableArabic(@NonNull String facilityNid, String sortId, String facilityTitle,
                                     String facilityImage, String facilitySubtitle, String facilityDescription,
                                     String facilityTiming, String facilityTitleTiming, String facilityLongitude,
                                     String facilityCategoryId, String facilityLatitude, String facilityLocationTitle) {
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
    }

    @NonNull
    public String getFacilityNid() {
        return facilityNid;
    }

    public void setFacilityNid(@NonNull String facilityNid) {
        this.facilityNid = facilityNid;
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

    public void setFacilityTitle(String facilityTitle) {
        this.facilityTitle = facilityTitle;
    }

    public String getFacilityImage() {
        return facilityImage;
    }

    public void setFacilityImage(String facilityImage) {
        this.facilityImage = facilityImage;
    }

    public String getFacilitySubtitle() {
        return facilitySubtitle;
    }

    public void setFacilitySubtitle(String facilitySubtitle) {
        this.facilitySubtitle = facilitySubtitle;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public void setFacilityDescription(String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    public String getFacilityTiming() {
        return facilityTiming;
    }

    public void setFacilityTiming(String facilityTiming) {
        this.facilityTiming = facilityTiming;
    }

    public String getFacilityTitleTiming() {
        return facilityTitleTiming;
    }

    public void setFacilityTitleTiming(String facilityTitleTiming) {
        this.facilityTitleTiming = facilityTitleTiming;
    }

    public String getFacilityLongitude() {
        return facilityLongitude;
    }

    public void setFacilityLongitude(String facilityLongitude) {
        this.facilityLongitude = facilityLongitude;
    }

    public String getFacilityCategoryId() {
        return facilityCategoryId;
    }

    public void setFacilityCategoryId(String facilityCategoryId) {
        this.facilityCategoryId = facilityCategoryId;
    }

    public String getFacilityLatitude() {
        return facilityLatitude;
    }

    public void setFacilityLatitude(String facilityLatitude) {
        this.facilityLatitude = facilityLatitude;
    }

    public String getFacilityLocationTitle() {
        return facilityLocationTitle;
    }

    public void setFacilityLocationTitle(String facilityLocationTitle) {
        this.facilityLocationTitle = facilityLocationTitle;
    }


}

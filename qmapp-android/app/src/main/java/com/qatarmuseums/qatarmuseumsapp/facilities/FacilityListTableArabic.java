package com.qatarmuseums.qatarmuseumsapp.facilities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "facilitylisttablearabic")
public class FacilityListTableArabic {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String facilityNid;
    @ColumnInfo()
    private String sortId;
    @ColumnInfo()
    private String facilityTitle;
    @ColumnInfo()
    private String facilityImage;


    public FacilityListTableArabic(@NonNull String facilityNid, String sortId, String facilityTitle, String facilityImage) {
        this.facilityNid = facilityNid;
        this.sortId = sortId;
        this.facilityTitle = facilityTitle;
        this.facilityImage = facilityImage;
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
}

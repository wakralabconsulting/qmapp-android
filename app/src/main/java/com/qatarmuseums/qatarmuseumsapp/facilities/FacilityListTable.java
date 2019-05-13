package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "facilityListTable")
public class FacilityListTable {

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

    public FacilityListTable(@NonNull String facilityNid, String sortId, String facilityTitle,
                             String facilityImage, String language) {
        this.facilityNid = facilityNid;
        this.sortId = sortId;
        this.facilityTitle = facilityTitle;
        this.facilityImage = facilityImage;
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

    public String getLanguage() {
        return language;
    }
}

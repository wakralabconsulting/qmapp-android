package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkTable")
public class NMoQParkTable {

    @ColumnInfo()
    private String language;
    @NonNull
    @PrimaryKey()
    private String parkNid;
    @ColumnInfo()
    private String parkToolbarTitle;
    @ColumnInfo()
    private String mainDescription;
    @ColumnInfo()
    private String parkTitle;
    @ColumnInfo()
    private String parkTitleDescription;
    @ColumnInfo()
    private String parkHoursTitle;
    @ColumnInfo()
    private String parksHoursDescription;
    @ColumnInfo()
    private String parkLocationTitle;
    @ColumnInfo()
    private String parkLatitude;
    @ColumnInfo()
    private String parkLongitude;

    NMoQParkTable(@NonNull String parkNid, String parkToolbarTitle,
                  String mainDescription, String parkTitle, String parkTitleDescription,
                  String parkHoursTitle, String parksHoursDescription,
                  String parkLocationTitle, String parkLatitude, String parkLongitude,
                  String language) {
        this.parkNid = parkNid;
        this.parkToolbarTitle = parkToolbarTitle;
        this.mainDescription = mainDescription;
        this.parkTitle = parkTitle;
        this.parkTitleDescription = parkTitleDescription;
        this.parkHoursTitle = parkHoursTitle;
        this.parksHoursDescription = parksHoursDescription;
        this.parkLocationTitle = parkLocationTitle;
        this.parkLatitude = parkLatitude;
        this.parkLongitude = parkLongitude;
        this.language = language;
    }

    @NonNull
    public String getParkNid() {
        return parkNid;
    }

    String getParkToolbarTitle() {
        return parkToolbarTitle;
    }

    String getMainDescription() {
        return mainDescription;
    }

    public String getParkTitle() {
        return parkTitle;
    }

    public void setParkTitle(String parkTitle) {
        this.parkTitle = parkTitle;
    }

    String getParkTitleDescription() {
        return parkTitleDescription;
    }

    String getParkHoursTitle() {
        return parkHoursTitle;
    }

    String getParksHoursDescription() {
        return parksHoursDescription;
    }

    String getParkLocationTitle() {
        return parkLocationTitle;
    }

    String getParkLatitude() {
        return parkLatitude;
    }

    String getParkLongitude() {
        return parkLongitude;
    }

    public String getLanguage() {
        return language;
    }
}

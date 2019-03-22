package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkTableEnglish")
public class NMoQParkTableEnglish {

    @NonNull
    @PrimaryKey(autoGenerate = false)
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

    public NMoQParkTableEnglish(@NonNull String parkNid, String parkToolbarTitle,
                                String mainDescription, String parkTitle, String parkTitleDescription,
                                String parkHoursTitle, String parksHoursDescription,
                                String parkLocationTitle, String parkLatitude, String parkLongitude) {
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
    }

    @NonNull
    public String getParkNid() {
        return parkNid;
    }

    public void setParkNid(@NonNull String parkNid) {
        this.parkNid = parkNid;
    }

    public String getParkToolbarTitle() {
        return parkToolbarTitle;
    }

    public void setParkToolbarTitle(String parkToolbarTitle) {
        this.parkToolbarTitle = parkToolbarTitle;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    public String getParkTitle() {
        return parkTitle;
    }

    public void setParkTitle(String parkTitle) {
        this.parkTitle = parkTitle;
    }

    public String getParkTitleDescription() {
        return parkTitleDescription;
    }

    public void setParkTitleDescription(String parkTitleDescription) {
        this.parkTitleDescription = parkTitleDescription;
    }

    public String getParkHoursTitle() {
        return parkHoursTitle;
    }

    public void setParkHoursTitle(String parkHoursTitle) {
        this.parkHoursTitle = parkHoursTitle;
    }

    public String getParksHoursDescription() {
        return parksHoursDescription;
    }

    public void setParksHoursDescription(String parksHoursDescription) {
        this.parksHoursDescription = parksHoursDescription;
    }

    public String getParkLocationTitle() {
        return parkLocationTitle;
    }

    public void setParkLocationTitle(String parkLocationTitle) {
        this.parkLocationTitle = parkLocationTitle;
    }

    public String getParkLatitude() {
        return parkLatitude;
    }

    public void setParkLatitude(String parkLatitude) {
        this.parkLatitude = parkLatitude;
    }

    public String getParkLongitude() {
        return parkLongitude;
    }

    public void setParkLongitude(String parkLongitude) {
        this.parkLongitude = parkLongitude;
    }
}

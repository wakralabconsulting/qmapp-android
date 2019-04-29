package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkListDetailsTableArabic")
public class NMoQParkListDetailsTableArabic {

    @PrimaryKey(autoGenerate = true)
    private Integer parkNumber;
    @ColumnInfo
    private String parkNid;
    @ColumnInfo()
    private String parkTitle;
    @ColumnInfo()
    private String parkSortId;
    @ColumnInfo()
    private String parkImages;
    @ColumnInfo()
    private String parkDescription;

    public NMoQParkListDetailsTableArabic(@NonNull String parkNid, String parkTitle, String parkSortId, String parkImages, String parkDescription) {
        this.parkNid = parkNid;
        this.parkTitle = parkTitle;
        this.parkSortId = parkSortId;
        this.parkImages = parkImages;
        this.parkDescription = parkDescription;
    }

    @NonNull
    public String getParkNid() {
        return parkNid;
    }

    public void setParkNid(@NonNull String parkNid) {
        this.parkNid = parkNid;
    }

    public String getParkTitle() {
        return parkTitle;
    }

    public void setParkTitle(String parkTitle) {
        this.parkTitle = parkTitle;
    }

    public String getParkSortId() {
        return parkSortId;
    }

    public void setParkSortId(String parkSortId) {
        this.parkSortId = parkSortId;
    }

    public String getParkImages() {
        return parkImages;
    }

    public void setParkImages(String parkImages) {
        this.parkImages = parkImages;
    }

    public String getParkDescription() {
        return parkDescription;
    }

    public void setParkDescription(String parkDescription) {
        this.parkDescription = parkDescription;
    }

    public Integer getParkNumber() {
        return parkNumber;
    }

    public void setParkNumber(Integer parkNumber) {
        this.parkNumber = parkNumber;
    }
}

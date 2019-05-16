package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkListDetailsTable")
public class NMoQParkListDetailsTable {

    @ColumnInfo
    private String language;
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

    public NMoQParkListDetailsTable(@NonNull String parkNid, String parkTitle, String parkSortId,
                                    String parkImages, String parkDescription, String language) {
        this.parkNid = parkNid;
        this.parkTitle = parkTitle;
        this.parkSortId = parkSortId;
        this.parkImages = parkImages;
        this.parkDescription = parkDescription;
        this.language = language;
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

    public String getParkImages() {
        return parkImages;
    }

    public String getParkDescription() {
        return parkDescription;
    }

    Integer getParkNumber() {
        return parkNumber;
    }

    void setParkNumber(Integer parkNumber) {
        this.parkNumber = parkNumber;
    }

    public String getLanguage() {
        return language;
    }
}

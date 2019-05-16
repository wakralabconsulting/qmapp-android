package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkListTable")
public class NMoQParkListTable {

    @ColumnInfo()
    private String language;
    @NonNull
    @PrimaryKey()
    private String parkNid;
    @ColumnInfo()
    private String parkTitle;
    @ColumnInfo()
    private String parkSortId;
    @ColumnInfo()
    private String parkImages;

    NMoQParkListTable(@NonNull String parkNid, String parkTitle, String parkSortId,
                      String parkImages, String language) {
        this.parkNid = parkNid;
        this.parkTitle = parkTitle;
        this.parkSortId = parkSortId;
        this.parkImages = parkImages;
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

    String getParkSortId() {
        return parkSortId;
    }

    String getParkImages() {
        return parkImages;
    }

    public String getLanguage() {
        return language;
    }
}

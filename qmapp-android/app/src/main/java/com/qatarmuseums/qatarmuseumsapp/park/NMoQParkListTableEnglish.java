package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "nMoQParkListTableEnglish")
public class NMoQParkListTableEnglish {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String parkNid;
    @ColumnInfo()
    private String parkTitle;
    @ColumnInfo()
    private String parkSortId;
    @ColumnInfo()
    private String parkImages;

    public NMoQParkListTableEnglish(@NonNull String parkNid, String parkTitle, String parkSortId,
                                    String parkImages) {
        this.parkNid = parkNid;
        this.parkTitle = parkTitle;
        this.parkSortId = parkSortId;
        this.parkImages = parkImages;
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
}

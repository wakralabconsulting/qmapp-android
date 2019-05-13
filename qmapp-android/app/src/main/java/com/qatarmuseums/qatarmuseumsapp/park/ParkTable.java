package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "parkTable")
public class ParkTable {
    @ColumnInfo()
    private String language;
    @PrimaryKey(autoGenerate = true)
    private long itemId;
    @ColumnInfo()
    private String mainTitle;
    @ColumnInfo()
    private String shortDescription;
    @ColumnInfo()
    private String image;
    @ColumnInfo()
    private long sortId;
    @ColumnInfo()
    private String latitude;
    @ColumnInfo()
    private String longitude;
    @ColumnInfo()
    private String timingInfo;

    ParkTable(String mainTitle, String shortDescription,
              String image, long sortId,
              String latitude, String longitude, String timingInfo, String language) {
        this.mainTitle = mainTitle;
        this.shortDescription = shortDescription;
        this.image = image;
        this.sortId = sortId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timingInfo = timingInfo;
        this.language = language;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getSortId() {
        return sortId;
    }

    public void setSortId(long sortId) {
        this.sortId = sortId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String getTimingInfo() {
        return timingInfo;
    }

    long getItemId() {
        return itemId;
    }

    void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getLanguage() {
        return language;
    }
}

package com.qatarmuseums.qatarmuseumsapp.park;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "parktableEnglish")
public class ParkTableEnglish {
    @NonNull
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

    public ParkTableEnglish(String mainTitle, String shortDescription,
                            String image, @NonNull long sortId,
                            String latitude, String longitude, String timingInfo) {
        this.mainTitle = mainTitle;
        this.shortDescription = shortDescription;
        this.image = image;
        this.sortId = sortId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timingInfo = timingInfo;
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

    @NonNull
    public long getSortId() {
        return sortId;
    }

    public void setSortId(@NonNull long sortId) {
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

    public String getTimingInfo() {
        return timingInfo;
    }

    public void setTimingInfo(String timingInfo) {
        this.timingInfo = timingInfo;
    }

    @NonNull
    public long getItemId() {
        return itemId;
    }

    public void setItemId(@NonNull long itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ParkTableEnglish)) return false;

        ParkTableEnglish parkTableEnglish = (ParkTableEnglish) obj;

        if (sortId != parkTableEnglish.sortId) return false;
        return mainTitle != null ? parkTableEnglish.equals(parkTableEnglish.mainTitle) : parkTableEnglish.mainTitle == null;

    }

    @Override
    public String toString() {
        return "parktableEnglish{" +
                "mainTitle=" + mainTitle +
                ", shortDescription='" + shortDescription + '\'' +
                ", image='" + image + '\'' +
                ",sortId='" + sortId + '\'' +
                ",latitude='" + latitude + '\'' +
                ",longitude='" + longitude + '\'' +
                ",timingInfo='" + timingInfo + '\'' +
                '}';
    }
}

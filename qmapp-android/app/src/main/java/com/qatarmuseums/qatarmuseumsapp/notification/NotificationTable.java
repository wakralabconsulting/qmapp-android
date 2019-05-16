package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "notificationTable")
public class NotificationTable {

    @PrimaryKey(autoGenerate = true)
    private long number;
    @ColumnInfo()
    private String language;
    @ColumnInfo()
    private String title;


    public NotificationTable(String title, String language) {
        this.title = title;
        this.language = language;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "notificationTableEnglish")
public class NotificationTableEnglish {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long number;
    @ColumnInfo()
    private String title;


    public NotificationTableEnglish(String title) {
        this.title = title;
    }

    @NonNull
    public long getNumber() {
        return number;
    }

    public void setNumber(@NonNull long number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

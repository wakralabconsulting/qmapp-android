package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "userRegistrationTable")
public class UserRegistrationDetailsTable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String registrationID;
    @ColumnInfo()
    private String eventId;
    @ColumnInfo()
    private String eventTitle;


    public UserRegistrationDetailsTable(@NonNull String registrationID, String eventId, String eventTitle) {
        this.registrationID = registrationID;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
    }

    @NonNull
    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(@NonNull String registrationID) {
        this.registrationID = registrationID;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}

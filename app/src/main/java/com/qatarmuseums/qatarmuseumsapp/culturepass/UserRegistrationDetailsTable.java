package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "userRegistrationTable")
public class UserRegistrationDetailsTable {
    @NonNull
    @PrimaryKey()
    private String registrationID;
    @ColumnInfo()
    private String eventId;
    @ColumnInfo()
    private String numberOfReservations;
    @ColumnInfo()
    private String eventTitle;


    public UserRegistrationDetailsTable(@NonNull String registrationID, String eventId,
                                        String eventTitle, String numberOfReservations) {
        this.registrationID = registrationID;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.numberOfReservations = numberOfReservations;
    }

    @NonNull
    String getRegistrationID() {
        return registrationID;
    }

    public String getEventId() {
        return eventId;
    }

    String getEventTitle() {
        return eventTitle;
    }

    public String getNumberOfReservations() {
        return numberOfReservations;
    }
}

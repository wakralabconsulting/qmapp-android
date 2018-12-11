package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

public class Und {
    @SerializedName(value = "value", alternate = {"iso2"})
    private String value;
    @SerializedName("value2")
    private String value2;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("offset")
    private String offset;
    @SerializedName("offset2")
    private String offset2;
    @SerializedName("timezone_db")
    private String timezoneDB;
    @SerializedName("date_type")
    private String dateType;

    public Und(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOffset2() {
        return offset2;
    }

    public void setOffset2(String offset2) {
        this.offset2 = offset2;
    }

    public String getTimezoneDB() {
        return timezoneDB;
    }

    public void setTimezoneDB(String timezoneDB) {
        this.timezoneDB = timezoneDB;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }
}

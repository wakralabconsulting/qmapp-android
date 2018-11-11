package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

public class Und {
    @SerializedName(value = "value", alternate = {"iso2"})
    private String value;

    public Und(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

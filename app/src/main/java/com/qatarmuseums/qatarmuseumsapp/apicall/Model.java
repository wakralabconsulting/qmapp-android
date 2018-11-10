package com.qatarmuseums.qatarmuseumsapp.apicall;

import com.google.gson.annotations.SerializedName;
import com.qatarmuseums.qatarmuseumsapp.profile.UserData;

import java.util.List;

public class Model extends UserData {
    @SerializedName("und")
    private List<UserData.Und> und;

    public List<UserData.Und> getUnd() {
        return und;
    }

    public void setUnd(List<UserData.Und> und) {
        this.und = und;
    }
}


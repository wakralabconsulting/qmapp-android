package com.qatarmuseums.qatarmuseumsapp.profile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model extends UserData {
    @SerializedName("und")
    private List<Und> und;

    public Model(List<Und> und) {
        this.und = und;
    }

    public List<Und> getUnd() {
        return und;
    }

}


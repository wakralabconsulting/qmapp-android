package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;

/**
 * Created by Exalture on 26-07-2018.
 */

public class MuseumHScrollModel {
    Context context;
    String textName;
    int resId;


    public Context getContext() {
        return context;
    }

    public String getTextName() {
        return textName;
    }

    public int getResId() {
        return resId;
    }

    public MuseumHScrollModel(Context context, String textName, int resId) {
        this.context = context;
        this.textName =textName;
        this.resId =resId;


    }

    public MuseumHScrollModel() {

    }

}

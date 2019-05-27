package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;

public class MuseumHScrollModel {
    private Context context;
    private String textName;
    private int resId;


    public Context getContext() {
        return context;
    }

    String getTextName() {
        return textName;
    }

    int getResId() {
        return resId;
    }

    MuseumHScrollModel(Context context, String textName, int resId) {
        this.context = context;
        this.textName = textName;
        this.resId = resId;
    }

}

package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;

public class MuseumHScrollModel {
    private Context context;
    private String textName;
    private int resId;


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
        this.textName = textName;
        this.resId = resId;
    }

}

package com.qatarmuseums.qatarmuseumsapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by Exalture on 23-07-2018.
 */

public class MyApp extends Application {
    Locale myLocale;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (appLanguage == 2) {
            Configuration configuration = getResources().getConfiguration();
            configuration.setLayoutDirection(new Locale("ar"));
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            myLocale = new Locale("ar");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        } else {
                // no need to do anything app open in english
        }


    }



}

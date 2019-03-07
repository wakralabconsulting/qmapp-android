package com.qatarmuseums.qatarmuseumsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

import static android.os.Build.VERSION_CODES.N;

public class LocaleManager {

    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_ARABIC = "ar";
    private static final String LANGUAGE_KEY = "AppLanguage";

    public static Context setLocale(Context c) {
        return updateResources(c, getLanguage(c));
    }

    public static Context setNewLocale(Context c, String mLanguage) {
        String language = mLanguage;
        if (language.equals("ur") || language.equals("ar"))
            language = LANGUAGE_ARABIC;
        else
            language = LANGUAGE_ENGLISH;
        persistLanguage(language, c);
        return updateResources(c, language);
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String language;
        if (Locale.getDefault().getLanguage().equals("ur") ||
                Locale.getDefault().getLanguage().equals("ar"))
            language = LANGUAGE_ARABIC;
        else
            language = LANGUAGE_ENGLISH;
        return prefs.getString(LANGUAGE_KEY, language);
    }

    @SuppressLint("ApplySharedPref")
    private static void persistLanguage(String language, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(LANGUAGE_KEY, language).commit();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLayoutDirection(locale);
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }

    public static Locale getLocale(Resources res) {
        Configuration config = res.getConfiguration();
        return Build.VERSION.SDK_INT >= N ? config.getLocales().get(0) : config.locale;
    }
}

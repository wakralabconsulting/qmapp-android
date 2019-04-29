package com.qatarmuseums.qatarmuseumsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.qatarmuseums.qatarmuseumsapp.timber.FileLoggingTree;

import timber.log.Timber;


public class MyApp extends Application {
    private final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        // For log integration
        Timber.plant(new FileLoggingTree(getApplicationContext()));
        Timber.i("Initializing Logger...");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        LeakCanary.enableDisplayLeakActivity(getApplicationContext());
        createNotificationChannel();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
        Timber.d("attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setNewLocale(this, newConfig.locale.getLanguage());
        Timber.d("onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Timber.i("Creating NotificationChannel for Oreo and above");
            CharSequence name = getString(R.string.notification_channel);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(Config.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

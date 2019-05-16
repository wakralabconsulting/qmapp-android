package com.qatarmuseums.qatarmuseumsapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.qatarmuseums.qatarmuseumsapp.timber.FileLoggingTree;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;


public class MyApp extends Application {
    private final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        configureCrashReporting();
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

    private void configureCrashReporting() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit);
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

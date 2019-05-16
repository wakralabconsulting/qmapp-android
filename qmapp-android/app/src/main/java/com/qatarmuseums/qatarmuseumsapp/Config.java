package com.qatarmuseums.qatarmuseumsapp;

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final String CHANNEL_ID = "Notification";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String ZIP_FILE_NAME = "log_latest.zip";
    public static final String ZIP_FILE_PASSWORD = "12";
    public static final String EMAIL_ID_TO_SEND_LOGS = "testingfourexalture@gmail.com";
    public static final String EMAIL_SUBJECT = "Please describe your problem";
    public static final String EMAIL_MESSAGE = "What are you doing?" +
            "\n1.\n2.\n3.\n\n\n" +
            "Version information:\nCulture Pass v" + BuildConfig.VERSION_NAME + " (build " +
            BuildConfig.VERSION_CODE + ")";
}

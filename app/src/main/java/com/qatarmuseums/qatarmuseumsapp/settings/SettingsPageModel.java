package com.qatarmuseums.qatarmuseumsapp.settings;

/**
 * Created by Exalture on 23-07-2018.
 */

public class SettingsPageModel {

    private String notificationItemName;
    private boolean notificationItemStatus;

    public SettingsPageModel() {
    }

    public SettingsPageModel(String notificationItemName, boolean notificationItemStatus) {
        this.notificationItemName = notificationItemName;
        this.notificationItemStatus = notificationItemStatus;
    }

    public String getNotificationItemName() {
        return notificationItemName;
    }

    public boolean isNotificationItemStatus() {
        return notificationItemStatus;
    }
}

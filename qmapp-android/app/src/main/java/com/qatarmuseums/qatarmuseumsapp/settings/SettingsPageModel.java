package com.qatarmuseums.qatarmuseumsapp.settings;


class SettingsPageModel {

    private String notificationItemName;

    SettingsPageModel(String notificationItemName) {
        this.notificationItemName = notificationItemName;
    }

    String getNotificationItemName() {
        return notificationItemName;
    }

}

package com.qatarmuseums.qatarmuseumsapp.notification;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final NotificationTableDao notificationDao;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationDao = QMDatabase.getInstance(application).getNotificationDao();
        executorService = Executors.newSingleThreadExecutor();

    }

    LiveData<List<NotificationTableEnglish>> getAllPostsEnglish() {
        return notificationDao.findAllEnglish();
    }

    LiveData<List<NotificationTableArabic>> getAllPostsArabic() {
        return notificationDao.findAllArabic();
    }

    void saveEnglishNotification(NotificationTableEnglish notificationTableEnglish) {
        executorService.execute(() -> notificationDao.saveEnglish(notificationTableEnglish));
    }

    void saveArabicNotification(NotificationTableArabic notificationTableArabic) {
        executorService.execute(() -> notificationDao.saveEnglish(notificationTableArabic));
    }
}

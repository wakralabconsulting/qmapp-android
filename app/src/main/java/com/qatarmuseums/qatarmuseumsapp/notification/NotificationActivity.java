package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<NotificationModel> models = new ArrayList<>();
    private NotificationListAdapter mAdapter;
    private View backArrow;
    private Animation zoomOutAnimation;
    private TextView emptyText;
    private QMDatabase qmDatabase;
    private String appLanguage;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction);
        Toolbar toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back);
        recyclerView = findViewById(R.id.notification_recycler_view);
        emptyText = findViewById(R.id.no_new_notification_txt);
        mAdapter = new NotificationListAdapter(this, models);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        backArrow.setOnClickListener(v -> onBackPressed());
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        qmDatabase = QMDatabase.getInstance(NotificationActivity.this);
        appLanguage = LocaleManager.getLanguage(this);

        NotificationViewModel notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH))
            notificationViewModel.getAllPostsEnglish().observe(this, models -> mAdapter.setData(models));
        else
            notificationViewModel.getAllPostsArabic().observe(this, models -> mAdapter.setData(models));
        getDataFromDataBase();

    }

    public void getDataFromDataBase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH))
            new RetrieveEnglishTableData(NotificationActivity.this).execute();
        else
            new RetrieveArabicTableData(NotificationActivity.this).execute();
    }

    public static class RetrieveEnglishTableData extends AsyncTask<Void, Void, List<NotificationTableEnglish>> {
        private WeakReference<NotificationActivity> activityReference;

        RetrieveEnglishTableData(NotificationActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<NotificationTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNotificationDao().getAllDataFromEnglishTable();

        }

        @Override
        protected void onPostExecute(List<NotificationTableEnglish> notificationTableEnglishes) {
            if (notificationTableEnglishes.size() > 0) {
                activityReference.get().models.clear();
                for (int i = 0; i < notificationTableEnglishes.size(); i++) {
                    NotificationModel notificationModel = new NotificationModel(notificationTableEnglishes.get(i).getTitle());
                    activityReference.get().models.add(i, notificationModel);
                }

                Collections.reverse(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
            } else {
                activityReference.get().emptyText.setVisibility(View.VISIBLE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public static class RetrieveArabicTableData extends AsyncTask<Void, Void,
            List<NotificationTableArabic>> {
        private WeakReference<NotificationActivity> activityReference;

        RetrieveArabicTableData(NotificationActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected List<NotificationTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getNotificationDao().getAllDataFromArabicTable();

        }

        @Override
        protected void onPostExecute(List<NotificationTableArabic> notificationTableArabics) {
            if (notificationTableArabics.size() > 0) {
                activityReference.get().models.clear();
                for (int i = 0; i < notificationTableArabics.size(); i++) {
                    NotificationModel notificationModel = new NotificationModel(notificationTableArabics.get(i).getTitle());
                    activityReference.get().models.add(i, notificationModel);
                }

                Collections.reverse(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
            } else {
                activityReference.get().emptyText.setVisibility(View.VISIBLE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
            }
        }

    }

}

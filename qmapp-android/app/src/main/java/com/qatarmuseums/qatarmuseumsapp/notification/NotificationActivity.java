package com.qatarmuseums.qatarmuseumsapp.notification;

import android.annotation.SuppressLint;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<NotificationModel> models = new ArrayList<>();
    private NotificationListAdapter mAdapter;
    private View backArrow;
    private Animation zoomOutAnimation;
    private TextView emptyText;
    private QMDatabase qmDatabase;
    private String appLanguage;
    private FirebaseAnalytics mFireBaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction);
        Toolbar toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back);

        recyclerView = findViewById(R.id.notification_recycler_view);
        emptyText = findViewById(R.id.no_new_notification_txt);
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAdapter = new NotificationListAdapter(this, models);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        backArrow.setOnClickListener(v -> {
            Timber.i("Back arrow clicked");
            onBackPressed();
        });
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
        notificationViewModel.getAllPosts(appLanguage).observe(this, models -> mAdapter.setData(models));
        getDataFromDataBase();
    }

    public void getDataFromDataBase() {
        Timber.i("getDataFromDataBase(language :%s)", appLanguage);
        new RetrieveTableData(NotificationActivity.this).execute();
    }

    public static class RetrieveTableData extends AsyncTask<Void, Void, List<NotificationTable>> {
        private WeakReference<NotificationActivity> activityReference;

        RetrieveTableData(NotificationActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<NotificationTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromTable(language :%s)", activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getNotificationDao()
                    .getAllDataFromTable(activityReference.get().appLanguage);

        }

        @Override
        protected void onPostExecute(List<NotificationTable> notificationTables) {
            if (notificationTables.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        notificationTables.size());
                activityReference.get().models.clear();
                for (int i = 0; i < notificationTables.size(); i++) {
                    Timber.i("Setting list from database for title: %s",
                            notificationTables.get(i).getTitle());
                    NotificationModel notificationModel = new NotificationModel(notificationTables.get(i).getTitle());
                    activityReference.get().models.add(i, notificationModel);
                }

                Collections.reverse(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().emptyText.setVisibility(View.VISIBLE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAnalytics.setCurrentScreen(this, getString(R.string.notification_page), null);
    }
}

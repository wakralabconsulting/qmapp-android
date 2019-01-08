package com.qatarmuseums.qatarmuseumsapp.notification;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<NotificationModel> models = new ArrayList<>();
    private NotificationListAdapter mAdapter;
    private View backArrow;
    private Animation zoomOutAnimation;
    private TextView emptyText;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private NotificationViewModel notificationViewModel;
    private QMDatabase qmDatabase;
    private SharedPreferences qmPreferences;
    private int appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction);
        toolbar = (Toolbar) findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        backArrow = findViewById(R.id.toolbar_back);
        recyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);
        emptyText = (TextView) findViewById(R.id.no_new_notification_txt);
        mAdapter = new NotificationListAdapter(this, models);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        qmDatabase = QMDatabase.getInstance(NotificationActivity.this);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);

        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        if (appLanguage == 1)
            notificationViewModel.getAllPostsEnglish().observe(this, models -> mAdapter.setData(models));
        else
            notificationViewModel.getAllPostsArabic().observe(this, models -> mAdapter.setData(models));
        getDataFromDataBase(appLanguage);

    }

    public void getDataFromDataBase(int language) {
        if (language == 1)
            new RetriveEnglishTableData(NotificationActivity.this, language).execute();
        else
            new RetriveArabicTableData(NotificationActivity.this, language).execute();
    }

    public static class RetriveEnglishTableData extends AsyncTask<Void, Void, List<NotificationTableEnglish>> {
        private WeakReference<NotificationActivity> activityReference;
        int language;

        RetriveEnglishTableData(NotificationActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
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

    public static class RetriveArabicTableData extends AsyncTask<Void, Void,
            List<NotificationTableArabic>> {
        private WeakReference<NotificationActivity> activityReference;
        int language;

        RetriveArabicTableData(NotificationActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
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
//                progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
//                retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().emptyText.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
//                retryLayout.setVisibility(View.VISIBLE);
            }
        }

    }

}

package com.qatarmuseums.qatarmuseumsapp.education;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationCalendarActivity extends AppCompatActivity {

    CollapsibleCalendar collapsibleCalendar;
    RecyclerView eventListView;
    EducationAdapter educationAdapter;

    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    ImageView backArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.toolbar_filter)
    ImageView toolbar_filter;
    @BindView(R.id.layout_container)
    RelativeLayout layoutContainer;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBar;
    @BindView(R.id.noResultFoundTxt)
    TextView noResultFoundTxt;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<EducationEvents> educationEvents = new ArrayList<>();
    Intent intent;
    String institutionFilter, ageGroupFilter, programmeTypeFilter;
    long selectedDate, todayDate;
    boolean isDateSelected = false;
    Util util;
    Calendar calendarInstance;
    ContentResolver contentResolver;
    int MY_PERMISSIONS_REQUEST_CALENDAR = 100;
    int REQUEST_PERMISSION_SETTING = 110;
    ContentValues cv;
    private Dialog dialog;
    private QMDatabase qmDatabase;
    private Integer eventsTableRowCount;
    EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish;
    EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic;
    private String language;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    private Calendar today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.education_calendar_activity_tittle));
        collapsibleCalendar = (CollapsibleCalendar) findViewById(R.id.collapsibleCalendarView);
        eventListView = (RecyclerView) findViewById(R.id.event_list);
        util = new Util();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);

        qmDatabase = QMDatabase.getInstance(EducationCalendarActivity.this);
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

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EducationCalendarActivity.this, EducationFilterActivity.class);
                startActivity(intent);
            }
        });

        if (institutionFilter == null) {
            institutionFilter = "any";
        }
        if (ageGroupFilter == null) {
            ageGroupFilter = "any";
        }
        if (programmeTypeFilter == null) {
            programmeTypeFilter = "any";
        }
        educationAdapter = new EducationAdapter(EducationCalendarActivity.this, educationEvents);
        layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(educationAdapter);

        today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 14);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
            if (isDateSelected) {
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getEducationCalendarDataFromApi(selectedDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
            } else {
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getEducationCalendarDataFromApi(today.getTimeInMillis(), institutionFilter, ageGroupFilter, programmeTypeFilter);
                isDateSelected = false;
            }
        } else {
            if (isDateSelected) {
                getEducationCalendarEventsFromDatabase(selectedDate);
            } else {
                getEducationCalendarEventsFromDatabase(today.getTimeInMillis());
            }
        }


        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                calendarInstance = Calendar.getInstance();
                calendarInstance.set(collapsibleCalendar.getSelectedItem().getYear(),
                        collapsibleCalendar.getSelectedItem().getMonth(),
                        collapsibleCalendar.getSelectedItem().getDay());
                calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
                calendarInstance.set(Calendar.MINUTE, 0);
                calendarInstance.set(Calendar.SECOND, 0);
                isDateSelected = true;
                selectedDate = calendarInstance.getTimeInMillis();
                if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
                    getEducationCalendarDataFromApi(calendarInstance.getTimeInMillis(), institutionFilter, ageGroupFilter, programmeTypeFilter);
                } else {
                    getEducationCalendarEventsFromDatabase(calendarInstance.getTimeInMillis());
                }
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemClick(View v) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int position) {

            }
        });

        eventListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    if (collapsibleCalendar.expanded) {
                        collapsibleCalendar.collapse(400);
                    }
                } else {
                    // Scrolling down
                    collapsibleCalendar.expand(400);
                    collapsibleCalendar.expanded = true;
                }
            }
        });

    }

    private void getEducationCalendarEventsFromDatabase(long timeStamp) {
        progressBar.setVisibility(View.VISIBLE);
        educationAdapter.clear();
        String language;
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        new EducationCalendarActivity.EventsRowCount(EducationCalendarActivity.this, language, timeStamp / 1000).execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        institutionFilter = intent.getStringExtra("INSTITUTION");
        ageGroupFilter = intent.getStringExtra("AGE_GROUP");
        programmeTypeFilter = intent.getStringExtra("PROGRAMME_TYPE");
        if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
            if (isDateSelected) {
                getEducationCalendarDataFromApi(selectedDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                getEducationCalendarDataFromApi(today.getTimeInMillis(), institutionFilter, ageGroupFilter, programmeTypeFilter);
                isDateSelected = false;
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (isDateSelected) {
                getEducationCalendarEventsFromDatabase(selectedDate);
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                getEducationCalendarEventsFromDatabase(today.getTimeInMillis());
                isDateSelected = false;
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

    }

    private void getEducationCalendarDataFromApi(long date, String institute, String ageGroup, String programmeType) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getTempClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        Call<ArrayList<EducationEvents>> call = apiService.
                getEducationCalendarDetails(language, date / 1000, institute, ageGroup, programmeType);

        call.enqueue(new Callback<ArrayList<EducationEvents>>() {
            @Override
            public void onResponse(Call<ArrayList<EducationEvents>> call, Response<ArrayList<EducationEvents>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        educationEvents.clear();
                        progressBar.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                        educationEvents.addAll(response.body());
                        boolean checkResult = checkWednesdayorSunday();
                        if (!checkResult) {
                            educationEvents.add(loadLocalEvent(calendarInstance.getTimeInMillis() / 1000));
                        }
                        updateTimeStamp();
                        sortEventsWithStartTime();
                        educationAdapter.notifyDataSetChanged();
                        new EventsRowCount(EducationCalendarActivity.this, language, calendarInstance.getTimeInMillis()).execute();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EducationEvents>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                eventListView.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private EducationEvents loadLocalEvent(long date) {
        EducationEvents events = new EducationEvents("15476", null,
                "Walk in Gallery Tours",
                "Join our Museum Guides for a tour of the Museum of Islamic Art's oustanding collection of objects, spread over 1,400 years and across three continents. No booking is required to be a part of the tour.,",
                "Monday - Science Tour\n" +
                        "\n" +
                        "Tuesday - Techniques Tour (from 1 July onwards)\n" +
                        "\n" +
                        "Thursday - MIA Architecture Tour\n" +
                        "\n" +
                        "Friday - Permanent Gallery Tour\n" +
                        "\n" +
                        "Saturday - Permanent Gallery Tour,",
                "Museum of Islamic Art,Artiium",
                "MIA", "14:00", "16:00",
                "40", "Adults", "Gallery",
                "MIA", "false", String.valueOf(date)
        );
        return events;
    }

    public class EventsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<EducationCalendarActivity> activityReference;
        String language;
        long timeStamp;

        EventsRowCount(EducationCalendarActivity context, String apiLanguage, long timeStamp) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.timeStamp = timeStamp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            eventsTableRowCount = integer;
            if (eventsTableRowCount > 0) {
                if (educationEvents.size() > 0 && util.isNetworkAvailable(EducationCalendarActivity.this))
                    new CheckEventRowExist(EducationCalendarActivity.this, language).execute();
                else {
                    if (language.equals("en")) {
                        new RetriveEnglishTableData(EducationCalendarActivity.this, 1,
                                timeStamp).execute();
                    } else {
                        new RetriveArabicTableData(EducationCalendarActivity.this, 2,
                                timeStamp).execute();
                    }
                }
            } else if (educationEvents.size() > 0) {
                new InsertDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                        educationalCalendarEventsTableArabic, language).execute();
            } else {
                progressBar.setVisibility(View.GONE);
                eventListView.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getNumberOfRowsArabic();

        }
    }

    public class CheckEventRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<EducationCalendarActivity> activityReference;
        String language;

        CheckEventRowExist(EducationCalendarActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (educationEvents.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                            .checkEnglishWithEventDateExist(educationEvents.get(0).getDate());
                    if (n > 0) {
                        for (int i = 0; i < educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkEnglishWithEventIdExist(educationEvents.get(i).getDate(),
                                            educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(EducationCalendarActivity.this, language,
                                        i, educationEvents.get(i).getDate(), educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                        educationalCalendarEventsTableArabic, language,i).execute();
                            }
                        }
                    } else {
                        new InsertDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                educationalCalendarEventsTableArabic, language).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao().checkArabicWithEventDateExist(
                            educationEvents.get(0).getDate());
                    if (n > 0) {
                        for (int i = 0; i < educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkArabicWithEventIdExist(educationEvents.get(i).getDate(),
                                            educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(EducationCalendarActivity.this, language,
                                        i, educationEvents.get(i).getDate(), educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                        educationalCalendarEventsTableArabic, language,i).execute();
                                break;
                            }
                        }
                    } else {
                        new EducationCalendarActivity.InsertDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                educationalCalendarEventsTableArabic, language).execute();
                    }
                }
            }
            return null;
        }
    }

    public class UpdateEventsTableRow extends AsyncTask<Void, Void, Void> {
        private WeakReference<EducationCalendarActivity> activityReference;
        String language;
        int position;
        String date;
        String id;

        UpdateEventsTableRow(EducationCalendarActivity context, String apiLanguage, int p,
                             String eventDate, String eventId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
            date = eventDate;
            id = eventId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (language.equals("en")) {
                // updateEnglishTable table with english name

                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsEnglish(
                        educationEvents.get(position).getTitle(),
                        educationEvents.get(position).getStart_time(),
                        educationEvents.get(position).getEnd_time(),
                        educationEvents.get(position).getRegistration(),
                        educationEvents.get(position).getMax_group_size(),
                        educationEvents.get(position).getShort_desc(),
                        educationEvents.get(position).getLong_desc(),
                        educationEvents.get(position).getLocation(),
                        educationEvents.get(position).getCategory(),
                        educationEvents.get(position).getEid()
                );


            } else {
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsArabic(
                        educationEvents.get(position).getTitle(),
                        educationEvents.get(position).getStart_time(),
                        educationEvents.get(position).getEnd_time(),
                        educationEvents.get(position).getRegistration(),
                        educationEvents.get(position).getMax_group_size(),
                        educationEvents.get(position).getShort_desc(),
                        educationEvents.get(position).getLong_desc(),
                        educationEvents.get(position).getLocation(),
                        educationEvents.get(position).getCategory(),
                        educationEvents.get(position).getEid()
                );
            }
            return null;
        }
    }

    public class InsertSingleElementDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<EducationCalendarActivity> activityReference;
        private EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish;
        private EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic;
        String language;
        int position;

        public InsertSingleElementDatabaseTask(EducationCalendarActivity context,
                                  EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish,
                                  EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic,
                                  String language,int pos) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTableEnglish = educationalCalendarEventsTableEnglish;
            this.educationalCalendarEventsTableArabic = educationalCalendarEventsTableArabic;
            this.language = language;
            this.position=pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (educationEvents != null) {
                if (language.equals("en")) {
                    educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                            educationEvents.get(position).getEid(),
                            educationEvents.get(position).getTitle(),
                            educationEvents.get(position).getDate(),
                            educationEvents.get(position).getInstitution(),
                            educationEvents.get(position).getAge_group(),
                            educationEvents.get(position).getProgram_type(),
                            educationEvents.get(position).getStart_time(),
                            educationEvents.get(position).getEnd_time(),
                            educationEvents.get(position).getRegistration(),
                            educationEvents.get(position).getMax_group_size(),
                            educationEvents.get(position).getShort_desc(),
                            educationEvents.get(position).getLong_desc(),
                            educationEvents.get(position).getLocation(),
                            educationEvents.get(position).getCategory(),
                            educationEvents.get(position).getFilter()
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                }else{

                }
            }
            return true;
        }
    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<EducationCalendarActivity> activityReference;
        private EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish;
        private EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic;
        String language;

        public InsertDatabaseTask(EducationCalendarActivity context,
                                  EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish,
                                  EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic,
                                  String language) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTableEnglish = educationalCalendarEventsTableEnglish;
            this.educationalCalendarEventsTableArabic = educationalCalendarEventsTableArabic;
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (educationEvents != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < educationEvents.size(); i++) {
                        educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                                educationEvents.get(i).getEid(),
                                educationEvents.get(i).getTitle(),
                                educationEvents.get(i).getDate(),
                                educationEvents.get(i).getInstitution(),
                                educationEvents.get(i).getAge_group(),
                                educationEvents.get(i).getProgram_type(),
                                educationEvents.get(i).getStart_time(),
                                educationEvents.get(i).getEnd_time(),
                                educationEvents.get(i).getRegistration(),
                                educationEvents.get(i).getMax_group_size(),
                                educationEvents.get(i).getShort_desc(),
                                educationEvents.get(i).getLong_desc(),
                                educationEvents.get(i).getLocation(),
                                educationEvents.get(i).getCategory(),
                                educationEvents.get(i).getFilter()
                        );
                        activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                                insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < educationEvents.size(); i++) {
                        educationalCalendarEventsTableArabic = new EducationalCalendarEventsTableArabic(
                                educationEvents.get(i).getEid(),
                                educationEvents.get(i).getTitle(),
                                educationEvents.get(i).getDate(),
                                educationEvents.get(i).getInstitution(),
                                educationEvents.get(i).getAge_group(),
                                educationEvents.get(i).getProgram_type(),
                                educationEvents.get(i).getStart_time(),
                                educationEvents.get(i).getEnd_time(),
                                educationEvents.get(i).getRegistration(),
                                educationEvents.get(i).getMax_group_size(),
                                educationEvents.get(i).getShort_desc(),
                                educationEvents.get(i).getLong_desc(),
                                educationEvents.get(i).getLocation(),
                                educationEvents.get(i).getCategory(),
                                educationEvents.get(i).getFilter()
                        );
                        activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                                insertEventsTableArabic(educationalCalendarEventsTableArabic);

                    }
                }
            }
            return true;
        }
    }

    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<EducationalCalendarEventsTableEnglish>> {

        private WeakReference<EducationCalendarActivity> activityReference;
        int language;
        long eventDate;

        RetriveEnglishTableData(EducationCalendarActivity context, int appLanguage, long eventDate) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.eventDate = eventDate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<EducationalCalendarEventsTableEnglish> educationalCalendarEventsTableEnglish) {
            educationEvents.clear();
            if (educationalCalendarEventsTableEnglish.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableEnglish.size(); i++) {
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableEnglish.get(i).getEvent_id(),
                            educationalCalendarEventsTableEnglish.get(i).getFilter(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_title(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_short_description(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_long_description(),
                            educationalCalendarEventsTableEnglish.get(i).getLocation(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_institution(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_start_time(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_end_time(),
                            educationalCalendarEventsTableEnglish.get(i).getMax_group_size(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableEnglish.get(i).getCategory(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_registration(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_date()
                    );
                    educationEvents.add(i, educationEventsList);
                }
                educationAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noResultFoundTxt.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                eventListView.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<EducationalCalendarEventsTableEnglish> doInBackground(Void... voids) {
            if (institutionFilter.equalsIgnoreCase("any")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getAllEventsEnglish(String.valueOf(eventDate));

            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getEventsWithDateEnglish(String.valueOf(eventDate), institutionFilter,
                        ageGroupFilter, programmeTypeFilter);
            }
        }
    }

    public class RetriveArabicTableData extends AsyncTask<Void, Void, List<EducationalCalendarEventsTableArabic>> {

        private WeakReference<EducationCalendarActivity> activityReference;
        int language;
        long eventDate;

        RetriveArabicTableData(EducationCalendarActivity context, int appLanguage, long eventDate) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.eventDate = eventDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<EducationalCalendarEventsTableArabic> educationalCalendarEventsTableArabic) {
            educationEvents.clear();
            if (educationalCalendarEventsTableArabic.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableArabic.size(); i++) {
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableArabic.get(i).getEvent_id(),
                            educationalCalendarEventsTableArabic.get(i).getFilter(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_title(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_short_description(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_long_description(),
                            educationalCalendarEventsTableArabic.get(i).getLocation(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_institution(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_start_time(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_end_time(),
                            educationalCalendarEventsTableArabic.get(i).getMax_group_size(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableArabic.get(i).getCategory(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_registration(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_date()
                    );
                    educationEvents.add(i, educationEventsList);
                }
                educationAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noResultFoundTxt.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                eventListView.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<EducationalCalendarEventsTableArabic> doInBackground(Void... voids) {

            if (institutionFilter.equalsIgnoreCase("any")) {

                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getAllEventsArabic(String.valueOf(eventDate));

            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().getEventsWithDateArabic(String.valueOf(eventDate), institutionFilter,
                        ageGroupFilter, programmeTypeFilter);
            }
        }
    }

    public void updateTimeStamp() {
        for (int i = 0; i < educationEvents.size(); i++) {
            educationEvents.get(i).setDate(String.valueOf(calendarInstance.getTimeInMillis() / 1000));
        }
    }

    public void sortEventsWithStartTime() {
        Collections.sort(educationEvents, new Comparator<EducationEvents>() {
            @Override
            public int compare(EducationEvents o1, EducationEvents o2) {
                return o1.getStart_time().compareTo(o2.getStart_time());
            }
        });
    }

    private boolean checkWednesdayorSunday() {

        if (collapsibleCalendar.getSelectedItem() == null) {
            calendarInstance = new GregorianCalendar();
            calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
            calendarInstance.set(Calendar.MINUTE, 0);
            calendarInstance.set(Calendar.SECOND, 0);
        } else {
            calendarInstance = Calendar.getInstance();
            calendarInstance.set(collapsibleCalendar.getSelectedItem().getYear(),
                    collapsibleCalendar.getSelectedItem().getMonth(),
                    collapsibleCalendar.getSelectedItem().getDay());
            calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
            calendarInstance.set(Calendar.MINUTE, 0);
            calendarInstance.set(Calendar.SECOND, 0);
        }
        if (calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return true;
        } else
            return false;

    }

    public void showDialog(final String buttonText
            , final ArrayList<EducationEvents> educationEvents, final int position) {

        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button registerNowBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);


        dialogTitle.setText(educationEvents.get(position).getTitle());
        registerNowBtn.setText(buttonText);
        dialogContent.setText(educationEvents.get(position).getLong_desc());

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                if (buttonText.equalsIgnoreCase(getResources().getString(R.string.register_now))) {
                    new Util().showComingSoonDialog(EducationCalendarActivity.this);
                    dialog.dismiss();
                } else {
                    addToCalendar(educationEvents, position);
                    dialog.dismiss();
                }

            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                dialog.dismiss();

            }
        });
        dialog.show();


    }

    public void addToCalendar(final ArrayList<EducationEvents> educationEvents, final int position) {
        calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
        calendarInstance.set(Calendar.MINUTE, 0);
        contentResolver = getContentResolver();
        cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, educationEvents.get(position).getTitle());
        cv.put(CalendarContract.Events.DESCRIPTION, educationEvents.get(position).getLong_desc());
        cv.put(CalendarContract.Events.DTSTART, calendarInstance.getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, calendarInstance.getTimeInMillis() + 2 * 60 * 60 * 1000);
        // Default calendar
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            cv.put(CalendarContract.Events.CALENDAR_ID, 3);
        } else {
            cv.put(CalendarContract.Events.CALENDAR_ID, 1);
        }
        TimeZone timeZone = TimeZone.getDefault();
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((EducationCalendarActivity) this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_CALENDAR);

            } else {
                insertEventToCalendar();
            }
        } else
            insertEventToCalendar();


    }

    public void insertEventToCalendar() {
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
        Snackbar snackbar = Snackbar
                .make(layoutContainer, R.string.event_added, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    insertEventToCalendar();
                } else {
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    }
                    if (!showRationale) {
                        showNavigationDialog(getString(R.string.permission_required), getString(R.string.runtime_permission));
                    }
                }
            }
        }
    }

    protected void showNavigationDialog(String title, final String details) {

        dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogActionButton.setText(getResources().getString(R.string.open_settings));
        dialogContent.setText(details);

        dialogActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSettings();
                dialog.dismiss();

            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void navigateToSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            insertEventToCalendar();
        }
    }
}

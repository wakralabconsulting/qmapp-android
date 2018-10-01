package com.qatarmuseums.qatarmuseumsapp.education;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    SharedPreferences sharedfilterpreferences;
    SharedPreferences.Editor editor;
    public static final String FILTERPREFS = "FilterPref";
    public static final String INSTITUTEPREFS = "InstitutionPref";
    public static final String AGEGROUPPREFS = "AgeGroupPref";
    public static final String PROGRAMMEPREFS = "ProgrammePref";
    private int appLanguage;
    private Calendar today;
    String day;
    String monthNumber;
    String year;

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
        sharedfilterpreferences = getSharedPreferences(FILTERPREFS, Context.MODE_PRIVATE);
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
            institutionFilter = "All";
        }
        if (ageGroupFilter == null) {
            ageGroupFilter = "All";
        }
        if (programmeTypeFilter == null) {
            programmeTypeFilter = "All";
        }
        educationAdapter = new EducationAdapter(EducationCalendarActivity.this, educationEvents);
        layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(educationAdapter);

        today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        day = (String) DateFormat.format("dd", today);
        monthNumber = (String) DateFormat.format("M", today);
        year = (String) DateFormat.format("yyyy", today);
        if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
            if (isDateSelected) {
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                day = (String) DateFormat.format("dd", selectedDate);
                monthNumber = (String) DateFormat.format("M", selectedDate);
                year = (String) DateFormat.format("yyyy", selectedDate);
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
            } else {
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, today.getTimeInMillis());
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
                calendarInstance.set(Calendar.HOUR_OF_DAY, 0);
                calendarInstance.set(Calendar.MINUTE, 0);
                calendarInstance.set(Calendar.SECOND, 0);
                isDateSelected = true;
                selectedDate = calendarInstance.getTimeInMillis();
                if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
                    day = (String) DateFormat.format("dd", selectedDate);
                    monthNumber = (String) DateFormat.format("M", selectedDate);
                    year = (String) DateFormat.format("yyyy", selectedDate);
                    getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
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

        eventListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Scrolling up
                if (collapsibleCalendar.expanded) {
                    collapsibleCalendar.collapse(400);
                } else {
                    // Scrolling down
                    collapsibleCalendar.expand(400);
                    collapsibleCalendar.expanded = true;
                }
                return false;
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
                day = (String) DateFormat.format("dd", selectedDate);
                monthNumber = (String) DateFormat.format("M", selectedDate);
                year = (String) DateFormat.format("yyyy", selectedDate);
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
                eventListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, today.getTimeInMillis());
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

    private void getEducationCalendarDataFromApi(String institute, String ageGroup, String programmeType, final String month, final String day, final String year, final long timeStamp) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }

        Call<ArrayList<EducationEvents>> call = apiService.
                getEducationCalendarDetails(language, institute, ageGroup, programmeType, month, day, year, "field_eduprog_date");
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
                        removeHtmlTags(educationEvents);
                        updateTimeStamp(timeStamp);
                        updateStartAndEndTime();
                        convertToTimstamp(day, month, year);
                        sortEventsWithStartTime();
                        educationAdapter.notifyDataSetChanged();
                        new EventsRowCount(EducationCalendarActivity.this, language, timeStamp / 1000).execute();

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

    public void removeHtmlTags(ArrayList<EducationEvents> models) {
        for (int i = 0; i < models.size(); i++) {
            ArrayList<String> fieldval = models.get(i).getField();
            fieldval.set(0, util.html2string(fieldval.get(0)));
            models.get(i).setField(fieldval);
            ArrayList<String> mainDescriptionVal = models.get(i).getLong_desc();
            mainDescriptionVal.set(0, util.html2string(mainDescriptionVal.get(0)));
            models.get(i).setLong_desc(mainDescriptionVal);
            models.get(i).setTitle(util.html2string(models.get(i).getTitle()));

        }
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
                    new CheckEventRowExist(EducationCalendarActivity.this, language, timeStamp).execute();
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
                        educationalCalendarEventsTableArabic, language, timeStamp).execute();
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
        long timestamp;

        CheckEventRowExist(EducationCalendarActivity context, String apiLanguage, long timestampval) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            timestamp = timestampval;
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
                            .checkEnglishWithEventDateExist(String.valueOf(timestamp));
                    if (n > 0) {
                        for (int i = 0; i < educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkEnglishWithEventIdExist(String.valueOf(timestamp),
                                            educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(EducationCalendarActivity.this, language,
                                        i, timestamp, educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                        educationalCalendarEventsTableArabic, language, i, timestamp).execute();
                            }
                        }
                    } else {
                        new InsertDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                educationalCalendarEventsTableArabic, language, timestamp).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                            .checkArabicWithEventDateExist(String.valueOf(timestamp));
                    if (n > 0) {
                        for (int i = 0; i < educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkArabicWithEventIdExist(String.valueOf(timestamp),
                                            educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(EducationCalendarActivity.this, language,
                                        i, timestamp, educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                        educationalCalendarEventsTableArabic, language, i, timestamp).execute();
                                break;
                            }
                        }
                    } else {
                        new EducationCalendarActivity.InsertDatabaseTask(EducationCalendarActivity.this, educationalCalendarEventsTableEnglish,
                                educationalCalendarEventsTableArabic, language, timestamp).execute();
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
        long date;
        String id;

        UpdateEventsTableRow(EducationCalendarActivity context, String apiLanguage, int p,
                             long eventDate, String eventId) {
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
                ArrayList<String> longDesc = new ArrayList<String>();
                longDesc = educationEvents.get(position).getLong_desc();
                ArrayList<String> fieldval = new ArrayList<String>();
                fieldval = educationEvents.get(position).getField();
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsEnglish(
                        educationEvents.get(position).getTitle(),
                        educationEvents.get(position).getStart_time(),
                        educationEvents.get(position).getEnd_time(),
                        educationEvents.get(position).getRegistration(),
                        educationEvents.get(position).getMax_group_size(),
                        educationEvents.get(position).getShort_desc(),
                        util.html2string(longDesc.get(0)),
                        educationEvents.get(position).getLocation(),
                        educationEvents.get(position).getCategory(),
                        educationEvents.get(position).getEid()
                );

            } else {
                ArrayList<String> longDesc = new ArrayList<String>();
                longDesc = educationEvents.get(position).getLong_desc();
                ArrayList<String> fieldval = new ArrayList<String>();
                fieldval = educationEvents.get(position).getField();
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsArabic(
                        educationEvents.get(position).getTitle(),
                        educationEvents.get(position).getStart_time(),
                        educationEvents.get(position).getEnd_time(),
                        educationEvents.get(position).getRegistration(),
                        educationEvents.get(position).getMax_group_size(),
                        educationEvents.get(position).getShort_desc(),
                        util.html2string(longDesc.get(0)),
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
        long timestamp;

        public InsertSingleElementDatabaseTask(EducationCalendarActivity context,
                                               EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish,
                                               EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic,
                                               String language, int pos, long timestamp) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTableEnglish = educationalCalendarEventsTableEnglish;
            this.educationalCalendarEventsTableArabic = educationalCalendarEventsTableArabic;
            this.language = language;
            this.position = pos;
            this.timestamp = timestamp;
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
                    ArrayList<String> longDesc = new ArrayList<String>();
                    longDesc = educationEvents.get(position).getLong_desc();
                    ArrayList<String> fieldval = new ArrayList<String>();
                    fieldval = educationEvents.get(position).getField();
                    educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                            educationEvents.get(position).getEid(),
                            educationEvents.get(position).getTitle(),
                            String.valueOf(timestamp),
                            educationEvents.get(position).getInstitution(),
                            educationEvents.get(position).getAge_group(),
                            educationEvents.get(position).getProgram_type(),
                            educationEvents.get(position).getStart_time(),
                            educationEvents.get(position).getEnd_time(),
                            educationEvents.get(position).getRegistration(),
                            educationEvents.get(position).getMax_group_size(),
                            educationEvents.get(position).getShort_desc(),
                            util.html2string(longDesc.get(0)),
                            educationEvents.get(position).getLocation(),
                            educationEvents.get(position).getCategory(),
                            educationEvents.get(position).getFilter(),
                            util.html2string(fieldval.get(0))
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                } else {
                    ArrayList<String> longDesc = new ArrayList<String>();
                    longDesc = educationEvents.get(position).getLong_desc();
                    ArrayList<String> fieldval = new ArrayList<String>();
                    fieldval = educationEvents.get(position).getField();
                    educationalCalendarEventsTableArabic = new EducationalCalendarEventsTableArabic(
                            educationEvents.get(position).getEid(),
                            educationEvents.get(position).getTitle(),
                            String.valueOf(timestamp),
                            educationEvents.get(position).getInstitution(),
                            educationEvents.get(position).getAge_group(),
                            educationEvents.get(position).getProgram_type(),
                            educationEvents.get(position).getStart_time(),
                            educationEvents.get(position).getEnd_time(),
                            educationEvents.get(position).getRegistration(),
                            educationEvents.get(position).getMax_group_size(),
                            educationEvents.get(position).getShort_desc(),
                            util.html2string(longDesc.get(0)),
                            educationEvents.get(position).getLocation(),
                            educationEvents.get(position).getCategory(),
                            educationEvents.get(position).getFilter(),
                            util.html2string(fieldval.get(0))
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTableArabic(educationalCalendarEventsTableArabic);
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
        long timestamp;

        public InsertDatabaseTask(EducationCalendarActivity context,
                                  EducationalCalendarEventsTableEnglish educationalCalendarEventsTableEnglish,
                                  EducationalCalendarEventsTableArabic educationalCalendarEventsTableArabic,
                                  String language, long timestamp) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTableEnglish = educationalCalendarEventsTableEnglish;
            this.educationalCalendarEventsTableArabic = educationalCalendarEventsTableArabic;
            this.language = language;
            this.timestamp = timestamp;
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
                        ArrayList<String> longDesc = new ArrayList<String>();
                        longDesc = educationEvents.get(i).getLong_desc();
                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = educationEvents.get(i).getField();
                        educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                                educationEvents.get(i).getEid(),
                                educationEvents.get(i).getTitle(),
                                String.valueOf(timestamp),
                                educationEvents.get(i).getInstitution(),
                                educationEvents.get(i).getAge_group(),
                                educationEvents.get(i).getProgram_type(),
                                educationEvents.get(i).getStart_time(),
                                educationEvents.get(i).getEnd_time(),
                                educationEvents.get(i).getRegistration(),
                                educationEvents.get(i).getMax_group_size(),
                                educationEvents.get(i).getShort_desc(),
                                util.html2string(longDesc.get(0)),
                                educationEvents.get(i).getLocation(),
                                educationEvents.get(i).getCategory(),
                                educationEvents.get(i).getFilter(),
                                util.html2string(fieldval.get(0))
                        );
                        activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                                insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < educationEvents.size(); i++) {
                        ArrayList<String> longDesc = new ArrayList<String>();
                        longDesc = educationEvents.get(i).getLong_desc();
                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = educationEvents.get(i).getField();
                        educationalCalendarEventsTableArabic = new EducationalCalendarEventsTableArabic(
                                educationEvents.get(i).getEid(),
                                educationEvents.get(i).getTitle(),
                                String.valueOf(timestamp),
                                educationEvents.get(i).getInstitution(),
                                educationEvents.get(i).getAge_group(),
                                educationEvents.get(i).getProgram_type(),
                                educationEvents.get(i).getStart_time(),
                                educationEvents.get(i).getEnd_time(),
                                educationEvents.get(i).getRegistration(),
                                educationEvents.get(i).getMax_group_size(),
                                educationEvents.get(i).getShort_desc(),
                                util.html2string(longDesc.get(0)),
                                educationEvents.get(i).getLocation(),
                                educationEvents.get(i).getCategory(),
                                educationEvents.get(i).getFilter(),
                                util.html2string(fieldval.get(0))
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
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> longDesc = new ArrayList<String>();
            if (educationalCalendarEventsTableEnglish.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableEnglish.size(); i++) {
                    longDesc.add(0, educationalCalendarEventsTableEnglish.get(i).getEvent_long_description());
                    fieldval.add(0, educationalCalendarEventsTableEnglish.get(i).getField());
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableEnglish.get(i).getEvent_id(),
                            educationalCalendarEventsTableEnglish.get(i).getFilter(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_title(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_short_description(),
                            longDesc,
                            educationalCalendarEventsTableEnglish.get(i).getLocation(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_institution(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_start_time(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_end_time(),
                            educationalCalendarEventsTableEnglish.get(i).getMax_group_size(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableEnglish.get(i).getCategory(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_registration(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_date(),
                            fieldval
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
            if (institutionFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All") &&
                    programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getAllEventsEnglish(String.valueOf(eventDate));
            } else if (institutionFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getProgrammeFilterEventsEnglish(String.valueOf(eventDate), programmeTypeFilter);
            } else if (institutionFilter.equalsIgnoreCase("All") &&
                    programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getAgeGroupFilterEventsEnglish(String.valueOf(eventDate), ageGroupFilter);
            } else if (programmeTypeFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getInstitutionFilterEventsEnglish(String.valueOf(eventDate), institutionFilter);
            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getEventsWithDateEnglish(String.valueOf(eventDate), institutionFilter,
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
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> longDesc = new ArrayList<String>();
            if (educationalCalendarEventsTableArabic.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableArabic.size(); i++) {
                    longDesc.add(0, educationalCalendarEventsTableArabic.get(i).getEvent_long_description());
                    fieldval.add(0, educationalCalendarEventsTableArabic.get(i).getField());
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableArabic.get(i).getEvent_id(),
                            educationalCalendarEventsTableArabic.get(i).getFilter(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_title(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_short_description(),
                            longDesc, educationalCalendarEventsTableArabic.get(i).getLocation(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_institution(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_start_time(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_end_time(),
                            educationalCalendarEventsTableArabic.get(i).getMax_group_size(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableArabic.get(i).getCategory(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_registration(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_date(),
                            fieldval
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

            if (institutionFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All") &&
                    programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getAllEventsArabic(String.valueOf(eventDate));
            } else if (institutionFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getProgrammeFilterEventsArabic(String.valueOf(eventDate), programmeTypeFilter);
            } else if (institutionFilter.equalsIgnoreCase("All") &&
                    programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getAgeGroupFilterEventsArabic(String.valueOf(eventDate), ageGroupFilter);
            } else if (programmeTypeFilter.equalsIgnoreCase("All") &&
                    ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getInstitutionFilterEventsArabic(String.valueOf(eventDate), institutionFilter);
            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getEventsWithDateArabic(String.valueOf(eventDate), institutionFilter,
                                ageGroupFilter, programmeTypeFilter);
            }
        }
    }

    public void updateTimeStamp(long timestamp) {
        for (int i = 0; i < educationEvents.size(); i++) {
            educationEvents.get(i).setDate(String.valueOf(timestamp / 1000));
        }
    }

    public void convertToTimstamp(String day, String month, String year) {
//        converting time to timstamp and updating
        for (int i = 0; i < educationEvents.size(); i++) {
            String str_start_date = day + "-" + month + "-" + year + " " + educationEvents.get(i).getStart_time();
            String str_end_date = day + "-" + month + "-" + year + " " + educationEvents.get(i).getEnd_time();
            educationEvents.get(i).setStart_time(String.valueOf(convertDate(str_start_date)));
            educationEvents.get(i).setEnd_time(String.valueOf(convertDate(str_end_date)));
        }
    }

    public long convertDate(String dateVal) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = null;
        try {
            date = (Date) formatter.parse(dateVal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public void updateStartAndEndTime() {
        //extracting time from text and updating the time field
        for (int i = 0; i < educationEvents.size(); i++) {
            String str = educationEvents.get(i).getField().get(0);
            String value = str.substring(str.indexOf("-") + 1);
            String[] timeArray = value.trim().split("-");
            String startTime = timeArray[0].trim();
            String endTime = timeArray[1].trim();
            educationEvents.get(i).setStart_time(startTime);
            educationEvents.get(i).setEnd_time(endTime);
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

    public void showDialog(final String buttonText
            , final ArrayList<EducationEvents> educationEvents, final int position) {

        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);
        dialog.setContentView(view);
        FrameLayout contentLayout = (FrameLayout) view.findViewById(R.id.content_frame_layout);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        final Button registerNowBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        int heightValue = getScreenheight();
        contentLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightValue));
        ArrayList<String> descriptionVal = educationEvents.get(position).getLong_desc();
        dialogTitle.setText(educationEvents.get(position).getTitle());
        registerNowBtn.setText(buttonText);
        if (buttonText.equalsIgnoreCase(getResources().getString(R.string.register_now))) {
            registerNowBtn.setEnabled(false);
            registerNowBtn.setTextColor(getResources().getColor(R.color.white));
            registerNowBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
        }
        dialogContent.setText(descriptionVal.get(0));

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
        contentResolver = getContentResolver();
        ArrayList<String> description = educationEvents.get(position).getLong_desc();
        cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, educationEvents.get(position).getTitle());
        cv.put(CalendarContract.Events.DESCRIPTION, description.get(0));
        cv.put(CalendarContract.Events.DTSTART, educationEvents.get(position).getStart_time());
        cv.put(CalendarContract.Events.DTEND, educationEvents.get(position).getEnd_time());
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

    public int getScreenheight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double height = displayMetrics.heightPixels;
        height = (height) * (0.75);
        return (int) height;
    }
}

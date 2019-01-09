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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

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
    @BindView(R.id.progress)
    LinearLayout progress;
    @BindView(R.id.noResultFoundTxt)
    TextView noResultFoundTxt;
    @BindView(R.id.retry_layout)
    LinearLayout retryLayout;
    @BindView(R.id.retry_btn)
    Button retryButton;
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
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataOnline();
                progressBar.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            }
        });
        retryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        retryButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
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
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        day = (String) DateFormat.format("d", today);
        monthNumber = (String) DateFormat.format("M", today);
        year = (String) DateFormat.format("yyyy", today);
        if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
            getDataOnline();
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
                    day = (String) DateFormat.format("d", selectedDate);
                    monthNumber = (String) DateFormat.format("M", selectedDate);
                    year = (String) DateFormat.format("yyyy", selectedDate);
                    getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
                } else {
                    getEducationCalendarEventsFromDatabase(calendarInstance.getTimeInMillis());
                }
                eventListView.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.GONE);
                retryLayout.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
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

    public void getDataOnline() {
        if (isDateSelected) {
            eventListView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            day = (String) DateFormat.format("d", selectedDate);
            monthNumber = (String) DateFormat.format("M", selectedDate);
            year = (String) DateFormat.format("yyyy", selectedDate);
            getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
        } else {
            eventListView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, today.getTimeInMillis());
            isDateSelected = false;
        }
    }

    private void getEducationCalendarEventsFromDatabase(long timeStamp) {
        progress.setVisibility(View.VISIBLE);
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
                day = (String) DateFormat.format("d", selectedDate);
                monthNumber = (String) DateFormat.format("M", selectedDate);
                year = (String) DateFormat.format("yyyy", selectedDate);
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, selectedDate);
            } else {
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter, monthNumber, day, year, today.getTimeInMillis());
                isDateSelected = false;
            }
        } else {
            if (isDateSelected) {
                getEducationCalendarEventsFromDatabase(selectedDate);
            } else {
                getEducationCalendarEventsFromDatabase(today.getTimeInMillis());
                isDateSelected = false;
            }
        }
        eventListView.setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    private void getEducationCalendarDataFromApi(String institute, String ageGroup, String programmeType, final String month, final String day, final String year, final long timeStamp) {
        progress.setVisibility(View.VISIBLE);
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
                    if (response.body() != null && response.body().size() > 0) {
                        educationEvents.clear();
                        progress.setVisibility(View.GONE);
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
                        progress.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                        retryLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EducationEvents>> call, Throwable t) {
                eventListView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });

    }

    public void removeHtmlTags(ArrayList<EducationEvents> models) {
        for (int i = 0; i < models.size(); i++) {
            ArrayList<String> fieldval = models.get(i).getField();
            fieldval.set(0, util.html2string(fieldval.get(0)));
            models.get(i).setField(fieldval);
            ArrayList<String> startDateVal = models.get(i).getStart_time();
            startDateVal.set(0, util.html2string(startDateVal.get(0)));
            models.get(i).setStart_time(startDateVal);
            ArrayList<String> endDateVal = models.get(i).getEnd_time();
            endDateVal.set(0, util.html2string(endDateVal.get(0)));
            models.get(i).setEnd_time(endDateVal);
            models.get(i).setProgram_type(util.html2string(models.get(i).getProgram_type()));
            models.get(i).setLong_desc(util.html2string(models.get(i).getLong_desc()));
            models.get(i).setTitle(util.html2string(models.get(i).getTitle()));
        }
    }

    public static class EventsRowCount extends AsyncTask<Void, Void, Integer> {

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
            activityReference.get().eventsTableRowCount = integer;
            if (activityReference.get().eventsTableRowCount > 0) {
                if (activityReference.get().educationEvents.size() > 0 &&
                        activityReference.get().util.isNetworkAvailable(activityReference.get()))
                    new CheckEventRowExist(activityReference.get(), language, timeStamp).execute();
                else {
                    if (language.equals("en")) {
                        new RetriveEnglishTableData(activityReference.get(), 1,
                                timeStamp).execute();
                    } else {
                        new RetriveArabicTableData(activityReference.get(), 2,
                                timeStamp).execute();
                    }
                }
            } else if (activityReference.get().educationEvents.size() > 0) {
                new InsertDatabaseTask(activityReference.get(),
                        activityReference.get().educationalCalendarEventsTableEnglish,
                        activityReference.get().educationalCalendarEventsTableArabic, language, timeStamp).execute();
            } else {
                activityReference.get().progress.setVisibility(View.GONE);
                activityReference.get().eventListView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
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

    public static class CheckEventRowExist extends AsyncTask<Void, Void, Void> {

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
            if (activityReference.get().educationEvents.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                            .checkEnglishWithEventDateExist(String.valueOf(timestamp));
                    if (n > 0) {
                        for (int i = 0; i < activityReference.get().educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkEnglishWithEventIdExist(String.valueOf(timestamp),
                                            activityReference.get().educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(activityReference.get(), language,
                                        i, timestamp, activityReference.get().educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(activityReference.get(),
                                        activityReference.get().educationalCalendarEventsTableEnglish,
                                        activityReference.get().educationalCalendarEventsTableArabic, language, i, timestamp).execute();
                            }
                        }
                    } else {
                        new InsertDatabaseTask(activityReference.get(),
                                activityReference.get().educationalCalendarEventsTableEnglish,
                                activityReference.get().educationalCalendarEventsTableArabic, language, timestamp).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                            .checkArabicWithEventDateExist(String.valueOf(timestamp));
                    if (n > 0) {
                        for (int i = 0; i < activityReference.get().educationEvents.size(); i++) {
                            int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                    .checkArabicWithEventIdExist(String.valueOf(timestamp),
                                            activityReference.get().educationEvents.get(i).getEid());
                            if (count > 0) {
                                new UpdateEventsTableRow(activityReference.get(), language,
                                        i, timestamp, activityReference.get().educationEvents.get(i).getEid()).execute();
                            } else {
                                new InsertSingleElementDatabaseTask(activityReference.get(),
                                        activityReference.get().educationalCalendarEventsTableEnglish,
                                        activityReference.get().educationalCalendarEventsTableArabic, language, i, timestamp).execute();
                                break;
                            }
                        }
                    } else {
                        new EducationCalendarActivity.InsertDatabaseTask(activityReference.get(),
                                activityReference.get().educationalCalendarEventsTableEnglish,
                                activityReference.get().educationalCalendarEventsTableArabic, language, timestamp).execute();
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateEventsTableRow extends AsyncTask<Void, Void, Void> {
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

                ArrayList<String> fieldval = new ArrayList<String>();
                fieldval = activityReference.get().educationEvents.get(position).getField();
                ArrayList<String> startDate = new ArrayList<String>();
                startDate = activityReference.get().educationEvents.get(position).getStart_time();
                ArrayList<String> endDate = new ArrayList<String>();
                endDate = activityReference.get().educationEvents.get(position).getEnd_time();
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsEnglish(
                        activityReference.get().educationEvents.get(position).getTitle(),
                        activityReference.get().util.html2string(startDate.get(0)),
                        activityReference.get().util.html2string(endDate.get(0)),
                        activityReference.get().educationEvents.get(position).getRegistration(),
                        activityReference.get().educationEvents.get(position).getMax_group_size(),
                        activityReference.get().educationEvents.get(position).getShort_desc(),
                        activityReference.get().educationEvents.get(position).getLong_desc(),
                        activityReference.get().educationEvents.get(position).getLocation(),
                        activityReference.get().educationEvents.get(position).getCategory(),
                        activityReference.get().educationEvents.get(position).getEid()
                );

            } else {
                ArrayList<String> fieldval = new ArrayList<String>();
                fieldval = activityReference.get().educationEvents.get(position).getField();
                ArrayList<String> startDate = new ArrayList<String>();
                startDate = activityReference.get().educationEvents.get(position).getStart_time();
                ArrayList<String> endDate = new ArrayList<String>();
                endDate = activityReference.get().educationEvents.get(position).getEnd_time();
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEventsArabic(
                        activityReference.get().educationEvents.get(position).getTitle(),
                        activityReference.get().util.html2string(startDate.get(0)),
                        activityReference.get().util.html2string(endDate.get(0)),
                        activityReference.get().educationEvents.get(position).getRegistration(),
                        activityReference.get().educationEvents.get(position).getMax_group_size(),
                        activityReference.get().educationEvents.get(position).getShort_desc(),
                        activityReference.get().educationEvents.get(position).getLong_desc(),
                        activityReference.get().educationEvents.get(position).getLocation(),
                        activityReference.get().educationEvents.get(position).getCategory(),
                        activityReference.get().educationEvents.get(position).getEid()
                );
            }
            return null;
        }
    }

    public static class InsertSingleElementDatabaseTask extends AsyncTask<Void, Void, Boolean> {

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
            if (activityReference.get().educationEvents != null) {
                if (language.equals("en")) {
                    Convertor converters = new Convertor();
                    ArrayList<String> fieldval = new ArrayList<String>();
                    fieldval = activityReference.get().educationEvents.get(position).getField();
                    ArrayList<String> startDate = new ArrayList<String>();
                    startDate = activityReference.get().educationEvents.get(position).getStart_time();
                    ArrayList<String> endDate = new ArrayList<String>();
                    endDate = activityReference.get().educationEvents.get(position).getEnd_time();
                    educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                            activityReference.get().educationEvents.get(position).getEid(),
                            activityReference.get().educationEvents.get(position).getTitle(),
                            String.valueOf(timestamp),
                            activityReference.get().educationEvents.get(position).getInstitution(),
                            activityReference.get().educationEvents.get(position).getAge_group(),
                            activityReference.get().educationEvents.get(position).getProgram_type(),
                            activityReference.get().util.html2string(startDate.get(0)),
                            activityReference.get().util.html2string(endDate.get(0)),
                            activityReference.get().educationEvents.get(position).getRegistration(),
                            activityReference.get().educationEvents.get(position).getMax_group_size(),
                            activityReference.get().educationEvents.get(position).getShort_desc(),
                            activityReference.get().educationEvents.get(position).getLong_desc(),
                            activityReference.get().educationEvents.get(position).getLocation(),
                            activityReference.get().educationEvents.get(position).getCategory(),
                            activityReference.get().educationEvents.get(position).getFilter(),
                            activityReference.get().util.html2string(fieldval.get(0)),
                            converters.fromArrayList(activityReference.get().educationEvents.get(position).getAge()),
                            converters.fromArrayList(activityReference.get().educationEvents.get(position).getAssociatedTopics()),
                            activityReference.get().educationEvents.get(position).getMuseumDepartment()
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                } else {
                    Convertor converters = new Convertor();
                    ArrayList<String> fieldval = new ArrayList<String>();
                    fieldval = activityReference.get().educationEvents.get(position).getField();
                    ArrayList<String> startDate = new ArrayList<String>();
                    startDate = activityReference.get().educationEvents.get(position).getStart_time();
                    ArrayList<String> endDate = new ArrayList<String>();
                    endDate = activityReference.get().educationEvents.get(position).getEnd_time();
                    educationalCalendarEventsTableArabic = new EducationalCalendarEventsTableArabic(
                            activityReference.get().educationEvents.get(position).getEid(),
                            activityReference.get().educationEvents.get(position).getTitle(),
                            String.valueOf(timestamp),
                            activityReference.get().educationEvents.get(position).getInstitution(),
                            activityReference.get().educationEvents.get(position).getAge_group(),
                            activityReference.get().educationEvents.get(position).getProgram_type(),
                            activityReference.get().util.html2string(startDate.get(0)),
                            activityReference.get().util.html2string(endDate.get(0)),
                            activityReference.get().educationEvents.get(position).getRegistration(),
                            activityReference.get().educationEvents.get(position).getMax_group_size(),
                            activityReference.get().educationEvents.get(position).getShort_desc(),
                            activityReference.get().educationEvents.get(position).getLong_desc(),
                            activityReference.get().educationEvents.get(position).getLocation(),
                            activityReference.get().educationEvents.get(position).getCategory(),
                            activityReference.get().educationEvents.get(position).getFilter(),
                            activityReference.get().util.html2string(fieldval.get(0)),
                            converters.fromArrayList(activityReference.get().educationEvents.get(position).getAge()),
                            converters.fromArrayList(activityReference.get().educationEvents.get(position).getAssociatedTopics()),
                            activityReference.get().educationEvents.get(position).getMuseumDepartment()
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTableArabic(educationalCalendarEventsTableArabic);
                }
            }
            return true;
        }
    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {

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
            if (activityReference.get().educationEvents != null) {
                if (language.equals("en")) {
                    Convertor converters = new Convertor();
                    for (int i = 0; i < activityReference.get().educationEvents.size(); i++) {
                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = activityReference.get().educationEvents.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = activityReference.get().educationEvents.get(i).getStart_time();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = activityReference.get().educationEvents.get(i).getEnd_time();
                        educationalCalendarEventsTableEnglish = new EducationalCalendarEventsTableEnglish(
                                activityReference.get().educationEvents.get(i).getEid(),
                                activityReference.get().educationEvents.get(i).getTitle(),
                                String.valueOf(timestamp),
                                activityReference.get().educationEvents.get(i).getInstitution(),
                                activityReference.get().educationEvents.get(i).getAge_group(),
                                activityReference.get().educationEvents.get(i).getProgram_type(),
                                activityReference.get().util.html2string(startDate.get(0)),
                                activityReference.get().util.html2string(endDate.get(0)),
                                activityReference.get().educationEvents.get(i).getRegistration(),
                                activityReference.get().educationEvents.get(i).getMax_group_size(),
                                activityReference.get().educationEvents.get(i).getShort_desc(),
                                activityReference.get().educationEvents.get(i).getLong_desc(),
                                activityReference.get().educationEvents.get(i).getLocation(),
                                activityReference.get().educationEvents.get(i).getCategory(),
                                activityReference.get().educationEvents.get(i).getFilter(),
                                activityReference.get().util.html2string(fieldval.get(0)),
                                converters.fromArrayList(activityReference.get().educationEvents.get(i).getAge()),
                                converters.fromArrayList(activityReference.get().educationEvents.get(i).getAssociatedTopics()),
                                activityReference.get().educationEvents.get(i).getMuseumDepartment()
                        );
                        activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                                insertEventsTableEnglish(educationalCalendarEventsTableEnglish);

                    }
                } else {
                    Convertor converters = new Convertor();
                    for (int i = 0; i < activityReference.get().educationEvents.size(); i++) {
                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = activityReference.get().educationEvents.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = activityReference.get().educationEvents.get(i).getStart_time();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = activityReference.get().educationEvents.get(i).getEnd_time();
                        educationalCalendarEventsTableArabic = new EducationalCalendarEventsTableArabic(
                                activityReference.get().educationEvents.get(i).getEid(),
                                activityReference.get().educationEvents.get(i).getTitle(),
                                String.valueOf(timestamp),
                                activityReference.get().educationEvents.get(i).getInstitution(),
                                activityReference.get().educationEvents.get(i).getAge_group(),
                                activityReference.get().educationEvents.get(i).getProgram_type(),
                                activityReference.get().util.html2string(startDate.get(0)),
                                activityReference.get().util.html2string(endDate.get(0)),
                                activityReference.get().educationEvents.get(i).getRegistration(),
                                activityReference.get().educationEvents.get(i).getMax_group_size(),
                                activityReference.get().educationEvents.get(i).getShort_desc(),
                                activityReference.get().educationEvents.get(i).getLong_desc(),
                                activityReference.get().educationEvents.get(i).getLocation(),
                                activityReference.get().educationEvents.get(i).getCategory(),
                                activityReference.get().educationEvents.get(i).getFilter(),
                                activityReference.get().util.html2string(fieldval.get(0)),
                                converters.fromArrayList(activityReference.get().educationEvents.get(i).getAge()),
                                converters.fromArrayList(activityReference.get().educationEvents.get(i).getAssociatedTopics()),
                                activityReference.get().educationEvents.get(i).getMuseumDepartment()
                        );
                        activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                                insertEventsTableArabic(educationalCalendarEventsTableArabic);

                    }
                }
            }
            return true;
        }
    }

    public static class RetriveEnglishTableData extends AsyncTask<Void, Void, List<EducationalCalendarEventsTableEnglish>> {

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
            activityReference.get().educationEvents.clear();
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (educationalCalendarEventsTableEnglish.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableEnglish.size(); i++) {
                    startDate.add(0, educationalCalendarEventsTableEnglish.get(i).getEvent_start_time());
                    endDate.add(0, educationalCalendarEventsTableEnglish.get(i).getEvent_end_time());
                    fieldval.add(0, educationalCalendarEventsTableEnglish.get(i).getField());
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableEnglish.get(i).getEvent_id(),
                            educationalCalendarEventsTableEnglish.get(i).getFilter(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_title(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_short_description(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_long_description(),
                            educationalCalendarEventsTableEnglish.get(i).getLocation(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            educationalCalendarEventsTableEnglish.get(i).getMax_group_size(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableEnglish.get(i).getCategory(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_registration(),
                            educationalCalendarEventsTableEnglish.get(i).getEvent_date(),
                            fieldval,
                            converters.fromString(educationalCalendarEventsTableEnglish.get(i).getAge()),
                            converters.fromString(educationalCalendarEventsTableEnglish.get(i).getAssociatedTopics()),
                            educationalCalendarEventsTableEnglish.get(i).getMuseum()
                    );
                    activityReference.get().educationEvents.add(i, educationEventsList);
                }
                activityReference.get().educationAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().progress.setVisibility(View.GONE);
                activityReference.get().eventListView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }

        @Override
        protected List<EducationalCalendarEventsTableEnglish> doInBackground(Void... voids) {
            if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getAllEventsEnglish(String.valueOf(eventDate));
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getProgrammeFilterEventsEnglish(String.valueOf(eventDate),
                                activityReference.get().programmeTypeFilter);
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getAgeGroupFilterEventsEnglish(String.valueOf(eventDate),
                                activityReference.get().ageGroupFilter);
            } else if (activityReference.get().programmeTypeFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getInstitutionFilterEventsEnglish(String.valueOf(eventDate),
                                activityReference.get().institutionFilter);
            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getEventsWithDateEnglish(String.valueOf(eventDate), activityReference.get().institutionFilter,
                                activityReference.get().ageGroupFilter,
                                activityReference.get().programmeTypeFilter);
            }
        }
    }

    public static class RetriveArabicTableData extends AsyncTask<Void, Void, List<EducationalCalendarEventsTableArabic>> {

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
            activityReference.get().educationEvents.clear();
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (educationalCalendarEventsTableArabic.size() > 0) {
                for (int i = 0; i < educationalCalendarEventsTableArabic.size(); i++) {
                    startDate.add(0, educationalCalendarEventsTableArabic.get(i).getEvent_start_time());
                    endDate.add(0, educationalCalendarEventsTableArabic.get(i).getEvent_end_time());
                    fieldval.add(0, educationalCalendarEventsTableArabic.get(i).getField());
                    EducationEvents educationEventsList = new EducationEvents(
                            educationalCalendarEventsTableArabic.get(i).getEvent_id(),
                            educationalCalendarEventsTableArabic.get(i).getFilter(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_title(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_short_description(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_long_description(),
                            educationalCalendarEventsTableArabic.get(i).getLocation(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            educationalCalendarEventsTableArabic.get(i).getMax_group_size(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_age_group(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_program_type(),
                            educationalCalendarEventsTableArabic.get(i).getCategory(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_registration(),
                            educationalCalendarEventsTableArabic.get(i).getEvent_date(),
                            fieldval,
                            converters.fromString(educationalCalendarEventsTableArabic.get(i).getAge()),
                            converters.fromString(educationalCalendarEventsTableArabic.get(i).getAssociatedTopics()),
                            educationalCalendarEventsTableArabic.get(i).getMuseum()
                    );
                    activityReference.get().educationEvents.add(i, educationEventsList);
                }
                activityReference.get().educationAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().progress.setVisibility(View.GONE);
                activityReference.get().eventListView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }

        @Override
        protected List<EducationalCalendarEventsTableArabic> doInBackground(Void... voids) {

            if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getAllEventsArabic(String.valueOf(eventDate));
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getProgrammeFilterEventsArabic(String.valueOf(eventDate), activityReference.get().programmeTypeFilter);
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getAgeGroupFilterEventsArabic(String.valueOf(eventDate), activityReference.get().ageGroupFilter);
            } else if (activityReference.get().programmeTypeFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getInstitutionFilterEventsArabic(String.valueOf(eventDate), activityReference.get().institutionFilter);
            } else {
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getEventsWithDateArabic(String.valueOf(eventDate), activityReference.get().institutionFilter,
                                activityReference.get().ageGroupFilter, activityReference.get().programmeTypeFilter);
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
            String str_start_date = day + "-" + month + "-" + year + " " + educationEvents.get(i).getStart_time().get(0);
            String str_end_date = day + "-" + month + "-" + year + " " + educationEvents.get(i).getEnd_time().get(0);
            ArrayList<String> st = new ArrayList<String>();
            st.add(0, String.valueOf(convertDate(str_start_date)));
            ArrayList<String> en = new ArrayList<String>();
            en.add(0, String.valueOf(convertDate(str_end_date)));
            educationEvents.get(i).setStart_time(st);
            educationEvents.get(i).setEnd_time(en);
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
            String start = educationEvents.get(i).getStart_time().get(0);
            String svalue = start.substring(start.lastIndexOf("-") + 1);
            String[] stimeArray = svalue.trim().split("-");
            String startTime = stimeArray[0].trim();
            String end = educationEvents.get(i).getEnd_time().get(0);
            String evalue = end.substring(end.lastIndexOf("-") + 1);
            String[] etimeArray = evalue.trim().split("-");
            String endTime = etimeArray[0].trim();
            ArrayList<String> startDateVal = new ArrayList<String>();
            startDateVal.add(0, startTime);
            ArrayList<String> endDateVal = new ArrayList<String>();
            endDateVal.add(0, endTime);
            educationEvents.get(i).setStart_time(startDateVal);
            educationEvents.get(i).setEnd_time(endDateVal);
        }

    }

    public void sortEventsWithStartTime() {
        Collections.sort(educationEvents, new Comparator<EducationEvents>() {
            @Override
            public int compare(EducationEvents o1, EducationEvents o2) {
                return o1.getStart_time().get(0).compareTo(o2.getStart_time().get(0));
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
//        ArrayList<String> descriptionVal = ;
        dialogTitle.setText(educationEvents.get(position).getTitle());
        registerNowBtn.setText(buttonText);
        if (buttonText.equalsIgnoreCase(getResources().getString(R.string.register_now))) {
            registerNowBtn.setEnabled(false);
            registerNowBtn.setTextColor(getResources().getColor(R.color.white));
            registerNowBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
        }
        dialogContent.setText(educationEvents.get(position).getLong_desc());

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                if (buttonText.equalsIgnoreCase(getResources().getString(R.string.register_now))) {
                    new Util().showComingSoonDialog(EducationCalendarActivity.this, R.string.coming_soon_content);
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

        cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, educationEvents.get(position).getTitle());
        cv.put(CalendarContract.Events.DESCRIPTION, educationEvents.get(position).getLong_desc());
        cv.put(CalendarContract.Events.DTSTART, educationEvents.get(position).getStart_time().get(0));
        cv.put(CalendarContract.Events.DTEND, educationEvents.get(position).getEnd_time().get(0));
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

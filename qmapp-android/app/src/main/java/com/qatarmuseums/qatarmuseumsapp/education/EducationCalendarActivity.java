package com.qatarmuseums.qatarmuseumsapp.education;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    ArrayList<Events> events = new ArrayList<>();
    String institutionFilter, ageGroupFilter, programmeTypeFilter;
    long selectedDate;
    boolean isDateSelected = false;
    Util util;
    Calendar calendarInstance;
    private QMDatabase qmDatabase;
    private Integer eventsTableRowCount;
    EducationalCalendarEventsTable educationalCalendarEventsTable;
    public static final String FILTERPREFS = "FilterPref";
    public static final String INSTITUTEPREFS = "InstitutionPref";
    public static final String AGEGROUPPREFS = "AgeGroupPref";
    public static final String PROGRAMMEPREFS = "ProgrammePref";
    private String appLanguage;
    private Calendar today;
    String day;
    String monthNumber;
    String year;
    private SimpleDateFormat dayDateFormat, monthDateFormat, yearDateFormat;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.education_calendar_activity_tittle));
        collapsibleCalendar = findViewById(R.id.collapsibleCalendarView);
        eventListView = findViewById(R.id.event_list);
        dayDateFormat = new SimpleDateFormat("d", Locale.US);
        monthDateFormat = new SimpleDateFormat("M", Locale.US);
        yearDateFormat = new SimpleDateFormat("yyyy", Locale.US);

        util = new Util();
        appLanguage = LocaleManager.getLanguage(this);
        qmDatabase = QMDatabase.getInstance(EducationCalendarActivity.this);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getDataOnline();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        backArrow.setOnClickListener(v -> {
            Timber.i("onBackPressed()");
            onBackPressed();
        });

        /* Filter option is disabled temporary
        toolbar_filter.setOnClickListener(view -> {
            Timber.i("Filter clicked");
            Intent intent = new Intent(EducationCalendarActivity.this, EducationFilterActivity.class);
            startActivity(intent);
        });
        */
        progress.setOnClickListener(view -> {
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
        educationAdapter = new EducationAdapter(EducationCalendarActivity.this, events);
        layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(educationAdapter);

        today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        day = dayDateFormat.format(today.getTime());
        monthNumber = monthDateFormat.format(today.getTime());
        year = yearDateFormat.format(today.getTime());
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
                Timber.i("onDaySelect()");
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
                    day = dayDateFormat.format(calendarInstance.getTime());
                    monthNumber = monthDateFormat.format(calendarInstance.getTime());
                    year = yearDateFormat.format(calendarInstance.getTime());
                    getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter,
                            programmeTypeFilter, monthNumber, day, year, selectedDate);
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
    }

    public void onClickCalled(Boolean registrationRequired, final ArrayList<Events> events, final int position) {
        Timber.i("Education Calendar event clicked with registration required: %b", registrationRequired);
        if (registrationRequired)
            util.showDialog(this, getResources().getString(R.string.register_now), events, position);
        else
            util.showDialog(this, getResources().getString(R.string.add_to_calendar), events, position);
    }

    public void getDataOnline() {
        if (isDateSelected) {
            eventListView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            day = dayDateFormat.format(calendarInstance.getTime());
            monthNumber = monthDateFormat.format(calendarInstance.getTime());
            year = yearDateFormat.format(calendarInstance.getTime());

            getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter,
                    monthNumber, day, year, selectedDate);
        } else {
            eventListView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter, programmeTypeFilter,
                    monthNumber, day, year, today.getTimeInMillis());
            isDateSelected = false;
        }
    }

    private void getEducationCalendarEventsFromDatabase(long timeStamp) {
        Timber.i("getEducationCalendarEventsFromDatabase()");
        progress.setVisibility(View.VISIBLE);
        educationAdapter.clear();
        new EducationCalendarActivity.EventsRowCount(EducationCalendarActivity.this, appLanguage, timeStamp / 1000).execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        institutionFilter = intent.getStringExtra("INSTITUTION");
        ageGroupFilter = intent.getStringExtra("AGE_GROUP");
        programmeTypeFilter = intent.getStringExtra("PROGRAMME_TYPE");

        if (util.isNetworkAvailable(EducationCalendarActivity.this)) {
            if (isDateSelected) {
                day = dayDateFormat.format(calendarInstance.getTime());
                monthNumber = monthDateFormat.format(calendarInstance.getTime());
                year = yearDateFormat.format(calendarInstance.getTime());

                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter,
                        programmeTypeFilter, monthNumber, day, year, selectedDate);
            } else {
                getEducationCalendarDataFromApi(institutionFilter, ageGroupFilter,
                        programmeTypeFilter, monthNumber, day, year, today.getTimeInMillis());
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

    private void getEducationCalendarDataFromApi(String institute, String ageGroup, String programmeType,
                                                 final String month, final String day,
                                                 final String year, final long timeStamp) {
        Timber.i("getEducationCalendarDataFromApi()");
        progress.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<Events>> call = apiService.
                getCalendarData(appLanguage, institute, ageGroup, programmeType, month, day, year, "field_eduprog_date");
        call.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getEducationCalendarDataFromApi() - isSuccessful with size: %s", response.body().size());
                        events.clear();
                        progress.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                        events.addAll(response.body());
                        util.removeHtmlTags(events);
                        util.updateTimeStamp(events, timeStamp, day, month, year);
                        educationAdapter.notifyDataSetChanged();
                        new EventsRowCount(EducationCalendarActivity.this, appLanguage,
                                timeStamp / 1000).execute();

                    } else {
                        Timber.i("getEducationCalendarDataFromApi() - have no data");
                        progress.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                        retryLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Timber.e("getEducationCalendarDataFromApi() - onFailure: %s", t.getMessage());
                eventListView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });

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
                Timber.i("Count: %d", integer);
                if (activityReference.get().events.size() > 0 &&
                        activityReference.get().util.isNetworkAvailable(activityReference.get()))
                    new CheckEventRowExist(activityReference.get(), language, timeStamp).execute();
                else {
                    new RetrieveTableData(activityReference.get(), 1,
                            timeStamp).execute();
                }
            } else if (activityReference.get().events.size() > 0) {
                Timber.i("%s Table have no data", activityReference.get().toolbar_title.getText());
                new InsertDatabaseTask(activityReference.get(),
                        activityReference.get().educationalCalendarEventsTable, language, timeStamp).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbar_title.getText());
                activityReference.get().progress.setVisibility(View.GONE);
                activityReference.get().eventListView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows%s()", activityReference.get().toolbar_title.getText(), language.toUpperCase());
            return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                    .getNumberOfRows(language);
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
            if (activityReference.get().events.size() > 0) {
                Timber.i("checkTableWithEventDateExist(%s) timestamp: %d", language.toUpperCase(), timestamp);
                int n = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .checkWithEventDateExist(String.valueOf(timestamp), language);
                if (n > 0) {
                    for (int i = 0; i < activityReference.get().events.size(); i++) {
                        int count = activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                                .checkWithEventIdExist(String.valueOf(timestamp),
                                        activityReference.get().events.get(i).getEid(), language);
                        if (count > 0) {
                            Timber.i("Event is Exist for %d", timestamp);
                            new UpdateEventsTableRow(activityReference.get(), language,
                                    i, timestamp, activityReference.get().events.get(i).getEid()).execute();
                        } else {
                            Timber.i("No Event for %d", timestamp);
                            new InsertSingleElementDatabaseTask(activityReference.get(),
                                    activityReference.get().educationalCalendarEventsTable, language, i, timestamp).execute();
                        }
                    }
                } else {
                    Timber.i("No Event for %d", timestamp);
                    new InsertDatabaseTask(activityReference.get(),
                            activityReference.get().educationalCalendarEventsTable,
                            language, timestamp).execute();
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
            Timber.i("Updating %s Table(%s) with id: %s", activityReference.get().toolbar_title.getText(),
                    language.toUpperCase(), activityReference.get().events.get(position).getEid());

            ArrayList<String> startDate;
            startDate = activityReference.get().events.get(position).getStartTime();
            ArrayList<String> endDate;
            endDate = activityReference.get().events.get(position).getEndTime();
            activityReference.get().qmDatabase.getEducationCalendarEventsDao().updateEvents(
                    activityReference.get().events.get(position).getTitle(),
                    activityReference.get().util.html2string(startDate.get(0)),
                    activityReference.get().util.html2string(endDate.get(0)),
                    activityReference.get().events.get(position).getRegistration(),
                    activityReference.get().events.get(position).getMaxGroupSize(),
                    activityReference.get().events.get(position).getShortDescription(),
                    activityReference.get().events.get(position).getLongDescription(),
                    activityReference.get().events.get(position).getLocation(),
                    activityReference.get().events.get(position).getCategory(),
                    activityReference.get().events.get(position).getEid(), language
            );
            return null;
        }
    }

    public static class InsertSingleElementDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<EducationCalendarActivity> activityReference;
        private EducationalCalendarEventsTable educationalCalendarEventsTable;
        String language;
        int position;
        long timestamp;

        public InsertSingleElementDatabaseTask(EducationCalendarActivity context,
                                               EducationalCalendarEventsTable educationalCalendarEventsTable,
                                               String language, int pos, long timestamp) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTable = educationalCalendarEventsTable;
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
            if (activityReference.get().events != null) {
                Timber.i("insert event on table(%s) with id: %s", language.toUpperCase(),
                        activityReference.get().events.get(position).getEid());
                Convertor converters = new Convertor();
                ArrayList<String> fieldValue = new ArrayList<String>();
                fieldValue = activityReference.get().events.get(position).getField();
                ArrayList<String> startDate = new ArrayList<String>();
                startDate = activityReference.get().events.get(position).getStartTime();
                ArrayList<String> endDate = new ArrayList<String>();
                endDate = activityReference.get().events.get(position).getEndTime();
                educationalCalendarEventsTable = new EducationalCalendarEventsTable(
                        activityReference.get().events.get(position).getEid(),
                        activityReference.get().events.get(position).getTitle(),
                        String.valueOf(timestamp),
                        activityReference.get().events.get(position).getInstitution(),
                        activityReference.get().events.get(position).getProgramType(),
                        activityReference.get().util.html2string(startDate.get(0)),
                        activityReference.get().util.html2string(endDate.get(0)),
                        activityReference.get().events.get(position).getRegistration(),
                        activityReference.get().events.get(position).getMaxGroupSize(),
                        activityReference.get().events.get(position).getShortDescription(),
                        activityReference.get().events.get(position).getLongDescription(),
                        activityReference.get().events.get(position).getLocation(),
                        activityReference.get().events.get(position).getCategory(),
                        activityReference.get().events.get(position).getFilter(),
                        activityReference.get().util.html2string(fieldValue.get(0)),
                        converters.fromArrayList(activityReference.get().events.get(position).getAgeGroup()),
                        converters.fromArrayList(activityReference.get().events.get(position).getAssociatedTopics()),
                        activityReference.get().events.get(position).getMuseumDepartment(),
                        language
                );
                activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        insertEventsTable(educationalCalendarEventsTable);

            }
            return true;
        }
    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<EducationCalendarActivity> activityReference;
        private EducationalCalendarEventsTable educationalCalendarEventsTable;
        String language;
        long timestamp;

        public InsertDatabaseTask(EducationCalendarActivity context,
                                  EducationalCalendarEventsTable educationalCalendarEventsTable,
                                  String language, long timestamp) {
            this.activityReference = new WeakReference<>(context);
            this.educationalCalendarEventsTable = educationalCalendarEventsTable;
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
            if (activityReference.get().events != null) {
                Timber.i("insert event on table(%s) with size: %d", language.toUpperCase(),
                        activityReference.get().events.size());
                Convertor converters = new Convertor();
                for (int i = 0; i < activityReference.get().events.size(); i++) {
                    Timber.i("insertTable with id: %s",
                            activityReference.get().events.get(i).getEid());
                    ArrayList<String> fieldValue;
                    fieldValue = activityReference.get().events.get(i).getField();
                    ArrayList<String> startDate;
                    startDate = activityReference.get().events.get(i).getStartTime();
                    ArrayList<String> endDate;
                    endDate = activityReference.get().events.get(i).getEndTime();
                    educationalCalendarEventsTable = new EducationalCalendarEventsTable(
                            activityReference.get().events.get(i).getEid(),
                            activityReference.get().events.get(i).getTitle(),
                            String.valueOf(timestamp),
                            activityReference.get().events.get(i).getInstitution(),
                            activityReference.get().events.get(i).getProgramType(),
                            activityReference.get().util.html2string(startDate.get(0)),
                            activityReference.get().util.html2string(endDate.get(0)),
                            activityReference.get().events.get(i).getRegistration(),
                            activityReference.get().events.get(i).getMaxGroupSize(),
                            activityReference.get().events.get(i).getShortDescription(),
                            activityReference.get().events.get(i).getLongDescription(),
                            activityReference.get().events.get(i).getLocation(),
                            activityReference.get().events.get(i).getCategory(),
                            activityReference.get().events.get(i).getFilter(),
                            activityReference.get().util.html2string(fieldValue.get(0)),
                            converters.fromArrayList(activityReference.get().events.get(i).getAgeGroup()),
                            converters.fromArrayList(activityReference.get().events.get(i).getAssociatedTopics()),
                            activityReference.get().events.get(i).getMuseumDepartment(), language
                    );
                    activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                            insertEventsTable(educationalCalendarEventsTable);

                }
            }
            return true;
        }
    }

    public static class RetrieveTableData extends AsyncTask<Void, Void, List<EducationalCalendarEventsTable>> {

        private WeakReference<EducationCalendarActivity> activityReference;
        int language;
        long eventDate;

        RetrieveTableData(EducationCalendarActivity context, int appLanguage, long eventDate) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.eventDate = eventDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<EducationalCalendarEventsTable> educationalCalendarEventsTables) {
            activityReference.get().events.clear();
            ArrayList<String> fieldValue = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (educationalCalendarEventsTables.size() > 0) {
                Timber.i("EducationCalendarEventsTable with size: %d", educationalCalendarEventsTables.size());
                for (int i = 0; i < educationalCalendarEventsTables.size(); i++) {
                    startDate.add(0, educationalCalendarEventsTables.get(i).getEvent_start_time());
                    endDate.add(0, educationalCalendarEventsTables.get(i).getEvent_end_time());
                    fieldValue.add(0, educationalCalendarEventsTables.get(i).getField());
                    Events eventsList = new Events(
                            educationalCalendarEventsTables.get(i).getEvent_id(),
                            educationalCalendarEventsTables.get(i).getFilter(),
                            educationalCalendarEventsTables.get(i).getEvent_title(),
                            educationalCalendarEventsTables.get(i).getEvent_short_description(),
                            educationalCalendarEventsTables.get(i).getEvent_long_description(),
                            educationalCalendarEventsTables.get(i).getLocation(),
                            educationalCalendarEventsTables.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            educationalCalendarEventsTables.get(i).getMax_group_size(),
                            educationalCalendarEventsTables.get(i).getEvent_program_type(),
                            educationalCalendarEventsTables.get(i).getCategory(),
                            educationalCalendarEventsTables.get(i).getEvent_registration(),
                            educationalCalendarEventsTables.get(i).getEvent_date(),
                            fieldValue,
                            converters.fromString(educationalCalendarEventsTables.get(i).getAge_group()),
                            converters.fromString(educationalCalendarEventsTables.get(i).getAssociated_topics()),
                            educationalCalendarEventsTables.get(i).getMuseum()
                    );
                    activityReference.get().events.add(i, eventsList);
                }
                activityReference.get().educationAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("EducationCalendarEventsTable have no data");
                activityReference.get().progress.setVisibility(View.GONE);
                activityReference.get().eventListView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }

        @Override
        protected List<EducationalCalendarEventsTable> doInBackground(Void... voids) {
            if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                Timber.i("getAllEventsWithDate(%d)", eventDate);
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getAllEvents(String.valueOf(eventDate), activityReference.get().appLanguage);
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                Timber.i("getProgrammeFilterEventsWithDate(%d)", eventDate);
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getProgrammeFilterEvents(String.valueOf(eventDate),
                                activityReference.get().programmeTypeFilter,
                                activityReference.get().appLanguage);
            } else if (activityReference.get().institutionFilter.equalsIgnoreCase("All") &&
                    activityReference.get().programmeTypeFilter.equalsIgnoreCase("All")) {
                Timber.i("getAgeGroupFilterEventsWithDate(%d)", eventDate);
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getAgeGroupFilterEvents(String.valueOf(eventDate), activityReference.get().appLanguage);
            } else if (activityReference.get().programmeTypeFilter.equalsIgnoreCase("All") &&
                    activityReference.get().ageGroupFilter.equalsIgnoreCase("All")) {
                Timber.i("getInstitutionFilterEventsWithDate(%d)", eventDate);
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao().
                        getInstitutionFilterEvents(String.valueOf(eventDate),
                                activityReference.get().institutionFilter,
                                activityReference.get().appLanguage);
            } else {
                Timber.i("getEventsWithDate(%d)", eventDate);
                return activityReference.get().qmDatabase.getEducationCalendarEventsDao()
                        .getEventsWithDate(String.valueOf(eventDate), activityReference.get().institutionFilter,
                                activityReference.get().programmeTypeFilter,
                                activityReference.get().appLanguage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("onRequestPermissionsResult() - PERMISSION_GRANTED");
                    util.insertEventToCalendar(this, null);
                } else {
                    Timber.i("onRequestPermissionsResult() - PERMISSION_NOT_GRANTED ");
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    }
                    if (!showRationale) {
                        util.showNavigationDialog(this, getString(R.string.permission_required),
                                getString(R.string.runtime_permission));
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            Timber.i("onActivityResult() - PERMISSION_GRANTED");
            util.insertEventToCalendar(this, null);
        }
    }

}

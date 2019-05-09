package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.qatarmuseums.qatarmuseumsapp.education.Events;
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

public class CalendarActivity extends AppCompatActivity {

    CollapsibleCalendar collapsibleCalendar;
    RecyclerView eventListView;
    CalendarAdapter calendarAdapter;
    ArrayList<Events> calendarEventList;
    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    ImageView backArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.no_events_txt)
    TextView noEventsTxt;
    @BindView(R.id.retry_layout)
    LinearLayout retryLayout;
    @BindView(R.id.retry_btn)
    Button retryButton;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBar;
    @BindView(R.id.progress)
    LinearLayout progress;
    @BindView(R.id.container)
    RelativeLayout layoutContainer;
    RecyclerView.LayoutManager layoutManager;
    APIInterface apiService;
    Calendar calendarInstance;

    private String language;
    private QMDatabase qmDatabase;
    CalendarEventsTable calendarEventsTable;
    private Util util;
    private Calendar today;
    String day;
    String monthNumber;
    String year;
    private SimpleDateFormat dayDateFormat, monthDateFormat, yearDateFormat;

    public CalendarActivity() {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.calendar_activity_tittle));
        qmDatabase = QMDatabase.getInstance(CalendarActivity.this);
        util = new Util();
        collapsibleCalendar = findViewById(R.id.collapsibleCalendarView);
        eventListView = findViewById(R.id.event_list);
        language = LocaleManager.getLanguage(this);
        dayDateFormat = new SimpleDateFormat("d", Locale.US);
        monthDateFormat = new SimpleDateFormat("M", Locale.US);
        yearDateFormat = new SimpleDateFormat("yyyy", Locale.US);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getCalendarEventsFromAPI(monthNumber, day, year, today.getTimeInMillis());
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

        progress.setOnClickListener(view -> {
            // To avoid background click while loading
        });

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
                day = dayDateFormat.format(calendarInstance.getTime());
                monthNumber = monthDateFormat.format(calendarInstance.getTime());
                year = yearDateFormat.format(calendarInstance.getTime());
                if (util.isNetworkAvailable(CalendarActivity.this))
                    getCalendarEventsFromAPI(monthNumber, day, year, calendarInstance.getTimeInMillis());
                else
                    getCalendarEventsFromDatabase(calendarInstance.getTimeInMillis());
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

        calendarEventList = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(CalendarActivity.this, calendarEventList);
        layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(calendarAdapter);

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

        today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        day = dayDateFormat.format(today.getTime());
        monthNumber = monthDateFormat.format(today.getTime());
        year = yearDateFormat.format(today.getTime());
        if (util.isNetworkAvailable(CalendarActivity.this))
            getCalendarEventsFromAPI(monthNumber, day, year, today.getTimeInMillis());
        else
            getCalendarEventsFromDatabase(today.getTimeInMillis());
    }

    public void getCalendarEventsFromDatabase(Long timeStamp) {
        Timber.i("getCalendarEventsFromDatabase()");
        progress.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
    }

    public void getCalendarEventsFromAPI(final String month, final String day, final String year, final long timeStamp) {
        Timber.i("getCalendarEventsFromAPI()");
        progress.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<Events>> call = apiService.getCalendarData(language,
                "All", "All", "All", month, day, year, "field_eduprog_date");
        call.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        Timber.i("getCalendarEventsFromAPI() - isSuccessful with size: %s", response.body().size());
                        noEventsTxt.setVisibility(View.GONE);
                        calendarEventList.addAll(response.body());
                        util.removeHtmlTags(calendarEventList);
                        util.updateTimeStamp(calendarEventList, timeStamp, day, month, year);
                        calendarAdapter.notifyDataSetChanged();
                        eventListView.setVisibility(View.VISIBLE);
                        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
                    } else {
                        Timber.i("getCalendarEventsFromAPI() - have no data");
                        noEventsTxt.setVisibility(View.VISIBLE);
                        noEventsTxt.setText(R.string.no_events);
                    }
                } else {
                    Timber.w("getCalendarEventsFromAPI() - response not successful");
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Timber.e("getCalendarEventsFromAPI() - onFailure: %s", t.getMessage());
                retryLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });

    }

    public void onClickCalled(Boolean registrationRequired, final ArrayList<Events> events, final int position) {
        Timber.i("Calendar event clicked with registration required: %b", registrationRequired);
        if (registrationRequired)
            util.showDialog(this, getResources().getString(R.string.register_now), events, position);
        else
            util.showDialog(this, getResources().getString(R.string.add_to_calendar), events, position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                        util.showNavigationDialog(this, getString(R.string.permission_required), getString(R.string.runtime_permission));
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

    public static class EventsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CalendarActivity> activityReference;
        String language;
        long timeStamp;

        EventsRowCount(CalendarActivity context, String apiLanguage, long timeStamp) {
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
            int eventsTableRowCount = integer;
            if (eventsTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                if (activityReference.get().calendarEventList.size() > 0 &&
                        activityReference.get().util.isNetworkAvailable(activityReference.get()))
                    new CheckEventRowExist(activityReference.get(), language, timeStamp).execute();
                else {
                    new RetrieveTableData(activityReference.get(), timeStamp).execute();
                }
            } else if (activityReference.get().calendarEventList.size() > 0) {
                Timber.i("Database table have no data");
                new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTable,
                        language, timeStamp).execute();
            } else {
                Timber.i("Database table have no data");
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                activityReference.get().progress.setVisibility(View.GONE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows%s()", activityReference.get().toolbar_title.getText(),
                    language.toUpperCase());
            return activityReference.get().qmDatabase.getCalendarEventsDao().getNumberOfRows(language);
        }
    }

    public static class CheckEventRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CalendarActivity> activityReference;
        String language;
        long timeStamp;

        CheckEventRowExist(CalendarActivity context, String apiLanguage, long timestamp) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            timeStamp = timestamp;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().calendarEventList.size() > 0) {
                Timber.i("checkTableWithEventDateExist(%s) timestamp: %d", language.toUpperCase(), timeStamp);
                int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkEventDateExist(
                        timeStamp, language);
                if (n > 0) {
                    Timber.i("Event is Exist for %d", timeStamp);
                    new DeleteEventsTableRow(activityReference.get(), language,
                            timeStamp).execute();
                } else {
                    Timber.i("No Event for %d", timeStamp);
                    new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTable,
                            language, timeStamp).execute();
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CalendarActivity> activityReference;
        private CalendarEventsTable calendarEventsTable;
        String language;
        long timestamp;

        InsertDatabaseTask(CalendarActivity context, CalendarEventsTable calendarEventsTable,
                           String lan, long time) {
            activityReference = new WeakReference<>(context);
            this.calendarEventsTable = calendarEventsTable;
            language = lan;
            timestamp = time;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().calendarEventList != null) {
                Timber.i("insert event on table(%s) with size: %d", language.toUpperCase(),
                        activityReference.get().calendarEventList.size());
                for (int i = 0; i < activityReference.get().calendarEventList.size(); i++) {
                    Timber.i("insertTable with id: %s",
                            activityReference.get().calendarEventList.get(i).getEid());
                    ArrayList<String> fieldValue = new ArrayList<String>();
                    fieldValue = activityReference.get().calendarEventList.get(i).getField();
                    ArrayList<String> startDate = new ArrayList<String>();
                    startDate = activityReference.get().calendarEventList.get(i).getStartTime();
                    ArrayList<String> endDate = new ArrayList<String>();
                    endDate = activityReference.get().calendarEventList.get(i).getEndTime();
                    Convertor converters = new Convertor();
                    calendarEventsTable = new CalendarEventsTable(
                            activityReference.get().calendarEventList.get(i).getEid(),
                            activityReference.get().calendarEventList.get(i).getTitle(),
                            activityReference.get().calendarEventList.get(i).getShortDescription(),
                            activityReference.get().calendarEventList.get(i).getLongDescription(),
                            activityReference.get().calendarEventList.get(i).getInstitution(),
                            activityReference.get().calendarEventList.get(i).getProgramType(),
                            activityReference.get().calendarEventList.get(i).getCategory(),
                            activityReference.get().calendarEventList.get(i).getLocation(),
                            String.valueOf(timestamp),
                            activityReference.get().util.html2string(startDate.get(0)),
                            activityReference.get().util.html2string(endDate.get(0)),
                            activityReference.get().calendarEventList.get(i).getRegistration(),
                            activityReference.get().calendarEventList.get(i).getFilter(),
                            activityReference.get().calendarEventList.get(i).getMaxGroupSize(),
                            activityReference.get().util.html2string(fieldValue.get(0)),
                            converters.fromArrayList(activityReference.get().calendarEventList.get(i).getAgeGroup()),
                            converters.fromArrayList(activityReference.get().calendarEventList.get(i).getAssociatedTopics()),
                            activityReference.get().calendarEventList.get(i).getMuseumDepartment(),
                            language
                    );
                    activityReference.get().qmDatabase.getCalendarEventsDao().
                            insertEventsTable(calendarEventsTable);

                }
            }
            return true;
        }

    }

    public static class DeleteEventsTableRow extends AsyncTask<Void, Void, Void> {
        private WeakReference<CalendarActivity> activityReference;
        String language;
        long timestamp;

        DeleteEventsTableRow(CalendarActivity context, String apiLanguage, long date) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            timestamp = date;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("DeleteEventsTableRow(%s) with timestamp: %d before insertion", language, timestamp);
            activityReference.get().qmDatabase.getCalendarEventsDao()
                    .deleteEventsWithDate(timestamp, language);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTable,
                    language, timestamp).execute();

        }
    }

    public static class RetrieveTableData extends AsyncTask<Void, Void, List<CalendarEventsTable>> {
        private WeakReference<CalendarActivity> activityReference;
        long eventDate;

        RetrieveTableData(CalendarActivity context, long eventDate) {
            activityReference = new WeakReference<>(context);
            this.eventDate = eventDate;
        }

        @Override
        protected List<CalendarEventsTable> doInBackground(Void... voids) {
            Timber.i("getEventsWithDate(%d)", eventDate);
            return activityReference.get().qmDatabase.getCalendarEventsDao()
                    .getEventsWithDate(eventDate, activityReference.get().language);

        }

        @Override
        protected void onPostExecute(List<CalendarEventsTable> eventsTableList) {
            activityReference.get().calendarEventList.clear();
            ArrayList<String> fieldValue = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (eventsTableList.size() > 0) {
                Timber.i("CalendarEventsTable with size: %d", eventsTableList.size());
                for (int i = 0; i < eventsTableList.size(); i++) {
                    Timber.i("Set calendar events from DB with id: %s", eventsTableList.get(i).getEvent_id());
                    startDate.add(0, eventsTableList.get(i).getEvent_start_time());
                    endDate.add(0, eventsTableList.get(i).getEvent_end_time());
                    fieldValue.add(0, eventsTableList.get(i).getField());
                    Events calendarEvents = new Events(
                            eventsTableList.get(i).getEvent_id(),
                            eventsTableList.get(i).getFilter(),
                            eventsTableList.get(i).getEvent_title(),
                            eventsTableList.get(i).getEvent_short_description(),
                            eventsTableList.get(i).getEvent_long_description(),
                            eventsTableList.get(i).getLocation(),
                            eventsTableList.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            eventsTableList.get(i).getMax_group_size(),
                            eventsTableList.get(i).getEvent_program_type(),
                            eventsTableList.get(i).getCategory(),
                            eventsTableList.get(i).getEvent_registration(),
                            eventsTableList.get(i).getEvent_date(),
                            fieldValue,
                            converters.fromString(eventsTableList.get(i).getAge_group()),
                            converters.fromString(eventsTableList.get(i).getAssociated_topics()),
                            eventsTableList.get(i).getMuseum()
                    );
                    activityReference.get().calendarEventList.add(i, calendarEvents);
                }
                activityReference.get().calendarAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                Timber.i("CalendarEventsTable have no data");
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }
    }
}

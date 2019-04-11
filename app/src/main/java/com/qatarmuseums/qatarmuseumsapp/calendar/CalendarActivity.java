package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
    int MY_PERMISSIONS_REQUEST_CALENDAR = 100;
    int REQUEST_PERMISSION_SETTING = 110;
    private LayoutInflater layoutInflater;
    private Dialog dialog;
    private ImageView closeBtn;
    private Button dialogActionButton;
    private TextView dialogTitle, dialogContent;
    private ContentResolver contentResolver;
    private ContentValues contentValues;
    private String language;
    private QMDatabase qmDatabase;
    CalendarEventsTableEnglish calendarEventsTableEnglish;
    CalendarEventsTableArabic calendarEventsTableArabic;
    private Util util;
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

        backArrow.setOnClickListener(v -> onBackPressed());

        progress.setOnClickListener(view -> {
            // To avoid background click while loading
        });

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

        calendarEventList = new ArrayList<Events>();
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
        progress.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
    }

    public void getCalendarEventsFromAPI(final String month, final String day, final String year, final long timeStamp) {
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
                        noEventsTxt.setVisibility(View.GONE);
                        calendarEventList.addAll(response.body());
                        util.removeHtmlTags(calendarEventList);
                        util.updateTimeStamp(calendarEventList, timeStamp, day, month, year);
                        calendarAdapter.notifyDataSetChanged();
                        eventListView.setVisibility(View.VISIBLE);
                        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
                    } else {
                        noEventsTxt.setVisibility(View.VISIBLE);
                        noEventsTxt.setText(R.string.no_events);
                    }
                } else {
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                retryLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });

    }

    public void onClickCalled(Boolean registrationRequired, final ArrayList<Events> events, final int position) {
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
                    util.insertEventToCalendar(this);
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

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        closeBtn = view.findViewById(R.id.close_dialog);
        dialogActionButton = view.findViewById(R.id.doneBtn);
        dialogTitle = view.findViewById(R.id.dialog_tittle);
        dialogContent = view.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogActionButton.setText(getResources().getString(R.string.open_settings));
        dialogContent.setText(details);

        dialogActionButton.setOnClickListener(view1 -> {
            navigateToSettings();
            dialog.dismiss();

        });
        closeBtn.setOnClickListener(view12 -> dialog.dismiss());
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
            util.insertEventToCalendar(this);
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
                if (activityReference.get().calendarEventList.size() > 0 &&
                        activityReference.get().util.isNetworkAvailable(activityReference.get()))
                    new CheckEventRowExist(activityReference.get(), language, timeStamp).execute();
                else {
                    if (language.equals("en")) {
                        new RetrieveEnglishTableData(activityReference.get(), timeStamp).execute();
                    } else {
                        new RetrieveArabicTableData(activityReference.get(), timeStamp).execute();
                    }
                }
            } else if (activityReference.get().calendarEventList.size() > 0) {
                new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTableEnglish,
                        activityReference.get().calendarEventsTableArabic, language, timeStamp).execute();
            } else {
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                activityReference.get().progress.setVisibility(View.GONE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getCalendarEventsDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getCalendarEventsDao().getNumberOfRowsArabic();

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
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkEnglishWithEventDateExist(
                            timeStamp);
                    if (n > 0) {
                        new DeleteEventsTableRow(activityReference.get(), language,
                                timeStamp).execute();
                    } else {
                        new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTableEnglish,
                                activityReference.get().calendarEventsTableArabic, language, timeStamp).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkArabicWithEventDateExist(
                            timeStamp);
                    if (n > 0) {
                        new DeleteEventsTableRow(activityReference.get(), language,
                                timeStamp).execute();
                    } else {
                        new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTableEnglish,
                                activityReference.get().calendarEventsTableArabic, language, timeStamp).execute();
                    }
                }
            }
            return null;
        }


    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CalendarActivity> activityReference;
        private CalendarEventsTableEnglish calendarEventsTableEnglish;
        private CalendarEventsTableArabic calendarEventsTableArabic;
        String language;
        long timestamp;

        InsertDatabaseTask(CalendarActivity context, CalendarEventsTableEnglish calendarEventsTableEnglish,
                           CalendarEventsTableArabic calendarEventsTableArabic, String lan, long time) {
            activityReference = new WeakReference<>(context);
            this.calendarEventsTableEnglish = calendarEventsTableEnglish;
            this.calendarEventsTableArabic = calendarEventsTableArabic;
            language = lan;
            timestamp = time;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().calendarEventList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().calendarEventList.size(); i++) {
                        ArrayList<String> fieldValue = new ArrayList<String>();
                        fieldValue = activityReference.get().calendarEventList.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = activityReference.get().calendarEventList.get(i).getStartTime();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = activityReference.get().calendarEventList.get(i).getEndTime();
                        Convertor converters = new Convertor();
                        calendarEventsTableEnglish = new CalendarEventsTableEnglish(
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
                                activityReference.get().calendarEventList.get(i).getMuseumDepartment()
                        );
                        activityReference.get().qmDatabase.getCalendarEventsDao().
                                insertEventsTableEnglish(calendarEventsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < activityReference.get().calendarEventList.size(); i++) {

                        ArrayList<String> fieldValue = new ArrayList<String>();
                        fieldValue = activityReference.get().calendarEventList.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = activityReference.get().calendarEventList.get(i).getStartTime();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = activityReference.get().calendarEventList.get(i).getEndTime();
                        Convertor converters = new Convertor();
                        calendarEventsTableArabic = new CalendarEventsTableArabic(
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
                                activityReference.get().calendarEventList.get(i).getMuseumDepartment()
                        );
                        activityReference.get().qmDatabase.getCalendarEventsDao().
                                insertEventsTableArabic(calendarEventsTableArabic);

                    }
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
            if (language.equals("en")) {
                activityReference.get().qmDatabase.getCalendarEventsDao().deleteEnglishEventsWithDate(
                        timestamp
                );

            } else {
                activityReference.get().qmDatabase.getCalendarEventsDao().deleteArabicEventsWithDate(
                        timestamp
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new InsertDatabaseTask(activityReference.get(), activityReference.get().calendarEventsTableEnglish,
                    activityReference.get().calendarEventsTableArabic, language, timestamp).execute();

        }
    }

    public static class RetrieveEnglishTableData extends AsyncTask<Void, Void, List<CalendarEventsTableEnglish>> {
        private WeakReference<CalendarActivity> activityReference;
        long eventDate;

        RetrieveEnglishTableData(CalendarActivity context, long eventDate) {
            activityReference = new WeakReference<>(context);
            this.eventDate = eventDate;
        }

        @Override
        protected List<CalendarEventsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getCalendarEventsDao().getEventsWithDateEnglish(eventDate);

        }

        @Override
        protected void onPostExecute(List<CalendarEventsTableEnglish> eventsTableEnglishList) {
            activityReference.get().calendarEventList.clear();
            ArrayList<String> fieldValue = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (eventsTableEnglishList.size() > 0) {
                for (int i = 0; i < eventsTableEnglishList.size(); i++) {
                    startDate.add(0, eventsTableEnglishList.get(i).getEvent_start_time());
                    endDate.add(0, eventsTableEnglishList.get(i).getEvent_end_time());
                    fieldValue.add(0, eventsTableEnglishList.get(i).getField());
                    Events calendarEvents = new Events(
                            eventsTableEnglishList.get(i).getEvent_id(),
                            eventsTableEnglishList.get(i).getFilter(),
                            eventsTableEnglishList.get(i).getEvent_title(),
                            eventsTableEnglishList.get(i).getEvent_short_description(),
                            eventsTableEnglishList.get(i).getEvent_long_description(),
                            eventsTableEnglishList.get(i).getLocation(),
                            eventsTableEnglishList.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            eventsTableEnglishList.get(i).getMax_group_size(),
                            eventsTableEnglishList.get(i).getEvent_program_type(),
                            eventsTableEnglishList.get(i).getCategory(),
                            eventsTableEnglishList.get(i).getEvent_registration(),
                            eventsTableEnglishList.get(i).getEvent_date(),
                            fieldValue,
                            converters.fromString(eventsTableEnglishList.get(i).getAge_group()),
                            converters.fromString(eventsTableEnglishList.get(i).getAssociated_topics()),
                            eventsTableEnglishList.get(i).getMuseum()
                    );
                    activityReference.get().calendarEventList.add(i, calendarEvents);
                }
                activityReference.get().calendarAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }
    }

    public static class RetrieveArabicTableData extends AsyncTask<Void, Void,
            List<CalendarEventsTableArabic>> {
        private WeakReference<CalendarActivity> activityReference;
        long eventDate;

        RetrieveArabicTableData(CalendarActivity context, long eventDate) {
            activityReference = new WeakReference<>(context);
            this.eventDate = eventDate;
        }

        @Override
        protected List<CalendarEventsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getCalendarEventsDao().getEventsWithDateArabic(eventDate);

        }

        @Override
        protected void onPostExecute(List<CalendarEventsTableArabic> eventsTableArabicList) {
            activityReference.get().calendarEventList.clear();
            ArrayList<String> fieldValue = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (eventsTableArabicList.size() > 0) {
                for (int i = 0; i < eventsTableArabicList.size(); i++) {
                    startDate.add(0, eventsTableArabicList.get(i).getEvent_start_time());
                    endDate.add(0, eventsTableArabicList.get(i).getEvent_end_time());
                    fieldValue.add(0, eventsTableArabicList.get(i).getField());
                    Events events = new Events(
                            eventsTableArabicList.get(i).getEvent_id(),
                            eventsTableArabicList.get(i).getFilter(),
                            eventsTableArabicList.get(i).getEvent_title(),
                            eventsTableArabicList.get(i).getEvent_short_description(),
                            eventsTableArabicList.get(i).getEvent_long_description(),
                            eventsTableArabicList.get(i).getLocation(),
                            eventsTableArabicList.get(i).getEvent_institution(),
                            startDate,
                            endDate,
                            eventsTableArabicList.get(i).getMax_group_size(),
                            eventsTableArabicList.get(i).getEvent_program_type(),
                            eventsTableArabicList.get(i).getCategory(),
                            eventsTableArabicList.get(i).getEvent_registration(),
                            eventsTableArabicList.get(i).getEvent_date(),
                            fieldValue,
                            converters.fromString(eventsTableArabicList.get(i).getAge_group()),
                            converters.fromString(eventsTableArabicList.get(i).getAssociated_topics()),
                            eventsTableArabicList.get(i).getMuseum()
                    );
                    activityReference.get().calendarEventList.add(i, events);
                }
                activityReference.get().calendarAdapter.notifyDataSetChanged();
                activityReference.get().eventListView.setVisibility(View.VISIBLE);
                activityReference.get().retryLayout.setVisibility(View.GONE);
            } else {
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progress.setVisibility(View.GONE);
        }
    }

}

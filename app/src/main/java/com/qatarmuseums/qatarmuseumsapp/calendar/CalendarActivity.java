package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
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
import android.widget.Toast;

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

public class CalendarActivity extends AppCompatActivity {

    CollapsibleCalendar collapsibleCalendar;
    RecyclerView eventListView;
    CalendarAdapter calendarAdapter;
    ArrayList<CalendarEvents> calendarEventList;
    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    ImageView backArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.no_events_txt)
    TextView noEventsTxt;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBar;
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
    private SharedPreferences qmPreferences;
    private String language;
    private int appLanguage;
    private QMDatabase qmDatabase;
    CalendarEventsTableEnglish calendarEventsTableEnglish;
    CalendarEventsTableArabic calendarEventsTableArabic;
    private Integer eventsTableRowCount;
    private Util util;
    private Calendar today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.calendar_activity_tittle));
        qmDatabase = QMDatabase.getInstance(CalendarActivity.this);
        util = new Util();
        collapsibleCalendar = (CollapsibleCalendar) findViewById(R.id.collapsibleCalendarView);
        eventListView = (RecyclerView) findViewById(R.id.event_list);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);

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

                if (util.isNetworkAvailable(CalendarActivity.this))
                    getCalendarEventsFromAPI(calendarInstance.getTimeInMillis());
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

        calendarEventList = new ArrayList<CalendarEvents>();
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
        today.set(Calendar.HOUR_OF_DAY, 14);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        if (util.isNetworkAvailable(CalendarActivity.this))
            getCalendarEventsFromAPI(today.getTimeInMillis());
        else
            getCalendarEventsFromDatabase(today.getTimeInMillis());

    }

    public void getCalendarEventsFromDatabase(Long timeStamp) {
        progressBar.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        String language;
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
    }

    public void getCalendarEventsFromAPI(Long timestamp) {
        progressBar.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        apiService = APIClient.getTempClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        Call<ArrayList<CalendarEvents>> call = apiService.getCalendarDetails(language, timestamp / 1000,
                "any", "any", "any");
        call.enqueue(new Callback<ArrayList<CalendarEvents>>() {
            @Override
            public void onResponse(Call<ArrayList<CalendarEvents>> call, Response<ArrayList<CalendarEvents>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        noEventsTxt.setVisibility(View.GONE);
                        calendarEventList.addAll(response.body());
                        if (!checkWednesdayOrSunday())
                            calendarEventList.add(getLocalEvent(calendarInstance.getTimeInMillis() / 1000));
                        updateTimeStamp();
                        sortEventsWithStartTime();
                        calendarAdapter.notifyDataSetChanged();
                        eventListView.setVisibility(View.VISIBLE);
                        new EventsRowCount(CalendarActivity.this, language, calendarInstance.getTimeInMillis()).execute();
                    } else {
                        noEventsTxt.setVisibility(View.VISIBLE);
                        noEventsTxt.setText(R.string.no_events);
                    }
                } else {
                    noEventsTxt.setVisibility(View.VISIBLE);
                    noEventsTxt.setText(R.string.no_events);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CalendarEvents>> call, Throwable t) {
                if (t instanceof IOException) {
                    new Util().showToast(getResources().getString(R.string.check_network), getApplicationContext());
                } else {
                    // error due to mapping issues
                }
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.no_events);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void updateTimeStamp() {
        for (int i = 0; i < calendarEventList.size(); i++) {
            calendarEventList.get(i).setEventDate(calendarInstance.getTimeInMillis() / 1000);
        }
    }

    public Boolean checkWednesdayOrSunday() {
        if (collapsibleCalendar.getSelectedItem() == null)
            calendarInstance = new GregorianCalendar();
        else {
            calendarInstance = Calendar.getInstance();
            calendarInstance.set(collapsibleCalendar.getSelectedItem().getYear(),
                    collapsibleCalendar.getSelectedItem().getMonth(),
                    collapsibleCalendar.getSelectedItem().getDay());
        }
        calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
        calendarInstance.set(Calendar.MINUTE, 0);
        calendarInstance.set(Calendar.SECOND, 0);
        if (calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return true;
        } else
            return false;
    }

    public CalendarEvents getLocalEvent(Long timeStamp) {
        CalendarEvents localEvent = new CalendarEvents(15476,
                getString(R.string.local_event_institution),
                getString(R.string.local_event_title),
                getString(R.string.local_event_short_description),
                getString(R.string.local_event_long_description),
                "14:00", "16:00", false, timeStamp,
                "no", "Museum of Islamic Art, Atrium", 40, "MIA",
                "Gallery", "Adults");
        return localEvent;
    }

    public void sortEventsWithStartTime() {
        Collections.sort(calendarEventList, new Comparator<CalendarEvents>() {
            @Override
            public int compare(CalendarEvents o1, CalendarEvents o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });
    }

    public void onClickCalled(Boolean registrationRequired, String title, String details) {
        if (registrationRequired)
            showRegisterDialog(title, details);
        else
            showCalendarDialog(title, details);
    }

    protected void showCalendarDialog(final String title, final String details) {

        dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogActionButton.setText(getResources().getString(R.string.add_to_calendar));
        dialogContent.setText(details);

        dialogActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCalendar(title, details);
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

    protected void showRegisterDialog(String title, final String details) {

        dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(title);
        dialogActionButton.setText(getResources().getString(R.string.register_now));
        dialogContent.setText(details);
        dialogActionButton.setEnabled(false);
        dialogActionButton.setTextColor(getResources().getColor(R.color.white));
        dialogActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
        dialogActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Util().showComingSoonDialog(CalendarActivity.this);

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

    private void addToCalendar(String title, String details) {
        calendarInstance.set(Calendar.HOUR_OF_DAY, 14);
        calendarInstance.set(Calendar.MINUTE, 0);
        contentResolver = getContentResolver();
        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DESCRIPTION, details);
        contentValues.put(CalendarContract.Events.DTSTART, calendarInstance.getTimeInMillis());
        contentValues.put(CalendarContract.Events.DTEND, calendarInstance.getTimeInMillis() + 2 * 60 * 60 * 1000);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            contentValues.put(CalendarContract.Events.CALENDAR_ID, 3);
        } else {
            contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        }
        TimeZone timeZone = TimeZone.getDefault();
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((CalendarActivity) this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_CALENDAR);

            } else {
                insertEventToCalendar();
            }
        } else
            insertEventToCalendar();

    }

    public void insertEventToCalendar() {
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
        Snackbar snackbar = Snackbar
                .make(layoutContainer, R.string.event_added, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) view.findViewById(R.id.dialog_content);
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

    public class EventsRowCount extends AsyncTask<Void, Void, Integer> {

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
            eventsTableRowCount = integer;
            if (eventsTableRowCount > 0) {
                if (calendarEventList.size() > 0 && util.isNetworkAvailable(CalendarActivity.this))
                    new CheckEventRowExist(CalendarActivity.this, language).execute();
                else {
                    if (language.equals("en")) {
                        new RetriveEnglishTableData(CalendarActivity.this, 1,
                                timeStamp).execute();
                    } else {
                        new RetriveArabicTableData(CalendarActivity.this, 2,
                                timeStamp).execute();
                    }
                }
            } else if (calendarEventList.size() > 0) {
                new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                        calendarEventsTableArabic, language).execute();
            } else {
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.no_events);
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

    public class CheckEventRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CalendarActivity> activityReference;
        String language;

        CheckEventRowExist(CalendarActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (calendarEventList.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkEnglishWithEventDateExist(
                            calendarEventList.get(0).getEventDate());
                    if (n > 0) {
                        new DeleteEventsTableRow(CalendarActivity.this, language,
                                calendarEventList.get(0).getEventDate()).execute();
                    } else {
                        new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                                calendarEventsTableArabic, language).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkArabicWithEventDateExist(
                            calendarEventList.get(0).getEventDate());
                    if (n > 0) {
                        new DeleteEventsTableRow(CalendarActivity.this, language,
                                calendarEventList.get(0).getEventDate()).execute();
                    } else {
                        new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                                calendarEventsTableArabic, language).execute();
                    }
                }
            }
            return null;
        }


    }


    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CalendarActivity> activityReference;
        private CalendarEventsTableEnglish calendarEventsTableEnglish;
        private CalendarEventsTableArabic calendarEventsTableArabic;
        String language;

        InsertDatabaseTask(CalendarActivity context, CalendarEventsTableEnglish calendarEventsTableEnglish,
                           CalendarEventsTableArabic calendarEventsTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.calendarEventsTableEnglish = calendarEventsTableEnglish;
            this.calendarEventsTableArabic = calendarEventsTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (calendarEventList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < calendarEventList.size(); i++) {
                        calendarEventsTableEnglish = new CalendarEventsTableEnglish(
                                calendarEventList.get(i).getEventId(),
                                calendarEventList.get(i).getEventTitle(),
                                calendarEventList.get(i).getEventTimings(),
                                calendarEventList.get(i).getEventDetails(),
                                calendarEventList.get(i).getInstitution(),
                                calendarEventList.get(i).getAgeGroup(),
                                calendarEventList.get(i).getProgramType(),
                                calendarEventList.get(i).getEventCategory(),
                                calendarEventList.get(i).getEventLocation(),
                                calendarEventList.get(i).getEventDate(),
                                calendarEventList.get(i).getStartTime(),
                                calendarEventList.get(i).getEndTime(),
                                calendarEventList.get(i).getRegistration(),
                                calendarEventList.get(i).getFilter(),
                                calendarEventList.get(i).getMaxGroupSize()
                        );
                        activityReference.get().qmDatabase.getCalendarEventsDao().
                                insertEventsTableEnglish(calendarEventsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < calendarEventList.size(); i++) {
                        calendarEventsTableArabic = new CalendarEventsTableArabic(
                                calendarEventList.get(i).getEventId(),
                                calendarEventList.get(i).getEventTitle(),
                                calendarEventList.get(i).getEventTimings(),
                                calendarEventList.get(i).getEventDetails(),
                                calendarEventList.get(i).getInstitution(),
                                calendarEventList.get(i).getAgeGroup(),
                                calendarEventList.get(i).getProgramType(),
                                calendarEventList.get(i).getEventCategory(),
                                calendarEventList.get(i).getEventLocation(),
                                calendarEventList.get(i).getEventDate(),
                                calendarEventList.get(i).getStartTime(),
                                calendarEventList.get(i).getEndTime(),
                                calendarEventList.get(i).getRegistration(),
                                calendarEventList.get(i).getFilter(),
                                calendarEventList.get(i).getMaxGroupSize()
                        );
                        activityReference.get().qmDatabase.getCalendarEventsDao().
                                insertEventsTableArabic(calendarEventsTableArabic);

                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            Toast.makeText(CalendarActivity.this, "Insertion Success", Toast.LENGTH_SHORT).show();
        }
    }


    public class DeleteEventsTableRow extends AsyncTask<Void, Void, Void> {
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
                        calendarEventList.get(0).getEventDate()
                );

            } else {
                activityReference.get().qmDatabase.getCalendarEventsDao().deleteArabicEventsWithDate(
                        calendarEventList.get(0).getEventDate()
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                    calendarEventsTableArabic, language).execute();

        }
    }

    public class RetriveEnglishTableData extends AsyncTask<Void, Void, List<CalendarEventsTableEnglish>> {
        private WeakReference<CalendarActivity> activityReference;
        int language;
        long eventDate;

        RetriveEnglishTableData(CalendarActivity context, int appLanguage, long eventDate) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.eventDate = eventDate;
        }

        @Override
        protected List<CalendarEventsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getCalendarEventsDao().getEventsWithDateEnglish(eventDate);

        }

        @Override
        protected void onPostExecute(List<CalendarEventsTableEnglish> eventsTableEnglishList) {
            calendarEventList.clear();
            if (eventsTableEnglishList.size() > 0) {
                for (int i = 0; i < eventsTableEnglishList.size(); i++) {
                    CalendarEvents calendarEvents = new CalendarEvents(
                            eventsTableEnglishList.get(i).getEvent_id(),
                            eventsTableEnglishList.get(i).getEvent_institution(),
                            eventsTableEnglishList.get(i).getEvent_title(),
                            eventsTableEnglishList.get(i).getEvent_short_description(),
                            eventsTableEnglishList.get(i).getEvent_long_description(),
                            eventsTableEnglishList.get(i).getEvent_start_time(),
                            eventsTableEnglishList.get(i).getEvent_end_time(),
                            eventsTableEnglishList.get(i).getEvent_registration(),
                            eventsTableEnglishList.get(i).getEvent_date(),
                            eventsTableEnglishList.get(i).getFilter(),
                            eventsTableEnglishList.get(i).getLocation(),
                            eventsTableEnglishList.get(i).getMax_group_size(),
                            eventsTableEnglishList.get(i).getCategory(),
                            eventsTableEnglishList.get(i).getEvent_age_group(),
                            eventsTableEnglishList.get(i).getEvent_program_type()
                    );
                    calendarEventList.add(i, calendarEvents);
                }
                calendarAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noEventsTxt.setVisibility(View.GONE);
            } else {
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.unable_to_fetch_details);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetriveArabicTableData extends AsyncTask<Void, Void,
            List<CalendarEventsTableArabic>> {
        private WeakReference<CalendarActivity> activityReference;
        int language;
        long eventDate;

        RetriveArabicTableData(CalendarActivity context, int appLanguage, long eventDate) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.eventDate = eventDate;
        }

        @Override
        protected List<CalendarEventsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getCalendarEventsDao().getEventsWithDateArabic(eventDate);

        }

        @Override
        protected void onPostExecute(List<CalendarEventsTableArabic> eventsTableArabicList) {
            calendarEventList.clear();
            if (eventsTableArabicList.size() > 0) {
                for (int i = 0; i < eventsTableArabicList.size(); i++) {
                    CalendarEvents calendarEvents = new CalendarEvents(
                            eventsTableArabicList.get(i).getEvent_id(),
                            eventsTableArabicList.get(i).getEvent_institution(),
                            eventsTableArabicList.get(i).getEvent_title(),
                            eventsTableArabicList.get(i).getEvent_short_description(),
                            eventsTableArabicList.get(i).getEvent_long_description(),
                            eventsTableArabicList.get(i).getEvent_start_time(),
                            eventsTableArabicList.get(i).getEvent_end_time(),
                            eventsTableArabicList.get(i).getEvent_registration(),
                            eventsTableArabicList.get(i).getEvent_date(),
                            eventsTableArabicList.get(i).getFilter(),
                            eventsTableArabicList.get(i).getLocation(),
                            eventsTableArabicList.get(i).getMax_group_size(),
                            eventsTableArabicList.get(i).getCategory(),
                            eventsTableArabicList.get(i).getEvent_age_group(),
                            eventsTableArabicList.get(i).getEvent_program_type()
                    );
                    calendarEventList.add(i, calendarEvents);
                }
                calendarAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noEventsTxt.setVisibility(View.GONE);
            } else {
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.unable_to_fetch_details);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}

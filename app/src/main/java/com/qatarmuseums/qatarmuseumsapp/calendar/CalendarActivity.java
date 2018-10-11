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
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private SharedPreferences qmPreferences;
    private String language;
    private int appLanguage;
    private QMDatabase qmDatabase;
    CalendarEventsTableEnglish calendarEventsTableEnglish;
    CalendarEventsTableArabic calendarEventsTableArabic;
    private Integer eventsTableRowCount;
    private Util util;
    private Calendar today;
    String day;
    String monthNumber;
    String year;

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

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
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
                day = (String) DateFormat.format("d", calendarInstance);
                monthNumber = (String) DateFormat.format("M", calendarInstance);
                year = (String) DateFormat.format("yyyy", calendarInstance);
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
        today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        day = (String) DateFormat.format("d", today);
        monthNumber = (String) DateFormat.format("M", today);
        year = (String) DateFormat.format("yyyy", today);
        if (util.isNetworkAvailable(CalendarActivity.this))
            getCalendarEventsFromAPI(monthNumber, day, year, today.getTimeInMillis());
        else
            getCalendarEventsFromDatabase(today.getTimeInMillis());
    }

    public void getCalendarEventsFromDatabase(Long timeStamp) {
        progress.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        String language;
        if (appLanguage == 1)
            language = "en";
        else
            language = "ar";
        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
    }

    public void getCalendarEventsFromAPI(final String month, final String day, final String year, final long timeStamp) {
        progress.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        apiService = APIClient.getClient().create(APIInterface.class);
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        Call<ArrayList<CalendarEvents>> call = apiService.getCalendarDetails(language,
                "All", "All", "All", month, day, year, "field_eduprog_date");
        call.enqueue(new Callback<ArrayList<CalendarEvents>>() {
            @Override
            public void onResponse(Call<ArrayList<CalendarEvents>> call, Response<ArrayList<CalendarEvents>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {

                        noEventsTxt.setVisibility(View.GONE);
                        calendarEventList.addAll(response.body());
                        removeHtmlTags(calendarEventList);
                        updateTimeStamp(timeStamp);
                        updateStartAndEndTime();
                        convertToTimstamp(day, month, year);
                        sortEventsWithStartTime();
                        calendarAdapter.notifyDataSetChanged();
                        eventListView.setVisibility(View.VISIBLE);
                        new EventsRowCount(CalendarActivity.this, language, timeStamp / 1000).execute();
                    } else {
                        noEventsTxt.setVisibility(View.VISIBLE);
                        noEventsTxt.setText(R.string.no_events);
                    }
                } else {
                    noEventsTxt.setVisibility(View.VISIBLE);
                    noEventsTxt.setText(R.string.no_events);
                }
                progress.setVisibility(View.GONE);

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
                progress.setVisibility(View.GONE);
            }
        });

    }

    public void removeHtmlTags(ArrayList<CalendarEvents> models) {
        for (int i = 0; i < models.size(); i++) {
            ArrayList<String> fieldval = models.get(i).getField();
            fieldval.set(0, util.html2string(fieldval.get(0)));
            models.get(i).setField(fieldval);
            ArrayList<String> startDateVal = models.get(i).getStartTime();
            startDateVal.set(0, util.html2string(startDateVal.get(0)));
            models.get(i).setStartTime(startDateVal);
            ArrayList<String> endDateVal = models.get(i).getEndTime();
            endDateVal.set(0, util.html2string(endDateVal.get(0)));
            models.get(i).setEndTime(endDateVal);
            models.get(i).setProgramType(util.html2string(models.get(i).getProgramType()));
            models.get(i).setEventDetails(util.html2string(models.get(i).getEventDetails()));
            models.get(i).setEventTitle(util.html2string(models.get(i).getEventTitle()));
        }
    }

    public void updateTimeStamp(long timestamp) {
        for (int i = 0; i < calendarEventList.size(); i++) {
            calendarEventList.get(i).setEventDate(timestamp / 1000);
        }
    }

    public void updateStartAndEndTime() {
        //extracting time from text and updating the time field
        for (int i = 0; i < calendarEventList.size(); i++) {
            String start = calendarEventList.get(i).getStartTime().get(0);
            String svalue = start.substring(start.lastIndexOf("-") + 1);
            String[] stimeArray = svalue.trim().split("-");
            String startTime = stimeArray[0].trim();
            String end = calendarEventList.get(i).getEndTime().get(0);
            String evalue = end.substring(end.lastIndexOf("-") + 1);
            String[] etimeArray = evalue.trim().split("-");
            String endTime = etimeArray[0].trim();
            ArrayList<String> startDateVal = new ArrayList<String>();
            startDateVal.add(0, startTime);
            ArrayList<String> endDateVal = new ArrayList<String>();
            endDateVal.add(0, endTime);
            calendarEventList.get(i).setStartTime(startDateVal);
            calendarEventList.get(i).setEndTime(endDateVal);
        }
    }

    public void convertToTimstamp(String day, String month, String year) {
//        converting time to timstamp and updating
        for (int i = 0; i < calendarEventList.size(); i++) {
            String str_start_date = day + "-" + month + "-" + year + " " + calendarEventList.get(i).getStartTime().get(0);
            String str_end_date = day + "-" + month + "-" + year + " " + calendarEventList.get(i).getEndTime().get(0);
            ArrayList<String> st = new ArrayList<String>();
            st.add(0, String.valueOf(convertDate(str_start_date)));
            ArrayList<String> en = new ArrayList<String>();
            en.add(0, String.valueOf(convertDate(str_end_date)));
            calendarEventList.get(i).setStartTime(st);
            calendarEventList.get(i).setEndTime(en);
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

    public void sortEventsWithStartTime() {
        Collections.sort(calendarEventList, new Comparator<CalendarEvents>() {
            @Override
            public int compare(CalendarEvents o1, CalendarEvents o2) {
                return o1.getStartTime().get(0).compareTo(o2.getStartTime().get(0));
            }
        });
    }

    public void onClickCalled(Boolean registrationRequired, String title, String details, String startDate, String endDate) {
        if (registrationRequired)
            showRegisterDialog(title, details);
        else
            showCalendarDialog(title, details, startDate, endDate);
    }

    public int getScreenheight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double height = displayMetrics.heightPixels;
        height = (height) * (0.75);
        return (int) height;
    }

    protected void showCalendarDialog(final String title, final String details, final String startDate, final String endDate) {

        dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);
        dialog.setContentView(view);
        FrameLayout contentLayout = (FrameLayout) view.findViewById(R.id.content_frame_layout);
        closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        int heightValue = getScreenheight();
        contentLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightValue));

        dialogTitle.setText(title);
        dialogActionButton.setText(getResources().getString(R.string.add_to_calendar));
        dialogContent.setText(details);

        dialogActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCalendar(title, details, startDate, endDate);
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
        FrameLayout contentLayout = (FrameLayout) view.findViewById(R.id.content_frame_layout);
        closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        dialogActionButton = (Button) view.findViewById(R.id.doneBtn);
        dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        int heightValue = getScreenheight();
        contentLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, heightValue));

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

    private void addToCalendar(String title, String details, String startDate, String endDate) {
        contentResolver = getContentResolver();
        contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.DESCRIPTION, details);
        contentValues.put(CalendarContract.Events.DTSTART, startDate);
        contentValues.put(CalendarContract.Events.DTEND, endDate);
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
                    new CheckEventRowExist(CalendarActivity.this, language, timeStamp).execute();
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
                        calendarEventsTableArabic, language, timeStamp).execute();
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
        long timeStamp;

        CheckEventRowExist(CalendarActivity context, String apiLanguage, long timestamp) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            timeStamp = timestamp;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (calendarEventList.size() > 0) {
                if (language.equals("en")) {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkEnglishWithEventDateExist(
                            timeStamp);
                    if (n > 0) {
                        new DeleteEventsTableRow(CalendarActivity.this, language,
                                timeStamp).execute();
                    } else {
                        new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                                calendarEventsTableArabic, language, timeStamp).execute();
                    }
                } else {
                    int n = activityReference.get().qmDatabase.getCalendarEventsDao().checkArabicWithEventDateExist(
                            timeStamp);
                    if (n > 0) {
                        new DeleteEventsTableRow(CalendarActivity.this, language,
                                timeStamp).execute();
                    } else {
                        new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                                calendarEventsTableArabic, language, timeStamp).execute();
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
            if (calendarEventList != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < calendarEventList.size(); i++) {
                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = calendarEventList.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = calendarEventList.get(i).getStartTime();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = calendarEventList.get(i).getEndTime();
                        Convertor converters = new Convertor();
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
                                timestamp,
                                util.html2string(startDate.get(0)),
                                util.html2string(endDate.get(0)),
                                calendarEventList.get(i).getRegistration(),
                                calendarEventList.get(i).getFilter(),
                                calendarEventList.get(i).getMaxGroupSize(),
                                util.html2string(fieldval.get(0)),
                                converters.fromArrayList(calendarEventList.get(i).getAge()),
                                converters.fromArrayList(calendarEventList.get(i).getAssociatedTopics()),
                                calendarEventList.get(i).getMuseumDepartment()
                        );
                        activityReference.get().qmDatabase.getCalendarEventsDao().
                                insertEventsTableEnglish(calendarEventsTableEnglish);

                    }
                } else {
                    for (int i = 0; i < calendarEventList.size(); i++) {

                        ArrayList<String> fieldval = new ArrayList<String>();
                        fieldval = calendarEventList.get(i).getField();
                        ArrayList<String> startDate = new ArrayList<String>();
                        startDate = calendarEventList.get(i).getStartTime();
                        ArrayList<String> endDate = new ArrayList<String>();
                        endDate = calendarEventList.get(i).getEndTime();
                        Convertor converters = new Convertor();
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
                                timestamp,
                                util.html2string(startDate.get(0)),
                                util.html2string(endDate.get(0)),
                                calendarEventList.get(i).getRegistration(),
                                calendarEventList.get(i).getFilter(),
                                calendarEventList.get(i).getMaxGroupSize(),
                                util.html2string(fieldval.get(0)),
                                converters.fromArrayList(calendarEventList.get(i).getAge()),
                                converters.fromArrayList(calendarEventList.get(i).getAssociatedTopics()),
                                calendarEventList.get(i).getMuseumDepartment()
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
            new InsertDatabaseTask(CalendarActivity.this, calendarEventsTableEnglish,
                    calendarEventsTableArabic, language, timestamp).execute();

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
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (eventsTableEnglishList.size() > 0) {
                for (int i = 0; i < eventsTableEnglishList.size(); i++) {
                    startDate.add(0, eventsTableEnglishList.get(i).getEvent_start_time());
                    endDate.add(0, eventsTableEnglishList.get(i).getEvent_end_time());
                    fieldval.add(0, eventsTableEnglishList.get(i).getField());
                    CalendarEvents calendarEvents = new CalendarEvents(
                            eventsTableEnglishList.get(i).getEvent_id(),
                            eventsTableEnglishList.get(i).getEvent_institution(),
                            eventsTableEnglishList.get(i).getEvent_title(),
                            eventsTableEnglishList.get(i).getEvent_short_description(),
                            eventsTableEnglishList.get(i).getEvent_long_description(),
                            startDate,
                            endDate,
                            eventsTableEnglishList.get(i).getEvent_registration(),
                            eventsTableEnglishList.get(i).getEvent_date(),
                            eventsTableEnglishList.get(i).getFilter(),
                            eventsTableEnglishList.get(i).getLocation(),
                            eventsTableEnglishList.get(i).getMax_group_size(),
                            eventsTableEnglishList.get(i).getCategory(),
                            eventsTableEnglishList.get(i).getEvent_age_group(),
                            eventsTableEnglishList.get(i).getEvent_program_type(),
                            fieldval,
                            converters.fromString(eventsTableEnglishList.get(i).getAge()),
                            converters.fromString(eventsTableEnglishList.get(i).getAssociatedTopics()),
                            eventsTableEnglishList.get(i).getMuseum()
                    );
                    calendarEventList.add(i, calendarEvents);
                }
                calendarAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noEventsTxt.setVisibility(View.GONE);
            } else {
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.no_events);
            }
            progress.setVisibility(View.GONE);
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
            ArrayList<String> fieldval = new ArrayList<String>();
            ArrayList<String> startDate = new ArrayList<String>();
            ArrayList<String> endDate = new ArrayList<String>();
            Convertor converters = new Convertor();
            if (eventsTableArabicList.size() > 0) {
                for (int i = 0; i < eventsTableArabicList.size(); i++) {
                    startDate.add(0, eventsTableArabicList.get(i).getEvent_start_time());
                    endDate.add(0, eventsTableArabicList.get(i).getEvent_end_time());
                    fieldval.add(0, eventsTableArabicList.get(i).getField());
                    CalendarEvents calendarEvents = new CalendarEvents(
                            eventsTableArabicList.get(i).getEvent_id(),
                            eventsTableArabicList.get(i).getEvent_institution(),
                            eventsTableArabicList.get(i).getEvent_title(),
                            eventsTableArabicList.get(i).getEvent_short_description(),
                            eventsTableArabicList.get(i).getEvent_long_description(),
                            startDate,
                            endDate,
                            eventsTableArabicList.get(i).getEvent_registration(),
                            eventsTableArabicList.get(i).getEvent_date(),
                            eventsTableArabicList.get(i).getFilter(),
                            eventsTableArabicList.get(i).getLocation(),
                            eventsTableArabicList.get(i).getMax_group_size(),
                            eventsTableArabicList.get(i).getCategory(),
                            eventsTableArabicList.get(i).getEvent_age_group(),
                            eventsTableArabicList.get(i).getEvent_program_type(),
                            fieldval,
                            converters.fromString(eventsTableArabicList.get(i).getAge()),
                            converters.fromString(eventsTableArabicList.get(i).getAssociatedTopics()),
                            eventsTableArabicList.get(i).getMuseum()
                    );
                    calendarEventList.add(i, calendarEvents);
                }
                calendarAdapter.notifyDataSetChanged();
                eventListView.setVisibility(View.VISIBLE);
                noEventsTxt.setVisibility(View.GONE);
            } else {
                noEventsTxt.setVisibility(View.VISIBLE);
                noEventsTxt.setText(R.string.no_events);
            }
            progress.setVisibility(View.GONE);
        }
    }

}

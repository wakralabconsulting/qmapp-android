package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
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

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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
    Date selectedDateTimeStamp;
    NumberFormat numberFormat;
    String selectedDate;
    private SimpleDateFormat simpleDateFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.calendar_activity_tittle));
        collapsibleCalendar = (CollapsibleCalendar) findViewById(R.id.collapsibleCalendarView);
        eventListView = (RecyclerView) findViewById(R.id.event_list);
        numberFormat = new DecimalFormat("00");
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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

        Calendar today = new GregorianCalendar();
        collapsibleCalendar.addEventTag(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        today.add(Calendar.DATE, 1);
        collapsibleCalendar.addEventTag(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), Color.BLUE);

        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                selectedDate = collapsibleCalendar.getSelectedItem().getDay() + "-" +
                        numberFormat.format(collapsibleCalendar.getSelectedItem().getMonth()) + "-" +
                        collapsibleCalendar.getSelectedItem().getYear();
                try {
                    selectedDateTimeStamp = (Date) simpleDateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                getCalendarEventsFromAPI(selectedDateTimeStamp.getTime());
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

        getCalendarEventsFromAPI(today.getTimeInMillis());
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

    public Boolean checkWednesdayOrSunday() {
        if (collapsibleCalendar.getSelectedItem() == null)
            calendarInstance = new GregorianCalendar();
        else {
            calendarInstance = Calendar.getInstance();
            calendarInstance.set(collapsibleCalendar.getSelectedItem().getYear(),
                    collapsibleCalendar.getSelectedItem().getMonth(),
                    collapsibleCalendar.getSelectedItem().getDay());
        }
        if (calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                calendarInstance.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            return true;
        } else
            return false;
    }

    public CalendarEvents getLocalEvent(Long timeStamp) {
        CalendarEvents localEvent = new CalendarEvents("MIA",
                "Walk In Gallery Tours",
                "Join our Museum Guides for a tour of the Museum of Islamic Art's oustanding collection of objects, spread over 1,400 years and across three continents. No booking is required to be a part of the tour.",
                "Monday - Science Tour" +
                        "\n\n" +
                        "Tuesday - Techniques Tour (from 1 July onwards)" +
                        "\n\n" +
                        "Thursday - MIA Architecture Tour" +
                        "\n\n" +
                        "Friday - Permanent Gallery Tour" +
                        "\n\n" +
                        "Saturday - Permanent Gallery Tour",
                "14:00",
                "16:00",
                false,
                timeStamp);
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

    public void getCalendarEventsFromAPI(Long timestamp) {
        progressBar.setVisibility(View.VISIBLE);
        calendarAdapter.clear();
        apiService = APIClient.getTempClient().create(APIInterface.class);
        final String language;
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
                            calendarEventList.add(getLocalEvent(calendarInstance.getTimeInMillis()));
                        sortEventsWithStartTime();
                        calendarAdapter.notifyDataSetChanged();
                        eventListView.setVisibility(View.VISIBLE);
                    } else {
                        noEventsTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    noEventsTxt.setVisibility(View.VISIBLE);
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
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}

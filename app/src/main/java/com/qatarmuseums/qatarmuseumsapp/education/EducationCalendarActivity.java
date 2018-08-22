package com.qatarmuseums.qatarmuseumsapp.education;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    SharedPreferences datePref;
    SharedPreferences.Editor editor;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.education_calendar_activity_tittle));
        collapsibleCalendar = (CollapsibleCalendar) findViewById(R.id.collapsibleCalendarView);
        eventListView = (RecyclerView) findViewById(R.id.event_list);

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

        Calendar today = new GregorianCalendar();
        collapsibleCalendar.addEventTag(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        today.add(Calendar.DATE, 1);
        collapsibleCalendar.addEventTag(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), Color.BLUE);

        String mDate = collapsibleCalendar.getSelectedDay().getDay() + "/"
                + collapsibleCalendar.getSelectedDay().getMonth()
                + "/" + collapsibleCalendar.getSelectedDay().getYear();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date) dateFormat.parse(mDate);
            todayDate = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (institutionFilter == null) {
            institutionFilter = "any";
        }
        if (ageGroupFilter == null) {
            ageGroupFilter = "any";
        }
        if (programmeTypeFilter == null) {
            programmeTypeFilter = "any";
        }
        if (isDateSelected) {
            eventListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getEducationCalendarDataFromApi(selectedDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
        } else {
            eventListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getEducationCalendarDataFromApi(todayDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
            isDateSelected = false;
        }


        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {

                Day day = collapsibleCalendar.getSelectedDay();
                String selected = day.getDay() + "/" + (day.getMonth() + 1) + "/" + day.getYear();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;
                try {
                    date = (Date) dateFormat.parse(selected);
                    selectedDate = date.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isDateSelected = true;
                getEducationCalendarDataFromApi(selectedDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
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

        educationAdapter = new EducationAdapter(EducationCalendarActivity.this, educationEvents);
        layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(educationAdapter);

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        institutionFilter = intent.getStringExtra("INSTITUTION");
        ageGroupFilter = intent.getStringExtra("AGE_GROUP");
        programmeTypeFilter = intent.getStringExtra("PROGRAMME_TYPE");
        if (isDateSelected) {
            getEducationCalendarDataFromApi(selectedDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
            eventListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            getEducationCalendarDataFromApi(todayDate, institutionFilter, ageGroupFilter, programmeTypeFilter);
            isDateSelected = false;
            eventListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private void getEducationCalendarDataFromApi(final long date, String institute, String ageGroup, String programmeType) {
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getTempClient().create(APIInterface.class);
        Call<ArrayList<EducationEvents>> call = apiService.
                getEducationCalendarDetails(date, institute, ageGroup, programmeType);

        call.enqueue(new Callback<ArrayList<EducationEvents>>() {
            @Override
            public void onResponse(Call<ArrayList<EducationEvents>> call, Response<ArrayList<EducationEvents>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        educationEvents.clear();
                        progressBar.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                        educationEvents.addAll(response.body());
                        boolean checkResult = checkWednesdayorSunday(date);
                        if (!checkResult) {
                            educationEvents.add(new EducationEvents("15476", null,
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
                                    "40", "adults", "gallery tour",
                                    "MIA", "false", String.valueOf(date)
                            ));
                        }
                        educationAdapter.notifyDataSetChanged();

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

    private boolean checkWednesdayorSunday(long day) {

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day);
        String dateValue = formatter.format(calendar.getTime());
        if (dateValue.equals("Wednesday")) {
            return true;
        } else if (dateValue.equals("Sunday")) {
            return true;
        } else {
            return false;
        }
    }
}

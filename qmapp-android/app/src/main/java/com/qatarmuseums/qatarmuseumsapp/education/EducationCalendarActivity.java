package com.qatarmuseums.qatarmuseumsapp.education;

import android.content.Intent;
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
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

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

        System.out.println("Testing date "
                + collapsibleCalendar.getSelectedDay().getDay() + "/"
                + collapsibleCalendar.getSelectedDay().getMonth()
                + "/" + collapsibleCalendar.getSelectedDay().getYear());

        String mDate = collapsibleCalendar.getSelectedDay().getDay() + "/"
                + collapsibleCalendar.getSelectedDay().getMonth()
                + "/" + collapsibleCalendar.getSelectedDay().getYear();
        System.out.println("Testing date " + mDate);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date) dateFormat.parse(mDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Testing date " + date.getTime());
        getEducationCalendarDataFromApi(date.getTime(), "any", "any", "any");


        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {

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


    private void getEducationCalendarDataFromApi(long date, String institute, String ageGroup, String programmeType) {
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
                        progressBar.setVisibility(View.GONE);
                        educationEvents.addAll(response.body());
                        educationAdapter.notifyDataSetChanged();
                        System.out.println("check "+educationEvents);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EducationEvents>> call, Throwable t) {

            }
        });

    }
}

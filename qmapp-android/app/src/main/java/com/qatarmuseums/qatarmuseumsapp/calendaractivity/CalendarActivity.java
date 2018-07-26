package com.qatarmuseums.qatarmuseumsapp.calendaractivity;

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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.calendar_activity_tittle));
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


//        recyclerview
        calendarEventList = new ArrayList<CalendarEvents>();

        CalendarEvents events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);
        events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);
        events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);
        events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);
        events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);
        events = new CalendarEvents(getResources().getString(R.string.event_text_first)
                , getResources().getString(R.string.event_text_second),
                getResources().getString(R.string.event_timing_text),
                getResources().getString(R.string.event_detail_text));
        calendarEventList.add(events);


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

    }
}

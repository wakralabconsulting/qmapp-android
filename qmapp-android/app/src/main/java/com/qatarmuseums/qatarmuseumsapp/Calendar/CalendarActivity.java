package com.qatarmuseums.qatarmuseumsapp.Calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarActivity extends AppCompatActivity {

    CollapsibleCalendar collapsibleCalendar;
    RecyclerView eventListView;
    CalendarAdapter calendarAdapter;
    ArrayList<CalendarEvents> calendarEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        collapsibleCalendar = (CollapsibleCalendar) findViewById(R.id.collapsibleCalendarView);
        eventListView = (RecyclerView) findViewById(R.id.event_list);

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
        calendarEventList=new ArrayList<CalendarEvents>();

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


        calendarAdapter = new CalendarAdapter(getApplication(), calendarEventList);
//        calendarAdapter=new CalendarAdapter(getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        eventListView.setLayoutManager(layoutManager);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setAdapter(calendarAdapter);


    }
}

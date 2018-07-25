package com.qatarmuseums.qatarmuseumsapp.Calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by MoongedePC on 23-Jul-18.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarAdapterViewHolder> {
    Context context;
    ArrayList<CalendarEvents> calendarEventsList;

    public CalendarAdapter(Context context, ArrayList<CalendarEvents> calendarEventsList) {
        this.context = context;
        this.calendarEventsList = calendarEventsList;
    }

    public CalendarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.event_row_layout, null);
        final CalendarAdapter.CalendarAdapterViewHolder calendarAdapterViewHolder = new CalendarAdapter.CalendarAdapterViewHolder(itemView);
        return new CalendarAdapter.CalendarAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.CalendarAdapterViewHolder holder, int position) {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        Calendar dt = Calendar.getInstance();

// Where untilDate is a date instance of your choice, for example 30/01/2012
//                dt.setTime(untilDate);

        // If you want the event until 30/01/2012, you must add one day from our day because
        // UNTIL in RRule sets events before the last day
        dt.add(Calendar.DATE, 1);
        final String dtUntill = yyyyMMdd.format(dt.getTime());
        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = context.getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(CalendarContract.Events.TITLE, "Birthday Celebration");
                cv.put(CalendarContract.Events.DESCRIPTION, "DESCRIPTION OF THE EVENT");
                cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
                cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
                // Default calendar
                cv.put(CalendarContract.Events.CALENDAR_ID, 1);
                TimeZone timeZone = TimeZone.getDefault();
                cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

//
//                cv.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
//                        + dtUntill);
// Set Period for 1 Hour
//                cv.put(CalendarContract.Events.DURATION, "+P1H");
//
//                cv.put(CalendarContract.Events.HAS_ALARM, 1);

// Insert event to calendar
                Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
                Toast.makeText(context,"entered",Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return calendarEventsList.size();
    }

    public class CalendarAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventSubTitle, eventDetails, eventTiming, viewDetails;

        public CalendarAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventSubTitle = (TextView) itemView.findViewById(R.id.event_subtitle);
            eventDetails = (TextView) itemView.findViewById(R.id.event_detail_text);
            eventTiming = (TextView) itemView.findViewById(R.id.event_timing);
            viewDetails = (TextView) itemView.findViewById(R.id.event_view_details);

        }
    }
}

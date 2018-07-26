package com.qatarmuseums.qatarmuseumsapp.calendaractivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;

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
    public void onBindViewHolder(@NonNull final CalendarAdapter.CalendarAdapterViewHolder holder, int position) {

        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(holder.eventDetails.getText().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return calendarEventsList.size();
    }

    public class CalendarAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventSubTitle, eventDetails, eventTiming, viewDetails;
        public LinearLayout layoutHolder;

        public CalendarAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventSubTitle = (TextView) itemView.findViewById(R.id.event_subtitle);
            eventDetails = (TextView) itemView.findViewById(R.id.event_detail_text);
            eventTiming = (TextView) itemView.findViewById(R.id.event_timing);
            viewDetails = (TextView) itemView.findViewById(R.id.event_view_details);
            layoutHolder=(LinearLayout)itemView.findViewById(R.id.layout_holder);

        }
    }


    protected void showDialog(final String details) {

        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button changeLanguageBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(context.getResources().getString(R.string.calendar_dialog_title));
        changeLanguageBtn.setText(context.getResources().getString(R.string.add_to_calendar));
        dialogContent.setText(details);

        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                addToCalendar(details);
                dialog.dismiss();

            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void addToCalendar(String details) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, "Title of the event");
        cv.put(CalendarContract.Events.DESCRIPTION, details);
        cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
        // Default calendar
        cv.put(CalendarContract.Events.CALENDAR_ID, 1);
        TimeZone timeZone = TimeZone.getDefault();
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        // Insert event to calendar
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
        Toast.makeText(context, "entered", Toast.LENGTH_SHORT).show();

    }
}

package com.qatarmuseums.qatarmuseumsapp.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

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
        final CalendarAdapter.CalendarAdapterViewHolder calendarAdapterViewHolder= new CalendarAdapter.CalendarAdapterViewHolder(itemView);
        return new CalendarAdapter.CalendarAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.CalendarAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return calendarEventsList.size();
    }

    public class CalendarAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle,eventSubTitle,eventDetails,eventTiming,viewDetails;

        public CalendarAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle=(TextView)itemView.findViewById(R.id.event_title);
            eventSubTitle=(TextView)itemView.findViewById(R.id.event_subtitle);
            eventDetails=(TextView)itemView.findViewById(R.id.event_detail_text);
            eventTiming=(TextView)itemView.findViewById(R.id.event_timing);
            viewDetails=(TextView)itemView.findViewById(R.id.event_view_details);

        }
    }
}

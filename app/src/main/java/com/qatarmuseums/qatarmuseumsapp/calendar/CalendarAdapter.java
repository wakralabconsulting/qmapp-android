package com.qatarmuseums.qatarmuseumsapp.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.education.Events;

import java.util.ArrayList;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarAdapterViewHolder> {
    private Context context;
    private ArrayList<Events> calendarEventsList;

    CalendarAdapter(Context context, ArrayList<Events> calendarEventsList) {
        this.context = context;
        this.calendarEventsList = calendarEventsList;
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams")
        View itemView = LayoutInflater.from(context).inflate(R.layout.education_event_row_layout, null);
        new CalendarAdapter.CalendarAdapterViewHolder(itemView);
        return new CalendarAdapter.CalendarAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendarAdapter.CalendarAdapterViewHolder holder, final int position) {
        if (calendarEventsList.size() > 0) {
            if (calendarEventsList.get(position).getInstitution() != null &&
                    !calendarEventsList.get(position).getInstitution().equals("")) {
                holder.eventTitle.setVisibility(View.VISIBLE);
                holder.titleHyphen.setVisibility(View.VISIBLE);
                holder.eventTitle.setText(calendarEventsList.get(position).getInstitution());
            }
            holder.eventSubTitle.setText(calendarEventsList.get(position).getTitle());
            holder.eventTiming.setText(calendarEventsList.get(position).getShortDescription());
        }

        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }

        holder.layoutHolder.setOnClickListener(view -> ((CalendarActivity) context).onClickCalled(
                Boolean.valueOf(calendarEventsList.get(position).getRegistration()),
                calendarEventsList,
                position));
    }

    @Override
    public int getItemCount() {
        return calendarEventsList.size();
    }

    class CalendarAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle, titleHyphen, eventSubTitle, eventTiming, viewDetails;
        LinearLayout layoutHolder;

        CalendarAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            titleHyphen = itemView.findViewById(R.id.event_hiphen);
            eventSubTitle = itemView.findViewById(R.id.event_subtitle);
            eventTiming = itemView.findViewById(R.id.event_timing);
            viewDetails = itemView.findViewById(R.id.event_view_details);
            layoutHolder = itemView.findViewById(R.id.layout_holder);
        }
    }

    public void clear() {
        final int size = calendarEventsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                calendarEventsList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

}

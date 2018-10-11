package com.qatarmuseums.qatarmuseumsapp.calendar;

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

import java.util.ArrayList;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarAdapterViewHolder> {
    Context context;
    ArrayList<CalendarEvents> calendarEventsList;
    ArrayList<String> descriptionVal;

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

        View itemView = LayoutInflater.from(context).inflate(R.layout.education_event_row_layout, null);
        final CalendarAdapter.CalendarAdapterViewHolder calendarAdapterViewHolder = new CalendarAdapter.CalendarAdapterViewHolder(itemView);
        return new CalendarAdapter.CalendarAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendarAdapter.CalendarAdapterViewHolder holder, final int position) {
        if (calendarEventsList.size() > 0) {
            holder.eventTitle.setText(calendarEventsList.get(position).getInstitution());
            holder.eventSubTitle.setText(calendarEventsList.get(position).getEventTitle());
            holder.eventTiming.setText(calendarEventsList.get(position).getEventTimings());
        }

        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
//        holder.viewDetails.setText();
        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity) context).onClickCalled(calendarEventsList.get(position).getRegistration(),
                        holder.eventSubTitle.getText().toString(),
                        calendarEventsList.get(position).getEventDetails(),calendarEventsList.get(position).getStartTime().get(0),
                        calendarEventsList.get(position).getEndTime().get(0));
            }
        });
    }

    @Override
    public int getItemCount() {
        return calendarEventsList.size();
    }

    public class CalendarAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventSubTitle, eventTiming, viewDetails;
        public LinearLayout layoutHolder;

        public CalendarAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventSubTitle = (TextView) itemView.findViewById(R.id.event_subtitle);
            eventTiming = (TextView) itemView.findViewById(R.id.event_timing);
            viewDetails = (TextView) itemView.findViewById(R.id.event_view_details);
            layoutHolder = (LinearLayout) itemView.findViewById(R.id.layout_holder);

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

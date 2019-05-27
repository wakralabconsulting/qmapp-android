package com.qatarmuseums.qatarmuseumsapp.education;

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

import java.util.ArrayList;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationAdapterViewHolder> {

    private Context context;
    private ArrayList<Events> events;
    ArrayList<String> descriptionVal;

    EducationAdapter(Context context, ArrayList<Events> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EducationAdapter.EducationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View itemView = LayoutInflater.from(context).inflate(R.layout.education_event_row_layout, null);
        final EducationAdapter.EducationAdapterViewHolder educationAdapterViewHolder = new EducationAdapter.EducationAdapterViewHolder(itemView);
        return new EducationAdapter.EducationAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final EducationAdapter.EducationAdapterViewHolder holder, final int position) {

        if (events.size() > 0) {
            holder.eventTitle.setText(events.get(position).getInstitution());
            holder.eventSubTitle.setText(events.get(position).getTitle());
            holder.eventTiming.setText(events.get(position).getShortDescription());
        }
        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
        holder.layoutHolder.setOnClickListener(view -> ((EducationCalendarActivity) context).onClickCalled(
                Boolean.valueOf(events.get(position).getRegistration()),
                events,
                position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public void clear() {
        final int size = events.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                events.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


    class EducationAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle, eventSubTitle, eventTiming, viewDetails;
        LinearLayout layoutHolder;

        EducationAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventSubTitle = itemView.findViewById(R.id.event_subtitle);
            eventTiming = itemView.findViewById(R.id.event_timing);
            viewDetails = itemView.findViewById(R.id.event_view_details);
            layoutHolder = itemView.findViewById(R.id.layout_holder);
        }
    }

}
   
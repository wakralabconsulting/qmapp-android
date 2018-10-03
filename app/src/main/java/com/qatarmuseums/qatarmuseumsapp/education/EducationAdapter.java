package com.qatarmuseums.qatarmuseumsapp.education;

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

/**
 * Created by MoongedePC on 26-Jul-18.
 */

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationAdapterViewHolder> {

    Context context;
    ArrayList<EducationEvents> educationEvents;
    ArrayList<String> descriptionVal;

    public EducationAdapter(Context context, ArrayList<EducationEvents> educationEvents) {
        this.context = context;
        this.educationEvents = educationEvents;
    }

    @NonNull
    @Override
    public EducationAdapter.EducationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.education_event_row_layout, null);
        final EducationAdapter.EducationAdapterViewHolder educationAdapterViewHolder = new EducationAdapter.EducationAdapterViewHolder(itemView);
        return new EducationAdapter.EducationAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final EducationAdapter.EducationAdapterViewHolder holder, final int position) {

        if (educationEvents.size() > 0) {
            holder.eventTitle.setText(educationEvents.get(position).getInstitution());
            holder.eventSubTitle.setText(educationEvents.get(position).getTitle());
            holder.eventTiming.setText(educationEvents.get(position).getShort_desc());

        }

        descriptionVal=educationEvents.get(position).getLong_desc();
        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (educationEvents.get(position).getRegistration().equals("true")) {
                    ((EducationCalendarActivity) context)
                            .showDialog(context.getResources().getString(R.string.register_now), educationEvents, position);

                } else {
                    ((EducationCalendarActivity) context).
                            showDialog(context.getResources().getString(R.string.add_to_calendar), educationEvents, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return educationEvents.size();
    }


    public void clear() {
        final int size = educationEvents.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                educationEvents.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }


    public class EducationAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventSubTitle, eventTiming, viewDetails;
        public LinearLayout layoutHolder;

        public EducationAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventSubTitle = (TextView) itemView.findViewById(R.id.event_subtitle);
            eventTiming = (TextView) itemView.findViewById(R.id.event_timing);
            viewDetails = (TextView) itemView.findViewById(R.id.event_view_details);
            layoutHolder = (LinearLayout) itemView.findViewById(R.id.layout_holder);
        }
    }

}
   
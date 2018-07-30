package com.qatarmuseums.qatarmuseumsapp.education;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.CustomDialogClass;

import java.util.ArrayList;

/**
 * Created by MoongedePC on 26-Jul-18.
 */

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationAdapterViewHolder> {

    Context context;
    ArrayList<EducationEvents> educationEvents;

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
    public void onBindViewHolder(@NonNull final EducationAdapter.EducationAdapterViewHolder holder, int position) {


        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(context.getResources().getString(R.string.education_detail), holder.eventSubTitle.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return educationEvents.size();
    }

    public class EducationAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventSubTitle, eventMaxGroup, eventTiming, viewDetails;
        public LinearLayout layoutHolder;

        public EducationAdapterViewHolder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventSubTitle = (TextView) itemView.findViewById(R.id.event_subtitle);
            eventMaxGroup = (TextView) itemView.findViewById(R.id.event_max_size);
            eventTiming = (TextView) itemView.findViewById(R.id.event_timing);
            viewDetails = (TextView) itemView.findViewById(R.id.event_view_details);
            layoutHolder = (LinearLayout) itemView.findViewById(R.id.layout_holder);
        }
    }


    protected void showDialog(final String details, String title) {

        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button registerNowBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(context.getResources().getString(R.string.calendar_dialog_title));
        registerNowBtn.setText(context.getResources().getString(R.string.register_now));
        dialogContent.setText(details);

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                CustomDialogClass cdd = new CustomDialogClass(context, context.getResources().getString(R.string.register_now_content));
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
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

}

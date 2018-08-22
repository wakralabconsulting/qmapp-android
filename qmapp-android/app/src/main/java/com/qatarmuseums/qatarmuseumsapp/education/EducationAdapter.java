package com.qatarmuseums.qatarmuseumsapp.education;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.CustomDialogClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by MoongedePC on 26-Jul-18.
 */

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationAdapterViewHolder> {

    Context context;
    ArrayList<EducationEvents> educationEvents;
    ContentResolver contentResolver;

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
            holder.eventTitle.setText(educationEvents.get(position).getCategory());
            holder.eventSubTitle.setText(educationEvents.get(position).getTitle());
            holder.eventTiming.setText(educationEvents.get(position).getShort_desc());
            holder.eventMaxGroup.setText("Max Group Size : " + educationEvents.get(position).getMax_group_size());

        }

        if (position % 2 == 1) {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.layoutHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (educationEvents.get(position).getRegistration().equals("true")) {
                    showDialog(context.getResources().getString(R.string.register_now), educationEvents, position);
                } else {
                    showDialog(context.getResources().getString(R.string.add_to_calendar), educationEvents, position);
                }
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


    protected void showDialog(final String buttonText
            , final ArrayList<EducationEvents> educationEvents, final int position) {

        final Dialog dialog = new Dialog(context, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.common_popup, null);

        dialog.setContentView(view);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button registerNowBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);


        dialogTitle.setText(educationEvents.get(position).getTitle());
        registerNowBtn.setText(buttonText);
        dialogContent.setText(educationEvents.get(position).getLong_desc());

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                if (buttonText.equalsIgnoreCase(context.getResources().getString(R.string.register_now))) {
                    CustomDialogClass cdd = new CustomDialogClass(context, context.getResources().getString(R.string.register_now_content));
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                    dialog.dismiss();
                } else {
                    addToCalendar(educationEvents, position);
                    dialog.dismiss();
                }

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

    private void addToCalendar(final ArrayList<EducationEvents> educationEvents, final int position) {

        contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.TITLE, educationEvents.get(position).getTitle());
        cv.put(CalendarContract.Events.DESCRIPTION, educationEvents.get(position).getLong_desc());
//        cv.put(CalendarContract.Events.DTSTART, 50400000);
//        cv.put(CalendarContract.Events.DTEND, 57600000);
//        // Default calendar
//        cv.put(CalendarContract.Events.CALENDAR_ID, 1);
//        double startTime=Long.parseLong(educationEvents.get(position).getStart_time())*(3600/.001);
        long watch = Calendar.getInstance().getTimeInMillis();
//        System.out.println("check time "+startTime);
        System.out.println("check time " + watch);
        cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
        // Default calendar
        cv.put(CalendarContract.Events.CALENDAR_ID, 3);
        TimeZone timeZone = TimeZone.getDefault();
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        // Insert event to calendar

//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR}, 100);
//        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//        }
//        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
//        Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show();

    }


}
   
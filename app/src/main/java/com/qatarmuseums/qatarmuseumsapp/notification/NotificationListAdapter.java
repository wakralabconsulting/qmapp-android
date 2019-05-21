package com.qatarmuseums.qatarmuseumsapp.notification;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.Collections;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private List<NotificationModel> notificationModelList;
    private Animation zoomOutAnimation;
    private SharedPreferences qmPreferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView information;
        View bellIcon, nextIcon;
        RelativeLayout notificationHolder;

        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(View view) {
            super(view);
            notificationHolder = view.findViewById(R.id.notification_holder);
            bellIcon = view.findViewById(R.id.bell_icon);
            information = view.findViewById(R.id.notification_text);
            nextIcon = view.findViewById(R.id.next_icon);
            nextIcon.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        nextIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            });


        }

    }

    public void setData(List<NotificationModel> newData) {
        // All notifications are viewed so badgeCount set as zero
        int badgeCount = 0;
        SharedPreferences.Editor editor = qmPreferences.edit();
        editor.putInt("BADGE_COUNT", badgeCount);
        editor.apply();

        this.notificationModelList = newData;
        Collections.reverse(notificationModelList);
        notifyDataSetChanged();

    }

    NotificationListAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
        zoomOutAnimation = AnimationUtils.loadAnimation(context.getApplicationContext(),
                R.anim.zoom_out_more);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public NotificationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.MyViewHolder holder, int position) {
        NotificationModel model = notificationModelList.get(position);
        holder.information.setText(model.getTitle());
        if (position % 2 == 1) {
            holder.notificationHolder.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.notificationHolder.setBackgroundColor(Color.parseColor("#FFf2f2f2"));
        }
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
}

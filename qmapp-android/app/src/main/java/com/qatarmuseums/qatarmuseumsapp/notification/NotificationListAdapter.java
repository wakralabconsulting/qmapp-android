package com.qatarmuseums.qatarmuseumsapp.notification;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<NotificationModel> notificationModelList;
    private Animation zoomOutAnimation;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView information;
        public ImageView bellIcon, nextIcon;
        public RelativeLayout notificationHolder;

        public MyViewHolder(View view) {
            super(view);
            notificationHolder = (RelativeLayout) view.findViewById(R.id.notification_holder);
            bellIcon = (ImageView) view.findViewById(R.id.bell_icon);
            information = (TextView) view.findViewById(R.id.notification_text);
            nextIcon = (ImageView) view.findViewById(R.id.next_icon);
            nextIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            nextIcon.startAnimation(zoomOutAnimation);
                            break;
                    }
                    return false;
                }
            });
            notificationHolder.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            nextIcon.startAnimation(zoomOutAnimation);
                            break;
                    }
                    return false;
                }
            });
            notificationHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bellIcon.setImageResource(R.drawable.notification_icon);
                }
            });

        }

    }


    public NotificationListAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
        this.mContext = context;
        zoomOutAnimation = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                R.anim.zoom_out_more);
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
        holder.information.setText(model.getInfo());
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

package com.qatarmuseums.qatarmuseumsapp.commonlistpage;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

import timber.log.Timber;

public class CommonListAdapter extends RecyclerView.Adapter<CommonListAdapter.MyViewHolder> {

    private final Context mContext;
    private final RecyclerTouchListener.ItemClickListener listener;
    private List<CommonListModel> commonListModelList;
    private Animation zoomOutAnimation;
    private Util util;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final int height;
        TextView name, statusTag, tourDayTxt, tourDateTxt,
                tourTitleTxt, dateAndTime;
        ImageView imageView, favIcon;
        RelativeLayout commonTitleLayout, tourTitleLayout, recyclerRowItem;

        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            imageView = view.findViewById(R.id.common_image_view);
            name = view.findViewById(R.id.name_text);
            dateAndTime = view.findViewById(R.id.date_and_time);
            favIcon = view.findViewById(R.id.favourite);
            statusTag = view.findViewById(R.id.open_close_tag);
            commonTitleLayout = view.findViewById(R.id.common_title_layout);
            tourTitleLayout = view.findViewById(R.id.tour_title_layout);
            tourDayTxt = view.findViewById(R.id.tour_day_text);
            tourDateTxt = view.findViewById(R.id.tour_date_text);
            tourTitleTxt = view.findViewById(R.id.tour_title_text);
            recyclerRowItem = view.findViewById(R.id.row_item);
            view.setOnClickListener(this);
            favIcon.setOnClickListener(this);
            favIcon.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        favIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            });
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = (int) (displayMetrics.heightPixels * 0.27);
        }

        @Override
        public void onClick(View v) {
            CommonListModel model = commonListModelList.get(getAdapterPosition());
            if (v.getId() == favIcon.getId()) {
                if (util.checkImageResource(mContext, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                    model.setIsFavourite(false);
                } else {
                    favIcon.setImageResource(R.drawable.heart_fill);
                    model.setIsFavourite(true);
                }
            } else {
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }

        }
    }

    CommonListAdapter(Context context, List<CommonListModel> commonListModelList, RecyclerTouchListener.ItemClickListener listener) {
        this.commonListModelList = commonListModelList;
        this.mContext = context;
        this.listener = listener;
        zoomOutAnimation = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                R.anim.zoom_out_more);
        util = new Util();

    }

    @NonNull
    @Override
    public CommonListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.common_list_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonListAdapter.MyViewHolder holder, int position) {
        CommonListModel model = commonListModelList.get(position);
        ViewGroup.LayoutParams params = holder.recyclerRowItem.getLayoutParams();
        params.height = holder.height;
        holder.recyclerRowItem.setLayoutParams(params);
        if (model.getEventDate() != null) {
            holder.commonTitleLayout.setVisibility(View.GONE);
            holder.tourTitleLayout.setVisibility(View.VISIBLE);
            holder.tourDayTxt.setText(model.getEventDay());
            try {
                model.setEventDate(util.formatDateFromDateString(model.getEventDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (model.getIsTour())
                holder.tourDateTxt.setText(model.getEventDate());
            holder.tourTitleTxt.setText(model.getName());
        } else if (model.getIsTravel() != null) {
            holder.name.setText(model.getName());
            holder.name.setAllCaps(false);
        } else
            holder.name.setText(model.getName());

        if (model.getIsFavourite() != null) {
            holder.favIcon.setVisibility(View.VISIBLE);
            if (model.getIsFavourite())
                holder.favIcon.setImageResource(R.drawable.heart_fill);
            else
                holder.favIcon.setImageResource(R.drawable.heart_empty);
        }
        if (model.getExhibitionStatus() != null) {
            holder.statusTag.setVisibility(View.VISIBLE);
            holder.statusTag.setText(model.getExhibitionStatus());
            if (model.getExhibitionStatus().equalsIgnoreCase("Now open") || model.getExhibitionStatus().equalsIgnoreCase("مفتوح")) {
                holder.statusTag.setBackgroundResource(R.drawable.round_corner_yellow);
            } else {
                holder.statusTag.setBackgroundResource(R.drawable.round_corner_grey);
            }
        }
        if (model.getDisplayDate() != null) {
            holder.dateAndTime.setVisibility(View.VISIBLE);
            holder.dateAndTime.setText(model.getDisplayDate());
        }
        if (model.isImageTypeIsArray()) {
            GlideApp.with(mContext)
                    .load(model.getImages().get(0))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        } else {
            GlideApp.with(mContext)
                    .load(model.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);

        }
        if (model.getEventDate() != null) {
            if (model.getImages().size() > 0 && (model.getImages().get(0) != null)) {
                GlideApp.with(mContext)
                        .load(model.getImages().get(0))
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            } else {
                GlideApp.with(mContext)
                        .load("")
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return commonListModelList.size();
    }
}

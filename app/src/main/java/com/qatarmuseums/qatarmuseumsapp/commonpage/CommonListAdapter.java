package com.qatarmuseums.qatarmuseumsapp.commonpage;


import android.content.Context;
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
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.List;

public class CommonListAdapter extends RecyclerView.Adapter<CommonListAdapter.MyViewHolder> {

    private final Context mContext;
    private final RecyclerTouchListener.ItemClickListener listener;
    private List<CommonModel> commonModelList;
    private Animation zoomOutAnimation;
    Util util;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, dateDetails, locationDetails, statusTag, tourDayTxt, tourDateTxt, tourTitleTxt;
        public ImageView imageView, favIcon;
        public RelativeLayout commonTitleLayout, tourTitleLayout, recyclerRowItem;

        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            imageView = (ImageView) view.findViewById(R.id.common_image_view);
            name = (TextView) view.findViewById(R.id.name_text);
            favIcon = (ImageView) view.findViewById(R.id.favourite);
            statusTag = (TextView) view.findViewById(R.id.open_close_tag);
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


        }

        @Override
        public void onClick(View v) {
            CommonModel model = commonModelList.get(getAdapterPosition());
            if (v.getId() == favIcon.getId()) {
                if (util.checkImageResource(mContext, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                    model.setIsfavourite(false);
                } else {
                    favIcon.setImageResource(R.drawable.heart_fill);
                    model.setIsfavourite(true);
                }
            } else {
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }

        }
    }

    public CommonListAdapter(Context context, List<CommonModel> commonModelList, RecyclerTouchListener.ItemClickListener listener) {
        this.commonModelList = commonModelList;
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
        CommonModel model = commonModelList.get(position);
        if (model.getEventDate() != null) {
            holder.commonTitleLayout.setVisibility(View.GONE);
            holder.tourTitleLayout.setVisibility(View.VISIBLE);
            holder.tourDayTxt.setText(model.getEventDay());
            holder.tourDateTxt.setText(model.getEventDate());
            holder.tourTitleTxt.setText(model.getName());
        } else if (model.getIsTravel() != null) {
            holder.name.setText(model.getName());
            holder.name.setTextSize(33);
            holder.name.setAllCaps(false);
            ViewGroup.LayoutParams params = holder.recyclerRowItem.getLayoutParams();
            params.height = model.getRowHeight();
            holder.recyclerRowItem.setLayoutParams(params);
        } else
            holder.name.setText(model.getName());

        if (model.getIsfavourite() != null) {
            holder.favIcon.setVisibility(View.VISIBLE);
            if (model.getIsfavourite())
                holder.favIcon.setImageResource(R.drawable.heart_fill);
            else
                holder.favIcon.setImageResource(R.drawable.heart_empty);
        }
        if (model.getStatusTag() != null) {
            holder.statusTag.setVisibility(View.VISIBLE);
            if (model.getStatusTag()) {
                holder.statusTag.setText(R.string.new_open);
                holder.statusTag.setBackgroundResource(R.drawable.round_corner_yellow);
            } else {

                holder.statusTag.setText(R.string.closed);
                holder.statusTag.setBackgroundResource(R.drawable.round_corner_grey);
            }
        }
        if (model.getIsTravel() != null)
            GlideApp.with(mContext)
                    .load(model.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_portrait)
                    .into(holder.imageView);
        else if (model.getEventDate() != null)
            GlideApp.with(mContext)
                    .load(model.getImages().get(0))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        else
            GlideApp.with(mContext)
                    .load(model.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return commonModelList.size();
    }
}

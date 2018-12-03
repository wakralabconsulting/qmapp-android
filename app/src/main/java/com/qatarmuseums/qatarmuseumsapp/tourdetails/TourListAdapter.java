package com.qatarmuseums.qatarmuseumsapp.tourdetails;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.List;

public class TourListAdapter extends RecyclerView.Adapter<TourListAdapter.MyViewHolder> {

    private final Context mContext;
    private final RecyclerTouchListener.ItemClickListener listener;
    private List<CommonModel> commonModelList;
    private Animation zoomOutAnimation;
    Util util;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, statusTag, tourDayTxt, tourDateTxt, tourTitleTxt;
        public ImageView imageView, favIcon;
        public RelativeLayout commonTitleLayout, tourTitleLayout;

        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            imageView = (ImageView) view.findViewById(R.id.common_image_view);
            name = (TextView) view.findViewById(R.id.name_text);
            commonTitleLayout = view.findViewById(R.id.common_title_layout);
            tourTitleLayout = view.findViewById(R.id.tour_title_layout);
            tourDayTxt = view.findViewById(R.id.tour_day_text);
            tourDateTxt = view.findViewById(R.id.tour_date_text);
            tourTitleTxt = view.findViewById(R.id.tour_title_text);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    public TourListAdapter(Context context, List<CommonModel> commonModelList, RecyclerTouchListener.ItemClickListener listener) {
        this.commonModelList = commonModelList;
        this.mContext = context;
        this.listener = listener;
        zoomOutAnimation = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                R.anim.zoom_out_more);
        util = new Util();


    }

    @NonNull
    @Override
    public TourListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.common_list_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TourListAdapter.MyViewHolder holder, int position) {
        CommonModel model = commonModelList.get(position);
        holder.commonTitleLayout.setVisibility(View.GONE);
        holder.tourTitleLayout.setVisibility(View.VISIBLE);
        holder.tourDayTxt.setText(model.getEventDay());
        holder.tourDayTxt.setTextSize(22);
        holder.tourDateTxt.setTextSize(22);
        holder.tourDateTxt.setText(model.getEventDate());
        holder.tourTitleTxt.setText(model.getName());

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

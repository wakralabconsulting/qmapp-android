package com.qatarmuseums.qatarmuseumsapp.commonactivity;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.DetailsActivity.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.homeactivity.GlideApp;
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
        public TextView name, nameDescription, statusTag;
        public ImageView imageView, favIcon;
        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            imageView = (ImageView) view.findViewById(R.id.common_image_view);
            name = (TextView) view.findViewById(R.id.name_text);
            nameDescription = (TextView) view.findViewById(R.id.name_description_text);
            favIcon = (ImageView) view.findViewById(R.id.favourite);
            statusTag = (TextView) view.findViewById(R.id.open_close_tag);
            view.setOnClickListener(this);
            favIcon.setOnClickListener(this);
            favIcon.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            favIcon.startAnimation(zoomOutAnimation);
                            break;
                    }
                    return false;
                }
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
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("HEADER_IMAGE", model.getImage());
                intent.putExtra("MAIN_TITLE", model.getName());
                intent.putExtra("IS_FAVOURITE", model.getIsfavourite());
                mContext.startActivity(intent);
            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
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
        holder.name.setText(model.getName());
        if (model.getNameDescription() != null) {
            holder.nameDescription.setVisibility(View.VISIBLE);
            holder.nameDescription.setText(model.getNameDescription());
        }
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
        GlideApp.with(mContext)
                .load(model.getImage())
                .centerCrop()
                .placeholder(R.drawable.placeholdeer)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return commonModelList.size();
    }
}

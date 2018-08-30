package com.qatarmuseums.qatarmuseumsapp.tourguide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class TourGuideAdapter extends RecyclerView.Adapter<TourGuideAdapter.MyViewHolder> {
    private final Context mContext;
    private List<TourGuideList> tourGuideList;

    public TourGuideAdapter(Context context, List<TourGuideList> tourGuideList) {
        this.tourGuideList = tourGuideList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TourGuideList tgList = tourGuideList.get(position);
        holder.name.setText(tgList.getName());
        GlideApp.with(mContext)
                .load(tgList.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return tourGuideList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView, headphoneIcon;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            name = (TextView) view.findViewById(R.id.name_text);
            headphoneIcon = (ImageView) view.findViewById(R.id.headphone_icon);
        }
    }
}

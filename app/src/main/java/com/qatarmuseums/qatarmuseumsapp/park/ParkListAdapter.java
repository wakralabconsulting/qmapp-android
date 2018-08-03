package com.qatarmuseums.qatarmuseumsapp.park;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import java.util.List;

public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<ParkList> parkLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mainTitle, title, shortDescription, longDescription, timingInfo, mapDetails;
        public ImageView imageView;
        public LinearLayout locationLayout, mainTitleLayout;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view);
            mainTitleLayout = (LinearLayout) view.findViewById(R.id.main_title_layout);
            mainTitle = (TextView) view.findViewById(R.id.main_title);
            title = (TextView) view.findViewById(R.id.title);
            shortDescription = (TextView) view.findViewById(R.id.short_description);
            longDescription = (TextView) view.findViewById(R.id.long_description);
            timingInfo = (TextView) view.findViewById(R.id.timing_info);
            locationLayout = (LinearLayout) view.findViewById(R.id.location_layout);
            mapDetails = (TextView) view.findViewById(R.id.map_info);
        }
    }

    public ParkListAdapter(Context context, List<ParkList> parkLists) {
        this.parkLists = parkLists;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ParkListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkListAdapter.MyViewHolder holder, int position) {
        final ParkList parkList = parkLists.get(position);
        if (position == 0) {
            holder.mainTitleLayout.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.locationLayout.setVisibility(View.VISIBLE);
            holder.mainTitle.setText(parkList.getMainTitle());
            holder.mapDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String geoUri = mContext.getString(R.string.map_navigation_api) + parkList.getLatitude() +
                            "," + parkList.getLongitude() + " (" + parkList.getTitle() + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    mContext.startActivity(intent);
                }
            });
        }
        holder.title.setText(parkList.getTitle());
        holder.shortDescription.setText(parkList.getShortDescription());
        if (parkList.getLongDescription() != null) {
            holder.longDescription.setVisibility(View.VISIBLE);
            holder.longDescription.setText(parkList.getLongDescription());
        }
        holder.timingInfo.setText(parkList.getTimingInfo());
        GlideApp.with(mContext)
                .load(parkList.getImage())
                .placeholder(R.drawable.placeholdeer)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return parkLists.size();
    }
}

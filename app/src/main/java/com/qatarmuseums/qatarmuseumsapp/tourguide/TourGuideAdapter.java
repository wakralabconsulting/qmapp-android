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
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;

import java.util.List;

public class TourGuideAdapter extends RecyclerView.Adapter<TourGuideAdapter.MyViewHolder> {
    private final Context mContext;
    private List<HomeList> tourGuideList;
    private String comingFrom;

    TourGuideAdapter(Context context, List<HomeList> tourGuideList, String comingFrom) {
        this.tourGuideList = tourGuideList;
        this.mContext = context;
        this.comingFrom = comingFrom;
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
        HomeList tgList = tourGuideList.get(position);
        holder.name.setText(tgList.getName());
        if (tgList.getTourGuideAvailable().equalsIgnoreCase("true")) {
            holder.headphoneIcon.setVisibility(View.VISIBLE);
            holder.headphoneIcon.setImageResource(R.drawable.floor_map_circle);
        } else {
            holder.headphoneIcon.setVisibility(View.GONE);
        }
        if (comingFrom.equals(mContext.getString(R.string.tourguide_sidemenu_title))) {
            GlideApp.with(mContext)
                    .load(tgList.getImage())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.headphoneIcon.setVisibility(View.VISIBLE);
            holder.headphoneIcon.setImageResource(R.drawable.audio_circle);
            if (tgList.getName().equals(mContext.getString(R.string.coming_soon_txt)))
                holder.headphoneIcon.setColorFilter(mContext.getResources().getColor(R.color.semi_transparent_grey));
            GlideApp.with(mContext)
                    .load(tgList.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return tourGuideList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ImageView imageView, headphoneIcon;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            name = view.findViewById(R.id.name_text);
            headphoneIcon = view.findViewById(R.id.headphone_icon);
        }
    }
}

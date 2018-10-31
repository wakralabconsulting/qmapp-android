package com.qatarmuseums.qatarmuseumsapp.tourguidedetails;

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
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuideStarterModel;

import java.util.List;

public class TourGuideDetailsAdapter extends RecyclerView.Adapter<TourGuideDetailsAdapter.MyViewHolder> {
    private final Context mContext;
    private List<SelfGuideStarterModel> tourGuideList;
    String comingFrom;

    public TourGuideDetailsAdapter(Context context, List<SelfGuideStarterModel> tourGuideList, String comingFrom) {
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
        SelfGuideStarterModel tgList = tourGuideList.get(position);
        holder.name.setText(tgList.getTitle());
//        if (tgList.getTourguideAvailable().equalsIgnoreCase("true")){
//            holder.headphoneIcon.setVisibility(View.VISIBLE);
//            holder.headphoneIcon.setImageResource(R.drawable.floor_map_circle);
//        } else {
//            holder.headphoneIcon.setVisibility(View.GONE);
//        }
        if (comingFrom.equals(mContext.getString(R.string.tourguide_sidemenu_title))) {
            GlideApp.with(mContext)
                    .load(tgList.getFirstImage())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.headphoneIcon.setVisibility(View.VISIBLE);
            holder.headphoneIcon.setImageResource(R.drawable.audio_circle);
            if(tgList.getTitle().equals(mContext.getString(R.string.coming_soon_txt)))
                holder.headphoneIcon.setColorFilter(mContext.getResources().getColor(R.color.grey));
            GlideApp.with(mContext)
                    .load(tgList.getFirstImage())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.imageView);
        }
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

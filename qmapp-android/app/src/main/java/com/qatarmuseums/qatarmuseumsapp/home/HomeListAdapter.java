package com.qatarmuseums.qatarmuseumsapp.home;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;

import timber.log.Timber;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<HomeList> homeLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout itemContainer;
        private final int height;
        public TextView name;
        ImageView imageView, headphoneIcon;

        public MyViewHolder(View view) {
            super(view);
            itemContainer = view.findViewById(R.id.item_container);
            imageView = view.findViewById(R.id.image_view);
            name = view.findViewById(R.id.name_text);
            headphoneIcon = view.findViewById(R.id.headphone_icon);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = (int) (displayMetrics.heightPixels * 0.27);
        }
    }

    HomeListAdapter(Context context, List<HomeList> homeLists) {
        this.homeLists = homeLists;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HomeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListAdapter.MyViewHolder holder, int position) {
        HomeList homeList = homeLists.get(position);
        Timber.i("Setting home list row with Id: %s", homeList.getId());
        ViewGroup.LayoutParams params = holder.itemContainer.getLayoutParams();
        params.height = holder.height;
        holder.itemContainer.setLayoutParams(params);
        holder.name.setText(homeList.getName());
        if (homeList.getTourGuideAvailable().equalsIgnoreCase("true"))
            holder.headphoneIcon.setVisibility(View.VISIBLE);
        else
            holder.headphoneIcon.setVisibility(View.GONE);

        GlideApp.with(mContext)
                .load(homeList.getImage())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return homeLists.size();
    }
}

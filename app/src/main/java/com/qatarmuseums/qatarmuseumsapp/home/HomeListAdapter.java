package com.qatarmuseums.qatarmuseumsapp.home;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;


import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<HomeList> homeLists;

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

    public HomeListAdapter(Context context, List<HomeList> homeLists) {
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
        holder.name.setText(homeList.getName());
        if (homeList.getTourguideAvailable().equalsIgnoreCase("false"))
            holder.headphoneIcon.setVisibility(View.GONE);

        GlideApp.with(mContext)
                .load(homeList.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return homeLists.size();
    }
}

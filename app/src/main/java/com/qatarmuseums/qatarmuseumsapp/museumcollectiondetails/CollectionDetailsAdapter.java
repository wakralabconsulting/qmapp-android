package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

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

import java.util.List;

public class CollectionDetailsAdapter extends RecyclerView.Adapter<CollectionDetailsAdapter.MyViewHolder> {
    private Context mContext;
    private List<CollectionDetailsList> collectionDetailsList = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_detail_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CollectionDetailsList cdl = collectionDetailsList.get(position);
        holder.mainTitle.setText(cdl.getMainTitle());
        holder.subTitle.setText(cdl.getAbout());
        GlideApp.with(mContext)
                .load(cdl.getImage1())
                .into(holder.image1);

    }

    @Override
    public int getItemCount() {
        return collectionDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mainTitle, subTitle;
        ImageView image1;

        public MyViewHolder(View view) {
            super(view);
            image1 = view.findViewById(R.id.collection_image1);
            mainTitle = view.findViewById(R.id.main_title);
            subTitle = view.findViewById(R.id.sub_title);
        }
    }

    CollectionDetailsAdapter(Context context, List<CollectionDetailsList> collectionDetailsList) {
        this.collectionDetailsList = collectionDetailsList;
        this.mContext = context;
    }
}

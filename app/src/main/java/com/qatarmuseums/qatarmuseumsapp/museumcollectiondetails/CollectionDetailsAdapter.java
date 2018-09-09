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

public class CollectionDetailsAdapter extends  RecyclerView.Adapter<CollectionDetailsAdapter.MyViewHolder> {
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
        holder.firstDescription.setText(cdl.getFirstDescription());
        holder.secondDescription.setText(cdl.getSecondDescription());
        holder.thirddDescription.setText(cdl.getThirdDescription());
        GlideApp.with(mContext)
                .load(cdl.getImage1())
                .placeholder(R.drawable.placeholder)
                .into(holder.image1);
        GlideApp.with(mContext)
                .load(cdl.getImage2())
                .placeholder(R.drawable.placeholder)
                .into(holder.image2);

    }

    @Override
    public int getItemCount() {
        return collectionDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mainTitle, subTitle, firstDescription,
                secondDescription,thirddDescription;
        public ImageView image1,image2;
        public MyViewHolder(View view) {
            super(view);
            image1 = (ImageView) view.findViewById(R.id.collection_image1);
            image2 = (ImageView) view.findViewById(R.id.collection_image2);
            mainTitle = (TextView) view.findViewById(R.id.main_title);
            subTitle = (TextView) view.findViewById(R.id.sub_title);
            firstDescription = (TextView) view.findViewById(R.id.first_description);
            secondDescription = (TextView) view.findViewById(R.id.second_description);
            thirddDescription = (TextView) view.findViewById(R.id.third_description);
        }
    }

    public CollectionDetailsAdapter(Context context, List<CollectionDetailsList> collectionDetailsList) {
        this.collectionDetailsList = collectionDetailsList;
        this.mContext = context;
    }
}

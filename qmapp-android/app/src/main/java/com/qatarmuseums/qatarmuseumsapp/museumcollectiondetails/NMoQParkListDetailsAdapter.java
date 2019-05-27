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

public class NMoQParkListDetailsAdapter extends RecyclerView.Adapter<NMoQParkListDetailsAdapter.MyViewHolder> {
    private Context mContext;
    private List<NMoQParkListDetails> nMoQParkListDetails = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection_detail_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NMoQParkListDetails listDetails = nMoQParkListDetails.get(position);
        holder.mainTitle.setText(listDetails.getMainTitle());

        holder.imageDescription.setVisibility(View.VISIBLE);
        holder.imageDescription.setText(listDetails.getDescription());
        holder.subTitle.setVisibility(View.GONE);

        GlideApp.with(mContext)
                .load(listDetails.getImages().get(0))
                .into(holder.image1);

    }

    @Override
    public int getItemCount() {
        return nMoQParkListDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mainTitle, subTitle, imageDescription;
        ImageView image1;

        public MyViewHolder(View view) {
            super(view);
            image1 = view.findViewById(R.id.collection_image1);
            mainTitle = view.findViewById(R.id.main_title);
            subTitle = view.findViewById(R.id.sub_title);
            imageDescription = view.findViewById(R.id.second_description);
        }
    }

    NMoQParkListDetailsAdapter(Context context, List<NMoQParkListDetails> nMoQParkListDetails) {
        this.nMoQParkListDetails = nMoQParkListDetails;
        this.mContext = context;
    }
}

package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

import java.lang.ref.WeakReference;
import java.util.List;

public class FacilitiesSecondaryAdapter extends RecyclerView.Adapter<FacilitiesSecondaryAdapter.MyViewHolder> {

    private final Context mContext;
    private final RecyclerTouchListener.ItemClickListener listener;
    private List<FacilitiesDetailModel> facilitiesDetailModelList;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.common_list_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FacilitiesDetailModel model = facilitiesDetailModelList.get(position);
        holder.name.setText(model.getFacilitiesTitle());

        if (model.getFacilityImage().size() > 0) {
            GlideApp.with(mContext)
                    .load(model.getFacilityImage().get(0))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        } else {
            GlideApp.with(mContext)
                    .load("")
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return facilitiesDetailModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView tourDayTxt;
        TextView tourDateTxt;
        TextView tourTitleTxt;
        public ImageView imageView;
        RelativeLayout commonTitleLayout, tourTitleLayout;

        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            imageView = view.findViewById(R.id.common_image_view);
            name = view.findViewById(R.id.name_text);
            commonTitleLayout = view.findViewById(R.id.common_title_layout);
            tourTitleLayout = view.findViewById(R.id.tour_title_layout);
            tourDayTxt = view.findViewById(R.id.tour_day_text);
            tourDateTxt = view.findViewById(R.id.tour_date_text);
            tourTitleTxt = view.findViewById(R.id.tour_title_text);
            name.setAllCaps(false);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    FacilitiesSecondaryAdapter(Context context, List<FacilitiesDetailModel> facilitiesDetailModelList, RecyclerTouchListener.ItemClickListener listener) {
        this.facilitiesDetailModelList = facilitiesDetailModelList;
        this.mContext = context;
        this.listener = listener;


    }
}

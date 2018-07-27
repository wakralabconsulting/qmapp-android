package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;

/**
 * Created by Exalture on 26-07-2018.
 */

public class MuseumHorizontalScrollViewAdapter extends RecyclerView.Adapter<MuseumHorizontalScrollViewAdapter.MyViewHolder>  {
    private Context mContext;
    private List<MuseumHScrollModel> museumHScrollModelList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musuem_horizontal_scroll_page_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MuseumHScrollModel museumHScrollModel = museumHScrollModelList.get(position);
        holder.museumHscrollItemText.setText(museumHScrollModel.getTextName());
        holder.museumHscrollItemImage.setImageResource(museumHScrollModel.getResId());


    }

    @Override
    public int getItemCount() {
        return museumHScrollModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView museumHscrollItemText;
        ImageView museumHscrollItemImage;
        public MyViewHolder(View view) {
            super(view);
            museumHscrollItemText = (TextView) view.findViewById(R.id.horizontal_scrol_text);
            museumHscrollItemImage = (ImageView) view.findViewById(R.id.horizontal_scrol_image_icon);
        }
    }


    public MuseumHorizontalScrollViewAdapter(Context context, List<MuseumHScrollModel> museumHScrollModelList){
        this.mContext = context;
        this.museumHScrollModelList = museumHScrollModelList;
    }
}

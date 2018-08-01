package com.qatarmuseums.qatarmuseumsapp.museum;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;

import java.util.List;

/**
 * Created by Exalture on 26-07-2018.
 */

public class MuseumHorizontalScrollViewAdapter extends RecyclerView.Adapter<MuseumHorizontalScrollViewAdapter.MyViewHolder> {
    private Context mContext;
    private Animation zoomOutAnimation;
    private List<MuseumHScrollModel> museumHScrollModelList;
    private Intent navigationIntent;
    String title;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musuem_horizontal_scroll_page_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        MuseumHScrollModel museumHScrollModel = museumHScrollModelList.get(position);
        holder.museumHscrollItemText.setText(museumHScrollModel.getTextName());
        holder.museumHscrollItemImage.setImageResource(museumHScrollModel.getResId());
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    navigationIntent = new Intent(mContext, DetailsActivity.class);
                    navigationIntent.putExtra("MAIN_TITLE", title);
                    navigationIntent.putExtra("COMING_FROM", mContext.getString(R.string.museum_about));
                    mContext.startActivity(navigationIntent);
                }else if (position == 3) {
                    navigationIntent = new Intent(mContext, CommonActivity.class);
                    navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.museum_collection_text));
                    mContext.startActivity(navigationIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return museumHScrollModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView museumHscrollItemText;
        LinearLayout itemLayout;
        ImageView museumHscrollItemImage;

        public MyViewHolder(View view) {
            super(view);
            museumHscrollItemText = (TextView) view.findViewById(R.id.horizontal_scrol_text);
            museumHscrollItemImage = (ImageView) view.findViewById(R.id.horizontal_scrol_image_icon);
            itemLayout = (LinearLayout) view.findViewById(R.id.item_layout);
            itemLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            itemLayout.startAnimation(zoomOutAnimation);
                            break;
                    }
                    return false;
                }
            });

        }
    }


    public MuseumHorizontalScrollViewAdapter(Context context, List<MuseumHScrollModel> museumHScrollModelList, String title) {
        this.mContext = context;
        this.museumHScrollModelList = museumHScrollModelList;
        zoomOutAnimation = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                R.anim.zoom_out_more);
        this.title = title;
    }
}

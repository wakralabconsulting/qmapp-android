package com.qatarmuseums.qatarmuseumsapp.museum;

import android.app.Activity;
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

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkActivity;
import com.qatarmuseums.qatarmuseumsapp.park.ParkActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguidedetails.TourGuideDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.List;

public class MuseumHorizontalScrollViewAdapter extends RecyclerView.Adapter<MuseumHorizontalScrollViewAdapter.MyViewHolder> {
    private String museumId;
    private Context mContext;
    private Animation zoomOutAnimation;
    private List<MuseumHScrollModel> museumHScrollModelList;
    private Intent navigationIntent;
    private String title;
    private Util util;
    private int screenWidth;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musuem_horizontal_scroll_page_list_row, parent, false);
        itemView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        MuseumHScrollModel museumHScrollModel = museumHScrollModelList.get(position);
        if (museumId.equals("88"))
            holder.museumHorizontalScrollItemText.setLines(2);
        holder.museumHorizontalScrollItemText.setText(museumHScrollModel.getTextName());
        holder.museumHorizontalScrollItemImage.setImageResource(museumHScrollModel.getResId());
        holder.itemLayout.setOnClickListener(v -> {
            if (museumHScrollModelList.get(position).getTextName().equals(
                    mContext.getResources().getString(R.string.museum_about_text))) {
                navigationIntent = new Intent(mContext, DetailsActivity.class);
                navigationIntent.putExtra("MAIN_TITLE", title);
                navigationIntent.putExtra("COMING_FROM", mContext.getString(R.string.museum_about_text));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.sidemenu_tour_guide_text))) {
                navigationIntent = new Intent(mContext, TourGuideDetailsActivity.class);
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.sidemenu_exhibition_text))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.sidemenu_exhibition_text));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.museum_collection_text))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.museum_collection_text));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.sidemenu_parks_text))) {
                if (museumId.equals("66") || museumId.equals("638")) {
                    // Park for NMoQ
                    navigationIntent = new Intent(mContext, NMoQParkActivity.class);
                    navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.sidemenu_parks_text));
                    mContext.startActivity(navigationIntent);
                } else {
                    navigationIntent = new Intent(mContext, ParkActivity.class);
                    mContext.startActivity(navigationIntent);
                }
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.sidemenu_dining_text))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.sidemenu_dining_text));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.museum_tours))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.museum_tours));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.museum_travel))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.museum_travel));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if (museumHScrollModelList.get(position).getTextName().equals(mContext.getResources().getString(R.string.museum_discussion))) {
                navigationIntent = new Intent(mContext, CommonActivity.class);
                navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.museum_discussion));
                navigationIntent.putExtra("ID", museumId);
                mContext.startActivity(navigationIntent);
            } else if ((museumId.equals("66") || museumId.equals("638"))) {
                switch (position) {
                    case 1:
                        // Facilities for NMoQ
                        //util.showComingSoonDialog((Activity) mContext, R.string.coming_soon_content);
                        navigationIntent = new Intent(mContext, CommonActivity.class);
                        navigationIntent.putExtra(mContext.getString(R.string.toolbar_title_key), mContext.getString(R.string.facilities_txt));
                        navigationIntent.putExtra("ID", museumId);
                        mContext.startActivity(navigationIntent);
                        break;
                    case 3:
                        // Experience for NMoQ won't be available before NMoQ launch
                        util.showComingSoonDialog((Activity) mContext, R.string.coming_soon_content);
                        break;
                    case 5:
                        // Event for NMoQ won't be available before NMoQ launch
                        util.showComingSoonDialog((Activity) mContext, R.string.coming_soon_content);
                        break;
                }
            } else
                util.showComingSoonDialog((Activity) mContext, R.string.coming_soon_content);
        });

    }

    @Override
    public int getItemCount() {
        return museumHScrollModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView museumHorizontalScrollItemText;
        LinearLayout itemLayout;
        ImageView museumHorizontalScrollItemImage;

        public MyViewHolder(View view) {
            super(view);
            museumHorizontalScrollItemText = view.findViewById(R.id.horizontal_scrol_text);
            museumHorizontalScrollItemImage = view.findViewById(R.id.horizontal_scrol_image_icon);
            itemLayout = view.findViewById(R.id.item_layout);
            itemLayout.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        itemLayout.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            });

        }
    }


    MuseumHorizontalScrollViewAdapter(Context context,
                                      List<MuseumHScrollModel> museumHScrollModelList,
                                      String title,
                                      String museumId, int screenWidth) {
        this.mContext = context;
        util = new Util();
        this.museumHScrollModelList = museumHScrollModelList;
        zoomOutAnimation = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                R.anim.zoom_out_more);
        this.title = title;
        this.museumId = museumId;
        this.screenWidth = screenWidth;
    }
}

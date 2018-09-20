package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;

public class StepIndicatorAdapter extends RecyclerView.Adapter<StepIndicatorAdapter.MyViewHolder> {
    private int count;
    private List<CurrentIndicatorPosition> currentIndicatorPosition;
    private int screenWidth;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sliding_stepper, parent, false);

        itemView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (position == 0) {
            holder.firstLine.setVisibility(View.INVISIBLE);
        } else if (position == count - 1) {
            holder.lastLine.setVisibility(View.INVISIBLE);
        } else {
            holder.firstLine.setVisibility(View.VISIBLE);
            holder.lastLine.setVisibility(View.VISIBLE);
        }


        if (currentIndicatorPosition.size() > 0) {
            if (position == 0 || position == count - 1) {
                if (position == currentIndicatorPosition.get(0).getCurrentPosition()) {
                    holder.indicatorImage.setImageResource(R.drawable.stripper_active_solid);

                } else
                    holder.indicatorImage.setImageResource(R.drawable.stripper_inactive_end_solid);
            } else {
                if (position == currentIndicatorPosition.get(0).getCurrentPosition()) {
                    holder.indicatorImage.setImageResource(R.drawable.stripper_active_solid);

                } else
                    holder.indicatorImage.setImageResource(R.drawable.stripper_inactive_solid);

            }


        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView indicatorImage;
        View firstLine, lastLine;

        public MyViewHolder(View view) {
            super(view);
            indicatorImage = (ImageView) view.findViewById(R.id.indicator_image);
            firstLine = (View) view.findViewById(R.id.first_line);
            lastLine = (View) view.findViewById(R.id.last_line);


        }

    }

    public StepIndicatorAdapter(List<CurrentIndicatorPosition> currentIndicatorPosition,
                                int count, int screenWidth) {
        this.currentIndicatorPosition = currentIndicatorPosition;
        this.count = count;
        this.screenWidth = screenWidth;
    }


}

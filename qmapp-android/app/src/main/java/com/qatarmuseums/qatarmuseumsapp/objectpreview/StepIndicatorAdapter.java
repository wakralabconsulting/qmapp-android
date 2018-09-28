package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private int screenWidth, listItemsSize;
    private int endController;


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
        endController = listItemsSize - 1 - currentIndicatorPosition.get(0).getOriginalPosition();

        if (currentIndicatorPosition.get(0).getOriginalPosition() < 5 && position == 0) {
            holder.firstLine.setVisibility(View.INVISIBLE);
        }  else if (endController <= (listItemsSize - 1) % 5 && position > (listItemsSize - 1) % 5) {
            holder.lastLine.setVisibility(View.INVISIBLE);
            holder.firstLine.setVisibility(View.INVISIBLE);
            holder.indicatorImage.setVisibility(View.INVISIBLE);
        } else if (endController <= (listItemsSize - 1) % 5 && position == (listItemsSize - 1) % 5) {
            holder.lastLine.setVisibility(View.INVISIBLE);
        }else if (currentIndicatorPosition.get(0).getOriginalPosition() == listItemsSize - 1 && position == 4) {
            holder.lastLine.setVisibility(View.INVISIBLE);
        } else {
            holder.firstLine.setVisibility(View.VISIBLE);
            holder.indicatorImage.setVisibility(View.VISIBLE);
            holder.lastLine.setVisibility(View.VISIBLE);
        }

        if (currentIndicatorPosition.size() > 0) {
            if (position == 0 || position == listItemsSize - 1) {
                if (position == currentIndicatorPosition.get(0).getCurrentPosition()) {
                    holder.indicatorImage.setImageResource(R.drawable.stripper_active_solid);
                } else if (currentIndicatorPosition.get(0).getOriginalPosition() > 5)
                    holder.indicatorImage.setImageResource(R.drawable.stripper_inactive_solid);
                else
                    holder.indicatorImage.setImageResource(R.drawable.stripper_inactive_end_solid);
            } else if (endController <= (listItemsSize - 1) % 5 && position == (listItemsSize - 1) % 5 &&
                    currentIndicatorPosition.get(0).getCurrentPosition() != position) {
                holder.indicatorImage.setImageResource(R.drawable.stripper_inactive_end_solid);
                holder.lastLine.setVisibility(View.INVISIBLE);
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
                                int count, int screenWidth, int listItemsSize) {
        this.currentIndicatorPosition = currentIndicatorPosition;
        this.count = count;
        this.screenWidth = screenWidth;
        this.listItemsSize = listItemsSize;
    }


}

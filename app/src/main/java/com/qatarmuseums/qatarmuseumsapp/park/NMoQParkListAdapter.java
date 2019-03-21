package com.qatarmuseums.qatarmuseumsapp.park;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

public class NMoQParkListAdapter extends RecyclerView.Adapter<NMoQParkListAdapter.MyViewHolder> {

    private final Context mContext;
    private List<NMoQParkList> nMoQParkLists;
    private final RecyclerTouchListener.ItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RelativeLayout itemContainer;
        private final int height;
        private WeakReference<RecyclerTouchListener.ItemClickListener> listenerRef;
        public TextView name;
        ImageView imageView;

        public MyViewHolder(View view, RecyclerTouchListener.ItemClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            itemContainer = view.findViewById(R.id.item_container);
            imageView = view.findViewById(R.id.image_view);
            name = view.findViewById(R.id.name_text);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = (int) (displayMetrics.heightPixels * 0.27);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    NMoQParkListAdapter(Context context, List<NMoQParkList> nMoQParkLists, RecyclerTouchListener.ItemClickListener listener) {
        this.nMoQParkLists = nMoQParkLists;
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NMoQParkListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_row, parent, false);

        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NMoQParkListAdapter.MyViewHolder holder, int position) {
        NMoQParkList nMoQParkList = nMoQParkLists.get(position);
        ViewGroup.LayoutParams params = holder.itemContainer.getLayoutParams();
        params.height = holder.height;
        holder.itemContainer.setLayoutParams(params);
        holder.name.setText(nMoQParkList.getMainTitle());
        GlideApp.with(mContext)
                .load(nMoQParkList.getImage().get(0))
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return nMoQParkLists.size();
    }
}

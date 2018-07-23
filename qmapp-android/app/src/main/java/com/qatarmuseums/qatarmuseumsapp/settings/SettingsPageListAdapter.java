package com.qatarmuseums.qatarmuseumsapp.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Exalture on 23-07-2018.
 */

public class SettingsPageListAdapter extends RecyclerView.Adapter<SettingsPageListAdapter.MyViewHolder> {
    private Context mContext;
    private List<SettingsPageModel> settingsPageModelList;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_page_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SettingsPageModel settingsPageModel = settingsPageModelList.get(position);
        holder.settingsItemName.setText(settingsPageModel.getNotificationItemName());
        if (position==2){
            holder.settingsItemStatusBtn.setBackgroundResource(R.drawable.switch_off);
        }

    }

    @Override
    public int getItemCount() {
        return settingsPageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.settings_page_item_name)
        TextView settingsItemName;
        @Nullable
        @BindView(R.id.settings_page_item_status_btn)
        Button settingsItemStatusBtn;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(view);
        }
    }

    public SettingsPageListAdapter(Context context, List<SettingsPageModel> settingsPageModelList){
        this.mContext = context;
        this.settingsPageModelList = settingsPageModelList;
    }
}

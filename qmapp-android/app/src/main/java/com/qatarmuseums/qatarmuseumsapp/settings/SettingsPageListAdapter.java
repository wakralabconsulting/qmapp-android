package com.qatarmuseums.qatarmuseumsapp.settings;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.List;


public class SettingsPageListAdapter extends RecyclerView.Adapter<SettingsPageListAdapter.MyViewHolder> {
    private List<SettingsPageModel> settingsPageModelList;

    @NonNull
    @Override
    public SettingsPageListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_page_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsPageListAdapter.MyViewHolder holder, int position) {

        SettingsPageModel settingsPageModel = settingsPageModelList.get(position);
        holder.settingsItemName.setText(settingsPageModel.getNotificationItemName());
    }

    @Override
    public int getItemCount() {
        return settingsPageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsItemName;
        SwitchCompat settingsItemStatusBtn;

        public MyViewHolder(View view) {
            super(view);
            settingsItemName = view.findViewById(R.id.settings_page_item_name);
            settingsItemStatusBtn = view.findViewById(R.id.settings_page_item_status_btn);
        }
    }

    SettingsPageListAdapter(List<SettingsPageModel> settingsPageModelList) {
        this.settingsPageModelList = settingsPageModelList;
    }
}

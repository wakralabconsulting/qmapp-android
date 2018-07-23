package com.qatarmuseums.qatarmuseumsapp.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonactivity.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<SettingsPageModel> settingsPageModelList = new ArrayList<>();
    private SettingsPageListAdapter settingsPageListAdapter;
    private Animation zoomOutAnimation;
    @Nullable
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.toolbar_back)
    ImageView backArrow;
    @Nullable
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @Nullable
    @BindView(R.id.settings_page_recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.language_change_button)
    Button languageChangeButton;
    @Nullable
    @BindView(R.id.setting_page_reset_to_default_btn)
    Button settingPageResetToDefaultBtn;
    @Nullable
    @BindView(R.id.setting_page_apply_btn)
    Button settingPageApplyButton;
    String language;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar_title.setText(getResources().getString(R.string.settings_activity_tittle));
        settingsPageListAdapter = new SettingsPageListAdapter(this, settingsPageModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(settingsPageListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // currently not using
            }

            @Override
            public void onLongClick(View view, int position) {
                // currently not using
            }
        }));
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        prepareRecyclerViewData();
    }

    public void prepareRecyclerViewData() {
        SettingsPageModel model = new SettingsPageModel("Event Updates", true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel("Exhibition Updates", true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel("Museum Updates", true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel("Culture Pass Updates", true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel("Tour Guide Updates", true);
        settingsPageModelList.add(model);

        settingsPageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.toolbar_back:
                // topbar back action
                onBackPressed();
                break;

            case R.id.language_change_button:
                // language change action
                language = "ar";
                setLocale(language);
                break;

            case R.id.setting_page_reset_to_default_btn:
                //reset to default action
                break;

            case R.id.setting_page_apply_btn:
                // apply action
                break;

            default:
                break;


        }
    }
    public void setLocale(String lang) {
        int language = 1;
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale(lang));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        if (lang.equalsIgnoreCase("en")) {
            language = 1;
        } else if (lang.equalsIgnoreCase("ar")) {
            language = 2;
        }
        SharedPreferences qfindPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = qfindPreferences.edit();
        editor.putInt("AppLanguage", language);
        editor.commit();
        refreshActivity();

    }

    public void refreshActivity(){
        //code to refresh actvity after changing language
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }
}

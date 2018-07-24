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
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonactivity.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.homeactivity.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<SettingsPageModel> settingsPageModelList = new ArrayList<>();
    private SettingsPageListAdapter settingsPageListAdapter;
    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    ImageView backArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.settings_page_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.language_change_button)
    Button languageChangeButton;
    @BindView(R.id.setting_page_reset_to_default_btn)
    Button settingPageResetToDefaultBtn;
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
        setClicklistenerforButtons();
        SharedPreferences qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (appLanguage == 1) {
            languageChangeButton.setBackgroundResource(R.drawable.switch_on);
        }else {
            languageChangeButton.setBackgroundResource(R.drawable.switch_off);
        }
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
                SharedPreferences qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                int appLanguage = qmPreferences.getInt("AppLanguage", 1);
                if (appLanguage == 1) {
                    language = "ar";
                    setLocale(language);
                    languageChangeButton.setBackgroundResource(R.drawable.switch_off);
                }else {
                    language = "en";
                    setLocale(language);
                    languageChangeButton.setBackgroundResource(R.drawable.switch_off);
                }

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
        SharedPreferences qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = qmPreferences.edit();
        editor.putInt("AppLanguage", language);
        editor.commit();
        refreshActivity();

    }

    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
    }

    public void setClicklistenerforButtons() {
        toolbar.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        toolbar_title.setOnClickListener(this);
        languageChangeButton.setOnClickListener(this);
        settingPageResetToDefaultBtn.setOnClickListener(this);
        settingPageApplyButton.setOnClickListener(this);
    }
}

package com.qatarmuseums.qatarmuseumsapp.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.HomeActivity;

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
    SwitchCompat languageChangeButton;
    @BindView(R.id.setting_page_reset_to_default_btn)
    Button settingPageResetToDefaultBtn;
    @BindView(R.id.setting_page_apply_btn)
    Button settingPageApplyButton;
    String language;
    Locale myLocale;
    private SharedPreferences qmPreferences;
    private int appLanguage;
    private SharedPreferences.Editor editor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setClicklistenerforButtons();
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        languageChangeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                appLanguage = qmPreferences.getInt("AppLanguage", 1);
                if (appLanguage == 1) {
                    language = "ar";
                    showDialog(language);
                } else {
                    language = "en";
                    showDialog(language);

                }
                return false;
            }
        });

        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        if (appLanguage == 1) {
            languageChangeButton.setChecked(false);
        } else {
            languageChangeButton.setChecked(true);
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
        SettingsPageModel model = new SettingsPageModel(getString(R.string.notification_event_updates), true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_exhibition_updates), true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_museum_updates), true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_culturepass_updates), true);
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_tourguide_updates), true);
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
            languageChangeButton.setChecked(true);
        } else if (lang.equalsIgnoreCase("ar")) {
            language = 2;
            languageChangeButton.setChecked(false);
        }
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = qmPreferences.edit();
        editor.putInt("AppLanguage", language);
        editor.commit();
        refreshActivity();

    }

    public void refreshActivity() {
        finishAffinity();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
    }

    public void setClicklistenerforButtons() {
        toolbar.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        toolbar_title.setOnClickListener(this);
        settingPageResetToDefaultBtn.setOnClickListener(this);
        settingPageApplyButton.setOnClickListener(this);
    }

    protected void showDialog(final String language) {

        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = getLayoutInflater().inflate(R.layout.settings_pop_up, null);
        dialog.setContentView(view);

        ImageView closeBtn = (ImageView) view.findViewById(R.id.close_dialog);
        Button changeLanguageBtn = (Button) view.findViewById(R.id.doneBtn);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = (TextView) view.findViewById(R.id.dialog_content);
        dialogTitle.setText(getResources().getString(R.string.change_language_dialog_title));
        changeLanguageBtn.setText(getResources().getString(R.string.change_language_button_text));
        dialogContent.setText(getResources().getString(R.string.change_language_content_text));

        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                setLocale(language);
                dialog.dismiss();

            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}

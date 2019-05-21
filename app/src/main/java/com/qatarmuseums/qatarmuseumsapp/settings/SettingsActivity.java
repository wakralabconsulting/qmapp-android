package com.qatarmuseums.qatarmuseumsapp.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.Config;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.home.HomeActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.mahdi.mzip.zip.ZipArchive;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private List<SettingsPageModel> settingsPageModelList = new ArrayList<>();
    private SettingsPageListAdapter settingsPageListAdapter;
    private Animation zoomOutAnimation;
    @BindView(R.id.common_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_back)
    View backArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.settings_page_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.language_change_button)
    View languageChangeButton;
    @BindView(R.id.setting_page_reset_to_default_btn)
    Button settingPageResetToDefaultBtn;
    @BindView(R.id.setting_page_apply_btn)
    Button settingPageApplyButton;
    @BindView(R.id.send_logs_btn)
    View sendLogsButton;
    String language;
    private FirebaseAnalytics mFirebaseAnalytics;
    private File shareFile;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setClickListenerforButtons();
        languageChangeButton.setOnTouchListener((v, event) -> {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                showDialog(LocaleManager.LANGUAGE_ARABIC);
            else
                showDialog(LocaleManager.LANGUAGE_ENGLISH);
            return false;
        });

        language = LocaleManager.getLanguage(this);

        toolbar_title.setText(getResources().getString(R.string.settings_activity_tittle));
        settingsPageListAdapter = new SettingsPageListAdapter(settingsPageModelList);
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
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        sendLogsButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendLogsButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        sendLogsButton.setOnClickListener(v -> {
            Timber.i("Send logs button clicked");
            showEmailConfirmationDialog();
        });
        prepareRecyclerViewData();
    }

    private void shareLogs() {
        Intent queryIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        Intent dataIntent = getDataIntent();

        Intent targetIntent = getSelectiveIntentChooser(this, queryIntent, dataIntent);

        try {
            startActivity(targetIntent);
        } catch (ActivityNotFoundException e) {
            Timber.e("Error: %s", e.getMessage());
        }
    }

    private Intent getDataIntent() {
        Intent dataIntent = new Intent(Intent.ACTION_SEND);
        dataIntent.setType("message/rfc822");
        String to[] = {Config.EMAIL_ID_TO_SEND_LOGS};
        dataIntent.putExtra(Intent.EXTRA_EMAIL, to);
        dataIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Config.EMAIL_SUBJECT);
        dataIntent.putExtra(android.content.Intent.EXTRA_TEXT, Config.EMAIL_MESSAGE);
        ArrayList<Uri> uris = getAttachmentUriList();

        if (uris.size() >= 1) {
            dataIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            dataIntent.putExtra(Intent.EXTRA_STREAM, uris);
        }

        return dataIntent;
    }

    protected ArrayList<Uri> getAttachmentUriList() {
        ArrayList<Uri> uris = new ArrayList<>();
        File logsFilePath = new File(this.getFilesDir(), "logs");

        File logsZipFilePath = new File(this.getFilesDir(), "logs_zip");
        boolean dirExists = true;
        if (!logsZipFilePath.exists()) {
            dirExists = logsZipFilePath.mkdirs();
        }

        if (dirExists) {
            ZipArchive.zip(logsFilePath.getPath(),
                    logsZipFilePath.getPath() + "/" + Config.ZIP_FILE_NAME,
                    Config.ZIP_FILE_PASSWORD);

            shareFile = new File(logsZipFilePath, Config.ZIP_FILE_NAME);

            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.qatarmuseums.qatarmuseumsapp.fileprovider", shareFile);
            uris.add(contentUri);
        }
        return uris;
    }

    public static Intent getSelectiveIntentChooser(Context context, Intent queryIntent, Intent dataIntent) {
        List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(queryIntent,
                PackageManager.MATCH_DEFAULT_ONLY);

        Intent finalIntent = null;

        if (!appList.isEmpty()) {
            List<android.content.Intent> targetedIntents = new ArrayList<>();

            for (ResolveInfo resolveInfo : appList) {
                String packageName = resolveInfo.activityInfo != null ? resolveInfo.activityInfo.packageName : null;

                Intent allowedIntent = new Intent(dataIntent);
                allowedIntent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                allowedIntent.setPackage(packageName);

                targetedIntents.add(allowedIntent);
            }

            if (!targetedIntents.isEmpty()) {
                //Share Intent
                Intent startIntent = targetedIntents.remove(0);

                Intent chooserIntent = android.content.Intent.createChooser(startIntent, "");
                chooserIntent.putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toArray(new Parcelable[]{}));
                // Grant temporary read permission to the content URI
                chooserIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

                finalIntent = chooserIntent;
            }
        }

        if (finalIntent == null) //As a fallback, we are using the sent data intent
            finalIntent = dataIntent;

        return finalIntent;
    }

    public void prepareRecyclerViewData() {
        SettingsPageModel model = new SettingsPageModel(getString(R.string.notification_event_updates));
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_exhibition_updates));
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_museum_updates));
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_culturepass_updates));
        settingsPageModelList.add(model);
        model = new SettingsPageModel(getString(R.string.notification_tourguide_updates));
        settingsPageModelList.add(model);

        settingsPageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.toolbar_back:
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
        if (lang.equalsIgnoreCase(LocaleManager.LANGUAGE_ENGLISH)) {
            ((SwitchCompat) languageChangeButton).setChecked(true);
        } else if (lang.equalsIgnoreCase(LocaleManager.LANGUAGE_ARABIC)) {
            ((SwitchCompat) languageChangeButton).setChecked(false);
        }
        LocaleManager.setNewLocale(this, lang);
        refreshActivity();

    }

    public void refreshActivity() {
        finishAffinity();
        overridePendingTransition(0, 0);
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(0, 0);
    }

    public void setClickListenerforButtons() {
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

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.common_popup, null);
        dialog.setContentView(view);

        ImageView closeBtn = view.findViewById(R.id.close_dialog);
        Button changeLanguageBtn = view.findViewById(R.id.doneBtn);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        dialogTitle.setText(getResources().getString(R.string.change_language_dialog_title));
        changeLanguageBtn.setText(getResources().getString(R.string.continue_button_text));
        dialogContent.setText(getResources().getString(R.string.change_language_content_text));

        changeLanguageBtn.setOnClickListener(view1 -> {
            //Do something
            setLocale(language);
            dialog.dismiss();

        });
        closeBtn.setOnClickListener(view12 -> {
            //Do something
            dialog.dismiss();

        });
        dialog.show();
    }

    protected void showEmailConfirmationDialog() {
        final Dialog dialog = new Dialog(this, R.style.DialogNoAnimation);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.common_popup, null);
        dialog.setContentView(view);

        ImageView closeBtn = view.findViewById(R.id.close_dialog);
        Button continueButton = view.findViewById(R.id.doneBtn);
        TextView dialogTitle = view.findViewById(R.id.dialog_tittle);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        dialogTitle.setText(getResources().getString(R.string.send_email_dialog_title));
        continueButton.setText(getResources().getString(R.string.continue_button_text));
        dialogContent.setText(getResources().getString(R.string.send_email_dialog_message));

        continueButton.setOnClickListener(view1 -> {
            Timber.i("Send mail confirmation approved");
            if (new Util().isNetworkAvailable(this))
                shareLogs();
            dialog.dismiss();
        });
        closeBtn.setOnClickListener(view12 -> {
            Timber.i("Send mail confirmation cancelled");
            dialog.dismiss();
            Toast.makeText(this, getResources().getString(R.string.send_email_cancelled), Toast.LENGTH_SHORT).show();

        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        language = LocaleManager.getLanguage(this);
        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            ((SwitchCompat) languageChangeButton).setChecked(false);
        } else {
            ((SwitchCompat) languageChangeButton).setChecked(true);
        }
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.settings_page), null);
    }
}

package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.dining.DiningDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiningActivity extends AppCompatActivity implements IPullZoom {

    ImageView headerImageView, toolbarClose, favIcon, shareIcon;
    String headerImage;
    String mainTitle;
    boolean isFavourite;
    Toolbar toolbar;
    TextView title, shortDescription, longDescription, timingDetails,
            locationDetails, mapDetails;
    private Util util;
    private Animation zoomOutAnimation;
    private PullToZoomCoordinatorLayout coordinatorLayout;
    private View zoomView;
    private AppBarLayout appBarLayout;
    private int headerOffSetSize;
    private String latitude, longitude;
    Intent intent;
    int language;
    private Intent navigation_intent;
    private String url, id;
    ProgressBar progressBar;
    TextView noResultFoundTxt;
    SharedPreferences qmPreferences;
    LinearLayout timingLayout, locationLayout, diningContent, retryLayout;
    private Integer diningTableRowCount;
    private ArrayList<DiningDetailModel> diningDetailModels = new ArrayList<>();
    private QMDatabase qmDatabase;
    Button retryButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining);
        intent = getIntent();
        headerImage = intent.getStringExtra("HEADER_IMAGE");
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        id = intent.getStringExtra("ID");
        isFavourite = intent.getBooleanExtra("IS_FAVOURITE", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        timingLayout = (LinearLayout) findViewById(R.id.timing_layout);
        locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        diningContent = (LinearLayout) findViewById(R.id.dining_content);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        qmDatabase = QMDatabase.getInstance(DiningActivity.this);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        title = (TextView) findViewById(R.id.main_title);
        shortDescription = (TextView) findViewById(R.id.short_description);
        longDescription = (TextView) findViewById(R.id.long_description);
        timingDetails = (TextView) findViewById(R.id.timing_info);
        locationDetails = (TextView) findViewById(R.id.location_info);
        mapDetails = (TextView) findViewById(R.id.map_info);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);
        util = new Util();
        GlideApp.with(this)
                .load(headerImage)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(headerImageView);
        title.setText(mainTitle);
        if (util.isNetworkAvailable(DiningActivity.this)) {
            getDiningDetailsFromAPI(id, language);
        } else {
            getDiningDetailsFromDatabase(id, language);
        }
        SpannableString ss = new SpannableString(getString(R.string.for_more_information_text));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                url = "http://www.mia.org.qa/en/visiting/idam";
                navigation_intent = new Intent(DiningActivity.this, WebviewActivity.class);
                navigation_intent.putExtra("url", url);
                startActivity(navigation_intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        int length = ss.length();
        ss.setSpan(clickableSpan, length - 4, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), length - 4, length, 0);
        mapDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != null && !latitude.equals("")) {
                    String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + mainTitle + ")";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent);
                } else {
                    util.showLocationAlertDialog(DiningActivity.this);
                }
            }
        });
        if (isFavourite)
            favIcon.setImageResource(R.drawable.heart_fill);
        else
            favIcon.setImageResource(R.drawable.heart_empty);

        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.checkImageResource(DiningActivity.this, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                } else
                    favIcon.setImageResource(R.drawable.heart_fill);
            }
        });
        initViews();

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDiningDetailsFromAPI(id, language);
                progressBar.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            }
        });

        retryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        retryButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        toolbarClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        toolbarClose.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        favIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        favIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }

    private void initViews() {
        coordinatorLayout = (PullToZoomCoordinatorLayout) findViewById(R.id.main_content);
        zoomView = findViewById(R.id.header_img);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        coordinatorLayout.setPullZoom(zoomView, PixelUtil.dp2px(this, 232),
                PixelUtil.dp2px(this, 332), this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                headerOffSetSize = verticalOffset;
            }
        });
    }

    @Override
    public boolean isReadyForPullStart() {
        return headerOffSetSize == 0;
    }

    @Override
    public void onPullZooming(int newScrollValue) {

    }

    @Override
    public void onPullZoomEnd() {

    }


    public void getDiningDetailsFromAPI(String id, int language) {
        progressBar.setVisibility(View.VISIBLE);
        diningContent.setVisibility(View.INVISIBLE);
        final String appLanguage;
        if (language == 1) {
            appLanguage = "en";
        } else {
            appLanguage = "ar";
        }

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<DiningDetailModel>> call = apiService.getDiningDetails(appLanguage, id);
        call.enqueue(new Callback<ArrayList<DiningDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<DiningDetailModel>> call, Response<ArrayList<DiningDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        diningContent.setVisibility(View.VISIBLE);
                        diningDetailModels = response.body();
                        loadData(diningDetailModels.get(0).getDescription(),
                                diningDetailModels.get(0).getOpeningTime(),
                                diningDetailModels.get(0).getClosingTime(),
                                diningDetailModels.get(0).getLocation(),
                                diningDetailModels.get(0).getLatitude(),
                                diningDetailModels.get(0).getLongitude());
                        new DiningRowCount(DiningActivity.this, appLanguage).execute();

                    } else {
                        diningContent.setVisibility(View.INVISIBLE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    diningContent.setVisibility(View.INVISIBLE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<DiningDetailModel>> call, Throwable t) {
                diningContent.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getDiningDetailsFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishDiningData(DiningActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicDiningData(DiningActivity.this, appLanguage, id).execute();
        }
    }

    public void loadData(String longDescription, String openingTime,
                         String closingTime, String locationInfo, String latitudefromApi,
                         String longitudefromApi) {
        this.shortDescription.setText(longDescription);
        latitude = latitudefromApi;
        longitude = longitudefromApi;
        if (openingTime != null) {
            this.timingLayout.setVisibility(View.VISIBLE);
            String time = getResources().getString(R.string.everyday_from) + " " +
                    openingTime + " " + getResources().getString(R.string.to) + " " +
                    closingTime;
            this.timingDetails.setText(time);
        }
        if (locationInfo != null) {
            this.locationLayout.setVisibility(View.VISIBLE);
            this.locationDetails.setText(locationInfo);
        }

        if (latitude != null && !latitude.equals("") && latitude.contains("°")) {
            latitude = convertDegreetoDecimalMeasure(latitude);
            longitude = convertDegreetoDecimalMeasure(longitude);
        }
    }

    private String convertDegreetoDecimalMeasure(String degreeValue) {
        String value = degreeValue.trim();
        String[] latParts = value.split("°");
        float degree = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("'");
        float min = Float.parseFloat(latParts[0]);
        value = latParts[1].trim();
        latParts = value.split("\"");
        float sec = Float.parseFloat(latParts[0]);
        String result;
        result = String.valueOf(degree + (min / 60) + (sec / 3600));
        return result;
    }

    public class DiningRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DiningActivity> activityReference;
        String language;

        DiningRowCount(DiningActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            diningTableRowCount = integer;
            if (diningTableRowCount > 0) {
                if (diningDetailModels.size() > 0)
                    //updateEnglishTable or add row to database
                    new CheckDiningDBRowExist(DiningActivity.this, language).execute();
            } else {
                diningContent.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsArabic();

        }
    }

    public class CheckDiningDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<DiningActivity> activityReference;
        private DiningTableEnglish diningTableEnglish;
        private DiningTableArabic diningTableArabic;
        String language;

        CheckDiningDBRowExist(DiningActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (diningDetailModels.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < diningDetailModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkEnglishIdExist(
                                Integer.parseInt(diningDetailModels.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(DiningActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < diningDetailModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkArabicIdExist(
                                Integer.parseInt(diningDetailModels.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(DiningActivity.this, language, i).execute();

                        }
                    }
                }

            }
            return null;
        }
    }

    public class UpdateDiningTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<DiningActivity> activityReference;
        String language;
        int position;

        UpdateDiningTable(DiningActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningDetailsEnglish(
                        diningDetailModels.get(position).getName(),
                        diningDetailModels.get(position).getImage(),
                        diningDetailModels.get(position).getDescription(),
                        diningDetailModels.get(position).getOpeningTime(),
                        diningDetailModels.get(position).getClosingTime(),
                        diningDetailModels.get(position).getLatitude(),
                        diningDetailModels.get(position).getLongitude(),
                        diningDetailModels.get(position).getId(),
                        diningDetailModels.get(position).getSortId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningDetailsArabic(
                        diningDetailModels.get(position).getName(),
                        diningDetailModels.get(position).getImage(),
                        diningDetailModels.get(position).getDescription(),
                        diningDetailModels.get(position).getOpeningTime(),
                        diningDetailModels.get(position).getClosingTime(),
                        diningDetailModels.get(position).getLatitude(),
                        diningDetailModels.get(position).getLongitude(),
                        diningDetailModels.get(position).getId(),
                        diningDetailModels.get(position).getSortId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public class RetriveEnglishDiningData extends AsyncTask<Void, Void, List<DiningTableEnglish>> {
        private WeakReference<DiningActivity> activityReference;
        int language;
        String diningId;

        RetriveEnglishDiningData(DiningActivity context, int appLanguage, String diningId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.diningId = diningId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<DiningTableEnglish> diningTableEnglishList) {
            if (diningTableEnglishList.get(0).getDescription() != null) {
                loadData(diningTableEnglishList.get(0).getDescription(),
                        diningTableEnglishList.get(0).getOpening_time(),
                        diningTableEnglishList.get(0).getClosing_time(),
                        diningTableEnglishList.get(0).getLocation(),
                        diningTableEnglishList.get(0).getLatitude(),
                        diningTableEnglishList.get(0).getLongitude());
            } else {
                diningContent.setVisibility(View.INVISIBLE);
                retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<DiningTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsEnglish(Integer.parseInt(diningId));
        }
    }

    public class RetriveArabicDiningData extends AsyncTask<Void, Void, List<DiningTableArabic>> {
        private WeakReference<DiningActivity> activityReference;
        int language;
        String diningId;

        RetriveArabicDiningData(DiningActivity context, int appLanguage, String diningId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            this.diningId = diningId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTableArabic> diningTableArabicList) {
            loadData(diningTableArabicList.get(0).getDescription(),
                    diningTableArabicList.get(0).getOpening_time(),
                    diningTableArabicList.get(0).getClosing_time(),
                    diningTableArabicList.get(0).getLocation(),
                    diningTableArabicList.get(0).getLatitude(),
                    diningTableArabicList.get(0).getLongitude());
        }

        @Override
        protected List<DiningTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsArabic(Integer.parseInt(diningId));
        }
    }


}

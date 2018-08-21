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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageOrExhibitionDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutModel;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museumabout.MuseumAboutTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.publicart.PublicArtModel;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements IPullZoom {

    ImageView headerImageView, toolbarClose, favIcon, shareIcon;
    String headerImage;
    String mainTitle, comingFrom;
    boolean isFavourite;
    Toolbar toolbar;
    TextView title, subTitle, shortDescription, longDescription, secondTitle, secondTitleDescription,
            timingTitle, timingDetails, locationDetails, mapDetails, contactDetails;
    private Util util;
    private Animation zoomOutAnimation;
    private PullToZoomCoordinatorLayout coordinatorLayout;
    private View zoomView;
    private AppBarLayout appBarLayout;
    private int headerOffSetSize, appLanguage;
    private LinearLayout secondTitleLayout, timingLayout, contactLayout;
    private String latitude, longitude, id;
    Intent intent;
    int language;
    QMDatabase qmDatabase;
    PublicArtsTableEnglish publicArtsTableEnglish;
    PublicArtsTableArabic publicArtsTableArabic;
    MuseumAboutTableEnglish museumAboutTableEnglish;
    MuseumAboutTableArabic museumAboutTableArabic;
    int publicArtsTableRowCount, heritageTableRowCount,
            exhibitionRowCount, museumAboutRowCount;
    SharedPreferences qmPreferences;
    ProgressBar progressBar;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;
    ArrayList<PublicArtModel> publicArtModel = new ArrayList<>();
    ArrayList<MuseumAboutModel> museumAboutModels = new ArrayList<>();

    ArrayList<HeritageOrExhibitionDetailModel> heritageOrExhibitionDetailModel = new ArrayList<>();
//    ArrayList<HeritageOrExhibitionDetailModel> heritageOrExhibitionDetailModel = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        headerImage = intent.getStringExtra("HEADER_IMAGE");
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        id = intent.getStringExtra("ID");
        isFavourite = intent.getBooleanExtra("IS_FAVOURITE", false);
        qmDatabase = QMDatabase.getInstance(DetailsActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        title = (TextView) findViewById(R.id.main_title);
        subTitle = (TextView) findViewById(R.id.sub_title);
        shortDescription = (TextView) findViewById(R.id.short_description);
        longDescription = (TextView) findViewById(R.id.long_description);
        secondTitle = (TextView) findViewById(R.id.second_title);
        secondTitleDescription = (TextView) findViewById(R.id.second_short_description);
        timingTitle = (TextView) findViewById(R.id.timing_title);
        timingDetails = (TextView) findViewById(R.id.timing_info);
        locationDetails = (TextView) findViewById(R.id.location_info);
        mapDetails = (TextView) findViewById(R.id.map_info);
        contactDetails = (TextView) findViewById(R.id.contact_info);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);
        secondTitleLayout = (LinearLayout) findViewById(R.id.second_title_layout);
        timingLayout = (LinearLayout) findViewById(R.id.timing_layout);
        contactLayout = (LinearLayout) findViewById(R.id.contact_layout);
        commonContentLayout = (LinearLayout) findViewById(R.id.common_content_layout);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);

        util = new Util();
        title.setText(mainTitle);
        if (comingFrom.equals(getString(R.string.sidemenu_exhibition_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, language, "Exhibition_detail_Page.json");
            } else {
                getExhibitionAPIDataFromDatabase(id, language);
            }
        } else if (comingFrom.equals(getString(R.string.sidemenu_heritage_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this)) {
                getHeritageOrExhibitionDetailsFromAPI(id, language, "heritage_detail_Page.json");
            } else {
                getHeritageAPIDataFromDatabase(id, language);
            }

        } else if (comingFrom.equals(getString(R.string.sidemenu_public_arts_text))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getPublicArtDetailsFromAPI(id, language);
            else
                getCommonListAPIDataFromDatabase(id, language);

        } else if (comingFrom.equals(getString(R.string.museum_about))) {
            if (util.isNetworkAvailable(DetailsActivity.this))
                getMuseumAboutDetailsFromAPI(id, language);
            else
                getMuseumAboutDetailsFromDatabase(id, language);
        }
        GlideApp.with(this)
                .load(headerImage)
                .centerCrop()
                .placeholder(R.drawable.placeholdeer)
                .into(headerImageView);


        mapDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUri = getString(R.string.map_navigation_api) + latitude + "," + longitude + " (" + mainTitle + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
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
                if (util.checkImageResource(DetailsActivity.this, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                } else
                    favIcon.setImageResource(R.drawable.heart_fill);
            }
        });
        initViews();

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
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

    private void getCommonListAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishPublicArtsData(DetailsActivity.this, appLanguage).execute();
        } else {
            new RetriveArabicPublicArtsData(DetailsActivity.this, appLanguage).execute();
        }
    }

    private void getHeritageAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishHeritageData(DetailsActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicHeritageData(DetailsActivity.this, appLanguage, id).execute();
        }
    }

    private void getExhibitionAPIDataFromDatabase(String id, int appLanguage) {
        if (appLanguage == 1) {
            new RetriveEnglishExhibitionData(DetailsActivity.this, appLanguage, id).execute();
        } else {
            new RetriveArabicExhibitionData(DetailsActivity.this, appLanguage, id).execute();
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

    public void loadData(String subTitle, String shortDescription, String longDescription,
                         String secondTitle, String secondTitleDescription, String openingTime,
                         String closingTime, String locationInfo, String contactInfo, String latitudefromApi,
                         String longitudefromApi) {
        commonContentLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        this.title.setText(mainTitle);
        latitude = intent.getStringExtra("LATITUDE");
        longitude = intent.getStringExtra("LONGITUDE");
        if (subTitle != null) {
            this.subTitle.setVisibility(View.VISIBLE);
            this.subTitle.setText(subTitle);
        }
        this.shortDescription.setText(shortDescription);
        this.longDescription.setText(longDescription);
        if (secondTitle != null) {
            this.secondTitleLayout.setVisibility(View.VISIBLE);
            this.secondTitle.setText(secondTitle);
            this.secondTitleDescription.setText(secondTitleDescription);
        }
        if (openingTime != null) {
            this.timingLayout.setVisibility(View.VISIBLE);
            String time = getResources().getString(R.string.everyday_from) +
                    " " + openingTime /*+ " " + getResources().getString(R.string.to) + " " +
                    closingTime*/;
            this.timingDetails.setText(time);
        }
        if (locationInfo != null) {
            this.locationDetails.setVisibility(View.VISIBLE);
            this.locationDetails.setText(locationInfo);
        }
        if (contactInfo != null) {
            this.contactLayout.setVisibility(View.VISIBLE);
            this.contactDetails.setText(contactInfo);
        }
        if (latitude != null) {
            if (latitude.contains("°")) {
                latitude = convertDegreetoDecimalMeasure(latitude);
                longitude = convertDegreetoDecimalMeasure(longitude);
            }
        } else {
            latitude = "25.29818300";
            longitude = "51.53972222";
        }
    }

    public void loadDataForHeritageOrExhibitionDetails(String subTitle, String shortDescription, String longDescription,
                                                       String secondTitle, String secondTitleDescription, String timingInfo,
                                                       String locationInfo, String contactInfo, String latitudefromApi,
                                                       String longitudefromApi, String openingTime, String closingtime) {
        commonContentLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        this.title.setText(mainTitle);
        latitude = latitudefromApi;
        longitude = longitudefromApi;
        if (subTitle != null) {
            this.subTitle.setVisibility(View.VISIBLE);
            this.subTitle.setText(subTitle);
        }
        this.shortDescription.setText(shortDescription);
        this.longDescription.setText(longDescription);
        if (secondTitle != null) {
            this.secondTitleLayout.setVisibility(View.VISIBLE);
            this.secondTitle.setText(secondTitle);
            this.secondTitleDescription.setText(secondTitleDescription);
        }
        if (timingInfo != null) {
            timingTitle.setText(R.string.exhibition_timings);
            this.timingLayout.setVisibility(View.VISIBLE);
            this.timingDetails.setText(timingInfo);
        }
        if (openingTime != null) {
            timingTitle.setText(R.string.exhibition_timings);
            this.timingLayout.setVisibility(View.VISIBLE);
            String time = getResources().getString(R.string.everyday_from) + " " +
                    openingTime + " " + getResources().getString(R.string.to) + " " +
                    closingtime;
            this.timingDetails.setText(time);
        }
        if (locationInfo != null) {
            this.locationDetails.setVisibility(View.VISIBLE);
            this.locationDetails.setText(locationInfo);
        }
        if (contactInfo != null) {
            this.contactLayout.setVisibility(View.VISIBLE);
            this.contactDetails.setText(contactInfo);
        }
        if (latitude != null) {
            if (latitude.contains("°")) {
                latitude = convertDegreetoDecimalMeasure(latitude);
                longitude = convertDegreetoDecimalMeasure(longitude);
            }
        } else {
            latitude = "25.29818300";
            longitude = "51.53972222";
        }
    }

    public void getHeritageOrExhibitionDetailsFromAPI(String id, int language, final String pageName) {
        progressBar.setVisibility(View.VISIBLE);
        final String appLanguage;
        if (language == 1) {
            appLanguage = "en";
        } else {
            appLanguage = "ar";
        }

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HeritageOrExhibitionDetailModel>> call = apiService.getHeritageOrExhebitionDetails(appLanguage,
                pageName, id);
        call.enqueue(new Callback<ArrayList<HeritageOrExhibitionDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<HeritageOrExhibitionDetailModel>> call, Response<ArrayList<HeritageOrExhibitionDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        heritageOrExhibitionDetailModel = response.body();
                        if (pageName.equals("heritage_detail_Page.json")) {
                            loadDataForHeritageOrExhibitionDetails(null, heritageOrExhibitionDetailModel.get(0).getShortDescription(),
                                    heritageOrExhibitionDetailModel.get(0).getLongDescription(),
                                    null, null,
                                    null,
                                    heritageOrExhibitionDetailModel.get(0).getLocation(),
                                    null, heritageOrExhibitionDetailModel.get(0).getLatitude(),
                                    heritageOrExhibitionDetailModel.get(0).getLongitude(),
                                    null, null);
                            new HeritageRowCount(DetailsActivity.this, appLanguage).execute();
                        } else {
                            loadDataForHeritageOrExhibitionDetails(null,
                                    heritageOrExhibitionDetailModel.get(0).getShortDescription(),
                                    heritageOrExhibitionDetailModel.get(0).getLongDescription(),
                                    null, null,
                                    null,
                                    heritageOrExhibitionDetailModel.get(0).getLocation(),
                                    null, heritageOrExhibitionDetailModel.get(0).getLatitude(),
                                    heritageOrExhibitionDetailModel.get(0).getLongitude(),
                                    heritageOrExhibitionDetailModel.get(0).getStartDate(),
                                    heritageOrExhibitionDetailModel.get(0).getEndDate());
                            new ExhibitionDetailRowCount(DetailsActivity.this, appLanguage).execute();
                        }

                    } else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<ArrayList<HeritageOrExhibitionDetailModel>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public class ExhibitionDetailRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        ExhibitionDetailRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            exhibitionRowCount = integer;
            if (exhibitionRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckExhibitionDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsArabic();
            }
        }
    }

    public class CheckExhibitionDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckExhibitionDetailDBRowExist(DetailsActivity context, String apiLanguage) {
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
            if (heritageOrExhibitionDetailModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkEnglishIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkArabicIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateArabicTable same id
                            new UpdateExhibitionDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }
            return null;
        }
    }

    public class UpdateExhibitionDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateExhibitionDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
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
            if (language.equals("en")) {
                // updateEnglishTable table with english name

                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionDetailEnglish(
                        heritageOrExhibitionDetailModel.get(position).getStartDate(),
                        heritageOrExhibitionDetailModel.get(position).getEndDate(),
                        heritageOrExhibitionDetailModel.get(position).getLocation(),
                        heritageOrExhibitionDetailModel.get(position).getImage(),
                        heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                        heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );


            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionDetailArabic(
                        heritageOrExhibitionDetailModel.get(position).getStartDate(),
                        heritageOrExhibitionDetailModel.get(position).getEndDate(),
                        heritageOrExhibitionDetailModel.get(position).getLocation(),
                        heritageOrExhibitionDetailModel.get(position).getImage(),
                        heritageOrExhibitionDetailModel.get(position).getLongDescription(),
                        heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveEnglishExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveEnglishExhibitionData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableEnglish> exhibitionListTableEnglishes) {
            super.onPostExecute(exhibitionListTableEnglishes);
        }

        @Override
        protected List<ExhibitionListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionDetailsEnglish(Integer.parseInt(heritageId));

        }
    }

    public class RetriveArabicExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveArabicExhibitionData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableArabic> exhibitionListTableArabics) {
            super.onPostExecute(exhibitionListTableArabics);
        }

        @Override
        protected List<ExhibitionListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionDetailsArabic(Integer.parseInt(heritageId));
        }
    }


    public class HeritageRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        HeritageRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            heritageTableRowCount = integer;
            if (heritageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckHeritageDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsArabic();
            }
        }
    }

    public class CheckHeritageDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;

        CheckHeritageDetailDBRowExist(DetailsActivity context, String apiLanguage) {
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
            if (heritageOrExhibitionDetailModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkEnglishIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritageDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < heritageOrExhibitionDetailModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkArabicIdExist(
                                Integer.parseInt(heritageOrExhibitionDetailModel.get(i).getId()));
                        if (n > 0) {
                            //updateArabicTable same id
                            new UpdateHeritageDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }
            return null;
        }
    }

    public class UpdateHeritageDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateHeritageDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
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
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageDetailEnglish(
                        heritageOrExhibitionDetailModel.get(position).getLocation(),
                        latitude, longitude, heritageOrExhibitionDetailModel.get(position).getLongDescription()
                        , heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageDetailArabic(
                        heritageOrExhibitionDetailModel.get(position).getLocation(),
                        latitude, longitude, heritageOrExhibitionDetailModel.get(position).getLongDescription()
                        , heritageOrExhibitionDetailModel.get(position).getShortDescription(),
                        heritageOrExhibitionDetailModel.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveEnglishHeritageData extends AsyncTask<Void, Void, List<HeritageListTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveEnglishHeritageData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<HeritageListTableEnglish> heritageListTableEnglish) {

            loadDataForHeritageOrExhibitionDetails(null, heritageListTableEnglish.get(0).getHeritage_short_description(),
                    heritageListTableEnglish.get(0).getHeritage_long_description(),
                    null, null,
                    null,
                    heritageListTableEnglish.get(0).getLocation(),
                    null, heritageListTableEnglish.get(0).getLatitude(),
                    heritageListTableEnglish.get(0).getLongitude(), null, null);
        }

        @Override
        protected List<HeritageListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getHeritageDetailsEnglish(Integer.parseInt(heritageId));
        }
    }

    public class RetriveArabicHeritageData extends AsyncTask<Void, Void, List<HeritageListTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;
        String heritageId;

        RetriveArabicHeritageData(DetailsActivity context, int appLanguage, String id) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            heritageId = id;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<HeritageListTableArabic> heritageListTableArabic) {

            loadDataForHeritageOrExhibitionDetails(null, heritageListTableArabic.get(0).getHeritage_short_description(),
                    heritageListTableArabic.get(0).getHeritage_long_description(),
                    null, null,
                    null,
                    heritageListTableArabic.get(0).getLocation(),
                    null, heritageListTableArabic.get(0).getLatitude(),
                    heritageListTableArabic.get(0).getLongitude(), null, null);

        }

        @Override
        protected List<HeritageListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getHeritageDetailsArabic(Integer.parseInt(heritageId));
        }
    }

    private void getPublicArtDetailsFromAPI(String id, int appLanguage) {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<PublicArtModel>> call = apiService.getPublicArtsDetails(language, id);
        call.enqueue(new Callback<ArrayList<PublicArtModel>>() {

            @Override
            public void onResponse(Call<ArrayList<PublicArtModel>> call, Response<ArrayList<PublicArtModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        publicArtModel = response.body();
                        loadData(null,
                                publicArtModel.get(0).getShortDescription(),
                                publicArtModel.get(0).getLongDescription(),
                                null, null, null, null,
                                null, null, latitude, longitude);
                        new PublicArtsRowCount(DetailsActivity.this, language).execute();
                    } else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<PublicArtModel>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        PublicArtsRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            publicArtsTableRowCount = integer;
            if (publicArtsTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckPublicArtsDetailDBRowExist(DetailsActivity.this, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsArabic();
            }


        }
    }

    public class CheckPublicArtsDetailDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        private PublicArtsTableEnglish publicArtsTableEnglish;
        private PublicArtsTableArabic publicArtsTableArabic;
        String language;

        CheckPublicArtsDetailDBRowExist(DetailsActivity context, String apiLanguage) {
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
            if (publicArtModel.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < publicArtModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkEnglishIdExist(
                                Integer.parseInt(publicArtModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                } else {
                    for (int i = 0; i < publicArtModel.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkArabicIdExist(
                                Integer.parseInt(publicArtModel.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsDetailTable(DetailsActivity.this, language, i).execute();

                        }
                    }
                }
            }

            return null;
        }
    }

    public class UpdatePublicArtsDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdatePublicArtsDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
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
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsDetailEnglish(
                        publicArtModel.get(position).getName(),
                        latitude, longitude, publicArtModel.get(position).getShortDescription()
                        , publicArtModel.get(position).getLongDescription(), publicArtModel.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsDetailArabic(
                        publicArtModel.get(position).getName(),
                        latitude, longitude, publicArtModel.get(position).getShortDescription()
                        , publicArtModel.get(position).getLongDescription(), publicArtModel.get(position).getId()
                );
            }
            return null;
        }
    }


    public class RetriveEnglishPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableEnglish>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;

        RetriveEnglishPublicArtsData(DetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableEnglish> publicArtsTableEnglish) {
            for (int i = 0; i < publicArtsTableEnglish.size(); i++) {

                loadData(null,
                        publicArtsTableEnglish.get(i).getShort_description(),
                        publicArtsTableEnglish.get(i).getDescription(),
                        null, null, null,
                        null, null, null,
                        publicArtsTableEnglish.get(i).getLatitude(),
                        publicArtsTableEnglish.get(i).getLongitude());
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<PublicArtsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllEnglish();

        }
    }

    public class RetriveArabicPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableArabic>> {

        private WeakReference<DetailsActivity> activityReference;
        int language;

        RetriveArabicPublicArtsData(DetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableArabic> publicArtsTableArabic) {
            for (int i = 0; i < publicArtsTableArabic.size(); i++) {
                loadData(null,
                        publicArtsTableArabic.get(i).getShort_description(),
                        publicArtsTableArabic.get(i).getDescription(),
                        null, null, null,
                        null, null, null,
                        publicArtsTableArabic.get(i).getLatitude(),
                        publicArtsTableArabic.get(i).getLongitude());
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<PublicArtsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllArabic();
        }
    }

    public void getMuseumAboutDetailsFromAPI(String id, int appLanguage) {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getTempClient().create(APIInterface.class);
        Call<ArrayList<MuseumAboutModel>> call = apiService.getMuseumAboutDetails("63");
        call.enqueue(new Callback<ArrayList<MuseumAboutModel>>() {
            @Override
            public void onResponse(Call<ArrayList<MuseumAboutModel>> call, Response<ArrayList<MuseumAboutModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        museumAboutModels = response.body();
                        timingTitle.setText(R.string.museum_timings);
                        headerImage = museumAboutModels.get(0).getImage();
                        GlideApp.with(DetailsActivity.this)
                                .load(headerImage)
                                .centerCrop()
                                .placeholder(R.drawable.placeholdeer)
                                .into(headerImageView);
                        loadData(null, museumAboutModels.get(0).getShortDescription(),
                                null,
                                museumAboutModels.get(0).getSubTitle(), museumAboutModels.get(0).getLongDescription(), museumAboutModels.get(0).getTimingInfo(), "",
                                null, museumAboutModels.get(0).getContact(), museumAboutModels.get(0).getLatitude(), museumAboutModels.get(0).getLongitude());
                        new MuseumAboutRowCount(DetailsActivity.this, language).execute();

                    } else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<MuseumAboutModel>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                commonContentLayout.setVisibility(View.GONE);
                noResultFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumAboutDetailsFromDatabase(String id, int language) {
        if (language == 1) {
            progressBar.setVisibility(View.VISIBLE);
            new RetriveMuseumAboutDataEnglish(DetailsActivity.this, language).execute();
        } else {
            new RetriveMuseumAboutDataArabic(DetailsActivity.this, language).execute();
        }
    }

    public class MuseumAboutRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<DetailsActivity> activityReference;
        String language;


        MuseumAboutRowCount(DetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            museumAboutRowCount = integer;
            if (museumAboutRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumAboutDBRowExist(DetailsActivity.this, language).execute();
            } else {
                //create databse
                new InsertMuseumAboutDatabaseTask(DetailsActivity.this, museumAboutTableEnglish,
                        museumAboutTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en")) {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsEnglish();
            } else {
                return activityReference.get().qmDatabase.getMuseumAboutDao().getNumberOfRowsArabic();
            }


        }
    }


    public class CheckMuseumAboutDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
        String language;

        CheckMuseumAboutDBRowExist(DetailsActivity context, String apiLanguage) {
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
            if (museumAboutModels.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkEnglishIdExist(
                                Integer.parseInt(museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumAboutDetailTable(DetailsActivity.this, language, i).execute();

                        }else {
                            museumAboutTableEnglish = new MuseumAboutTableEnglish(Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                    museumAboutModels.get(i).getTitle(),
                                    museumAboutModels.get(i).getSubTitle(),
                                    museumAboutModels.get(i).getImage(),
                                    museumAboutModels.get(i).getShortDescription(),
                                    museumAboutModels.get(i).getLongDescription(),
                                    museumAboutModels.get(i).getTimingInfo(),
                                    museumAboutModels.get(i).getContact(), museumAboutModels.get(i).getFilter(),
                                    museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumAboutDao().checkArabicIdExist(
                                Integer.parseInt(museumAboutModels.get(i).getMuseumId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsDetailTable(DetailsActivity.this, language, i).execute();

                        }else {
                            museumAboutTableArabic = new MuseumAboutTableArabic(Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                    museumAboutModels.get(i).getTitle(),
                                    museumAboutModels.get(i).getSubTitle(),
                                    museumAboutModels.get(i).getImage(),
                                    museumAboutModels.get(i).getShortDescription(),
                                    museumAboutModels.get(i).getLongDescription(),
                                    museumAboutModels.get(i).getTimingInfo(),
                                    museumAboutModels.get(i).getContact(), museumAboutModels.get(i).getFilter(),
                                    museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude());
                            activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                        }
                    }
                }
            }

            return null;
        }
    }

    public class InsertMuseumAboutDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetailsActivity> activityReference;
        private MuseumAboutTableEnglish museumAboutTableEnglish;
        private MuseumAboutTableArabic museumAboutTableArabic;
        String language;

        InsertMuseumAboutDatabaseTask(DetailsActivity context, MuseumAboutTableEnglish museumAboutTableEnglish,
                                      MuseumAboutTableArabic museumAboutTableArabic, String lan) {
            activityReference = new WeakReference<>(context);
            this.museumAboutTableEnglish = museumAboutTableEnglish;
            this.museumAboutTableArabic = museumAboutTableArabic;
            language = lan;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (museumAboutModels != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        museumAboutTableEnglish = new MuseumAboutTableEnglish(Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                museumAboutModels.get(i).getTitle(),
                                museumAboutModels.get(i).getSubTitle(),
                                museumAboutModels.get(i).getImage(),
                                museumAboutModels.get(i).getShortDescription(),
                                museumAboutModels.get(i).getLongDescription(),
                                museumAboutModels.get(i).getTimingInfo(),
                                museumAboutModels.get(i).getContact(), museumAboutModels.get(i).getFilter(),
                                museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableEnglish);

                    }
                } else {
                    for (int i = 0; i < museumAboutModels.size(); i++) {
                        museumAboutTableArabic = new MuseumAboutTableArabic(Long.parseLong(museumAboutModels.get(i).getMuseumId()),
                                museumAboutModels.get(i).getTitle(),
                                museumAboutModels.get(i).getSubTitle(),
                                museumAboutModels.get(i).getImage(),
                                museumAboutModels.get(i).getShortDescription(),
                                museumAboutModels.get(i).getLongDescription(),
                                museumAboutModels.get(i).getTimingInfo(),
                                museumAboutModels.get(i).getContact(), museumAboutModels.get(i).getFilter(),
                                museumAboutModels.get(i).getLatitude(), museumAboutModels.get(i).getLongitude());
                        activityReference.get().qmDatabase.getMuseumAboutDao().insert(museumAboutTableArabic);

                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public class UpdateMuseumAboutDetailTable extends AsyncTask<Void, Void, Void> {

        private WeakReference<DetailsActivity> activityReference;
        String language;
        int position;

        UpdateMuseumAboutDetailTable(DetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
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
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataEnglish(
                        museumAboutModels.get(position).getTitle(), museumAboutModels.get(position).getSubTitle(),
                        museumAboutModels.get(position).getImage(), museumAboutModels.get(position).getShortDescription(),
                        museumAboutModels.get(position).getLongDescription(), museumAboutModels.get(position).getTimingInfo(),
                        museumAboutModels.get(position).getLatitude(), museumAboutModels.get(position).getLatitude(), museumAboutModels.get(position).getContact(),
                        museumAboutModels.get(position).getFilter(), museumAboutModels.get(position).getMuseumId());
            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getMuseumAboutDao().updateMuseumAboutDataArabic(
                        museumAboutModels.get(position).getTitle(), museumAboutModels.get(position).getSubTitle(),
                        museumAboutModels.get(position).getImage(), museumAboutModels.get(position).getShortDescription(),
                        museumAboutModels.get(position).getLongDescription(), museumAboutModels.get(position).getTimingInfo(),
                        museumAboutModels.get(position).getLatitude(), museumAboutModels.get(position).getLatitude(), museumAboutModels.get(position).getContact(),
                        museumAboutModels.get(position).getFilter(), museumAboutModels.get(position).getMuseumId());

            }
            return null;
        }
    }

    public class RetriveMuseumAboutDataEnglish extends AsyncTask<Void, Void, MuseumAboutTableEnglish> {
        private WeakReference<DetailsActivity> activityReference;
        int language;

        RetriveMuseumAboutDataEnglish(DetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected MuseumAboutTableEnglish doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getAllMuseumAboutDataEnglish();
        }

        @Override
        protected void onPostExecute(MuseumAboutTableEnglish museumAboutTableEnglish) {
            if (museumAboutTableEnglish != null) {
                headerImage = museumAboutTableEnglish.getMuseum_image();
                GlideApp.with(DetailsActivity.this)
                        .load(headerImage)
                        .centerCrop()
                        .placeholder(R.drawable.placeholdeer)
                        .into(headerImageView);
                loadData(null, museumAboutTableEnglish.getMuseum_short_description(),
                        null,
                        museumAboutTableEnglish.getMuseum_subtitle(), museumAboutTableEnglish.getMuseum_long_description(),
                        museumAboutTableEnglish.getMuseum_opening_time(), "",
                        null, museumAboutTableEnglish.getMuseum_contact(),
                        museumAboutTableEnglish.getMuseum_lattitude(), museumAboutTableEnglish.getMuseum_longitude());

            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetriveMuseumAboutDataArabic extends AsyncTask<Void, Void, MuseumAboutTableArabic> {
        private WeakReference<DetailsActivity> activityReference;
        int language;

        RetriveMuseumAboutDataArabic(DetailsActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected MuseumAboutTableArabic doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumAboutDao().getAllMuseumAboutDataArabic();
        }

        @Override
        protected void onPostExecute(MuseumAboutTableArabic museumAboutTableArabic) {
            if (museumAboutTableArabic != null) {
                headerImage = museumAboutTableEnglish.getMuseum_image();
                GlideApp.with(DetailsActivity.this)
                        .load(headerImage)
                        .centerCrop()
                        .placeholder(R.drawable.placeholdeer)
                        .into(headerImageView);
                loadData(null, museumAboutTableArabic.getMuseum_short_description(),
                        null,
                        museumAboutTableArabic.getMuseum_subtitle(), museumAboutTableArabic.getMuseum_long_description(),
                        museumAboutTableArabic.getMuseum_opening_time(), "",
                        null, museumAboutTableArabic.getMuseum_contact(),
                        museumAboutTableArabic.getMuseum_lattitude(), museumAboutTableArabic.getMuseum_longitude());

            }
            progressBar.setVisibility(View.GONE);
        }
    }


}

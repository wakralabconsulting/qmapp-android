package com.qatarmuseums.qatarmuseumsapp.detailspage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.heritage.HeritageDetailModel;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.IPullZoom;
import com.qatarmuseums.qatarmuseumsapp.utils.PixelUtil;
import com.qatarmuseums.qatarmuseumsapp.utils.PullToZoomCoordinatorLayout;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.util.ArrayList;

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
    private int headerOffSetSize;
    private LinearLayout secondTitleLayout, timingLayout, contactLayout;
    private String latitude, longitude, id;
    Intent intent;
    int language;
    SharedPreferences qmPreferences;
    ProgressBar progressBar;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;

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
            timingTitle.setText(R.string.exhibition_timings);
            loadData(null, getString(R.string.details_page_short_description),
                    getString(R.string.details_page_long_description),
                    null, null, getString(R.string.details_page_timing_details),
                    getString(R.string.details_page_location_details), getString(R.string.contact_mail), "", "");
        } else if (comingFrom.equals(getString(R.string.sidemenu_heritage_text))) {
            getHeritageDetailsFromAPI(id, language);

        } else if (comingFrom.equals(getString(R.string.sidemenu_public_arts_text))) {
            loadData("AN ARRESTING INSTALLATION", getString(R.string.details_page_short_description),
                    getString(R.string.details_page_long_description),
                    "A WORLD HERITAGE SITE", "Once a thriving port bustling with fishermen and merchants, the town of Al Zubarah was designated a protected area in 2009. Since then, Qatar Museums has led teams of archaeologists and scientists to investigate the site. Through their research and engagement with local communities, they are documenting and shedding light on the rise and fall of this unique area.\n" +
                            "\n" +
                            "In 2013 the World Heritage Committee inscribed Al Zubarah Archaeological Site into the UNESCO World Heritage List. The site includes three major features, the largest of which are the archaeological remains of the town, dating back to the 1760s. Connected to it is the settlement of Qal’at Murair, which was fortified to protect the city’s inland wells. Al Zubarah Fort was built in 1938 and is the youngest, most prominent feature at the site.",
                    null,
                    "Katara Cultural Village", null, "", "");
        } else if (comingFrom.equals(getString(R.string.museum_about))) {
            timingTitle.setText(R.string.museum_timings);
            headerImage = "http://www.qm.org.qa/sites/default/files/styles/gallery_small/public/images/gallery/mia_architecture_071215_4844.jpg";
            loadData(null, getString(R.string.details_page_short_description),
                    getString(R.string.details_page_long_description),
                    "TRADITIONAL INSPIRATION", "Once a thriving port bustling with fishermen and merchants, the town of Al Zubarah was designated a protected area in 2009. Since then, Qatar Museums has led teams of archaeologists and scientists to investigate the site. Through their research and engagement with local communities, they are documenting and shedding light on the rise and fall of this unique area.\n" +
                            "\n" +
                            "In 2013 the World Heritage Committee inscribed Al Zubarah Archaeological Site into the UNESCO World Heritage List. The site includes three major features, the largest of which are the archaeological remains of the town, dating back to the 1760s. Connected to it is the settlement of Qal’at Murair, which was fortified to protect the city’s inland wells. Al Zubarah Fort was built in 1938 and is the youngest, most prominent feature at the site.",
                    "Saturday to Sunday: 9:00AM - 7:00PM\n" +
                            "Fridays: 1:30PM to 7:00PM",
                    "Katara Cultural Village", "info@mia.org.qa", "", "");
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
                         String secondTitle, String secondTitleDescription, String timingInfo,
                         String locationInfo, String contactInfo, String latitudefromApi,
                         String longitudefromApi) {
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
            this.timingLayout.setVisibility(View.VISIBLE);
            this.timingDetails.setText(timingInfo);
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
            latitude = convertDegreetoDecimalMeasure(latitude);
            longitude = convertDegreetoDecimalMeasure(longitude);
        } else {
            latitude = "25.29818300";
            longitude = "51.53972222";
        }
    }

    public void getHeritageDetailsFromAPI(String id, int language) {
        progressBar.setVisibility(View.VISIBLE);
        String appLanguage;
        if (language == 1) {
            appLanguage = "en";
        } else {
            appLanguage = "ar";
        }

        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HeritageDetailModel>> call = apiService.getHeritageDetails(appLanguage,id);
        call.enqueue(new Callback<ArrayList<HeritageDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<HeritageDetailModel>> call, Response<ArrayList<HeritageDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                   ArrayList<HeritageDetailModel> heritageDetailModel = response.body();
                        timingTitle.setText(R.string.opening_timings);
                        loadData("", heritageDetailModel.get(0).getShortDescription(),
                                heritageDetailModel.get(0).getLongDescription(),
                                "", "",
                                null,
                                heritageDetailModel.get(0).getLocation(),
                                null, heritageDetailModel.get(0).getLatitude(),
                                heritageDetailModel.get(0).getLongitude());

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
            public void onFailure(Call<ArrayList<HeritageDetailModel>> call, Throwable t) {


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
}

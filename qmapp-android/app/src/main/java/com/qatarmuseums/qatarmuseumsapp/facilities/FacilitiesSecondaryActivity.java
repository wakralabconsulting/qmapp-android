package com.qatarmuseums.qatarmuseumsapp.facilities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacilitiesSecondaryActivity extends AppCompatActivity {


    private ProgressBar progressBar;
    private ImageView toolbarBack;
    private Animation zoomOutAnimation;
    private Button retryButton;
    private LinearLayout retryLayout;
    private RecyclerView recyclerView;
    private FacilitiesSecondaryAdapter mAdapter;
    private Intent navigationIntent;
    private String comingFrom, facilityId;
    private ArrayList<FacilitiesDetailModel> facilitiesDetailList = new ArrayList<>();
    private Util util;
    private RelativeLayout noResultsLayout;
    private String language;
    private QMDatabase qmDatabase;
    private String[] splitArray;
    private String startTime, endTime;
    private long startTimeStamp, endTimeStamp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_secondary_list);
        language = LocaleManager.getLanguage(this);
        progressBar = findViewById(R.id.progressBarLoading);
        Intent intent = getIntent();
        String mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        facilityId = intent.getStringExtra("ID");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarBack = findViewById(R.id.toolbar_back);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        retryLayout = findViewById(R.id.retry_layout);
        noResultsLayout = findViewById(R.id.no_result_layout);
        retryButton = findViewById(R.id.retry_btn);
        recyclerView = findViewById(R.id.tour_recycler_view);
        qmDatabase = QMDatabase.getInstance(FacilitiesSecondaryActivity.this);
        util = new Util();
        mAdapter = new FacilitiesSecondaryAdapter(this, facilitiesDetailList, position -> {
            navigationIntent = new Intent(FacilitiesSecondaryActivity.this, DetailsActivity.class);
            if (facilitiesDetailList.get(position).getFacilityImage().size() > 0) {
                navigationIntent.putExtra("HEADER_IMAGE", facilitiesDetailList.get(position).getFacilityImage().get(0));
            }
            navigationIntent.putExtra("MAIN_TITLE", facilitiesDetailList.get(position).getFacilitiesTitle());
            navigationIntent.putExtra("SUB_TITLE", facilitiesDetailList.get(position).getFacilitiesSubtitle());
            navigationIntent.putExtra("DESCRIPTION", facilitiesDetailList.get(position).getFacilityDescription());
            navigationIntent.putExtra("TIMING", facilitiesDetailList.get(position).getFacilitiesTiming());
            navigationIntent.putExtra("TITLE_TIMING", facilitiesDetailList.get(position).getFacilityTitleTiming());
            navigationIntent.putExtra("ID", facilitiesDetailList.get(position).getFacilitiesId());
            navigationIntent.putExtra("COMING_FROM", getString(R.string.facility_sublist));
            navigationIntent.putExtra("LONGITUDE", facilitiesDetailList.get(position).getLongitude());
            navigationIntent.putExtra("LATITUDE", facilitiesDetailList.get(position).getLattitude());
            navigationIntent.putExtra("CATEGORY_ID", facilitiesDetailList.get(position).getFacilitiesCategoryId());
            navigationIntent.putExtra("LOCATION_TITLE", facilitiesDetailList.get(position).getLocationTitle());
            startActivity(navigationIntent);
        });


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);

        retryButton.setOnClickListener(v -> {
            getFacilityDetailsFromAPI(facilityId);
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        toolbarBack.setOnClickListener(v -> onBackPressed());
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarBack.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarBack.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });

        toolbarTitle.setText(mainTitle);
        if (util.isNetworkAvailable(this))
            getFacilityDetailsFromAPI(facilityId);
        else
            getFacilityDetailsFromDatabase(facilityId);





    }



    public void getFacilityDetailsFromAPI(String id){
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<FacilitiesDetailModel>> call = apiService.getFacilityDetails(language, id);
        call.enqueue(new Callback<ArrayList<FacilitiesDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FacilitiesDetailModel>> call, Response<ArrayList<FacilitiesDetailModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        facilitiesDetailList.addAll(response.body());
                        removeHtmlTags(facilitiesDetailList);
                        mAdapter.notifyDataSetChanged();

                    }else {
                        recyclerView.setVisibility(View.GONE);
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<FacilitiesDetailModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    public void getFacilityDetailsFromDatabase(String id){

    }
    public void removeHtmlTags(ArrayList<FacilitiesDetailModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setFacilitiesSubtitle(util.html2string(models.get(i).getFacilitiesSubtitle()));
            models.get(i).setFacilitiesTitle(util.html2string(models.get(i).getFacilitiesTitle()));
            models.get(i).setFacilityDescription(util.html2string(models.get(i).getFacilityDescription()));
            models.get(i).setFacilitiesTiming(util.html2string(models.get(i).getFacilitiesTiming()));


        }
    }
}

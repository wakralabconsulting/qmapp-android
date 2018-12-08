package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourDetailsActivity extends AppCompatActivity {

    private SharedPreferences qmPreferences;
    private ProgressBar progressBar;
    private Intent intent;
    private String mainTitle, comingFrom, headerImage, description, contactInfo, id;
    private Toolbar toolbar;
    private ImageView toolbarClose;
    private LinearLayout retryLayout;
    private TextView noResultFoundTxt;
    private Button retryButton;
    private Util util;
    private Animation zoomOutAnimation;
    private RecyclerView recyclerView;
    private TourDetailsAdapter mAdapter;
    private ArrayList<TourDetailsModel> tourDetailsList = new ArrayList<>();
    private int appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        headerImage = intent.getStringExtra("HEADER_IMAGE");
        description = intent.getStringExtra("LONG_DESC");
        contactInfo = intent.getStringExtra("CONTACT");
        id = intent.getStringExtra("ID");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        noResultFoundTxt = (TextView) findViewById(R.id.no_result_view);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout);
        retryButton = (Button) findViewById(R.id.retry_btn);
        recyclerView = (RecyclerView) findViewById(R.id.tour_details_recycler_view);
        mAdapter = new TourDetailsAdapter(this, tourDetailsList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setFocusable(false);
        util = new Util();
        toolbarClose.setOnClickListener(v ->
                onBackPressed());

        zoomOutAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        getTourDetailFromAPI();
    }

    public void getTourDetailFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<TourDetailsModel>> call = apiService.getTourDetails(language, id);
        call.enqueue(new Callback<ArrayList<TourDetailsModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TourDetailsModel>> call, Response<ArrayList<TourDetailsModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tourDetailsList.addAll(response.body());
                        removeHtmlTags(tourDetailsList);
                        mAdapter.notifyDataSetChanged();
//                        new CollectionDetailRowCount(TourDetailsActivity.this, appLanguage).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<TourDetailsModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void removeHtmlTags(ArrayList<TourDetailsModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setTourTitle(util.html2string(models.get(i).getTourTitle()));
            models.get(i).setTourDate(util.html2string(models.get(i).getTourDate()));
            models.get(i).setTourBody(util.html2string(models.get(i).getTourBody()));

        }
    }

}

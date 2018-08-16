package com.qatarmuseums.qatarmuseumsapp.park;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkActivity extends AppCompatActivity {

    private List<ParkList> parkLists = new ArrayList<>();
    private ParkListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView headerImageView, favIcon, shareIcon, toolbarClose;
    private Toolbar toolbar;
    private Util util;
    private Animation zoomOutAnimation;
    SharedPreferences qmPreferences;
    ProgressBar progressBar;
    RelativeLayout noResultFoundLayout;
    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        recyclerView = (RecyclerView) findViewById(R.id.park_recycler_view);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);
        mAdapter = new ParkListAdapter(this, parkLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        getParkDetailsFromAPI(appLanguage);
        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        util = new Util();
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.checkImageResource(ParkActivity.this, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                } else
                    favIcon.setImageResource(R.drawable.heart_fill);
            }
        });
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



    public void getParkDetailsFromAPI(int lan){
       String language;
        if (lan == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ParkList>> call = apiService.getParkDetails(language);
        call.enqueue(new Callback<ArrayList<ParkList>>() {
            @Override
            public void onResponse(Call<ArrayList<ParkList>> call, Response<ArrayList<ParkList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        parkLists.addAll(response.body());
                        GlideApp.with(ParkActivity.this)
                                .load(parkLists.get(0).getImage())
                                .centerCrop()
                                .placeholder(R.drawable.placeholdeer)
                                .into(headerImageView);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mainLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainLayout.setVisibility(View.GONE);
                    noResultFoundLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ParkList>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());
                } else {
                    // due to mapping issues
                }
                mainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}

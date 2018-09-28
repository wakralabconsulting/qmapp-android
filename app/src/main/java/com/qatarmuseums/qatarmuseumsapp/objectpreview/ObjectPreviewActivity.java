package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.booking.rtlviewpager.RtlViewPager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ObjectPreviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView backBtn, shareBtn, locationBtn;
    private RecyclerView stepIndicatorRecyclerView;
    private StepIndicatorAdapter stepIndicatorAdapter;
    private List<CurrentIndicatorPosition> currentIndicatorPositionList = new ArrayList<>();
    Animation zoomOutAnimation;
    ProgressBar progressBar;
    LinearLayout commonContentLayout;
    TextView noResultFoundTxt;
    Intent intent;
    String tourId;
    int language;
    SharedPreferences qmPreferences;
    private Util util;
    ViewPager pager;
    ArrayList<ObjectPreviewModel> objectPreviewModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview);
        setSupportActionBar(toolbar);
        intent = getIntent();

        util = new Util();
        tourId = intent.getStringExtra("TOURID");
        tourId="12216";
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.back_btn);
        shareBtn = findViewById(R.id.share_btn);
        locationBtn = findViewById(R.id.location_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        commonContentLayout = (LinearLayout) findViewById(R.id.main_content_layout);
        noResultFoundTxt = (TextView) findViewById(R.id.noResultFoundTxt);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
         pager = (RtlViewPager) findViewById(R.id.pager);
        assert pager != null;
        stepIndicatorRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);


        RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
        stepIndicatorRecyclerView.addOnItemTouchListener(disabler);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);


        getObjectPreviewDetailsFromAPI(tourId,language);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                currentIndicatorPositionList.clear();
                CurrentIndicatorPosition c = new CurrentIndicatorPosition(position);
                currentIndicatorPositionList.add(c);
                stepIndicatorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        backBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        shareBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ObjectPreviewActivity.this, FloorMapActivity.class);
                startActivity(i);
            }
        });
        locationBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        locationBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });


    }

    public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width / 5;
        return width;
    }

    public void getObjectPreviewDetailsFromAPI(String id, int appLanguage){
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<ObjectPreviewModel>> call = apiService.getObjectPreviewDetails(language, id);
        call.enqueue(new Callback<ArrayList<ObjectPreviewModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ObjectPreviewModel>> call, Response<ArrayList<ObjectPreviewModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        commonContentLayout.setVisibility(View.VISIBLE);
                        objectPreviewModels = response.body();
                        if (objectPreviewModels.size()<6)
                        stepIndicatorAdapter = new StepIndicatorAdapter(currentIndicatorPositionList, objectPreviewModels.size(), getScreenWidth(),objectPreviewModels.size());
                        else
                            stepIndicatorAdapter = new StepIndicatorAdapter(currentIndicatorPositionList, 5, getScreenWidth(),objectPreviewModels.size());
                        currentIndicatorPositionList.clear();
                        CurrentIndicatorPosition c = new CurrentIndicatorPosition(0);
                        currentIndicatorPositionList.add(c);
                        stepIndicatorAdapter.notifyDataSetChanged();
                        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ObjectPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        stepIndicatorRecyclerView.setLayoutManager(horizontalLayoutManager);
                        stepIndicatorRecyclerView.setAdapter(stepIndicatorAdapter);
                        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(),objectPreviewModels.size(),objectPreviewModels));
                    }else {
                        commonContentLayout.setVisibility(View.GONE);
                        noResultFoundTxt.setVisibility(View.VISIBLE);
                    }

                }else {
                    commonContentLayout.setVisibility(View.GONE);
                    noResultFoundTxt.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<ObjectPreviewModel>> call, Throwable t) {
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

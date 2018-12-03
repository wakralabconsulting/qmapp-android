package com.qatarmuseums.qatarmuseumsapp.tourdetails;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonModel;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;

import java.util.ArrayList;

public class TourDetailsActivity extends AppCompatActivity {

    private SharedPreferences qmPreferences;
    private int language;
    private ProgressBar progressBar;
    private Intent intent;
    private String mainTitle;
    private Toolbar toolbar;
    private TextView title;
    private TextView shortDescription;
    private ImageView toolbarBack;
    private Animation zoomOutAnimation;
    private Button retryButton;
    private LinearLayout retryLayout;
    private RecyclerView recyclerView;
    private TourListAdapter mAdapter;
    private Intent navigationIntent;
    private ArrayList<CommonModel> models = new ArrayList<>();
    private String comingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = qmPreferences.getInt("AppLanguage", 1);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        intent = getIntent();
        mainTitle = intent.getStringExtra("MAIN_TITLE");
        comingFrom = intent.getStringExtra("COMING_FROM");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarBack = (ImageView) findViewById(R.id.toolbar_back);
        title = (TextView) findViewById(R.id.main_title);
        shortDescription = (TextView) findViewById(R.id.short_description);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout_about);
        retryButton = (Button) findViewById(R.id.retry_btn);
        recyclerView = (RecyclerView) findViewById(R.id.tour_recycler_view);
        mAdapter = new TourListAdapter(this, models, position -> {
            navigationIntent = new Intent(TourDetailsActivity.this, DetailsActivity.class);
            navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImage());
            navigationIntent.putExtra("MAIN_TITLE", models.get(position).getName());
            navigationIntent.putExtra("LONG_DESC", models.get(position).getDescription());
            navigationIntent.putExtra("ID", models.get(position).getId());
            navigationIntent.putExtra("COMING_FROM", comingFrom);
            navigationIntent.putExtra("IS_FAVOURITE", models.get(position).getIsfavourite());
            navigationIntent.putExtra("LONGITUDE", models.get(position).getLongitude());
            navigationIntent.putExtra("LATITUDE", models.get(position).getLatitude());
            navigationIntent.putExtra("PUBLIC_ARTS_ID", models.get(position).getId());
            startActivity(navigationIntent);
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
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

        title.setText(mainTitle);
        title.setAllCaps(false);
        title.setTextSize(33);
        getTourData();
    }

    public void getTourData() {
        recyclerView.setVisibility(View.VISIBLE);
        CommonModel model = new CommonModel("10", "8 AM", "11 AM", "Day Tour",
                "https://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/qiff-800.jpg", true);
        models.add(model);
        model = new CommonModel("11", "6 PM", "10 PM", "Evening Tour",
                "https://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/luc-tuymans-blog-3.jpg", true);
        models.add(model);
        mAdapter.notifyDataSetChanged();
    }

}

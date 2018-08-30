package com.qatarmuseums.qatarmuseumsapp.tourguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.HomeList;
import com.qatarmuseums.qatarmuseumsapp.home.HomeListAdapter;

import java.util.ArrayList;

public class TourGuideCommonActivity extends AppCompatActivity {
    TextView tourguideMainTitle,tourguideSubTitle,tourguideMainDesc,tourguideSubDesc;
    LinearLayout exploreLayout,tourguideSubtitleLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    ImageView backButton;
    String comingFrom;
    private TourGuideAdapter mAdapter;
    private ArrayList<TourGuideList> tourGuideList = new ArrayList<>();
    TourGuideList tourguideObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        comingFrom = intent.getStringExtra("COMING_FROM");

        tourguideMainTitle = (TextView) findViewById(R.id.tourguide_tittle);
        tourguideSubTitle = (TextView) findViewById(R.id.tourguide_subtittle);
        tourguideMainDesc = (TextView) findViewById(R.id.tourguide_title_desc);
        tourguideSubDesc = (TextView) findViewById(R.id.tourguide_subtitle_desc);
        exploreLayout = (LinearLayout) findViewById(R.id.explore_layout);
        tourguideSubtitleLayout = (LinearLayout) findViewById(R.id.tourguide_subtitle_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tourguide_recycler_view);
        backButton = (ImageView) findViewById(R.id.toolbar_close);

        if (!comingFrom.equals(getString(R.string.sidemenu_text))) {
            tourguideSubtitleLayout.setVisibility(View.VISIBLE);
            tourguideMainTitle.setText(getString(R.string.tourguide_title));
            tourguideMainDesc.setText(getString(R.string.tourguide_title_desc));
            tourguideSubTitle.setText(getString(R.string.tourguide_title));
            tourguideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapter = new TourGuideAdapter(this, tourGuideList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareData();
    }
    public void prepareData(){
        tourguideObject = new TourGuideList("ScienceTour",R.drawable.science_tour);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("MIA Highlights Tour",R.drawable.mia);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Coming Soon",R.drawable.coming_soon_1);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Coming Soon",R.drawable.coming_soon_2);
        tourGuideList.add(tourguideObject);
        mAdapter.notifyDataSetChanged();

    }
}

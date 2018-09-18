package com.qatarmuseums.qatarmuseumsapp.tourguidedetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.tourguide.TourGuideAdapter;
import com.qatarmuseums.qatarmuseumsapp.tourguide.TourGuideList;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.SelfGuidedStartPageActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

public class TourGuideDetailsActivity extends AppCompatActivity {
    TextView tourguideMainTitle, tourguideSubTitle, tourguideMainDesc, tourguideSubDesc;
    LinearLayout exploreLayout, tourguideSubtitleLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Intent intent;
    ImageView backButton;
    private Animation zoomOutAnimation;
    private TourGuideAdapter mAdapter;
    private ArrayList<TourGuideList> tourGuideList = new ArrayList<>();
    TourGuideList tourguideObject;
    private Intent navigationIntent;
    private String museumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        museumId = intent.getStringExtra("ID");

        tourguideMainTitle = (TextView) findViewById(R.id.tourguide_tittle);
        tourguideSubTitle = (TextView) findViewById(R.id.tourguide_subtittle);
        tourguideMainDesc = (TextView) findViewById(R.id.tourguide_title_desc);
        tourguideSubDesc = (TextView) findViewById(R.id.tourguide_subtitle_desc);
        exploreLayout = (LinearLayout) findViewById(R.id.explore_layout);
        tourguideSubtitleLayout = (LinearLayout) findViewById(R.id.tourguide_subtitle_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tourguide_recycler_view);
        backButton = (ImageView) findViewById(R.id.toolbar_close);

        mAdapter = new TourGuideAdapter(this, tourGuideList, "");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        if (tourGuideList.get(position).getName().equals(getString(R.string.coming_soon_txt))) {
                            new Util().showComingSoonDialog(TourGuideDetailsActivity.this);
                        } else {
                            navigationIntent = new Intent(TourGuideDetailsActivity.this,
                                    SelfGuidedStartPageActivity.class);
                            navigationIntent.putExtra("ID",tourGuideList.get(position).getMuseumId());
                            navigationIntent.putExtra("TOUR_NAME",tourGuideList.get(position).getName());
                            startActivity(navigationIntent);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        if (museumId.equals("63")) {
            tourguideMainTitle.setText(getString(R.string.mia_tour_guide));
            tourguideMainDesc.setText(getString(R.string.tourguide_title_desc));
            tourguideSubTitle.setText(getString(R.string.tourguide_title));
            tourguideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
            tourGuideList.clear();
            prepareMiaTourGuideData();
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        exploreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TourGuideDetailsActivity.this, TourGuideStartPageActivity.class);
                startActivity(i);
            }
        });


    }

    public void prepareMiaTourGuideData() {
        tourguideObject = new TourGuideList("Science Tour", "10", R.drawable.science_tour);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("MIA Highlights Tour", "11", R.drawable.mia);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList(getString(R.string.coming_soon_txt), "12", R.drawable.coming_soon_1);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList(getString(R.string.coming_soon_txt), "13", R.drawable.coming_soon_2);
        tourGuideList.add(tourguideObject);
        mAdapter.notifyDataSetChanged();

    }

}

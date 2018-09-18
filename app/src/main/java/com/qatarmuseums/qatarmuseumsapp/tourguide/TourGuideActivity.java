package com.qatarmuseums.qatarmuseumsapp.tourguide;

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
import com.qatarmuseums.qatarmuseumsapp.tourguidedetails.TourGuideDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

public class TourGuideActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_common);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();

        tourguideMainTitle = (TextView) findViewById(R.id.tourguide_tittle);
        tourguideSubTitle = (TextView) findViewById(R.id.tourguide_subtittle);
        tourguideMainDesc = (TextView) findViewById(R.id.tourguide_title_desc);
        tourguideSubDesc = (TextView) findViewById(R.id.tourguide_subtitle_desc);
        exploreLayout = (LinearLayout) findViewById(R.id.explore_layout);
        tourguideSubtitleLayout = (LinearLayout) findViewById(R.id.tourguide_subtitle_layout);
        recyclerView = (RecyclerView) findViewById(R.id.tourguide_recycler_view);
        backButton = (ImageView) findViewById(R.id.toolbar_close);

        tourguideSubtitleLayout.setVisibility(View.GONE);
        exploreLayout.setVisibility(View.GONE);
        tourguideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
        tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));

        mAdapter = new TourGuideAdapter(this, tourGuideList, getString(R.string.tourguide_sidemenu_title));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        if (tourGuideList.get(position).getMuseumId().equals("63")) {
                            navigationIntent = new Intent(TourGuideActivity.this,
                                    TourGuideDetailsActivity.class);
                            navigationIntent.putExtra("ID", tourGuideList.get(position).getMuseumId());
                            startActivity(navigationIntent);
                        } else {
                            new Util().showComingSoonDialog(TourGuideActivity.this);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

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

        prepareSidemenuTourGuideData();
    }


    public void prepareSidemenuTourGuideData() {
        tourguideObject = new TourGuideList("Museum Of Islamic Art", "63",
                "http://www.qm.org.qa/sites/default/files/museum_of_islamic_art.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Mathaf - Arab Museum Of Modern Art", "61",
                "http://www.qm.org.qa/sites/default/files/mathaf_arab_museum.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Firestation - Artists In Residence", "119",
                "");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Qatar Olympic And Sports Museum", "67",
                "http://www.qm.org.qa/sites/default/files/qatar_olypic_sports_museum.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("National Museum Of Qatar", "66",
                "http://www.qm.org.qa/sites/default/files/national_museum_of_qatar.png");
        tourGuideList.add(tourguideObject);
        mAdapter.notifyDataSetChanged();

    }

}

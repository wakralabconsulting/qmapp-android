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
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.tourguidestartpage.TourGuideStartPageActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

public class TourGuideCommonActivity extends AppCompatActivity {
    TextView tourguideMainTitle, tourguideSubTitle, tourguideMainDesc, tourguideSubDesc;
    LinearLayout exploreLayout, tourguideSubtitleLayout;
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

        mAdapter = new TourGuideAdapter(this, tourGuideList, comingFrom);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        if (!comingFrom.equals(getString(R.string.sidemenu_text))) {

                        } else {
                            if (position==0){
                                tourguideSubtitleLayout.setVisibility(View.VISIBLE);
                                exploreLayout.setVisibility(View.VISIBLE);
                                tourguideMainTitle.setText(getString(R.string.tourguide_title));
                                tourguideMainDesc.setText(getString(R.string.tourguide_title_desc));
                                tourguideSubTitle.setText(getString(R.string.tourguide_title));
                                tourguideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
                                tourGuideList.clear();
                                prepareMiaTourGuideData();
                            }else {
                                new Util().showComingSoonDialog(TourGuideCommonActivity.this);

                            }
                        }

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        if (!comingFrom.equals(getString(R.string.sidemenu_text))) {
            tourguideSubtitleLayout.setVisibility(View.VISIBLE);
            exploreLayout.setVisibility(View.VISIBLE);
            tourguideMainTitle.setText(getString(R.string.tourguide_title));
            tourguideMainDesc.setText(getString(R.string.tourguide_title_desc));
            tourguideSubTitle.setText(getString(R.string.tourguide_title));
            tourguideSubDesc.setText(getString(R.string.tourguide_subtitle_desc));
            tourGuideList.clear();
            prepareMiaTourGuideData();
            recyclerView.scrollToPosition(0);
        } else {
            tourguideSubtitleLayout.setVisibility(View.GONE);
            exploreLayout.setVisibility(View.GONE);
            tourguideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
            tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));
            tourGuideList.clear();
            prepareSidemenuTourGuideData();
            recyclerView.scrollToPosition(0);

        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comingFrom.equals(getString(R.string.sidemenu_text))) {
                    finish();
                } else {
                   if (tourguideSubtitleLayout.getVisibility()==View.VISIBLE){
                       tourguideSubtitleLayout.setVisibility(View.GONE);
                       exploreLayout.setVisibility(View.GONE);
                       tourguideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
                       tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));
                       tourGuideList.clear();
                       prepareSidemenuTourGuideData();
                       recyclerView.scrollToPosition(0);
                   }else {
                       finish();
                   }

                }

            }
        });
        exploreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TourGuideCommonActivity.this, TourGuideStartPageActivity.class);
                startActivity(i);
            }
        });


    }

    public void prepareMiaTourGuideData() {
        tourguideObject = new TourGuideList("ScienceTour", R.drawable.science_tour);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("MIA Highlights Tour", R.drawable.mia);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Coming Soon", R.drawable.coming_soon_1);
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Coming Soon", R.drawable.coming_soon_2);
        tourGuideList.add(tourguideObject);
        mAdapter.notifyDataSetChanged();

    }

    public void prepareSidemenuTourGuideData() {
        tourguideObject = new TourGuideList("Museum Of Islamic Art", "http://www.qm.org.qa/sites/default/files/museum_of_islamic_art.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Mathaf - Arab Museum Of Modern Art", "http://www.qm.org.qa/sites/default/files/mathaf_arab_museum.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Firestation - Artists In Residence", "");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("Qatar Olympic And Sports Museum", "http://www.qm.org.qa/sites/default/files/qatar_olypic_sports_museum.png");
        tourGuideList.add(tourguideObject);
        tourguideObject = new TourGuideList("National Museum Of Qatar", "http://www.qm.org.qa/sites/default/files/national_museum_of_qatar.png");
        tourGuideList.add(tourguideObject);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        if (!comingFrom.equals(getString(R.string.sidemenu_text))) {
            finish();
        } else {
            if (tourguideSubtitleLayout.getVisibility()==View.VISIBLE){
                tourguideSubtitleLayout.setVisibility(View.GONE);
                exploreLayout.setVisibility(View.GONE);
                tourguideMainTitle.setText(getString(R.string.tourguide_sidemenu_title));
                tourguideMainDesc.setText(getString(R.string.tourguide_sidemenu_title_desc));
                tourGuideList.clear();
                prepareSidemenuTourGuideData();
                recyclerView.scrollToPosition(0);
            }else {
                finish();
            }

        }
    }
}

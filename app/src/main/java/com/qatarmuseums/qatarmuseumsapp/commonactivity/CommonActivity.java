package com.qatarmuseums.qatarmuseumsapp.commonactivity;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.DetailsActivity.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private RecyclerView recyclerView;
    private List<CommonModel> models = new ArrayList<>();
    private CommonListAdapter mAdapter;
    private ImageView backArrow;
    private Animation zoomOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.toolbar_title_key));
        toolbar_title.setText(title);
        recyclerView = (RecyclerView) findViewById(R.id.common_recycler_view);

        mAdapter = new CommonListAdapter(this, models, new RecyclerTouchListener.ItemClickListener() {
            @Override
            public void onPositionClicked(int position) {
                // callback performed on click
            }

        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

        prepareRecyclerViewData();
    }

    private void prepareRecyclerViewData() {
        CommonModel model = new CommonModel("1", "LAUNDROMAT",
                "15 MARCH 2018 – 1 JUNE 2018\n" + "FIRE STATION ARTIST IN RESIDENCE, GARAGE GALLERY",
                "http://www.qm.org.qa/sites/default/files/2012-photo-credit-ai-weiwei-studio-post_0.jpg",
                true, true);
        models.add(model);
        model = new CommonModel("1", "POWDER AND DAMASK",
                "27TH AUGUST 2017 UNTIL 12TH MAY 2018\n" + "MUSEUM OF ISLAMIC ART",
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/powder-damask-exhi-03.jpg",
                true, false);
        models.add(model);
        model = new CommonModel("1", "CONTEMPORARY ART QATAR",
                "9 DECEMBER 2017 – 3 JANUARY 2018\n" + "KRAFTWERK BERLIN",
                "http://www.qm.org.qa/sites/default/files/contemporary-art-qatar-02.jpg",
                false, false);
        models.add(model);
        model = new CommonModel("1", "DRIVEN BY GERMAN DESIGN",
                "3 OCTOBER 2017- 14 JANUARY 2018\n" + "QATAR MUSEUMS GALLERY, AL RIWAQ",
                "http://www.qm.org.qa/sites/default/files/styles/exhibition_teaser/public/driven-by-german-design-02.png",
                false, false);
        models.add(model);
        model = new CommonModel("1", "TAMIM AL MAJD",
                "20 AUGUST 2017 6:00PM\n" + "MUSEUM OF ISLAMIC ART PARK",
                "http://www.qm.org.qa/sites/default/files/tamim-almajd-hero_0.jpg",
                false, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }
}

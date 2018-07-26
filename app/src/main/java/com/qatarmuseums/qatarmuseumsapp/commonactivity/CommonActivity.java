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
    String toolbarTitle;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        intent = getIntent();
        toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title_key));
        toolbar_title.setText(toolbarTitle);
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
        if (toolbarTitle.equals(getString(R.string.sidemenu_exhibition_text)))
            prepareExhibitionData();
        else if (toolbarTitle.equals(getString(R.string.sidemenu_heritage_text)))
            prepareHeritageData();
    }

    private void prepareExhibitionData() {
        CommonModel model = new CommonModel("1", "Project Space 12 Bouthayna Al-Muftah: Echoes",
                "Wednesday, July 11, 2018 - 11:45 - Monday, September 10, 2018 - 11:45",
                "Mathaf: Arab Museum of Modern Art",
                "",
                null, true);
        models.add(model);
        model = new CommonModel("2", "ARTIST IN RESIDENCE 2017-2018: DUAL INSPIRATIONS",
                "Tuesday, July 17, 2018 - 07:45 - Monday, October 1, 2018 - 07:45",
                "Fire Station Artist in Residence, Garage Gallery",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/air3-homepage_qm_2000x750px-02.jpg?itok=rmFs7UIN",
                null, false);
        models.add(model);
        model = new CommonModel("3", "PEARLS: TREASURES FROM THE SEAS AND THE RIVERS",
                "Wednesday, July 11, 2018 - 09:15 - Monday, October 1, 2018 - 09:15",
                "Revolution Square 2/3, Moscow 109012",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/076-and-cover-marie-valerie-tiara.jpg?itok=PxyEnw9O",
                null, false);
        models.add(model);
        model = new CommonModel("1", "LAUNDROMAT",
                "Thursday, March 15, 2018 - 12:45 - Friday, June 1, 2018 - 12:45",
                "Fire Station Artist in Residence, Garage Gallery",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/2012-photo-credit-ai-weiwei-studio-post_0.jpg?itok=MVxORAFa",
                null, false);
        models.add(model);
        model = new CommonModel("1", "Contemporary Art Qatar",
                "Saturday, December 9, 2017 - 07:45 - Wednesday, January 3, 2018 - 07:45",
                "KRAFTWERK BERLIN",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/contemporary-art-qatar-01.jpg?itok=F4qKDliY",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void prepareHeritageData() {
        CommonModel model = new CommonModel("1", "Al Zubarah",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al-zubarah-page-site-01.jpg?itok=wcS5I03J",
                null, true);
        models.add(model);
        model = new CommonModel("2", "Forts of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al_zubara_fort_2.jpg?itok=3bUUyTJy",
                null, false);
        models.add(model);
        model = new CommonModel("3", "Towers of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-barzantowersbyfotoarabia-11.jpg?itok=TuqhuEAA",
                null, false);
        models.add(model);
        model = new CommonModel("4", "Wells of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-alkhorwellbyfotoarabia-3.jpg?itok=0Jp0UX93",
                null, false);
        models.add(model);
        model = new CommonModel("4", "New life for old Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-abudhaloufmosquebyfotoarabia-1.jpg?itok=FTZrKTPQ",
                null, false);
        models.add(model);
        model = new CommonModel("4", "Cliffs, Carvings and Islands",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al_khor_island_12.jpg?itok=s0vYeuk4",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

}

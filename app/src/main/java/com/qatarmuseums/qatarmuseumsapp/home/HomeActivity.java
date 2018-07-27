package com.qatarmuseums.qatarmuseumsapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    RelativeLayout diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation;
    private RecyclerView recyclerView;
    private HomeListAdapter mAdapter;
    private List<HomeList> homeLists = new ArrayList<>();
    private Intent navigation_intent;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        diningNavigation = (RelativeLayout) findViewById(R.id.dining_layout);
        giftShopNavigation = (RelativeLayout) findViewById(R.id.gift_shop_layout);
        culturePassNavigation = (RelativeLayout) findViewById(R.id.culture_pass_layout);
        moreNavigation = (RelativeLayout) findViewById(R.id.more_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        util = new Util();
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        diningNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation_intent = new Intent(HomeActivity.this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_dining_text));
                startActivity(navigation_intent);
            }
        });
        giftShopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gift Shop click action
                util.showComingSoonDialog(HomeActivity.this);
            }
        });
        culturePassNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Culture Pass click action
                util.showComingSoonDialog(HomeActivity.this);
            }
        });
        moreNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlingDrawer();
            }
        });

        mAdapter = new HomeListAdapter(this, homeLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                HomeList homeList = homeLists.get(position);
                if (homeList.getId().equals("61")) {
                    Intent intent = new Intent(HomeActivity.this, CommonActivity.class);
                    intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_exhibition_text));
                    startActivity(intent);
                } else {
                    util.showComingSoonDialog(HomeActivity.this);
                }
                if (homeList.getId().equals("63")) {
//                    Intent intent = new Intent(HomeActivity.this, MuseumActivity.class);
//                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        prepareRecyclerViewData();

        diningNavigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        diningNavigation.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        giftShopNavigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        giftShopNavigation.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        culturePassNavigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        culturePassNavigation.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        moreNavigation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moreNavigation.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }

    private void prepareRecyclerViewData() {
        HomeList movie = new HomeList(getString(R.string.home_page_mia_title), "63",
                "http://www.qm.org.qa/sites/default/files/museum_of_islamic_art.png",
                2, true);
        homeLists.add(movie);
        movie = new HomeList(getString(R.string.home_page_national_museum_title), "68",
                "http://www.qm.org.qa/sites/default/files/national_museum_of_qatar.png",
                2, false);
        homeLists.add(movie);
        movie = new HomeList(getString(R.string.home_page_mathaf_title), "66",
                "http://www.qm.org.qa/sites/default/files/mathaf_arab_museum.png",
                2, false);
        homeLists.add(movie);
        movie = new HomeList(getString(R.string.home_page_fire_station_title), "60",
                "http://www.qm.org.qa/sites/default/files/firestation.png",
                2, false);
        homeLists.add(movie);
        movie = new HomeList(getString(R.string.home_page_exhibition_title), "61",
                "",
                2, false);
        homeLists.add(movie);
        movie = new HomeList(getString(R.string.home_page_sports_museum_title), "69",
                "http://www.qm.org.qa/sites/default/files/qatar_olypic_sports_museum.png",
                2, false);
        homeLists.add(movie);
        mAdapter.notifyDataSetChanged();
    }
}

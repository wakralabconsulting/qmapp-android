package com.qatarmuseums.qatarmuseumsapp.homeactivity;

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
import com.qatarmuseums.qatarmuseumsapp.baseactivity.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.commonactivity.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.commonactivity.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    RelativeLayout diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation;
    private RecyclerView recyclerView;
    private HomeListAdapter mAdapter;
    private List<HomeList> homeLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        diningNavigation = (RelativeLayout) findViewById(R.id.dining_layout);
        giftShopNavigation = (RelativeLayout) findViewById(R.id.gift_shop_layout);
        culturePassNavigation = (RelativeLayout) findViewById(R.id.culture_pass_layout);
        moreNavigation = (RelativeLayout) findViewById(R.id.more_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        diningNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dining click action
            }
        });
        giftShopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gift Shop click action
            }
        });
        culturePassNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Culture Pass click action
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
                    intent.putExtra(getString(R.string.toolbar_title_key), "Exhibitions");
                    startActivity(intent);
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
        HomeList movie = new HomeList("Mathaf: Arab Museum of Modern Art", "61",
                "",
                2, true);
        homeLists.add(movie);
        movie = new HomeList("Museum of Islamic Art (MIA)", "63",
                "http://www.qm.org.qa/sites/default/files/museum_of_islamic_art.png",
                2, true);
        homeLists.add(movie);
        movie = new HomeList("National Museum of Qatar", "66",
                "http://www.qm.org.qa/sites/default/files/national_museum_of_qatar.png",
                2, true);
        homeLists.add(movie);
        movie = new HomeList("3-2-1 Qatar Olympic and Sports Museum", "68",
                "http://www.qm.org.qa/sites/default/files/qatar_olypic_sports_museum.png",
                2, true);
        homeLists.add(movie);
        movie = new HomeList("Garage Gallery, Fire Station", "69",
                "http://www.qm.org.qa/sites/default/files/firestation.png",
                2, false);
        homeLists.add(movie);
        mAdapter.notifyDataSetChanged();
    }
}

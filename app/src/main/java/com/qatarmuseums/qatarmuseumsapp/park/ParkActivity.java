package com.qatarmuseums.qatarmuseumsapp.park;

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

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class ParkActivity extends AppCompatActivity {

    private List<ParkList> parkLists = new ArrayList<>();
    private ParkListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView headerImageView, favIcon, shareIcon, toolbarClose;
    private Toolbar toolbar;
    private Util util;
    private Animation zoomOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarClose = (ImageView) findViewById(R.id.toolbar_close);
        headerImageView = (ImageView) findViewById(R.id.header_img);
        recyclerView = (RecyclerView) findViewById(R.id.park_recycler_view);
        favIcon = (ImageView) findViewById(R.id.favourite);
        shareIcon = (ImageView) findViewById(R.id.share);
        mAdapter = new ParkListAdapter(this, parkLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareParkData();
        GlideApp.with(this)
                .load(parkLists.get(0).getImage())
                .centerCrop()
                .placeholder(R.drawable.placeholdeer)
                .into(headerImageView);
        toolbarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        util = new Util();
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (util.checkImageResource(ParkActivity.this, favIcon, R.drawable.heart_fill)) {
                    favIcon.setImageResource(R.drawable.heart_empty);
                } else
                    favIcon.setImageResource(R.drawable.heart_fill);
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        toolbarClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        toolbarClose.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        favIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        favIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        shareIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shareIcon.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

    }

    private void prepareParkData() {
        ParkList parkList = new ParkList("MIA PARK", "A WONDERFUL GREEN,\n" +
                "OPEN SPACE IN THE CITY", "Adjacent to the museum is MIA Park where you can stroll, participate in activities or just gaze at the Doha skyline from the best vantage point in the city.\n" +
                "\n" +
                "MIA Park is the perfect place to take a stroll or for your children to run around in complete safety.\n" +
                "\n" +
                "Take part in our activities or enjoy something to eat or drink.",
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/inq.jpg?itok=C14Qr6xt",
                "MIA Park is the perfect place to take a stroll or for your children to run around in complete safety.\n" +
                        "\n" +
                        "Take part in our activities or enjoy something to eat or drink.",
                "25.3154649", "51.4779437",
                "The Park is open 24 hours every day");
        parkLists.add(parkList);
        parkList = new ParkList(null, "Park Café", "Lounge in front of the spectacular panorama of Doha’s West Bay as you indulge items from our picnic basket selections. Complement your outdoor experience with a selection of juices and other soft beverages.",
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/10_0.jpg?itok=4BYJtRQB",
                "Relax on the shady terrace while enjoying freshly made sandwiches, salads, and pastries with a spectacular panorama of West Bay.",
                "25.3154649", "51.4779437",
                "Everyday From 11am to 11pm");
        parkLists.add(parkList);
        parkList = new ParkList(null, "Family Play", "The Playground has doubled in size and now offers 3 spaces for children of different ages, with exciting new play equipment that has been designed for fun, safety and physical development. There is now a play area for 2-5 year olds, a larger playground for 5-12 year olds and a playground for young people 12-16 years.",
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/332a0071_0.jpg?itok=--l8qFkn",
                null,
                "25.3154649", "51.4779437",
                "The playground is open during\n" +
                        "normal park opening times.");
        parkLists.add(parkList);
        mAdapter.notifyDataSetChanged();
    }
}
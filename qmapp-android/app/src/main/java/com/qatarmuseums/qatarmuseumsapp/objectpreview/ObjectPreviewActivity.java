package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;

import java.util.ArrayList;
import java.util.List;


public class ObjectPreviewActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView backBtn, shareBtn, locationBtn;
    private RecyclerView stepIndicatorRecyclerView;
    private StepIndicatorAdapter stepIndicatorAdapter;
    private List<CurrentIndicatorPosition> currentIndicatorPositionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.back_btn);
        shareBtn = findViewById(R.id.share_btn);
        locationBtn = findViewById(R.id.location_btn);
        setSupportActionBar(toolbar);
        final ViewPager pager = findViewById(R.id.pager);
        assert pager != null;
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        stepIndicatorRecyclerView = findViewById(R.id.idRecyclerViewHorizontalList);
        stepIndicatorAdapter = new StepIndicatorAdapter(currentIndicatorPositionList, 5, getScreenWidth());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ObjectPreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        stepIndicatorRecyclerView.setLayoutManager(horizontalLayoutManager);
        stepIndicatorRecyclerView.setAdapter(stepIndicatorAdapter);
        RecyclerView.OnItemTouchListener disabler = new RecyclerViewDisabler();
        stepIndicatorRecyclerView.addOnItemTouchListener(disabler);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentIndicatorPositionList.clear();
                CurrentIndicatorPosition c = new CurrentIndicatorPosition(position);
                currentIndicatorPositionList.add(c);
                stepIndicatorAdapter.notifyDataSetChanged();

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ObjectPreviewActivity.this, FloorMapActivity.class);
                startActivity(i);
            }
        });


    }

    public class RecyclerViewDisabler implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width / 5;
        return width;
    }

}

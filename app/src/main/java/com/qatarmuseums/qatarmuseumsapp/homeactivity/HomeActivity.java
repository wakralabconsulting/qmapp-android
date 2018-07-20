package com.qatarmuseums.qatarmuseumsapp.homeactivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.baseactivity.BaseActivity;

public class HomeActivity extends BaseActivity {
    LinearLayout diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        diningNavigation = (LinearLayout) findViewById(R.id.dining_layout);
        giftShopNavigation = (LinearLayout) findViewById(R.id.gift_shop_layout);
        culturePassNavigation = (LinearLayout) findViewById(R.id.culture_pass_layout);
        moreNavigation = (LinearLayout) findViewById(R.id.more_layout);

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
}

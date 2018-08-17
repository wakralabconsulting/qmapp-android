package com.qatarmuseums.qatarmuseumsapp.culturepass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

public class CulturePassActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View backArrow;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    private View becomeMember, loginButton;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_pass);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        becomeMember = findViewById(R.id.become_a_member_btn);
        loginButton = findViewById(R.id.login_btn);
        setSupportActionBar(toolbar);
        util = new Util();
        backArrow = findViewById(R.id.toolbar_back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        becomeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showComingSoonDialog(CulturePassActivity.this);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showComingSoonDialog(CulturePassActivity.this);
            }
        });
        becomeMember.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        becomeMember.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        loginButton.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }
}

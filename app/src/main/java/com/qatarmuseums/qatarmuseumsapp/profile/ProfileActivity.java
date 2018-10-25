package com.qatarmuseums.qatarmuseumsapp.profile;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private View backArrow, logOut;
    private Animation iconZoomOutAnimation, zoomOutAnimation;
    TextView usernameTxt, membershipNumberTxt, emailTxt, dobTxt, residenceTxt, nationalityTxt;
    ImageView profilePic, profileEdit;
    View myFavBtn, myCardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.common_toolbar);
        backArrow = findViewById(R.id.toolbar_back);
        logOut = findViewById(R.id.log_out);
        usernameTxt = findViewById(R.id.username_txt);
        membershipNumberTxt = findViewById(R.id.membership_number_txt);
        emailTxt = findViewById(R.id.email_txt);
        dobTxt = findViewById(R.id.date_of_birth_txt);
        residenceTxt = findViewById(R.id.residence_txt);
        nationalityTxt = findViewById(R.id.nationality_txt);
        profilePic = findViewById(R.id.profile_pic);
        profileEdit = findViewById(R.id.profile_edit);
        myFavBtn = findViewById(R.id.view_my_fav_btn);
        myCardBtn = findViewById(R.id.view_my_card_btn);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        iconZoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultOkSoIntermediateActivityWontBeShown();
                onBackPressed();
            }
        });
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
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        logOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        logOut.startAnimation(iconZoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        GlideApp.with(this)
                .load("https://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/faterh-al-modares1.jpg?itok=IsBmvNlb")
                .apply(RequestOptions.circleCropTransform())
                .into(profilePic);
        myCardBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myCardBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        myFavBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        myFavBtn.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResultOkSoIntermediateActivityWontBeShown();
        super.onBackPressed();
    }

    private void setResultOkSoIntermediateActivityWontBeShown() {
        Intent intent = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }
}

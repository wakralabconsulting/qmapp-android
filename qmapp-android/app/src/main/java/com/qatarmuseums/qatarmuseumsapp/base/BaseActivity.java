package com.qatarmuseums.qatarmuseumsapp.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarActivity;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.education.EducationActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationActivity;
import com.qatarmuseums.qatarmuseumsapp.objectpreview.ObjectPreviewActivity;
import com.qatarmuseums.qatarmuseumsapp.park.ParkActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileDetails;
import com.qatarmuseums.qatarmuseumsapp.settings.SettingsActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguide.TourGuideActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity
        implements View.OnClickListener {
    @Nullable
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @Nullable
    @BindView(R.id.topbar_back)
    ImageView topbarBack;
    @Nullable
    @BindView(R.id.topbar_calendar)
    ImageView topbarCalander;
    @Nullable
    @BindView(R.id.topbar_notification)
    ImageView topbarNotification;
    @Nullable
    @BindView(R.id.topbar_profile)
    ImageView topbarProfile;
    @Nullable
    @BindView(R.id.topbar_sidemenu)
    ImageView topbarSidemenu;
    @Nullable
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Nullable
    @BindView(R.id.nav_view)
    public NavigationView navigationView;
    @Nullable
    @BindView(R.id.sidemenu_exibition_icon)
    ImageView sidemenuExhibition;
    @Nullable
    @BindView(R.id.sidemenu_event_icon)
    ImageView sidemenuEvents;
    @Nullable
    @BindView(R.id.sidemenu_education_icon)
    ImageView sidemenuEducation;
    @Nullable
    @BindView(R.id.sidemenu_tour_guide_icon)
    ImageView sidemenuTourGuide;
    @Nullable
    @BindView(R.id.sidemenu_heritage_icon)
    ImageView sidemenuHeritage;
    @Nullable
    @BindView(R.id.sidemenu_public_arts_icon)
    ImageView sidemenuPublicArts;
    @Nullable
    @BindView(R.id.sidemenu_dining_icon)
    ImageView sidemenuDining;
    @Nullable
    @BindView(R.id.sidemenu_gift_shop_icon)
    ImageView sidemenuGiftShop;
    @Nullable
    @BindView(R.id.sidemenu_park_icon)
    ImageView sidemenuPark;
    @Nullable
    @BindView(R.id.sidemenu_settings_icon)
    ImageView sidemenuSettings;
    @Nullable
    @BindView(R.id.sidemenu_exibition_layout)
    LinearLayout sidemenuExibitionLayout;
    @Nullable
    @BindView(R.id.sidemenu_event_layout)
    LinearLayout sidemenuEventLayout;
    @Nullable
    @BindView(R.id.sidemenu_education_layout)
    LinearLayout sidemenuEducationLayout;
    @Nullable
    @BindView(R.id.sidemenu_tour_guide_layout)
    LinearLayout sidemenuTourGuideLayout;
    @Nullable
    @BindView(R.id.sidemenu_heritage_layout)
    LinearLayout sidemenuHeritageLayout;
    @Nullable
    @BindView(R.id.sidemenu_public_arts_layout)
    LinearLayout sidemenuPublicArtsLayout;
    @Nullable
    @BindView(R.id.sidemenu_dining_layout)
    LinearLayout sidemenuDiningLayout;
    @Nullable
    @BindView(R.id.sidemenu_gift_shop_layout)
    LinearLayout sidemenuGiftShopLayout;
    @Nullable
    @BindView(R.id.sidemenu_park_layout)
    LinearLayout sidemenuParkLayout;
    @Nullable
    @BindView(R.id.sidemenu_settings_layout)
    LinearLayout sidemenuSettingsLayout;


    private FrameLayout fullView;
    private FrameLayout activityContainer;
    Animation fadeInAnimation, fadeOutAnimation, zoomOutAnimation;
    private Intent navigation_intent;
    Util util;
    private SharedPreferences qmPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setOnclickListenerForButtons();
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);


        topbarBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        topbarBack.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });


        util = new Util();
        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_animation);
        fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                closeDrawer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setDrawerSlideAnimationEnabled(false);
        drawer.setAnimation(null);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setBackgroundColor(Color.parseColor("#CC000000"));
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = qmPreferences.getString("TOKEN", null);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                navigationView.startAnimation(fadeOutAnimation);
                toolbar.setBackgroundColor(Color.parseColor("#000000"));
            } else {
                super.onBackPressed();
            }
        } else {

            super.onBackPressed();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.topbar_back:
                // topbar back action
                onBackPressed();
                break;

            case R.id.topbar_calendar:
                topbarCalander.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.topbar_notification:
                topbarNotification.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(this, NotificationActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;
            case R.id.topbar_profile:
                topbarProfile.startAnimation(zoomOutAnimation);
                token = qmPreferences.getString("TOKEN", null);
                if (token == null)
                    navigation_intent = new Intent(this, CulturePassActivity.class);
                else
                    navigation_intent = new Intent(this, ProfileActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.topbar_sidemenu:
                // topbar sidemenu action
                handlingDrawer();
                break;


            case R.id.sidemenu_exibition_layout:
            case R.id.sidemenu_exibition_icon:
                touchListnerForLayout(sidemenuExibitionLayout);
                navigation_intent = new Intent(this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_exhibition_text));
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_event_layout:
            case R.id.sidemenu_event_icon:
                touchListnerForLayout(sidemenuEventLayout);
                navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_education_layout:
            case R.id.sidemenu_education_icon:
                touchListnerForLayout(sidemenuEducationLayout);
                sidemenuEducationLayout.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(this, EducationActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_tour_guide_layout:
            case R.id.sidemenu_tour_guide_icon:
                // navigation drawer tour guide action
                touchListnerForLayout(sidemenuTourGuideLayout);
                navigation_intent = new Intent(this, TourGuideActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;
            case R.id.sidemenu_heritage_layout:
            case R.id.sidemenu_heritage_icon:
                touchListnerForLayout(sidemenuHeritageLayout);
                navigation_intent = new Intent(this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_heritage_text));
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_public_arts_layout:
            case R.id.sidemenu_public_arts_icon:
                touchListnerForLayout(sidemenuPublicArtsLayout);
                navigation_intent = new Intent(this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_public_arts_text));
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_dining_layout:
            case R.id.sidemenu_dining_icon:
                touchListnerForLayout(sidemenuDiningLayout);
                navigation_intent = new Intent(this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_dining_text));
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_gift_shop_layout:
            case R.id.sidemenu_gift_shop_icon:
                touchListnerForLayout(sidemenuGiftShopLayout);
                sidemenuGiftShopLayout.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(BaseActivity.this, WebviewActivity.class);
                navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
                startActivity(navigation_intent);
                closeDrawer();
                break;
            case R.id.sidemenu_park_layout:
            case R.id.sidemenu_park_icon:
                touchListnerForLayout(sidemenuParkLayout);
                navigation_intent = new Intent(BaseActivity.this, ParkActivity.class);
                startActivity(navigation_intent);
                closeDrawer();
                break;

            case R.id.sidemenu_settings_layout:
            case R.id.sidemenu_settings_icon:
                touchListnerForLayout(sidemenuSettingsLayout);
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                closeDrawer();
                break;

            default:
                break;
        }

    }

    public void showToolBarOptions() {
        topbarCalander.setVisibility(View.VISIBLE);
        topbarNotification.setVisibility(View.VISIBLE);
        topbarProfile.setVisibility(View.VISIBLE);
    }

    public void hideToolBarOptions() {
        topbarCalander.setVisibility(View.INVISIBLE);
        topbarNotification.setVisibility(View.INVISIBLE);
        topbarProfile.setVisibility(View.INVISIBLE);
    }

    public void handlingDrawer() {
        if (drawer.isDrawerOpen(Gravity.END)) {
            navigationView.startAnimation(fadeOutAnimation);
            showToolBarOptions();
            toolbar.setBackgroundColor(Color.parseColor("#000000"));

        } else {
            topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.close));
            hideToolBarOptions();
            drawer.openDrawer(Gravity.END, false);
            toolbar.setBackgroundColor(Color.parseColor("#CC000000"));
            navigationView.startAnimation(fadeInAnimation);

        }
    }

    public void setOnclickListenerForButtons() {
        topbarBack.setOnClickListener(this);
        topbarCalander.setOnClickListener(this);
        topbarNotification.setOnClickListener(this);
        topbarProfile.setOnClickListener(this);
        topbarSidemenu.setOnClickListener(this);
        sidemenuExibitionLayout.setOnClickListener(this);
        sidemenuExhibition.setOnClickListener(this);
        sidemenuEvents.setOnClickListener(this);
        sidemenuEventLayout.setOnClickListener(this);
        sidemenuEducation.setOnClickListener(this);
        sidemenuEducationLayout.setOnClickListener(this);
        sidemenuTourGuide.setOnClickListener(this);
        sidemenuTourGuideLayout.setOnClickListener(this);
        sidemenuHeritage.setOnClickListener(this);
        sidemenuHeritageLayout.setOnClickListener(this);
        sidemenuPublicArts.setOnClickListener(this);
        sidemenuPublicArtsLayout.setOnClickListener(this);
        sidemenuDining.setOnClickListener(this);
        sidemenuDiningLayout.setOnClickListener(this);
        sidemenuGiftShop.setOnClickListener(this);
        sidemenuGiftShopLayout.setOnClickListener(this);
        sidemenuPark.setOnClickListener(this);
        sidemenuParkLayout.setOnClickListener(this);
        sidemenuSettings.setOnClickListener(this);
        sidemenuSettingsLayout.setOnClickListener(this);

    }

    public void setToolbarForMuseumActivity() {
        topbarSidemenu.setVisibility(View.INVISIBLE);
        topbarBack.setVisibility(View.VISIBLE);
    }

    public void closeDrawer() {
        topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
        drawer.closeDrawer(GravityCompat.END, false);
        sidemenuExibitionLayout.clearAnimation();
        sidemenuEventLayout.clearAnimation();
        sidemenuEducationLayout.clearAnimation();
        sidemenuTourGuideLayout.clearAnimation();
        sidemenuHeritageLayout.clearAnimation();
        sidemenuPublicArtsLayout.clearAnimation();
        sidemenuDiningLayout.clearAnimation();
        sidemenuGiftShopLayout.clearAnimation();
        sidemenuParkLayout.clearAnimation();
        sidemenuSettingsLayout.clearAnimation();
        showToolBarOptions();
    }

    public void touchListnerForLayout(final LinearLayout linearLayout) {
        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        linearLayout.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
    }
}

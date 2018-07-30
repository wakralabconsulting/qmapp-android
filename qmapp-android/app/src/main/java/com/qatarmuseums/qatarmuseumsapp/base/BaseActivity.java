package com.qatarmuseums.qatarmuseumsapp.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarActivity;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.education.EducationActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationActivity;
import com.qatarmuseums.qatarmuseumsapp.park.ParkActivity;
import com.qatarmuseums.qatarmuseumsapp.settings.SettingsActivity;
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
    private FrameLayout fullView;
    private FrameLayout activityContainer;
    Animation fadeInAnimation, fadeOutAnimation, zoomOutAnimation;
    private Intent navigation_intent;
    Util util;

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
                topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                drawer.closeDrawer(GravityCompat.END, false);
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
        public void onClick (View v){
            switch (v.getId()) {

                case R.id.topbar_back:
                    // topbar back action
                    onBackPressed();
                    break;

                case R.id.topbar_calendar:
                    topbarCalander.startAnimation(zoomOutAnimation);
                    navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(navigation_intent);
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    break;

                case R.id.topbar_notification:
                    topbarNotification.startAnimation(zoomOutAnimation);
                    navigation_intent = new Intent(this, NotificationActivity.class);
                    startActivity(navigation_intent);
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    break;
                case R.id.topbar_profile:
                    // topbar profile action
                    topbarProfile.startAnimation(zoomOutAnimation);
                    util.showComingSoonDialog(BaseActivity.this);
                    break;

                case R.id.topbar_sidemenu:
                    // topbar sidemenu action
                    handlingDrawer();
                    break;

                case R.id.sidemenu_exibition_icon:
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    navigation_intent = new Intent(this, CommonActivity.class);
                    navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_exhibition_text));
                    startActivity(navigation_intent);
                    break;

                case R.id.sidemenu_event_icon:
                    navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                    startActivity(navigation_intent);
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    break;

                case R.id.sidemenu_education_icon:
                    drawer.startAnimation(fadeOutAnimation);
                    navigation_intent = new Intent(this, EducationActivity.class);
                    startActivity(navigation_intent);

                    break;

                case R.id.sidemenu_tour_guide_icon:
                    // navigation drawer tour guide action
                    util.showComingSoonDialog(BaseActivity.this);
//                topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
//                drawer.closeDrawer(GravityCompat.END, false);
                    break;
                case R.id.sidemenu_heritage_icon:
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    navigation_intent = new Intent(this, CommonActivity.class);
                    navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_heritage_text));
                    startActivity(navigation_intent);
                    break;

                case R.id.sidemenu_public_arts_icon:
                    navigation_intent = new Intent(this, CommonActivity.class);
                    navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_public_arts_text));
                    startActivity(navigation_intent);
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    break;

                case R.id.sidemenu_dining_icon:
                    navigation_intent = new Intent(this, CommonActivity.class);
                    navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_dining_text));
                    startActivity(navigation_intent);
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    break;

                case R.id.sidemenu_gift_shop_icon:
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    navigation_intent = new Intent(BaseActivity.this, WebviewActivity.class);
                    navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
                    startActivity(navigation_intent);
                    break;
                case R.id.sidemenu_park_icon:
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    navigation_intent = new Intent(BaseActivity.this, ParkActivity.class);
                    startActivity(navigation_intent);
                    break;

                case R.id.sidemenu_settings_icon:
                    // navigation drawer settings action
                    topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
                    drawer.closeDrawer(GravityCompat.END, false);
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;

                default:
                    break;
            }

        }

        public void handlingDrawer () {
            if (drawer.isDrawerOpen(Gravity.END)) {
                navigationView.startAnimation(fadeOutAnimation);
                toolbar.setBackgroundColor(Color.parseColor("#000000"));

            } else {
                topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.close));
                drawer.openDrawer(Gravity.END, false);
                toolbar.setBackgroundColor(Color.parseColor("#CC000000"));
                navigationView.startAnimation(fadeInAnimation);

            }
        }

        public void setOnclickListenerForButtons () {
            topbarBack.setOnClickListener(this);
            topbarCalander.setOnClickListener(this);
            topbarNotification.setOnClickListener(this);
            topbarProfile.setOnClickListener(this);
            topbarSidemenu.setOnClickListener(this);
            sidemenuExhibition.setOnClickListener(this);
            sidemenuEvents.setOnClickListener(this);
            sidemenuEducation.setOnClickListener(this);
            sidemenuTourGuide.setOnClickListener(this);
            sidemenuHeritage.setOnClickListener(this);
            sidemenuPublicArts.setOnClickListener(this);
            sidemenuDining.setOnClickListener(this);
            sidemenuGiftShop.setOnClickListener(this);
            sidemenuPark.setOnClickListener(this);
            sidemenuSettings.setOnClickListener(this);

        }

        public void setToolbarForMuseumActivity () {
            topbarSidemenu.setVisibility(View.INVISIBLE);
            topbarBack.setVisibility(View.VISIBLE);
        }
    }

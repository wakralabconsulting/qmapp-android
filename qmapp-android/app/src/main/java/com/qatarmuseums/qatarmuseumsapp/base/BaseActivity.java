package com.qatarmuseums.qatarmuseumsapp.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.calendar.CalendarActivity;
import com.qatarmuseums.qatarmuseumsapp.commonlistpage.CommonListActivity;
import com.qatarmuseums.qatarmuseumsapp.culturepass.CulturePassActivity;
import com.qatarmuseums.qatarmuseumsapp.education.EducationActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationActivity;
import com.qatarmuseums.qatarmuseumsapp.notification.NotificationTable;
import com.qatarmuseums.qatarmuseumsapp.park.ParkActivity;
import com.qatarmuseums.qatarmuseumsapp.profile.ProfileActivity;
import com.qatarmuseums.qatarmuseumsapp.settings.SettingsActivity;
import com.qatarmuseums.qatarmuseumsapp.tourguide.TourGuideActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebViewActivity;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity
        implements View.OnClickListener {
    @Nullable
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @Nullable
    @BindView(R.id.topbar_back)
    ImageView topBarBack;
    @Nullable
    @BindView(R.id.topbar_calendar)
    ImageView topBarCalender;
    @Nullable
    @BindView(R.id.topbar_notification)
    ImageView topBarNotification;
    @Nullable
    @BindView(R.id.topbar_profile)
    ImageView topBarProfile;
    @Nullable
    @BindView(R.id.topbar_sidemenu)
    public ImageView topBarSideMenu;
    @Nullable
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;
    @Nullable
    @BindView(R.id.nav_view)
    public NavigationView navigationView;
    @Nullable
    @BindView(R.id.sidemenu_exibition_icon)
    ImageView sideMenuExhibition;
    @Nullable
    @BindView(R.id.sidemenu_event_icon)
    ImageView sideMenuEvents;
    @Nullable
    @BindView(R.id.sidemenu_education_icon)
    ImageView sideMenuEducation;
    @Nullable
    @BindView(R.id.sidemenu_tour_guide_icon)
    ImageView sideMenuTourGuide;
    @Nullable
    @BindView(R.id.sidemenu_heritage_icon)
    ImageView sideMenuHeritage;
    @Nullable
    @BindView(R.id.sidemenu_public_arts_icon)
    ImageView sideMenuPublicArts;
    @Nullable
    @BindView(R.id.sidemenu_dining_icon)
    ImageView sideMenuDining;
    @Nullable
    @BindView(R.id.sidemenu_gift_shop_icon)
    ImageView sideMenuGiftShop;
    @Nullable
    @BindView(R.id.sidemenu_park_icon)
    ImageView sideMenuPark;
    @Nullable
    @BindView(R.id.sidemenu_settings_icon)
    ImageView sideMenuSettings;
    @Nullable
    @BindView(R.id.sidemenu_exibition_layout)
    LinearLayout sideMenuExhibitionLayout;
    @Nullable
    @BindView(R.id.sidemenu_event_layout)
    LinearLayout sideMenuEventLayout;
    @Nullable
    @BindView(R.id.sidemenu_education_layout)
    LinearLayout sideMenuEducationLayout;
    @Nullable
    @BindView(R.id.sidemenu_tour_guide_layout)
    LinearLayout sideMenuTourGuideLayout;
    @Nullable
    @BindView(R.id.sidemenu_heritage_layout)
    LinearLayout sideMenuHeritageLayout;
    @Nullable
    @BindView(R.id.sidemenu_public_arts_layout)
    LinearLayout sideMenuPublicArtsLayout;
    @Nullable
    @BindView(R.id.sidemenu_dining_layout)
    LinearLayout sideMenuDiningLayout;
    @Nullable
    @BindView(R.id.sidemenu_gift_shop_layout)
    LinearLayout sideMenuGiftShopLayout;
    @Nullable
    @BindView(R.id.sidemenu_park_layout)
    LinearLayout sideMenuParkLayout;
    @Nullable
    @BindView(R.id.sidemenu_settings_layout)
    LinearLayout sideMenuSettingsLayout;
    @BindView(R.id.badge_notification)
    public TextView badgeCountTextView;

    Animation fadeInAnimation, fadeOutAnimation, zoomOutAnimation;
    private Intent navigation_intent;
    Util util;
    private SharedPreferences qmPreferences;
    private String name;
    private int badgeCount;

    private QMDatabase qmDatabase;
    NotificationTable notificationTable;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout fullView = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setOnclickListenerForButtons();
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        qmDatabase = QMDatabase.getInstance(BaseActivity.this);
        topBarBack.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    topBarBack.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
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
                clearAnimations();
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
        name = qmPreferences.getString("NAME", null);
        badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
        if (badgeCount > 0)
            setBadge(badgeCount);
    }

    public void insertNotificationRelatedDataToDataBase(String msg, String lan) {
        Timber.i("Inserting Notification message to Database");
        new InsertDatabaseTask(BaseActivity.this, notificationTable, lan, msg).execute();
    }

    public static class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<BaseActivity> activityReference;
        private NotificationTable notificationTable;
        String language, notificationMessage;

        InsertDatabaseTask(BaseActivity context, NotificationTable notificationTable, String lan, String msg) {
            activityReference = new WeakReference<>(context);
            this.notificationTable = notificationTable;
            language = lan;
            this.notificationMessage = msg;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (notificationMessage != null) {
                notificationTable = new NotificationTable(notificationMessage, language);
                activityReference.get().qmDatabase.getNotificationDao().insertTable(notificationTable);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

    }

    public void updateBadge() {
        badgeCount = qmPreferences.getInt("BADGE_COUNT", 0);
        if (badgeCount > 0) {
            Timber.i("updateBadge()");
            setBadge(badgeCount);
        } else
            badgeCountTextView.setVisibility(View.GONE);
    }

    public void setBadge(int badgeCount) {
        Timber.i("Setting Badge Count: %d", badgeCount);
        if ((topBarNotification != null ? topBarNotification.getVisibility() : 0) == View.VISIBLE)
            badgeCountTextView.setVisibility(View.VISIBLE);
        if (badgeCount < 10)
            badgeCountTextView.setPadding(20, 5, 20, 5);
        else
            badgeCountTextView.setPadding(5, 5, 5, 5);
        badgeCountTextView.setText(String.valueOf(badgeCount));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                Timber.i("Top bar Back clicked");
                onBackPressed();
                break;

            case R.id.topbar_calendar:
                Timber.i("Top bar Calendar clicked");
                topBarCalender.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.topbar_notification:
                Timber.i("Top bar Notification clicked");
                topBarNotification.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(this, NotificationActivity.class);
                startActivity(navigation_intent);
                SharedPreferences.Editor editor = qmPreferences.edit();
                editor.putInt("BADGE_COUNT", 0);
                editor.commit();
                badgeCountTextView.setVisibility(View.GONE);
                clearAnimations();
                break;
            case R.id.topbar_profile:
                Timber.i("Top bar Profile clicked");
                topBarProfile.startAnimation(zoomOutAnimation);
                name = qmPreferences.getString("NAME", null);
                if (name == null)
                    navigation_intent = new Intent(this, CulturePassActivity.class);
                else
                    navigation_intent = new Intent(this, ProfileActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.topbar_sidemenu:
                // topbar sidemenu action
                handlingDrawer();
                Timber.i("Hamburger menu clicked");
                break;


            case R.id.sidemenu_exibition_layout:
            case R.id.sidemenu_exibition_icon:
                Timber.i("Side menu Exhibition clicked");
                touchListenerForLayout(sideMenuExhibitionLayout);
                navigation_intent = new Intent(this, CommonListActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_exhibition_text));
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_event_layout:
            case R.id.sidemenu_event_icon:
                Timber.i("Side menu Event clicked");
                touchListenerForLayout(sideMenuEventLayout);
                navigation_intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_education_layout:
            case R.id.sidemenu_education_icon:
                Timber.i("Side menu Education clicked");
                touchListenerForLayout(sideMenuEducationLayout);
                navigation_intent = new Intent(this, EducationActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_tour_guide_layout:
            case R.id.sidemenu_tour_guide_icon:
                // navigation drawer tour guide action
                Timber.i("Side menu Tour guide clicked");
                touchListenerForLayout(sideMenuTourGuideLayout);
                navigation_intent = new Intent(this, TourGuideActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;
            case R.id.sidemenu_heritage_layout:
            case R.id.sidemenu_heritage_icon:
                Timber.i("Side menu Heritage clicked");
                touchListenerForLayout(sideMenuHeritageLayout);
                navigation_intent = new Intent(this, CommonListActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_heritage_text));
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_public_arts_layout:
            case R.id.sidemenu_public_arts_icon:
                Timber.i("Side menu Public arts clicked");
                touchListenerForLayout(sideMenuPublicArtsLayout);
                navigation_intent = new Intent(this, CommonListActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_public_arts_text));
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_dining_layout:
            case R.id.sidemenu_dining_icon:
                Timber.i("Side menu Dining clicked");
                touchListenerForLayout(sideMenuDiningLayout);
                navigation_intent = new Intent(this, CommonListActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.side_menu_dining_text));
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_gift_shop_layout:
            case R.id.sidemenu_gift_shop_icon:
                Timber.i("Side menu Gift shop clicked");
                touchListenerForLayout(sideMenuGiftShopLayout);
                sideMenuGiftShopLayout.startAnimation(zoomOutAnimation);
                navigation_intent = new Intent(BaseActivity.this, WebViewActivity.class);
                navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
                startActivity(navigation_intent);
                clearAnimations();
                break;
            case R.id.sidemenu_park_layout:
            case R.id.sidemenu_park_icon:
                Timber.i("Side menu Park clicked");
                touchListenerForLayout(sideMenuParkLayout);
                navigation_intent = new Intent(BaseActivity.this, ParkActivity.class);
                startActivity(navigation_intent);
                clearAnimations();
                break;

            case R.id.sidemenu_settings_layout:
            case R.id.sidemenu_settings_icon:
                Timber.i("Side menu Settings clicked");
                touchListenerForLayout(sideMenuSettingsLayout);
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                clearAnimations();
                Timber.i("Settings clicked");
                break;

            default:
                break;
        }

    }

    public void showToolBarOptions() {
        topBarCalender.setVisibility(View.VISIBLE);
        topBarNotification.setVisibility(View.VISIBLE);
        updateBadge();
        topBarProfile.setVisibility(View.VISIBLE);
    }

    public void hideToolBarOptions() {
        topBarCalender.setVisibility(View.INVISIBLE);
        topBarNotification.setVisibility(View.INVISIBLE);
        badgeCountTextView.setVisibility(View.INVISIBLE);
        topBarProfile.setVisibility(View.INVISIBLE);
    }

    public void handlingDrawer() {
        if (drawer.isDrawerOpen(Gravity.END)) {
            navigationView.startAnimation(fadeOutAnimation);
            showToolBarOptions();
            topBarSideMenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
            drawer.closeDrawer(GravityCompat.END, false);
            toolbar.setBackgroundColor(Color.parseColor("#000000"));

        } else {
            topBarSideMenu.setImageDrawable(getResources().getDrawable(R.drawable.close));
            hideToolBarOptions();
            drawer.openDrawer(Gravity.END, false);
            toolbar.setBackgroundColor(Color.parseColor("#CC000000"));
            navigationView.startAnimation(fadeInAnimation);

        }
    }

    public void setOnclickListenerForButtons() {
        topBarBack.setOnClickListener(this);
        topBarCalender.setOnClickListener(this);
        topBarNotification.setOnClickListener(this);
        topBarProfile.setOnClickListener(this);
        topBarSideMenu.setOnClickListener(this);
        sideMenuExhibitionLayout.setOnClickListener(this);
        sideMenuExhibition.setOnClickListener(this);
        sideMenuEvents.setOnClickListener(this);
        sideMenuEventLayout.setOnClickListener(this);
        sideMenuEducation.setOnClickListener(this);
        sideMenuEducationLayout.setOnClickListener(this);
        sideMenuTourGuide.setOnClickListener(this);
        sideMenuTourGuideLayout.setOnClickListener(this);
        sideMenuHeritage.setOnClickListener(this);
        sideMenuHeritageLayout.setOnClickListener(this);
        sideMenuPublicArts.setOnClickListener(this);
        sideMenuPublicArtsLayout.setOnClickListener(this);
        sideMenuDining.setOnClickListener(this);
        sideMenuDiningLayout.setOnClickListener(this);
        sideMenuGiftShop.setOnClickListener(this);
        sideMenuGiftShopLayout.setOnClickListener(this);
        sideMenuPark.setOnClickListener(this);
        sideMenuParkLayout.setOnClickListener(this);
        sideMenuSettings.setOnClickListener(this);
        sideMenuSettingsLayout.setOnClickListener(this);

    }

    public void setToolbarForMuseumActivity() {
        topBarSideMenu.setVisibility(View.INVISIBLE);
        topBarBack.setVisibility(View.VISIBLE);
    }

    public void setToolbarForMuseumLaunchy() {
        topBarSideMenu.setVisibility(View.INVISIBLE);
        topBarProfile.setVisibility(View.INVISIBLE);
        topBarCalender.setVisibility(View.INVISIBLE);
        topBarNotification.setVisibility(View.INVISIBLE);
        topBarBack.setVisibility(View.VISIBLE);
    }

    public void clearAnimations() {
        sideMenuExhibitionLayout.clearAnimation();
        sideMenuEventLayout.clearAnimation();
        sideMenuEducationLayout.clearAnimation();
        sideMenuTourGuideLayout.clearAnimation();
        sideMenuHeritageLayout.clearAnimation();
        sideMenuPublicArtsLayout.clearAnimation();
        sideMenuDiningLayout.clearAnimation();
        sideMenuGiftShopLayout.clearAnimation();
        sideMenuParkLayout.clearAnimation();
        sideMenuSettingsLayout.clearAnimation();
    }

    public void touchListenerForLayout(final LinearLayout linearLayout) {
        linearLayout.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    linearLayout.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
    }
}

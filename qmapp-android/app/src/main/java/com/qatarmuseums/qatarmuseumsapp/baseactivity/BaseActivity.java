package com.qatarmuseums.qatarmuseumsapp.baseactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qatarmuseums.qatarmuseumsapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    DrawerLayout drawer;
    private FrameLayout fullView;
    private FrameLayout activityContainer;

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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.topbar_back:
                finish();
                break;

            case R.id.topbar_calendar:
                // do your code
                break;

            case R.id.topbar_notification:
                // do your code
                break;
            case R.id.topbar_profile:
                // do your code
                break;

            case R.id.topbar_sidemenu:
                handlingDrawer();
                break;

            default:
                break;
        }

    }

    public void handlingDrawer() {
        if (drawer.isDrawerOpen(Gravity.END)) {
            topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.side_menu_icon));
            drawer.closeDrawer(Gravity.END);
        } else {
            topbarSidemenu.setImageDrawable(getResources().getDrawable(R.drawable.close));
            drawer.openDrawer(Gravity.END);
        }
    }

    public void setOnclickListenerForButtons() {
        topbarBack.setOnClickListener(this);
        topbarCalander.setOnClickListener(this);
        topbarNotification.setOnClickListener(this);
        topbarProfile.setOnClickListener(this);
        topbarSidemenu.setOnClickListener(this);
    }
}

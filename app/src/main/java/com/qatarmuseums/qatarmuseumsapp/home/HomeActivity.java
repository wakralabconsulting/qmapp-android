package com.qatarmuseums.qatarmuseumsapp.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.base.BaseActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.CommonActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpage.RecyclerTouchListener;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;
import com.qatarmuseums.qatarmuseumsapp.webview.WebviewActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    RelativeLayout diningNavigation, giftShopNavigation, culturePassNavigation, moreNavigation;
    Animation zoomOutAnimation, fadeOutAnimation;
    private RecyclerView recyclerView;
    private HomeListAdapter mAdapter;
    private ArrayList<HomeList> homeLists = new ArrayList<>();
    private Intent navigation_intent;
    Util util;
    RelativeLayout noResultFoundLayout;
    private boolean doubleBackToExitPressedOnce;
    ProgressBar progressBar;
    SharedPreferences qmPreferences;
    QMDatabase qmDatabase;
    HomePageTable homePageTable;
    int homePageTableRowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        progressBar.setVisibility(View.VISIBLE);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        diningNavigation = (RelativeLayout) findViewById(R.id.dining_layout);
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        giftShopNavigation = (RelativeLayout) findViewById(R.id.gift_shop_layout);
        culturePassNavigation = (RelativeLayout) findViewById(R.id.culture_pass_layout);
        moreNavigation = (RelativeLayout) findViewById(R.id.more_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        util = new Util();
        qmDatabase = QMDatabase.getInstance(HomeActivity.this);
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
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
        diningNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation_intent = new Intent(HomeActivity.this, CommonActivity.class);
                navigation_intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_dining_text));
                startActivity(navigation_intent);
            }
        });
        giftShopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation_intent = new Intent(HomeActivity.this, WebviewActivity.class);
                navigation_intent.putExtra("url", getString(R.string.gift_shop_url));
                startActivity(navigation_intent);

            }
        });
        culturePassNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Culture Pass click action
                util.showComingSoonDialog(HomeActivity.this);
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
                    intent.putExtra(getString(R.string.toolbar_title_key), getString(R.string.sidemenu_exhibition_text));
                    startActivity(intent);
                } else if (homeList.getId().equals("63")) {
                    Intent intent = new Intent(HomeActivity.this, MuseumActivity.class);
                    intent.putExtra("MUSEUMTITLE", homeList.getName());
                    startActivity(intent);
                } else {
                    util.showComingSoonDialog(HomeActivity.this);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        getHomePageAPIData();

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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            navigationView.startAnimation(fadeOutAnimation);
            toolbar.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.touch_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void getHomePageAPIData() {
        int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<HomeList>> call = apiService.getHomepageDetails(language);
        call.enqueue(new Callback<ArrayList<HomeList>>() {
            @Override
            public void onResponse(Call<ArrayList<HomeList>> call, Response<ArrayList<HomeList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        homeLists.addAll(response.body());
                        HomeList exhibitonObject = new HomeList("Exhibitions","61",
                                "",false);
                        int secondLastIndex = homeLists.size() - 1;
                        homeLists.add(secondLastIndex,exhibitonObject);
                        mAdapter.notifyDataSetChanged();
                        new RowCount(HomeActivity.this, language).execute();

                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noResultFoundLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<HomeList>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());
                } else {
                    // due to mapping issues
                }
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);

            }
        });
    }


    public class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<HomeActivity> activityReference;
        String language;

        RowCount(HomeActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHomePageTableDao().getNumberOfRows();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            homePageTableRowCount = integer;
            if (homePageTableRowCount > 0) {
                //update or add row to database
                new CheckDBRowExist(HomeActivity.this,language).execute();

            } else {
                //create databse
                new InsertDatabaseTask(HomeActivity.this, homePageTable).execute();

            }

        }
    }


    public class CheckDBRowExist extends AsyncTask<Void,Void,Void>{
        private WeakReference<HomeActivity> activityReference;
        private HomePageTable homePageTable;
        String language;

        CheckDBRowExist(HomeActivity context,String apiLanguage){
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if (homeLists.size()>0) {
                for (int i=0;i<homeLists.size();i++) {
                    int n= activityReference.get().qmDatabase.getHomePageTableDao().checkIdExist(
                            Integer.parseInt(homeLists.get(i).getId()));
                    if (n>0){
                        //update same id
                        new UpdateHomePageTable(HomeActivity.this,language,i).execute();

                    }else {
                        //create row with corresponding id
                        homePageTable = new HomePageTable(Long.parseLong(homeLists.get(i).getId()),
                                homeLists.get(i).getName(),
                                homeLists.get(i).getTourguideAvailable().toString(),
                                homeLists.get(i).getImage());
                        activityReference.get().qmDatabase.getHomePageTableDao().insert(homePageTable);

                    }
                }
            }
            return null;

        }


    }

    public class InsertDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<HomeActivity> activityReference;
        private HomePageTable homePageTable;

        InsertDatabaseTask(HomeActivity context, HomePageTable homePageTable) {
            activityReference = new WeakReference<>(context);
            this.homePageTable = homePageTable;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (homeLists != null) {
                for (int i = 0; i < homeLists.size(); i++) {
                    homePageTable = new HomePageTable(Long.parseLong(homeLists.get(i).getId()),
                            homeLists.get(i).getName(),
                            homeLists.get(i).getTourguideAvailable().toString(),
                            homeLists.get(i).getImage());
                    activityReference.get().qmDatabase.getHomePageTableDao().insert(homePageTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                activityReference.get().setResult(homePageTable, 1);
            }
        }
    }

    private void setResult(HomePageTable note, int flag) {
        Toast.makeText(this, "Database Success", Toast.LENGTH_LONG).show();
    }

    public class UpdateHomePageTable extends AsyncTask<Void,Void,Void>{
        private WeakReference<HomeActivity> activityReference;
        String language;
        int position;
        UpdateHomePageTable(HomeActivity context,String apiLanguage,int p){
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")){
                // update table with english name
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageEnglish(
                        homeLists.get(position).getName(),homeLists.get(position).getTourguideAvailable().toString(),
                        homeLists.get(position).getImage(), homeLists.get(position).getId()
                );

            }else {
                // update table with arabic name
                activityReference.get().qmDatabase.getHomePageTableDao().updateHomePageArabic(
                        homeLists.get(position).getName(),homeLists.get(position).getTourguideAvailable().toString(),
                        homeLists.get(position).getImage(),homeLists.get(position).getId()
                );
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(HomeActivity.this, "Update success", Toast.LENGTH_SHORT).show();
        }
    }

}

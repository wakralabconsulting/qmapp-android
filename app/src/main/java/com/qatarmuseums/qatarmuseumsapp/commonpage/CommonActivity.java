package com.qatarmuseums.qatarmuseumsapp.commonpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTable;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DiningActivity;
import com.qatarmuseums.qatarmuseumsapp.home.HomeActivity;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private RecyclerView recyclerView;
    private ArrayList<CommonModel> models = new ArrayList<>();
    private CommonListAdapter mAdapter;
    private ImageView backArrow;
    private Animation zoomOutAnimation;
    String toolbarTitle;
    Intent intent, navigationIntent;
    ProgressBar progressBar;
    SharedPreferences qmPreferences;
    Util util;
    RelativeLayout noResultFoundLayout;
    QMDatabase qmDatabase;
    int publicArtsTableRowCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        intent = getIntent();
        util = new Util();
        qmDatabase = QMDatabase.getInstance(CommonActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
//        progressBar.setVisibility(View.VISIBLE);
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title_key));
        toolbar_title.setText(toolbarTitle);
        recyclerView = (RecyclerView) findViewById(R.id.common_recycler_view);

        mAdapter = new CommonListAdapter(this, models, new RecyclerTouchListener.ItemClickListener() {
            @Override
            public void onPositionClicked(int position) {
                if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text)))
                    navigationIntent = new Intent(CommonActivity.this, DiningActivity.class);
                else if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
                    navigationIntent = new Intent(CommonActivity.this,
                            CollectionDetailsActivity.class);
                else
                    navigationIntent = new Intent(CommonActivity.this, DetailsActivity.class);
                navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImage());
                navigationIntent.putExtra("MAIN_TITLE", models.get(position).getName());
                navigationIntent.putExtra("COMING_FROM", toolbarTitle);
                navigationIntent.putExtra("IS_FAVOURITE", models.get(position).getIsfavourite());
                navigationIntent.putExtra("LONGITUDE", models.get(position).getLongitude());
                navigationIntent.putExtra("LATITUDE", models.get(position).getLatitude());
                startActivity(navigationIntent);

            }

        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        backArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        backArrow.startAnimation(zoomOutAnimation);
                        break;
                }
                return false;
            }
        });
        if (toolbarTitle.equals(getString(R.string.sidemenu_exhibition_text))) {
            prepareExhibitionData();
//            getCommonListAPIData("Exhibition_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.sidemenu_heritage_text))) {
            getCommonListAPIData("Heritage_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.sidemenu_public_arts_text))) {
            getCommonListAPIData("Public_Arts_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text)))
            prepareDiningData();
        else if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
            prepareCollectionData();
    }

    private void prepareExhibitionData() {

        CommonModel model = new CommonModel("1", "Project Space 12 Bouthayna Al-Muftah: Echoes",
                "Wednesday, July 11, 2018 - 11:45 - Monday, September 10, 2018 - 11:45", "",
                "Mathaf: Arab Museum of Modern Art",
                "",
                null, true);
        models.add(model);
        model = new CommonModel("2", "ARTIST IN RESIDENCE 2017-2018: DUAL INSPIRATIONS",
                "Tuesday, July 17, 2018 - 07:45 - Monday, October 1, 2018 - 07:45",
                "",
                "Fire Station Artist in Residence, Garage Gallery",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/air3-homepage_qm_2000x750px-02.jpg?itok=rmFs7UIN",
                null, false);
        models.add(model);
        model = new CommonModel("3", "PEARLS: TREASURES FROM THE SEAS AND THE RIVERS",
                "Wednesday, July 11, 2018 - 09:15 - Monday, October 1, 2018 - 09:15",
                "",
                "Revolution Square 2/3, Moscow 109012",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/076-and-cover-marie-valerie-tiara.jpg?itok=PxyEnw9O",
                null, false);
        models.add(model);
        model = new CommonModel("1", "LAUNDROMAT",
                "Thursday, March 15, 2018 - 12:45 - Friday, June 1, 2018 - 12:45",
                "Fire Station Artist in Residence, Garage Gallery", "",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/2012-photo-credit-ai-weiwei-studio-post_0.jpg?itok=MVxORAFa",
                null, false);
        models.add(model);
        model = new CommonModel("1", "Contemporary Art Qatar",
                "Saturday, December 9, 2017 - 07:45 - Wednesday, January 3, 2018 - 07:45",
                "",
                "KRAFTWERK BERLIN",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/contemporary-art-qatar-01.jpg?itok=F4qKDliY",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void getCommonListAPIData(String name) {
        final int appLanguage = qmPreferences.getInt("AppLanguage", 1);
        String language;
        String pageName = name;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<CommonModel>> call = apiService.getCommonpageList(language, pageName);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
//                        new RowCount(CommonActivity.this, appLanguage).execute();
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
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                if (t instanceof IOException) {
                    util.showToast(getResources().getString(R.string.check_network), getApplicationContext());

                } else {
                    // error due to mapping issues
                }
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        });

    }


    private void prepareDiningData() {
        CommonModel model = new CommonModel("1", "IDAM",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/idam-pierremonetta_mg_6372_1.jpg?itok=bKArHUGQ",
                null, true);
        models.add(model);
        model = new CommonModel("2", "IN-Q CAFÉ",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/inq.jpg?itok=C14Qr6xt",
                null, true);
        models.add(model);
        model = new CommonModel("3", "MIA CAFÉ",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/dsc_0597_2_0.jpg?itok=TXvRM1HE",
                null, false);
        models.add(model);
        model = new CommonModel("4", "AL RIWAQ CAFÉ",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/10_0.jpg?itok=4BYJtRQB",
                null, false);
        models.add(model);
        model = new CommonModel("4", "MIA CATERING",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/mia-catering.jpg?itok=Kk7svJPU",
                null, false);
        models.add(model);
        model = new CommonModel("4", "MATHAF MAQHA",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/332a0071_0.jpg?itok=--l8qFkn",
                null, false);
        models.add(model);
        model = new CommonModel("4", "CAFÉ #999",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/332a4417.jpg?itok=_OpfHaT_",
                null, true);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void prepareCollectionData() {
        CommonModel model = new CommonModel("1", "CERAMICS COLLECTION",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/idam-pierremonetta_mg_6372_1.jpg?itok=bKArHUGQ",
                null, null);
        models.add(model);
        model = new CommonModel("2", "GLASS COLLECTION",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/inq.jpg?itok=C14Qr6xt",
                null, null);
        models.add(model);
        model = new CommonModel("3", "THE CAVOUR VASE",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/dsc_0597_2_0.jpg?itok=TXvRM1HE",
                null, null);
        models.add(model);
        model = new CommonModel("4", "GOLD AND GLASS",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/10_0.jpg?itok=4BYJtRQB",
                null, null);
        models.add(model);
        model = new CommonModel("4", "MOSQUE LAMP",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/mia-catering.jpg?itok=Kk7svJPU",
                null, null);
        models.add(model);
        mAdapter.notifyDataSetChanged();
    }

//    public class RowCount extends AsyncTask<Void, Void, Integer>{
//
//        private WeakReference<CommonActivity> activityReference;
//        int language;
//
//        RowCount(CommonActivity context, int apiLanguage) {
//            activityReference = new WeakReference<>(context);
//            language = apiLanguage;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            publicArtsTableRowCount = integer;
//            if (publicArtsTableRowCount > 0) {
//                //update or add row to database
////                new CheckDBRowExist(CommonActivity.this, appLanguage).execute();
//            } else {
//                //create databse
////                new InsertDataToDataBase(CommonActivity.this, heritageListTable).execute();
//            }
//        }
//
//        @Override
//        protected Integer doInBackground(Void... voids) {
//            return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRows();
//        }
//    }
//
//    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
//        private WeakReference<CommonActivity> activityReference;
//        private PublicArtsTable publicArtsTable;
//        int language;
//
//        CheckDBRowExist(CommonActivity context, int apiLanguage) {
//            activityReference = new WeakReference<>(context);
//            language = apiLanguage;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            return null;
//        }
//    }


}

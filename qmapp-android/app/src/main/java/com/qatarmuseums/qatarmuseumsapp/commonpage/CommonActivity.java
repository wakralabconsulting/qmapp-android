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
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DiningActivity;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
    QMDatabase qmDatabase;
    HeritageListTableEnglish heritageListTableEnglish;
    HeritageListTableArabic heritageListTableArabic;
    PublicArtsTableEnglish publicArtsTableEnglish;
    PublicArtsTableArabic publicArtsTableArabic;
    ExhibitionListTableEnglish exhibitionListTableEnglish;
    ExhibitionListTableArabic exhibitionListTableArabic;
    RelativeLayout noResultFoundLayout;
    int publicArtsTableRowCount;
    int heritageTableRowCount;
    int exhibitionTableRowCount;
    int appLanguage;
    String pageName = null;
    String id;

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
        noResultFoundLayout = (RelativeLayout) findViewById(R.id.no_result_layout);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
        toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title_key));
        id = intent.getStringExtra("ID");
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
                navigationIntent.putExtra("ID", models.get(position).getId());
                navigationIntent.putExtra("COMING_FROM", toolbarTitle);
                navigationIntent.putExtra("IS_FAVOURITE", models.get(position).getIsfavourite());
                navigationIntent.putExtra("LONGITUDE", models.get(position).getLongitude());
                navigationIntent.putExtra("LATITUDE", models.get(position).getLatitude());
                navigationIntent.putExtra("PUBLIC_ARTS_ID", models.get(position).getId());
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
            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("Exhibition_List_Page.json");
            else
                getCommonListDataFromDatabase("Exhibition_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.sidemenu_heritage_text))) {
            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("Heritage_List_Page.json");
            else
                getCommonListDataFromDatabase("Heritage_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.sidemenu_public_arts_text))) {

            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("Public_Arts_List_Page.json");
            else
                getCommonListDataFromDatabase("Public_Arts_List_Page.json");


        } else if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text))) {
            getCommonListAPIDataFromAPI("getDiningList.json");
        } else if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
            getMuseumCollectionListFromAPI();
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

    public void getCommonListDataFromDatabase(String apiParts) {

        if (apiParts.equals("Heritage_List_Page.json")) {
            if (appLanguage == 1) {
                new RetriveDataEnglish(CommonActivity.this, appLanguage).execute();
            } else {
                new RetriveDataArabic(CommonActivity.this, appLanguage).execute();
            }

        } else if (apiParts.equals("Public_Arts_List_Page.json")) {
            if (appLanguage == 1) {
                new RetriveEnglishPublicArtsData(CommonActivity.this, appLanguage).execute();
            } else {
                new RetriveArabicPublicArtsData(CommonActivity.this, appLanguage).execute();
            }

        } else if (apiParts.equals("Exhibition_List_Page.json")) {
            if (appLanguage == 1) {
                new RetriveExhibitionDataEnglish(CommonActivity.this, appLanguage).execute();
            } else {
                new RetriveExhibitionDataArabic(CommonActivity.this, appLanguage).execute();
            }
        }


    }


    private void getMuseumCollectionListFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<CommonModel>> call = apiService.getCollectionList(language, id);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        mAdapter.notifyDataSetChanged();
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
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getCommonListAPIDataFromAPI(String name) {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        pageName = name;
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
                        if (pageName.equals("Heritage_List_Page.json")) {
                            new RowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("Public_Arts_List_Page.json")) {
                            new PublicArtsRowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("Exhibition_List_Page.json")) {
                            new ExhibitionRowCount(CommonActivity.this, language).execute();
                        }
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
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void prepareCollectionData() {

        CommonModel model = new CommonModel("1", "CERAMICS COLLECTION",
                null,
                null, null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/idam-pierremonetta_mg_6372_1.jpg?itok=bKArHUGQ",
                false, false);
        models.add(model);
        model = new CommonModel("2", "GLASS COLLECTION",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/inq.jpg?itok=C14Qr6xt",
                false, false);
        models.add(model);
        model = new CommonModel("3", "THE CAVOUR VASE",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/dsc_0597_2_0.jpg?itok=TXvRM1HE",
                false, false);
        models.add(model);
        model = new CommonModel("4", "GOLD AND GLASS",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/10_0.jpg?itok=4BYJtRQB",
                false, false);
        models.add(model);
        model = new CommonModel("4", "MOSQUE LAMP",
                null, null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/mia-catering.jpg?itok=Kk7svJPU",
                false, false);
        models.add(model);
        mAdapter.notifyDataSetChanged();
    }

    public class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;

        PublicArtsRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            publicArtsTableRowCount = integer;
            if (publicArtsTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckPublicArtsDBRowExist(CommonActivity.this, language).execute();
            } else {
                //create databse
                new InsertPublicArtsDataToDataBase(CommonActivity.this, publicArtsTableEnglish, publicArtsTableArabic, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsArabic();

        }
    }

    public class CheckPublicArtsDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private PublicArtsTableEnglish publicArtsTableEnglish;
        private PublicArtsTableArabic publicArtsTableArabic;
        String language;

        CheckPublicArtsDBRowExist(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (models.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkEnglishIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsTable(CommonActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            publicArtsTableEnglish = new PublicArtsTableEnglish(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getLongitude(),
                                    models.get(i).getLatitude());
                            activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkArabicIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsTable(CommonActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            publicArtsTableArabic = new PublicArtsTableArabic(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getLongitude(),
                                    models.get(i).getLatitude());
                            activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableArabic);

                        }
                    }
                }

            }
            return null;
        }
    }

    public class UpdatePublicArtsTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdatePublicArtsTable(CommonActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsEnglish(
                        models.get(position).getName(),
                        models.get(position).getLatitude(), models.get(position).getLongitude()
                        , models.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsArabic(
                        models.get(position).getName(),
                        models.get(position).getLatitude(), models.get(position).getLongitude()
                        , models.get(position).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public class InsertPublicArtsDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private PublicArtsTableEnglish publicArtsTableEnglish;
        private PublicArtsTableArabic publicArtsTableArabic;
        String language;

        InsertPublicArtsDataToDataBase(CommonActivity context, PublicArtsTableEnglish publicArtsTableEnglish,
                                       PublicArtsTableArabic publicArtsTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.publicArtsTableEnglish = publicArtsTableEnglish;
            this.publicArtsTableArabic = publicArtsTableArabic;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (models != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        publicArtsTableEnglish = new PublicArtsTableEnglish(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getLongitude(),
                                models.get(i).getLatitude());
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        publicArtsTableArabic = new PublicArtsTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getLongitude(),
                                models.get(i).getLatitude());
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableArabic);
                    }
                }

            }
            return true;
        }
    }


    public class RetriveEnglishPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveEnglishPublicArtsData(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableEnglish> publicArtsTableEnglish) {
            models.clear();
            for (int i = 0; i < publicArtsTableEnglish.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(publicArtsTableEnglish.get(i).getPublic_arts_id()),
                        publicArtsTableEnglish.get(i).getPublic_arts_name(),
                        "",
                        publicArtsTableEnglish.get(i).getLatitude(), publicArtsTableEnglish.get(i).getLongitude());
                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<PublicArtsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllEnglish();
        }
    }

    public class RetriveArabicPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveArabicPublicArtsData(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableArabic> publicArtsTableArabic) {
            models.clear();
            for (int i = 0; i < publicArtsTableArabic.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(publicArtsTableArabic.get(i).getPublic_arts_id()),
                        publicArtsTableArabic.get(i).getPublic_arts_name(),
                        "",
                        publicArtsTableArabic.get(i).getLatitude(), publicArtsTableArabic.get(i).getLongitude());
                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<PublicArtsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllArabic();
        }
    }

    public class RowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonActivity> activityReference;
        String language;

        RowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals("en"))
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            heritageTableRowCount = integer;
            if (heritageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(CommonActivity.this, language).execute();

            } else {
                //create databse
                new InsertDataToDataBase(CommonActivity.this, heritageListTableEnglish, heritageListTableArabic, language).execute();

            }
        }
    }


    public class InsertDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private HeritageListTableEnglish heritageListTableEnglish;
        private HeritageListTableArabic heritageListTableArabic;
        String language;

        InsertDataToDataBase(CommonActivity context, HeritageListTableEnglish heritageListTableEnglish,
                             HeritageListTableArabic heritageListTableArabic, String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.heritageListTableEnglish = heritageListTableEnglish;
            this.heritageListTableArabic = heritageListTableArabic;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (models != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        heritageListTableEnglish = new HeritageListTableEnglish(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                "", "", "", "",
                                "", models.get(i).getSortId());
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        heritageListTableArabic = new HeritageListTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                "", "", "", "",
                                "", models.get(i).getSortId());
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableArabic);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


    public class CheckDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private HeritageListTableEnglish heritageListTableEnglish;
        String language;

        CheckDBRowExist(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (models.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkEnglishIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritagePageTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            heritageListTableEnglish = new HeritageListTableEnglish(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getSortId(), "", "",
                                    "", "", "");
                            activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkArabicIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritagePageTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            heritageListTableArabic = new HeritageListTableArabic(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getSortId(), "", "", "",
                                    "", "");
                            activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableArabic);

                        }
                    }
                }
            }
            return null;
        }
    }

    public class UpdateHeritagePageTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        int position;

        UpdateHeritagePageTable(CommonActivity context, int apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language == 1) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageListEnglish(
                        models.get(position).getName(), models.get(position).getSortId(),
                        models.get(position).getImage(), models.get(position).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageListArabic(
                        models.get(position).getName(), models.get(position).getSortId(),
                        models.get(position).getImage(), models.get(position).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }

    public class RetriveDataEnglish extends AsyncTask<Void, Void, List<HeritageListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveDataEnglish(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<HeritageListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getAllEnglish();
        }

        @Override
        protected void onPostExecute(List<HeritageListTableEnglish> heritageListTableEnglishes) {
            models.clear();
            for (int i = 0; i < heritageListTableEnglishes.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(heritageListTableEnglishes.get(i).getHeritage_id()),
                        heritageListTableEnglishes.get(i).getHeritage_name(),
                        "", "", "", heritageListTableEnglishes.get(i).getHeritage_image(),
                        false, false);
                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    public class RetriveDataArabic extends AsyncTask<Void, Void, List<HeritageListTableArabic>> {

        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveDataArabic(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<HeritageListTableArabic> heritageListTableArabics) {
            models.clear();
            for (int i = 0; i < heritageListTableArabics.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(heritageListTableArabics.get(i).getHeritage_id()),
                        heritageListTableArabics.get(i).getHeritage_name(),
                        "", "", "", heritageListTableArabics.get(i).getHeritage_image(),
                        false, false);
                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<HeritageListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getAllArabic();
        }
    }


    public class ExhibitionRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;

        ExhibitionRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            exhibitionTableRowCount = integer;
            if (exhibitionTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckExhibitionDBRowExist(CommonActivity.this, language).execute();

            } else {
                //create databse
                new InsertExhibitionDataToDataBase(CommonActivity.this, exhibitionListTableEnglish, exhibitionListTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals("en"))
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsArabic();
        }

    }

    public class CheckExhibitionDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<CommonActivity> activityReference;
        private ExhibitionListTableEnglish exhibitionListTableEnglish;
        String language;

        CheckExhibitionDBRowExist(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (models.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkEnglishIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            exhibitionListTableEnglish = new ExhibitionListTableEnglish(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(), models.get(i).getImage(),
                                    models.get(i).getStartDate(), models.get(i).getEndDate(),
                                    models.get(i).getLocation(), "", "");
                            activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkArabicIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            exhibitionListTableArabic = new ExhibitionListTableArabic(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getStartDate(), models.get(i).getEndDate(),
                                    models.get(i).getLocation(), null, null);
                            activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableArabic);

                        }
                    }
                }
            }
            return null;
        }

    }

    public class InsertExhibitionDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private ExhibitionListTableEnglish exhibitionListTableEnglish;
        private ExhibitionListTableArabic exhibitionListTableArabic;
        String language;

        InsertExhibitionDataToDataBase(CommonActivity context, ExhibitionListTableEnglish exhibitionListTableEnglish,
                                       ExhibitionListTableArabic exhibitionListTableArabic, String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.exhibitionListTableEnglish = exhibitionListTableEnglish;
            this.exhibitionListTableArabic = exhibitionListTableArabic;
            this.language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (models != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        exhibitionListTableEnglish = new ExhibitionListTableEnglish(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getStartDate(),
                                models.get(i).getEndDate(),
                                models.get(i).getLocation(), "", "");
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        exhibitionListTableArabic = new ExhibitionListTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getStartDate(),
                                models.get(i).getEndDate(),
                                models.get(i).getLocation(), null, null);
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableArabic);
                    }
                }
            }
            return true;
        }
    }

    public class UpdateExhibitionTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        int position;

        UpdateExhibitionTable(CommonActivity context, int apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language == 1) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionListEnglish(
                        models.get(position).getStartDate(), models.get(position).getEndDate(),
                        models.get(position).getLocation(), models.get(position).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionListArabic(
                        models.get(position).getStartDate(), models.get(position).getEndDate(),
                        models.get(position).getLocation(), models.get(position).getId()
                );
            }
            return null;
        }
    }

    public class RetriveExhibitionDataEnglish extends AsyncTask<Void, Void, List<ExhibitionListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveExhibitionDataEnglish(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableEnglish> exhibitionListTableEnglish) {
            models.clear();
            for (int i = 0; i < exhibitionListTableEnglish.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableEnglish.get(i).getExhibition_id()),
                        exhibitionListTableEnglish.get(i).getExhibition_name(),
                        exhibitionListTableEnglish.get(i).getExhibition_start_date(),
                        exhibitionListTableEnglish.get(i).getExhibition_end_date(),
                        exhibitionListTableEnglish.get(i).getExhibition_location(),
                        exhibitionListTableEnglish.get(i).getExhibition_latest_image(),
                        null, false);

                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<ExhibitionListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getAllEnglish();
        }
    }

    public class RetriveExhibitionDataArabic extends AsyncTask<Void, Void, List<ExhibitionListTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveExhibitionDataArabic(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableArabic> exhibitionListTableArabic) {
            models.clear();
            for (int i = 0; i < exhibitionListTableArabic.size(); i++) {
                CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableArabic.get(i).getExhibition_id()),
                        exhibitionListTableArabic.get(i).getExhibition_name(),
                        exhibitionListTableArabic.get(i).getExhibition_start_date(),
                        exhibitionListTableArabic.get(i).getExhibition_end_date(),
                        exhibitionListTableArabic.get(i).getExhibition_location(),
                        exhibitionListTableArabic.get(i).getExhibition_latest_image(),
                        null, false);

                models.add(i, commonModel);

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<ExhibitionListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getExhibitionTableDao().getAllArabic();

        }
    }

}

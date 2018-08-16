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
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTable;
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
    HeritageListTable heritageListTable;
    PublicArtsTableEnglish publicArtsTableEnglish;
    PublicArtsTableArabic publicArtsTableArabic;
    RelativeLayout noResultFoundLayout;
    int publicArtsTableRowCount;
    int heritageTableRowCount;
    int appLanguage;
    String pageName = null;

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
        appLanguage = qmPreferences.getInt("AppLanguage", 1);
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
            prepareExhibitionData();
//            getCommonListAPIData("Exhibition_List_Page.json");
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

    public void getCommonListDataFromDatabase(String apiParts) {

        if (apiParts.equals("Heritage_List_Page.json")) {
            new RetriveData(CommonActivity.this, appLanguage).execute();
        } else if (apiParts.equals("Public_Arts_List_Page.json")) {
            if (appLanguage == 1) {
                new RetriveEnglishPublicArtsData(CommonActivity.this, appLanguage).execute();
            } else {
                new RetriveArabicPublicArtsData(CommonActivity.this, appLanguage).execute();
            }

        }


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
                            new RowCount(CommonActivity.this, appLanguage).execute();
                        } else if (pageName.equals("Public_Arts_List_Page.json")) {
                            new PublicArtsRowCount(CommonActivity.this, language).execute();
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
            //Toast.makeText(CommonActivity.this, "Databse updated", Toast.LENGTH_SHORT).show();
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
                                models.get(i).getLongitude(),
                                models.get(i).getLatitude());
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        publicArtsTableArabic = new PublicArtsTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
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
        int language;

        RowCount(CommonActivity context, int apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRows();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            heritageTableRowCount = integer;
            if (heritageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckDBRowExist(CommonActivity.this, appLanguage).execute();

            } else {
                //create databse
                new InsertDataToDataBase(CommonActivity.this, heritageListTable).execute();

            }
        }
    }


    public class InsertDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private HeritageListTable heritageListTable;

        InsertDataToDataBase(CommonActivity context, HeritageListTable heritageListTable) {
            activityReference = new WeakReference<>(context);
            this.heritageListTable = heritageListTable;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (models != null) {
                for (int i = 0; i < models.size(); i++) {
                    heritageListTable = new HeritageListTable(Long.parseLong(models.get(i).getId()),
                            models.get(i).getName(),
                            models.get(i).getImage(),
                            models.get(i).getSortId());
                    activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTable);
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
        private HeritageListTable heritageListTable;
        int language;

        CheckDBRowExist(CommonActivity context, int apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (models.size() > 0) {
                for (int i = 0; i < models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkIdExist(
                            Integer.parseInt(models.get(i).getId()));
                    if (n > 0) {
                        //updateEnglishTable same id
                        new UpdateHeritagePageTable(CommonActivity.this, appLanguage, i).execute();

                    } else {
                        //create row with corresponding id
                        heritageListTable = new HeritageListTable(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getSortId());
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTable);

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
            //Toast.makeText(CommonActivity.this, "Databse updated", Toast.LENGTH_SHORT).show();
        }


    }

    public class RetriveData extends AsyncTask<Void, Void, List<HeritageListTable>> {
        private WeakReference<CommonActivity> activityReference;
        int language;

        RetriveData(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<HeritageListTable> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getAll();
        }

        @Override
        protected void onPostExecute(List<HeritageListTable> heritageListTables) {
            models.clear();
            if (language == 1) {
                for (int i = 0; i < heritageListTables.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(heritageListTables.get(i).getHeritage_id()),
                            heritageListTables.get(i).getHeritage_name(),
                            "", "", "", heritageListTables.get(i).getHeritage_image(),
                            false, false);
                    models.add(i, commonModel);

                }

            } else {
                for (int i = 0; i < heritageListTables.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(heritageListTables.get(i).getHeritage_id()),
                            heritageListTables.get(i).getHeritage_name_arabic(),
                            "", "", "", heritageListTables.get(i).getHeritage_image(),
                            false, false);
                    models.add(i, commonModel);

                }

            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }


}

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
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DiningActivity;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableEnglish;
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
    MuseumCollectionListTableEnglish museumCollectionListTableEnglish;
    MuseumCollectionListTableArabic museumCollectionListTableArabic;
    int exhibitionTableRowCount;
    DiningTableEnglish diningTableEnglish;
    DiningTableArabic diningTableArabic;
    RelativeLayout noResultFoundLayout;
    int publicArtsTableRowCount;
    int heritageTableRowCount, museumCollectionListRowCount;
    int diningTableRowCount;
    int appLanguage;
    String pageName = null;
    String id;
    private APIInterface apiService;
    private Call<ArrayList<CommonModel>> call;

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
                    navigationIntent = new Intent(CommonActivity.this,
                            DiningActivity.class);
                else if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
                    navigationIntent = new Intent(CommonActivity.this,
                            CollectionDetailsActivity.class);

                else
                    navigationIntent = new Intent(CommonActivity.this, DetailsActivity.class);
                navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImage());
                navigationIntent.putExtra("MAIN_TITLE", models.get(position).getName());
                navigationIntent.putExtra("LONG_DESC", models.get(position).getDescription());
                navigationIntent.putExtra("ID", models.get(position).getId());
                navigationIntent.putExtra("CATEGORY_ID", models.get(position).getCategory());
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
            /* Commented for Temporary API
            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("Exhibition_List_Page.json");
            else
                getCommonListDataFromDatabase("Exhibition_List_Page.json");
            */
            if (util.isNetworkAvailable(CommonActivity.this)) // For Temporary API
                getCommonListAPIDataFromAPI("Exhibition_List_Page.php");
            else
                getCommonListDataFromDatabase("Exhibition_List_Page.php");
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

            /* Commented for Temporary API
            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("getDiningList.json");
            else
                getCommonListDataFromDatabase("getDiningList.json");
             */

            if (util.isNetworkAvailable(CommonActivity.this))  // For Temporary API
                getCommonListAPIDataFromAPI("getDiningList.php");
            else
                getCommonListDataFromDatabase("getDiningList.php");

        } else if (toolbarTitle.equals(getString(R.string.museum_collection_text))) {

            if (util.isNetworkAvailable(CommonActivity.this))
                getMuseumCollectionListFromAPI();
            else
                getMuseumCollectionListFromDatabase();
        }
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
        } else if (apiParts.equals("getDiningList.json")) {
            String language;
            if (appLanguage == 1)
                language = "en";
            else
                language = "ar";
            new DiningRowCount(CommonActivity.this, language).execute();
        } else if (apiParts.equals("getDiningList.php")) {    // For Temporary API
            String language;
            if (appLanguage == 1)
                language = "en";
            else
                language = "ar";
            new DiningRowCount(CommonActivity.this, language, id).execute();
        } else if (apiParts.equals("Exhibition_List_Page.php")) {   // For Temporary API
            if (appLanguage == 1) {
                new RetriveExhibitionDataEnglish(CommonActivity.this, appLanguage, id).execute();
            } else {
                new RetriveExhibitionDataArabic(CommonActivity.this, appLanguage, id).execute();
            }
        }


    }


    private void getMuseumCollectionListFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        final String language;
        if (appLanguage == 1) {
            language = "en";
        } else {
            language = "ar";
        }
        APIInterface apiService =
                APIClient.getTempClient().create(APIInterface.class);

        Call<ArrayList<CommonModel>> call = apiService.getCollectionList(language, id);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new MuseumCollectionRowCount(CommonActivity.this, language).execute();
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

    public void removeHtmlTags(ArrayList<CommonModel> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setName(util.html2string(models.get(i).getName()));
            models.get(i).setStartDate(util.html2string(models.get(i).getStartDate()));
            models.get(i).setEndDate(util.html2string(models.get(i).getEndDate()));
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

        if (pageName.contains("php"))  // For Temporary API
            apiService = APIClient.getTempClient().create(APIInterface.class);
        else
            apiService = APIClient.getClient().create(APIInterface.class);
        if (id != null)
            call = apiService.getCommonpageListWithID(language, pageName, id);
        else
            call = apiService.getCommonpageList(language, pageName);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        if (pageName.equals("Heritage_List_Page.json")) {
                            new RowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("Public_Arts_List_Page.json")) {
                            new PublicArtsRowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("Exhibition_List_Page.json")) {
                            new ExhibitionRowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("getDiningList.json")) {
                            new DiningRowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("getDiningList.php")) {  // For Temporary API
                            new DiningRowCount(CommonActivity.this, language).execute();
                        } else if (pageName.equals("Exhibition_List_Page.php")) {  // For Temporary API
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


    public class DiningRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;
        String museumID;

        DiningRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        DiningRowCount(CommonActivity context, String apiLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            diningTableRowCount = integer;
            if (diningTableRowCount > 0) {
                if (models.size() > 0)
                    //updateEnglishTable or add row to database
                    new CheckDiningDBRowExist(CommonActivity.this, language).execute();
                else {
                    if (museumID == null) {
                        if (language.equals("en")) {
                            new RetriveEnglishDiningData(CommonActivity.this, 1).execute();
                        } else {
                            new RetriveArabicDiningData(CommonActivity.this, 2).execute();
                        }
                    } else {
                        if (language.equals("en")) {
                            new RetriveEnglishDiningData(CommonActivity.this, 1, museumID).execute();
                        } else {
                            new RetriveArabicDiningData(CommonActivity.this, 2, museumID).execute();
                        }
                    }
                }
            } else if (models.size() > 0) {
                //create databse
                new InsertDiningDataToDataBase(CommonActivity.this, diningTableEnglish, diningTableArabic, language).execute();
            } else {
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals("en"))
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsArabic();

        }
    }

    public class CheckDiningDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private DiningTableEnglish diningTableEnglish;
        private DiningTableArabic diningTableArabic;
        String language;

        CheckDiningDBRowExist(CommonActivity context, String apiLanguage) {
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
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkEnglishIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(CommonActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            diningTableEnglish = new DiningTableEnglish(Long.parseLong(
                                    models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getSortId(),
                                    models.get(i).getMuseumId());
                            activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkArabicIdExist(
                                Integer.parseInt(models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(CommonActivity.this, language, i).execute();

                        } else {
                            //create row with corresponding id
                            diningTableArabic = new DiningTableArabic(Long.parseLong(models.get(i).getId()),
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getSortId(),
                                    models.get(i).getMuseumId());
                            activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableArabic);

                        }
                    }
                }

            }
            return null;
        }
    }

    public class UpdateDiningTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdateDiningTable(CommonActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningEnglish(
                        models.get(position).getName(),
                        models.get(position).getImage(),
                        models.get(position).getId(),
                        models.get(position).getSortId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningArabic(
                        models.get(position).getName(),
                        models.get(position).getImage(),
                        models.get(position).getId(),
                        models.get(position).getSortId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public class InsertDiningDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private DiningTableEnglish diningTableEnglish;
        private DiningTableArabic diningTableArabic;
        String language;

        InsertDiningDataToDataBase(CommonActivity context, DiningTableEnglish diningTableEnglish,
                                   DiningTableArabic diningTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.diningTableEnglish = diningTableEnglish;
            this.diningTableArabic = diningTableArabic;
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
                        diningTableEnglish = new DiningTableEnglish(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getSortId(),
                                models.get(i).getMuseumId());
                        activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        diningTableArabic = new DiningTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getSortId(),
                                models.get(i).getMuseumId());
                        activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableArabic);
                    }
                }

            }
            return true;
        }
    }

    public class RetriveEnglishDiningData extends AsyncTask<Void, Void, List<DiningTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumID;

        RetriveEnglishDiningData(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        RetriveEnglishDiningData(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTableEnglish> diningTableEnglishList) {
            if (diningTableEnglishList.size() > 0) {
                models.clear();
                for (int i = 0; i < diningTableEnglishList.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(diningTableEnglishList.get(i).getDining_id()),
                            diningTableEnglishList.get(i).getDining_name(),
                            diningTableEnglishList.get(i).getDining_image(), "", "");
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<DiningTableEnglish> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsEnglishWithMuseumId(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getAllEnglish();
        }
    }

    public class RetriveArabicDiningData extends AsyncTask<Void, Void, List<DiningTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumID;

        RetriveArabicDiningData(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        RetriveArabicDiningData(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTableArabic> diningTableArabicList) {
            if (diningTableArabicList.size() > 0) {
                models.clear();
                for (int i = 0; i < diningTableArabicList.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(diningTableArabicList.get(i).getDining_id()),
                            diningTableArabicList.get(i).getDining_name(),
                            diningTableArabicList.get(i).getDining_image(), "", "");
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<DiningTableArabic> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsArabicWithMuseumId(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getAllArabic();
        }
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
            if (publicArtsTableEnglish.size() > 0) {
                for (int i = 0; i < publicArtsTableEnglish.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(publicArtsTableEnglish.get(i).getPublic_arts_id()),
                            publicArtsTableEnglish.get(i).getPublic_arts_name(),
                            "",
                            publicArtsTableEnglish.get(i).getLatitude(), publicArtsTableEnglish.get(i).getLongitude());
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
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
            if (publicArtsTableArabic.size() > 0) {
                for (int i = 0; i < publicArtsTableArabic.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(publicArtsTableArabic.get(i).getPublic_arts_id()),
                            publicArtsTableArabic.get(i).getPublic_arts_name(),
                            "",
                            publicArtsTableArabic.get(i).getLatitude(), publicArtsTableArabic.get(i).getLongitude());
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
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
                                null, null, null, null,
                                null, models.get(i).getSortId());
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        heritageListTableArabic = new HeritageListTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                null, null, null, null,
                                null, models.get(i).getSortId());
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
                                    models.get(i).getSortId(), null, null,
                                    null, null, null);
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
                                    models.get(i).getSortId(), null, null, null,
                                    null, null);
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
            if (heritageListTableEnglishes.size() > 0) {
                for (int i = 0; i < heritageListTableEnglishes.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(heritageListTableEnglishes.get(i).getHeritage_id()),
                            heritageListTableEnglishes.get(i).getHeritage_name(),
                            null, null, null,
                            heritageListTableEnglishes.get(i).getHeritage_image(),
                            null, null);
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
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
            if (heritageListTableArabics.size() > 0) {
                for (int i = 0; i < heritageListTableArabics.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(heritageListTableArabics.get(i).getHeritage_id()),
                            heritageListTableArabics.get(i).getHeritage_name(),
                            null, null, null, heritageListTableArabics.get(i).getHeritage_image(),
                            null, null);
                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
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
                                    models.get(i).getName(),
                                    models.get(i).getImage(),
                                    models.get(i).getStartDate(),
                                    models.get(i).getEndDate(),
                                    models.get(i).getLocation(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    models.get(i).getMuseumId());
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
                                    models.get(i).getStartDate(),
                                    models.get(i).getEndDate(),
                                    models.get(i).getLocation(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    models.get(i).getMuseumId());
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
                                models.get(i).getLocation(),
                                null,
                                null,
                                null,
                                null,
                                models.get(i).getMuseumId());
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        exhibitionListTableArabic = new ExhibitionListTableArabic(Long.parseLong(models.get(i).getId()),
                                models.get(i).getName(),
                                models.get(i).getImage(),
                                models.get(i).getStartDate(),
                                models.get(i).getEndDate(),
                                models.get(i).getLocation(),
                                null,
                                null,
                                null,
                                null,
                                models.get(i).getMuseumId());
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
                        models.get(position).getStartDate(),
                        models.get(position).getEndDate(),
                        models.get(position).getLocation(),
                        models.get(position).getId(),
                        models.get(position).getMuseumId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionListArabic(
                        models.get(position).getStartDate(),
                        models.get(position).getEndDate(),
                        models.get(position).getLocation(),
                        models.get(position).getId(),
                        models.get(position).getMuseumId()
                );
            }
            return null;
        }
    }

    public class RetriveExhibitionDataEnglish extends AsyncTask<Void, Void, List<ExhibitionListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumID;

        RetriveExhibitionDataEnglish(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        RetriveExhibitionDataEnglish(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableEnglish> exhibitionListTableEnglish) {
            models.clear();
            if (exhibitionListTableEnglish.size() > 0) {
                for (int i = 0; i < exhibitionListTableEnglish.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableEnglish.get(i).getExhibition_id()),
                            exhibitionListTableEnglish.get(i).getExhibition_name(),
                            exhibitionListTableEnglish.get(i).getExhibition_start_date(),
                            exhibitionListTableEnglish.get(i).getExhibition_end_date(),
                            exhibitionListTableEnglish.get(i).getExhibition_location(),
                            exhibitionListTableEnglish.get(i).getExhibition_latest_image(),
                            null);

                    models.add(i, commonModel);

                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ExhibitionListTableEnglish> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionWithMuseumIdEnglish(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getExhibitionTableDao().getAllEnglish();
        }
    }

    public class RetriveExhibitionDataArabic extends AsyncTask<Void, Void, List<ExhibitionListTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumID;

        RetriveExhibitionDataArabic(CommonActivity context, int appLanguage) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
        }

        RetriveExhibitionDataArabic(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableArabic> exhibitionListTableArabic) {
            models.clear();
            if (exhibitionListTableArabic.size() > 0) {
                for (int i = 0; i < exhibitionListTableArabic.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableArabic.get(i).getExhibition_id()),
                            exhibitionListTableArabic.get(i).getExhibition_name(),
                            exhibitionListTableArabic.get(i).getExhibition_start_date(),
                            exhibitionListTableArabic.get(i).getExhibition_end_date(),
                            exhibitionListTableArabic.get(i).getExhibition_location(),
                            exhibitionListTableArabic.get(i).getExhibition_latest_image(),
                            null);
                    models.add(i, commonModel);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected List<ExhibitionListTableArabic> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getExhibitionTableDao().getExhibitionWithMuseumIdArabic(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getExhibitionTableDao().getAllArabic();

        }
    }

    public void getMuseumCollectionListFromDatabase() {
        id = "63"; // For Temporary
        if (appLanguage == 1) {
            new RetriveMuseumCollectionDataEnglish(CommonActivity.this, appLanguage, id).execute();
        } else {
            new RetriveMuseumCollectionDataArabic(CommonActivity.this, appLanguage, id).execute();
        }
    }

    public class MuseumCollectionRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonActivity> activityReference;
        String language;

        MuseumCollectionRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals("en"))
                return activityReference.get().qmDatabase.getMuseumCollectionListDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getMuseumCollectionListDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            museumCollectionListRowCount = integer;
            if (museumCollectionListRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumCollectionDBRowExist(CommonActivity.this, language).execute();

            } else {
                //create databse
                new InsertMuseumCollectionListDataToDataBase(CommonActivity.this, museumCollectionListTableEnglish,
                        museumCollectionListTableArabic, language).execute();

            }
        }
    }


    public class CheckMuseumCollectionDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private MuseumCollectionListTableEnglish museumCollectionListTableEnglish;
        private MuseumCollectionListTableArabic museumCollectionListTableArabic;
        String language;

        CheckMuseumCollectionDBRowExist(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (models.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumCollectionListDao().checkNameExistEnglish(
                                models.get(i).getName());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumCollectionListTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            museumCollectionListTableEnglish = new MuseumCollectionListTableEnglish(models.get(i).getName(),
                                    models.get(i).getImage(), models.get(i).getMuseumId(),models.get(i).getCategory(),
                                    models.get(i).getDescription(),null,
                                    null,null,null,null,
                                    null,null);
                            activityReference.get().qmDatabase.getMuseumCollectionListDao().insertEnglishTable(museumCollectionListTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumCollectionListDao().checkNameExistArabic(
                                models.get(i).getName());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumCollectionListTable(CommonActivity.this, appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            museumCollectionListTableArabic = new MuseumCollectionListTableArabic(models.get(i).getName(),
                                    models.get(i).getImage(), models.get(i).getMuseumId(),
                                    models.get(i).getCategory(),models.get(i).getDescription(),null,
                                    null,null,null,null,
                                    null,null);
                            activityReference.get().qmDatabase.getMuseumCollectionListDao().insertArabicTable(museumCollectionListTableArabic);

                        }
                    }
                }
            }
            return null;
        }
    }


    public class InsertMuseumCollectionListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private MuseumCollectionListTableEnglish museumCollectionListTableEnglish;
        private MuseumCollectionListTableArabic museumCollectionListTableArabic;
        String language;

        InsertMuseumCollectionListDataToDataBase(CommonActivity context,
                                                 MuseumCollectionListTableEnglish museumCollectionListTableEnglish,
                                                 MuseumCollectionListTableArabic museumCollectionListTableArabic, String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.museumCollectionListTableEnglish = museumCollectionListTableEnglish;
            this.museumCollectionListTableArabic = museumCollectionListTableArabic;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (models != null) {
                if (language.equals("en")) {
                    for (int i = 0; i < models.size(); i++) {
                        museumCollectionListTableEnglish = new MuseumCollectionListTableEnglish(models.get(i).getName(),
                                models.get(i).getImage(), models.get(i).getMuseumId(),
                                models.get(i).getCategory(),models.get(i).getDescription(),null,
                        null,null,null,null,
                                null,null);
                        activityReference.get().qmDatabase.getMuseumCollectionListDao().insertEnglishTable(museumCollectionListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < models.size(); i++) {
                        museumCollectionListTableArabic = new MuseumCollectionListTableArabic(models.get(i).getName(),
                                models.get(i).getImage(), models.get(i).getMuseumId(),
                                models.get(i).getCategory(),models.get(i).getDescription(),
                                null,
                                null,null,null,null,
                                null,null);
                        activityReference.get().qmDatabase.getMuseumCollectionListDao().insertArabicTable(museumCollectionListTableArabic);
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public class UpdateMuseumCollectionListTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        int position;

        UpdateMuseumCollectionListTable(CommonActivity context, int apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language == 1) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumCollectionListDao().updateMuseumListTableEnglish(
                        models.get(position).getImage(), models.get(position).getMuseumId(),
                        models.get(position).getCategory(),models.get(position).getDescription(),
                        models.get(position).getName());

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getMuseumCollectionListDao().updateMuseumListTableArabic(
                        models.get(position).getImage(), models.get(position).getMuseumId(),
                        models.get(position).getCategory(),models.get(position).getDescription(),
                        models.get(position).getName());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }


    public class RetriveMuseumCollectionDataEnglish extends AsyncTask<Void, Void, List<MuseumCollectionListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumReference;

        RetriveMuseumCollectionDataEnglish(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumReference = museumId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionListDao().
                    getDataFromEnglishTableWithReference(museumReference);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionListTableEnglish> museumCollectionListTableEnglishes) {
            models.clear();
            if (museumCollectionListTableEnglishes.size() > 0) {
                for (int i = 0; i < museumCollectionListTableEnglishes.size(); i++) {
                    CommonModel commonModel = new CommonModel(museumCollectionListTableEnglishes.get(i).getMuseum_id(),museumCollectionListTableEnglishes.get(i).getName(),
                            museumCollectionListTableEnglishes.get(i).getImage(),
                            museumCollectionListTableEnglishes.get(i).getCategory(),museumCollectionListTableEnglishes.get(i).getCollection_description());
                    models.add(i, commonModel);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public class RetriveMuseumCollectionDataArabic extends AsyncTask<Void, Void, List<MuseumCollectionListTableArabic>> {

        private WeakReference<CommonActivity> activityReference;
        int language;
        String museumReference;

        RetriveMuseumCollectionDataArabic(CommonActivity context, int appLanguage, String museumId) {
            activityReference = new WeakReference<>(context);
            language = appLanguage;
            museumReference = museumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionListTableArabic> museumCollectionListTableArabics) {
            models.clear();
            if (museumCollectionListTableArabics.size() > 0) {
                for (int i = 0; i < museumCollectionListTableArabics.size(); i++) {
                    CommonModel commonModel = new CommonModel( museumCollectionListTableArabics.get(i).getMuseum_id(),museumCollectionListTableArabics.get(i).getName(),
                            museumCollectionListTableArabics.get(i).getImage(),
                            museumCollectionListTableArabics.get(i).getCategory(),museumCollectionListTableArabics.get(i).getCollection_description());
                    models.add(i, commonModel);
                }
                mAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noResultFoundLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<MuseumCollectionListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionListDao().
                    getDataFromArabicTableWithReference(museumReference);
        }
    }


}

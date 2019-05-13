package com.qatarmuseums.qatarmuseumsapp.commonlistpage;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.Convertor;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.commonlistsecondary.SecondaryListActivity;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTable;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.facilities.FacilityListTable;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTable;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CommonListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<CommonListModel> models = new ArrayList<>();
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
    PublicArtsTable publicArtsTable;
    ExhibitionListTable exhibitionListTable;
    MuseumCollectionListTable museumCollectionListTable;
    int exhibitionTableRowCount;
    DiningTable diningTable;
    TravelDetailsTable travelDetailsTable;
    TourListTable tourListTable;
    FacilityListTable facilityListTable;
    RelativeLayout noResultFoundLayout;
    int publicArtsTableRowCount;
    int heritageTableRowCount, museumCollectionListRowCount;
    int diningTableRowCount;
    String appLanguage;
    String pageName = null;
    String id;
    private APIInterface apiService;
    private Call<ArrayList<CommonListModel>> call;


    LinearLayout retryLayout;
    Button retryButton;
    private Integer travelTableRowCount;
    private Convertor convertor;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_list);
        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.toolbar_back);
        intent = getIntent();
        util = new Util();
        qmDatabase = QMDatabase.getInstance(this);
        progressBar = findViewById(R.id.progressBarLoading);
        noResultFoundLayout = findViewById(R.id.no_result_layout);
        retryLayout = findViewById(R.id.retry_layout);
        retryButton = findViewById(R.id.retry_btn);
        qmPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appLanguage = LocaleManager.getLanguage(this);
        toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title_key));
        id = intent.getStringExtra("ID");
        toolbar_title.setText(toolbarTitle);
        recyclerView = findViewById(R.id.common_recycler_view);

        mAdapter = new CommonListAdapter(this, models, position -> {
            Timber.i("%s is clicked with ID: %s from %s",
                    models.get(position).getName().toUpperCase(),
                    models.get(position).getId(),
                    toolbarTitle);
            if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
                navigationIntent = new Intent(CommonListActivity.this,
                        CollectionDetailsActivity.class);
            else if (toolbarTitle.equals(getString(R.string.museum_tours)) ||
                    toolbarTitle.equals(getString(R.string.museum_discussion)) ||
                    models.get(position).getId().equals("15256") ||
                    models.get(position).getId().equals("15826"))
                navigationIntent = new Intent(CommonListActivity.this,
                        SecondaryListActivity.class);
            else
                navigationIntent = new Intent(CommonListActivity.this, DetailsActivity.class);
            if (toolbarTitle.equals(getString(R.string.museum_tours)) ||
                    toolbarTitle.equals(getString(R.string.museum_discussion))) {
                if (models.get(position).getImages().size() > 0) {
                    navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImages().get(0));
                }
            } else
                navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImage());

            navigationIntent.putExtra("MAIN_TITLE", models.get(position).getName());
            navigationIntent.putExtra("LONG_DESC", models.get(position).getDescription());
            navigationIntent.putExtra("ID", models.get(position).getId());
            navigationIntent.putExtra("COMING_FROM", toolbarTitle);
            navigationIntent.putExtra("IS_FAVOURITE", models.get(position).getIsFavourite());
            navigationIntent.putExtra("LONGITUDE", models.get(position).getLongitude());
            navigationIntent.putExtra("LATITUDE", models.get(position).getLatitude());
            navigationIntent.putExtra("PUBLIC_ARTS_ID", models.get(position).getId());
            navigationIntent.putExtra("PROMOTION_CODE", models.get(position).getPromotionalCode());
            navigationIntent.putExtra("CLAIM_OFFER", models.get(position).getClaimOffer());
            navigationIntent.putExtra("CONTACT_PHONE", models.get(position).getContactNumber());
            navigationIntent.putExtra("CONTACT_MAIL", models.get(position).getEmail());
            startActivity(navigationIntent);
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        backArrow.setOnClickListener(v -> onBackPressed());
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            getData();
            progressBar.setVisibility(View.VISIBLE);
            retryLayout.setVisibility(View.GONE);
        });
        retryButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    retryButton.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        backArrow.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    backArrow.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        getData();
        convertor = new Convertor();
    }


    public void getData() {
        Timber.i("getData() for %s", toolbarTitle);
        if (toolbarTitle.equals(getString(R.string.side_menu_exhibition_text))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getCommonListAPIDataFromAPI("Exhibition_List_Page.json");
            else
                getCommonListDataFromDatabase("Exhibition_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.side_menu_heritage_text))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getCommonListAPIDataFromAPI("Heritage_List_Page.json");
            else
                getCommonListDataFromDatabase("Heritage_List_Page.json");
        } else if (toolbarTitle.equals(getString(R.string.side_menu_public_arts_text))) {

            if (util.isNetworkAvailable(CommonListActivity.this))
                getCommonListAPIDataFromAPI("Public_Arts_List_Page.json");
            else
                getCommonListDataFromDatabase("Public_Arts_List_Page.json");

        } else if (toolbarTitle.equals(getString(R.string.side_menu_dining_text))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getCommonListAPIDataFromAPI("getDiningList.json");
            else
                getCommonListDataFromDatabase("getDiningList.json");
        } else if (toolbarTitle.equals(getString(R.string.museum_collection_text))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getMuseumCollectionListFromAPI();
            else
                getMuseumCollectionListFromDatabase();
        } else if (toolbarTitle.equals(getString(R.string.museum_tours))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getTourListFromAPI();
            else
                getTourListFromDatabase();
        } else if (toolbarTitle.equals(getString(R.string.museum_travel))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getTravelDataFromAPI();
            else
                getTravelDataFromDataBase();
        } else if (toolbarTitle.equals(getString(R.string.museum_discussion))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getSpecialEventFromAPI();
            else
                getSpecialEventFromDatabase();

        } else if (toolbarTitle.equals(getString(R.string.facilities_txt))) {
            if (util.isNetworkAvailable(CommonListActivity.this))
                getFacilityListFromAPI();
            else
                getFacilityListFromDataBase();
        }
    }

    private void getFacilityListFromAPI() {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<CommonListModel>> call = apiService.getFacilityList(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        for (int i = 0; i < models.size(); i++) {
                            models.get(i).setImageTypeIsArray(true);
                        }
                        removeHtmlTags(models);
                        Collections.sort(models);
                        Timber.i("Set %s list with size: %d", toolbarTitle, response.body().size());
                        mAdapter.notifyDataSetChanged();
                        new FacilityRowCount(CommonListActivity.this, appLanguage).execute();

                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void getTourListFromAPI() {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getTourList(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        Timber.i("Setting %s list with size: %d", toolbarTitle, response.body().size());
                        for (int i = 0; i < response.body().size(); i++) {
                            models.add(new CommonListModel(
                                    response.body().get(i).getId(),
                                    response.body().get(i).getEventDay(),
                                    response.body().get(i).getEventDate(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getImages(),
                                    true));
                        }
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TourRowCount(CommonListActivity.this, appLanguage, 1).execute();
                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSpecialEventFromAPI() {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getSpecialEvents(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        Timber.i("Setting %s list with size: %d", toolbarTitle, response.body().size());
                        for (int i = 0; i < response.body().size(); i++) {
                            models.add(new CommonListModel(
                                    response.body().get(i).getId(),
                                    response.body().get(i).getEventDay(),
                                    response.body().get(i).getEventDate(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getImages(),
                                    false));
                        }
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TourRowCount(CommonListActivity.this, appLanguage, 0).execute();
                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getTravelDataFromAPI() {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getTravelData(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        Timber.i("Setting %s list with size: %d", toolbarTitle, response.body().size());
                        for (int i = 0; i < response.body().size(); i++) {
                            models.add(new CommonListModel(response.body().get(i).getName(),
                                    response.body().get(i).getImage(),
                                    response.body().get(i).getDescription(),
                                    response.body().get(i).getEmail(),
                                    response.body().get(i).getContactNumber(),
                                    response.body().get(i).getPromotionalCode(),
                                    response.body().get(i).getClaimOffer(),
                                    response.body().get(i).getId(),
                                    true));
                        }
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TravelRowCount(CommonListActivity.this, appLanguage).execute();
                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getMuseumCollectionListFromAPI() {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);

        call = apiService.getCollectionList(appLanguage, id);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("Setting %s list with size: %d", toolbarTitle, response.body().size());
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new MuseumCollectionRowCount(CommonListActivity.this, appLanguage).execute();
                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getCommonListAPIDataFromAPI(String name) {
        Timber.i("get%sListFromAPI(language :%s)", toolbarTitle, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        pageName = name;
        apiService = APIClient.getClient().create(APIInterface.class);
        if (id != null)
            call = apiService.getCommonPageListWithID(appLanguage, pageName, id);
        else
            call = apiService.getCommonPageList(appLanguage, pageName);
        call.enqueue(new Callback<ArrayList<CommonListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonListModel>> call, Response<ArrayList<CommonListModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("Setting %s list with size: %d", toolbarTitle, response.body().size());
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        removeHtmlTags(models);
                        Collections.sort(models);
                        mAdapter.notifyDataSetChanged();
                        if (pageName.equals("Heritage_List_Page.json")) {
                            new HeritageRowCount(CommonListActivity.this, appLanguage).execute();
                        } else if (pageName.equals("Public_Arts_List_Page.json")) {
                            new PublicArtsRowCount(CommonListActivity.this, appLanguage).execute();
                        } else if (pageName.equals("Exhibition_List_Page.json")) {
                            new ExhibitionRowCount(CommonListActivity.this, appLanguage).execute();
                        } else if (pageName.equals("getDiningList.json")) {
                            new DiningRowCount(CommonListActivity.this, appLanguage).execute();
                        }
                    } else {
                        Timber.i("%s list have no data", toolbarTitle);
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("%s list response is not successful", toolbarTitle);
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonListModel>> call, Throwable t) {
                Timber.e("get%sListFromAPI() - onFailure: %s", toolbarTitle, t.getMessage());
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void removeHtmlTags(ArrayList<CommonListModel> models) {
        if (models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                models.get(i).setName(util.html2string(models.get(i).getName()));
                models.get(i).setDescription(util.html2string(models.get(i).getDescription()));
                models.get(i).setStartDate(util.html2string(models.get(i).getStartDate()));
                models.get(i).setEndDate(util.html2string(models.get(i).getEndDate()));
            }
        }
    }

    public void getCommonListDataFromDatabase(String apiParts) {
        Timber.i("get%sListFromDatabase()", toolbarTitle);
        switch (apiParts) {
            case "Heritage_List_Page.json":
                new RetrieveHeritageData(CommonListActivity.this).execute();
                break;
            case "Public_Arts_List_Page.json":
                new RetrievePublicArtsData(CommonListActivity.this).execute();
                break;
            case "Exhibition_List_Page.json":
                new RetrieveExhibitionData(CommonListActivity.this).execute();
                break;
            case "getDiningList.json":
                new DiningRowCount(CommonListActivity.this, appLanguage).execute();
                break;
        }
    }

    private void getTravelDataFromDataBase() {
        Timber.i("get%sListFromDatabase()", toolbarTitle);
        new RetrieveTravelData(CommonListActivity.this).execute();
    }

    public void getTourListFromDatabase() {
        Timber.i("get%sListFromDatabase()", toolbarTitle);
        new RetrieveTourData(CommonListActivity.this, 1).execute();
    }

    public void getFacilityListFromDataBase() {
        Timber.i("get%sListFromDatabase()", toolbarTitle);
        new RetrieveFacilityListData(CommonListActivity.this).execute();
    }


    public void getSpecialEventFromDatabase() {
        Timber.i("get%sListFromDatabase()", toolbarTitle);
        new RetrieveTourData(CommonListActivity.this, 0).execute();
    }


    public static class FacilityRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonListActivity> activityReference;
        String language;

        FacilityRowCount(CommonListActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getFacilitiesListTableDao().getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckFacilityDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertFacilityDataToDataBase(activityReference.get(), activityReference.get().facilityListTable,
                        language).execute();
            }
        }
    }

    public static class InsertFacilityDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<CommonListActivity> activityReference;
        private FacilityListTable facilityListTable;
        String language;

        InsertFacilityDataToDataBase(CommonListActivity context, FacilityListTable facilityListTable,
                                     String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.facilityListTable = facilityListTable;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    facilityListTable = new FacilityListTable(activityReference.get().models.get(i).getId(),
                            activityReference.get().models.get(i).getSortId(),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImages().get(0),
                            language);
                    activityReference.get().qmDatabase.getFacilitiesListTableDao().insertData(facilityListTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public static class CheckFacilityDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private FacilityListTable facilityListTable;
        String language;

        CheckFacilityDBRowExist(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getFacilitiesListTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateFacilityTable(activityReference.get(), language).execute();
                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        facilityListTable = new FacilityListTable(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImages().get(0),
                                language);
                        activityReference.get().qmDatabase.getFacilitiesListTableDao().insertData(facilityListTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateFacilityTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;

        UpdateFacilityTable(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getFacilitiesListTableDao().updateFacilityList(
                    activityReference.get().models.get(0).getSortId(),
                    activityReference.get().models.get(0).getName(),
                    activityReference.get().models.get(0).getImages().get(0),
                    activityReference.get().models.get(0).getId(),
                    language
            );
            return null;
        }
    }

    public static class RetrieveFacilityListData extends AsyncTask<Void, Void, List<FacilityListTable>> {
        private WeakReference<CommonListActivity> activityReference;

        RetrieveFacilityListData(CommonListActivity context) {
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<FacilityListTable> doInBackground(Void... voids) {
            Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getFacilitiesListTableDao()
                    .getAllData(activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<FacilityListTable> facilityListTables) {
            CommonListModel commonListModel;
            activityReference.get().models.clear();
            if (facilityListTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        facilityListTables.size());
                for (int i = 0; i < facilityListTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            facilityListTables.get(i).getFacilityNid());
                    commonListModel = new CommonListModel(
                            facilityListTables.get(i).getFacilityTitle(),
                            facilityListTables.get(i).getSortId(),
                            facilityListTables.get(i).getFacilityNid(),
                            facilityListTables.get(i).getFacilityImage());
                    activityReference.get().models.add(i, commonListModel);
                }
                Collections.sort(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class TourRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonListActivity> activityReference;
        String language;
        int isTour;

        TourRowCount(CommonListActivity context, String apiLanguage, int isTour) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.isTour = isTour;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckTourDBRowExist(activityReference.get(), language, isTour).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertTourDataToDataBase(activityReference.get(), activityReference.get().tourListTable,
                        language, isTour).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getTourListTaleDao().getNumberOfRows(language);
        }
    }

    public static class CheckTourDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private TourListTable tourListTable;
        String language;
        int isTour;

        CheckTourDBRowExist(CommonListActivity context, String apiLanguage, int isTour) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.isTour = isTour;
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
            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getTourListTaleDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateTourTable(activityReference.get(), language, i).execute();
                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        tourListTable = new TourListTable(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getEventDay(),
                                activityReference.get().models.get(i).getEventDate(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getDescription(),
                                isTour, language);
                        activityReference.get().qmDatabase.getTourListTaleDao().insert(tourListTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateTourTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int i;

        UpdateTourTable(CommonListActivity context, String apiLanguage, int i) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.i = i;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getTourListTaleDao().updateTourList(
                    activityReference.get().models.get(i).getEventDay(),
                    activityReference.get().models.get(i).getEventDate(),
                    activityReference.get().models.get(i).getName(),
                    activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                    activityReference.get().models.get(i).getSortId(),
                    activityReference.get().models.get(i).getDescription(),
                    activityReference.get().models.get(i).getId(),
                    language
            );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertTourDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private TourListTable tourListTable;
        String language;
        int isTour;

        InsertTourDataToDataBase(CommonListActivity context, TourListTable tourListTable,
                                 String apiLanguage,
                                 int isTour) {
            activityReference = new WeakReference<>(context);
            this.tourListTable = tourListTable;
            this.language = apiLanguage;
            this.isTour = isTour;
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
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    tourListTable = new TourListTable(activityReference.get().models.get(i).getId(),
                            activityReference.get().models.get(i).getEventDay(),
                            activityReference.get().models.get(i).getEventDate(),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                            activityReference.get().models.get(i).getSortId(),
                            activityReference.get().models.get(i).getDescription(),
                            isTour, language);
                    activityReference.get().qmDatabase.getTourListTaleDao().insert(tourListTable);
                }
            }
            return true;
        }
    }

    public static class RetrieveTourData extends AsyncTask<Void, Void, List<TourListTable>> {
        private WeakReference<CommonListActivity> activityReference;
        int isTour;

        RetrieveTourData(CommonListActivity context, int isTour) {
            activityReference = new WeakReference<>(context);
            this.isTour = isTour;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TourListTable> tourListTables) {
            CommonListModel commonListModel;
            activityReference.get().models.clear();
            if (tourListTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        tourListTables.size());
                for (int i = 0; i < tourListTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            tourListTables.get(i).getTourNid());
                    if (isTour == 1) {
                        commonListModel = new CommonListModel(
                                tourListTables.get(i).getTourNid(),
                                tourListTables.get(i).getTourDay(),
                                tourListTables.get(i).getTourEventDate(),
                                tourListTables.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTables.get(i).getTourImages()),
                                true);
                    } else {
                        commonListModel = new CommonListModel(
                                tourListTables.get(i).getTourNid(),
                                tourListTables.get(i).getTourDay(),
                                tourListTables.get(i).getTourEventDate(),
                                tourListTables.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTables.get(i).getTourImages()),
                                false);
                    }
                    activityReference.get().models.add(i, commonListModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TourListTable> doInBackground(Void... voids) {
            Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage);
            if (isTour == 1)
                return activityReference.get().qmDatabase.getTourListTaleDao()
                        .getTourList(1, activityReference.get().appLanguage);
            else
                return activityReference.get().qmDatabase.getTourListTaleDao()
                        .getTourList(0, activityReference.get().appLanguage);
        }
    }

    public static class TravelRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonListActivity> activityReference;
        String language;

        TravelRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().travelTableRowCount = integer;
            if (activityReference.get().travelTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckTravelDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertTravelDataToDataBase(activityReference.get(), activityReference.get().travelDetailsTable,
                        language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getTravelDetailsTableDao().getNumberOfRows(language);
        }
    }

    public static class CheckTravelDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private TravelDetailsTable travelDetailsTable;
        String language;

        CheckTravelDBRowExist(CommonListActivity context, String apiLanguage) {
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

            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getTravelDetailsTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateTravelTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        travelDetailsTable = new TravelDetailsTable(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getDescription(),
                                activityReference.get().models.get(i).getEmail(),
                                activityReference.get().models.get(i).getContactNumber(),
                                activityReference.get().models.get(i).getPromotionalCode(),
                                activityReference.get().models.get(i).getClaimOffer(),
                                language);
                        activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateTravelTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdateTravelTable(CommonListActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getTravelDetailsTableDao().updateTravelDetails(
                    activityReference.get().models.get(position).getName(),
                    activityReference.get().models.get(position).getImage(),
                    activityReference.get().models.get(position).getDescription(),
                    activityReference.get().models.get(position).getPromotionalCode(),
                    activityReference.get().models.get(position).getContactNumber(),
                    activityReference.get().models.get(position).getEmail(),
                    activityReference.get().models.get(position).getId(),
                    language
            );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertTravelDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private TravelDetailsTable travelDetailsTable;
        String language;

        InsertTravelDataToDataBase(CommonListActivity context, TravelDetailsTable travelDetailsTable,
                                   String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.travelDetailsTable = travelDetailsTable;
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
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    travelDetailsTable = new TravelDetailsTable(
                            activityReference.get().models.get(i).getId(),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImage(),
                            activityReference.get().models.get(i).getDescription(),
                            activityReference.get().models.get(i).getEmail(),
                            activityReference.get().models.get(i).getContactNumber(),
                            activityReference.get().models.get(i).getPromotionalCode(),
                            activityReference.get().models.get(i).getClaimOffer(),
                            language);
                    activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTable);
                }
            }
            return true;
        }
    }

    public static class RetrieveTravelData extends AsyncTask<Void, Void, List<TravelDetailsTable>> {
        private WeakReference<CommonListActivity> activityReference;

        RetrieveTravelData(CommonListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TravelDetailsTable> travelDetailsTableList) {
            activityReference.get().models.clear();
            if (travelDetailsTableList.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        travelDetailsTableList.size());
                for (int i = 0; i < travelDetailsTableList.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            travelDetailsTableList.get(i).getContent_ID());
                    CommonListModel commonListModel = new CommonListModel(travelDetailsTableList.get(i).getTravel_name(),
                            travelDetailsTableList.get(i).getTravel_image(),
                            travelDetailsTableList.get(i).getTravel_description(),
                            travelDetailsTableList.get(i).getTravel_email(),
                            travelDetailsTableList.get(i).getContact_number(),
                            travelDetailsTableList.get(i).getPromotional_code(),
                            travelDetailsTableList.get(i).getClaim_offer(),
                            travelDetailsTableList.get(i).getContent_ID(),
                            true);
                    activityReference.get().models.add(i, commonListModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TravelDetailsTable> doInBackground(Void... voids) {
            Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getTravelDetailsTableDao()
                    .getAllData(activityReference.get().appLanguage);
        }
    }

    public static class DiningRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonListActivity> activityReference;
        String language;
        String museumID;

        DiningRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().diningTableRowCount = integer;
            if (activityReference.get().diningTableRowCount > 0) {
                if (activityReference.get().models.size() > 0) {
                    Timber.i("Count: %d", integer);
                    new CheckDiningDBRowExist(activityReference.get(), language).execute();
                } else {
                    if (museumID == null) {
                        new RetrieveDiningData(activityReference.get()).execute();
                    } else {
                        new RetrieveDiningData(activityReference.get(), museumID).execute();
                    }
                }
            } else if (activityReference.get().models.size() > 0) {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertDiningDataToDataBase(activityReference.get(),
                        activityReference.get().diningTable, language).execute();
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                activityReference.get().progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRows(language);
        }
    }

    public static class CheckDiningDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private DiningTable diningTable;
        String language;

        CheckDiningDBRowExist(CommonListActivity context, String apiLanguage) {
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

            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getDiningTableDao()
                            .checkIdExist(Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateDiningTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        diningTable = new DiningTable(Long.parseLong(
                                activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getMuseumId(),
                                language);
                        activityReference.get().qmDatabase.getDiningTableDao().insert(diningTable);

                    }
                }
            }
            return null;
        }
    }

    public static class UpdateDiningTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdateDiningTable(CommonListActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getDiningTableDao().updateDiningTable(
                    activityReference.get().models.get(position).getName(),
                    activityReference.get().models.get(position).getImage(),
                    activityReference.get().models.get(position).getId(),
                    activityReference.get().models.get(position).getSortId(),
                    language
            );
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertDiningDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private DiningTable diningTable;
        String language;

        InsertDiningDataToDataBase(CommonListActivity context, DiningTable diningTable,
                                   String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.diningTable = diningTable;
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
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    diningTable = new DiningTable(Long.parseLong(
                            activityReference.get().models.get(i).getId()),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImage(),
                            activityReference.get().models.get(i).getSortId(),
                            activityReference.get().models.get(i).getMuseumId(),
                            language);
                    activityReference.get().qmDatabase.getDiningTableDao().insert(diningTable);
                }


            }
            return true;
        }
    }

    public static class RetrieveDiningData extends AsyncTask<Void, Void, List<DiningTable>> {
        private WeakReference<CommonListActivity> activityReference;
        String museumID;

        RetrieveDiningData(CommonListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveDiningData(CommonListActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTable> diningTableList) {
            if (diningTableList.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        diningTableList.size());
                activityReference.get().models.clear();
                for (int i = 0; i < diningTableList.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            diningTableList.get(i).getDining_id());
                    CommonListModel commonListModel = new CommonListModel(
                            diningTableList.get(i).getDining_name(),
                            diningTableList.get(i).getDining_sort_id(),
                            String.valueOf(diningTableList.get(i).getDining_id()),
                            diningTableList.get(i).getDining_image());
                    activityReference.get().models.add(i, commonListModel);

                }
                Collections.sort(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                Timber.i("Have no data in database");
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<DiningTable> doInBackground(Void... voids) {
            if (museumID != null) {
                Timber.i("get%sData(language :%s) for id: %s", activityReference.get().toolbarTitle,
                        activityReference.get().appLanguage, museumID);
                return activityReference.get().qmDatabase.getDiningTableDao()
                        .getDiningDetailsWithMuseumId(Integer.parseInt(museumID), activityReference.get().appLanguage);
            } else {
                Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                        activityReference.get().appLanguage);
                return activityReference.get().qmDatabase.getDiningTableDao()
                        .getAllData(activityReference.get().appLanguage);
            }
        }
    }

    public static class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonListActivity> activityReference;
        String language;

        PublicArtsRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().publicArtsTableRowCount = integer;
            if (activityReference.get().publicArtsTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckPublicArtsDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertPublicArtsDataToDataBase(activityReference.get(),
                        activityReference.get().publicArtsTable, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRows(language);

        }
    }

    public static class CheckPublicArtsDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private PublicArtsTable publicArtsTable;
        String language;

        CheckPublicArtsDBRowExist(CommonListActivity context, String apiLanguage) {
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

            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdatePublicArtsTable(activityReference.get(), language, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        publicArtsTable = new PublicArtsTable(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getLongitude(),
                                activityReference.get().models.get(i).getLatitude(),
                                activityReference.get().models.get(i).getSortId(),
                                language);
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTable);

                    }
                }
            }
            return null;
        }
    }

    public static class UpdatePublicArtsTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdatePublicArtsTable(CommonListActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArts(
                    activityReference.get().models.get(position).getName(),
                    activityReference.get().models.get(position).getLatitude(),
                    activityReference.get().models.get(position).getLongitude(),
                    activityReference.get().models.get(position).getId(),
                    language
            );

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertPublicArtsDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private PublicArtsTable publicArtsTable;
        String language;

        InsertPublicArtsDataToDataBase(CommonListActivity context, PublicArtsTable publicArtsTable,
                                       String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.publicArtsTable = publicArtsTable;
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
            Timber.i("insertData %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("insertData %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    publicArtsTable = new PublicArtsTable(
                            Long.parseLong(activityReference.get().models.get(i).getId()),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImage(),
                            activityReference.get().models.get(i).getLongitude(),
                            activityReference.get().models.get(i).getLatitude(),
                            activityReference.get().models.get(i).getSortId(),
                            language);
                    activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTable);
                }
            }
            return true;
        }
    }

    public static class RetrievePublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTable>> {
        private WeakReference<CommonListActivity> activityReference;

        RetrievePublicArtsData(CommonListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTable> publicArtsTables) {
            activityReference.get().models.clear();
            if (publicArtsTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        publicArtsTables.size());
                for (int i = 0; i < publicArtsTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            publicArtsTables.get(i).getPublic_arts_id());
                    CommonListModel commonListModel = new CommonListModel(
                            publicArtsTables.get(i).getPublic_arts_name(),
                            publicArtsTables.get(i).getSort_id(),
                            String.valueOf(publicArtsTables.get(i).getPublic_arts_id()),
                            publicArtsTables.get(i).getPublic_arts_image(),
                            publicArtsTables.get(i).getLatitude(),
                            publicArtsTables.get(i).getLongitude());
                    activityReference.get().models.add(i, commonListModel);

                }
                Collections.sort(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTable> doInBackground(Void... voids) {
            Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getPublicArtsTableDao()
                    .getAllData(activityReference.get().appLanguage);
        }
    }

    public static class HeritageRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonListActivity> activityReference;
        String language;

        HeritageRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getHeritageListTableDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().heritageTableRowCount = integer;
            if (activityReference.get().heritageTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckHeritageDBRowExist(activityReference.get(), language).execute();

            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertHeritageDataToDataBase(activityReference.get(),
                        activityReference.get().heritageListTable, language).execute();
            }
        }
    }

    public static class InsertHeritageDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private HeritageListTable heritageListTable;
        String language;

        InsertHeritageDataToDataBase(CommonListActivity context, HeritageListTable heritageListTable,
                                     String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.heritageListTable = heritageListTable;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    heritageListTable = new HeritageListTable(
                            Long.parseLong(activityReference.get().models.get(i).getId()),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImage(),
                            null, null, null, null,
                            null,
                            activityReference.get().models.get(i).getSortId(),
                            language);
                    activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class CheckHeritageDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private HeritageListTable heritageListTable;
        String language;

        CheckHeritageDBRowExist(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateHeritagePageTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        heritageListTable = new HeritageListTable(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getSortId(),
                                null, null, null,
                                null, null, language);
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTable);

                    }
                }
            }
            return null;
        }
    }

    public static class UpdateHeritagePageTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdateHeritagePageTable(CommonListActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageList(
                    activityReference.get().models.get(position).getName(),
                    activityReference.get().models.get(position).getSortId(),
                    activityReference.get().models.get(position).getImage(),
                    activityReference.get().models.get(position).getId(),
                    language
            );


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }

    public static class RetrieveHeritageData extends AsyncTask<Void, Void, List<HeritageListTable>> {
        private WeakReference<CommonListActivity> activityReference;


        RetrieveHeritageData(CommonListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<HeritageListTable> doInBackground(Void... voids) {
            Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getHeritageListTableDao()
                    .getAllData(activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<HeritageListTable> heritageListTables) {
            activityReference.get().models.clear();
            if (heritageListTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        heritageListTables.size());
                for (int i = 0; i < heritageListTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            heritageListTables.get(i).getHeritage_id());
                    CommonListModel commonListModel = new CommonListModel(
                            heritageListTables.get(i).getHeritage_name(),
                            String.valueOf(heritageListTables.get(i).getHeritage_id()),
                            null, heritageListTables.get(i).getHeritage_image(),
                            null, null,
                            null, null);
                    activityReference.get().models.add(i, commonListModel);

                }
                Collections.sort(activityReference.get().models);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class ExhibitionRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonListActivity> activityReference;
        String language;

        ExhibitionRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().exhibitionTableRowCount = integer;
            if (activityReference.get().exhibitionTableRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckExhibitionDBRowExist(activityReference.get(), language).execute();

            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertExhibitionDataToDataBase(activityReference.get(),
                        activityReference.get().exhibitionListTable, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRows(language);
        }

    }

    public static class CheckExhibitionDBRowExist extends AsyncTask<Void, Void, Void> {

        private WeakReference<CommonListActivity> activityReference;
        private ExhibitionListTable exhibitionListTable;
        String language;

        CheckExhibitionDBRowExist(CommonListActivity context, String apiLanguage) {
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
            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().models.get(i).getId()), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateExhibitionTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        exhibitionListTable = new ExhibitionListTable(Long.parseLong(
                                activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getStartDate(),
                                activityReference.get().models.get(i).getEndDate(),
                                activityReference.get().models.get(i).getLocation(),
                                null,
                                null,
                                null,
                                null,
                                activityReference.get().models.get(i).getMuseumId(),
                                activityReference.get().models.get(i).getExhibitionStatus(),
                                activityReference.get().models.get(i).getDisplayDate(),
                                activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                language);
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTable);

                    }
                }
            }
            return null;
        }

    }

    public static class InsertExhibitionDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private ExhibitionListTable exhibitionListTable;
        String language;

        InsertExhibitionDataToDataBase(CommonListActivity context, ExhibitionListTable exhibitionListTable,
                                       String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.exhibitionListTable = exhibitionListTable;
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
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    exhibitionListTable = new ExhibitionListTable(Long.parseLong(
                            activityReference.get().models.get(i).getId()),
                            activityReference.get().models.get(i).getName(),
                            activityReference.get().models.get(i).getImage(),
                            activityReference.get().models.get(i).getStartDate(),
                            activityReference.get().models.get(i).getEndDate(),
                            activityReference.get().models.get(i).getLocation(),
                            null,
                            null,
                            null,
                            null,
                            activityReference.get().models.get(i).getMuseumId(),
                            activityReference.get().models.get(i).getExhibitionStatus(),
                            activityReference.get().models.get(i).getDisplayDate(),
                            activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                            language);
                    activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTable);
                }
            }
            return true;
        }
    }

    public static class UpdateExhibitionTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdateExhibitionTable(CommonListActivity context, String apiLanguage, int p) {
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
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionList(
                    activityReference.get().models.get(position).getStartDate(),
                    activityReference.get().models.get(position).getEndDate(),
                    activityReference.get().models.get(position).getLocation(),
                    activityReference.get().models.get(position).getId(),
                    activityReference.get().models.get(position).getMuseumId(),
                    language
            );
            return null;
        }
    }

    public static class RetrieveExhibitionData extends AsyncTask<Void, Void, List<ExhibitionListTable>> {
        private WeakReference<CommonListActivity> activityReference;

        String museumID;

        RetrieveExhibitionData(CommonListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveExhibitionData(CommonListActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTable> exhibitionListTables) {
            activityReference.get().models.clear();
            if (exhibitionListTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        exhibitionListTables.size());
                for (int i = 0; i < exhibitionListTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            exhibitionListTables.get(i).getExhibition_id());
                    CommonListModel commonListModel = new CommonListModel(
                            String.valueOf(exhibitionListTables.get(i).getExhibition_id()),
                            exhibitionListTables.get(i).getExhibition_name(),
                            exhibitionListTables.get(i).getExhibition_location(),
                            exhibitionListTables.get(i).getExhibition_latest_image(),
                            exhibitionListTables.get(i).getExhibition_status(),
                            exhibitionListTables.get(i).getExhibition_display_date(),
                            exhibitionListTables.get(i).getExhibition_start_date(),
                            exhibitionListTables.get(i).getExhibition_end_date(),
                            exhibitionListTables.get(i).getMuseum_id());

                    activityReference.get().models.add(i, commonListModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ExhibitionListTable> doInBackground(Void... voids) {
            if (museumID != null) {
                Timber.i("get%sData(language :%s) for id: %s", activityReference.get().toolbarTitle,
                        activityReference.get().appLanguage, museumID);
                return activityReference.get().qmDatabase.getExhibitionTableDao()
                        .getExhibitionWithMuseumId(Integer.parseInt(museumID), activityReference.get().appLanguage);
            } else {
                Timber.i("getAll%sData(language :%s)", activityReference.get().toolbarTitle,
                        activityReference.get().appLanguage);
                return activityReference.get().qmDatabase.getExhibitionTableDao()
                        .getAllData(activityReference.get().appLanguage);
            }
        }
    }

    public void getMuseumCollectionListFromDatabase() {
        new RetrieveMuseumCollectionListData(CommonListActivity.this, id).execute();
    }

    public static class MuseumCollectionRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonListActivity> activityReference;
        String language;

        MuseumCollectionRowCount(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOf%sRows(language :%s)", activityReference.get().toolbarTitle, language);
            return activityReference.get().qmDatabase.getMuseumCollectionListDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().museumCollectionListRowCount = integer;
            if (activityReference.get().museumCollectionListRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckMuseumCollectionDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("%s Table have no data", activityReference.get().toolbarTitle);
                new InsertMuseumCollectionListDataToDataBase(activityReference.get(),
                        activityReference.get().museumCollectionListTable, language).execute();

            }
        }
    }

    public static class CheckMuseumCollectionDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        private MuseumCollectionListTable museumCollectionListTable;
        String language;

        CheckMuseumCollectionDBRowExist(CommonListActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    int n = activityReference.get().qmDatabase.getMuseumCollectionListDao().checkNameExist(
                            activityReference.get().models.get(i).getName(), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language :%s) for id: %s", language,
                                activityReference.get().models.get(i).getId());
                        new UpdateMuseumCollectionListTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                    } else {
                        Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                                language, activityReference.get().models.get(i).getId());
                        museumCollectionListTable =
                                new MuseumCollectionListTable(
                                        activityReference.get().models.get(i).getName(),
                                        activityReference.get().models.get(i).getImage(),
                                        activityReference.get().models.get(i).getMuseumReference(),
                                        activityReference.get().models.get(i).getDescription(),
                                        language);
                        activityReference.get().qmDatabase.getMuseumCollectionListDao().insertTable(museumCollectionListTable);
                    }
                }
            }
            return null;
        }
    }

    public static class InsertMuseumCollectionListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonListActivity> activityReference;
        private MuseumCollectionListTable museumCollectionListTable;
        String language;

        InsertMuseumCollectionListDataToDataBase(CommonListActivity context,
                                                 MuseumCollectionListTable museumCollectionListTable,
                                                 String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.museumCollectionListTable = museumCollectionListTable;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Timber.i("Insert %s Table(language :%s) with size: %d", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.size());
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                for (int i = 0; i < activityReference.get().models.size(); i++) {
                    Timber.i("Inserting %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                            language, activityReference.get().models.get(i).getId());
                    museumCollectionListTable =
                            new MuseumCollectionListTable(activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getMuseumReference(),
                                    activityReference.get().models.get(i).getDescription(),
                                    language);
                    activityReference.get().qmDatabase.getMuseumCollectionListDao().insertTable(museumCollectionListTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    public static class UpdateMuseumCollectionListTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonListActivity> activityReference;
        String language;
        int position;

        UpdateMuseumCollectionListTable(CommonListActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating %s Table(language :%s) with id: %s", activityReference.get().toolbarTitle,
                    language, activityReference.get().models.get(0).getId());
            activityReference.get().qmDatabase.getMuseumCollectionListDao().updateMuseumListTable(
                    activityReference.get().models.get(position).getImage(),
                    activityReference.get().models.get(position).getMuseumReference(),
                    activityReference.get().models.get(position).getDescription(),
                    activityReference.get().models.get(position).getName(),
                    language);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }

    public static class RetrieveMuseumCollectionListData extends AsyncTask<Void, Void, List<MuseumCollectionListTable>> {
        private WeakReference<CommonListActivity> activityReference;
        String museumReference;

        RetrieveMuseumCollectionListData(CommonListActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumReference = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionListTable> doInBackground(Void... voids) {
            Timber.i("get%sListData(language :%s) for id: %s", activityReference.get().toolbarTitle,
                    activityReference.get().appLanguage, museumReference);
            return activityReference.get().qmDatabase.getMuseumCollectionListDao()
                    .getDataFromTableWithReference(museumReference, activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionListTable> museumCollectionListTables) {
            activityReference.get().models.clear();
            if (museumCollectionListTables.size() > 0) {
                Timber.i("Set %s list from database with size: %d", activityReference.get().toolbarTitle,
                        museumCollectionListTables.size());
                for (int i = 0; i < museumCollectionListTables.size(); i++) {
                    Timber.i("Setting %s list from database with id: %s", activityReference.get().toolbarTitle,
                            museumCollectionListTables.get(i).getMuseum_id());
                    CommonListModel commonListModel = new CommonListModel(museumCollectionListTables.get(i).getName(),
                            museumCollectionListTables.get(i).getImage(),
                            museumCollectionListTables.get(i).getMuseum_id());
                    activityReference.get().models.add(i, commonListModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

}

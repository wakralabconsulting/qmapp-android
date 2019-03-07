package com.qatarmuseums.qatarmuseumsapp.commonpage;

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
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.DiningTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.ExhibitionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.HeritageListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.PublicArtsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TourListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTableArabic;
import com.qatarmuseums.qatarmuseumsapp.commonpagedatabase.TravelDetailsTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.detailspage.DiningActivity;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableArabic;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionListTableEnglish;
import com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails.CollectionDetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.toursecondarylist.TourSecondaryListActivity;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivity extends AppCompatActivity {

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
    TravelDetailsTableEnglish travelDetailsTableEnglish;
    TravelDetailsTableArabic travelDetailsTableArabic;
    TourListTableEnglish tourListTableEnglish;
    TourListTableArabic tourListTableArabic;
    RelativeLayout noResultFoundLayout;
    int publicArtsTableRowCount;
    int heritageTableRowCount, museumCollectionListRowCount;
    int diningTableRowCount;
    String appLanguage;
    String pageName = null;
    String id;
    private APIInterface apiService;
    private Call<ArrayList<CommonModel>> call;
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
        setContentView(R.layout.activity_common);
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
            if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text)))
                navigationIntent = new Intent(CommonActivity.this,
                        DiningActivity.class);
            else if (toolbarTitle.equals(getString(R.string.museum_collection_text)))
                navigationIntent = new Intent(CommonActivity.this,
                        CollectionDetailsActivity.class);
            else if (toolbarTitle.equals(getString(R.string.museum_tours)) ||
                    toolbarTitle.equals(getString(R.string.museum_discussion)))
                navigationIntent = new Intent(CommonActivity.this,
                        TourSecondaryListActivity.class);
            else
                navigationIntent = new Intent(CommonActivity.this, DetailsActivity.class);
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

    private void getTravelDataFromDataBase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishTravelData(CommonActivity.this).execute();
        } else {
            new RetrieveArabicTravelData(CommonActivity.this).execute();
        }
    }

    public void getData() {
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
            if (util.isNetworkAvailable(CommonActivity.this))
                getCommonListAPIDataFromAPI("getDiningList.json");
            else
                getCommonListDataFromDatabase("getDiningList.json");
        } else if (toolbarTitle.equals(getString(R.string.museum_collection_text))) {
            if (util.isNetworkAvailable(CommonActivity.this))
                getMuseumCollectionListFromAPI();
            else
                getMuseumCollectionListFromDatabase();
        } else if (toolbarTitle.equals(getString(R.string.museum_tours))) {
            if (util.isNetworkAvailable(CommonActivity.this))
                getTourListFromAPI();
            else
                getTourListFromDatabase();
        } else if (toolbarTitle.equals(getString(R.string.museum_travel))) {
            if (util.isNetworkAvailable(CommonActivity.this))
                getTravelDataFromAPI();
            else
                getTravelDataFromDataBase();
        } else if (toolbarTitle.equals(getString(R.string.museum_discussion))) {
            if (util.isNetworkAvailable(CommonActivity.this))
                getSpecialEventFromAPI();
            else
                getSpecialEventFromDatabase();

        }
    }

    public void getTourListFromDatabase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishTourData(CommonActivity.this, 1).execute();
        } else {
            new RetrieveArabicTourData(CommonActivity.this, 1).execute();
        }
    }

    public void getSpecialEventFromDatabase() {
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveEnglishTourData(CommonActivity.this, 0).execute();
        } else {
            new RetrieveArabicTourData(CommonActivity.this, 0).execute();
        }
    }

    private void getTourListFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getTourList(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < response.body().size(); i++)
                            models.add(new CommonModel(
                                    response.body().get(i).getId(),
                                    response.body().get(i).getEventDay(),
                                    response.body().get(i).getEventDate(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getImages(),
                                    true));
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TourRowCount(CommonActivity.this, appLanguage, 1).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getSpecialEventFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getSpecialEvents(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < response.body().size(); i++)
                            models.add(new CommonModel(
                                    response.body().get(i).getId(),
                                    response.body().get(i).getEventDay(),
                                    response.body().get(i).getEventDate(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getImages(),
                                    false));
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TourRowCount(CommonActivity.this, appLanguage, 0).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getTravelDataFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);
        call = apiService.getTravelData(appLanguage);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < response.body().size(); i++)
                            models.add(new CommonModel(response.body().get(i).getName(),
                                    response.body().get(i).getImage(),
                                    response.body().get(i).getDescription(),
                                    response.body().get(i).getEmail(),
                                    response.body().get(i).getContactNumber(),
                                    response.body().get(i).getPromotionalCode(),
                                    response.body().get(i).getClaimOffer(),
                                    response.body().get(i).getId(),
                                    true));
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new TravelRowCount(CommonActivity.this, appLanguage).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void getCommonListDataFromDatabase(String apiParts) {

        if (apiParts.equals("Heritage_List_Page.json")) {
            if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                new RetrieveHeritageDataEnglish(CommonActivity.this).execute();
            } else {
                new RetrieveHeritageDataArabic(CommonActivity.this).execute();
            }

        } else if (apiParts.equals("Public_Arts_List_Page.json")) {
            if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                new RetrieveEnglishPublicArtsData(CommonActivity.this).execute();
            } else {
                new RetrieveArabicPublicArtsData(CommonActivity.this).execute();
            }

        } else if (apiParts.equals("Exhibition_List_Page.json")) {
            if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                new RetrieveExhibitionDataEnglish(CommonActivity.this).execute();
            } else {
                new RetrieveExhibitionDataArabic(CommonActivity.this).execute();
            }
        } else if (apiParts.equals("getDiningList.json")) {
            new DiningRowCount(CommonActivity.this, appLanguage).execute();
        }
    }

    private void getMuseumCollectionListFromAPI() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = APIClient.getClient().create(APIInterface.class);

        call = apiService.getCollectionList(appLanguage, id);
        call.enqueue(new Callback<ArrayList<CommonModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CommonModel>> call, Response<ArrayList<CommonModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        models.addAll(response.body());
                        removeHtmlTags(models);
                        mAdapter.notifyDataSetChanged();
                        new MuseumCollectionRowCount(CommonActivity.this, appLanguage).execute();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void removeHtmlTags(ArrayList<CommonModel> models) {
        if (models.size() > 0) {
            for (int i = 0; i < models.size(); i++) {
                models.get(i).setName(util.html2string(models.get(i).getName()));
                models.get(i).setDescription(util.html2string(models.get(i).getDescription()));
                models.get(i).setStartDate(util.html2string(models.get(i).getStartDate()));
                models.get(i).setEndDate(util.html2string(models.get(i).getEndDate()));
            }
        }
    }

    private void getCommonListAPIDataFromAPI(String name) {
        progressBar.setVisibility(View.VISIBLE);
        pageName = name;
        apiService = APIClient.getClient().create(APIInterface.class);
        if (id != null)
            call = apiService.getCommonPageListWithID(appLanguage, pageName, id);
        else
            call = apiService.getCommonPageList(appLanguage, pageName);
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
                            new HeritageRowCount(CommonActivity.this, appLanguage).execute();
                        } else if (pageName.equals("Public_Arts_List_Page.json")) {
                            new PublicArtsRowCount(CommonActivity.this, appLanguage).execute();
                        } else if (pageName.equals("Exhibition_List_Page.json")) {
                            new ExhibitionRowCount(CommonActivity.this, appLanguage).execute();
                        } else if (pageName.equals("getDiningList.json")) {
                            new DiningRowCount(CommonActivity.this, appLanguage).execute();
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<CommonModel>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    public static class TourRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;
        int isTour;

        TourRowCount(CommonActivity context, String apiLanguage, int isTour) {
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
                new CheckTourDBRowExist(activityReference.get(), language, isTour).execute();
            } else {
                new InsertTourDataToDataBase(activityReference.get(), activityReference.get().tourListTableEnglish,
                        activityReference.get().tourListTableArabic, language, isTour).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getTourListTaleDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getTourListTaleDao().getNumberOfRowsArabic();
        }
    }

    public static class CheckTourDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private TourListTableEnglish tourListTableEnglish;
        private TourListTableArabic tourListTableArabic;
        String language;
        int isTour;

        CheckTourDBRowExist(CommonActivity context, String apiLanguage, int isTour) {
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
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTourListTaleDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            new UpdateTourTable(activityReference.get(), language, i).execute();
                        } else {
                            tourListTableEnglish = new TourListTableEnglish(activityReference.get().models.get(i).getId(),
                                    activityReference.get().models.get(i).getEventDay(),
                                    activityReference.get().models.get(i).getEventDate(),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                    activityReference.get().models.get(i).getSortId(),
                                    activityReference.get().models.get(i).getDescription(), isTour);
                            activityReference.get().qmDatabase.getTourListTaleDao().insertEnglish(tourListTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTourListTaleDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            new UpdateTourTable(activityReference.get(), language, i).execute();
                        } else {
                            tourListTableArabic = new TourListTableArabic(activityReference.get().models.get(i).getId(),
                                    activityReference.get().models.get(i).getEventDay(),
                                    activityReference.get().models.get(i).getEventDate(),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                    activityReference.get().models.get(i).getSortId(),
                                    activityReference.get().models.get(i).getDescription(), isTour);
                            activityReference.get().qmDatabase.getTourListTaleDao().insertArabic(tourListTableArabic);
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateTourTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int i;

        UpdateTourTable(CommonActivity context, String apiLanguage, int i) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            this.i = i;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                activityReference.get().qmDatabase.getTourListTaleDao().updateTourListEnglish(
                        activityReference.get().models.get(i).getEventDay(),
                        activityReference.get().models.get(i).getEventDate(),
                        activityReference.get().models.get(i).getName(),
                        activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                        activityReference.get().models.get(i).getSortId(),
                        activityReference.get().models.get(i).getDescription(),
                        activityReference.get().models.get(i).getId()
                );

            } else {
                activityReference.get().qmDatabase.getTourListTaleDao().updateTourListArabic(
                        activityReference.get().models.get(i).getEventDay(),
                        activityReference.get().models.get(i).getEventDate(),
                        activityReference.get().models.get(i).getName(),
                        activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                        activityReference.get().models.get(i).getSortId(),
                        activityReference.get().models.get(i).getDescription(),
                        activityReference.get().models.get(i).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertTourDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private TourListTableEnglish tourListTableEnglish;
        private TourListTableArabic tourListTableArabic;
        String language;
        int isTour;

        InsertTourDataToDataBase(CommonActivity context, TourListTableEnglish tourListTableEnglish,
                                 TourListTableArabic tourListTableArabic, String apiLanguage,
                                 int isTour) {
            activityReference = new WeakReference<>(context);
            this.tourListTableEnglish = tourListTableEnglish;
            this.tourListTableArabic = tourListTableArabic;
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        tourListTableEnglish = new TourListTableEnglish(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getEventDay(),
                                activityReference.get().models.get(i).getEventDate(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getDescription(), isTour);
                        activityReference.get().qmDatabase.getTourListTaleDao().insertEnglish(tourListTableEnglish);
                    }
                }
            } else {
                if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        tourListTableArabic = new TourListTableArabic(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getEventDay(),
                                activityReference.get().models.get(i).getEventDate(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().convertor.fromArrayList(activityReference.get().models.get(i).getImages()),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getDescription(), isTour);
                        activityReference.get().qmDatabase.getTourListTaleDao().insertArabic(tourListTableArabic);
                    }
                }
            }
            return true;
        }
    }

    public static class RetrieveEnglishTourData extends AsyncTask<Void, Void, List<TourListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        int isTour;

        RetrieveEnglishTourData(CommonActivity context, int isTour) {
            activityReference = new WeakReference<>(context);
            this.isTour = isTour;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TourListTableEnglish> tourListTableEnglishes) {
            CommonModel commonModel;
            activityReference.get().models.clear();
            if (tourListTableEnglishes.size() > 0) {
                for (int i = 0; i < tourListTableEnglishes.size(); i++) {
                    if (isTour == 1)
                        commonModel = new CommonModel(
                                tourListTableEnglishes.get(i).getTourNid(),
                                tourListTableEnglishes.get(i).getTourDay(),
                                tourListTableEnglishes.get(i).getTourEventDate(),
                                tourListTableEnglishes.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTableEnglishes.get(i).getTourImages()),
                                true);
                    else
                        commonModel = new CommonModel(
                                tourListTableEnglishes.get(i).getTourNid(),
                                tourListTableEnglishes.get(i).getTourDay(),
                                tourListTableEnglishes.get(i).getTourEventDate(),
                                tourListTableEnglishes.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTableEnglishes.get(i).getTourImages()),
                                false);
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TourListTableEnglish> doInBackground(Void... voids) {
            if (isTour == 1)
                return activityReference.get().qmDatabase.getTourListTaleDao().getTourListEnglish(1);
            else
                return activityReference.get().qmDatabase.getTourListTaleDao().getTourListEnglish(0);
        }
    }

    public static class RetrieveArabicTourData extends AsyncTask<Void, Void, List<TourListTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        int isTour;

        RetrieveArabicTourData(CommonActivity context, int isTour) {
            activityReference = new WeakReference<>(context);
            this.isTour = isTour;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TourListTableArabic> tourListTableArabics) {
            CommonModel commonModel;
            activityReference.get().models.clear();
            if (tourListTableArabics.size() > 0) {
                for (int i = 0; i < tourListTableArabics.size(); i++) {
                    if (isTour == 1)
                        commonModel = new CommonModel(
                                tourListTableArabics.get(i).getTourNid(),
                                tourListTableArabics.get(i).getTourDay(),
                                tourListTableArabics.get(i).getTourEventDate(),
                                tourListTableArabics.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTableArabics.get(i).getTourImages()),
                                true);
                    else
                        commonModel = new CommonModel(
                                tourListTableArabics.get(i).getTourNid(),
                                tourListTableArabics.get(i).getTourDay(),
                                tourListTableArabics.get(i).getTourEventDate(),
                                tourListTableArabics.get(i).getTourSubtitle(),
                                activityReference.get().convertor.fromString(tourListTableArabics.get(i).getTourImages()),
                                false);
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TourListTableArabic> doInBackground(Void... voids) {
            if (isTour == 1)
                return activityReference.get().qmDatabase.getTourListTaleDao().getTourListArabic(1);
            else
                return activityReference.get().qmDatabase.getTourListTaleDao().getTourListArabic(0);
        }
    }

    public static class TravelRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;

        TravelRowCount(CommonActivity context, String apiLanguage) {
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
                new CheckTravelDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertTravelDataToDataBase(activityReference.get(), activityReference.get().travelDetailsTableEnglish,
                        activityReference.get().travelDetailsTableArabic, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getTravelDetailsTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getTravelDetailsTableDao().getNumberOfRowsArabic();

        }
    }

    public static class CheckTravelDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private TravelDetailsTableEnglish travelDetailsTableEnglish;
        private TravelDetailsTableArabic travelDetailsTableArabic;
        String language;

        CheckTravelDBRowExist(CommonActivity context, String apiLanguage) {
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
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTravelDetailsTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            new UpdateTravelTable(activityReference.get(), language, i).execute();

                        } else {
                            travelDetailsTableEnglish = new TravelDetailsTableEnglish(activityReference.get().models.get(i).getId(),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getDescription(),
                                    activityReference.get().models.get(i).getEmail(),
                                    activityReference.get().models.get(i).getContactNumber(),
                                    activityReference.get().models.get(i).getPromotionalCode(),
                                    activityReference.get().models.get(i).getClaimOffer());
                            activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getTravelDetailsTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateTravelTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            travelDetailsTableArabic = new TravelDetailsTableArabic(activityReference.get().models.get(i).getId(),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getDescription(),
                                    activityReference.get().models.get(i).getEmail(),
                                    activityReference.get().models.get(i).getContactNumber(),
                                    activityReference.get().models.get(i).getPromotionalCode(),
                                    activityReference.get().models.get(i).getClaimOffer());
                            activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTableArabic);

                        }
                    }
                }

            }
            return null;
        }
    }

    public static class UpdateTravelTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdateTravelTable(CommonActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals("en")) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getTravelDetailsTableDao().updateTraveldetailsenglish(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getDescription(),
                        activityReference.get().models.get(position).getPromotionalCode(),
                        activityReference.get().models.get(position).getContactNumber(),
                        activityReference.get().models.get(position).getEmail(),
                        activityReference.get().models.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getTravelDetailsTableDao().updateTraveldetailsarabic(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getDescription(),
                        activityReference.get().models.get(position).getPromotionalCode(),
                        activityReference.get().models.get(position).getContactNumber(),
                        activityReference.get().models.get(position).getEmail(),
                        activityReference.get().models.get(position).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertTravelDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private TravelDetailsTableEnglish travelDetailsTableEnglish;
        private TravelDetailsTableArabic travelDetailsTableArabic;
        String language;

        InsertTravelDataToDataBase(CommonActivity context, TravelDetailsTableEnglish travelDetailsTableEnglish,
                                   TravelDetailsTableArabic travelDetailsTableArabic, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.travelDetailsTableEnglish = travelDetailsTableEnglish;
            this.travelDetailsTableArabic = travelDetailsTableArabic;
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
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals("en")) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        travelDetailsTableEnglish = new TravelDetailsTableEnglish(
                                activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getDescription(),
                                activityReference.get().models.get(i).getEmail(),
                                activityReference.get().models.get(i).getContactNumber(),
                                activityReference.get().models.get(i).getPromotionalCode(),
                                activityReference.get().models.get(i).getClaimOffer());
                        activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        travelDetailsTableArabic = new TravelDetailsTableArabic(activityReference.get().models.get(i).getId(),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getDescription(),
                                activityReference.get().models.get(i).getEmail(),
                                activityReference.get().models.get(i).getContactNumber(),
                                activityReference.get().models.get(i).getPromotionalCode(),
                                activityReference.get().models.get(i).getClaimOffer());
                        activityReference.get().qmDatabase.getTravelDetailsTableDao().insert(travelDetailsTableArabic);
                    }
                }

            }
            return true;
        }
    }

    public static class RetrieveEnglishTravelData extends AsyncTask<Void, Void, List<TravelDetailsTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;

        RetrieveEnglishTravelData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TravelDetailsTableEnglish> travelDetailsTableEnglishe) {
            activityReference.get().models.clear();
            if (travelDetailsTableEnglishe.size() > 0) {
                for (int i = 0; i < travelDetailsTableEnglishe.size(); i++) {
                    CommonModel commonModel = new CommonModel(travelDetailsTableEnglishe.get(i).getTravel_name(),
                            travelDetailsTableEnglishe.get(i).getTravel_image(),
                            travelDetailsTableEnglishe.get(i).getTravel_description(),
                            travelDetailsTableEnglishe.get(i).getTravel_email(),
                            travelDetailsTableEnglishe.get(i).getContact_number(),
                            travelDetailsTableEnglishe.get(i).getPromotional_code(),
                            travelDetailsTableEnglishe.get(i).getClaim_offer(),
                            travelDetailsTableEnglishe.get(i).getContent_ID(),
                            true);
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TravelDetailsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTravelDetailsTableDao().getAllEnglish();
        }
    }

    public static class RetrieveArabicTravelData extends AsyncTask<Void, Void, List<TravelDetailsTableArabic>> {
        private WeakReference<CommonActivity> activityReference;

        RetrieveArabicTravelData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<TravelDetailsTableArabic> travelDetailsTableArabic) {
            activityReference.get().models.clear();
            if (travelDetailsTableArabic.size() > 0) {
                for (int i = 0; i < travelDetailsTableArabic.size(); i++) {
                    CommonModel commonModel = new CommonModel(travelDetailsTableArabic.get(i).getTravel_name(),
                            travelDetailsTableArabic.get(i).getTravel_image(),
                            travelDetailsTableArabic.get(i).getTravel_description(),
                            travelDetailsTableArabic.get(i).getTravel_email(),
                            travelDetailsTableArabic.get(i).getContact_number(),
                            travelDetailsTableArabic.get(i).getPromotional_code(),
                            travelDetailsTableArabic.get(i).getClaim_offer(),
                            travelDetailsTableArabic.get(i).getContent_ID(),
                            true);
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<TravelDetailsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getTravelDetailsTableDao().getAllArabic();
        }
    }

    public static class DiningRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CommonActivity> activityReference;
        String language;
        String museumID;

        DiningRowCount(CommonActivity context, String apiLanguage) {
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
                if (activityReference.get().models.size() > 0)
                    //updateEnglishTable or add row to database
                    new CheckDiningDBRowExist(activityReference.get(), language).execute();
                else {
                    if (museumID == null) {
                        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                            new RetrieveEnglishDiningData(activityReference.get()).execute();
                        } else {
                            new RetrieveArabicDiningData(activityReference.get()).execute();
                        }
                    } else {
                        if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                            new RetrieveEnglishDiningData(activityReference.get(), museumID).execute();
                        } else {
                            new RetrieveArabicDiningData(activityReference.get(), museumID).execute();
                        }
                    }
                }
            } else if (activityReference.get().models.size() > 0) {
                new InsertDiningDataToDataBase(activityReference.get(),
                        activityReference.get().diningTableEnglish,
                        activityReference.get().diningTableArabic, language).execute();
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
                activityReference.get().progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getNumberOfRowsArabic();

        }
    }

    public static class CheckDiningDBRowExist extends AsyncTask<Void, Void, Void> {
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

            if (activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            diningTableEnglish = new DiningTableEnglish(Long.parseLong(
                                    activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getSortId(),
                                    activityReference.get().models.get(i).getMuseumId());
                            activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getDiningTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateDiningTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            diningTableArabic = new DiningTableArabic(Long.parseLong(
                                    activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getSortId(),
                                    activityReference.get().models.get(i).getMuseumId());
                            activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableArabic);

                        }
                    }
                }

            }
            return null;
        }
    }

    public static class UpdateDiningTable extends AsyncTask<Void, Void, Void> {
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningEnglish(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getId(),
                        activityReference.get().models.get(position).getSortId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getDiningTableDao().updateDiningArabic(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getId(),
                        activityReference.get().models.get(position).getSortId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertDiningDataToDataBase extends AsyncTask<Void, Void, Boolean> {
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
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        diningTableEnglish = new DiningTableEnglish(Long.parseLong(
                                activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getMuseumId());
                        activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        diningTableArabic = new DiningTableArabic(Long.parseLong(
                                activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getSortId(),
                                activityReference.get().models.get(i).getMuseumId());
                        activityReference.get().qmDatabase.getDiningTableDao().insert(diningTableArabic);
                    }
                }

            }
            return true;
        }
    }

    public static class RetrieveEnglishDiningData extends AsyncTask<Void, Void, List<DiningTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        String museumID;

        RetrieveEnglishDiningData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveEnglishDiningData(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTableEnglish> diningTableEnglishList) {
            if (diningTableEnglishList.size() > 0) {
                activityReference.get().models.clear();
                for (int i = 0; i < diningTableEnglishList.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(diningTableEnglishList.get(i).getDining_id()),
                            diningTableEnglishList.get(i).getDining_name(),
                            diningTableEnglishList.get(i).getDining_image(), "", "");
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<DiningTableEnglish> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsEnglishWithMuseumId(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getAllEnglish();
        }
    }

    public static class RetrieveArabicDiningData extends AsyncTask<Void, Void, List<DiningTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        String museumID;

        RetrieveArabicDiningData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveArabicDiningData(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<DiningTableArabic> diningTableArabicList) {
            if (diningTableArabicList.size() > 0) {
                activityReference.get().models.clear();
                for (int i = 0; i < diningTableArabicList.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(diningTableArabicList.get(i).getDining_id()),
                            diningTableArabicList.get(i).getDining_name(),
                            diningTableArabicList.get(i).getDining_image(), "", "");
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
            } else {
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
            activityReference.get().progressBar.setVisibility(View.GONE);
        }

        @Override
        protected List<DiningTableArabic> doInBackground(Void... voids) {
            if (museumID != null)
                return activityReference.get().qmDatabase.getDiningTableDao().getDiningDetailsArabicWithMuseumId(Integer.parseInt(museumID));
            else
                return activityReference.get().qmDatabase.getDiningTableDao().getAllArabic();
        }
    }

    public static class PublicArtsRowCount extends AsyncTask<Void, Void, Integer> {

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
            activityReference.get().publicArtsTableRowCount = integer;
            if (activityReference.get().publicArtsTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckPublicArtsDBRowExist(activityReference.get(), language).execute();
            } else {
                new InsertPublicArtsDataToDataBase(activityReference.get(),
                        activityReference.get().publicArtsTableEnglish,
                        activityReference.get().publicArtsTableArabic, language).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getPublicArtsTableDao().getNumberOfRowsArabic();

        }
    }

    public static class CheckPublicArtsDBRowExist extends AsyncTask<Void, Void, Void> {
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

            if (activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            publicArtsTableEnglish = new PublicArtsTableEnglish(Long.parseLong(
                                    activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getLongitude(),
                                    activityReference.get().models.get(i).getLatitude());
                            activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getPublicArtsTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdatePublicArtsTable(activityReference.get(), language, i).execute();

                        } else {
                            //create row with corresponding id
                            publicArtsTableArabic = new PublicArtsTableArabic(Long.parseLong(
                                    activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getLongitude(),
                                    activityReference.get().models.get(i).getLatitude());
                            activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableArabic);

                        }
                    }
                }

            }
            return null;
        }
    }

    public static class UpdatePublicArtsTable extends AsyncTask<Void, Void, Void> {
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsEnglish(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getLatitude(),
                        activityReference.get().models.get(position).getLongitude(),
                        activityReference.get().models.get(position).getId()
                );

            } else {
                // updateArabicTable table with arabic name
                activityReference.get().qmDatabase.getPublicArtsTableDao().updatePublicArtsArabic(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getLatitude(),
                        activityReference.get().models.get(position).getLongitude(),
                        activityReference.get().models.get(position).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public static class InsertPublicArtsDataToDataBase extends AsyncTask<Void, Void, Boolean> {
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
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        publicArtsTableEnglish = new PublicArtsTableEnglish(Long.parseLong(
                                activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getLongitude(),
                                activityReference.get().models.get(i).getLatitude());
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        publicArtsTableArabic = new PublicArtsTableArabic(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getLongitude(),
                                activityReference.get().models.get(i).getLatitude());
                        activityReference.get().qmDatabase.getPublicArtsTableDao().insert(publicArtsTableArabic);
                    }
                }

            }
            return true;
        }
    }

    public static class RetrieveEnglishPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;

        RetrieveEnglishPublicArtsData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableEnglish> publicArtsTableEnglish) {
            activityReference.get().models.clear();
            if (publicArtsTableEnglish.size() > 0) {
                for (int i = 0; i < publicArtsTableEnglish.size(); i++) {
                    CommonModel commonModel = new CommonModel(
                            String.valueOf(publicArtsTableEnglish.get(i).getPublic_arts_id()),
                            publicArtsTableEnglish.get(i).getPublic_arts_name(),
                            publicArtsTableEnglish.get(i).getPublic_arts_image(),
                            publicArtsTableEnglish.get(i).getLatitude(),
                            publicArtsTableEnglish.get(i).getLongitude());
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllEnglish();
        }
    }

    public static class RetrieveArabicPublicArtsData extends AsyncTask<Void, Void, List<PublicArtsTableArabic>> {
        private WeakReference<CommonActivity> activityReference;

        RetrieveArabicPublicArtsData(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<PublicArtsTableArabic> publicArtsTableArabic) {
            activityReference.get().models.clear();
            if (publicArtsTableArabic.size() > 0) {
                for (int i = 0; i < publicArtsTableArabic.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(publicArtsTableArabic.get(i).getPublic_arts_id()),
                            publicArtsTableArabic.get(i).getPublic_arts_name(),
                            publicArtsTableArabic.get(i).getPublic_arts_image(),
                            publicArtsTableArabic.get(i).getLatitude(), publicArtsTableArabic.get(i).getLongitude());
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PublicArtsTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getPublicArtsTableDao().getAllArabic();
        }
    }

    public static class HeritageRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonActivity> activityReference;
        String language;

        HeritageRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getHeritageListTableDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().heritageTableRowCount = integer;
            if (activityReference.get().heritageTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckHeritageDBRowExist(activityReference.get(), language).execute();

            } else {
                new InsertHeritageDataToDataBase(activityReference.get(),
                        activityReference.get().heritageListTableEnglish,
                        activityReference.get().heritageListTableArabic, language).execute();
            }
        }
    }

    public static class InsertHeritageDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CommonActivity> activityReference;
        private HeritageListTableEnglish heritageListTableEnglish;
        private HeritageListTableArabic heritageListTableArabic;
        String language;

        InsertHeritageDataToDataBase(CommonActivity context, HeritageListTableEnglish heritageListTableEnglish,
                                     HeritageListTableArabic heritageListTableArabic, String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.heritageListTableEnglish = heritageListTableEnglish;
            this.heritageListTableArabic = heritageListTableArabic;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        heritageListTableEnglish = new HeritageListTableEnglish(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                null, null, null, null,
                                null,
                                activityReference.get().models.get(i).getSortId());
                        activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        heritageListTableArabic = new HeritageListTableArabic(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                null, null, null, null,
                                null,
                                activityReference.get().models.get(i).getSortId());
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

    public static class CheckHeritageDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        private HeritageListTableEnglish heritageListTableEnglish;
        String language;

        CheckHeritageDBRowExist(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritagePageTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            heritageListTableEnglish = new HeritageListTableEnglish(
                                    Long.parseLong(activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getSortId(), null, null,
                                    null, null, null);
                            activityReference.get().qmDatabase.getHeritageListTableDao().insert(heritageListTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getHeritageListTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateHeritagePageTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            activityReference.get().heritageListTableArabic = new HeritageListTableArabic(
                                    Long.parseLong(activityReference.get().models.get(i).getId()),
                                    activityReference.get().models.get(i).getName(),
                                    activityReference.get().models.get(i).getImage(),
                                    activityReference.get().models.get(i).getSortId(),
                                    null, null, null,
                                    null, null);
                            activityReference.get().qmDatabase.getHeritageListTableDao().insert(activityReference.get().heritageListTableArabic);
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateHeritagePageTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdateHeritagePageTable(CommonActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageListEnglish(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getSortId(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getHeritageListTableDao().updateHeritageListArabic(
                        activityReference.get().models.get(position).getName(),
                        activityReference.get().models.get(position).getSortId(),
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getId()
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }

    public static class RetrieveHeritageDataEnglish extends AsyncTask<Void, Void, List<HeritageListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;


        RetrieveHeritageDataEnglish(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<HeritageListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getAllEnglish();
        }

        @Override
        protected void onPostExecute(List<HeritageListTableEnglish> heritageListTableEnglishes) {
            activityReference.get().models.clear();
            if (heritageListTableEnglishes.size() > 0) {
                for (int i = 0; i < heritageListTableEnglishes.size(); i++) {
                    CommonModel commonModel = new CommonModel(
                            heritageListTableEnglishes.get(i).getHeritage_name(),
                            String.valueOf(heritageListTableEnglishes.get(i).getHeritage_id()),
                            null, heritageListTableEnglishes.get(i).getHeritage_image(),
                            null, null,
                            null, null);
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class RetrieveHeritageDataArabic extends AsyncTask<Void, Void, List<HeritageListTableArabic>> {

        private WeakReference<CommonActivity> activityReference;

        RetrieveHeritageDataArabic(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<HeritageListTableArabic> heritageListTableArabics) {
            activityReference.get().models.clear();
            if (heritageListTableArabics.size() > 0) {
                for (int i = 0; i < heritageListTableArabics.size(); i++) {
                    CommonModel commonModel = new CommonModel(
                            heritageListTableArabics.get(i).getHeritage_name(),
                            String.valueOf(heritageListTableArabics.get(i).getHeritage_id()),
                            null, heritageListTableArabics.get(i).getHeritage_image(),
                            null, null,
                            null, null);
                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<HeritageListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getHeritageListTableDao().getAllArabic();
        }
    }

    public static class ExhibitionRowCount extends AsyncTask<Void, Void, Integer> {

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
            activityReference.get().exhibitionTableRowCount = integer;
            if (activityReference.get().exhibitionTableRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckExhibitionDBRowExist(activityReference.get(), language).execute();

            } else {
                new InsertExhibitionDataToDataBase(activityReference.get(),
                        activityReference.get().exhibitionListTableEnglish,
                        activityReference.get().exhibitionListTableArabic, language).execute();

            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getExhibitionTableDao().getNumberOfRowsArabic();
        }

    }

    public static class CheckExhibitionDBRowExist extends AsyncTask<Void, Void, Void> {

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
            if (activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkEnglishIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            exhibitionListTableEnglish = new ExhibitionListTableEnglish(Long.parseLong(
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
                                    activityReference.get().models.get(i).getDisplayDate());
                            activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableEnglish);

                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getExhibitionTableDao().checkArabicIdExist(
                                Integer.parseInt(activityReference.get().models.get(i).getId()));
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateExhibitionTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            activityReference.get().exhibitionListTableArabic = new ExhibitionListTableArabic(
                                    Long.parseLong(activityReference.get().models.get(i).getId()),
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
                                    activityReference.get().models.get(i).getDisplayDate());
                            activityReference.get().qmDatabase.getExhibitionTableDao().insert(activityReference.get().exhibitionListTableArabic);

                        }
                    }
                }
            }
            return null;
        }

    }

    public static class InsertExhibitionDataToDataBase extends AsyncTask<Void, Void, Boolean> {
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
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        exhibitionListTableEnglish = new ExhibitionListTableEnglish(Long.parseLong(
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
                                activityReference.get().models.get(i).getDisplayDate());
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        exhibitionListTableArabic = new ExhibitionListTableArabic(
                                Long.parseLong(activityReference.get().models.get(i).getId()),
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
                                activityReference.get().models.get(i).getDisplayDate());
                        activityReference.get().qmDatabase.getExhibitionTableDao().insert(exhibitionListTableArabic);
                    }
                }
            }
            return true;
        }
    }

    public static class UpdateExhibitionTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdateExhibitionTable(CommonActivity context, String apiLanguage, int p) {
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
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionListEnglish(
                        activityReference.get().models.get(position).getStartDate(),
                        activityReference.get().models.get(position).getEndDate(),
                        activityReference.get().models.get(position).getLocation(),
                        activityReference.get().models.get(position).getId(),
                        activityReference.get().models.get(position).getMuseumId()
                );

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getExhibitionTableDao().updateExhibitionListArabic(
                        activityReference.get().models.get(position).getStartDate(),
                        activityReference.get().models.get(position).getEndDate(),
                        activityReference.get().models.get(position).getLocation(),
                        activityReference.get().models.get(position).getId(),
                        activityReference.get().models.get(position).getMuseumId()
                );
            }
            return null;
        }
    }

    public static class RetrieveExhibitionDataEnglish extends AsyncTask<Void, Void, List<ExhibitionListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;

        String museumID;

        RetrieveExhibitionDataEnglish(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveExhibitionDataEnglish(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableEnglish> exhibitionListTableEnglish) {
            activityReference.get().models.clear();
            if (exhibitionListTableEnglish.size() > 0) {
                for (int i = 0; i < exhibitionListTableEnglish.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableEnglish.get(i).getExhibition_id()),
                            exhibitionListTableEnglish.get(i).getExhibition_name(),
                            exhibitionListTableEnglish.get(i).getExhibition_location(),
                            exhibitionListTableEnglish.get(i).getExhibition_latest_image(),
                            exhibitionListTableEnglish.get(i).getExhibition_status(),
                            exhibitionListTableEnglish.get(i).getExhibition_display_date());

                    activityReference.get().models.add(i, commonModel);

                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
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

    public static class RetrieveExhibitionDataArabic extends AsyncTask<Void, Void, List<ExhibitionListTableArabic>> {
        private WeakReference<CommonActivity> activityReference;
        String museumID;

        RetrieveExhibitionDataArabic(CommonActivity context) {
            activityReference = new WeakReference<>(context);
        }

        RetrieveExhibitionDataArabic(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumID = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ExhibitionListTableArabic> exhibitionListTableArabic) {
            activityReference.get().models.clear();
            if (exhibitionListTableArabic.size() > 0) {
                for (int i = 0; i < exhibitionListTableArabic.size(); i++) {
                    CommonModel commonModel = new CommonModel(String.valueOf(exhibitionListTableArabic.get(i).getExhibition_id()),
                            exhibitionListTableArabic.get(i).getExhibition_name(),
                            exhibitionListTableArabic.get(i).getExhibition_location(),
                            exhibitionListTableArabic.get(i).getExhibition_latest_image(),
                            exhibitionListTableArabic.get(i).getExhibition_status(),
                            exhibitionListTableArabic.get(i).getExhibition_display_date());
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
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
        if (appLanguage.equals(LocaleManager.LANGUAGE_ENGLISH)) {
            new RetrieveMuseumCollectionDataEnglish(CommonActivity.this, id).execute();
        } else {
            new RetrieveMuseumCollectionDataArabic(CommonActivity.this, id).execute();
        }
    }

    public static class MuseumCollectionRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CommonActivity> activityReference;
        String language;

        MuseumCollectionRowCount(CommonActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            if (language.equals(LocaleManager.LANGUAGE_ENGLISH))
                return activityReference.get().qmDatabase.getMuseumCollectionListDao().getNumberOfRowsEnglish();
            else
                return activityReference.get().qmDatabase.getMuseumCollectionListDao().getNumberOfRowsArabic();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().museumCollectionListRowCount = integer;
            if (activityReference.get().museumCollectionListRowCount > 0) {
                //updateEnglishTable or add row to database
                new CheckMuseumCollectionDBRowExist(activityReference.get(), language).execute();

            } else {
                new InsertMuseumCollectionListDataToDataBase(activityReference.get(),
                        activityReference.get().museumCollectionListTableEnglish,
                        activityReference.get().museumCollectionListTableArabic, language).execute();

            }
        }
    }

    public static class CheckMuseumCollectionDBRowExist extends AsyncTask<Void, Void, Void> {
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
            if (activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumCollectionListDao().checkNameExistEnglish(
                                activityReference.get().models.get(i).getName());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumCollectionListTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            museumCollectionListTableEnglish =
                                    new MuseumCollectionListTableEnglish(
                                            activityReference.get().models.get(i).getName(),
                                            activityReference.get().models.get(i).getImage(),
                                            activityReference.get().models.get(i).getMuseumReference(),
                                            activityReference.get().models.get(i).getDescription());
                            activityReference.get().qmDatabase.getMuseumCollectionListDao().insertEnglishTable(museumCollectionListTableEnglish);
                        }
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        int n = activityReference.get().qmDatabase.getMuseumCollectionListDao().checkNameExistArabic(
                                activityReference.get().models.get(i).getName());
                        if (n > 0) {
                            //updateEnglishTable same id
                            new UpdateMuseumCollectionListTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                        } else {
                            //create row with corresponding id
                            museumCollectionListTableArabic =
                                    new MuseumCollectionListTableArabic(
                                            activityReference.get().models.get(i).getName(),
                                            activityReference.get().models.get(i).getImage(),
                                            activityReference.get().models.get(i).getMuseumReference(),
                                            activityReference.get().models.get(i).getDescription());
                            activityReference.get().qmDatabase.getMuseumCollectionListDao().insertArabicTable(museumCollectionListTableArabic);

                        }
                    }
                }
            }
            return null;
        }
    }

    public static class InsertMuseumCollectionListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
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
            if (activityReference.get().models != null && activityReference.get().models.size() > 0) {
                if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        museumCollectionListTableEnglish =
                                new MuseumCollectionListTableEnglish(activityReference.get().models.get(i).getName(),
                                        activityReference.get().models.get(i).getImage(),
                                        activityReference.get().models.get(i).getMuseumReference(),
                                        activityReference.get().models.get(i).getDescription());
                        activityReference.get().qmDatabase.getMuseumCollectionListDao().insertEnglishTable(museumCollectionListTableEnglish);
                    }
                } else {
                    for (int i = 0; i < activityReference.get().models.size(); i++) {
                        museumCollectionListTableArabic = new MuseumCollectionListTableArabic(
                                activityReference.get().models.get(i).getName(),
                                activityReference.get().models.get(i).getImage(),
                                activityReference.get().models.get(i).getMuseumReference(),
                                activityReference.get().models.get(i).getDescription());
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

    public static class UpdateMuseumCollectionListTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CommonActivity> activityReference;
        String language;
        int position;

        UpdateMuseumCollectionListTable(CommonActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (language.equals(LocaleManager.LANGUAGE_ENGLISH)) {
                // updateEnglishTable table with english name
                activityReference.get().qmDatabase.getMuseumCollectionListDao().updateMuseumListTableEnglish(
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getMuseumReference(),
                        activityReference.get().models.get(position).getDescription(),
                        activityReference.get().models.get(position).getName());

            } else {
                // updateEnglishTable table with arabic name
                activityReference.get().qmDatabase.getMuseumCollectionListDao().updateMuseumListTableArabic(
                        activityReference.get().models.get(position).getImage(),
                        activityReference.get().models.get(position).getMuseumReference(),
                        activityReference.get().models.get(position).getDescription(),
                        activityReference.get().models.get(position).getName());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }

    public static class RetrieveMuseumCollectionDataEnglish extends AsyncTask<Void, Void, List<MuseumCollectionListTableEnglish>> {
        private WeakReference<CommonActivity> activityReference;
        String museumReference;

        RetrieveMuseumCollectionDataEnglish(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumReference = museumId;
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionListTableEnglish> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionListDao().
                    getDataFromEnglishTableWithReference(museumReference);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionListTableEnglish> museumCollectionListTableEnglishes) {
            activityReference.get().models.clear();
            if (museumCollectionListTableEnglishes.size() > 0) {
                for (int i = 0; i < museumCollectionListTableEnglishes.size(); i++) {
                    CommonModel commonModel = new CommonModel(museumCollectionListTableEnglishes.get(i).getName(),
                            museumCollectionListTableEnglishes.get(i).getImage(),
                            museumCollectionListTableEnglishes.get(i).getMuseum_id());
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class RetrieveMuseumCollectionDataArabic extends AsyncTask<Void, Void,
            List<MuseumCollectionListTableArabic>> {

        private WeakReference<CommonActivity> activityReference;
        String museumReference;

        RetrieveMuseumCollectionDataArabic(CommonActivity context, String museumId) {
            activityReference = new WeakReference<>(context);
            museumReference = museumId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionListTableArabic> museumCollectionListTableArabics) {
            activityReference.get().models.clear();
            if (museumCollectionListTableArabics.size() > 0) {
                for (int i = 0; i < museumCollectionListTableArabics.size(); i++) {
                    CommonModel commonModel = new CommonModel(museumCollectionListTableArabics.get(i).getName(),
                            museumCollectionListTableArabics.get(i).getImage(),
                            museumCollectionListTableArabics.get(i).getMuseum_id());
                    activityReference.get().models.add(i, commonModel);
                }
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().recyclerView.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<MuseumCollectionListTableArabic> doInBackground(Void... voids) {
            return activityReference.get().qmDatabase.getMuseumCollectionListDao().
                    getDataFromArabicTableWithReference(museumReference);
        }
    }

}

package com.qatarmuseums.qatarmuseumsapp.museumcollectiondetails;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.QMDatabase;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIClient;
import com.qatarmuseums.qatarmuseumsapp.apicall.APIInterface;
import com.qatarmuseums.qatarmuseumsapp.museum.MuseumCollectionDetailTable;
import com.qatarmuseums.qatarmuseumsapp.park.NMoQParkListDetailsTable;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class CollectionDetailsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_close)
    ImageView toolbarClose;
    @BindView(R.id.collection_title)
    TextView collectionTitle;
    @BindView(R.id.collection_title_divider)
    View collectionTitleDivider;
    @BindView(R.id.long_description)
    TextView longDescription;
    @BindView(R.id.details_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.collection_share)
    ImageView shareIcon;
    Intent intent;
    @BindView(R.id.collection_favourite)
    ImageView favIcon;
    @BindView(R.id.progressBarLoading)
    ProgressBar progressBar;
    @BindView(R.id.no_result_layout)
    TextView noResultFoundLayout;
    @BindView(R.id.retry_layout)
    LinearLayout retryLayout;
    @BindView(R.id.retry_btn)
    Button retryButton;

    @BindView(R.id.details_layout)
    NestedScrollView detailLayout;

    private Animation zoomOutAnimation;
    private CollectionDetailsAdapter mAdapter;
    private NMoQParkListDetailsAdapter mAdapterPark;
    private ArrayList<CollectionDetailsList> collectionDetailsList = new ArrayList<>();
    private ArrayList<NMoQParkListDetails> nMoQParkListDetails = new ArrayList<>();
    private String categoryName, nid;
    Util util;
    String appLanguage;
    int collectionDetailRowCount;
    QMDatabase qmDatabase;
    MuseumCollectionDetailTable museumCollectionDetailTable;
    private NMoQParkListDetailsTable nMoQParkListDetailsTable;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        intent = getIntent();
        qmDatabase = QMDatabase.getInstance(CollectionDetailsActivity.this);
        util = new Util();
        categoryName = intent.getStringExtra("MAIN_TITLE");
        nid = intent.getStringExtra("NID");
        zoomOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out_more);
        if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.side_menu_parks_text))) {
            mAdapterPark = new NMoQParkListDetailsAdapter(this, nMoQParkListDetails);
            collectionTitle.setVisibility(View.GONE);
            collectionTitleDivider.setVisibility(View.GONE);
            longDescription.setVisibility(View.GONE);
            recyclerView.setAdapter(mAdapterPark);
        } else {
            mAdapter = new CollectionDetailsAdapter(this, collectionDetailsList);
            collectionTitle.setText(categoryName);
            longDescription.setText(intent.getStringExtra("LONG_DESC"));
            recyclerView.setAdapter(mAdapter);
        }
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setFocusable(false);
        appLanguage = LocaleManager.getLanguage(this);
        if (util.isNetworkAvailable(CollectionDetailsActivity.this)) {
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.side_menu_parks_text)))
                getNMoQParkDetailsFromAPI(nid);
            else
                getMuseumCollectionDetailFromAPI();
        } else {
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.side_menu_parks_text)))
                getNMoQParkListDetailsFromDataBase(nid);
            else
                getMuseumCollectionDetailFromDatabase();
        }
        retryButton.setOnClickListener(v -> {
            Timber.i("Retry button clicked");
            if (intent.getStringExtra("COMING_FROM").equals(this.getString(R.string.side_menu_parks_text)))
                getNMoQParkDetailsFromAPI(nid);
            else
                getMuseumCollectionDetailFromAPI();
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
        toolbarClose.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    toolbarClose.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        toolbarClose.setOnClickListener(view -> finish());
        favIcon.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    favIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });
        shareIcon.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    shareIcon.startAnimation(zoomOutAnimation);
                    break;
            }
            return false;
        });


    }

    private void getNMoQParkDetailsFromAPI(String nid) {
        Timber.i("getNMoQParkDetailsFromAPI(id: %s, language: %s)", nid, appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<NMoQParkListDetails>> call = apiService.getNMoQParkListDetails(appLanguage, nid);
        call.enqueue(new Callback<ArrayList<NMoQParkListDetails>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NMoQParkListDetails>> call,
                                   @NonNull Response<ArrayList<NMoQParkListDetails>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getNMoQParkDetailsFromAPI() - isSuccessful with size: %s", response.body().size());
                        detailLayout.setVisibility(View.VISIBLE);
                        nMoQParkListDetails.addAll(response.body());
                        removeParkHtmlTags(nMoQParkListDetails);
                        Collections.sort(nMoQParkListDetails);
                        mAdapterPark.notifyDataSetChanged();

                        // Commented for Nid issue in park list details API
//                        new NMoQParkListDetailsRowCount(CollectionDetailsActivity.this, appLanguage).execute();
                    } else {
                        Timber.i("Response have no data");
                        detailLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("Response not successful");
                    detailLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NMoQParkListDetails>> call, @NonNull Throwable t) {
                Timber.e("getNMoQParkDetailsFromAPI() - onFailure: %s", t.getMessage());
                detailLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumCollectionDetailFromAPI() {
        Timber.i("getMuseumCollectionDetailFromAPI(language: %s)", appLanguage);
        progressBar.setVisibility(View.VISIBLE);
        APIInterface apiService =
                APIClient.getClient().create(APIInterface.class);
        Call<ArrayList<CollectionDetailsList>> call = apiService.getMuseumCollectionDetails(appLanguage, categoryName);
        call.enqueue(new Callback<ArrayList<CollectionDetailsList>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CollectionDetailsList>> call,
                                   @NonNull Response<ArrayList<CollectionDetailsList>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        Timber.i("getMuseumCollectionDetailFromAPI() - isSuccessful with size: %s", response.body().size());
                        detailLayout.setVisibility(View.VISIBLE);
                        collectionDetailsList.addAll(response.body());
                        removeHtmlTags(collectionDetailsList);
                        mAdapter.notifyDataSetChanged();
                        new CollectionDetailRowCount(CollectionDetailsActivity.this).execute();


                    } else {
                        Timber.i("Response have no data");
                        detailLayout.setVisibility(View.GONE);
                        noResultFoundLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Timber.w("Response not successful");
                    detailLayout.setVisibility(View.GONE);
                    retryLayout.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CollectionDetailsList>> call,
                                  @NonNull Throwable t) {
                Timber.e("getMuseumCollectionDetailFromAPI() - onFailure: %s", t.getMessage());
                detailLayout.setVisibility(View.GONE);
                retryLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void getMuseumCollectionDetailFromDatabase() {
        Timber.i("getMuseumCollectionDetailFromDatabase()");
        new RetrieveMuseumCollectionDetailData(CollectionDetailsActivity.this).execute();
    }

    public void removeHtmlTags(ArrayList<CollectionDetailsList> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));
            models.get(i).setAbout(util.html2string(models.get(i).getAbout()));
        }
    }

    private void removeParkHtmlTags(ArrayList<NMoQParkListDetails> models) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setMainTitle(util.html2string(models.get(i).getMainTitle()));
            models.get(i).setDescription(util.html2string(models.get(i).getDescription()));
        }
    }

    public static class NMoQParkListDetailsRowCount extends AsyncTask<Void, Void, Integer> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;

        NMoQParkListDetailsRowCount(CollectionDetailsActivity context, String language) {
            this.activityReference = new WeakReference<>(context);
            this.language = language;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language: %s)", language);
            return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao()
                    .getNumberOfRows(language);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer > 0) {
                Timber.i("Count: %d", integer);
                new CheckNMoQParkListDetailsDBRowExist(activityReference.get(), language).execute();
            } else {
                Timber.i("Database table have no data");
                new InsertNMoQParkListDetailsDataToDataBase(
                        activityReference.get(),
                        activityReference.get().nMoQParkListDetailsTable, language).execute();
            }
        }
    }

    public static class CheckNMoQParkListDetailsDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;
        private NMoQParkListDetailsTable nMoQParkListDetailsTable;

        CheckNMoQParkListDetailsDBRowExist(CollectionDetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().nMoQParkListDetails.size() > 0) {
                for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                    int n = activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().checkIdExist(
                            Integer.parseInt(activityReference.get().nMoQParkListDetails.get(i).getNid()),
                            language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for id: %s", language,
                                activityReference.get().nMoQParkListDetails.get(i).getNid());
                        new UpdateNMoQParkListDetailsTable(activityReference.get(), language).execute();
                    } else {
                        Timber.i("Inserting data to Table(language: %s) with id: %s",
                                language, activityReference.get().nMoQParkListDetails.get(i).getNid());
                        nMoQParkListDetailsTable = new NMoQParkListDetailsTable(
                                activityReference.get().nMoQParkListDetails.get(i).getNid(),
                                activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                                activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                                activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                                activityReference.get().nMoQParkListDetails.get(i).getDescription(),
                                language);

                        activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                                insertTable(nMoQParkListDetailsTable);
                    }
                }
            }
            return null;
        }
    }

    public static class UpdateNMoQParkListDetailsTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;

        UpdateNMoQParkListDetailsTable(CollectionDetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating data on Table(language: %s) with id: %s",
                    language, activityReference.get().nMoQParkListDetails.get(0).getNid());
            activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().updateNMoQParkListDetails(
                    activityReference.get().nMoQParkListDetails.get(0).getMainTitle(),
                    activityReference.get().nMoQParkListDetails.get(0).getImages(),
                    activityReference.get().nMoQParkListDetails.get(0).getSortId(),
                    activityReference.get().nMoQParkListDetails.get(0).getNid(),
                    activityReference.get().nMoQParkListDetails.get(0).getDescription(),
                    language
            );
            return null;
        }
    }

    public static class InsertNMoQParkListDetailsDataToDataBase extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<CollectionDetailsActivity> activityReference;
        private NMoQParkListDetailsTable nMoQParkListDetailsTable;
        String language;

        InsertNMoQParkListDetailsDataToDataBase(CollectionDetailsActivity context,
                                                NMoQParkListDetailsTable nMoQParkListDetailsTable,
                                                String apiLanguage) {
            activityReference = new WeakReference<>(context);
            this.nMoQParkListDetailsTable = nMoQParkListDetailsTable;
            this.language = apiLanguage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().nMoQParkListDetails != null &&
                    activityReference.get().nMoQParkListDetails.size() > 0) {
                for (int i = 0; i < activityReference.get().nMoQParkListDetails.size(); i++) {
                    Timber.i("Inserting data to Table(language: %s) with id: %s",
                            language, activityReference.get().nMoQParkListDetails.get(i).getNid());
                    nMoQParkListDetailsTable = new NMoQParkListDetailsTable(
                            activityReference.get().nMoQParkListDetails.get(i).getNid(),
                            activityReference.get().nMoQParkListDetails.get(i).getMainTitle(),
                            activityReference.get().nMoQParkListDetails.get(i).getSortId(),
                            activityReference.get().nMoQParkListDetails.get(i).getImages().get(0),
                            activityReference.get().nMoQParkListDetails.get(i).getDescription(),
                            language);
                    activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                            insertTable(nMoQParkListDetailsTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public void getNMoQParkListDetailsFromDataBase(String nid) {
        Timber.i("getNMoQParkListDetailsFromDataBase(id: %s, language: %s)", nid, appLanguage);
        new RetrieveNMoQParkListDetailsData(CollectionDetailsActivity.this, nid).execute();
    }

    public static class RetrieveNMoQParkListDetailsData extends AsyncTask<Void, Void,
            List<NMoQParkListDetailsTable>> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private int nid;

        RetrieveNMoQParkListDetailsData(CollectionDetailsActivity context, String nid) {
            this.activityReference = new WeakReference<>(context);
            this.nid = Integer.parseInt(nid);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<NMoQParkListDetailsTable> doInBackground(Void... voids) {
            Timber.i("getNMoQParkListDetailsTable(id: %s, language: %s)", nid,
                    activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getNMoQParkListDetailsTableDao().
                    getNMoQParkListDetailsTable(nid, activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<NMoQParkListDetailsTable> nMoQParkListDetailsTables) {
            NMoQParkListDetails nMoQParkListDetails;
            if (nMoQParkListDetailsTables.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        nMoQParkListDetailsTables.size());
                for (int i = 0; i < nMoQParkListDetailsTables.size(); i++) {
                    Timber.i("Setting list from database with id: %s",
                            nMoQParkListDetailsTables.get(i).getParkNid());
                    ArrayList<String> image = new ArrayList<>();
                    image.add(nMoQParkListDetailsTables.get(i).getParkImages());

                    nMoQParkListDetails = new NMoQParkListDetails(
                            nMoQParkListDetailsTables.get(i).getParkNid(),
                            nMoQParkListDetailsTables.get(i).getParkTitle(),
                            image,
                            nMoQParkListDetailsTables.get(i).getParkDescription(),
                            nMoQParkListDetailsTables.get(i).getParkSortId());
                    activityReference.get().nMoQParkListDetails.add(i, nMoQParkListDetails);
                }
                activityReference.get().recyclerView.setVisibility(View.VISIBLE);
                activityReference.get().progressBar.setVisibility(View.GONE);
                Collections.sort(activityReference.get().nMoQParkListDetails);
                activityReference.get().mAdapterPark.notifyDataSetChanged();
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }


    }

    public static class CollectionDetailRowCount extends AsyncTask<Void, Void, Integer> {

        private WeakReference<CollectionDetailsActivity> activityReference;

        CollectionDetailRowCount(CollectionDetailsActivity context) {
            activityReference = new WeakReference<>(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            activityReference.get().collectionDetailRowCount = integer;
            if (activityReference.get().collectionDetailRowCount > 0) {
                Timber.i("Count: %d", integer);
                new CheckMuseumCollectionDetailDBRowExist(activityReference.get(),
                        activityReference.get().appLanguage).execute();

            } else {
                Timber.i("Database table have no data");
                new InsertMuseumCollectionDetailListDataToDataBase(activityReference.get(),
                        activityReference.get().museumCollectionDetailTable,
                        activityReference.get().appLanguage).execute();
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Timber.i("getNumberOfRows(language: %s)", activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao()
                    .getNumberOfRows(activityReference.get().appLanguage);
        }

    }

    public static class InsertMuseumCollectionDetailListDataToDataBase extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private MuseumCollectionDetailTable museumCollectionDetailTable;
        String language;

        InsertMuseumCollectionDetailListDataToDataBase(CollectionDetailsActivity context,
                                                       MuseumCollectionDetailTable museumCollectionDetailTable,
                                                       String appLanguage) {
            activityReference = new WeakReference<>(context);
            this.museumCollectionDetailTable = museumCollectionDetailTable;
            this.language = appLanguage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (activityReference.get().collectionDetailsList != null) {
                for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                    Timber.i("Inserting data to Table(language: %s) with title: %s",
                            language, activityReference.get().collectionDetailsList.get(i).getMainTitle());
                    museumCollectionDetailTable = new MuseumCollectionDetailTable(
                            activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                            activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                            activityReference.get().collectionDetailsList.get(i).getImage1(),
                            activityReference.get().collectionDetailsList.get(i).getAbout(),
                            language);
                    activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertData(museumCollectionDetailTable);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


    public static class CheckMuseumCollectionDetailDBRowExist extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        private MuseumCollectionDetailTable museumCollectionDetailTable;
        String language;

        CheckMuseumCollectionDetailDBRowExist(CollectionDetailsActivity context, String apiLanguage) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (activityReference.get().collectionDetailsList.size() > 0) {
                for (int i = 0; i < activityReference.get().collectionDetailsList.size(); i++) {
                    int n = activityReference.get().qmDatabase.getMuseumCollectionDetailDao().checkTitleExist(
                            activityReference.get().collectionDetailsList.get(i).getMainTitle(), language);
                    if (n > 0) {
                        Timber.i("Row exist in database(language: %s) for title: %s", language,
                                activityReference.get().collectionDetailsList.get(i).getMainTitle());
                        new UpdateMuseumCollectionDetailTable(activityReference.get(), activityReference.get().appLanguage, i).execute();

                    } else {
                        Timber.i("Inserting data to Table(language: %s) with title: %s",
                                language, activityReference.get().collectionDetailsList.get(i).getMainTitle());
                        museumCollectionDetailTable = new MuseumCollectionDetailTable(
                                activityReference.get().collectionDetailsList.get(i).getCategoryName(),
                                activityReference.get().collectionDetailsList.get(i).getMainTitle(),
                                activityReference.get().collectionDetailsList.get(i).getImage1(),
                                activityReference.get().collectionDetailsList.get(i).getAbout(),
                                language);
                        activityReference.get().qmDatabase.getMuseumCollectionDetailDao().insertData(museumCollectionDetailTable);

                    }
                }
            }
            return null;
        }
    }

    public static class UpdateMuseumCollectionDetailTable extends AsyncTask<Void, Void, Void> {
        private WeakReference<CollectionDetailsActivity> activityReference;
        String language;
        int position;

        UpdateMuseumCollectionDetailTable(CollectionDetailsActivity context, String apiLanguage, int p) {
            activityReference = new WeakReference<>(context);
            language = apiLanguage;
            position = p;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Timber.i("Updating data on Table(language: %s) for title: %s",
                    language, activityReference.get().collectionDetailsList.get(0).getMainTitle());
            activityReference.get().qmDatabase.getMuseumCollectionDetailDao().updateMuseumDetailTable(
                    activityReference.get().collectionDetailsList.get(position).getCategoryName(),
                    activityReference.get().collectionDetailsList.get(position).getImage1(),
                    activityReference.get().collectionDetailsList.get(position).getAbout(),
                    activityReference.get().collectionDetailsList.get(position).getMainTitle(),
                    language);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }


    }


    public static class RetrieveMuseumCollectionDetailData extends AsyncTask<Void, Void, List<MuseumCollectionDetailTable>> {
        private WeakReference<CollectionDetailsActivity> activityReference;

        RetrieveMuseumCollectionDetailData(CollectionDetailsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            activityReference.get().progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MuseumCollectionDetailTable> doInBackground(Void... voids) {
            Timber.i("getAllDataFromMuseumDetailTable(name: %s, language: %s)",
                    activityReference.get().categoryName, activityReference.get().appLanguage);
            return activityReference.get().qmDatabase.getMuseumCollectionDetailDao().
                    getAllDataFromMuseumDetailTable(activityReference.get().categoryName,
                            activityReference.get().appLanguage);
        }

        @Override
        protected void onPostExecute(List<MuseumCollectionDetailTable> museumCollectionListTableList) {
            activityReference.get().collectionDetailsList.clear();
            if (museumCollectionListTableList.size() > 0) {
                Timber.i("Set list from database with size: %d",
                        museumCollectionListTableList.size());
                for (int i = 0; i < museumCollectionListTableList.size(); i++) {
                    Timber.i("Setting list from database with name: %s",
                            museumCollectionListTableList.get(i).getDetail_title());
                    CollectionDetailsList collectionDetailsList1 = new CollectionDetailsList(
                            museumCollectionListTableList.get(i).getDetail_title(),
                            museumCollectionListTableList.get(i).getDetail_image1(),
                            museumCollectionListTableList.get(i).getDetail_about(),
                            museumCollectionListTableList.get(i).getCategory_id());
                    activityReference.get().collectionDetailsList.add(i, collectionDetailsList1);

                }
                activityReference.get().detailLayout.setVisibility(View.VISIBLE);
                activityReference.get().mAdapter.notifyDataSetChanged();
                activityReference.get().progressBar.setVisibility(View.GONE);
            } else {
                Timber.i("Have no data in database");
                activityReference.get().progressBar.setVisibility(View.GONE);
                activityReference.get().detailLayout.setVisibility(View.GONE);
                activityReference.get().retryLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}

package com.qatarmuseums.qatarmuseumsapp.commonactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.detailsactivity.DetailsActivity;
import com.qatarmuseums.qatarmuseumsapp.detailsactivity.DiningActivity;

import java.util.ArrayList;
import java.util.List;

public class CommonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbar_title;
    private RecyclerView recyclerView;
    private List<CommonModel> models = new ArrayList<>();
    private CommonListAdapter mAdapter;
    private ImageView backArrow;
    private Animation zoomOutAnimation;
    String toolbarTitle;
    Intent intent, navigationIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        backArrow = (ImageView) findViewById(R.id.toolbar_back);
        intent = getIntent();
        toolbarTitle = intent.getStringExtra(getString(R.string.toolbar_title_key));
        toolbar_title.setText(toolbarTitle);
        recyclerView = (RecyclerView) findViewById(R.id.common_recycler_view);

        mAdapter = new CommonListAdapter(this, models, new RecyclerTouchListener.ItemClickListener() {
            @Override
            public void onPositionClicked(int position) {
                if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text)))
                    navigationIntent = new Intent(CommonActivity.this, DiningActivity.class);
                else
                    navigationIntent = new Intent(CommonActivity.this, DetailsActivity.class);
                navigationIntent.putExtra("HEADER_IMAGE", models.get(position).getImage());
                navigationIntent.putExtra("MAIN_TITLE", models.get(position).getName());
                navigationIntent.putExtra("COMING_FROM", toolbarTitle);
                navigationIntent.putExtra("IS_FAVOURITE", models.get(position).getIsfavourite());
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
        if (toolbarTitle.equals(getString(R.string.sidemenu_exhibition_text)))
            prepareExhibitionData();
        else if (toolbarTitle.equals(getString(R.string.sidemenu_heritage_text)))
            prepareHeritageData();
        else if (toolbarTitle.equals(getString(R.string.sidemenu_public_arts_text)))
            preparePublicArtsData();
        else if (toolbarTitle.equals(getString(R.string.sidemenu_dining_text)))
            prepareDiningData();
    }

    private void prepareExhibitionData() {
        CommonModel model = new CommonModel("1", "Project Space 12 Bouthayna Al-Muftah: Echoes",
                "Wednesday, July 11, 2018 - 11:45 - Monday, September 10, 2018 - 11:45",
                "Mathaf: Arab Museum of Modern Art",
                "",
                null, true);
        models.add(model);
        model = new CommonModel("2", "ARTIST IN RESIDENCE 2017-2018: DUAL INSPIRATIONS",
                "Tuesday, July 17, 2018 - 07:45 - Monday, October 1, 2018 - 07:45",
                "Fire Station Artist in Residence, Garage Gallery",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/air3-homepage_qm_2000x750px-02.jpg?itok=rmFs7UIN",
                null, false);
        models.add(model);
        model = new CommonModel("3", "PEARLS: TREASURES FROM THE SEAS AND THE RIVERS",
                "Wednesday, July 11, 2018 - 09:15 - Monday, October 1, 2018 - 09:15",
                "Revolution Square 2/3, Moscow 109012",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/076-and-cover-marie-valerie-tiara.jpg?itok=PxyEnw9O",
                null, false);
        models.add(model);
        model = new CommonModel("1", "LAUNDROMAT",
                "Thursday, March 15, 2018 - 12:45 - Friday, June 1, 2018 - 12:45",
                "Fire Station Artist in Residence, Garage Gallery",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/2012-photo-credit-ai-weiwei-studio-post_0.jpg?itok=MVxORAFa",
                null, false);
        models.add(model);
        model = new CommonModel("1", "Contemporary Art Qatar",
                "Saturday, December 9, 2017 - 07:45 - Wednesday, January 3, 2018 - 07:45",
                "KRAFTWERK BERLIN",
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/contemporary-art-qatar-01.jpg?itok=F4qKDliY",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void prepareHeritageData() {
        CommonModel model = new CommonModel("1", "Al Zubarah",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al-zubarah-page-site-01.jpg?itok=wcS5I03J",
                null, true);
        models.add(model);
        model = new CommonModel("2", "Forts of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al_zubara_fort_2.jpg?itok=3bUUyTJy",
                null, false);
        models.add(model);
        model = new CommonModel("3", "Towers of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-barzantowersbyfotoarabia-11.jpg?itok=TuqhuEAA",
                null, false);
        models.add(model);
        model = new CommonModel("4", "Wells of Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-alkhorwellbyfotoarabia-3.jpg?itok=0Jp0UX93",
                null, false);
        models.add(model);
        model = new CommonModel("4", "New life for old Qatar",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/heritagesites-abudhaloufmosquebyfotoarabia-1.jpg?itok=FTZrKTPQ",
                null, false);
        models.add(model);
        model = new CommonModel("4", "Cliffs, Carvings and Islands",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/mobile_design/public/hero_image/project/al_khor_island_12.jpg?itok=s0vYeuk4",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void preparePublicArtsData() {
        CommonModel model = new CommonModel("1", "GANDHI’S THREE MONKEYS\n" +
                "BY SUBODH GUPTA",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/three_monkeys_resize.jpg?itok=IbQeExDN",
                null, true);
        models.add(model);
        model = new CommonModel("2", "7 BY RICHARD SERRA",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/publicart-7byfotoarabia-2.jpg?itok=FoETPWXy",
                null, true);
        models.add(model);
        model = new CommonModel("3", "LUSAIL HANDBALL INSTALLATIONS\n",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/lusail_multipurpose_hall-sculpture_hands_1.jpg?itok=M-JWK40f",
                null, false);
        models.add(model);
        model = new CommonModel("4", "SMOKE BY TONY SMITH",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/smke_4_of_1.jpg?itok=4_xFXy7c",
                null, false);
        models.add(model);
        model = new CommonModel("5", "QATAR UNIVERSITY INSTALLATIONS",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/she_0214.jpg.jpg?itok=KmeK2Yll",
                null, false);
        models.add(model);
        model = new CommonModel("6", "AIRPORT INSTALLATIONS ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/c_lsp_qm_hia_artwork-9938_0.jpg?itok=F4Vxmn2W",
                null, false);
        models.add(model);
        model = new CommonModel("6", "EAST-WEST / WEST-EAST BY RICHARD SERRA",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/publicart-eastwestwesteastbyfotoarabia-9.jpg?itok=sxjwfULm",
                null, false);
        models.add(model);
        model = new CommonModel("6", "THE MIRACULOUS JOURNEY BY DAMIEN HIRST",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/the_miraculous_journey_dh_resize_for_teaser.jpg?itok=JBx4ktsu",
                null, false);
        models.add(model);
        model = new CommonModel("6", "CALLIGRAFFITI BY EL SEED",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/day_12_87.jpg?itok=8eA8_yWk",
                null, false);
        models.add(model);
        model = new CommonModel("6", "MAMAN BY LOUISE BOURGEOIS ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/publicart-mamanbyfotoarabia-46.jpg?itok=SMdtKfML",
                null, false);
        models.add(model);
        model = new CommonModel("6", "PERCEVAL BY SARAH LUCAS",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/publicart-percevalbyfotoarabia-5.jpg?itok=RyV5XB_R",
                null, false);
        models.add(model);
        model = new CommonModel("6", "HEALTHY LIVING FROM THE START\n" +
                "BY ANNE GEDDES ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/promo_image/public/teaser_images/projects/yassin_ismail_mousa_basketballer_holding_mohammad_1_kg_-_3_weeks_old_0.jpeg?itok=Mt6vu_cH",
                null, false);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

    private void prepareDiningData() {
        CommonModel model = new CommonModel("1", "IDAM",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/idam-pierremonetta_mg_6372_1.jpg?itok=bKArHUGQ",
                null, true);
        models.add(model);
        model = new CommonModel("2", "IN-Q CAFÉ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/inq.jpg?itok=C14Qr6xt",
                null, true);
        models.add(model);
        model = new CommonModel("3", "MIA CAFÉ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/dsc_0597_2_0.jpg?itok=TXvRM1HE",
                null, false);
        models.add(model);
        model = new CommonModel("4", "AL RIWAQ CAFÉ",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/10_0.jpg?itok=4BYJtRQB",
                null, false);
        models.add(model);
        model = new CommonModel("4", "MIA CATERING",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/mia-catering.jpg?itok=Kk7svJPU",
                null, false);
        models.add(model);
        model = new CommonModel("4", "MATHAF MAQHA",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/332a0071_0.jpg?itok=--l8qFkn",
                null, false);
        models.add(model);
        model = new CommonModel("4", "CAFÉ #999",
                null,
                null,
                "http://www.qm.org.qa/sites/default/files/styles/content_image/public/images/body/332a4417.jpg?itok=_OpfHaT_",
                null, true);
        models.add(model);

        mAdapter.notifyDataSetChanged();
    }

}

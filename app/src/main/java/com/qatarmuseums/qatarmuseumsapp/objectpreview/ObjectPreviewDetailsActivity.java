package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qatarmuseums.qatarmuseumsapp.LocaleManager;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.floormap.FloorMapActivity;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class ObjectPreviewDetailsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    Toolbar toolbar;
    ImageView closeBtn, image1, image2, image3, image4, imageToZoom, playButton;

    TextView maiTtitle, shortDescription, image1Description, historyTitle, historyDescription,
            image2Description;
    String title, description, history, summary, mainImage;
    ArrayList<String> imageList;

    private Intent intent;
    private Util utils;
    private SeekBar seekBar;
    private String audioURL;
    private boolean playPause;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private MediaPlayer mediaPlayer;
    private int lengthOfAudio;
    private final Handler handler = new Handler();
    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            updateSeekProgress();
        }
    };
    private LinearLayout audioControlLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_preview_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        closeBtn = findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                stopAudio();
            }
        });

        imageToZoom = findViewById(R.id.image_to_zoom);
        maiTtitle = findViewById(R.id.title);
        shortDescription = findViewById(R.id.short_description);
        historyTitle = findViewById(R.id.history_title);
        historyDescription = findViewById(R.id.history_description);
        image1 = findViewById(R.id.image_1);
        image2 = findViewById(R.id.image_2);
        image3 = findViewById(R.id.image_3);
        image4 = findViewById(R.id.image_4);
        image1Description = findViewById(R.id.image_desc1);
        image2Description = findViewById(R.id.image_desc2);
        audioControlLayout = findViewById(R.id.audio_control);
        playButton = findViewById(R.id.play_button);
        seekBar = findViewById(R.id.seek_bar);
        utils = new Util();
        intent = getIntent();
        title = intent.getStringExtra("Title");
        mainImage = intent.getStringExtra("Image");
        description = intent.getStringExtra("Description");
        history = intent.getStringExtra("History");
        summary = intent.getStringExtra("Summary");
        imageList = intent.getStringArrayListExtra("Images");
        audioURL = intent.getStringExtra("Audio");
        if (audioURL != null && !audioURL.equals(""))
            audioControlLayout.setVisibility(View.VISIBLE);
        imageToZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForZoomingImage();
            }
        });

        maiTtitle.setText(utils.html2string(title));
        shortDescription.setText(description);
        GlideApp.with(this)
                .load(mainImage)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into(imageToZoom);
        if (summary != null) {
            image2Description.setText(utils.html2string(summary));
            image2Description.setVisibility(View.VISIBLE);
        }
        if (history != null && !history.equals("")) {
            historyTitle.setVisibility(View.VISIBLE);
            historyDescription.setVisibility(View.VISIBLE);
            historyDescription.setText(history);
        }
        if (imageList != null && imageList.size() > 0) {
            switch (imageList.size()) {
                case 1:
                    setImage1();
                    break;
                case 2:
                    setImage1();
                    setImage2();
                    break;
                case 3:
                    setImage1();
                    setImage2();
                    setImage3();
                    break;
                case 4:
                    setImage1();
                    setImage2();
                    setImage3();
                    setImage4();
                    break;
            }
        }
        initialize_Controls();
    }

    private void initialize_Controls() {

        seekBar.setOnSeekBarChangeListener(this);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isNetworkAvailable(getApplicationContext())) {
                    if (!playPause) {
                        playButton.setImageDrawable(getDrawable(R.drawable.pause_black));
                        if (initialStage) {
                            new Player().execute(audioURL);
                        } else {
                            if (!mediaPlayer.isPlaying()) {
                                playAudio();
                                updateSeekProgress();
                            }
                        }
                        playPause = true;
                    } else {
                        playButton.setImageDrawable(getDrawable(R.drawable.play_black));
                        if (mediaPlayer.isPlaying()) {
                            pauseAudio();
                        }
                        playPause = false;
                    }
                } else {
                    Toast.makeText(ObjectPreviewDetailsActivity.this, R.string.check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                lengthOfAudio = mediaPlayer.getDuration();
                prepared = true;

            } catch (Exception e) {
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            playAudio();
            updateSeekProgress();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        initialStage = true;
        playPause = false;
        mediaPlayer.stop();
        mediaPlayer.reset();
        playButton.setImageDrawable(getDrawable(R.drawable.play_black));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo((lengthOfAudio / 100) * seekBar.getProgress());
        }
    }

    private void updateSeekProgress() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / lengthOfAudio) * 100));
            handler.postDelayed(r, 1000);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            initialStage = true;
            playPause = false;
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        playButton.setImageDrawable(getDrawable(R.drawable.play_black));
        seekBar.setProgress(0);
    }

    private void pauseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        playButton.setImageDrawable(getDrawable(R.drawable.play_black));
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audio != null) {
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                Toast.makeText(this, R.string.increase_volume, Toast.LENGTH_SHORT).show();
        }
        playButton.setImageDrawable(getDrawable(R.drawable.pause_black));
    }

    public void setImage1() {
        GlideApp.with(this)
                .load(imageList.get(0))
                .into(image1);
        image1.setVisibility(View.VISIBLE);
    }

    public void setImage2() {
        GlideApp.with(this)
                .load(imageList.get(1))
                .into(image2);
        image2.setVisibility(View.VISIBLE);
    }

    public void setImage3() {
        GlideApp.with(this)
                .load(imageList.get(2))
                .into(image3);
        image3.setVisibility(View.VISIBLE);
    }

    public void setImage4() {
        GlideApp.with(this)
                .load(imageList.get(3))
                .into(image4);
        image4.setVisibility(View.VISIBLE);
    }

    public void openDialogForZoomingImage() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this/*,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen*/);
        View mView = getLayoutInflater().inflate(R.layout.zooming_layout, null);
        PhotoView photoView = mView.findViewById(R.id.imageView);
        photoView.setImageURI(Uri.parse(mainImage));
        GlideApp.with(this)
                .load(mainImage)
                .placeholder(R.drawable.placeholder_portrait)
                .into(photoView);

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
        mDialog.setCancelable(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAudio();
        playPause = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(this, getString(R.string.object_preview_details_page), null);
    }
}

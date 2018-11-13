package com.qatarmuseums.qatarmuseumsapp.objectpreview;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.booking.rtlviewpager.RtlViewPager;
import com.github.chrisbanes.photoview.PhotoView;
import com.qatarmuseums.qatarmuseumsapp.R;
import com.qatarmuseums.qatarmuseumsapp.home.GlideApp;
import com.qatarmuseums.qatarmuseumsapp.utils.Util;

import java.util.ArrayList;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class PageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, ViewPager.OnPageChangeListener {

    private TextView positionInfo, artifactName, acessionText, shortDescription, image1Description,
            historyTitle, historyDescription, image2Description;
    ImageView image1, image2, image3, image4, playButton;
    private SeekBar seekBar;
    private ImageView mainImageView;
    String title, description, history, summary, mainImage, audioURL, galleryNumber, floorNumber;
    ArrayList<String> imageList;
    private LinearLayout audioControlLayout;
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
    private Util utils;
    ViewPager pager;

    public PageFragment() {
    }

    public PageFragment newInstance(int position, boolean isLast, String gallery, String floor,
                                    String image, String title, String accessionNumber,
                                    String curatorialDescription, String objectHistory, String objectENGSummary,
                                    ArrayList<String> images, String audio) {

        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        args.putString("GALLERY", gallery);
        args.putString("FLOOR", floor);
        args.putString("MAINTITLE", title);
        args.putString("ACCESIONNUMBER", accessionNumber);
        args.putString("IMAGE", image);
        args.putString("DESCRIPTION", curatorialDescription);
        args.putString("HISTORY", objectHistory);
        args.putString("SUMMARY", objectENGSummary);
        args.putStringArrayList("IMAGES", images);
        args.putString("AUDIO", audio);
        if (isLast)
            args.putBoolean("isLast", true);
        final PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_page, container, false);
        positionInfo = view.findViewById(R.id.floor_gallery);

        mainImageView = view.findViewById(R.id.image_to_zoom);
        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForZoomingImage();
            }
        });
        artifactName = view.findViewById(R.id.title);
        acessionText = view.findViewById(R.id.acession_number);
        shortDescription = view.findViewById(R.id.short_description);
        historyTitle = view.findViewById(R.id.history_title);
        historyDescription = view.findViewById(R.id.history_description);
        image1 = view.findViewById(R.id.image_1);
        image2 = view.findViewById(R.id.image_2);
        image3 = view.findViewById(R.id.image_3);
        image4 = view.findViewById(R.id.image_4);
        image1Description = view.findViewById(R.id.image_desc1);
        image2Description = view.findViewById(R.id.image_desc2);
        audioControlLayout = view.findViewById(R.id.audio_control);
        playButton = view.findViewById(R.id.play_button);
        seekBar = view.findViewById(R.id.seek_bar);
        utils = new Util();
        pager = (RtlViewPager) getActivity().findViewById(R.id.pager);
        pager.addOnPageChangeListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int position = getArguments().getInt("POSITION", 0);
        galleryNumber = getArguments().getString("GALLERY");
        floorNumber = getArguments().getString("FLOOR");
        title = getArguments().getString("MAINTITLE");
        mainImage = getArguments().getString("IMAGE");
        description = getArguments().getString("DESCRIPTION");
        history = getArguments().getString("HISTORY");
        summary = getArguments().getString("SUMMARY");
        audioURL = getArguments().getString("AUDIO");
        imageList = getArguments().getStringArrayList("IMAGES");

        positionInfo.setText(getResources().getString(R.string.floor_label) + " " + floorNumber +
                getResources().getString(R.string.gallery_label) + " " + galleryNumber);
        if (audioURL != null && !audioURL.equals(""))
            audioControlLayout.setVisibility(View.VISIBLE);
        artifactName.setText(title);
        acessionText.setText(getArguments().getString("ACCESIONNUMBER"));
        GlideApp.with(this)
                .load(mainImage)
                .centerInside()
                .placeholder(R.drawable.placeholder)
                .into(mainImageView);
        shortDescription.setText(description);
        if (history != null && !history.equals("")) {
            historyTitle.setVisibility(View.VISIBLE);
            historyDescription.setVisibility(View.VISIBLE);
            historyDescription.setText(history);
            if (summary != null) {
                image2Description.setText(utils.html2string(summary));
                image2Description.setVisibility(View.VISIBLE);
            }

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
                if (utils.isNetworkAvailable(getContext())) {
                    if (!playPause) {
                        playButton.setImageDrawable(getContext().getDrawable(R.drawable.pause_black));
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
                        playButton.setImageDrawable(getContext().getDrawable(R.drawable.play_black));
                        if (mediaPlayer.isPlaying()) {
                            pauseAudio();
                        }
                        playPause = false;
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stopAudio();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
        playButton.setImageDrawable(getContext().getDrawable(R.drawable.play_black));
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
        if (playButton != null)
            playButton.setImageDrawable(getContext().getDrawable(R.drawable.play_black));
        if (seekBar != null)
            seekBar.setProgress(0);
    }

    private void pauseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        playButton.setImageDrawable(getContext().getDrawable(R.drawable.play_black));
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        AudioManager audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (audio != null) {
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
                Toast.makeText(getActivity(), R.string.increase_volume, Toast.LENGTH_SHORT).show();
        }
        playButton.setImageDrawable(getContext().getDrawable(R.drawable.pause_black));
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
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
    public void onPause() {
        super.onPause();
        pauseAudio();
        playPause = false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pager.removeOnPageChangeListener(this);
        stopAudio();
    }


}
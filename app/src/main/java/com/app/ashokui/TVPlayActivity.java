package com.app.ashokui;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class TVPlayActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private VideoView mVideoView;
    private String url;
    private ProgressBar load;
    private TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tv_play);
        url = getIntent().getStringExtra("videoUrl");
        init();
    }

    public void init() {
        load = findViewById(R.id.load);
        empty = findViewById(R.id.empty);
        mVideoView = findViewById(R.id.surface_view);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        Uri videoUri = Uri.parse(url);
        mVideoView.setVideoURI(videoUri);
        mVideoView.requestFocus();
        loading();
    }

    private void loading() {
        load.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    private void loadComplete(MediaPlayer arg0) {
        load.setVisibility(View.GONE);
        // vv.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        mVideoView.start();
        mVideoView.resume();
    }

    private void error(String msg) {
        load.setVisibility(View.GONE);
        mVideoView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        if (msg != null)
            empty.setText(msg);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Prepared");
        loadComplete(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Error");
        error("Unable to play this channel.");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Complete");
    }
}

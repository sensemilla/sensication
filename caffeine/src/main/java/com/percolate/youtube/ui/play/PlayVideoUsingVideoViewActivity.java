package com.percolate.youtube.ui.play;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.percolate.R;
import com.percolate.youtube.support.player.UrlParser;
import com.percolate.youtube.support.util.LogUtil;

import java.io.IOException;

/**
 * Created by LQG on 2014/12/5.
 */
// todo @EActivity(R.layout.playvideo_videoview)
  //  @EActivity
//@Fullscreen
public class PlayVideoUsingVideoViewActivity extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

  //  @ViewById
    VideoView player;

   // @ViewById
    ProgressBar playPb;

    MediaController mc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playvideo_videoview);
        playPb = (ProgressBar) findViewById(R.id.tl_custom);
        player = (VideoView) findViewById(R.id.player);
    }

     //   @AfterViews
    void afterView() {
        mc = new MediaController(this);
        player.setMediaController(mc);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);

        String videoId = getIntent().getStringExtra("videoId");
        play(videoId);
    }

    // @Background
    public void play(String videoId) {
        try {
            String videoUrl = getUrlFromId(videoId);
            LogUtil.d(videoUrl);
            startPlay(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    public String getUrlFromId(String videoId) throws IOException {
        String lYouTubeFmtQuality = "18";
        return new UrlParser().calculateYouTubeUrl(lYouTubeFmtQuality, true, videoId);
    }

  //  @UiThread
    void startPlay(String url) {
        //Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        player.setVideoURI(Uri.parse(url));
        player.requestFocus();
        player.start();
        mc.show();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        finish();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playPb.setVisibility(View.GONE);
    }
}

package com.percolate.youtube.ui.play;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.percolate.R;
import com.percolate.youtube.support.http.Search;

/**
 * Created by LQG on 2014/12/4.
 */
// todo @EActivity(R.layout.playvideo_youtube)
  //  @EActivity
//@Fullscreen
public class PlayVideoUsingYouTuBeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    // todo @ViewById(R.id.player)
    YouTubePlayerView player;

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.playvideo_youtube);

         player = (YouTubePlayerView) findViewById(R.id.player);
    }

  //  @AfterViews
    void config() {
        player.initialize(Search.apiKey, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, " onInitializationFailure ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        String videoId = getIntent().getStringExtra("videoId");
        if (!b) {
            youTubePlayer.cueVideo(videoId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            player.initialize(Search.apiKey, this);
        }
    }

}

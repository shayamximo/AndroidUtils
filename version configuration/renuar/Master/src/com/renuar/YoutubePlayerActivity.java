package com.renuar;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity implements
		YouTubePlayer.OnInitializedListener {

	public static final String YOUTUBE_VIDEO_URL = "youtube_video_url";
	YouTubePlayerView youTubeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_player);

		youTubeView = (YouTubePlayerView) findViewById(R.id.videoView1);
		String youtubeKey = getResources().getString(R.string.gplus_app_id);
		youTubeView.initialize(youtubeKey, this);

	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		Bundle bundle = getIntent().getExtras();
		String videoId = bundle.getString(YOUTUBE_VIDEO_URL);
		if (!wasRestored) {

			player.loadVideo(videoId);
			// player.setFullscreen(true);
		}

	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
	}

}

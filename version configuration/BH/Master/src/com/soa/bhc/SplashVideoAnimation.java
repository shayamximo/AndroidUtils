package com.soa.bhc;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.utils.MximoLog;

public class SplashVideoAnimation extends SplashHandler {

	protected DisplayImageOptions options;
	protected ImageLoader imageLoader;
	ImageView imageView;
	VideoView videoView;

	public SplashVideoAnimation(Splash splashActivity) {
		super(splashActivity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() {
		splashActivity.setContentView(R.layout.splash_video_layout);
		imageView = (ImageView) splashActivity
				.findViewById(R.id.image_in_video_splash);

	}

	private void goToImageScreen(final String urlForBackGround,
			final VideoView videoView) {
		options = App.getApp().getOptions();
		imageLoader = App.getApp().getImageLoader();

		imageLoader.displayImage(urlForBackGround, imageView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						videoView.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);

						Handler handler = new Handler();

						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								splashActivity.startNextActivity();

							}
						}, 2000);

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});
	}

	@Override
	protected void doOnWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {

			Uri video = Uri.parse("android.resource://"
					+ splashActivity.getPackageName() + "/"
					+ R.raw.splash_video);
			videoView = (VideoView) splashActivity
					.findViewById(R.id.splash_videoview);
			videoView.setVideoURI(video);
			videoView.start();

			videoView.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					IncrementEventForGoToImageView();
				}
			});
		}

	}

	@Override
	protected void doOnResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideMainView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldContinueAfterDataLoad() {
		return false;
	}

	@Override
	public void onInitInfoFinishLoadData() {
		IncrementEventForGoToImageView();
	}

	Integer eventCounter = 0;

	private void IncrementEventForGoToImageView() {

		MximoLog.i("ddddd", "eventCounter = " + eventCounter);
		synchronized (eventCounter) {
			eventCounter++;

			if (eventCounter == 2) {
				eventCounter = 0;

				splashActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						String urlForBackGround = InitInfoStore.getInstance()
								.getBackgroundUrlForView();
						goToImageScreen(urlForBackGround, videoView);

					}
				});

			}
		}

	}
}

package com.parqueteam;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

public class SplashDefaultAnimation extends SplashHandler {

	ImageView mainImageViewForDefaultSplashScreen;

	SplashDefaultAnimation(Splash splashActivity) {
		super(splashActivity);
	}

	private void showAnimation() {
		final AnimationDrawable frameAnimation = (AnimationDrawable) splashActivity
				.getResources().getDrawable(R.anim.animation_loading);
		mainImageViewForDefaultSplashScreen.setImageDrawable(frameAnimation);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				frameAnimation.start();
			}
		}, 10);

	}

	@Override
	protected void init() {
		splashActivity.setContentView(R.layout.default_splashscreen);
		mainImageViewForDefaultSplashScreen = (ImageView) splashActivity
				.findViewById(R.id.ss_image);

	}

	@Override
	protected void doOnWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			showAnimation();

		}

	}

	@Override
	protected void doOnResume() {

	}

	@Override
	public void hideMainView() {
		mainImageViewForDefaultSplashScreen.setVisibility(View.GONE);

	}
	
	@Override
	public boolean shouldContinueAfterDataLoad() {
		return true;
	}
	
	@Override
	public void onInitInfoFinishLoadData() {

	}

}

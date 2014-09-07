package com.renuar;


public abstract class SplashHandler {

	Splash splashActivity;

	public SplashHandler(Splash splashActivity) {
		this.splashActivity = splashActivity;
	}

	// things to be done right after onCreate() is called
	protected abstract void init();

	// things to be done right after onCreate() is called
	protected abstract void doOnWindowFocusChanged(boolean hasFocus);

	protected abstract void doOnResume();

	public abstract void hideMainView();
	
	public abstract boolean shouldContinueAfterDataLoad();
	
	public abstract void onInitInfoFinishLoadData();

}

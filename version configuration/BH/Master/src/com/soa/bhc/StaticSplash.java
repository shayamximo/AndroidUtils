package com.soa.bhc;


public class StaticSplash extends SplashHandler {

	public StaticSplash(Splash splashActivity) {
		super(splashActivity);

	}

	@Override
	protected void init() {
		splashActivity.setContentView(R.layout.static_splash_layout);

	}

	@Override
	protected void doOnWindowFocusChanged(boolean hasFocus) {

	}

	@Override
	protected void doOnResume() {

	}

	@Override
	public void hideMainView() {

	}
	
	@Override
	public boolean shouldContinueAfterDataLoad() {
		return true;
	}
	
	@Override
	public void onInitInfoFinishLoadData() {

	}

}

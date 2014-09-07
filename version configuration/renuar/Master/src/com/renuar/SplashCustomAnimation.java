package com.renuar;

import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SplashCustomAnimation extends SplashHandler {

	WebView webview;

	public SplashCustomAnimation(Splash splashActivity) {
		super(splashActivity);

	}

	@Override
	protected void init() {
		splashActivity.setContentView(R.layout.splash_screen);

		webview = (WebView) splashActivity.findViewById(R.id.splash_webView1);

		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webview.setHorizontalScrollBarEnabled(false);
		webview.setInitialScale(100);

	}

	@Override
	protected void doOnWindowFocusChanged(boolean hasFocus) {

	}

	@Override
	protected void doOnResume() {
		webview.clearCache(true);
		// webview.loadUrl("file:///android_asset/anim.htm");

		switch (splashActivity.getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			webview.loadUrl("file:///android_asset/anim_hdpi.htm");
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			webview.loadUrl("file:///android_asset/anim_hdpi.htm");
			break;
		case DisplayMetrics.DENSITY_HIGH:
			webview.loadUrl("file:///android_asset/anim_hdpi.htm");
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			webview.loadUrl("file:///android_asset/anim_xhdpi.htm");
			break;
		default:
			webview.loadUrl("file:///android_asset/anim_xhdpi.htm");
			break;
		}

	}

	@Override
	public void hideMainView() {
		webview.setVisibility(View.GONE);

	}

	@Override
	public boolean shouldContinueAfterDataLoad() {
		return true;
	}

	@Override
	public void onInitInfoFinishLoadData() {

	}

}

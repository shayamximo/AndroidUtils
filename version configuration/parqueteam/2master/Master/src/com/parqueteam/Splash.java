package com.parqueteam;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.PromotionsStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.ConfigurationFile.SplashType;

public class Splash extends Activity {

	public static final String KEY_IS_FROM_NOTIFICATION = "key_is_from_notification";
	public static final String KEY_DEEP_LINK_CONTROLLER = "com.parqueteam.key_deep_link_controler";
	boolean isFromNotification = false;
	WebView webview;
	Timer timer1;
	boolean animationFinished = false;
	SplashHandler splashHandler;

	@Override
	protected void onResume() {

		super.onResume();

		splashHandler.doOnResume();

		// com.facebook.AppEventsLogger.activateApp(this,
		// GlobalConstants.FB_PROMOTION);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		splashHandler.doOnWindowFocusChanged(hasFocus);

	}

	private boolean shouldOpenLoginScreen() {
		String action = getIntent().getAction();
		boolean isFromLaunch = (action != null)
				&& (action.equals("android.intent.action.MAIN"));
		// This means that
		// 1. this is mximo view
		// 2. that the mximo id(the configurable one hasn't been configured
		// yet.)
		boolean isMximoView = ConfigurationFile.getInstance().getAppAddress()
				.equals(0);
		return (isFromLaunch && isMximoView);

	}

	private SplashHandler getSplashHandler() {

		SplashType splashType = ConfigurationFile.getInstance().getSplashType();
		switch (splashType) {
		case SPLASH_DEFAULT_MXIMO_ANIMATION:
			return new SplashDefaultAnimation(this);

		case SPLASH_CONFIGURED_ANIMATION:

			return new SplashCustomAnimation(this);
		case SPLASH_STATIC:
			return new StaticSplash(this);
		case SPLASH_VIDEO:
			return new SplashVideoAnimation(this);

		default:
			break;
		}

		return null;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		splashHandler = getSplashHandler();

		splashHandler.init();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			isFromNotification = bundle.getBoolean(KEY_IS_FROM_NOTIFICATION,
					false);

			if (isFromNotification) {
				splashHandler.hideMainView();

			}

		}

		if (shouldOpenLoginScreen()) {
			startLoginActivity();
		}

		else {
			
			PromotionsStore.getInstance().loadDataIfNotInitialized(
					new StoreLoadDataListener() {

						@Override
						public void onFinish() {

						}

						@Override
						public void onError() {

						}
					});
			
			InitInfoStore.getInstance().loadDataIfNotInitialized(
					new StoreLoadDataListener() {

						@Override
						public void onFinish() {
							App.getApp().configureLanguage(
									InitInfoStore.getInstance().getLanguage());

							splashHandler.onInitInfoFinishLoadData();
							TaxonomiesStore.getInstance()
									.loadDataIfNotInitialized(
											new StoreLoadDataListener() {

												@Override
												public void onFinish() {
													if (splashHandler
															.shouldContinueAfterDataLoad()) {
														startNextActivity();
													} else {

													}

												}

												@Override
												public void onError() {
													// TODO Auto-generated
													// method stub

												}
											});

						}

						@Override
						public void onError() {
							// Do nothing

						}
					});

		}

	}

	public void startTimmer() {
		timer1 = new Timer();
		timer1.schedule(new TimerTask() {

			@Override
			public void run() {
				startNextActivity();
			}
		}, 3300);
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (timer1 != null)
			timer1.cancel();
	}

	public void startNextActivity() {
		animationFinished = true;
		Intent intent = new Intent(Splash.this, MainActivity.class);

		intent.putExtra(KEY_IS_FROM_NOTIFICATION, isFromNotification);

		Uri uri = getIntent().getData();
		if (uri != null) {
			DeepLinkController dlc = new DeepLinkController(this,
					uri.toString());
			if (dlc.isRelevant()) {
				intent.putExtra(KEY_DEEP_LINK_CONTROLLER, dlc);

			}
		}

		startActivity(intent);
		finish();
	}

	public void startLoginActivity() {
		animationFinished = true;
		Intent intent = new Intent(Splash.this, MximoLoginActivity.class);
		startActivity(intent);
		finish();
	}

}

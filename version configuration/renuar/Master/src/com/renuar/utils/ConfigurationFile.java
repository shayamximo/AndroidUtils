package com.renuar.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.renuar.App;

public class ConfigurationFile {

	public enum SplashType {
		SPLASH_DEFAULT_MXIMO_ANIMATION, SPLASH_CONFIGURED_ANIMATION, SPLASH_STATIC, SPLASH_VIDEO
	}

	private static ConfigurationFile configurationFile;
	public static final String ZOOZ_ID;
	public static final String ZOOZ_MASTER_ID;
	public static final Boolean IS_SANDBOX;
	public static final Boolean SHOW_LOGS;
	public static final Boolean SEND_FLURRY_EVENTS;
	public static final Integer APP_ADDRESS;
	public static Integer MXIMO_VIEW_APP_ADDRESS;
	public static final String FLURRY_KEY;
	public static final SplashType SPLASH_TYPE;
	public static final Boolean IS_LANGUAGE_CONFIGURABLE;

	public enum SUPPORTED_LANGUAGES {
		DEFAULT, ENGLISH_UK, DEUTCH, FRENCH
	}

	private static SUPPORTED_LANGUAGES getDeviceLanguage() {
		String deviceLanguage = Locale.getDefault().getLanguage();
		String deviceISO3Country = Locale.getDefault().getISO3Country();

		if (deviceLanguage.equals("fr"))
			return SUPPORTED_LANGUAGES.FRENCH;
		if (deviceLanguage.equals("de"))
			return SUPPORTED_LANGUAGES.DEUTCH;
		if ((deviceLanguage.equals("en") && (deviceISO3Country.equals("GBR"))))
			return SUPPORTED_LANGUAGES.ENGLISH_UK;

		return SUPPORTED_LANGUAGES.DEFAULT;
	}

	static {
		Resources resources = App.getApp().getResources();
		AssetManager assetManager = resources.getAssets();
		Properties properties = new Properties();
		// Read from the /assets directory
		try {
			InputStream inputStream = assetManager.open("config.properties");
			properties.load(inputStream);

		} catch (IOException e) {

		}

		ZOOZ_ID = (String) properties.get("zooz_id");
		ZOOZ_MASTER_ID = (String) properties.get("master_zooz_id");
		IS_SANDBOX = Boolean.parseBoolean(properties.getProperty("is_sandbox"));
		SHOW_LOGS = Boolean.parseBoolean(properties.getProperty("show_logs"));
		SEND_FLURRY_EVENTS = Boolean.parseBoolean(properties
				.getProperty("send_flurry_events"));
		FLURRY_KEY = (String) properties.get("flurry_key");

		IS_LANGUAGE_CONFIGURABLE = Boolean.parseBoolean(properties
				.getProperty("configure_address_by_device_language"));
		if (IS_LANGUAGE_CONFIGURABLE) {

			switch (getDeviceLanguage()) {
			case FRENCH:
				APP_ADDRESS = Integer
						.parseInt(properties.getProperty("french"));
				break;
			case DEUTCH:
				APP_ADDRESS = Integer
						.parseInt(properties.getProperty("deutch"));
				break;
			case ENGLISH_UK:
				APP_ADDRESS = Integer.parseInt(properties
						.getProperty("english_uk"));
				break;

			default:
				APP_ADDRESS = Integer.parseInt(properties
						.getProperty("app_address"));

				break;
			}
		} else {
			APP_ADDRESS = Integer.parseInt(properties
					.getProperty("app_address"));
		}
		MXIMO_VIEW_APP_ADDRESS = APP_ADDRESS;

		SPLASH_TYPE = configureSplashType(properties);

	}

	// precondition - only one of them is true.
	private static SplashType configureSplashType(Properties properties) {

		boolean isSplashConfiguredAnimation = Boolean.parseBoolean(properties
				.getProperty("splash_configured_animation"));
		boolean isSplashDefaulMximotAnimation = Boolean.parseBoolean(properties
				.getProperty("splash_default_mximo_animation"));
		boolean isSplashStatic = Boolean.parseBoolean(properties
				.getProperty("splash_static"));
		boolean isSplashVideo = Boolean.parseBoolean(properties
				.getProperty("splash_video"));

		if (isSplashConfiguredAnimation)
			return SplashType.SPLASH_CONFIGURED_ANIMATION;
		if (isSplashDefaulMximotAnimation)
			return SplashType.SPLASH_DEFAULT_MXIMO_ANIMATION;
		if (isSplashStatic)
			return SplashType.SPLASH_STATIC;
		if (isSplashVideo)
			return SplashType.SPLASH_VIDEO;

		return null;

	}

	private ConfigurationFile() {
	}

	public static ConfigurationFile getInstance() {
		if (configurationFile == null) {
			configurationFile = new ConfigurationFile();
		}

		return configurationFile;
	}

	public String getZoozId() {
		return ZOOZ_ID;
	}

	public Boolean getIsSandbox() {
		return IS_SANDBOX;
	}

	public Boolean getShowLogs() {
		return SHOW_LOGS;
	}

	public Boolean getSendFlurryEvents() {
		return SEND_FLURRY_EVENTS;
	}

	public Integer getAppAddress() {
		return (!isAppMximoView()) ? APP_ADDRESS : MXIMO_VIEW_APP_ADDRESS;
	}

	public String getFlurryKey() {
		return FLURRY_KEY;
	}

	public SplashType getSplashType() {
		return SPLASH_TYPE;
	}

	public static String getZoozMasterId() {
		return ZOOZ_MASTER_ID;
	}

	public boolean isLanguageConfigurable() {
		return IS_LANGUAGE_CONFIGURABLE;
	}

	public void restartAppInfoWithNewAddress(int address) {
		MXIMO_VIEW_APP_ADDRESS = address;
	}

	/**
	 * subtle point to realize: if the app is configured, in configuration file,
	 * to 0, APP_ADDRESS, WILL NOT BE MODIFIED ANYWHERE. It will always stay 0
	 * MXIMO_VIEW_APP_ADDRESS, is modifiable
	 */
	public boolean isAppMximoView() {
		return (APP_ADDRESS == 0);
	}
}

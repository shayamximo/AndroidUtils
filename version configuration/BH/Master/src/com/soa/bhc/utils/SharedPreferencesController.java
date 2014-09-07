package com.soa.bhc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import com.facebook.android.Facebook;
import com.soa.bhc.App;

public class SharedPreferencesController {

	public static final String TOKEN = "access_token";
	public static final String EXPIRES = "expires_in";
	public static final String API_KEY = "facebook-credentials";
	public static final String CHECKOUT_SCREEN_DETAILS = "check-out-screen-details";

	public static boolean saveFacebookCredentials(Facebook facebook) {
		Editor editor = App.getApp()
				.getSharedPreferences(API_KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public static boolean restoreFacebookCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = App.getApp()
				.getSharedPreferences(API_KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	public static boolean saveUserDetail(Context context, String key,
			String name) {
		Editor editor = context.getSharedPreferences(CHECKOUT_SCREEN_DETAILS,
				Context.MODE_PRIVATE).edit();
		editor.putString(key, name);
		return editor.commit();
	}

	public static void clearUserDetail(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CHECKOUT_SCREEN_DETAILS, Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}

	public static String getUserDetail(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CHECKOUT_SCREEN_DETAILS, Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, null);
		return value;
	}

	public static boolean saveUserDetail(Context context, int key, String name) {
		return saveUserDetail(context, Integer.toString(key), name);
	}

	public static String getUserDetail(Context context, int key) {
		return getUserDetail(context, Integer.toString(key));
	}

	// check if this is the first launch ever, if it is, then not only
	// return true, but make sure all future calls return false.
	public static boolean isFirstLaunch() {

		final String IS_FIRST_LAUNCH = "is_first_launch";
		final String KEY_IS_FIRST_LAUNCH = "key_is_first_launch";
		SharedPreferences sharedPreferences = App.getApp()
				.getSharedPreferences(IS_FIRST_LAUNCH, Context.MODE_PRIVATE);
		boolean value = sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
		if (value == true) {
			Editor editor = App
					.getApp()
					.getSharedPreferences(IS_FIRST_LAUNCH, Context.MODE_PRIVATE)
					.edit();
			editor.putBoolean(KEY_IS_FIRST_LAUNCH, false);
			editor.commit();
			return true;
		}
		return false;

	}

	final static String IS_USER_REGISTERED = "is_user_registered";
	final static String KEY_IS_USER_REGISTERED = "key_is_user_registered";

	public static void clearRegistration(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				IS_USER_REGISTERED, Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().commit();
	}

	public static boolean isUserRegistered() {

		SharedPreferences sharedPreferences = App.getApp()
				.getSharedPreferences(IS_USER_REGISTERED, Context.MODE_PRIVATE);
		boolean value = sharedPreferences.getBoolean(KEY_IS_USER_REGISTERED,
				false);
		return value;
	}

	public static void saveUserRegistered() {
		Editor editor = App.getApp()
				.getSharedPreferences(IS_USER_REGISTERED, Context.MODE_PRIVATE)
				.edit();
		editor.putBoolean(KEY_IS_USER_REGISTERED, true);
		editor.commit();
	}

	final static String CREDENTIALS = "credentials";
	final static String USER_NAME = "user_name";
	final static String PASSWORD = "password";

	public static void saveUserCredentials(String user, String password) {
		Editor editor = App.getApp()
				.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE).edit();
		editor.putString(USER_NAME, user);
		editor.putString(PASSWORD, password);
		editor.commit();
	}

	public static Pair<String, String> getUserCredentials() {
		SharedPreferences sharedPreferences = App.getApp()
				.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE);
		String user = sharedPreferences.getString(USER_NAME, null);
		String password = sharedPreferences.getString(PASSWORD, null);
		if (user != null && password != null)
			return new Pair<String, String>(user, password);

		return null;
	}

}

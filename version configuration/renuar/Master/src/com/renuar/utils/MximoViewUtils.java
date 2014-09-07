package com.renuar.utils;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.renuar.App;
import com.renuar.Splash;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.PromotionsStore;
import com.renuar.json.stores.TaxonomiesStore;

public class MximoViewUtils {

	/**
	 * 1. set the correct address in config file 2. delete all stores 3. delete
	 * 4. remove user details (for UserInfoViewController) database
	 */
	public static void restartApp(int address, Activity activity) {

		ConfigurationFile.getInstance().restartAppInfoWithNewAddress(address);

		InitInfoStore.getInstance().restartStore();
		TaxonomiesStore.getInstance().restartStore();
		PromotionsStore.getInstance().restartStore();
		App.getApp().restartCountriesAndCitiesStore();
		App.getApp().restartShopStore();
		App.getApp().restartSaleStore();

		App.getApp().getFavorite().removeAllCartItems();
		App.getApp().getCart().removeAllCartItems();

		SharedPreferencesController.clearUserDetail(App.getApp());
		SharedPreferencesController.clearRegistration(App.getApp());

		Intent intent = new Intent(activity, Splash.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
		activity.startActivity(intent);
	}

	public static boolean giveStagingOption() {
		File file = new File("/sdcard/mximo_qa");
		return file.exists();
	}
}

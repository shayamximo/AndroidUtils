package com.soa.bhc.utils;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.soa.bhc.App;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.Splash;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.PromotionsStore;
import com.soa.bhc.json.stores.TaxonomiesStore;

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
		File file = new File(GlobalConstants.QA_FILE_PATH);
		return file.exists();
	}
}

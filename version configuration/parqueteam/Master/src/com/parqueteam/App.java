/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.parqueteam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;

import com.parqueteam.json.AppInfo;
import com.parqueteam.json.stores.PromotionsStore;
import com.parqueteam.json.stores.SaleStore;
import com.parqueteam.json.stores.ShippingLocationStore;
import com.parqueteam.json.stores.ShopStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.userselection.UserSelectionStore;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.MximoLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class App extends Application {

	// DB
	public static final String DB_TABLE_CART = "tableCart";
	public static final String DB_TABLE_FAVORITE = "favoriteCart";
	private ShopStore shopStore;
	private ShippingLocationStore countryAndCitiesStore;
	private static App app;

	private Location myLocation = null;

	private UserSelectionStore cart;

	private UserSelectionStore favorite;

	private ArrayList<AppInfo> appInfoList;

	private SaleStore saleStore;

	public ShopStore getShopStore() {
		return shopStore;
	}

	public ShippingLocationStore getCountriesAndCitiesStore() {
		return countryAndCitiesStore;
	}

	public void restartShopStore() {
		shopStore = new ShopStore();
		shopStore.loadDataIfNotInitialized(new StoreLoadDataListener() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		});
	}

	public void restartSaleStore() {
		saleStore = new SaleStore();
		saleStore.loadDataIfNotInitialized(new StoreLoadDataListener() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		});
	}

	public void restartCountriesAndCitiesStore() {
		countryAndCitiesStore = new ShippingLocationStore();

		countryAndCitiesStore
				.loadDataIfNotInitialized(new StoreLoadDataListener() {

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
	}

	public UserSelectionStore getCart() {
		return cart;
	}

	public UserSelectionStore getFavorite() {
		return favorite;
	}

	public static App getApp() {
		return app;
	}

	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
			.cacheOnDisc(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();;

	public DisplayImageOptions getOptions() {
		return options;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	private ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate() {

		app = this;
		super.onCreate();
		ConfigurationFile.getInstance();
		PromotionsStore.getInstance().loadDataIfNotInitialized(
				new StoreLoadDataListener() {

					@Override
					public void onFinish() {

					}

					@Override
					public void onError() {

					}
				});

		restartCountriesAndCitiesStore();
		restartShopStore();
		restartSaleStore();

		cart = new UserSelectionStore(DB_TABLE_CART);
		favorite = new UserSelectionStore(DB_TABLE_FAVORITE);
		initImageLoader(getApplicationContext());

		airPushInit();

	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove
									// for
									// release
									// app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public void configureLanguage(String language) {
		Configuration config = new Configuration(getResources()
				.getConfiguration());
		config.locale = new Locale(language);
		getResources().updateConfiguration(config,
				getResources().getDisplayMetrics());
	}

	public Location getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(Location myLocation) {
		this.myLocation = myLocation;
	}

	private void airPushInit() {
		AirshipConfigOptions options = AirshipConfigOptions
				.loadDefaultOptions(this);

		UAirship.takeOff(this, options);

		PushManager.enablePush();

		String apid = PushManager.shared().getAPID();
		if (apid != null)
			MximoLog.d("apid", apid);
		else
			MximoLog.d("apid", "null");

		// use CustomPushNotificationBuilder to specify a custom layout
		CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();

		nb.statusBarIconDrawableId = R.drawable.icon;// custom status bar
		// icon

		nb.layout = R.layout.notification;
		nb.layoutIconDrawableId = R.drawable.icon;// custom layout icon
		nb.layoutIconId = R.id.icon;
		nb.layoutSubjectId = R.id.subject;
		nb.layoutMessageId = R.id.message;

		nb.constantNotificationId = 100;

		// customize the sound played when a push is received
		// nb.soundUri =
		// Uri.parse("android.resource://"+this.getPackageName()+"/"
		// +R.raw.cat);

		PushManager.shared().setNotificationBuilder(nb);
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
	}

	public ArrayList<AppInfo> getAppInfoList() {
		return appInfoList;
	}

	public void setAppInfoList(ArrayList<AppInfo> appInfoList) {
		this.appInfoList = appInfoList;
	}

	public SaleStore getSaleStore() {
		return saleStore;
	}

}
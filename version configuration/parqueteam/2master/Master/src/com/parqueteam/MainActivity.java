/*
 * Copyright (C) 2012 Muhammad Tayyab Akram <dear_tayyab@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parqueteam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.parqueteam.CustomizedViews.ReclickableTabHost;
import com.parqueteam.CustomizedViews.ReclickableTabHost.HomeTabClickedListener;
import com.parqueteam.category.NavDrawerItem;
import com.parqueteam.category.NavDrawerItemTabReplacement;
import com.parqueteam.category.NavDrawerListAdapter;
import com.parqueteam.category.NavDrawerListAdapterTabReplacement;
import com.parqueteam.json.helpers.ConfigurableTabInfo;
import com.parqueteam.json.helpers.TabInfo;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.OrderDetailsStore;
import com.parqueteam.json.stores.PromotionsStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.pagefragments.DialogExitFragment;
import com.parqueteam.pagefragments.FragmentRoot;
import com.parqueteam.pagefragments.MximoDialog.IOnUserChooseOption;
import com.parqueteam.userselection.UserSelectionStore.IOnAmountOfItemsInListChange;
import com.parqueteam.userselection.UserSelectionStore.IonItemsFinishedLoadingFromDataBase;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.FontsOverride;
import com.parqueteam.utils.MximoFlurryAgent;
import com.parqueteam.utils.SharedPreferencesController;

public class MainActivity extends FragmentActivity {

	public static final String CLASS_NAME_KEY = "class_name_key";
	public static int ZOOZ_ACTIVITY_ID = 12;
	public static int FACEBOOK_SHARE_ACTIVITY_ID = 13;
	private UiLifecycleHelper uiHelper;

	private ReclickableTabHost mTabHost;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private List<ArrayList<String>> navMenuTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<String> tabIds = new ArrayList<String>();
	private boolean openFragmentFromDrawer = false;
	Map<String, Integer> tabNameToTabNumber = new HashMap<String, Integer>();

	private boolean backFromSlidingMenu = false;
	private TextView titleOfSlidingActionBarTextView;

	private Double sumCostFromZooz = null;
	private String orderNumber = null;;

	public void onStart() {
		super.onStart();
		MximoFlurryAgent.onStartSession(this, ConfigurationFile.getInstance()
				.getFlurryKey());
	}

	public void onStop() {
		super.onStop();
		MximoFlurryAgent.onEndSession(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {

		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onResume() {

		super.onResume();
		com.facebook.AppEventsLogger.activateApp(this, getResources()
				.getString(R.string.facebook_app_id));

		if (SharedPreferencesController.isFirstLaunch()) {
			MximoFlurryAgent.logEvent(MximoFlurryAgent.FIRST_TIME_USAGE);
			// if this is first installation, send the device name
			// this is in try catch, in case some devices return null
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put(MximoFlurryAgent.DEVICE, getDeviceName());
				MximoFlurryAgent.logEvent(MximoFlurryAgent.DEVICE_MODEL, map);
			} catch (Exception e) {

			}
		}
		uiHelper.onResume();

	}

	public void hideTabs() {
		mTabHost.getTabWidget().setVisibility(View.GONE);
	}

	public void showTabs() {
		if (InitInfoStore.getInstance().isTabsVisible())
			mTabHost.getTabWidget().setVisibility(View.VISIBLE);
	}

	public void disableDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void enableDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	private void initSlidingMenuNoTabs() {

		hideTabs();

		List<TabInfo> tabInfoList = InitInfoStore.getInstance()
				.getTabsInformation();

		ArrayList<NavDrawerItemTabReplacement> navItemsList = new ArrayList<NavDrawerItemTabReplacement>();
		for (TabInfo tabInfo : tabInfoList) {
			navItemsList.add(new NavDrawerItemTabReplacement(tabInfo));
		}
		NavDrawerListAdapterTabReplacement nlatr = new NavDrawerListAdapterTabReplacement(
				navItemsList, this, mDrawerLayout, mDrawerList);

		mDrawerList.setAdapter(nlatr);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				if (titleOfSlidingActionBarTextView != null)
					titleOfSlidingActionBarTextView.setText(InitInfoStore
							.getInstance().getName());
			}

			public void onDrawerOpened(View drawerView) {
				if (titleOfSlidingActionBarTextView != null)
					titleOfSlidingActionBarTextView.setText(getResources()
							.getString(R.string.choose_category));
			}

		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	private void initActionBar() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		if (!InitInfoStore.getInstance().isSlidingMenu()) {
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			return;
		}

		mDrawerLayout.setScrimColor(getResources().getColor(
				R.color.black_quarter_transparacy));

		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		if (!InitInfoStore.getInstance().isTabsVisible()) {
			initSlidingMenuNoTabs();
			return;
		}

		navMenuTitles = TaxonomiesStore.getInstance()
				.getTaxonsTopLevelNamesSortedByIndex();

		navDrawerItems = new ArrayList<NavDrawerItem>();

		for (int i = 0; i < navMenuTitles.size(); i++) {
			ArrayList<String> currentIndedInTaxonsStore = navMenuTitles.get(i);
			for (int j = 0; j < currentIndedInTaxonsStore.size(); j++) {
				navDrawerItems.add(new NavDrawerItem(currentIndedInTaxonsStore
						.get(j), i, j));
			}
		}

		adapter = new NavDrawerListAdapter(this, navDrawerItems);

		mDrawerList.setAdapter(adapter);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				if (titleOfSlidingActionBarTextView != null)
					titleOfSlidingActionBarTextView.setText(InitInfoStore
							.getInstance().getName());
			}

			public void onDrawerOpened(View drawerView) {
				if (titleOfSlidingActionBarTextView != null)
					titleOfSlidingActionBarTextView.setText(getResources()
							.getString(R.string.choose_category));
			}

		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (InitInfoStore.getInstance().isSlidingMenu()) {
					if (tabId.equals(tabIds.get(0))) {
						FragmentRoot root = (FragmentRoot) getSupportFragmentManager()
								.findFragmentByTag(mTabHost.getCurrentTabTag());
						if (root != null) {
							int numberOfFragments = root
									.getSizeOfFragmentList();
							if (numberOfFragments == 1)
								mDrawerLayout
										.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
						}

					}
					// disable drawer, when switching tab.
					else
						mDrawerLayout
								.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				}

			}
		});
	}

	public FragmentRoot getMainFragmentRoot() {
		return (FragmentRoot) getSupportFragmentManager().findFragmentByTag(
				tabIds.get(0));
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, new StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
			}
		});
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Bundle bundle = getIntent().getExtras();
		boolean isFromNotification = false;
		DeepLinkController dlc = null;
		if (bundle != null) {
			isFromNotification = bundle.getBoolean(
					Splash.KEY_IS_FROM_NOTIFICATION, false);

			dlc = (DeepLinkController) bundle
					.getSerializable(Splash.KEY_DEEP_LINK_CONTROLLER);

		}

		setTabs(dlc);

		if (isFromNotification)
			switchTab(1);

		initActionBar();

		setBackFromSlidingMenu(false);
		setHomeTabClicked();
		configureDefaultFont();

	}

	private void configureDefaultFont() {

		String defaultFont = InitInfoStore.getInstance().getDefaultFont();

		// We add the condition of not mximo view,
		// because the font will overcome the whole app
		// and for now, i can't find a way to revert the font.
		if (defaultFont != null
				&& (!ConfigurationFile.getInstance().isAppMximoView()))
			FontsOverride.setDefaultFont(this, "SANS_SERIF", defaultFont
					+ ".ttf");

	}

	private void setHomeTabClicked() {
		mTabHost.setOnHomeTabClickedListener(new HomeTabClickedListener() {

			@Override
			public void onHomeTabClicked() {
				FragmentRoot root = (FragmentRoot) getSupportFragmentManager()
						.findFragmentByTag(mTabHost.getCurrentTabTag());
				int numberOfFragmentsToGoBack = root
						.getNumberOfFragmentsInStack() - 1;
				for (int i = 0; i < numberOfFragmentsToGoBack; i++)
					root.onBackPressed();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.main, menu);
		String joinAction = InitInfoStore.getInstance()
				.shouldShowJoinInHomePage();

		boolean isRegistered = SharedPreferencesController.isUserRegistered();

		// if (joinAction != null && (!isRegistered)) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.activity_main_actions, menu);
		// this.menu = menu;
		// }

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (InitInfoStore.getInstance().isSlidingMenu()) {
			// toggle nav drawer on selecting action bar app icon/title
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			// Handle action bar actions click
			switch (item.getItemId()) {

			case R.id.action_join:
				String joinAction = InitInfoStore.getInstance()
						.shouldShowJoinInHomePage();
				FragmentRoot root = (FragmentRoot) getSupportFragmentManager()
						.findFragmentByTag(mTabHost.getCurrentTabTag());
				root.openFragment(joinAction, null);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		} else
			return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (InitInfoStore.getInstance().isSlidingMenu())
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		if (InitInfoStore.getInstance().isSlidingMenu())
			mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public FragmentRoot getFragmentRoot() {
		FragmentRoot root = (FragmentRoot) getSupportFragmentManager()
				.findFragmentByTag(mTabHost.getCurrentTabTag());
		return root;
	}

	public void displayView(ArrayList<Integer> placeHolders, String title) {

		FragmentRoot root = (FragmentRoot) getSupportFragmentManager()
				.findFragmentByTag(mTabHost.getCurrentTabTag());

		boolean isLook = TaxonomiesStore.getInstance().isLook(placeHolders);

		if (isLook) {
			root.onCategoryChosen(placeHolders, null, title, false);

		} else {

			root.onCategoryChosen(placeHolders, null, title, false);
			openFragmentFromDrawer = true;

			// mDrawerList.setItemChecked(position, true);
			// mDrawerList.setSelection(position);
			mDrawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			setTitle(getResources().getString(R.string.app_name));
			mDrawerLayout.closeDrawer(mDrawerList);

		}

	}

	@Override
	public void onBackPressed() {
		Fragment f = getSupportFragmentManager().findFragmentByTag(
				mTabHost.getCurrentTabTag());
		if (f != null && f instanceof FragmentRoot) {
			FragmentRoot root = (FragmentRoot) f;
			if (root.onBackPressed()) {
				if (InitInfoStore.getInstance().isSlidingMenu())
					openDrawerIfNeccacaryFromBackButton(root);
				return;
			}
		}

		// super.onBackPressed();
		showExitMessage();
	}

	private void openDrawerIfNeccacaryFromBackButton(FragmentRoot root) {
		String tagOfRoot = root.getTag();
		String tabIdOfHome = tabIds.get(0);
		int numberOfFragments = root.getSizeOfFragmentList();

		if (tagOfRoot.equals(tabIdOfHome) && (numberOfFragments == 2)
				&& (openFragmentFromDrawer)) {
			// If this condition happens, then
			// we are coming back from one before the fragmenthomescreen

			mDrawerLayout.openDrawer(mDrawerList);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			openFragmentFromDrawer = false;
			setBackFromSlidingMenu(true);

		}

	}

	private String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	private void setTabs(DeepLinkController dlc) {

		mTabHost = (ReclickableTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);

		List<TabInfo> tabInfoList = InitInfoStore.getInstance()
				.getTabsInformation();
		tabNameToTabNumber = new HashMap<String, Integer>();
		int i = 0;
		for (TabInfo tabInfo : tabInfoList) {
			tabIds.add(tabInfo.tabName);
			addTab(tabInfo.tabName,
					tabInfo.tabTitle,
					tabInfo.tabTitle,
					createTabDrawable(tabInfo.imageResourceId,
							tabInfo.imageResourceIdSelected),
					tabInfo.className, dlc);
			tabNameToTabNumber.put(tabInfo.tabName, i);
			i++;
		}

	}

	public void switchTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	private Drawable createTabDrawable(int resId, int resIdSelected) {
		Resources res = getResources();
		StateListDrawable states = new StateListDrawable();

		final Options options = new Options();

		Bitmap icon = BitmapFactory.decodeResource(res, resId, options);
		Bitmap iconselected = BitmapFactory.decodeResource(res, resIdSelected,
				options);

		states.addState(new int[] { android.R.attr.state_selected },
				new BitmapDrawable(res, iconselected));

		states.addState(new int[] { android.R.attr.state_enabled },
				new BitmapDrawable(res, icon));

		return states;
	}

	private View createTabIndicator(ConfigurableTabInfo configurableTabInfo,
			String tabName, String label, Drawable drawable) {
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, mTabHost.getTabWidget(), false);

		TextView txtTitle = (TextView) tabIndicator
				.findViewById(R.id.text_view_tab_title);
		txtTitle.setText(label);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtTitle
				.getLayoutParams();
		txtTitle.setLayoutParams(params);

		ImageView imgIcon = (ImageView) tabIndicator
				.findViewById(R.id.image_view_tab_icon);
		imgIcon.setImageDrawable(drawable);

		setTabIndicatorListeners(tabName,
				(TextView) tabIndicator
						.findViewById(R.id.bottom_txt_notificaton));

		if (configurableTabInfo.backgroundColor != null) {
			tabIndicator.setBackgroundColor(Color
					.parseColor(configurableTabInfo.backgroundColor));
		}

		if (configurableTabInfo.seperatorColor != null) {
			View seperatorView = tabIndicator.findViewById(R.id.seperator_view);
			if (!label.equals("home"))
				seperatorView.setVisibility(View.VISIBLE);

		}
		if (configurableTabInfo.textColor != null) {
			txtTitle.setTextColor(Color
					.parseColor(configurableTabInfo.textColor));
		}

		if (configurableTabInfo.font != null) {

			Typeface font = Typeface.createFromAsset(App.getApp().getAssets(),
					configurableTabInfo.font + ".ttf");
			txtTitle.setTypeface(font);
		}

		return tabIndicator;
	}

	private void updateOffersTab(final TextView tabNotificationView,
			final int numberOfPromotions) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (numberOfPromotions > 0) {
					tabNotificationView.setVisibility(View.VISIBLE);
					tabNotificationView.setText(Integer
							.toString(numberOfPromotions));
				}

				else
					tabNotificationView.setVisibility(View.GONE);
			}
		});
	}

	private void updateUserSelectionTab(final TextView tabNotificationView,
			final int numberOfCartItems) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (numberOfCartItems > 0) {
					tabNotificationView.setVisibility(View.VISIBLE);
					tabNotificationView.setText(Integer
							.toString(numberOfCartItems));
				}

				else
					tabNotificationView.setVisibility(View.GONE);
			}
		});
	}

	// This is not such a nice solution, a
	// way should be thought of in the future where it's run by the fragment
	// that
	// deals with cart and not from the main activity
	// this is not good OO apprauch.
	private void setTabIndicatorListeners(String tabName,
			final TextView tabNotificationView) {
		if (tabName.equals("cart")) {
			App.getApp()
					.getCart()
					.setOnItemsChangeListener(
							new IOnAmountOfItemsInListChange() {

								@Override
								public void onItemsAmountChange(
										final int numberOfCartItems) {
									updateUserSelectionTab(tabNotificationView,
											numberOfCartItems);

								}
							});

			App.getApp()
					.getCart()
					.setOnItemsFinishedLoadingFromDataBase(
							new IonItemsFinishedLoadingFromDataBase() {

								@Override
								public void onItemsFinishedLoadingFromDataBase(
										int numberOfCartItems) {
									updateUserSelectionTab(tabNotificationView,
											numberOfCartItems);

								}
							});
		}

		if (tabName.equals("wishlist")) {
			App.getApp()
					.getFavorite()
					.setOnItemsChangeListener(
							new IOnAmountOfItemsInListChange() {

								@Override
								public void onItemsAmountChange(
										final int numberOfCartItems) {
									updateUserSelectionTab(tabNotificationView,
											numberOfCartItems);

								}
							});

			App.getApp()
					.getFavorite()
					.setOnItemsFinishedLoadingFromDataBase(
							new IonItemsFinishedLoadingFromDataBase() {

								@Override
								public void onItemsFinishedLoadingFromDataBase(
										int numberOfCartItems) {
									updateUserSelectionTab(tabNotificationView,
											numberOfCartItems);

								}
							});
		}

		if (tabName.equals("offers")) {
			PromotionsStore.getInstance().loadDataIfNotInitialized(
					new StoreLoadDataListener() {

						@Override
						public void onFinish() {
							updateOffersTab(tabNotificationView,
									PromotionsStore.getInstance()
											.getPromotionsList().size());
						}

						@Override
						public void onError() {

						}
					});
		}

	}

	private void addTab(String tabName, String label, String tag,
			Drawable drawable, String className, DeepLinkController dlc) {

		Bundle args = new Bundle();
		args.putString(CLASS_NAME_KEY, className);
		if ((tabName.toLowerCase()).equals("home") && dlc != null
				&& dlc.isRelevant()) {
			args.putSerializable(Splash.KEY_DEEP_LINK_CONTROLLER, dlc);
		}

		ConfigurableTabInfo configurableTabInfo = InitInfoStore.getInstance()
				.getConfigurableTabInfo();
		mTabHost.addTab(
				mTabHost.newTabSpec(tabName).setIndicator(
						createTabIndicator(configurableTabInfo, tabName, label,
								drawable)), FragmentRoot.class, args);
	}

	private void showExitMessage() {
		DialogExitFragment dialogExitFragment = new DialogExitFragment();
		dialogExitFragment.setCancelable(false);
		dialogExitFragment.setiOnUserChooseOption(new IOnUserChooseOption() {

			@Override
			public void onProceed() {
				finish();
			}

			@Override
			public void onCancel() {

			}
		});
		dialogExitFragment.show(this.getFragmentManager(),
				GlobalConstants.EMPTY_STRING);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MainActivity.ZOOZ_ACTIVITY_ID) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				MximoFlurryAgent
						.logEvent(MximoFlurryAgent.ZOOZ_TRANSACTION_SUCCEEDED);
				App.getApp().getCart().emptySelectionStore();
				Toast.makeText(this,
						getResources().getString(R.string.order_successful),
						Toast.LENGTH_LONG).show();

				if (sumCostFromZooz != null && orderNumber != null) {
					Bundle bundle = data.getExtras();
					String transactionID = bundle
							.getString("com.zooz.android.lib.ZOOZ_TRANSACTION_ID");

					List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();

					nameValuePairsList.add(new BasicNameValuePair(
							"order[transaction_id]", transactionID));

					nameValuePairsList.add(new BasicNameValuePair(
							"order[amount]", sumCostFromZooz.toString()));
					OrderDetailsStore metadataDetails = new OrderDetailsStore(
							nameValuePairsList, orderNumber);
					metadataDetails
							.loadDataIfNotInitialized(new StoreLoadDataListener() {

								@Override
								public void onFinish() {
									sumCostFromZooz = null;
									orderNumber = null;
								}

								@Override
								public void onError() {
									sumCostFromZooz = null;
									orderNumber = null;

								}
							});

				}

				break;
			case Activity.RESULT_CANCELED:
				MximoFlurryAgent
						.logEvent(MximoFlurryAgent.ZOOZ_TRANSACTION_FAILED);
				Toast.makeText(this,
						getResources().getString(R.string.order_cancelled),
						Toast.LENGTH_LONG).show();

				break;
			}
		}

		if (requestCode == MainActivity.FACEBOOK_SHARE_ACTIVITY_ID) {
			uiHelper.onActivityResult(requestCode, resultCode, data,
					new FacebookDialog.Callback() {
						@Override
						public void onError(
								FacebookDialog.PendingCall pendingCall,
								Exception error, Bundle data) {

						}

						@Override
						public void onComplete(
								FacebookDialog.PendingCall pendingCall,
								Bundle data) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(MainActivity.this,
											"Shared Successfully.",
											Toast.LENGTH_SHORT).show();

								}
							});

						}
					});
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public UiLifecycleHelper getUiHelper() {
		return uiHelper;
	}

	public boolean isBackFromSlidingMenu() {
		return backFromSlidingMenu;
	}

	public void setBackFromSlidingMenu(boolean backFromSlidingMenu) {
		this.backFromSlidingMenu = backFromSlidingMenu;
	}

	public void setTitleOfSlidingActionBarTextView(
			TextView titleOfSlidingActionBarTextView) {
		this.titleOfSlidingActionBarTextView = titleOfSlidingActionBarTextView;
	}

	public Integer getTabNumberByName(String name) {
		return tabNameToTabNumber.get(name);
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setSumCostFromZooz(double sumCostFromZooz) {
		this.sumCostFromZooz = sumCostFromZooz;
	}

}
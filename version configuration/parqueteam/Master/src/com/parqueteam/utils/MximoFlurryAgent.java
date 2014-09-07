package com.parqueteam.utils;

import java.util.Map;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.parqueteam.GlobalConstants;

public class MximoFlurryAgent {

	// Constants for events
	public static final String HOME_PAGE_VIEWED = "Home Page Viewed";
	public static final String PUSH_NOTIFICATION_RECIVED = "Push Notification Received";
	public static final String USER_PERFORMED_SEARCH = "User performed Search";
	public static final String USER_NAVIGATED_TO_STORE = "User Navigated to Store";
	public static final String CATEGORY_PAGE_VIEWED = "Category Page Viewed";
	public static final String LOOK_PAGE_VIEWED = "Look Page Viewed";
	public static final String GRID_PAGE_VIEWED = "Grid Page Viewed";
	public static final String ITEM_PAGE_VIEWED = "Item Page Viewed";
	public static final String ADD_TO_BAG_CLICKED = "Add to bag clicked";
	public static final String ADD_TO_BAG_ACTION = "Add to bag action";
	public static final String CART_PAGE_VIEWED = "Cart Page Viewed";
	public static final String USER_INFO_PAGE_VIEWED = "User info Page Viewed";
	public static final String SHIPPING_INFO_PAGE_VIEWED = "Shipping info Page Viewed";
	public static final String ZOOZ_PAGE_VIEWED = "Zooz Page Viewed";
	public static final String ZOOZ_TRANSACTION_SUCCEEDED = "Zooz transaction succeeded";
	public static final String ZOOZ_TRANSACTION_FAILED = "Zooz transaction failed";
	public static final String SHARE_ITEM_CLICKED = "Share item clicked";
	public static final String DIAL_CLICKED = "Dial Clicked";
	public static final String FIRST_TIME_USAGE = "First Time Usage";
	public static final String DEVICE_MODEL = "User Device";
	public static final String BUY_AFFILIATE_CLICK = "buy affiliate clicked";
	// Constants for keys
	public static final String CATEGORY = "category";
	public static final String SKU = "sku";
	public static final String SHARE_ITEM_TYPE = "type";
	public static final String DEVICE = "Device";

	public static void onStartSession(Context context, String key) {
		if (ConfigurationFile.getInstance().getSendFlurryEvents())
			FlurryAgent.onStartSession(context, key);
	}

	public static void onEndSession(Context context) {
		if (ConfigurationFile.getInstance().getSendFlurryEvents())
			FlurryAgent.onEndSession(context);
	}

	public static void logEvent(String event) {
		if (ConfigurationFile.getInstance().getSendFlurryEvents())
			FlurryAgent.logEvent(event);
	}

	public static void logEvent(String event, Map<String, String> args) {
		if (ConfigurationFile.getInstance().getSendFlurryEvents())
			FlurryAgent.logEvent(event, args);
	}
}

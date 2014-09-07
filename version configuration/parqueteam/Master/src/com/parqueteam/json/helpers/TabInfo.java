package com.parqueteam.json.helpers;

import java.util.Locale;

import android.util.Pair;

import com.parqueteam.GlobalConstants;
import com.parqueteam.R;
import com.parqueteam.json.stores.InitInfoStore;

public class TabInfo {

	public final String tabName;
	public final String className;
	public final String tabTitle;
	public final String tabImage;
	public final int imageResourceId;
	public final int imageResourceIdSelected;
	public static final String HOME_SCREEN_CATEGORY_COLLAPSIBLE = "FragmentHomeScreenCollapsibleCategory";
	public static final String HOME_SCREEN_CATEGORY = "FragmentHomeScreenCategory";

	public enum PageName {

		OFFERS, CART, FACEBOOK, SOCIAL, ACCOUNT, WISHLIST, STORES, BLOG, ONSALE, SHOP, SEARCH
	}

	public TabInfo(String tabName, String className, String tabTitle,
			String tabImage) {

		if (!tabName.equals("home")) {
			this.tabName = tabName;
			this.className = className;
			this.tabTitle = tabTitle;
			this.tabImage = tabImage;
			Pair<Integer, Integer> tabImages = getTabImageResourceId();
			imageResourceId = tabImages.first;
			imageResourceIdSelected = tabImages.second;
		} else {
			this.tabName = "home";
			this.className = ((InitInfoStore.getInstance()
					.isCollapsibleCategory()) ? HOME_SCREEN_CATEGORY_COLLAPSIBLE
					: HOME_SCREEN_CATEGORY);

			if (tabTitle == null)
				this.tabTitle = "home";
			else
				this.tabTitle = tabTitle;

			this.tabImage = null;
			imageResourceId = R.drawable.tabbar_home;
			imageResourceIdSelected = R.drawable.tabbar_home_selected;
		}

	}

	private Pair<Integer, Integer> getTabImageResourceId() {
		// if (tabImage != null) {
		// String uri = "drawable/" + tabImage;
		// return App.getApp().getResources()
		// .getIdentifier(uri, null, App.getApp().getPackageName());
		// }

		// else {
		PageName pageName = PageName.valueOf(tabName
				.toUpperCase(Locale.ENGLISH));
		switch (pageName) {

		case ACCOUNT:
			return new Pair<Integer, Integer>(R.drawable.tabbar_more,
					R.drawable.tabbar_more_selected);
		case CART:
			return new Pair<Integer, Integer>(R.drawable.tabbar_bag,
					R.drawable.tabbar_bag_selected);
		case FACEBOOK:
			return new Pair<Integer, Integer>(R.drawable.tabbar_facebook,
					R.drawable.tabbar_facebook_selected);
		case SOCIAL:
			return new Pair<Integer, Integer>(R.drawable.tabbar_facebook,
					R.drawable.tabbar_facebook_selected);
		case OFFERS:
			return new Pair<Integer, Integer>(R.drawable.tabbar_offers,
					R.drawable.tabbar_offers_selected);
		case WISHLIST:
			return new Pair<Integer, Integer>(R.drawable.tabbar_wishlist,
					R.drawable.tabbar_wishlist_selected);

		case STORES:
			return new Pair<Integer, Integer>(R.drawable.tabbar_stores,
					R.drawable.tabbar_stores_selected);

		case BLOG:
			return new Pair<Integer, Integer>(R.drawable.tabbar_terms,
					R.drawable.tabbar_terms_selected);

		case ONSALE:
			return new Pair<Integer, Integer>(R.drawable.tabbar_sale,
					R.drawable.tabbar_sale_selected);

		case SHOP:
			return new Pair<Integer, Integer>(R.drawable.tabbar_bag,
					R.drawable.tabbar_bag_selected);

		case SEARCH:
			return new Pair<Integer, Integer>(R.drawable.tab_search,
					R.drawable.tab_search_sel);

		}
		// }

		return null;
	}

}

package com.parqueteam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import com.parqueteam.json.stores.TaxonomiesStore;

import android.app.Activity;
import android.net.Uri;

@SuppressWarnings("serial")
public class DeepLinkController implements Serializable {

	private String deepLinkMessage;

	public DeepLinkController(Activity activity, String url) {

		initActionForDeepLinking(activity, url);

	}

	private String initActionForDeepLinking(Activity activity, String uri) {

		String data = uri.toString();

		String https = activity.getResources().getString(R.string.https)
				+ "://";
		String host = activity.getResources().getString(R.string.host);
		String pathPrefix = activity.getResources().getString(
				R.string.path_prefix);
		String fullUrl = https + host + pathPrefix + "/?";

		if (data.contains(fullUrl)) {
			deepLinkMessage = data.replace(fullUrl, "");
		}

		return deepLinkMessage;

	}

	public boolean isRelevant() {
		return (deepLinkMessage != null);
	}

	public void handleDeepLinkEvent(MainActivity mainActivity) {
		Integer tabNumber = mainActivity.getTabNumberByName(deepLinkMessage);
		if (tabNumber != null)
			mainActivity.switchTab(tabNumber);
		// as of now, it can only be category
		else if (verifyCategory()) {
			String[] categoryTreeStringArray = (deepLinkMessage.replace(
					"category/", "")).split("/");
			ArrayList<String> categoryTreeStringArrayList = new ArrayList<String>(
					Arrays.asList(categoryTreeStringArray));

			ArrayList<Integer> placeHolders = TaxonomiesStore.getInstance()
					.getPlaceHoldersByStringForCollapsibleCategory(
							categoryTreeStringArrayList);

			mainActivity.getFragmentRoot().onCategoryChosen(placeHolders, null,
					"", true);

		}
	}

	private boolean verifyCategory() {
		String testedString = deepLinkMessage.substring(0, 8);
		return testedString.equals("category");
	}

}

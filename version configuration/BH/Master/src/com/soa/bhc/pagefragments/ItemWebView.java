package com.soa.bhc.pagefragments;

import android.os.Bundle;

public class ItemWebView extends MximoWebViewFragment {

	String url;
	String title;

	public static final String URL_FOR_ITEM = "url_for_item";
	public static final String TITLE_ITEM = "title_item";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Bundle args = getArguments();
		url = args.getString(URL_FOR_ITEM);
		title = args.getString(TITLE_ITEM);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected String getUrlOfPage() {
		return url;
	}

	@Override
	protected String getTitle() {
		return title;
	}

}

package com.parqueteam.pagefragments;

import android.os.Bundle;

public class WebViewController extends MximoWebViewFragment {

	private final String EXTRA_ADDITION_TO_RENDER_PDF_FILE = "http://docs.google.com/viewer?embedded=true&url=";

	@Override
	protected String getUrlOfPage() {
		String url = getValueOfParamFromName("url");
		if (url.contains(".pdf"))
			url = EXTRA_ADDITION_TO_RENDER_PDF_FILE + url;
		return url;

	}

	@Override
	protected String getTitle() {
		Bundle bundle = getArguments();
		String titlePassedByArgument = bundle
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		if (titlePassedByArgument != null)
			return titlePassedByArgument;
		return getValueOfParamFromName("title");
	}

}

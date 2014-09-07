package com.soa.bhc.pagefragments;

public class FragmentPathChosenFromPromotions extends MximoWebViewFragment {

	public static final String URL_OF_CHOSEN_PATH = "url_of_chosen_path";
	public static final String TITLE_OF_CHOSEN_PROMOTION = "title_of_chosen_promotion";

	@Override
	protected String getUrlOfPage() {
		return (String) getArguments().get(URL_OF_CHOSEN_PATH);

	}

	@Override
	protected String getTitle() {
		return (String) getArguments().get(TITLE_OF_CHOSEN_PROMOTION);
	}

}

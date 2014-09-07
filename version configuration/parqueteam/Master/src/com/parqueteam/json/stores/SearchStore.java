package com.parqueteam.json.stores;

import com.parqueteam.json.Taxon;
import com.parqueteam.utils.ConfigurationFile;

public class SearchStore extends ProductsStore {

	public static enum SEARCH_TYPE {
		RATING, SALE, QUERY, PRICE_RANGE, CREATED_DAYS_AGO
	}

	SEARCH_TYPE searchType = null;
	int createdDaysAgo = -1;
	int fromPrice = -1;
	int toPrice = -1;
	String query;

	public SEARCH_TYPE getSearchType() {
		return searchType;
	}

	protected SearchStore() {

	}

	public SearchStore(SEARCH_TYPE searchType) {
		this.searchType = searchType;
	}

	public SearchStore(int createdDaysAgo) {

		this.createdDaysAgo = createdDaysAgo;
		this.searchType = SEARCH_TYPE.CREATED_DAYS_AGO;
	}

	public SearchStore(int fromPrice, int toPrice) {
		searchType = SEARCH_TYPE.PRICE_RANGE;
		this.fromPrice = fromPrice;
		this.toPrice = toPrice;
	}

	public SearchStore(String query) {
		searchType = SEARCH_TYPE.QUERY;
		this.query = query;
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "https://beta.mximo.com/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/products?" + getGetPrams();
	}

	private String getGetPrams() {

		switch (searchType) {

		case PRICE_RANGE:
			return "&price_from=" + fromPrice + "&price_to=" + toPrice;
		case QUERY:
			return "&q=" + query;
		case RATING:
			return "&by_rating=true";
		case SALE:
			return "&on_sale=true";
		case CREATED_DAYS_AGO:
			return "created_days_ago=" + createdDaysAgo;
		}
		return null;
	}
}

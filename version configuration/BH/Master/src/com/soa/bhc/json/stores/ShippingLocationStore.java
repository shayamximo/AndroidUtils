package com.soa.bhc.json.stores;

import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.annotations.SerializedName;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.json.ShippingLocation;
import com.soa.bhc.utils.ConfigurationFile;

public class ShippingLocationStore extends Store {

	@SerializedName("countries")
	private List<ShippingLocation> countryList;

	@Override
	protected Store getStore() {
		return this;
	}

	public List<ShippingLocation> getCountryList() {
		return countryList;
	}

	public List<ShippingLocation> getStatesByCountryId(int id) {
		for (ShippingLocation cos : countryList) {
			if (cos.getId().equals(id))
				return cos.getStates();
		}
		return null;
	}

	public int getCountryIdByName(String countryName) {
		for (ShippingLocation country : countryList) {
			if (countryName.equals(country.getName()))
				return country.getId();
		}
		// This shouldn't happen if given a valid country name.
		return -1;

	}

	@Override
	protected void setStore(Store store) {
		ShippingLocationStore countriesAndCitiesStore = (ShippingLocationStore) store;
		this.countryList = countriesAndCitiesStore.countryList;
	}

	@Override
	protected String getUrl() {

		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/countries?per_page=999";
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

}

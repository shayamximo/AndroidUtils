package com.renuar.json.stores;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.annotations.SerializedName;
import com.renuar.GlobalConstants;
import com.renuar.json.Shop;
import com.renuar.utils.ConfigurationFile;

public class ShopStore extends Store implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8665261441720384004L;
	private static final String SHOPS_TAG = "shops_tag";
	private boolean isListInitialized;

	@SerializedName(SHOPS_TAG)
	private List<Shop> shopsList;

	@Override
	protected Store getStore() {
		return this;
	}

	@Override
	protected void setStore(Store store) {
		ShopStore shopStore = (ShopStore) store;
		this.shopsList = shopStore.shopsList;
		isListInitialized = false;
	}

	@Override
	protected String getUrl() {
		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/stores";
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	protected Store createInstanceFromJson(String jsonString) {

		return super.createInstanceFromJson("{" + SHOPS_TAG + ":" + jsonString
				+ "}");
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

	public List<Shop> getShopsList() {
		return shopsList;
	}

	public int getShopPositionByName(String name) {
		int i = 0;
		for (Shop shop : shopsList) {
			if (shop.getName().equals(name))
				return i;
			i++;
		}
		return -1;
	}

	public void sortShopList() {
		Collections.sort(shopsList);
		isListInitialized = true;
	}

	public boolean isListInitialized() {
		return isListInitialized;
	}

}

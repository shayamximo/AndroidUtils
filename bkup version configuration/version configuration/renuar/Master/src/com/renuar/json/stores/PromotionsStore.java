package com.renuar.json.stores;

import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.annotations.SerializedName;
import com.renuar.GlobalConstants;
import com.renuar.json.Promotion;
import com.renuar.utils.ConfigurationFile;

public class PromotionsStore extends Store {

	private static PromotionsStore promotionsStore;

	private IOnPromotionsDataFinishedLoading iOnPromotionsDataFinishedLoading;

	private boolean promotionsFinishedLoading = false;

	// this is just a "workaround", because the server returns a json with no
	// tag
	private static final String PROMOTIONS_TAG = "promotions_tag";

	@SerializedName(PROMOTIONS_TAG)
	private List<Promotion> promotions;

	private PromotionsStore() {

	}

	public List<Promotion> getPromotionsList() {
		return promotions;
	}

	public static PromotionsStore getInstance() {

		if (promotionsStore == null) {
			promotionsStore = new PromotionsStore();
		}
		return promotionsStore;
	}

	@Override
	protected Store getStore() {
		return promotionsStore;
	}

	@Override
	protected void setStore(Store store) {
		this.promotionsStore = (PromotionsStore) store;
	}

	@Override
	protected String getUrl() {
		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/promotions";
	}

	public void setIOnPromotionsDataFinishedLoading(
			IOnPromotionsDataFinishedLoading iOnPromotionsDataFinishedLoading) {
		this.iOnPromotionsDataFinishedLoading = iOnPromotionsDataFinishedLoading;
	}

	public interface IOnPromotionsDataFinishedLoading {
		public void onPromotionsDataFinishDownloading();
	}

	protected Store createInstanceFromJson(String jsonString) {

		return super.createInstanceFromJson("{" + PROMOTIONS_TAG + ":"
				+ jsonString + "}");
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

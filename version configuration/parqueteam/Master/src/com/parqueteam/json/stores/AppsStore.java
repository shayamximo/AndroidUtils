package com.parqueteam.json.stores;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.annotations.SerializedName;
import com.parqueteam.GlobalConstants;
import com.parqueteam.json.AppInfo;

public class AppsStore extends Store {

	public final String APPS_STORE_TAG = "apps_store_tag";
	private String user;
	private String password;

	public AppsStore(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@SerializedName(APPS_STORE_TAG)
	private List<AppInfo> appInfoList;

	@Override
	protected Store getStore() {
		return this;
	}

	@Override
	protected void setStore(Store store) {
		AppsStore appStore = (AppsStore) store;
		this.appInfoList = appStore.appInfoList;
	}

	@Override
	protected String getUrl() {
		return GlobalConstants.PREFIX + "/static/apps";

	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.POST;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		ArrayList<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(new BasicNameValuePair("email", user));
		nameValuePairList.add(new BasicNameValuePair("password", password));
		nameValuePairList.add(new BasicNameValuePair("format", "json"));
		nameValuePairList.add(new BasicNameValuePair("app_id", "2"));

		return nameValuePairList;
	}

	@Override
	protected Store createInstanceFromJson(String jsonString) {

		return super.createInstanceFromJson("{" + APPS_STORE_TAG + ":"
				+ jsonString + "}");
	}

	public List<AppInfo> getAppInfoList() {

		return appInfoList;
	}

}

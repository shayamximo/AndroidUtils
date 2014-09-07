package com.parqueteam.json.stores;

import java.util.List;

import org.apache.http.NameValuePair;

import com.parqueteam.GlobalConstants;
import com.parqueteam.utils.ConfigurationFile;

public class RegisterStore extends Store {

	private List<NameValuePair> nameValueParams;

	public RegisterStore(List<NameValuePair> nameValueParams) {
		this.nameValueParams = nameValueParams;
	}

	@Override
	protected Store getStore() {
		return this;
	}

	@Override
	protected void setStore(Store store) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getUrl() {
		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/users" + ".json";
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.POST;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return nameValueParams;
	}
}

package com.soa.bhc.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Tab {

	@SerializedName("id")
	private int id;

	@SerializedName("params")
	private List<Params> Params;

	public List<Params> getParams() {
		return Params;
	}

	public void setParams(List<Params> params) {
		Params = params;
	}
}

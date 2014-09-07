package com.parqueteam.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class View {

	@SerializedName("ids")
	private String ids;

	@SerializedName("name")
	private String name;

	@SerializedName("params")
	private List<Params> params;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Params> getParams() {
		return params;
	}

	public void setParams(List<Params> params) {
		this.params = params;
	}

	public Params getParamsByName(String name) {

		for (Params param : params) {
			if (param.getName().equals(name))
				return param;
		}
		return null;
	}

}

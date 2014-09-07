package com.soa.bhc.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Page {

	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;

	@SerializedName("params")
	private List<Params> params;

	@SerializedName("views")
	private List<View> views;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public List<View> getViews() {
		return views;
	}

	public void setViews(List<View> views) {
		this.views = views;
	}

	public View getViewByName(String name) {
		for (View view : views) {

			if (name.equals(view.getName()))
				return view;
		}
		return null;
	}

	public Params getParamsByName(String name) {
		for (Params param : params) {

			if (name.equals(param.getName()))
				return param;
		}
		return null;
	}

	public String getValueByName(String name) {
		Params params = getParamsByName(name);
		if (params != null)
			return params.getValue();

		return null;
	}

	public String getClassName() {
		Params params = getParamsByName("class_name");
		if (params != null)
			return params.getValue();
		return null;

	}

}

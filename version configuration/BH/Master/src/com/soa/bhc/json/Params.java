package com.soa.bhc.json;

import com.google.gson.annotations.SerializedName;

public class Params {

	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;

	@SerializedName("value")
	private String value;

	@SerializedName("ui")
	private String ui;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

}

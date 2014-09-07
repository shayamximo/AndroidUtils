package com.renuar.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ShippingLocation {

	@SerializedName("name")
	protected String name;

	@SerializedName("id")
	private Integer id;

	@SerializedName("states")
	private List<ShippingLocation> states;

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public List<ShippingLocation> getStates() {
		return states;
	}
}

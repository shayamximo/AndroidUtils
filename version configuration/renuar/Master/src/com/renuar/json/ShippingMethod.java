package com.renuar.json;

import com.google.gson.annotations.SerializedName;

public class ShippingMethod {

	@SerializedName("cost")
	private double cost;

	@SerializedName("currency")
	private String currency;

	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;

	@SerializedName("select_branch")
	private Boolean selectBranch;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

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

	public Boolean getSelectBranch() {
		if (selectBranch == null)
			return false;
		return selectBranch;
	}

	public void setSelectBranch(Boolean selectBranch) {
		this.selectBranch = selectBranch;
	}
}

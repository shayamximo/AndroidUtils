package com.parqueteam.json;

import com.google.gson.annotations.SerializedName;

public class LineItem {

	@SerializedName("id")
	protected int id;

	@SerializedName("price")
	protected double price;

	@SerializedName("quantity")
	protected int quantity;

	@SerializedName("variant")
	protected Variant variant;

	public int getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public Variant getVariant() {
		return variant;
	}

}

package com.parqueteam.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Variant extends Product {

	public Variant(int id, String name, String sku, double price) {
		super(id, name, sku, price);
	}

	@SerializedName("option_values")
	protected List<OptionValues> optionValues;

	@SerializedName("is_master")
	boolean isMaster;

	public List<OptionValues> getOptionValues() {
		return optionValues;
	}

	public boolean isVariantMaster() {
		return isMaster;
	}

}

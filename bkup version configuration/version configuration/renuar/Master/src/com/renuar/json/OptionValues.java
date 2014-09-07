package com.renuar.json;

import com.google.gson.annotations.SerializedName;

public class OptionValues implements Comparable<OptionValues> {

	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;

	@SerializedName("presentation")
	private String presentation;

	@SerializedName("option_type_name")
	private String optionTypeName;

	public String getName() {
		return name;
	}

	public String getOptionTypeValue() {
		return optionTypeName;
	}

	public String getOptionTypeName() {
		return optionTypeName;
	}

	@Override
	public int compareTo(OptionValues another) {
		return (optionTypeName.compareTo(another.getOptionTypeName()));
	}

}

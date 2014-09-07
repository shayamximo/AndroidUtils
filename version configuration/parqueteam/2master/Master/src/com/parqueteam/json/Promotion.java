package com.parqueteam.json;

import com.google.gson.annotations.SerializedName;

public class Promotion {

	@SerializedName("fine_print")
	protected String finePrint;

	@SerializedName("id")
	protected int id;

	@SerializedName("name")
	protected String name;

	@SerializedName("description")
	protected String description;

	@SerializedName("image_url")
	protected String imageUrl;

	@SerializedName("path")
	protected String path;

	@SerializedName("position")
	protected int position;

	@SerializedName("event_name")
	protected String eventName;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getPath() {
		return path;
	}

	public int getPosition() {
		return position;
	}

	public String getDescription() {
		return description;
	}

	public String getFinePrint() {
		return finePrint;
	}

	public String getEventName() {
		return eventName;
	}

}

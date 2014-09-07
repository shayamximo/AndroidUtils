package com.renuar.json;

import com.google.gson.annotations.SerializedName;

public class Shop implements Comparable<Shop> {

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("longitude")
	private double longitude;

	@SerializedName("name")
	private String name;

	@SerializedName("street")
	private String street;

	@SerializedName("id")
	private int id;

	@SerializedName("hours")
	private String hours;

	@SerializedName("city")
	private String city;

	@SerializedName("phone")
	private String phone;

	@SerializedName("pin_image")
	private String pinImage;

	private double distanceFromCurrentLocation;

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getStreet() {
		return street;
	}

	public String getName() {
		return name;
	}

	public String getDistanceFromCurrentLocation() {
		return distanceFromCurrentLocation + "km";
	}

	public void setDistanceFromCurrentLocation(
			double distanceFromCurrentLocation) {
		this.distanceFromCurrentLocation = distanceFromCurrentLocation;
	}

	public String getHours() {
		return hours;
	}

	public String getCity() {
		return city;
	}

	public String getPhone() {
		return phone;
	}

	public String getPinImage() {
		return pinImage;
	}

	@Override
	public int compareTo(Shop arg0) {
		// TODO Auto-generated method stub
		if (this.distanceFromCurrentLocation < arg0.distanceFromCurrentLocation)
			return -1;
		return 1;
	}

}

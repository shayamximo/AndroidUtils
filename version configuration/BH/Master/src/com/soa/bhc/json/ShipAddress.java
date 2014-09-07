package com.soa.bhc.json;

import com.google.gson.annotations.SerializedName;

public class ShipAddress {

	@SerializedName("id")
	protected int id;

	@SerializedName("firstname")
	protected String firstName;

	@SerializedName("lastname")
	protected String lastName;

	@SerializedName("address1")
	protected String address;

	@SerializedName("city")
	protected String city;

	@SerializedName("phone")
	protected String phone;

	@SerializedName("zipcode")
	protected String zipcode;

	@SerializedName("country_id")
	protected String countryId;

	@SerializedName("state_id")
	protected String stateId;

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getPhone() {
		return phone;
	}

	public String getZipcode() {
		return zipcode;
	}

	public String getCountryId() {
		return countryId;
	}

	public String getStateId() {
		return stateId;
	}

}

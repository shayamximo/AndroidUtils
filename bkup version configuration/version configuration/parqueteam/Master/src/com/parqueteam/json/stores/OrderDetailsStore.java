package com.parqueteam.json.stores;

import java.io.Serializable;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.parqueteam.GlobalConstants;
import com.parqueteam.json.LineItem;
import com.parqueteam.json.ShipAddress;
import com.parqueteam.json.ShippingMethod;
import com.parqueteam.utils.ConfigurationFile;

public class OrderDetailsStore extends Store implements Serializable,
		Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8267291608816306932L;

	@SerializedName("exception")
	protected String exception;

	@SerializedName("total")
	protected double total;

	@SerializedName("adjustment_total")
	protected double adjustmentTotal;

	@SerializedName("number")
	private String number;

	@SerializedName("shipping_methods")
	protected List<ShippingMethod> shippingMethodsList;

	@SerializedName("line_items")
	protected List<LineItem> lineItemsList;

	@SerializedName("ship_address")
	protected ShipAddress shipAddress;

	@SerializedName("email")
	private String email;

	private boolean isActualOrder;

	private String orderNumber;

	private List<NameValuePair> postParams;

	public OrderDetailsStore(List<NameValuePair> postParams) {
		this.postParams = postParams;
		this.isActualOrder = true;
	}

	public OrderDetailsStore(List<NameValuePair> postParams, String orderNumber) {
		this.postParams = postParams;
		this.isActualOrder = false;
		this.orderNumber = orderNumber;

	}

	@Override
	protected Store getStore() {
		return this;
	}

	@Override
	protected void setStore(Store store) {
		OrderDetailsStore orderDetailsStore = (OrderDetailsStore) store;
		this.shippingMethodsList = orderDetailsStore.shippingMethodsList;
		this.number = orderDetailsStore.number;
		this.total = orderDetailsStore.total;
		this.lineItemsList = orderDetailsStore.lineItemsList;
		this.shipAddress = orderDetailsStore.shipAddress;
		this.email = orderDetailsStore.email;
		this.adjustmentTotal = orderDetailsStore.adjustmentTotal;
		this.exception = orderDetailsStore.exception;
	}

	@Override
	protected String getUrl() {
		if (isActualOrder) {
			return GlobalConstants.PREFIX + "/apps/"
					+ ConfigurationFile.getInstance().getAppAddress()
					+ "/api/orders.json";
		} else {
			return GlobalConstants.PREFIX + "/apps/"
					+ ConfigurationFile.getInstance().getAppAddress()
					+ "/api/orders/" + orderNumber + ".json";

		}

	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		if (isActualOrder)
			return REQUEST_TYPE.POST;
		else
			return REQUEST_TYPE.PUT;
	}

	@Override
	protected List<NameValuePair> getPostParams() {

		return postParams;
	}

	public List<ShippingMethod> getShippingMethodsList() {
		return shippingMethodsList;
	}

	public Double getTotal() {
		return total;
	}

	public String getNumber() {
		return number;
	}

	public List<LineItem> getLineItemsList() {
		return lineItemsList;
	}

	public ShipAddress getShipAddress() {
		return shipAddress;
	}

	public String getEmail() {
		return email;
	}

	public double getAdjustmentTotal() {
		return adjustmentTotal;

	}

	public String getException() {
		return exception;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}

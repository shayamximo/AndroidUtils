package com.renuar.json.stores;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.renuar.json.Product;

public abstract class ProductsStore extends Store {

	@SerializedName("products")
	private List<Product> productsList;

	protected Store getStore() {
		return this;
	}

	protected void setStore(Store store) {
		this.productsList = ((ProductsStore) store).getProducts();
	}

	@Override
	protected Pair<String, String> getHeader() {
		return new Pair<String, String>("Accept", "application/json");
	}

	public List<Product> getProducts() {
		return productsList;
	}

	public void setProducts(List<Product> productsList) {
		this.productsList = productsList;
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

}

package com.renuar.json.stores;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;

import com.google.gson.annotations.SerializedName;
import com.renuar.utils.MximoLog;

public class ExchangeRateStore extends Store {

	@SerializedName("rate")
	private Double rate;

	private String fromCurrency;
	private String toCurrency;

	public ExchangeRateStore(String fromCurrency, String toCurrency) {
		this.fromCurrency = fromCurrency;
		this.toCurrency = toCurrency;

	}

	@Override
	protected Store getStore() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	protected void setStore(Store store) {
		ExchangeRateStore ers = (ExchangeRateStore) store;
		this.rate = ers.rate;

	}

	@Override
	protected String getUrl() {

		String url = "http://rate-exchange.appspot.com/currency?from="
				+ fromCurrency + "&to=" + toCurrency;
		MximoLog.d("priv", url);
		return url;
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {

		return null;
	}

	public double getRate() {
		return rate;
	}

}

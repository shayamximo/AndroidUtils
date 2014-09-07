package com.soa.bhc.comm;

public abstract class Request {

	public abstract void onFinish(String jsonResponse);

	public abstract void onError();

}

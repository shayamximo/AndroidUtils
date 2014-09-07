package com.soa.bhc.json.helpers;

public class ItemPageParams {

	public String actionLabel;
	public String action;
	public boolean showThumbTitle;

	public ItemPageParams() {
	}

	public boolean isOrder() {
		return (action != null && action.equals("order"));
	}

}

package com.renuar.json.helpers;

import com.renuar.App;
import com.renuar.R;

public class NavBarParams {

	public String textColor;
	public String navBarBackgroundColor;
	public String titleLogoUrl;
	public String font;
	private boolean isLightActionBar;

	public NavBarParams() {
		textColor = null;
		navBarBackgroundColor = null;
		titleLogoUrl = null;
		font = null;
		isLightActionBar = false;
	}

	public boolean isLightActionBar() {
		return isLightActionBar;
	}

	public void setLightActionBar(boolean isLightActionBar) {
		this.isLightActionBar = isLightActionBar;
		if (isLightActionBar == true)
			textColor = App.getApp().getResources()
					.getString(R.color.actionbar_light_text_color);

	}

}

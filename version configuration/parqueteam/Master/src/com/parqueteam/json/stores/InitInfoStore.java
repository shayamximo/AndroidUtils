package com.parqueteam.json.stores;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.parqueteam.GlobalConstants;
import com.parqueteam.json.Page;
import com.parqueteam.json.Params;
import com.parqueteam.json.Tab;
import com.parqueteam.json.View;
import com.parqueteam.json.helpers.ConfigurableTabInfo;
import com.parqueteam.json.helpers.HomeScreenImageDetails;
import com.parqueteam.json.helpers.ItemPageParams;
import com.parqueteam.json.helpers.NavBarParams;
import com.parqueteam.json.helpers.TabInfo;
import com.parqueteam.pagefragments.YouTubeViewController.YoutubeRowInfo;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.MximoLog;
import com.parqueteam.utils.UtilityFunctions;

public class InitInfoStore extends Store {

	private static final String HOME_KEY = "home";

	private static final String SHOW_NAV_BAR_KEY = "show_navbar";
	private static final String BRAND_COLOR_KEY = "brand_color";
	private static final String NAVBAR_LOGO_NAME_KEY = "navbar_logo_name";
	private static final String URL_KEY = "url";
	private static final String FRAME_KEY = "frame";
	private static final String CATEGORY_KEY = "category";
	private static final String TYPE_KEY = "type";
	private static final String IMAGE = "image";

	private static final String CLASS_NAME_KEY = "class_name";
	public static String CLASS_NAME_CATEGORY_KEY = "category";
	private static final String ACTION_DATA = "action_data";

	private List<Pair<String, String>> bindingList;

	@SerializedName("language")
	private String language;

	@SerializedName("google_qr")
	private String googleQr;

	@SerializedName("share_title")
	private String shareTitle;

	@SerializedName("share_message")
	private String shareMessage;

	@SerializedName("terms_and_conditions")
	private String terms;

	@SerializedName("promotion_share_message")
	private String promotionShareMessage;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("pages")
	private List<Page> pages;

	@SerializedName("tabs")
	private List<Tab> tabs;

	@SerializedName("currency")
	private String currency;

	@SerializedName("orders_email")
	private String ordersEmail;

	private double currencyRateMultiplier;

	private static InitInfoStore initInfo = null;

	String fromCurrencyCode;
	String ToCurrencyCode;

	private InitInfoStore() {
		super();
	}

	@Override
	protected Store getStore() {
		return initInfo;
	}

	public String getCurrency() {
		return currency;
	}

	@Override
	protected void setStore(Store store) {
		this.initInfo = (InitInfoStore) store;
		// This is just because on server side, it will send "he" instead of
		// "iw"
		if (initInfo.language.equals("he"))
			initInfo.language = GlobalConstants.HEBREW;
		getInstance().initiateBindings();
		getInstance().initiateMultiCurrenct();

	}

	public boolean shouldShowMultiCurrency() {

		if (fromCurrencyCode.equals(ToCurrencyCode)) {
			return false;
		}

		return getSpecificBooleanInPage("general", "multi_currency", false);
	}

	private void initiateMultiCurrenct() {
		fromCurrencyCode = InitInfoStore.getInstance().getCurrency();
		ToCurrencyCode = Currency.getInstance(Locale.getDefault())
				.getCurrencyCode();
		boolean isMultiCur = shouldShowMultiCurrency();
		if (!isMultiCur) {
			return;
		}

		else {
			final ExchangeRateStore ers = new ExchangeRateStore(
					fromCurrencyCode, ToCurrencyCode);
			ers.loadDataIfNotInitialized(new StoreLoadDataListener() {

				@Override
				public void onFinish() {
					currencyRateMultiplier = ers.getRate();
				}

				@Override
				public void onError() {
					MximoLog.d("", "");

				}
			});
		}
	}

	@Override
	protected String getUrl() {

		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "?device_name=android&format=json";

	}

	public static InitInfoStore getInstance() {

		if (initInfo == null) {
			initInfo = new InitInfoStore();
		}
		return initInfo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public Page getPageByName(String name) {

		for (Page page : pages) {
			if (page.getName().equals(name))
				return page;
		}
		return null;
	}

	public List<Params> getPageParamsByName(String name) {

		try {
			return getPageByName(name).getParams();
		} catch (NullPointerException nullPointerException) {
			return null;
		}

	}

	public String shouldShowJoinInHomePage() {
		Page homePage = getPageByName(HOME_KEY);
		if (homePage != null) {
			Params params = homePage.getParamsByName("action_label");
			if (params != null && params.getValue().equals("JOIN")) {
				return homePage.getParamsByName("action").getValue();
			}
		}

		return null;
	}

	public ArrayList<HomeScreenImageDetails> getHomeScreenImageDetails() {

		ArrayList<HomeScreenImageDetails> homeScreenImages = new ArrayList<HomeScreenImageDetails>();
		List<View> viewList = getPageByName(HOME_KEY).getViews();

		int i = 0;
		for (View view : viewList) {
			Params params = view.getParamsByName(TYPE_KEY);

			if ((params != null) && (params.getValue().equals(IMAGE))) {
				String url = view.getParamsByName(URL_KEY).getValue();
				String frameString = view.getParamsByName(FRAME_KEY).getValue();
				HomeScreenImageDetails homeScreenImage = new HomeScreenImageDetails(
						url, frameString, i);
				homeScreenImages.add(homeScreenImage);
			}
			i++;

		}

		return homeScreenImages;
	}

	public String getBrandColor() {
		String ret = null;

		Page homePage = getPageByName(HOME_KEY);
		ret = homePage.getParamsByName(BRAND_COLOR_KEY).getValue();
		return ("#" + ret);
	}

	public String getNavBarLogoUrl() {
		String ret = null;

		Params navBarParams = getPageByName(HOME_KEY).getParamsByName(
				NAVBAR_LOGO_NAME_KEY);
		if (navBarParams != null)
			ret = navBarParams.getValue();
		return ret;
	}

	public String getNavBarAction() {
		String ret = null;

		Params navBarParams = getPageByName(HOME_KEY).getParamsByName("action");
		if (navBarParams != null)
			ret = navBarParams.getValue();
		return ret;
	}

	public String getCollapsibleCategoriesStringKey() {
		Params params = getPageByName(HOME_KEY).getParamsByName(
				"show_categories");

		if (params != null)
			return params.getValue();
		else
			return null;
	}

	public List<Params> getCategoryParams() {
		return (getPageByName(HOME_KEY).getViewByName(CATEGORY_KEY).getParams());
	}

	public List<Params> getCategoryRowParams() {
		return (getPageByName(CATEGORY_KEY).getParams());
	}

	public boolean isCollapsibleCategory() {
		List<Params> categoryParams = getCategoryParams();
		for (Params params : categoryParams) {
			if (params.getName().equals(TYPE_KEY)) {
				if (params.getValue().equals("collapsible_category"))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public boolean shouldHideSearchBarInCollapsibleView() {
		List<Params> categoryParams = getCategoryParams();
		for (Params params : categoryParams) {
			if (params.getName().equals("hide_searchbar")) {
				return Boolean.parseBoolean(params.getValue());
			}
		}
		return false;
	}

	public boolean shouldHideLabels() {
		List<Params> categoryParams = getCategoryParams();
		for (Params params : categoryParams) {
			if (params.getName().equals("hide_labels")) {
				return Boolean.valueOf(params.getValue());
			}
		}
		return false;
	}

	public String getCategoryParamsByName(String name) {
		return getPageByName(HOME_KEY).getViewByName(CATEGORY_KEY)
				.getParamsByName(name).getValue();
	}

	public String getDefaultFont() {

		return getSpecificStringInPage("general", "font");
	}

	public String getActionButtonColor() {
		return getSpecificStringInPage("general", "action_button_color");
	}

	public String getBoldFont() {

		return getSpecificStringInPage("general", "bold_font");
	}

	public boolean isCategoryDrillDown() {
		return getSpecificBooleanInPage("general", "category_drilldown", false);

	}

	private boolean getSpecificBooleanInPage(String pageName, String field,
			boolean ifNull) {
		String boolInString = getSpecificStringInPage(pageName, field);
		if (boolInString != null)
			return Boolean.parseBoolean(boolInString);
		return ifNull;
	}

	private String getSpecificStringInPage(String pageName, String field) {
		Page generalPage = getPageByName(pageName);
		if (generalPage != null) {
			List<Params> generalParamsList = generalPage.getParams();
			for (Params params : generalParamsList) {
				String name = params.getName();
				if (name.equals(field))
					return params.getValue();
			}
		}

		return null;
	}

	public ConfigurableTabInfo getConfigurableTabInfo() {
		ConfigurableTabInfo configurableTabInfo = new ConfigurableTabInfo();

		Page generalPage = getPageByName("general");
		if (generalPage != null) {
			List<Params> configurableTabParamsList = getPageByName("general")
					.getParams();

			for (Params params : configurableTabParamsList) {
				String value = params.getValue();
				String name = params.getName();

				if (name.equals("tabbar_background_color"))
					configurableTabInfo.backgroundColor = "#" + value;
				if (name.equals("tabbar_text_color"))
					configurableTabInfo.textColor = "#" + value;
				if (name.equals("tabbar_seperator_color"))
					configurableTabInfo.seperatorColor = value;
				if (name.equals("tabbar_font"))
					configurableTabInfo.font = value;

			}
		}

		return configurableTabInfo;
	}

	public List<TabInfo> getTabsInformation() {

		ArrayList<TabInfo> retArrTabInfo = new ArrayList<TabInfo>();
		for (Tab tab : tabs) {
			String value = tab.getParams().get(0).getValue();

			Page page = getPageByName(value);
			String classKey = page.getName();
			Params paramsTabTitle = page.getParamsByName("title");
			Params paramsTabImage = page.getParamsByName("tab_image");

			retArrTabInfo
					.add(new TabInfo(value,
							(classKey != null ? classKey : null),
							(paramsTabTitle != null ? paramsTabTitle.getValue()
									: null),
							(paramsTabImage != null ? paramsTabImage.getValue()
									: null)));

		}

		return retArrTabInfo;
	}

	public String getFacebookUrl() {
		return getPageByName("facebook").getParamsByName("url").getValue();
	}

	public List<String> getRowsInAccount() {
		ArrayList<String> rowsList = new ArrayList<String>();

		Page accountPage = getPageByName("account");
		for (Params params : accountPage.getParams()) {
			if (params.getName().equals("row"))
				rowsList.add(params.getValue());
		}
		return rowsList;
	}

	public String getPhoneNumber() {
		return getPageByName("item").getParamsByName(ACTION_DATA).getValue();
	}

	public String getName() {
		return name;
	}

	public String getShareTitle() {
		return shareTitle;
	}

	public String getShareMessage() {
		return shareMessage;
	}

	public String getGoogleQr() {
		return googleQr;
	}

	public String getPromotionShareMessage() {
		return promotionShareMessage;
	}

	public String getTerms() {
		return terms;
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

	public String getCurrencySymbol() {

		return UtilityFunctions.getCurrencySymbol(currency);
	}

	public boolean doesApplicationHaveCart() {
		for (Tab tab : tabs) {
			if (tab.getParams().get(0).getValue().equals("cart"))
				return true;
		}
		return false;
	}

	public String getLanguage() {
		return language;
	}

	public boolean isAppLanguageHebrew() {
		return (language.equals(GlobalConstants.HEBREW));
	}

	public boolean isSlidingMenu() {

		return getSpecificBooleanInPage("general", "show_menubar", false);
	}
	
	public boolean isTabsVisible()
	{
		return getSpecificBooleanInPage("general", "show_tabbar", true);
	}

	public void initiateBindings() {
		Page homePage = getPageByName(HOME_KEY);
		bindingList = new ArrayList<Pair<String, String>>();
		List<Params> paramsList = homePage.getParams();

		for (Params params : paramsList) {
			if (params.getName().equals("bind")) {
				String[] stringArrayOfBindingValues = params.getValue().split(
						":");
				Pair<String, String> pair = new Pair<String, String>(
						stringArrayOfBindingValues[0],
						stringArrayOfBindingValues[1]);
				bindingList.add(pair);
			}
		}

		Page generalPage = getPageByName("general");
		if (generalPage == null)
			return;
		List<Params> generalParams = generalPage.getParams();

		for (Params params : generalParams) {
			if (params.getName().equals("bind")
					&& (!params.getValue().equals(""))) {
				String[] stringArrayOfBindingValues = params.getValue().split(
						":");
				Pair<String, String> pair = new Pair<String, String>(
						stringArrayOfBindingValues[0],
						stringArrayOfBindingValues[1]);
				bindingList.add(pair);
			}
		}
	}

	public Page getBindingPageByName(String bindingName) {
		String pageName = getBindingValueByName(bindingName);
		if (pageName != null) {
			return getPageByName(pageName);
		}
		return null;
	}

	private String getBindingValueByName(String bindingName) {

		for (Pair<String, String> pair : bindingList) {
			if (pair.first.equals(bindingName))
				return pair.second;
		}
		return null;
	}

	public ItemPageParams getItemPageParams() {
		Page itemPage = getPageByName("item");
		if (itemPage == null)
			return null;
		List<Params> paramsList = itemPage.getParams();
		ItemPageParams ipp = new ItemPageParams();

		for (Params params : paramsList) {
			String name = params.getName();
			String value = params.getValue();
			if (name.equals("action_label"))
				ipp.actionLabel = value;
			if (name.equals("action"))
				ipp.action = value;
			if (name.equals("show_thumb_title"))
				ipp.showThumbTitle = Boolean.parseBoolean(value);
		}

		return ipp;

	}

	public NavBarParams getConfigurableNavBarParams() {
		NavBarParams navBarParams = new NavBarParams();

		Page generalPage = getPageByName("general");
		if (generalPage != null) {
			List<Params> configurableTabParamsList = generalPage.getParams();

			for (Params params : configurableTabParamsList) {
				String name = params.getName();
				String value = params.getValue();
				// stupidly configured, this is the way it comes from parquetim,
				// just set
				// it manually to white.
				if (name.equals("navbar_tint") && value.equals("dark"))
					navBarParams.textColor = "#FFFFFF";

				if (name.equals("navbar_color"))
					navBarParams.navBarBackgroundColor = "#" + value;
				if (name.equals("navbar_tint"))
					navBarParams
							.setLightActionBar((value.equals("light") ? true
									: false));
				if (name.equals("navbar_title_logo"))
					navBarParams.titleLogoUrl = value;
				if (name.equals("navbar_font"))
					navBarParams.font = value;

			}
		}

		return navBarParams;
	}

	public String getOrdersEmail() {
		return ordersEmail;
	}

	public Pair<String, String> getRegularAndBoldFonts() {
		Page generalPage = getPageByName("general");

		// First is regular, second is bold

		List<Params> paramsList = generalPage.getParams();

		String first = null;
		String second = null;

		for (Params params : paramsList) {
			String name = params.getName();
			String value = params.getValue();

			if (name.equals("font"))
				first = value + ".ttf";
			if (name.equals("bold_font"))
				second = value + ".ttf";

		}

		if (first == null || second == null)
			return null;

		Pair<String, String> fonts = new Pair<String, String>(first, second);

		return fonts;
	}

	public boolean shouldShowPromotionsOnShipInfo() {
		Page itemPage = getPageByName("shipinfo");
		List<Params> paramsList = itemPage.getParams();
		for (Params params : paramsList) {
			String name = params.getName();
			String value = params.getValue();
			if (name.equals("show_promotions")) {
				if (value.equals("true"))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public String getFullClassNameByKey(String fragmentKey) {

		Page page = getPageByName(fragmentKey);

		if (page != null) {
			return GlobalConstants.PAGE_FRAGMENTS_PACKAGE
					+ getPageByName(fragmentKey)
							.getParamsByName(CLASS_NAME_KEY).getValue();
		}
		return null;
	}

	public boolean showNavBar() {

		Page homePage = getPageByName(HOME_KEY);
		Params params = homePage.getParamsByName(SHOW_NAV_BAR_KEY);
		if (params == null)
			return false;
		return Boolean.parseBoolean(params.getValue());
	}

	public double getCurrencyMultiplier() {
		return currencyRateMultiplier;
	}

	public String getBackgroundUrlForView() {
		List<View> viewList = getPageByName(HOME_KEY).getViews();
		for (View view : viewList) {
			if (view.getName().equals("background"))

			{
				Params params = view.getParamsByName("url");
				return params.getValue();
			}
		}
		return null;
	}
	
	public boolean showDiscountBadge()
	{
		return getSpecificBooleanInPage("general", "show_discount_badge", false);
	}
	
	private ArrayList<YoutubeRowInfo> youtubeRowInfoList;

	public ArrayList<YoutubeRowInfo> getYoutubeRowInfoList() {
		return youtubeRowInfoList;
	}

	public void setYoutubeRowInfoList(ArrayList<YoutubeRowInfo> youtubeRowInfoList) {
		this.youtubeRowInfoList = youtubeRowInfoList;
	}

}

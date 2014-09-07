package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parqueteam.App;
import com.parqueteam.GlobalConstants;
import com.parqueteam.MainActivity;
import com.parqueteam.R;
import com.parqueteam.json.OptionValues;
import com.parqueteam.json.Product;
import com.parqueteam.json.Variant;
import com.parqueteam.json.helpers.ConfigurableTabInfo;
import com.parqueteam.json.helpers.ItemPageParams;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.pagefragments.MximoDialog.IOnUserChooseOption;
import com.parqueteam.userselection.UserSelectionItem;
import com.parqueteam.userselection.UserSelectionStore;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.HackyUtils;
import com.parqueteam.utils.MximoFlurryAgent;
import com.parqueteam.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FullCommerceItemViewController extends FragmentPageBase implements
		OnClickListener {

	// Constants
	private static final int SIZE_OF_SPACE_BETWEEN_DOTS = 8;
	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 220;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private static final int HEIGHT_OF_TEXT_VIEW_IN_VARIANT_OPTIONS = 50;
	private static final int EXTRA_WIDTH_FOR_TEXT_VIEW_IN_VARIANT_OPTIONS = 60;
	private static final int MAX_NUMBER_OF_LINES_IN_INFO_TEXT = 8;
	private boolean proceedingToFavoritesPage = false;

	// The main part of this fragment is the scrollView which contains 90
	// percent of it.
	private ScrollView mainScrollView;
	private LinearLayout layoutOfDots;

	// These are for miscellanous things (metadata/facebook/rating)
	private ViewFlipper viewFlipper;
	private GestureDetector gestureDetector;

	private ArrayList<HorizontalScrollView> otherVariantsList = new ArrayList<HorizontalScrollView>();

	private Product master;

	private int positionOfProductInTaxon;
	private ArrayList<Integer> placeHoldersOfTaxon;
	private boolean showOnlyChosenProduct;
	private boolean fromFavoritesPage;
	private boolean fromSearchPage;
	private int positionInCartStore;

	private View wishListAdd;

	private UserSelectionStore cart;

	private UserSelectionStore favorite;

	private OnClickListener onClickOrder = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (isOrderPage()) {
				String textToShare = new StringBuilder()
						.append(getResources().getString(
								R.string.glad_to_get_details)).append("<br/>")
						.append(master.getName()).append("<br/>")

						.append(master.getDescription()).append("<br/><br/>")
						.toString();

				ArrayList<String> urlsToAttatch = new ArrayList<String>();
				urlsToAttatch.add(master.getMainImageUrl());

				UtilityFunctions.sendEmail(getActivity(), textToShare,
						urlsToAttatch,
						getResources().getString(R.string.glad_to_get_details),
						InitInfoStore.getInstance().getOrdersEmail());

			} else {
				MximoFlurryAgent.logEvent(MximoFlurryAgent.ADD_TO_BAG_CLICKED);
				setDialogslayout();
			}

		}
	};

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.full_commerce_item_view_layout;
	}

	@Override
	public void onResume() {

		super.onResume();
		fragmentRoot.hideTabs();
	}

	private void initDialogForCheckout() {

		LinearLayout bottomBuyButton = (LinearLayout) viewOfFragment
				.findViewById(R.id.buy_bottom_screen);
		// Special horrible workaround for very specific requirment of parquetim
		if (isOrderPage()) {

			ImageView imageOfBuy = (ImageView) bottomBuyButton
					.findViewById(R.id.image_view_buy_button);
			TextView textOfPlus = (TextView) bottomBuyButton
					.findViewById(R.id.textview_plus_of_buy);
			TextView textOfBuy = (TextView) bottomBuyButton
					.findViewById(R.id.textview_of_buy);

			int dpPadding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
							.getDisplayMetrics());

			bottomBuyButton.setBackgroundColor(getResources().getColor(
					R.color.black));
			bottomBuyButton.setPadding(dpPadding * 2, dpPadding * 2,
					dpPadding * 2, dpPadding * 2);
			textOfBuy.setBackgroundResource(R.drawable.variantoption_mail_to);
			textOfBuy.setPadding((dpPadding * 80), (dpPadding * 10),
					(dpPadding * 80), (dpPadding * 10));

			ConfigurableTabInfo configurableTabInfo = InitInfoStore
					.getInstance().getConfigurableTabInfo();
			if (configurableTabInfo.font != null) {
				Typeface font = Typeface.createFromAsset(App.getApp()
						.getAssets(), configurableTabInfo.font + ".ttf");
				textOfBuy.setTypeface(font);

			}

			bottomBuyButton.removeView(imageOfBuy);
			bottomBuyButton.removeView(textOfPlus);

			textOfBuy.setText("Mail To");

			ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) textOfBuy
					.getLayoutParams();
			mlp.leftMargin = 0;

			LayoutParams lp = (LayoutParams) textOfBuy.getLayoutParams();
			lp.gravity = Gravity.CENTER;

			textOfBuy.setLayoutParams(lp);
			textOfBuy.setGravity(Gravity.CENTER);
		}
		bottomBuyButton.setOnClickListener(onClickOrder);

	}

	void outOfStockMessage() {
		String appname = InitInfoStore.getInstance().getName();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(appname);
		builder.setMessage(getResources().getString(R.string.item_out_of_stock))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private View getRelevantVariantsList(int numberOfRows) {
		return otherVariantsList.get(numberOfRows - 1);
	}

	private void setDialogslayout() {

		int numberOfVariantRows = otherVariantsList.size();
		Variant variant = null;
		if (numberOfVariantRows > 0) {
			View currentlyTestedView = otherVariantsList
					.get(numberOfVariantRows - 1);
			boolean areAllVariantsChecked = (Boolean) currentlyTestedView
					.getTag(R.id.TAG_HSV_IN_ITEM_PAGE);

			if (!areAllVariantsChecked) {
				UtilityFunctions.setHighLightAnimation(currentlyTestedView);
				int[] location = new int[2];
				currentlyTestedView.getLocationOnScreen(location);
				mainScrollView.smoothScrollTo(0, location[1]);
				return;
			}

			variant = (Variant) currentlyTestedView
					.getTag(R.id.TAG_HSV_CHOSEN_VARIANT);
			if (variant.isOutOfStock()) {
				outOfStockMessage();
				return;
			}

		}

		// it's a master only
		else {
			if (master.isOutOfStock()) {
				outOfStockMessage();
				return;
			}

		}

		Map<String, String> map = new HashMap<String, String>();
		map.put(MximoFlurryAgent.SKU, master.getSku());
		MximoFlurryAgent.logEvent(MximoFlurryAgent.ADD_TO_BAG_ACTION, map);

		UserSelectionStore userSelectionStore = cart;

		userSelectionStore.addCartItem(master, variant);

		AddToBagDialogFragment AddToBagDialogFragment = new AddToBagDialogFragment() {

			@Override
			public void onButtonContinueClicked() {
				dismiss();
				fragmentRoot.onBackPressed();

			}

			@Override
			public void onButtonCheckoutClicked() {
				dismiss();
				((MainActivity) getActivity()).switchTab(2);
				fragmentRoot.onBackPressed();

			}
		};
		AddToBagDialogFragment.setCancelable(false);

		AddToBagDialogFragment.show(getActivity().getFragmentManager(),
				GlobalConstants.EMPTY_STRING);

	}

	private void initializeDotView(int size) {
		layoutOfDots.removeAllViews();

		if (size > 1) {
			for (int i = 0; i < size; i++) {
				final ImageView dot = new ImageView(getActivity());
				LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(SIZE_OF_SPACE_BETWEEN_DOTS, 0, 0, 0);
				if (i == 0) {
					dot.setBackgroundResource(R.drawable.dot_sel);
				} else {
					dot.setBackgroundResource(R.drawable.dot);
				}
				layoutOfDots.addView(dot, params);

			}
		}
	}

	private void setDotIndicator(int location) {
		int numberOfDots = layoutOfDots.getChildCount();
		if (numberOfDots > 0) {
			for (int i = 0; i < numberOfDots; i++) {
				ImageView img = (ImageView) layoutOfDots.getChildAt(i);
				img.setBackgroundResource(R.drawable.dot);
			}
			ImageView img = (ImageView) layoutOfDots.getChildAt(location);
			img.setBackgroundResource(R.drawable.dot_sel);
		}
	}

	private void setInfoButtonSelector(int i) {
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
				.setSelected(false);
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
				.setSelected(false);
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
				.setSelected(false);

		switch (i) {
		case 1:
			(viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
					.setSelected(true);
			viewFlipper.setDisplayedChild(0);
			break;
		case 2:
			(viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
					.setSelected(true);
			viewFlipper.setDisplayedChild(1);
			break;

		case 3:
			(viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
					.setSelected(true);
			viewFlipper.setDisplayedChild(2);
			break;

		default:
			break;
		}

	}

	private ViewPager initializePagerAndDotsView(List<String> urlsOfImages,
			boolean isOutOfStock) {
		ViewPager pager = (ViewPager) viewOfFragment
				.findViewById(R.id.comm_item_viewPagerr);
		pager.removeAllViews();

		pager.setAdapter(new ImagePagerAdapter(urlsOfImages, inflator,
				isOutOfStock));

		layoutOfDots = (LinearLayout) viewOfFragment
				.findViewById(R.id.dots_layout);
		initializeDotView(urlsOfImages.size());

		pager.setCurrentItem(0);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setDotIndicator(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		return pager;
	}

	/*
	 * initialize title, product name and sale or not sale
	 */
	private void initializeSimpleFields(String title, Product product) {
		TextView textViewTitle = (TextView) getView().findViewById(
				R.id.textiew_main_title);
		textViewTitle.setText(title);

		TextView textProductName = (TextView) getView().findViewById(
				R.id.prodcut_name);
		textProductName.setText(product.getName());

		Double salePrice = product.getSalePrice();
		TextView textFinalPrice = (TextView) getView().findViewById(
				R.id.com_iview_txt_final_price);
		TextView textOldPrice = (TextView) getView().findViewById(
				R.id.com_iview_txt_oldprice);

		if (salePrice == null) {
			textOldPrice.setVisibility(View.GONE);

			if (InitInfoStore.getInstance().getCurrencySymbol() != null
					&& (product.getPrice() > 0))
				textFinalPrice.setText(generateProperStringForPrice(product
						.getPrice()));

		} else {
			textOldPrice.setVisibility(View.VISIBLE);

			textOldPrice.setText(generateProperStringForPrice(product
					.getPrice()));
			textOldPrice.setPaintFlags(textOldPrice.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			textFinalPrice.setText(generateProperStringForPrice(salePrice));
			textFinalPrice.setBackgroundColor(getResources().getColor(
					R.color.red));
		}
	}

	private void setInfoInViewFlipper() {
		Animation slideLeftIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_in);
		Animation slideLeftOut = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_left_out);

		Animation slideRightIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_in);
		Animation slideRightOut = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_right_out);

		gestureDetector = new GestureDetector(getActivity(),
				new MyGestureDetector(slideLeftIn, slideLeftOut, slideRightIn,
						slideRightOut));

		viewFlipper.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});

		viewOfFragment.findViewById(R.id.ll_images_of_info).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						gestureDetector.onTouchEvent(event);
						return true;
					}
				});

		setGestureOnTouch(R.id.ll_images_of_info);
		setGestureOnTouch(R.id.com_iinfo_ViewFlipper01);
	}

	private void setInfoButtons() {
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
				.setOnClickListener(this);
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
				.setOnClickListener(this);
		(viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
				.setOnClickListener(this);
		(viewOfFragment.findViewById(R.id.image_wish)).setOnClickListener(this);

		if (InitInfoStore.getInstance().getPageByName("wishlist") == null)
			(viewOfFragment.findViewById(R.id.image_wish))
					.setVisibility(View.GONE);
	}

	private void setAllInfoDataAndViews(Product product) {
		viewFlipper = (ViewFlipper) viewOfFragment
				.findViewById(R.id.com_iinfo_ViewFlipper01);

		// title
		final TextView textInfoTitle = (TextView) viewFlipper
				.findViewById(R.id.info_title);
		textInfoTitle.setText(product.getName());

		// description
		final TextView textInfoDescription = (TextView) viewFlipper
				.findViewById(R.id.info_description);
		String buildStringForDescription = GlobalConstants.EMPTY_STRING;

		String priceString = ((product.getPrice() > 0) ? ("\n"
				+ getResources().getString(R.string.price) + ": " + generateProperStringForPrice(product
				.getPrice())) : "");

		String skuString = (product.getSku() != null
				& (!product.getSku().equals("")) ? ("\n"
				+ getResources().getString(R.string.sku) + ": " + product
				.getSku()) : "");

		String description = (product.getDescription() != null) ? product
				.getDescription() : "";

		buildStringForDescription = description + priceString + skuString;

		textInfoDescription.setText(buildStringForDescription);

		// linkify for parquetim
		if (HackyUtils.shouldLinkify()) {

			// This is a hack because when there is text in hebrew, with the
			// link,
			// it becomes uncklicable
			buildStringForDescription = buildStringForDescription.replace(
					"http:", "aaa");
			buildStringForDescription = buildStringForDescription.replace(": ",
					":");
			buildStringForDescription = buildStringForDescription.replace(":",
					":\n");
			buildStringForDescription = buildStringForDescription.replace(
					"aaa", "http:");
			textInfoDescription.setText(buildStringForDescription);
			Linkify.addLinks(textInfoDescription, Linkify.ALL);
		}

		final TextView moreTextView = (TextView) viewFlipper
				.findViewById(R.id.more_text_view);
		moreTextView.setVisibility(View.VISIBLE);

		textInfoDescription.post(new Runnable() {
			@Override
			public void run() {
				final int lineCnt = textInfoTitle.getLineCount()
						+ textInfoDescription.getLineCount();
				if (lineCnt <= MAX_NUMBER_OF_LINES_IN_INFO_TEXT) {
					moreTextView.setVisibility(View.GONE);
				} else {
					textInfoDescription
							.setMaxLines(MAX_NUMBER_OF_LINES_IN_INFO_TEXT
									- textInfoTitle.getLineCount());
					moreTextView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							moreTextView.setVisibility(View.GONE);
							textInfoDescription.setMaxLines(lineCnt);
						}
					});
				}
			}
		});

		setInfoButtonSelector(1);
		setInfoButtons();
		setInfoInViewFlipper();
	}

	private void initSocialNetworks(final Product product) {
		Button fbButton = (Button) viewOfFragment
				.findViewById(R.id.fb_share_btn);
		fbButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Map<String, String> map = new HashMap<String, String>();
				map.put(MximoFlurryAgent.SHARE_ITEM_TYPE, "facebook");
				MximoFlurryAgent.logEvent(MximoFlurryAgent.SHARE_ITEM_CLICKED,
						map);

				String textToPublish = new StringBuilder()
						.append(InitInfoStore.getInstance().getShareTitle())
						.append("\n" + product.getName()).append("\n")
						.append(product.getDescription() + "\n\n")
						.append("Price :" + product.getPrice() + "\n")
						.append(InitInfoStore.getInstance().getGoogleQr())
						.toString();

				UtilityFunctions.shareToFacebook(getActivity(),
						product.getName(), product.getMainImageUrl());
			}
		});

		Button emailButton = (Button) viewOfFragment
				.findViewById(R.id.email_share_btn);
		emailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Map<String, String> map = new HashMap<String, String>();
				map.put(MximoFlurryAgent.SHARE_ITEM_TYPE, "email");
				MximoFlurryAgent.logEvent(MximoFlurryAgent.SHARE_ITEM_CLICKED,
						map);

				String url = InitInfoStore.getInstance().getGoogleQr();

				String priceString = Double.toString(product.getPrice());
				String textToShare = null;
				if (url == null) {

					textToShare = new StringBuilder().append(product.getName())
							.append("<br/>").append(product.getDescription())
							.append("<br/><br/>")
							.append("Price :" + priceString + "<br/>")
							.append(product.getSalePrice()).toString();

				} else {

					textToShare = new StringBuilder().append(product.getName())
							.append("<br/>").append(product.getDescription())
							.append("<br/><br/>")
							.append("Price :" + priceString + "<br/>")
							.append("sku: " + product.getSku())
							.append("<br/><br/>").append(url).toString();

				}

				ArrayList<String> urlsToAttatch = new ArrayList<String>();
				urlsToAttatch.add(product.getMainImageUrl());

				UtilityFunctions.sendEmail(getActivity(), textToShare,
						urlsToAttatch);
			}
		});

	}

	@Override
	protected void initializeActionBar() {

		String title = getArguments()
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		actionBarContorller.textViewMainTitle.setText(title);

		if (isOrderPage()) {

			actionBarContorller.textViewFarRight.setText("Mail To");
			makeViewsVisible(actionBarContorller.textViewFarRight);

		} else {
			actionBarContorller
					.setShoppingBagDrawable(actionBarContorller.imageViewOnRightStandalone);
			makeViewsVisible(actionBarContorller.imageViewOnRightStandalone);
		}

		actionBarContorller.imageViewOnRightStandalone
				.setOnClickListener(onClickOrder);
		actionBarContorller.textViewFarRight.setOnClickListener(onClickOrder);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		MximoFlurryAgent.logEvent(MximoFlurryAgent.ITEM_PAGE_VIEWED);
		cart = App.getApp().getCart();
		favorite = App.getApp().getFavorite();

		wishListAdd = viewOfFragment.findViewById(R.id.image_wish);

		initDialogForCheckout();
		Bundle args = getArguments();

		String title = args.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		placeHoldersOfTaxon = args
				.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);
		int positionOfProductInTaxon = args.getInt(
				FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY, -1);

		showOnlyChosenProduct = args.getBoolean(
				FragmentRoot.SHOW_ONLY_CHOSEN_PRODUCT, false);

		fromFavoritesPage = args.getBoolean(FragmentRoot.FROM_FAVORITES_PAGE,
				false);

		fromSearchPage = args.getBoolean(FragmentRoot.FROM_SEARCH_PAGE, false);
		if (fromSearchPage) {
			master = (Product) args
					.getSerializable(FragmentRoot.SEARCH_PAGE_PRODUCT);
		}

		positionInCartStore = args.getInt(FragmentRoot.CART_ITEM_ADDRESS, -1);

		refreshPage(title, placeHoldersOfTaxon, positionOfProductInTaxon, true);
		super.onActivityCreated(savedInstanceState);

	}

	private void refreshPage(String title,
			ArrayList<Integer> placeHoldersOfTaxon,
			int positionOfProductInTaxon, boolean refreshOtherProductsView) {
		this.positionOfProductInTaxon = positionOfProductInTaxon;
		this.placeHoldersOfTaxon = placeHoldersOfTaxon;

		if (fromSearchPage) {

			// for now do nothing

		} else {
			if (showOnlyChosenProduct) {

				if (fromFavoritesPage)
					master = favorite.getCartItemAtAddress(positionInCartStore)
							.getProduct();
				else
					master = cart.getCartItemAtAddress(positionInCartStore)
							.getProduct();
			}

			else {
				master = TaxonomiesStore.getInstance()
						.getTaxonsByPlaceHolder(placeHoldersOfTaxon)
						.getProducts().get(positionOfProductInTaxon);

			}
		}

		ViewPager pagerOfImagesInVariant = initializePagerAndDotsView(
				master.getAllImageUrlsFullSize(), master.isOutOfStock());
		initializeSimpleFields(title, master);

		initSocialNetworks(master);
		setViewsToBrandColor(R.id.buy_bottom_screen);

		setAllInfoDataAndViews(master);

		buildOtherVariants(master, pagerOfImagesInVariant);

		if (refreshOtherProductsView)
			buildOtherProducts(title, placeHoldersOfTaxon,
					positionOfProductInTaxon);
	}

	private void buildOtherProducts(final String title,
			final ArrayList<Integer> placeHoldersOfTaxon,
			int positionOfProductInTaxon) {

		final ArrayList<LinearLayout> linearLayoutsViewListOtherOptions = new ArrayList<LinearLayout>();
		LinearLayout myGallery = (LinearLayout) viewOfFragment
				.findViewById(R.id.mygallery);

		List<Product> otherProdcutsList;
		if (showOnlyChosenProduct) {
			otherProdcutsList = new ArrayList<Product>();
			otherProdcutsList.add(master);
		} else {
			otherProdcutsList = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHoldersOfTaxon).getProducts();
		}

		int locationOfProductInArray = 0;

		ItemPageParams itemPageParams = InitInfoStore.getInstance()
				.getItemPageParams();
		boolean isBottomThumb = itemPageParams.showThumbTitle;
		for (Product product : otherProdcutsList) {

			final boolean isOnSale = product.isOnSale();
			final boolean isOutOfStock = product.isOutOfStock();
			final Double origPrice = product.getPrice();
			final Double salePrice = product.getSalePrice();
			String urlOfImage = product.getMainImageUrlThumbNailSize();
			final LinearLayout layout = (LinearLayout) inflator.inflate(
					R.layout.layout_in_gallery, myGallery, false);
			final ImageView imageView = (ImageView) layout
					.findViewById(R.id.image_view_in_gallery);
			final ImageView saleImage = (ImageView) layout
					.findViewById(R.id.sale_image);
			final RelativeLayout saleCircle = (RelativeLayout) layout
					.findViewById(R.id.circle_sale_in_gallery);
			final TextView outOfStockMessageTextView = (TextView) layout
					.findViewById(R.id.item_out_of_stock_message);
			final ViewGroup layoutOfImageView = (ViewGroup) layout
					.findViewById(R.id.layout_of_image_view_in_gallery);
			final RelativeLayout progressLayout = (RelativeLayout) layout
					.findViewById(R.id.loadingPanel);
			final TextView bottomThumb = (TextView) layout
					.findViewById(R.id.item_name_bottom_text);

			myGallery.addView(layout);
			linearLayoutsViewListOtherOptions.add(layout);
			final String brandColor = InitInfoStore.getInstance()
					.getBrandColor();
			imageView.setTag(locationOfProductInArray);

			if ((locationOfProductInArray == positionOfProductInTaxon)
					|| showOnlyChosenProduct)
				layout.setBackgroundColor(Color.parseColor(brandColor));

			if (!showOnlyChosenProduct) {
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						for (LinearLayout ll : linearLayoutsViewListOtherOptions) {

							ll.setBackgroundColor(getResources().getColor(
									R.color.black_full_transparacy));
						}
						layout.setBackgroundColor(Color.parseColor(brandColor));
						mainScrollView.smoothScrollTo(0,
								mainScrollView.getTop());
						refreshPage(title, placeHoldersOfTaxon,
								(Integer) arg0.getTag(), false);
					}
				});

			}

			imageLoader.displayImage(urlOfImage, imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {

							layoutOfImageView.setVisibility(View.GONE);
							progressLayout.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {

							layoutOfImageView.setVisibility(View.VISIBLE);
							if (isOnSale)
							{
								saleImage.setVisibility(View.VISIBLE);
								
								if (InitInfoStore.getInstance().showDiscountBadge()) {
									
									GradientDrawable backgroundGradient = (GradientDrawable)saleCircle.getBackground();
								    backgroundGradient.setColor(Color.parseColor(InitInfoStore.getInstance().getBrandColor()));
									saleCircle.setVisibility(View.VISIBLE);
									TextView percentText = (TextView) saleCircle
											.findViewById(R.id.number_percent_id);

									int percent = (int) ((salePrice / origPrice) * 100);
									percentText.setText("" + percent + "%");
								}
								
							}
								
							if (isOutOfStock)
								outOfStockMessageTextView
										.setVisibility(View.VISIBLE);

							progressLayout.setVisibility(View.GONE);
						}
					});

			if (isBottomThumb) {
				bottomThumb.setText(product.getName());
				bottomThumb.setVisibility(View.VISIBLE);
			} else {
				bottomThumb.setVisibility(View.GONE);
			}
			locationOfProductInArray++;

		}

	}

	private boolean isValidListOfVariants(List<Variant> variants) {
		return (!(variants == null || variants.isEmpty() || (variants.size() == 1 && variants
				.get(0).isVariantMaster())));
	}

	private void buildOtherVariants(Product product, final ViewPager pager) {

		// Every key, has a number of options, i.e (color -> {green,blue,white})
		HashMap<String, ArrayList<String>> mapKeyToListOfOptions = new HashMap<String, ArrayList<String>>();

		// Map a string which is a combination of whatever permutation, to a
		// variant
		// i.e (orange + xs -> (variant with images bla bla))
		final HashMap<String, Variant> mapKeyVariant = new HashMap<String, Variant>();

		// Map the regular keys (color/size) to the text viev that represents it
		final HashMap<String, ArrayList<TextView>> mapKeyTextViews = new HashMap<String, ArrayList<TextView>>();

		final ArrayList<String> listOfKeys = new ArrayList<String>();

		final int numberOfKeys;

		List<Variant> variants = product.getVariants();
		wishListAdd.setEnabled(true);
		if (!isValidListOfVariants(variants)) {
			UserSelectionItem userSelectionItem = new UserSelectionItem(master,
					null, 1);
			boolean doesCartItemExistInFavorites = favorite
					.doesCartItemExist(userSelectionItem);

			if (doesCartItemExistInFavorites)
				wishListAdd.setEnabled(false);
		}

		for (Variant variant : variants) {
			List<OptionValues> optionValuesList = variant.getOptionValues();

			Collections.sort(optionValuesList);

			String constructingKey = GlobalConstants.EMPTY_STRING;
			for (OptionValues optionValues : optionValuesList) {

				String tempKey = optionValues.getOptionTypeValue();
				ArrayList<String> tempArrFromMapKeyToListOfOptions = mapKeyToListOfOptions
						.get(tempKey);

				// First time entering values for this key
				if (tempArrFromMapKeyToListOfOptions == null) {
					tempArrFromMapKeyToListOfOptions = new ArrayList<String>();
					mapKeyToListOfOptions.put(tempKey,
							tempArrFromMapKeyToListOfOptions);
					listOfKeys.add(tempKey);
				}

				if (!tempArrFromMapKeyToListOfOptions.contains(optionValues
						.getName()))
					tempArrFromMapKeyToListOfOptions
							.add(optionValues.getName());
				constructingKey += optionValues.getName();

			}
			if (!constructingKey.equals(GlobalConstants.EMPTY_STRING)) {
				mapKeyVariant.put(constructingKey, variant);
			}

		}
		numberOfKeys = listOfKeys.size();

		final ArrayList<String> listCurrentChosen = new ArrayList<String>(
				numberOfKeys);

		// Silly workaround, so nullPointerException isn't thrown when we
		// call put()
		for (int i = 0; i < numberOfKeys; i++)
			listCurrentChosen.add("");

		Iterator it = mapKeyToListOfOptions.entrySet().iterator();

		while (it.hasNext()) {
			final ArrayList<TextView> textViews = new ArrayList<TextView>();
			Map.Entry pairs = (Map.Entry) it.next();

			String key = (String) pairs.getKey();
			ArrayList<String> stringVals = (ArrayList<String>) pairs.getValue();

			for (String name : stringVals) {
				final TextView textViewName = new TextView(getActivity());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

				textViewName.setText(name);
				textViewName.setGravity(Gravity.CENTER);
				textViewName.setLayoutParams(params);
				textViewName.setBackgroundColor(getResources().getColor(
						R.color.white));
				textViewName.setTextColor(getResources()
						.getColor(R.color.black));
				textViews.add(textViewName);

			}

			mapKeyTextViews.put(key, textViews);
			// it.remove(); // avoids a ConcurrentModificationException
		}

		mainScrollView = (ScrollView) viewOfFragment
				.findViewById(R.id.comm_item_scrollView);
		final LinearLayout layoutInMainScrollView = (LinearLayout) mainScrollView
				.findViewById(R.id.comm_item_lin1);

		ArrayList<View> viewsToRemove = (ArrayList<View>) layoutInMainScrollView
				.getTag();
		if (viewsToRemove != null) {
			for (View view : viewsToRemove)
				layoutInMainScrollView.removeView(view);
		}

		ArrayList<View> removableViews = new ArrayList<View>();

		boolean firstRowEntry = true;
		boolean wereElementsInsertedInPreviousRow = false;
		int positionInRows = 0;

		it = mapKeyTextViews.entrySet().iterator();

		otherVariantsList.clear();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();

			final LinearLayout horizontalScrollView = (LinearLayout) inflator
					.inflate(R.layout.variant_options, layoutInMainScrollView,
							false);

			TextView selectText = (TextView) horizontalScrollView
					.findViewById(R.id.select_text);

			selectText.setText(getResources().getString(R.string.select_a)
					+ selectText.getText() + (String) pairs.getKey() + ":");

			final LinearLayout linearLayoutInHorizontalScrollView = (LinearLayout) horizontalScrollView
					.findViewById(R.id.linearlayout_variant_options);

			final HorizontalScrollView hsv = (HorizontalScrollView) horizontalScrollView
					.findViewById(R.id.horizontal_scroll_view_in_options);
			otherVariantsList.add(hsv);
			hsv.setTag(R.id.TAG_HSV_IN_ITEM_PAGE, false);

			final ArrayList<TextView> currentTextViewArr = (ArrayList<TextView>) pairs
					.getValue();

			for (final TextView textView : currentTextViewArr) {
				linearLayoutInHorizontalScrollView.addView(textView);

				textView.setTag(positionInRows);
				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						final View viewToUpdateWithMetaData = getRelevantVariantsList(numberOfKeys);

						viewToUpdateWithMetaData.setTag(
								R.id.TAG_HSV_IN_ITEM_PAGE, true);
						viewToUpdateWithMetaData.clearAnimation();
						viewToUpdateWithMetaData
								.setBackgroundResource(R.drawable.variantoption);
						int location = (Integer) arg0.getTag();

						if (location == 0 && (numberOfKeys > 1)) {

							ArrayList<TextView> viewsInNextHSV = mapKeyTextViews
									.get(listOfKeys.get(1));
							updateSecondRowInOtherVariants(viewsInNextHSV,
									textView, mapKeyVariant);

						}

						for (TextView tv : currentTextViewArr) {
							tv.setBackgroundColor(getResources().getColor(
									R.color.white));
						}
						textView.setBackgroundColor(getResources().getColor(
								R.color.gray_lyt_country));
						listCurrentChosen.set(location, textView.getText()
								.toString());

						String key = GlobalConstants.EMPTY_STRING;
						for (String string : listCurrentChosen) {
							key += string;
						}
						Variant variant = mapKeyVariant.get(key);

						if (variant != null) {

							viewToUpdateWithMetaData.setTag(
									R.id.TAG_HSV_CHOSEN_VARIANT, variant);

							if (!variant.isImageArrayEmpty()) {
								pager.removeAllViews();
								pager.setAdapter(new ImagePagerAdapter(variant
										.getAllImageUrlsFullSize(), inflator,
										variant.isOutOfStock()));

								initializeDotView(variant
										.getAllImageUrlsFullSize().size());
							}

							if (favorite
									.doesCartItemExist(new UserSelectionItem(
											master, variant, 1)))
								wishListAdd.setEnabled(false);
							else
								wishListAdd.setEnabled(true);
						}

						// As of now the only explanation, is that the user
						// picked a variant that is in a different second row,
						// and moved to another value in the first row
						else if ((numberOfKeys > 1))
							viewToUpdateWithMetaData.setTag(
									R.id.TAG_HSV_IN_ITEM_PAGE, false);

					}
				});
			}

			View lastView = viewOfFragment
					.findViewById(R.id.view_under_scroll_of_options);
			int index = layoutInMainScrollView.indexOfChild(lastView);
			layoutInMainScrollView.addView(horizontalScrollView, index);
			removableViews.add(horizontalScrollView);
			horizontalScrollView.post(new Runnable() {

				@Override
				public void run() {

					// Configure the size of all the textview's
					HorizontalScrollView hsv = (HorizontalScrollView) horizontalScrollView
							.findViewById(R.id.horizontal_scroll_view_in_options);
					LinearLayout ll = (LinearLayout) hsv.getChildAt(0);
					int numberOfTextViews = ll.getChildCount();

					int maxWidth = 0;
					for (int i = 0; i < numberOfTextViews; i++) {
						TextView tv = (TextView) ll.getChildAt(i);
						int currentWidth = tv.getWidth();
						if (currentWidth > maxWidth)
							maxWidth = currentWidth;

					}

					float height = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP,
							HEIGHT_OF_TEXT_VIEW_IN_VARIANT_OPTIONS,
							getResources().getDisplayMetrics());

					float extraWidth = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP,
							EXTRA_WIDTH_FOR_TEXT_VIEW_IN_VARIANT_OPTIONS,
							getResources().getDisplayMetrics());

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							(int) (maxWidth + extraWidth), (int) height);

					// this is returned in pixels
					int marginOfVariant = (int) getResources().getDimension(
							R.dimen.margin_of_variant_options);
					if ((params.width * numberOfTextViews) < (UtilityFunctions.WIDTH_OF_SCREEN - marginOfVariant)) {
						params = new LinearLayout.LayoutParams(
								(int) ((UtilityFunctions.WIDTH_OF_SCREEN - (marginOfVariant * 2)) / numberOfTextViews) - 4,
								(int) height);
						for (int i = 0; i < numberOfTextViews; i++) {
							TextView tv = (TextView) ll.getChildAt(i);
							tv.setLayoutParams(params);

						}
					}

					else {
						for (int i = 0; i < numberOfTextViews; i++) {
							TextView tv = (TextView) ll.getChildAt(i);
							tv.setLayoutParams(params);

						}
					}

				}
			});
			layoutInMainScrollView.setTag(removableViews);
			// if there are more than one line of options, set the first
			// option in the first line, as chosen

			// first time will always be false
			if (wereElementsInsertedInPreviousRow) {
				TextView firstTextViewInRow = currentTextViewArr.get(0);
				listCurrentChosen.set(positionInRows, firstTextViewInRow
						.getText().toString());

				TextView firstTextViewInFirstRow = mapKeyTextViews.get(
						listOfKeys.get(0)).get(0);

				updateSecondRowInOtherVariants(
						mapKeyTextViews.get(listOfKeys.get(1)),
						firstTextViewInFirstRow, mapKeyVariant);
			}

			if (firstRowEntry && it.hasNext()) {
				TextView firstTextViewInRow = currentTextViewArr.get(0);
				firstTextViewInRow.setBackgroundColor(getResources().getColor(
						R.color.gray_1));

				listCurrentChosen.set(0, firstTextViewInRow.getText()
						.toString());
				wereElementsInsertedInPreviousRow = true;
			}

			if (firstRowEntry && (!it.hasNext())) {
				for (TextView tv : currentTextViewArr) {
					Product variant = mapKeyVariant.get(tv.getText());
					if ((variant != null) && variant.isOutOfStock()) {
						tv.setPaintFlags(tv.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG);
						tv.setTextColor(getResources().getColor(R.color.gray_1));
					}

				}

			}

			firstRowEntry = true;

			// it.remove(); // avoids a ConcurrentModificationException
			positionInRows++;
		}

	}

	private void updateSecondRowInOtherVariants(
			ArrayList<TextView> viewsInNextHSV, TextView textView,
			HashMap<String, Variant> mapKeyVariant) {

		String currentChosenString = textView.getText().toString();
		for (TextView tv : viewsInNextHSV) {
			tv.setVisibility(View.VISIBLE);
			String tempNextRowCurrentString = tv.getText().toString();
			String tempKey = currentChosenString + tempNextRowCurrentString;
			Product variants = mapKeyVariant.get(tempKey);
			if (variants == null)
				tv.setVisibility(View.GONE);
			else {
				if (variants.isOutOfStock()) {
					tv.setPaintFlags(tv.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					tv.setTextColor(getResources().getColor(R.color.gray_1));
				}

				else {
					tv.setPaintFlags(tv.getPaintFlags()
							& (~Paint.STRIKE_THRU_TEXT_FLAG));
					tv.setTextColor(getResources().getColor(R.color.black));
				}

			}

		}

	}

	private void setGestureOnTouch(int id) {
		viewOfFragment.findViewById(id).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						gestureDetector.onTouchEvent(event);
						return true;
					}
				});
	}

	class MyGestureDetector extends SimpleOnGestureListener {

		public MyGestureDetector(Animation slideLeftIn, Animation slideLeftOut,
				Animation slideRightIn, Animation slideRightOut) {
			super();
			this.slideLeftIn = slideLeftIn;
			this.slideLeftOut = slideLeftOut;
			this.slideRightIn = slideRightIn;
			this.slideRightOut = slideRightOut;
		}

		private Animation slideLeftIn;
		private Animation slideLeftOut;
		private Animation slideRightIn;
		private Animation slideRightOut;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (viewFlipper.getDisplayedChild() < 2) {
						viewFlipper.setInAnimation(slideLeftIn);
						viewFlipper.setOutAnimation(slideLeftOut);
						viewFlipper.showNext();
						int n = viewFlipper.getDisplayedChild();
						setInfoButtonSelector(n + 1);
					}

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (viewFlipper.getDisplayedChild() > 0) {
						viewFlipper.setInAnimation(slideRightIn);
						viewFlipper.setOutAnimation(slideRightOut);
						viewFlipper.showPrevious();
						int n = viewFlipper.getDisplayedChild();
						setInfoButtonSelector(n + 1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	@Override
	public void onStop() {
		if (!proceedingToFavoritesPage)
			fragmentRoot.showTabs();
		proceedingToFavoritesPage = false;
		super.onStop();
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;
		boolean isOutOfStock;

		ImagePagerAdapter(List<String> images, LayoutInflater inflater,
				boolean isOutOfStock) {
			this.images = images;
			this.inflater = inflater;
			this.isOutOfStock = isOutOfStock;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			if (isOutOfStock)
				imageLayout.findViewById(R.id.out_of_stock_textview)
						.setVisibility(View.VISIBLE);

			final RelativeLayout progressLayout = (RelativeLayout) imageLayout
					.findViewById(R.id.loadingPanel);
			imageLoader.displayImage(images.get(position), imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressLayout.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}

							progressLayout.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progressLayout.setVisibility(View.GONE);
						}
					});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	@Override
	public void onClick(View viewOfFragment) {
		switch (viewOfFragment.getId()) {

		case R.id.com_iinfo_btn_info:
			setInfoButtonSelector(1);
			break;
		case R.id.com_iinfo_btn_share:
			setInfoButtonSelector(2);
			break;
		case R.id.com_iinfo_btn_rate:
			setInfoButtonSelector(3);
			break;
		case R.id.image_wish:

			int numberOfVariantRows = otherVariantsList.size();
			Variant variant = null;
			if (numberOfVariantRows > 0) {
				View currentlyTestedView = otherVariantsList
						.get(numberOfVariantRows - 1);
				boolean areAllVariantsChecked = (Boolean) currentlyTestedView
						.getTag(R.id.TAG_HSV_IN_ITEM_PAGE);

				if (!areAllVariantsChecked) {
					UtilityFunctions.setHighLightAnimation(currentlyTestedView);
					int[] location = new int[2];
					currentlyTestedView.getLocationOnScreen(location);
					mainScrollView.smoothScrollTo(0, location[1]);
					return;
				}

				variant = (Variant) currentlyTestedView
						.getTag(R.id.TAG_HSV_CHOSEN_VARIANT);

			}

			viewOfFragment.setEnabled(false);

			AddToFavoritesDialogFragment addToFavoritesDialogFragment = new AddToFavoritesDialogFragment();
			addToFavoritesDialogFragment.setCancelable(false);
			addToFavoritesDialogFragment
					.setiOnUserChooseOption(new IOnUserChooseOption() {

						@Override
						public void onProceed() {
							proceedingToFavoritesPage = true;
							Fragment f = getParentFragment();
							if (f instanceof FragmentRoot)
								((FragmentRoot) f).openFavoritesFragment();
						}

						@Override
						public void onCancel() {

						}
					});
			addToFavoritesDialogFragment.show(getActivity()
					.getFragmentManager(), GlobalConstants.EMPTY_STRING);

			favorite.addCartItem(master, variant);

			break;

		}

	}

}

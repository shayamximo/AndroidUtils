package com.soa.bhc.pagefragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.soa.bhc.App;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.R;
import com.soa.bhc.json.Page;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.soa.bhc.userselection.UserSelectionItem;
import com.soa.bhc.userselection.UserSelectionStore;
import com.soa.bhc.utils.MximoFlurryAgent;
import com.soa.bhc.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CatalogViewController extends FragmentPageBase implements
		OnClickListener {

	private boolean showOnlyChosenProduct;
	private boolean fromFavoritesPage;
	private boolean fromSearchPage;
	private Product currentlyDisplayedProduct;
	private UserSelectionStore cart;
	private int positionInCartStore;
	private UserSelectionStore favorite;
	private ArrayList<Integer> placeHoldersOfTaxon;
	private String title;
	private int positionOfProductInTaxon;
	private List<Product> otherProducts;
	private ViewGroup overShadowedViewGroup;
	private ViewFlipper viewFlipper;
	private GestureDetector gestureDetector;

	private static final int SWIPE_MIN_DISTANCE = 50;
	private static final int SWIPE_MAX_OFF_PATH = 220;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private Button wishButton;
	private TextView viewFlipperTextView;
	private ViewPager viewPager;
	private HorizontalScrollView scrollViewOfGallery;
	private ArrayList<LinearLayout> layoutsInGallery;

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.catalog_view_controller;
	}

	protected boolean shouldShowMultiView() {
		String showMultiView = getValueOfParamFromName("multi_view");
		if (showMultiView == null)
			return true;
		else
			return Boolean.parseBoolean(showMultiView);
	}

	private ViewPager initializePager() {

		List<String> urlsOfImages = getAllImageUrlsFullSizeForTaxon(otherProducts);

		ViewPager pager = (ViewPager) viewOfFragment
				.findViewById(R.id.catalog_viewpager);

		int dpOneUnit = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
						.getDisplayMetrics());

		pager.setPadding(dpOneUnit * 25, 0, dpOneUnit * 25, 0);
		pager.setClipToPadding(false);
		pager.setPageMargin(dpOneUnit * 10);
		pager.removeAllViews();

		pager.setAdapter(new ImagePagerAdapter(urlsOfImages, inflator));

		pager.setCurrentItem(positionOfProductInTaxon);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pagePosition) {
				refreshPage(pagePosition);
				scrollInGalleryToItem(pagePosition);
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

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<String> images, LayoutInflater inflater) {
			this.images = images;
			this.inflater = inflater;

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
			if (currentlyDisplayedProduct.isOutOfStock())
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

			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!shouldShowMultiView())
						return;

					final HorizontalScrollView hsv = (HorizontalScrollView) viewOfFragment
							.findViewById(R.id.other_options_scroller);

					final View plusButton = viewOfFragment
							.findViewById(R.id.more_info_button);
					final View titleLayout = viewOfFragment
							.findViewById(R.id.details_title);
					int visibility = hsv.getVisibility();

					final Animation animationFadeIn = AnimationUtils
							.loadAnimation(getActivity(), R.anim.fadein);

					final Animation animationFadeOut = AnimationUtils
							.loadAnimation(getActivity(), R.anim.fadeout);

					animationFadeOut
							.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {

									hsv.setVisibility(View.GONE);
									plusButton.setVisibility(View.GONE);
									titleLayout.setVisibility(View.GONE);
								}
							});

					animationFadeIn
							.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {

									hsv.setVisibility(View.VISIBLE);
									plusButton.setVisibility(View.VISIBLE);
									titleLayout.setVisibility(View.VISIBLE);
								}
							});

					if (visibility == View.VISIBLE) {
						hsv.startAnimation(animationFadeOut);
						plusButton.startAnimation(animationFadeOut);
						titleLayout.startAnimation(animationFadeOut);

					}

					else {

						hsv.startAnimation(animationFadeIn);
						plusButton.startAnimation(animationFadeIn);
						titleLayout.startAnimation(animationFadeIn);
					}

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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		cart = App.getApp().getCart();
		favorite = App.getApp().getFavorite();

		Bundle args = getArguments();
		title = args.getString(FragmentRoot.CATEGORY_TITLE_KEY);

		// variables, for getting to this page from favorites or from cart
		showOnlyChosenProduct = args.getBoolean(
				FragmentRoot.SHOW_ONLY_CHOSEN_PRODUCT, false);
		fromFavoritesPage = args.getBoolean(FragmentRoot.FROM_FAVORITES_PAGE,
				false);
		fromSearchPage = args.getBoolean(FragmentRoot.FROM_SEARCH_PAGE, false);

		positionInCartStore = args.getInt(FragmentRoot.CART_ITEM_ADDRESS, -1);

		// location in taxons store of information to be displayed
		placeHoldersOfTaxon = args
				.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);
		positionOfProductInTaxon = args.getInt(
				FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY, -1);

		otherProducts = new ArrayList<Product>();

		if (fromSearchPage) {
			currentlyDisplayedProduct = (Product) args
					.getSerializable(FragmentRoot.SEARCH_PAGE_PRODUCT);
			otherProducts.add(currentlyDisplayedProduct);
		}

		if (!showOnlyChosenProduct)
			otherProducts = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHoldersOfTaxon).getProducts();

		View moreInfoButton = viewOfFragment
				.findViewById(R.id.more_info_button);
		moreInfoButton.setTag(new Boolean(false));
		moreInfoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				final Boolean isInfoVisible = (Boolean) view.getTag();

				int animationId;
				if (isInfoVisible)
					animationId = R.anim.rotate_btn_right;
				else
					animationId = R.anim.rotate_btn_left;

				Animation anim = AnimationUtils.loadAnimation(getActivity(),
						animationId);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {

						if (isInfoVisible) {
							overShadowedViewGroup.setVisibility(View.GONE);
							if (shouldShowMultiView())
								scrollViewOfGallery.setVisibility(View.VISIBLE);
						}

						else {
							overShadowedViewGroup.setVisibility(View.VISIBLE);
							scrollViewOfGallery.setVisibility(View.GONE);
						}

						view.setTag(!isInfoVisible);

					}
				});
				anim.setFillAfter(true);
				view.startAnimation(anim);

			}
		});
		overShadowedViewGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_shadowed);
		viewFlipper = (ViewFlipper) viewOfFragment
				.findViewById(R.id.catalog_view_flipper);
		viewFlipperTextView = (TextView) viewOfFragment
				.findViewById(R.id.title_viewflipper);
		refreshPage(positionOfProductInTaxon);
		viewPager = initializePager();
		setViewsToBrandColor(R.id.order_now_button);
		buildOtherProducts(title, placeHoldersOfTaxon, positionOfProductInTaxon);
		scrollViewOfGallery = (HorizontalScrollView) viewOfFragment
				.findViewById(R.id.other_options_scroller);
		if (!shouldShowMultiView())
			scrollViewOfGallery.setVisibility(View.GONE);

		viewOfFragment.findViewById(R.id.order_now_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onOrderClicked();

					}
				});

	}

	private void onOrderClicked() {

		String action = getValueOfParamFromName("action");
		if (action.equals("order")) {

			ArrayList<String> urlList = new ArrayList<String>();
			urlList.add(currentlyDisplayedProduct.getMainImageUrl());
			String message = InitInfoStore.getInstance().getShareMessage();
			message += currentlyDisplayedProduct.getDescription();
			message += "<br></br> <br></br>Product ID: "
					+ currentlyDisplayedProduct.getId();

			UtilityFunctions.sendEmail(getActivity(), message, urlList,
					getResources().getString(R.string.order_via_email_subject)
							+ InitInfoStore.getInstance().getName(),
					InitInfoStore.getInstance().getOrdersEmail());
		}
		if (action.equals("call")) {
			setDialogslayout();
		}

		if (action.equals("buy")) {
			callProductWebView(true);

		}

		if (action.equals("join")) {

			boolean isClosestPlaceSet = App.getApp().getShopStore()
					.isListInitialized();
			if (isClosestPlaceSet) {

				Bundle bundle = new Bundle();
				bundle.putBoolean(ShopDetailsFragment.DISPLAY_CLOSEST_SHOP,
						true);

				fragmentRoot.openShopDetailsFragment(bundle, false);

			} else {
				Bundle bundle = new Bundle();
				bundle.putBoolean(StoreMapViewController.KEY_OPEN_CLOSEST, true);
				fragmentRoot.openFragment("stores", bundle);
			}

		}

	}

	private void scrollInGalleryToItem(int position) {
		final LinearLayout itemToScrollTo = layoutsInGallery.get(position);

		for (LinearLayout ll : layoutsInGallery) {

			ll.setBackgroundColor(getResources().getColor(
					R.color.black_full_transparacy));
		}

		itemToScrollTo.setBackgroundColor(Color.parseColor(InitInfoStore
				.getInstance().getBrandColor()));

		scrollViewOfGallery.post(new Runnable() {

			@Override
			public void run() {
				int screenWidth = getActivity().getWindowManager()
						.getDefaultDisplay().getWidth();
				int scrollX = (itemToScrollTo.getLeft() - (screenWidth / 2))
						+ (itemToScrollTo.getWidth() / 2);
				scrollViewOfGallery.smoothScrollTo(scrollX, 0);

			}
		});

	}

	protected void callProductWebView(boolean showToolBar) {
		MximoFlurryAgent.logEvent(MximoFlurryAgent.BUY_AFFILIATE_CLICK);

		Bundle bundle = new Bundle();
		bundle.putString(ItemWebView.URL_FOR_ITEM,
				currentlyDisplayedProduct.getProductUrl());
		bundle.putString(ItemWebView.TITLE_ITEM, title);

		if (showToolBar)
			bundle.putBoolean(MximoWebViewFragment.SHOW_TOOLBAR_KEY, true);
		fragmentRoot.openFragmnet(new ItemWebView(), bundle);
	}

	private void setDialogslayout() {
		final Dialog dialog = new Dialog(getActivity(),
				R.style.ThemeDialogCustom);
		dialog.setContentView(R.layout.lyt_buybuttondialog);
		dialog.setCancelable(false);

		TextView tvWebview = (TextView) dialog.findViewById(R.id.buy_tvWebView);
		TextView tvCall = (TextView) dialog.findViewById(R.id.buy_tvCall);
		TextView tvCancel = (TextView) dialog.findViewById(R.id.buy_tvCancle);

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		tvWebview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				callProductWebView(false);

			}
		});

		tvCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				MximoFlurryAgent.logEvent(MximoFlurryAgent.DIAL_CLICKED);
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ InitInfoStore.getInstance().getPhoneNumber()));
				startActivity(callIntent);

			}
		});

		dialog.show();
	}

	private boolean isWishListDefined() {
		Page wishlistPage = InitInfoStore.getInstance().getPageByName(
				"wishlist");
		boolean isDefined = ((wishlistPage != null) ? true : false);
		return isDefined;
	}

	private void setInfoButtons() {
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
				.setOnClickListener(this);
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
				.setOnClickListener(this);
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
				.setOnClickListener(this);

		wishButton = (Button) viewOfFragment
				.findViewById(R.id.com_iinfo_btn_wish);
		if (!isWishListDefined())
			wishButton.setVisibility(View.GONE);

		wishButton.setOnClickListener(this);
		wishButton.setEnabled(true);
		UserSelectionItem userSelectionItem = new UserSelectionItem(
				currentlyDisplayedProduct,
				currentlyDisplayedProduct.getMaster(), 1);
		boolean doesCartItemExistInFavorites = favorite
				.doesCartItemExist(userSelectionItem);

		if (doesCartItemExistInFavorites)
			wishButton.setEnabled(false);

	}

	private void setInfoButtonSelector(int i) {
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
				.setSelected(false);
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
				.setSelected(false);
		((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
				.setSelected(false);

		switch (i) {
		case 1:
			((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_info))
					.setSelected(true);
			viewFlipper.setDisplayedChild(0);
			viewFlipperTextView
					.setText(getResources().getString(R.string.info));
			break;
		case 2:
			((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_share))
					.setSelected(true);
			viewFlipper.setDisplayedChild(1);
			viewFlipperTextView.setText(getResources()
					.getString(R.string.share));

			break;

		case 3:
			((Button) viewOfFragment.findViewById(R.id.com_iinfo_btn_rate))
					.setSelected(true);
			viewFlipper.setDisplayedChild(2);
			viewFlipperTextView
					.setText(getResources().getString(R.string.rate));

			break;

		default:
			break;
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
		setGestureOnTouch(R.id.catalog_view_flipper);
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

	private void configureViewFlipper() {

		// title
		final TextView textInfoTitle = (TextView) viewFlipper
				.findViewById(R.id.info_title);
		textInfoTitle.setText(currentlyDisplayedProduct.getName());

		// description
		final TextView textInfoDescription = (TextView) viewFlipper
				.findViewById(R.id.info_description);
		String buildStringForDescription = GlobalConstants.EMPTY_STRING;

		String priceText = "";
		String skuText = "";
		if (currentlyDisplayedProduct.getPrice() > 0) {
			priceText = "\n" + getResources().getString(R.string.price) + ": "
					+ currentlyDisplayedProduct.getFinalPrice() + " "
					+ InitInfoStore.getInstance().getCurrencySymbol() + "\n";
		}

		if (currentlyDisplayedProduct.getSku() != null
				|| currentlyDisplayedProduct.getSku().length() > 0) {

			skuText = getResources().getString(R.string.sku) + ": "
					+ currentlyDisplayedProduct.getSku();

		}

		buildStringForDescription = ((currentlyDisplayedProduct
				.getDescription() != null) ? currentlyDisplayedProduct
				.getDescription() : "")
				+ priceText + skuText;
		textInfoDescription.setText(buildStringForDescription);

		setInfoButtonSelector(1);
		setInfoButtons();
		setInfoInViewFlipper();

	}

	private void buildOtherProducts(final String title,
			final ArrayList<Integer> placeHoldersOfTaxon,
			final int positionOfProductInTaxon) {

		final ArrayList<LinearLayout> linearLayoutsViewListOtherOptions = new ArrayList<LinearLayout>();
		LinearLayout myGallery = (LinearLayout) viewOfFragment
				.findViewById(R.id.mygallery);

		if (layoutsInGallery == null)
			layoutsInGallery = new ArrayList<LinearLayout>();

		int locationOfProductInArray = 0;
		for (Product product : otherProducts) {

			final boolean isOnSale = product.isOnSale();
			final boolean isOutOfStock = product.isOutOfStock();
			String urlOfImage = product.getMainImageUrlThumbNailSize();
			final LinearLayout layout = (LinearLayout) inflator.inflate(
					R.layout.layout_in_gallery, myGallery, false);
			layoutsInGallery.add(layout);
			final ImageView imageView = (ImageView) layout
					.findViewById(R.id.image_view_in_gallery);
			final ImageView saleImage = (ImageView) layout
					.findViewById(R.id.sale_image);
			final TextView outOfStockMessageTextView = (TextView) layout
					.findViewById(R.id.item_out_of_stock_message);
			final ViewGroup layoutOfImageView = (ViewGroup) layout
					.findViewById(R.id.layout_of_image_view_in_gallery);
			final RelativeLayout progressLayout = (RelativeLayout) layout
					.findViewById(R.id.loadingPanel);
			myGallery.addView(layout);
			linearLayoutsViewListOtherOptions.add(layout);
			final String brandColor = InitInfoStore.getInstance()
					.getBrandColor();
			imageView.setTag(locationOfProductInArray);

			if ((locationOfProductInArray == positionOfProductInTaxon)
					|| showOnlyChosenProduct)
				layout.setBackgroundColor(Color.parseColor(brandColor));

			if (!showOnlyChosenProduct) {

				imageView.setTag(Integer.valueOf(locationOfProductInArray));
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						for (LinearLayout ll : linearLayoutsViewListOtherOptions) {

							ll.setBackgroundColor(getResources().getColor(
									R.color.black_full_transparacy));
						}
						int locationOfProductInArray = (Integer) view.getTag();
						layout.setBackgroundColor(Color.parseColor(brandColor));
						refreshPage(positionOfProductInTaxon);
						viewPager.setCurrentItem(locationOfProductInArray);

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
								saleImage.setVisibility(View.VISIBLE);
							if (isOutOfStock)
								outOfStockMessageTextView
										.setVisibility(View.VISIBLE);

							progressLayout.setVisibility(View.GONE);
						}
					});
			locationOfProductInArray++;

		}

	}

	public void refreshPage(int positionOfProductInTaxon) {

		if (fromSearchPage) {

			// for now do nothing

		} else {
			if (showOnlyChosenProduct) {

				if (fromFavoritesPage)
					currentlyDisplayedProduct = favorite.getCartItemAtAddress(
							positionInCartStore).getProduct();
				else
					currentlyDisplayedProduct = cart.getCartItemAtAddress(
							positionInCartStore).getProduct();
				otherProducts.add(currentlyDisplayedProduct);
			}

			else {

				currentlyDisplayedProduct = TaxonomiesStore
						.getInstance()
						.getTaxonsByPlaceHolder(placeHoldersOfTaxon)
						.getProducts()
						.get((positionOfProductInTaxon != -1) ? positionOfProductInTaxon
								: 0);

			}
		}

		initializeSimpleFields();
		configureViewFlipper();
		initSocialNetworks(currentlyDisplayedProduct);

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

	// The assumption is that there are no variants in product
	private List<String> getAllImageUrlsFullSizeForTaxon() {
		List<Product> listProduct = TaxonomiesStore.getInstance()
				.getTaxonsByPlaceHolder(placeHoldersOfTaxon).getProducts();

		ArrayList<String> urlsList = new ArrayList<String>();
		for (Product product : listProduct)
			urlsList.add(product.getMainImageUrl());
		return urlsList;
	}

	// The assumption is that there are no variants in product
	private List<String> getAllImageUrlsFullSizeForTaxon(
			List<Product> listProduct) {
		ArrayList<String> urlsList = new ArrayList<String>();
		for (Product product : listProduct)
			urlsList.add(product.getMainImageUrl());
		return urlsList;
	}

	private void initializeSimpleFields() {
		TextView textViewTitle = (TextView) getView().findViewById(
				R.id.textiew_main_title);
		textViewTitle.setText(title);

		TextView textProductName = (TextView) getView().findViewById(
				R.id.prodcut_name);
		textProductName.setText(currentlyDisplayedProduct.getName());

		Double salePrice = currentlyDisplayedProduct.getSalePrice();
		TextView textFinalPrice = (TextView) getView().findViewById(
				R.id.com_iview_txt_final_price);
		TextView textOldPrice = (TextView) getView().findViewById(
				R.id.com_iview_txt_oldprice);

		if (salePrice == null) {
			textOldPrice.setVisibility(View.GONE);

			if (currentlyDisplayedProduct.getPrice() > 0) {
				textFinalPrice
						.setText(generateProperStringForPrice(currentlyDisplayedProduct
								.getPrice()));
			}

		} else {
			textOldPrice.setVisibility(View.VISIBLE);
			textOldPrice
					.setText(generateProperStringForPrice(currentlyDisplayedProduct
							.getPrice()));

			textOldPrice.setPaintFlags(textOldPrice.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			textFinalPrice.setText(InitInfoStore.getInstance()
					.getCurrencySymbol() + Double.toString(salePrice));
			textFinalPrice.setBackgroundColor(getResources().getColor(
					R.color.red));
		}
	}

	@Override
	protected void initializeActionBar() {

		String title = getArguments()
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		actionBarContorller.textViewMainTitle.setText(title);

		actionBarContorller.imageViewOnRightStandalone
				.setImageResource(R.drawable.ic_action_shopping_bag);
		makeViewsVisible(actionBarContorller.imageViewOnRightStandalone);

		actionBarContorller.imageViewOnRightStandalone
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onOrderClicked();

					}
				});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.com_iinfo_btn_info:
			setInfoButtonSelector(1);
			break;
		case R.id.com_iinfo_btn_share:
			setInfoButtonSelector(2);
			break;
		case R.id.com_iinfo_btn_rate:
			setInfoButtonSelector(3);
			break;
		case R.id.com_iinfo_btn_wish:

			view.setEnabled(false);

			final AlertDialog alertDialog = new AlertDialog.Builder(
					getActivity()).create();
			alertDialog.setMessage(getResources().getString(
					R.string.add_to_favorites_message));
			alertDialog.setCancelable(false);
			alertDialog.setTitle(InitInfoStore.getInstance().getName());

			alertDialog.setButton(getResources().getString(R.string.close),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					});

			alertDialog.show();

			favorite.addCartItem(currentlyDisplayedProduct,
					currentlyDisplayedProduct.getMaster());

			break;

		}
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

}

package com.soa.bhc.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.soa.bhc.R;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.Taxon;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.StoreLoadDataListener;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.soa.bhc.json.stores.TaxonsStore;
import com.soa.bhc.utils.MximoFlurryAgent;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class LooksViewController extends FragmentPageBase {

	private Taxon chosenTaxons;
	private TextView textViewTitle;
	private ArrayList<Integer> placeHolders;

	@Override
	protected int getLayoutID() {
		return R.layout.look_layout;
	}

	@Override
	protected void initializeActionBar() {
		textViewTitle = actionBarContorller.textViewMainTitle;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		MximoFlurryAgent.logEvent(MximoFlurryAgent.LOOK_PAGE_VIEWED);
		Bundle args = getArguments();
		placeHolders = args.getIntegerArrayList(LIST_OF_PLACEHOLDERS_KEY);

		chosenTaxons = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
				placeHolders);

		final ArrayList<String> bigImagesForPager = new ArrayList<String>();
		for (Taxon taxons : chosenTaxons.getTaxons()) {
			bigImagesForPager.add(taxons.getImageUrl());
		}

		initializePager(bigImagesForPager);

		refreshGallery(0);

		configureMoreInfoButton();

	}

	private void configureMoreInfoButtonVisibility(Button moreInfoButton) {
		String showThumbTitle = getValueOfParamFromName("show_thumb_title");
		if (showThumbTitle != null) {
			Boolean boolShowThumbTitle = Boolean.parseBoolean(showThumbTitle);
			if (!boolShowThumbTitle)
				moreInfoButton.setVisibility(View.GONE);

		} else
			moreInfoButton.setVisibility(View.GONE);
	}

	private void configureMoreInfoButton() {
		final Button moreInfoButton = (Button) viewOfFragment
				.findViewById(R.id.more_info_button);
		configureMoreInfoButtonVisibility(moreInfoButton);
		final TextView description = (TextView) viewOfFragment
				.findViewById(R.id.description_in_look);

		final Animation animationFadeIn = AnimationUtils.loadAnimation(
				getActivity(), R.anim.fadein);

		final Animation animationFadeOut = AnimationUtils.loadAnimation(
				getActivity(), R.anim.fadeout);

		animationFadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				moreInfoButton.setVisibility(View.VISIBLE);
				description.setVisibility(View.GONE);
			}
		});

		moreInfoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				moreInfoButton.setVisibility(View.GONE);
				description.setVisibility(View.VISIBLE);
				description.startAnimation(animationFadeIn);

			}
		});

		description.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				description.startAnimation(animationFadeOut);

			}
		});

	}

	private boolean showBottomTextInGalleryImage() {
		String showThumbTitle = getValueOfParamFromName("show_thumb_title");
		if (showThumbTitle != null)
			return Boolean.valueOf(showThumbTitle);
		return false;
	}

	private void onFinishLoadingProductsOfTaxon(
			final List<Product> productList,
			final Taxon currentTaxonsDisplayed, final int locationOfImageInPager) {

		currentTaxonsDisplayed.setProducts(productList);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				final HorizontalScrollView hsv = (HorizontalScrollView) viewOfFragment
						.findViewById(R.id.other_options_scroller);
				hsv.scrollTo(0, 0);

				final LinearLayout myGallery = (LinearLayout) viewOfFragment
						.findViewById(R.id.mygallery);
				myGallery.removeAllViews();
				final ArrayList<LinearLayout> linearLayoutsViewListOtherOptions = new ArrayList<LinearLayout>();

				boolean showBottomText = showBottomTextInGalleryImage();
				int locationOfProductInArray = 0;

				if (productList != null) {
					for (final Product product : productList) {

						final Double origPrice = product.getPrice();
						final Double salePrice = product.getSalePrice();
						final boolean isOnSale = product.isOnSale();
						final boolean isOutOfStock = product.isOutOfStock();
						String urlOfImage = product
								.getMainImageUrlThumbNailSize();
						final LinearLayout layout = (LinearLayout) inflator
								.inflate(R.layout.layout_in_gallery, myGallery,
										false);
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
						myGallery.addView(layout);
						linearLayoutsViewListOtherOptions.add(layout);
						final String brandColor = InitInfoStore.getInstance()
								.getBrandColor();
						imageView.setTag(locationOfProductInArray);

						if (showBottomText) {
							String productName = product.getName();

							final TextView textViewBottomText = (TextView) layout
									.findViewById(R.id.item_name_bottom_text);
							textViewBottomText.setVisibility(View.VISIBLE);
							textViewBottomText.setText(productName);
						}

						imageView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								for (LinearLayout ll : linearLayoutsViewListOtherOptions) {

									ll.setBackgroundColor(getResources()
											.getColor(
													R.color.black_full_transparacy));
								}
								layout.setBackgroundColor(Color
										.parseColor(brandColor));

								Bundle args = new Bundle();
								args.putString(FragmentRoot.CATEGORY_TITLE_KEY,
										currentTaxonsDisplayed.getName());

								ArrayList<Integer> tempPlaceHolders = (ArrayList<Integer>) placeHolders
										.clone();
								tempPlaceHolders.add(locationOfImageInPager);
								args.putIntegerArrayList(
										FragmentRoot.TAXON_ADDRESS_KEY,
										tempPlaceHolders);
								Integer position = (Integer) imageView.getTag();
								args.putInt(
										FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY,
										position);
								fragmentRoot.openItemFragment(args);

							}
						});

						imageLoader.displayImage(urlOfImage, imageView,
								options, new SimpleImageLoadingListener() {
									@Override
									public void onLoadingStarted(
											String imageUri, View view) {

										layoutOfImageView
												.setVisibility(View.GONE);
										progressLayout
												.setVisibility(View.VISIBLE);
									}

									@Override
									public void onLoadingFailed(
											String imageUri, View view,
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
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {

										layoutOfImageView
												.setVisibility(View.VISIBLE);
										if (isOnSale)
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
										if (isOutOfStock)
											outOfStockMessageTextView
													.setVisibility(View.VISIBLE);

										progressLayout.setVisibility(View.GONE);
									}
								});
						locationOfProductInArray++;

					}
				}

			}
		});

	}

	private void refreshGallery(final int locationOfImageInPager) {
		final Taxon currentTaxonsDisplayed = chosenTaxons.getTaxons().get(
				locationOfImageInPager);

		TextView textViewDescription = (TextView) viewOfFragment
				.findViewById(R.id.description_in_look);
		textViewDescription.setText(currentTaxonsDisplayed.getDescription());

		textViewTitle.setText(currentTaxonsDisplayed.getName());
		final List<Product> productsList = currentTaxonsDisplayed.getProducts();

		if (productsList == null) {
			final TaxonsStore taxonsStore = new TaxonsStore(
					currentTaxonsDisplayed.getId());
			taxonsStore.loadDataIfNotInitialized(new StoreLoadDataListener() {

				@Override
				public void onFinish() {
					onFinishLoadingProductsOfTaxon(taxonsStore.getProducts(),
							currentTaxonsDisplayed, locationOfImageInPager);
				}

				@Override
				public void onError() {
					// For now, do nothing.

				}
			});
		} else {
			onFinishLoadingProductsOfTaxon(productsList,
					currentTaxonsDisplayed, locationOfImageInPager);
		}

	}

	public void initializePager(List<String> urlsOfImages) {
		ViewPager pager = (ViewPager) viewOfFragment
				.findViewById(R.id.comm_item_viewPagerr);
		
		int dpOneUnit = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
						.getDisplayMetrics());

		pager.setPadding(dpOneUnit * 25, 0, dpOneUnit * 25, 0);
		pager.setClipToPadding(false);
		pager.setPageMargin(dpOneUnit * 10);
		
		
		
		pager.removeAllViews();

		pager.setAdapter(new ImagePagerAdapter(urlsOfImages, inflator, false));

		pager.setCurrentItem(0);

		// int dpOneUnit = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
		// .getDisplayMetrics());
		//
		// pager.setPadding(dpOneUnit * 25, 0, dpOneUnit * 25, 0);
		// pager.setClipToPadding(false);
		// pager.setPageMargin(dpOneUnit * 10);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				refreshGallery(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

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

			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final HorizontalScrollView hsv = (HorizontalScrollView) viewOfFragment
							.findViewById(R.id.other_options_scroller);

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
								}
							});

					if (visibility == View.VISIBLE) {
						hsv.startAnimation(animationFadeOut);

					}

					else {

						hsv.startAnimation(animationFadeIn);
					}

				}
			});

			assert imageLayout != null;
			final ImageView imageView = (ImageView) imageLayout
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
							imageView.setVisibility(View.GONE);
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

}

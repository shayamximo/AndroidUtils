package com.soa.bhc.pagefragments;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soa.bhc.R;
import com.soa.bhc.json.Page;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.Taxon;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.SearchStore;
import com.soa.bhc.json.stores.StoreLoadDataListener;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.soa.bhc.utils.HackyUtils;
import com.soa.bhc.utils.MximoFlurryAgent;
import com.soa.bhc.utils.MximoLog;
import com.soa.bhc.utils.ProgressDialogFragment;
import com.soa.bhc.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class GridViewController extends FragmentPageBase {

	private String title;
	protected AbsListView listView;

	@Override
	protected int getLayoutID() {
		return R.layout.ac_image_grid;
	}

	private boolean isSearchDefined() {
		Page page = InitInfoStore.getInstance().getPageByName("search");
		boolean isDefined = ((page != null) ? true : false);
		return isDefined;

	}

	@Override
	protected void initializeActionBar() {
		
		if (title == null||title.equals("")) {
			Bundle bundle = getArguments();
			final ArrayList<Integer> placeHolders = bundle
					.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);
			Taxon taxonOfGrid = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHolders);
			title = taxonOfGrid.getName();
		}

		actionBarContorller.textViewMainTitle.setText(title);

		actionBarContorller
				.setSearchDrawable(actionBarContorller.imageViewOnRightStandalone);

		if (isSearchDefined())
			makeViewsVisible(actionBarContorller.imageViewOnRightStandalone);

		actionBarContorller.imageViewOnRightStandalone
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						fragmentRoot.openSearchFragment(null);

					}
				});

		if (shouldProvideFilter()) {

			ImageView filter = actionBarContorller.secondViewOnRightStandalone;

			actionBarContorller.setFilterDrawable(filter);
			makeViewsVisible(filter);
			filter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Bundle send = new Bundle();
					ArrayList<Integer> placeHolders = getArguments()
							.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);

					send.putIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY,
							placeHolders);
					fragmentRoot.openFragmnet(new FilterFragment(), send);

				}
			});
		}

	}

	private boolean shouldProvideFilter() {
		Bundle bundle = getArguments();

		final ArrayList<Integer> placeHolders = bundle
				.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);
		Taxon taxon = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
				placeHolders);
		ArrayList<Integer> idList = taxon.getTaxonsIdsList();

		return ((idList != null) && taxon.getTaxonsIdsList().size() > 1);
	}

	private void configureRefinedTopText(int numberOfProducts) {
		TextView textTopOfFragment = (TextView) viewOfFragment
				.findViewById(R.id.refine_text);
		textTopOfFragment.setVisibility(View.VISIBLE);

		String refinedShowing = getResources().getString(
				R.string.refined_showing);
		String products = getResources().getString(R.string.prodcuts);

		textTopOfFragment.setText(refinedShowing + " " + numberOfProducts + " "
				+ products);

	}

	private boolean isFragmentBinded(String title,
			final ArrayList<Integer> placeHolders) {
		if (title != null) {
			Page bindingPage = InitInfoStore.getInstance()
					.getBindingPageByName(title);
			if (bindingPage != null) {
				try {
					Integer recentItemsTime = Integer.parseInt(bindingPage
							.getParamsByName("recent_items_time").getValue());
					final Taxon taxon = TaxonomiesStore.getInstance()
							.getTaxonsByPlaceHolder(placeHolders);
					final SearchStore searchStore = new SearchStore(
							recentItemsTime);

					final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
					progressDialogFragment.show(getFragmentManager(),
							"MyProgressDialog");
					progressDialogFragment.setCancelable(false);

					searchStore
							.loadDataIfNotInitialized(new StoreLoadDataListener() {

								@Override
								public void onFinish() {
									progressDialogFragment.dismiss();
									if (searchStore.getProducts() == null
											|| (searchStore.getProducts()
													.size() == 0)) {
										UtilityFunctions
												.makeToast(
														getActivity(),
														getActivity()
																.getResources()
																.getString(
																		R.string.no_items_found_for_search));
									}

									else {
										taxon.setProducts(searchStore
												.getProducts());
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												openGrid(placeHolders, null);

											}
										});

									}
								}

								@Override
								public void onError() {
									progressDialogFragment.dismiss();

								}
							});

				} catch (Exception e) {
					// do nothing
				}

				return true;
			}
		} else {
			MximoLog.e(
					"ERROR",
					"when calling isFragmentBinded in gridViewController , a title must be attatched");
		}

		return false;
	}

	private void openGrid(ArrayList<Integer> placeHolders,
			ArrayList<Integer> refinedTaxonIds) {
		ArrayList<ProductViewInfo> productViewInfoList = createListOfProducts(
				placeHolders, refinedTaxonIds);

		if (refinedTaxonIds != null)
			configureRefinedTopText(productViewInfoList.size());

		Map<String, String> map = new HashMap<String, String>();
		map.put(MximoFlurryAgent.CATEGORY, title);
		MximoFlurryAgent.logEvent(MximoFlurryAgent.GRID_PAGE_VIEWED, map);

		listView = (GridView) getView().findViewById(R.id.gridview);
		((GridView) listView).setAdapter(new ImageAdapter(productViewInfoList,
				placeHolders));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		title = bundle.getString(FragmentRoot.CATEGORY_TITLE_KEY);

		final ArrayList<Integer> placeHolders = bundle
				.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);

		final ArrayList<Integer> refinedTaxonIds = bundle
				.getIntegerArrayList(FragmentRoot.KEY_FILTERED_TAXONS);

		boolean isBinded = isFragmentBinded(title, placeHolders);

		// if it's binded, the binding handler will call openGrid.
		if (!isBinded)
			openGrid(placeHolders, refinedTaxonIds);

		super.onActivityCreated(savedInstanceState);
	}

	private ArrayList<ProductViewInfo> createListOfProducts(
			ArrayList<Integer> placeHolders, ArrayList<Integer> refinedTaxonIds) {

		ArrayList<ProductViewInfo> retArray = new ArrayList<GridViewController.ProductViewInfo>();

		if (refinedTaxonIds != null) {

			List<Pair<Integer, List<Product>>> pairTaxonIdToProducts = TaxonomiesStore
					.getInstance().getTaxonsByPlaceHolder(placeHolders)
					.getProductsByTaxonIds(refinedTaxonIds);

			int i = 0;
			for (Pair<Integer, List<Product>> pair : pairTaxonIdToProducts) {
				retArray.addAll(createProdcutViewInfoFromProductsList(
						pair.second, pair.first));
				i++;
			}

		} else {
			List<Product> productsList = null;
			productsList = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHolders).getProducts();
			retArray = createProdcutViewInfoFromProductsList(productsList, null);
		}

		return retArray;
	}

	private ArrayList<ProductViewInfo> createProdcutViewInfoFromProductsList(
			List<Product> productsList, Integer placeHolderOfTaxon) {
		ArrayList<ProductViewInfo> retArray = new ArrayList<GridViewController.ProductViewInfo>();

		if (productsList != null) {
			int i = 0;
			for (Product product : productsList) {
				String imageUrl = product.getMainImageUrlThumbNailSize();
				String title = product.getName();
				double price = product.getPrice();
				Double salePrice = product.getSalePrice();
				boolean isOutOfStock = product.isOutOfStock();

				ProductViewInfo pvi = new ProductViewInfo(imageUrl, title,
						price, salePrice, isOutOfStock, placeHolderOfTaxon, i,
						product.getTaxonIds());
				retArray.add(pvi);
				i++;
			}
		}
		return retArray;
	}

	public class ProductViewInfo {
		public ProductViewInfo(String imageUrl, String title, double price,
				Double salePrice, boolean isOutOfStock,
				Integer placeHolderOfTaxon, Integer placeHolderOfProduct,
				List<Integer> taxonIdList) {
			super();
			this.imageUrl = imageUrl;
			this.title = title;
			this.price = price;
			this.salePrice = salePrice;
			this.isOutOfStock = isOutOfStock;
			this.placeHolderOfTaxon = placeHolderOfTaxon;
			this.placeHolderOfProduct = placeHolderOfProduct;
			this.taxonIdList = taxonIdList;
		}

		public String imageUrl;
		public String title;
		public double price;
		public Double salePrice;
		public boolean isOutOfStock;
		public Integer placeHolderOfTaxon;
		public Integer placeHolderOfProduct;
		public List<Integer> taxonIdList;
	}

	public class ImageAdapter extends BaseAdapter {

		private ArrayList<ProductViewInfo> productViewInfoList;
		private ArrayList<Integer> placeHolders;

		private final int originalPriceColor = getResources().getColor(
				R.color.grid_cell_price_original_color);
		private final int salePriceColor = getResources().getColor(
				R.color.grid_cell_price_sale_color);

		public ImageAdapter(ArrayList<ProductViewInfo> productViewInfoList,
				ArrayList<Integer> placeHolders) {
			this.productViewInfoList = productViewInfoList;
			this.placeHolders = placeHolders;
		}

		@Override
		public int getCount() {
			return productViewInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(
						R.layout.item_grid_view_controller, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.title = (TextView) view.findViewById(R.id.title);
				holder.categoryTitle = (TextView) view
						.findViewById(R.id.category_title_in_grid);
				holder.exchangePrice = (TextView) view
						.findViewById(R.id.exchange_price);
				holder.price = (TextView) view.findViewById(R.id.price);
				holder.originalPrice = (TextView) view
						.findViewById(R.id.original_price);
				holder.saleImageView = (ImageView) view
						.findViewById(R.id.sale_image);
				holder.progressLayout = (RelativeLayout) view
						.findViewById(R.id.loadingPanel);
				holder.outOfStockMessage = (TextView) view
						.findViewById(R.id.out_of_stock_message);
				holder.circleSaleLayout = (RelativeLayout) view
						.findViewById(R.id.circle_sale_in_grid);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Bundle args = new Bundle();
					args.putString(FragmentRoot.CATEGORY_TITLE_KEY, title);

					Integer refinedPlaceHolderForTaxon = productViewInfoList
							.get(position).placeHolderOfTaxon;
					if (refinedPlaceHolderForTaxon != null) {

						ArrayList<Integer> tempPlaceHolders = new ArrayList<Integer>(
								placeHolders);

						tempPlaceHolders.add(refinedPlaceHolderForTaxon);

						args.putIntegerArrayList(
								FragmentRoot.TAXON_ADDRESS_KEY,
								tempPlaceHolders);

						args.putInt(
								FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY,
								productViewInfoList.get(position).placeHolderOfProduct);
					} else {
						args.putInt(FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY,
								position);
						args.putIntegerArrayList(
								FragmentRoot.TAXON_ADDRESS_KEY, placeHolders);
					}

					fragmentRoot.openItemFragment(args);

				}
			});

			ProductViewInfo currentPVI = productViewInfoList.get(position);

			if (currentPVI.isOutOfStock)
				holder.outOfStockMessage.setVisibility(View.VISIBLE);
			else
				holder.outOfStockMessage.setVisibility(View.GONE);

			holder.title.setText(currentPVI.title);

			holder.categoryTitle.setVisibility(View.GONE);
			List<Integer> taxonIdList = currentPVI.taxonIdList;
			for (Integer id : taxonIdList) {
				String tempCategoryTitle = TaxonomiesStore.getInstance()
						.getNameOfLeafIfStarred(id);
				if (tempCategoryTitle != null) {
					holder.categoryTitle.setVisibility(View.VISIBLE);
					holder.categoryTitle.setText(tempCategoryTitle);
					break;

				}
			}

			if (InitInfoStore.getInstance().shouldShowMultiCurrency()) {
				holder.exchangePrice.setVisibility(View.VISIBLE);
				double multiplier = InitInfoStore.getInstance()
						.getCurrencyMultiplier();
				double priceAfterMultiply = currentPVI.price * multiplier;

				String priceAfterExchange = generateProperStringForPrice(priceAfterMultiply);
				String currencySymbol = Currency.getInstance(
						Locale.getDefault()).getSymbol();
				holder.exchangePrice.setText(("(" + currencySymbol
						+ priceAfterExchange + " approx)").replace(
						InitInfoStore.getInstance().getCurrencySymbol(), ""));

			}

			holder.saleImageView.setVisibility(View.GONE);
			holder.circleSaleLayout.setVisibility(View.GONE);
			holder.originalPrice.setVisibility(View.GONE);
			holder.price.setTextColor(originalPriceColor);

			if (currentPVI.salePrice != null) {
				holder.originalPrice.setVisibility(View.VISIBLE);
				holder.originalPrice.setPaintFlags(holder.originalPrice
						.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

				holder.originalPrice
						.setText(generateProperStringForPrice(currentPVI.price));

				float marginRightForOriginalPrice = TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 5, getResources()
								.getDisplayMetrics());
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.originalPrice
						.getLayoutParams();
				params.setMargins(0, 0, (int) marginRightForOriginalPrice, 0); // substitute
																				// parameters
																				// for
																				// left,
																				// top,
																				// right,
																				// bottom
				holder.price.setLayoutParams(params);

				holder.price
						.setText(generateProperStringForPrice(currentPVI.salePrice));
				holder.price.setTextColor((salePriceColor));

				holder.saleImageView.setVisibility(View.VISIBLE);
				if (InitInfoStore.getInstance().showDiscountBadge()) {
					
					GradientDrawable backgroundGradient = (GradientDrawable)holder.circleSaleLayout.getBackground();
				    backgroundGradient.setColor(Color.parseColor(InitInfoStore.getInstance().getBrandColor()));
					holder.circleSaleLayout.setVisibility(View.VISIBLE);
					TextView percentText = (TextView) holder.circleSaleLayout
							.findViewById(R.id.number_percent_id);

					int percent = (int) ((currentPVI.salePrice / currentPVI.price) * 100);
					percentText.setText("" + percent + "%");
				}

			}

			else {
				holder.price
						.setText(generateProperStringForPrice(currentPVI.price));
			}

			if ((currentPVI.imageUrl != null)) {
				imageLoader.displayImage(currentPVI.imageUrl, holder.imageView,
						options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.progressLayout
										.setVisibility(View.VISIBLE);

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								holder.progressLayout.setVisibility(View.GONE);

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								holder.progressLayout.setVisibility(View.GONE);

							}
						}, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String imageUri,
									View view, int current, int total) {

							}
						});
			} else {
				holder.progressLayout.setVisibility(View.GONE);
				holder.imageView.setImageResource(android.R.color.transparent);

			}

			return view;
		}

		class ViewHolder {
			ImageView imageView;
			ImageView saleImageView;
			TextView title;
			TextView categoryTitle;
			TextView exchangePrice;
			TextView price;
			TextView originalPrice;
			TextView outOfStockMessage;
			RelativeLayout progressLayout;
			RelativeLayout circleSaleLayout;

		}
	}

}

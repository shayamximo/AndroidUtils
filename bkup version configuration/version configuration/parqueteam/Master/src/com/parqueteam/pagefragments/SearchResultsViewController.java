package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parqueteam.App;
import com.parqueteam.R;
import com.parqueteam.json.Product;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.utils.MximoLog;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class SearchResultsViewController extends FragmentPageBase {

	public static final String PRODUCT_LIST_FROM_SEARCH_RESULT = "product_list_from_search_result";
	public static final String TITLE_OF_SEARCH_RESULTS = "title_of_search_results";

	private HashMap<Integer, ArrayList<Product>> taxonToProduct;
	private SearchResultsAdapter searchResultsAdapter;

	private List<Product> productsList;
	private String topBarTitle;;
	private TextView textViewTitle;

	private ArrayList<Integer> positionsOfTaxonTitles;

	@Override
	protected int getLayoutID() {

		return R.layout.search_results_fragment;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(topBarTitle);
	}

	private boolean isSaleFragment() {
		String isSaleFragment = getValueOfParamFromName("title");
		if (isSaleFragment == null)
			return false;
		else
			return isSaleFragment.equals("Sale");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		positionsOfTaxonTitles = new ArrayList<Integer>();
		boolean isSale = isSaleFragment();

		if (isSale) {
			productsList = App.getApp().getSaleStore().getProducts();
			topBarTitle = getValueOfParamFromName("title");
		} else {
			Bundle args = getArguments();
			productsList = args
					.getParcelableArrayList(PRODUCT_LIST_FROM_SEARCH_RESULT);
			topBarTitle = args.getString(TITLE_OF_SEARCH_RESULTS);
		}

		buildTaxonToProductMap();

		ListView listView = (ListView) viewOfFragment
				.findViewById(R.id.listview_searchresults);
		searchResultsAdapter = new SearchResultsAdapter();

		int positionCounter = 0;
		Iterator it = taxonToProduct.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			ArrayList<Product> productListForCurrentTaxon = (ArrayList<Product>) pairs
					.getValue();

			searchResultsAdapter.addSeparatorItem(TaxonomiesStore.getInstance()
					.getNameByTaxonLeafID(((Integer) pairs.getKey())));
			positionsOfTaxonTitles.add(positionCounter);
			positionCounter++;
			for (Product product : productListForCurrentTaxon) {
				searchResultsAdapter.addItem(product);
				positionCounter++;
			}

		}

		listView.setAdapter(searchResultsAdapter);
		textViewTitle = (TextView) viewOfFragment
				.findViewById(R.id.textview_title);
		textViewTitle.setText(searchResultsAdapter.getProductData().get(0)
				.getName());

		listView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Do nothing
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (positionsOfTaxonTitles.contains(firstVisibleItem)) {
					textViewTitle.setText(searchResultsAdapter.getProductData()
							.get(firstVisibleItem).getName());
				}

				// user scrolled up case
				if (positionsOfTaxonTitles.contains(firstVisibleItem + 1)) {
					int firstAddressIndex = positionsOfTaxonTitles
							.indexOf(firstVisibleItem + 1);
					int previous = positionsOfTaxonTitles
							.get(firstAddressIndex - 1);

					textViewTitle.setText(searchResultsAdapter.getProductData()
							.get(previous).getName());
				}

			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	private void buildTaxonToProductMap() {
		taxonToProduct = new HashMap<Integer, ArrayList<Product>>();
		for (Product product : productsList) {

			List<Integer> taxonIdList = product.getTaxonIds();

			if (taxonIdList != null && taxonIdList.size() > 0) {
				int taxonId = taxonIdList.get(0);
				ArrayList<Product> tempProducts = taxonToProduct.get(taxonId);
				if (tempProducts == null) {
					tempProducts = new ArrayList<Product>();
					taxonToProduct.put(taxonId, tempProducts);
				}

				tempProducts.add(product);
			}

		}
	}

	private class SearchResultsAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

		private ArrayList<Product> mData = new ArrayList<Product>();

		private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

		public void addItem(final Product product) {
			mData.add(product);
			notifyDataSetChanged();
		}

		public void addSeparatorItem(final String item) {
			mData.add(new Product(item));
			// save separator position
			mSeparatorsSet.add(mData.size() - 1);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR
					: TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		public int getCount() {
			return mData.size();
		}

		public Product getItem(int position) {
			return mData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public ArrayList<Product> getProductData() {
			return mData;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			int type = getItemViewType(position);

			if (convertView == null) {

				holder = new ViewHolder();
				switch (type) {
				case TYPE_ITEM:
					convertView = inflator.inflate(R.layout.lyt_search, null);
					holder.textView = (TextView) convertView
							.findViewById(R.id.lyt_sch_txt_itemtitle);
					holder.price = (TextView) convertView
							.findViewById(R.id.lyt_sch_txt_price);
					holder.salePrice = (TextView) convertView
							.findViewById(R.id.lyt_sch_txt_sale_price);
					holder.imageView = (ImageView) convertView
							.findViewById(R.id.lyt_sch_img_item);
					holder.progressView = convertView
							.findViewById(R.id.src_result_img_loading);
					holder.ratingBar = (RatingBar) convertView
							.findViewById(R.id.lyt_sch_RatingBar);
					holder.ratingBar.setEnabled(false);
					holder.noRatingsTextView = (TextView) convertView
							.findViewById(R.id.lyt_sch_txt_rattings);
					holder.isProduct = true;
					break;
				case TYPE_SEPARATOR:

					convertView = inflator.inflate(
							R.layout.seperator_layout_in_search, null);
					holder.textView = (TextView) convertView
							.findViewById(R.id.textSeparator);
					holder.isProduct = false;
					break;
				}

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(mData.get(position).getName());

			if (holder.isProduct) {
				Product product = mData.get(position);

				holder.price.setText(generateProperStringForPrice(product
						.getPrice()));

				if (product.isOnSale()) {

					holder.salePrice
							.setText(generateProperStringForPrice(product
									.getSalePrice()));
					holder.price.setPaintFlags(holder.price.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				}
				float rating = product.getAverageRating();
				if (rating > 0) {
					holder.ratingBar.setVisibility(View.VISIBLE);
					holder.ratingBar.setRating(rating);
					holder.noRatingsTextView.setText(String.valueOf(product
							.getReviewsCount())
							+ " "
							+ getResources().getString(R.string.ratings));
				} else {
					holder.ratingBar.setVisibility(View.GONE);
					holder.noRatingsTextView.setText(getResources().getString(
							R.string.no_raters));
				}

				if (product.getMainImageUrlThumbNailSize() != null) {
					final View tempProgressView = holder.progressView;
					final ImageView tempImageView = holder.imageView;
					imageLoader.displayImage(
							product.getMainImageUrlThumbNailSize(),
							holder.imageView, options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri,
										View view) {
									tempProgressView
											.setVisibility(View.VISIBLE);
									tempImageView.setVisibility(View.GONE);

								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {

								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									tempProgressView.setVisibility(View.GONE);
									tempImageView.setVisibility(View.VISIBLE);

								}
							}, new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String imageUri,
										View view, int current, int total) {

								}
							});
				}

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Product product = mData.get(position);
						Bundle args = new Bundle();
						args.putBoolean(FragmentRoot.FROM_SEARCH_PAGE, true);
						args.putBoolean(FragmentRoot.SHOW_ONLY_CHOSEN_PRODUCT,
								true);
						args.putSerializable(FragmentRoot.SEARCH_PAGE_PRODUCT,
								product);
						fragmentRoot.openItemFragment(args);

					}
				});
			}

			return convertView;
		}

		public class ViewHolder {
			public TextView textView;
			public TextView price;
			public TextView salePrice;
			ImageView imageView;
			public View progressView;
			RatingBar ratingBar;
			TextView noRatingsTextView;
			public boolean isProduct;

		}
	}
}

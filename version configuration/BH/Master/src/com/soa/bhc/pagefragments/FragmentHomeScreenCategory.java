package com.soa.bhc.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.soa.bhc.R;
import com.soa.bhc.CustomizedViews.FrameLayoutThatInflatesWhenAllViewsInitialized;
import com.soa.bhc.category.CategoryTitlesUIItem;
import com.soa.bhc.json.Params;
import com.soa.bhc.json.helpers.CatgoryAttributes;
import com.soa.bhc.json.helpers.HomeScreenImageDetails;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.StoreLoadDataListener;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.soa.bhc.utils.MximoFlurryAgent;
import com.soa.bhc.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class FragmentHomeScreenCategory extends FragmentHomeScreen {

	ArrayList<CategoryTitlesUIItem> categoryTitlesUIItemArray = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home_screen;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MximoFlurryAgent.logEvent(MximoFlurryAgent.HOME_PAGE_VIEWED);
		categoryTitlesUIItemArray = new ArrayList<CategoryTitlesUIItem>();
		InitInfoStore.getInstance().loadDataIfNotInitialized(
				new StoreLoadDataListener() {

					@Override
					public void onFinish() {

						// Here we recived and processed the app data
						// request.
						TaxonomiesStore.getInstance().loadDataIfNotInitialized(
								new StoreLoadDataListener() {

									@Override
									public void onFinish() {

										// ArrayList<Integer>
										// arr
										// =
										// new
										// ArrayList<Integer>();
										// arr.add(0);
										// arr.add(3);
										// arr.add(1);
										// arr.add(4);
										//
										// Taxons
										// taxons
										// =
										// TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(arr);

										final List<List<String>> catagoryNames = TaxonomiesStore
												.getInstance()
												.getTaxonsTopLevelNamesByIndex();
										final List<Params> categoriesParams = InitInfoStore
												.getInstance()
												.getCategoryParams();

										// Initialize
										// the
										// catagory
										// blocks
										getActivity().runOnUiThread(
												new Runnable() {

													@Override
													public void run() {
														CatgoryAttributes catgoryAttributes = new CatgoryAttributes(
																categoriesParams,
																TaxonomiesStore
																		.getInstance()
																		.getTaxonsTopLevelNames()
																		.size(),
																getActivity()
																		.getWindowManager()
																		.getDefaultDisplay(),
																getActivity());

														int x = catgoryAttributes
																.getFrame()
																.getX();
														int y = catgoryAttributes
																.getFrame()
																.getY();

														RelativeLayout.LayoutParams paramsOfLayout = new RelativeLayout.LayoutParams(
																RelativeLayout.LayoutParams.MATCH_PARENT,
																RelativeLayout.LayoutParams.MATCH_PARENT);
														paramsOfLayout.leftMargin = x;
														paramsOfLayout.topMargin = y;

														FrameLayout frameOfTextViewCategory = (FrameLayout) viewOfFragment
																.findViewById(R.id.frame);
														frameOfTextViewCategory
																.setLayoutParams(paramsOfLayout);

														for (int i = 0; i < catagoryNames
																.size(); i++) {
															List<String> categoriesList = catagoryNames
																	.get(i);

															for (int j = 0; j < categoriesList
																	.size(); j++) {
																categoryTitlesUIItemArray
																		.add(new CategoryTitlesUIItem(
																				categoriesList
																						.get(j),
																				i,
																				j));
															}

														}
														if (InitInfoStore
																.getInstance()
																.isAppLanguageHebrew()) {
															categoryTitlesUIItemArray = organizeCatagoryTitlesInOtherDirection(
																	categoryTitlesUIItemArray,
																	catgoryAttributes
																			.getNumberofrows(),
																	catgoryAttributes
																			.getNumberofcolumns());
														}

														TableLayout tableLayout = new TableLayout(
																getActivity());

														FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
																TableRow.LayoutParams.MATCH_PARENT,
																TableRow.LayoutParams.MATCH_PARENT);

														tableLayout
																.setLayoutParams(lp);
														tableLayout
																.setStretchAllColumns(true);

														tableLayout.setPadding(
																0, 0, 0, 0);

														int index = 0;

														while (index < categoryTitlesUIItemArray
																.size()) {

															TableRow tr = new TableRow(
																	getActivity());

															for (int k = 0; k < catgoryAttributes
																	.getNumberofcolumns(); k++) {
																if (index < categoryTitlesUIItemArray
																		.size()) {

																	View view = inflator
																			.inflate(
																					R.layout.category_titles_row_grid,
																					tr,
																					false);

																	TextView textView = (TextView) view
																			.findViewById(R.id.home_txt);

																	final CategoryTitlesUIItem item = categoryTitlesUIItemArray
																			.get(index);
																	textView.setText(item
																			.getName());

																	textView.setOnClickListener(new OnClickListener() {

																		@Override
																		public void onClick(
																				View v) {

																			ArrayList<Integer> arr = new ArrayList<Integer>();
																			arr.add(item
																					.getTaxamonyID());
																			arr.add(item
																					.getAddress());
																			fragmentRoot
																					.onCategoryChosen(
																							arr,
																							null,
																							item.getName(),
																							true);

																		}
																	});
																	LinearLayout.LayoutParams paramsOfTextView = (LinearLayout.LayoutParams) textView
																			.getLayoutParams();

																	int marginLeft = 1;
																	if (k == 0)
																		marginLeft = 0;

																	int marginBottom = 1;
																	if (index >= ((catgoryAttributes
																			.getNumberofrows() - 1) * (catgoryAttributes
																			.getNumberofcolumns())))
																		marginBottom = 0;
																	paramsOfTextView
																			.setMargins(
																					marginLeft,
																					1,
																					1,
																					marginBottom);

																	textView.setBackgroundColor(Color
																			.parseColor(catgoryAttributes
																					.getColor()));

																	textView.getBackground()
																			.setAlpha(
																					(int) (255 * catgoryAttributes
																							.getAlpha()));

																	paramsOfTextView.height = catgoryAttributes
																			.getCellHeight();
																	paramsOfTextView.width = catgoryAttributes
																			.getCellWidth();
																	textView.setLayoutParams(paramsOfTextView);
																	textView.setTextSize(
																			TypedValue.COMPLEX_UNIT_SP,
																			catgoryAttributes
																					.getFontsize());
																	textView.setTypeface(catgoryAttributes
																			.getFont());

																	textView.setGravity(catgoryAttributes
																			.getTextAlignement());

																	textView.setPadding(
																			catgoryAttributes
																					.getMarginLeft(),
																			0,
																			0,
																			0);

																	tr.addView(view);
																	index++;
																}
															}

															tableLayout
																	.addView(tr);

														}
														frameOfTextViewCategory
																.addView(tableLayout);

													}
												});

										// Initalize
										// the
										// backgroud
										ArrayList<HomeScreenImageDetails> homeScreenImagesDetailsList = InitInfoStore
												.getInstance()
												.getHomeScreenImageDetails();
										final FrameLayoutThatInflatesWhenAllViewsInitialized fl = (FrameLayoutThatInflatesWhenAllViewsInitialized) viewOfFragment
												.findViewById(R.id.frame_image);
										fl.setNumberOfViews(homeScreenImagesDetailsList
												.size());
										for (final HomeScreenImageDetails homeScreenImageDetails : homeScreenImagesDetailsList) {
											final ImageView mainImageView = new ImageView(
													getActivity());
											mainImageView
													.setLayoutParams(new LayoutParams(
															LayoutParams.MATCH_PARENT,
															LayoutParams.MATCH_PARENT));

											getActivity().runOnUiThread(
													new Runnable() {

														@Override
														public void run() {
															imageLoader
																	.displayImage(
																			homeScreenImageDetails
																					.getUrl(),
																			mainImageView,
																			options,
																			new ImageLoadingListener() {

																				@Override
																				public void onLoadingStarted(
																						String imageUri,
																						View view) {
																					// TODO
																					// Auto-generated
																					// method
																					// stub

																				}

																				@Override
																				public void onLoadingFailed(
																						String imageUri,
																						View view,
																						FailReason failReason) {
																					// TODO
																					// Auto-generated
																					// method
																					// stub

																				}

																				@Override
																				public void onLoadingComplete(
																						String imageUri,
																						View view,
																						Bitmap loadedImage) {
																					try {
																						Point size = UtilityFunctions
																								.getScreenDimensions();

																						int height = homeScreenImageDetails
																								.getFrame()
																								.getHeight();
																						int width = homeScreenImageDetails
																								.getFrame()
																								.getWidth();

																						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
																								width,
																								height);
																						params.leftMargin = homeScreenImageDetails
																								.getFrame()
																								.getX();
																						params.topMargin = homeScreenImageDetails
																								.getFrame()
																								.getY();
																						mainImageView
																								.setLayoutParams(params);
																						mainImageView
																								.setScaleType(ScaleType.CENTER_CROP);
																						fl.addViewToSmartFrame(
																								mainImageView,
																								homeScreenImageDetails
																										.getOrderInFrame());
																					} catch (Exception e) {
																						// TODO:
																						// handle
																						// exception
																					}

																				}

																				@Override
																				public void onLoadingCancelled(
																						String imageUri,
																						View view) {

																				}
																			});

														}
													});

										}

									}

									@Override
									public void onError() {
										// Do nothing

									}
								});

					}

					@Override
					public void onError() {
						// Do nothing

					}
				});

		super.onActivityCreated(savedInstanceState);
	}

	// precondition: each row has equal amount of cells
	private static <T> ArrayList<T> organizeCatagoryTitlesInOtherDirection(
			ArrayList<T> input, int numberOfRows, int numberOfColumns) {

		ArrayList<T> returnList = new ArrayList<T>();
		for (int i = 0; i < numberOfRows; i++) {
			int lastIndex = (i * numberOfColumns) + numberOfColumns;

			for (int j = lastIndex - 1; j >= (i * numberOfColumns); j--) {
				returnList.add(input.get(j));
			}
		}
		return returnList;
	}

}

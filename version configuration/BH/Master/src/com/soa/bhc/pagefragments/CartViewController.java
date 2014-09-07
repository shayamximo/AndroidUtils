package com.soa.bhc.pagefragments;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soa.bhc.App;
import com.soa.bhc.R;
import com.soa.bhc.json.OptionValues;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.Variant;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.userselection.UserSelectionItem;
import com.soa.bhc.userselection.UserSelectionStore;
import com.soa.bhc.utils.MximoFlurryAgent;

public class CartViewController extends FragmentPageBase implements
		ICustomizedEmptyListFragment {

	private TextView total;
	private UserSelectionStore cart;
	private ListProductAdaptor lpaAdaptor;

	protected int getLayoutID() {

		if (shouldCustomizeEmptyList(this)) {
			return R.layout.layout_empty_list_background_configured;
		}

		return R.layout.my_bag;
	}

	@Override
	public void onResume() {
		super.onResume();
		fragmentRoot.showTabs();
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.mybag));

		final ImageView edit = actionBarContorller.imageViewOnRightStandalone;

		actionBarContorller.setEditDrawable(edit);

		if (!shouldCustomizeEmptyList(this))
			makeViewsVisible(edit);

		edit.setTag(Boolean.valueOf(true));
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Boolean isShowingEdit = (Boolean) view.getTag();

				if (isShowingEdit)
					actionBarContorller.setAcceptDrawable(edit);
				else
					actionBarContorller.setEditDrawable(edit);
				view.setTag(!isShowingEdit);

				lpaAdaptor.toggleDeleteButtonsVisibility();
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		if (shouldCustomizeEmptyList(this)) {
			loadEmptyListFragment(this);
		} else {
			MximoFlurryAgent.logEvent(MximoFlurryAgent.CART_PAGE_VIEWED);
			setViewsToBrandColor(R.id.my_bag_btn_checkout);
			getImagesSize();
			cart = App.getApp().getCart();
			total = (TextView) viewOfFragment
					.findViewById(R.id.my_bag_wish_txt_totalprice);
			total.setText(generateProperStringForPrice(cart
					.getTotalAmountOfPrice()));

			List<UserSelectionItem> chosenProducts = cart.getAllCartItems();
			lpaAdaptor = new ListProductAdaptor(getActivity(),
					R.layout.lyt_mybag, chosenProducts);
			((ListView) viewOfFragment.findViewById(R.id.cart_listview))
					.setAdapter(lpaAdaptor);

			Button checkOut = (Button) viewOfFragment
					.findViewById(R.id.my_bag_btn_checkout);
			checkOut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (cart.getAllCartItems().size() == 0)
						return;
					fragmentRoot.openUserInfoFragment(null);

				}
			});
		}

		super.onActivityCreated(savedInstanceState);
	}

	private void getImagesSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		int h = dm.heightPixels;

	}

	public class ListProductAdaptor extends ArrayAdapter<UserSelectionItem> {

		private List<UserSelectionItem> listOfCartItems;
		int layoutResourceId;
		private ArrayList<ImageView> deleteButtons;
		private boolean areDeleteButtonsVisible;

		public ListProductAdaptor(Context context, int layoutResourceId,
				List<UserSelectionItem> listOfCartItems) {
			super(context, layoutResourceId, listOfCartItems);
			this.layoutResourceId = layoutResourceId;
			this.listOfCartItems = listOfCartItems;
			deleteButtons = new ArrayList<ImageView>();
			areDeleteButtonsVisible = false;

		}

		@Override
		public int getCount() {
			return listOfCartItems.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View rowView = convertView;

			if (rowView == null) {
				ViewHolder holder = new ViewHolder();

				rowView = inflator.inflate(layoutResourceId, null);

				holder.textName = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemName);
				holder.textDescription = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemDes);
				holder.textPrice = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemprise);
				holder.exchangePrice = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_item_exchange_price);
				holder.buttonDelete = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_btnDelete);
				deleteButtons.add(holder.buttonDelete);

				holder.imageItemPhoto = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_img_item);
				holder.imageSale = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_img_sale);
				holder.textSalePrice = (TextView) rowView
						.findViewById(R.id.tv_saleprice);
				// holder.imageSale.setVisibility(View.GONE);

				holder.textQuantity = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_qty);
				holder.imageMinusOne = (ImageButton) rowView
						.findViewById(R.id.lyt_mybag_img_qtyminus);
				holder.imagePlusOne = (ImageButton) rowView
						.findViewById(R.id.lyt_mybag_img_qtyplus);
				holder.textVariants = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemVariants);
				holder.circleSaleLayout = (RelativeLayout)rowView
						.findViewById(R.id.circle_sale_in_gallery);
				rowView.setTag(holder);

			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			final UserSelectionItem currentCartItem = listOfCartItems
					.get(position);
			final Product master = currentCartItem.getProduct();
			final Variant variant = currentCartItem.getVariant();

			rowView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Bundle args = new Bundle();
					args.putString(FragmentRoot.CATEGORY_TITLE_KEY,
							getResources().getString(R.string.mybag));
					args.putBoolean(FragmentRoot.SHOW_ONLY_CHOSEN_PRODUCT, true);

					args.putInt(FragmentRoot.CART_ITEM_ADDRESS, position);

					fragmentRoot.openItemFragment(args);
				}
			});

			// This is horrible workaround just to make the buttons of + and -
			// on the bottom, appear on the bottom.
			// this just makes sure that the top textview will be two lines
			holder.textName.setText(master.getName()
					+ "                                     \n");
			holder.textDescription.setText(master.getDescription());
			holder.textPrice.setText(generateProperStringForPrice(master
					.getPrice()));

			if (InitInfoStore.getInstance().shouldShowMultiCurrency()) {
				holder.exchangePrice.setVisibility(View.VISIBLE);
				double multiplier = InitInfoStore.getInstance()
						.getCurrencyMultiplier();
				double priceAfterMultiply = master.getFinalPrice() * multiplier;

				String priceAfterExchange = generateProperStringForPrice(priceAfterMultiply);
				String currencySymbol = Currency.getInstance(
						Locale.getDefault()).getSymbol();
				holder.exchangePrice.setText(("(" + currencySymbol
						+ priceAfterExchange + "\n approx)").replace(
						InitInfoStore.getInstance().getCurrencySymbol(), ""));

			}

			imageLoader.displayImage(master.getMainImageUrlThumbNailSize(),
					holder.imageItemPhoto, options, null);

			if (master.isOnSale()) {
				holder.imageSale.setVisibility(View.VISIBLE);
				holder.textSalePrice.setVisibility(View.VISIBLE);
				holder.textPrice
						.setText(generateProperStringForPrice(master
								.getSalePrice()));
				holder.textPrice.setTextColor(getResources().getColor(
						R.color.red));
				holder.textSalePrice.setText(generateProperStringForPrice(master.getPrice()));
				holder.textSalePrice.setPaintFlags(holder.textPrice.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				
				if (InitInfoStore.getInstance().showDiscountBadge()) {
					
					GradientDrawable backgroundGradient = (GradientDrawable)holder.circleSaleLayout.getBackground();
				    backgroundGradient.setColor(Color.parseColor(InitInfoStore.getInstance().getBrandColor()));
					holder.circleSaleLayout.setVisibility(View.VISIBLE);
					TextView percentText = (TextView) holder.circleSaleLayout
							.findViewById(R.id.number_percent_id);

					int percent = (int) ((master.getSalePrice() / master.getPrice()) * 100);
					percentText.setText("" + percent + "%");
				}
			}

			else {
				holder.imageSale.setVisibility(View.GONE);
				holder.textSalePrice.setVisibility(View.GONE);
			}

			holder.textQuantity.setText(Integer.toString(currentCartItem
					.getQuantity()));

			if (variant != null) {
				holder.textVariants.setVisibility(View.VISIBLE);
				List<OptionValues> optionValuesList = variant.getOptionValues();

				holder.textVariants.setText("");
				for (OptionValues optionValues : optionValuesList) {
					String currentText = holder.textVariants.getText()
							.toString();
					holder.textVariants.setText(currentText
							+ optionValues.getOptionTypeValue() + ":"
							+ optionValues.getName() + "   ");
				}
			} else
				holder.textVariants.setText("");

			holder.buttonDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listOfCartItems.remove(currentCartItem);
					notifyDataSetChanged();
					cart.removeCartItem(currentCartItem);
					total.setText(generateProperStringForPrice(cart
							.getTotalAmountOfPrice()));

				}
			});

			holder.imageMinusOne.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int currentQuantity = Integer.parseInt(holder.textQuantity
							.getText().toString());
					if (currentQuantity > 1) {
						currentCartItem.decrementQuantity();
						cart.decrementCartItem(currentCartItem);
						holder.textQuantity.setText(Integer
								.toString(currentQuantity - 1));
						total.setText(generateProperStringForPrice(cart
								.getTotalAmountOfPrice()));

					}

				}
			});

			holder.imagePlusOne.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int currentQuantity = Integer.parseInt(holder.textQuantity
							.getText().toString());
					currentCartItem.incrementQuantity();
					cart.incrementCartItem(currentCartItem);
					holder.textQuantity.setText(Integer
							.toString(currentQuantity + 1));
					total.setText(generateProperStringForPrice(cart
							.getTotalAmountOfPrice()));

				}
			});

			return rowView;
		}

		public void toggleDeleteButtonsVisibility() {
			int visibility;
			if (areDeleteButtonsVisible)
				visibility = View.GONE;
			else
				visibility = View.VISIBLE;
			for (ImageView deleteButton : deleteButtons) {
				deleteButton.setVisibility(visibility);
			}
			areDeleteButtonsVisible = !areDeleteButtonsVisible;
		}

		private class ViewHolder {
			ImageView imageItemPhoto, imageSale;
			TextView textName, textDescription, textPrice, textQuantity,
					textVariants, exchangePrice, textSalePrice;
			ImageButton imagePlusOne, imageMinusOne;
			ImageView buttonDelete;
			RelativeLayout circleSaleLayout;

		}

	}

	@Override
	public boolean isEmptyList() {
		if (cart == null)
			cart = App.getApp().getCart();
		return (cart == null || cart.getAllCartItems().size() == 0);
	}

	@Override
	public int getImageResourceIdIfNoneDefinedFromServer() {
		return -1;
	}

}

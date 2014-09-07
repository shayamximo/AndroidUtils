package com.renuar.userselection;

import java.util.ArrayList;
import java.util.List;

import com.renuar.App;
import com.renuar.json.Product;
import com.renuar.json.Variant;
import com.renuar.json.stores.StoreLoadDataListener;
import com.renuar.utils.UtilityFunctions;

public class UserSelectionStore {

	private List<UserSelectionItem> cartItemsList;

	private IOnAmountOfItemsInListChange iOnAmountOfItemsInListChange;

	private IonItemsFinishedLoadingFromDataBase ionItemsFinishedLoadingFromDataBase;

	private DatabaseHandler db;

	private boolean itemsFinishedLoadingOnInit;

	public UserSelectionStore(String tableNaem) {
		itemsFinishedLoadingOnInit = false;
		cartItemsList = new ArrayList<UserSelectionItem>();
		db = new DatabaseHandler(App.getApp(), tableNaem);
		final List<UserSelectionItemDB> cartItemDBList = db.getAllCartItemDB();

		for (final UserSelectionItemDB cidb : cartItemDBList) {

			final Product product = new Product(cidb.getProductID());
			product.loadDataIfNotInitialized(new StoreLoadDataListener() {

				@Override
				public void onFinish() {
					Variant chosenVariant = product.getVariantByID(cidb
							.getVariantID());
					int Quantity = cidb.getQuantity();
					UserSelectionItem userSelectionItem = new UserSelectionItem(
							product, chosenVariant, Quantity);
					synchronized (UserSelectionStore.this) {

						if (userSelectionItem.isItemOutOfStock()) {
							cartItemDBList.remove(cidb);
						} else {
							cartItemsList.add(userSelectionItem);
						}

						onFinishUpdatingCartFromDB(cartItemDBList);

					}

				}

				@Override
				public void onError() {
					synchronized (UserSelectionStore.this) {
						cartItemDBList.remove(cidb);
						removeCartItem(cidb);
						onFinishUpdatingCartFromDB(cartItemDBList);
					}
				}
			});
		}

		if (cartItemDBList.isEmpty())
			itemsFinishedLoadingOnInit = true;

	}

	private void onFinishUpdatingCartFromDB(
			List<UserSelectionItemDB> cartItemDBList) {
		if (cartItemsList.size() == cartItemDBList.size()) {
			if (ionItemsFinishedLoadingFromDataBase != null)
				ionItemsFinishedLoadingFromDataBase
						.onItemsFinishedLoadingFromDataBase(cartItemsList
								.size());
			itemsFinishedLoadingOnInit = true;
		}
	}

	public boolean doesCartItemExist(UserSelectionItem userSelectionItem) {
		return cartItemsList.contains(userSelectionItem);
	}

	public void addCartItem(Product product, Variant variant) {

		UserSelectionItem cartItemToAdd = new UserSelectionItem(product,
				variant, 1);

		if (cartItemsList.contains(cartItemToAdd)) {
			int location = cartItemsList.indexOf(cartItemToAdd);
			cartItemToAdd = cartItemsList.get(location);
			cartItemToAdd.incrementQuantity();
			db.updateCartItem(cartItemToAdd);
		} else {
			cartItemsList.add(cartItemToAdd);
			db.addCartItem(cartItemToAdd);
		}

		if (iOnAmountOfItemsInListChange != null)
			iOnAmountOfItemsInListChange.onItemsAmountChange(cartItemsList
					.size());
	}

	// Note: this is shallow copy!
	public List<UserSelectionItem> getAllCartItems() {

		ArrayList<UserSelectionItem> copyCartItem = new ArrayList<UserSelectionItem>();

		for (UserSelectionItem userSelectionItem : cartItemsList) {
			copyCartItem.add(new UserSelectionItem(userSelectionItem
					.getProduct(), userSelectionItem.getVariant(),
					userSelectionItem.getQuantity()));
		}

		return copyCartItem;
	}

	public UserSelectionItem getCartItemAtAddress(int address) {
		UserSelectionItem hardCartItem = cartItemsList.get(address);
		return new UserSelectionItem(hardCartItem.getProduct(),
				hardCartItem.getVariant(), hardCartItem.getQuantity());
	}

	public void removeCartItem(UserSelectionItem userSelectionItem) {
		cartItemsList.remove(userSelectionItem);
		db.deleteCartItem(userSelectionItem);
		if (iOnAmountOfItemsInListChange != null)
			iOnAmountOfItemsInListChange.onItemsAmountChange(cartItemsList
					.size());
	}

	public void removeAllCartItems() {
		for (UserSelectionItem userSelectionItem : cartItemsList) {
			db.deleteCartItem(userSelectionItem);
		}
		cartItemsList.clear();

	}

	public void emptySelectionStore() {

		int size = cartItemsList.size();
		for (int i = 0; i < size; i++) {
			synchronized (this) {
				removeCartItem(cartItemsList.get(0));
			}
		}
	}

	private void removeCartItem(UserSelectionItemDB cidb) {
		db.deleteCartItem(cidb);
	}

	public void incrementCartItem(UserSelectionItem userSelectionItem) {
		int index = cartItemsList.indexOf(userSelectionItem);
		UserSelectionItem cartItemActual = cartItemsList.get(index);
		cartItemActual.incrementQuantity();
		db.updateCartItem(cartItemActual);

		if (iOnAmountOfItemsInListChange != null)
			iOnAmountOfItemsInListChange.onItemsAmountChange(cartItemsList
					.size());
	}

	public void decrementCartItem(UserSelectionItem userSelectionItem) {
		int index = cartItemsList.indexOf(userSelectionItem);
		UserSelectionItem cartItemActual = cartItemsList.get(index);
		cartItemActual.decrementQuantity();
		db.updateCartItem(cartItemActual);

		if (iOnAmountOfItemsInListChange != null)
			iOnAmountOfItemsInListChange.onItemsAmountChange(cartItemsList
					.size());
	}

	public double getTotalAmountOfPrice() {
		double amount = 0;
		for (UserSelectionItem userSelectionItem : cartItemsList) {
			amount += userSelectionItem.getTotalAmountOfCartItemPrice();
		}
		return UtilityFunctions.formatDouble(amount);
	}

	public interface IOnAmountOfItemsInListChange {
		public void onItemsAmountChange(int numberOfCartItems);
	}

	public void setOnItemsChangeListener(
			IOnAmountOfItemsInListChange iOnAmountOfItemsInListChange) {
		this.iOnAmountOfItemsInListChange = iOnAmountOfItemsInListChange;
	}

	public interface IonItemsFinishedLoadingFromDataBase {
		public void onItemsFinishedLoadingFromDataBase(int numberOfCartItems);
	}

	public void setOnItemsFinishedLoadingFromDataBase(
			IonItemsFinishedLoadingFromDataBase ionItemsFinishedLoadingFromDataBase) {
		this.ionItemsFinishedLoadingFromDataBase = ionItemsFinishedLoadingFromDataBase;
		if (itemsFinishedLoadingOnInit)
			ionItemsFinishedLoadingFromDataBase
					.onItemsFinishedLoadingFromDataBase(cartItemsList.size());
	}

}

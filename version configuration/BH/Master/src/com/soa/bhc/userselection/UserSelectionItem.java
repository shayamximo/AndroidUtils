package com.soa.bhc.userselection;

import com.soa.bhc.json.Product;
import com.soa.bhc.json.Variant;

public class UserSelectionItem {

	private Product product;
	private Variant variant;
	private int quantity;

	public UserSelectionItem(Product product, Variant variant, int quantity) {
		super();
		this.product = product;
		this.variant = variant;
		this.quantity = quantity;

	}

	public double getTotalAmountOfCartItemPrice() {
		double price = product.getFinalPrice();
		return (price * quantity);

	}

	public boolean isItemOutOfStock() {
		if (variant != null)
			return variant.isOutOfStock();
		else
			return product.isOutOfStock();
	}

	public Product getProduct() {
		return product;
	}

	public Variant getVariant() {
		return variant;
	}

	private Variant getMasterVariant() {
		return product.getMaster();
	}

	public Variant getVariantAndReturnMasterIfNone() {
		if (variant == null)
			return getMasterVariant();
		return variant;
	}

	public int getQuantity() {
		return quantity;
	}

	public void incrementQuantity() {
		quantity++;
	}

	public void decrementQuantity() {
		quantity--;
	}

	@Override
	public boolean equals(Object o) {
		UserSelectionItem otherCartItem = ((UserSelectionItem) (o));

		if ((otherCartItem.getVariant() == null))
			return ((otherCartItem.getProduct().equals(product)) && (getVariant() == null));

		else
			return (otherCartItem.getProduct().equals(product) && otherCartItem
					.getVariant().equals(variant));

	}

}

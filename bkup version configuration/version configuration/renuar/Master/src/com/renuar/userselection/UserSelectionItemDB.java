package com.renuar.userselection;

public class UserSelectionItemDB {

	private int productID;
	private int variantID;
	private int quantity;
	private String id;

	private String createID() {
		return Integer.toString(productID) + Integer.toString(variantID);
	}

	public UserSelectionItemDB(UserSelectionItem userSelectionItem) {
		productID = userSelectionItem.getProduct().getId();

		if (userSelectionItem.getVariant() == null)
			variantID = 0;
		else
			variantID = userSelectionItem.getVariant().getId();
		quantity = userSelectionItem.getQuantity();
		id = createID();
	}

	public UserSelectionItemDB(int productID, int variantID, int quantity) {
		super();
		this.productID = productID;
		this.variantID = variantID;
		this.quantity = quantity;
		id = createID();
	}

	public int getProductID() {
		return productID;
	}

	public int getVariantID() {
		return variantID;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getID() {
		return id;
	}

	@Override
	public String toString() {

		return "Product = " + Integer.toString(productID) + " " + "Variant = "
				+ Integer.toString(variantID) + " Quantity= "
				+ Integer.toString(quantity);
	}

}

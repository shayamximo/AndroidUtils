package com.soa.bhc.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.json.stores.Store;
import com.soa.bhc.utils.ConfigurationFile;

public class Product extends Store implements Serializable, Parcelable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6880708976391680929L;

	// This constructor is for horrible workaround in SearchResultsAdapter,
	// change when there is time.
	public Product(String name) {
		this.name = name;
	}

	public Product(int id, String name, String sku, double price) {
		super();
		this.id = id;
		this.name = name;
		this.sku = sku;
		this.price = price;
	}

	public Product(int id) {
		this.id = id;

	}

	@SerializedName("product_url")
	protected String productUrl;

	@SerializedName("id")
	protected int id;

	@SerializedName("avg_rating")
	protected float averageRating;
	@SerializedName("reviews_count")
	protected int reviewsCount;

	@SerializedName("taxon_ids")
	protected List<Integer> taxonIds;

	@SerializedName("name")
	protected String name;

	@SerializedName("sku")
	protected String sku;

	@SerializedName("description")
	protected String description;

	@SerializedName("price")
	protected double price;

	@SerializedName("images")
	protected List<Images> images;

	@SerializedName("variants")
	protected List<Variant> variants;

	@SerializedName("sale_price")
	protected Double salePrice;

	@SerializedName("on_demand")
	protected boolean onDemand;

	@SerializedName("count_on_hand")
	protected int countOnHand;

	public String getMainImageUrlThumbNailSize() {
		if (images.size() > 0)
			return images.get(0).getImageUrl()
					+ GlobalConstants.IMAGE_THUMB_SIZE;
		else
			return null;
	}

	public String getMainImageUrl() {
		return images.get(0).getImageUrl();
	}

	public boolean isImageArrayEmpty() {
		return ((images == null) || (images.size() == 0));
	}

	public List<String> getAllImageUrlsFullSize() {

		ArrayList<String> urlArray = new ArrayList<String>();
		for (Images image : images) {
			urlArray.add(image.getImageUrl() + "&size=phone");
		}
		return urlArray;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Double getSalePrice() {

		if (salePrice != null)
			return salePrice;

		if (variants != null) {
			for (Variant variant : variants) {

				if (variant.getSalePrice() != null)
					return variant.getSalePrice();
			}
		}

		return null;
	}

	public double getFinalPrice() {
		Double salePrice = getSalePrice();
		if (salePrice != null)
			return salePrice;
		return price;
	}

	public boolean isOnSale() {
		return (getSalePrice() != null);
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected class Images {
		@SerializedName("image_method")
		protected String imageUrl;

		public String getImageUrl() {
			return imageUrl;
		}

	}

	public List<Variant> getVariants() {
		return variants;
	}

	public String getSku() {
		if (sku != null)
			return sku;
		else
			return variants.get(0).getSku();
	}

	public String getDescription() {
		return description;
	}

	public boolean isOutOfStock() {

		if (isLeaf())
			return ((onDemand == false) && (countOnHand == 0));
		else {

			for (int i = 1; i < variants.size(); i++) {

				if (!variants.get(i).isOutOfStock())
					return false;
			}
			return true;
		}
	}

	public int getId() {
		return id;
	}

	@Override
	protected Store getStore() {
		return this;
	}

	@Override
	protected void setStore(Store store) {
		Product productFromServer = (Product) store;
		this.id = productFromServer.getId();
		this.name = productFromServer.getName();
		this.sku = productFromServer.getSku();
		this.description = productFromServer.getDescription();
		this.price = productFromServer.getPrice();
		this.images = productFromServer.images;
		this.variants = productFromServer.getVariants();
		this.onDemand = productFromServer.onDemand;
		this.countOnHand = productFromServer.countOnHand;
		this.averageRating = productFromServer.averageRating;
		this.taxonIds = productFromServer.taxonIds;

	}

	@Override
	protected String getUrl() {

		int id = getId();
		String url = GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/products/" + id + "?format=json";
		return url;
	}

	public Variant getVariantByID(int variantID) {
		Variant variantReturn = null;

		for (Variant variant : variants) {
			if (variantID == variant.getId())
				return variant;
		}

		return variantReturn;
	}

	@Override
	public String toString() {
		return "ID: " + id;
	}

	@Override
	public boolean equals(Object o) {
		Product product = ((Product) (o));
		return (product.getId() == id);
	}

	public Variant getMaster() {
		return variants.get(0);
	}

	public boolean isLeaf() {
		// if size equals one it's a master
		return ((variants == null) || variants.size() == 1);
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public List<Integer> getTaxonIds() {
		return taxonIds;
	}

	public int getReviewsCount() {
		return reviewsCount;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}

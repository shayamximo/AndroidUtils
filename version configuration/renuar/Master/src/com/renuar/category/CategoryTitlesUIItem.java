package com.renuar.category;

/**
 * 
 * @author manish.s
 * 
 */

public class CategoryTitlesUIItem {

	String name;
	int address;
	int taxamonyAdress;

	public CategoryTitlesUIItem(String name, int taxamonyID, int address) {
		super();
		this.name = name;
		this.taxamonyAdress = taxamonyID;
		this.address = address;

	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getTaxamonyID() {
		return taxamonyAdress;
	}

	public void setTaxamonyID(int taxamonyID) {
		this.taxamonyAdress = taxamonyID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

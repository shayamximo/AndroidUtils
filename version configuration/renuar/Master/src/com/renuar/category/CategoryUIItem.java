package com.renuar.category;

/**
 * 
 * @author manish.s
 * 
 */

public class CategoryUIItem {

	String name;
	String url;

	public CategoryUIItem(String name, String url) {
		super();
		this.name = name;
		this.url = url;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

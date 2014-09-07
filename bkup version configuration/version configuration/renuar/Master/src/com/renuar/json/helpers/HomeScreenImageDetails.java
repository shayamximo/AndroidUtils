package com.renuar.json.helpers;

public class HomeScreenImageDetails {

	private String url;
	Frame frame;
	private int orderInFrame;

	public HomeScreenImageDetails(String url, String frameString,
			int orderInFrame) {
		this.url = url;
		this.orderInFrame = orderInFrame;
		frame = new Frame(frameString);

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public int getOrderInFrame() {
		return orderInFrame;
	}

	public void setOrderInFrame(int orderInFrame) {
		this.orderInFrame = orderInFrame;
	}

}

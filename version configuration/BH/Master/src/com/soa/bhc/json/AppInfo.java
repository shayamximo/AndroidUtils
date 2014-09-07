package com.soa.bhc.json;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AppInfo implements Serializable, Parcelable, Comparable<AppInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8863288519506463282L;

	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AppInfo(Parcel in) {
		this.name = in.readString();
		this.id = in.readInt();
	}

	public AppInfo() {
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeInt(this.id);

	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public AppInfo createFromParcel(Parcel in) {
			return new AppInfo(in);
		}

		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};

	@Override
	public int compareTo(AppInfo otherAppInfo) {
		return name.compareTo(otherAppInfo.name);
	}
}
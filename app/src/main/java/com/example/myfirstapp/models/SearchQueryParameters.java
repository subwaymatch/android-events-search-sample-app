package com.example.myfirstapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A POJO with Parcelable implementation to pass search query parameters between components
 */
public class SearchQueryParameters implements Parcelable {
	public String keyword;
	public String categoryId;
	public String distance;
	public String distanceMetric;
	public String useCurrentLocation;
	public String originLocation;
	public double userLat;
	public double userLng;

	public SearchQueryParameters() {}

	private SearchQueryParameters(Parcel in) {
		this.keyword = in.readString();
		this.categoryId = in.readString();
		this.distance = in.readString();
		this.distanceMetric = in.readString();
		this.useCurrentLocation = in.readString();
		this.originLocation = in.readString();
		this.userLat = in.readDouble();
		this.userLng = in.readDouble(); 
	}

	public static final Parcelable.Creator<SearchQueryParameters> CREATOR
			= new Parcelable.Creator<SearchQueryParameters>() {
		public SearchQueryParameters createFromParcel(Parcel in) {
			return new SearchQueryParameters(in);
		}

		public SearchQueryParameters[] newArray(int size) {
			return new SearchQueryParameters[size];
		}
	};

	@Override
	public String toString() {
		return "SearchQueryParameters{" +
				"keyword='" + keyword + '\'' +
				", categoryId='" + categoryId + '\'' +
				", distance='" + distance + '\'' +
				", distanceMetric='" + distanceMetric + '\'' +
				", useCurrentLocation='" + useCurrentLocation + '\'' +
				", originLocation='" + originLocation + '\'' +
				", userLat=" + userLat +
				", userLng=" + userLng +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.keyword);
		dest.writeString(this.categoryId);
		dest.writeString(this.distance);
		dest.writeString(this.distanceMetric);
		dest.writeString(this.useCurrentLocation);
		dest.writeString(this.originLocation);
		dest.writeDouble(this.userLat);
		dest.writeDouble(this.userLng);
	}
}

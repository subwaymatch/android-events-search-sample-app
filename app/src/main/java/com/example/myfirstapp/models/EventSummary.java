package com.example.myfirstapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class EventSummary implements Parcelable {
	public String id;
	public String date;
	public String name;
	public String category;
	public String venueId;
	public String venueInfo;

	public EventSummary(String id, String date, String name, String category, String venueId, String venueInfo) {
		this.id = id;
		this.date = date;
		this.name = name;
		this.category = category;
		this.venueId = venueId;
		this.venueInfo = venueInfo;
	}

	private EventSummary(Parcel in) {
		this.id = in.readString();
		this.date = in.readString();
		this.name = in.readString();
		this.category = in.readString();
		this.venueId = in.readString();
		this.venueInfo = in.readString();
	}

	public static final Parcelable.Creator<EventSummary> CREATOR
			= new Parcelable.Creator<EventSummary>() {
		public EventSummary createFromParcel(Parcel in) {
			return new EventSummary(in);
		}

		public EventSummary[] newArray(int size) {
			return new EventSummary[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.date);
		dest.writeString(this.name);
		dest.writeString(this.category);
		dest.writeString(this.venueId);
		dest.writeString(this.venueInfo);
	}

	@Override
	public String toString() {
		return "EventSummary{" +
				"id='" + id + '\'' +
				", date='" + date + '\'' +
				", name='" + name + '\'' +
				", category='" + category + '\'' +
				", venueId='" + venueId + '\'' +
				", venueInfo='" + venueInfo + '\'' +
				'}';
	}
}

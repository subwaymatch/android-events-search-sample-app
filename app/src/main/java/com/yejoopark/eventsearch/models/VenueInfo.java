package com.yejoopark.eventsearch.models;

public class VenueInfo {
	public String id;
	public String name;
	public String address;
	public String city;
	public String phoneNumber;
	public String openHours;
	public String generalRule;
	public String childRule;
	public double lat;
	public double lng;

	@Override
	public String toString() {
		return "VenueInfo{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", address='" + address + '\'' +
				", city='" + city + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", openHours='" + openHours + '\'' +
				", generalRule='" + generalRule + '\'' +
				", childRule='" + childRule + '\'' +
				", lat=" + lat +
				", lng=" + lng +
				'}';
	}
}

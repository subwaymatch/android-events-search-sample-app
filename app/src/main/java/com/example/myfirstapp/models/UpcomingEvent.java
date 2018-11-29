package com.example.myfirstapp.models;

public class UpcomingEvent {
	public String id;
	public String name;
	public String artist;
	public String datetime;
	public int timestamp;
	public String type;
	public String link;

	@Override
	public String toString() {
		return "UpcomingEvent{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", artist='" + artist + '\'' +
				", datetime='" + datetime + '\'' +
				", timestamp=" + timestamp +
				", type='" + type + '\'' +
				", link='" + link + '\'' +
				'}';
	}
}

package com.example.myfirstapp.models;

import java.util.ArrayList;

public class EventSummary {
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
}

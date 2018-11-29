package com.example.myfirstapp.models;

import java.util.Arrays;

public class EventInfo {
	public String id;
	public String name;
	public String[] artistTeam;
	public String venue;
	public String time;
	public String category;
	public String priceRange;
	public String ticketStatus;
	public String buyTicketAt;
	public String seatmap;

	@Override
	public String toString() {
		return "EventInfo{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", artistTeam=" + Arrays.toString(artistTeam) +
				", venue='" + venue + '\'' +
				", time='" + time + '\'' +
				", category='" + category + '\'' +
				", priceRange='" + priceRange + '\'' +
				", ticketStatus='" + ticketStatus + '\'' +
				", buyTicketAt='" + buyTicketAt + '\'' +
				", seatmap='" + seatmap + '\'' +
				'}';
	}
}

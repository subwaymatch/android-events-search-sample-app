package com.example.myfirstapp.models;

import java.util.Arrays;

public class EventDetail {
	public EventInfo eventInfo;
	public VenueInfo venueInfo;
	public ArtistInfo[] artistInfos;
	public ArtistTeamPhotos[] photos;
	public UpcomingEvent[] upcomingEvents;

	@Override
	public String toString() {
		return "EventDetail{" +
				"eventInfo=" + eventInfo +
				", venueInfo=" + venueInfo +
				", artistInfos=" + Arrays.toString(artistInfos) +
				", photos=" + Arrays.toString(photos) +
				", upcomingEvents=" + Arrays.toString(upcomingEvents) +
				'}';
	}
}

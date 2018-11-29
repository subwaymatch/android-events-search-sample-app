package com.example.myfirstapp.helpers;

import android.util.Log;

import com.example.myfirstapp.models.EventSummary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteEventsHelper {
	private static final String TAG = "FavoriteEventsHelper";
	private static FavoriteEventsHelper instance;

	private Set<String> favoriteEventIds;
	private List<EventSummary> favoriteEvents;

	private FavoriteEventsHelper() {
		favoriteEventIds = new HashSet<String>();
		favoriteEvents = new ArrayList<EventSummary>();
	}

	public static FavoriteEventsHelper getInstance() {
		if (instance == null) {
			instance = new FavoriteEventsHelper();
		}

		return instance;
	}

	public boolean checkIfFavorite(String eventId) {
		return favoriteEventIds.contains(eventId);
	}

	public void add(EventSummary eventSummary) {
		favoriteEventIds.add(eventSummary.id);
		Log.d(TAG, "add: favorite events length=" + favoriteEventIds.size());

		favoriteEvents.add(eventSummary);
	}

	public void remove(EventSummary eventSummary) {
		favoriteEventIds.remove(eventSummary.id);
		Log.d(TAG, "remove: favorite events length=" + favoriteEventIds.size());

		favoriteEvents.remove(eventSummary);
	}
}

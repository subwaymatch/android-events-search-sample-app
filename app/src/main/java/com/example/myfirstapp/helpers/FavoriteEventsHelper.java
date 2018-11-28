package com.example.myfirstapp.helpers;

import android.util.Log;

import com.example.myfirstapp.models.EventSummary;

import java.util.HashSet;
import java.util.Set;

public class FavoriteEventsHelper {
	private static final String TAG = "FavoriteEventsHelper";
	private static FavoriteEventsHelper instance;

	private Set<String> favoriteEventIds;

	private FavoriteEventsHelper() {
		favoriteEventIds = new HashSet<String>();
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
	}

	public void remove(String eventId) {
		favoriteEventIds.remove(eventId);
		Log.d(TAG, "remove: favorite events length=" + favoriteEventIds.size());
	}

}

package com.yejoopark.eventsearch.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.yejoopark.eventsearch.MyApplication;
import com.yejoopark.eventsearch.models.EventSummary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteEventsHelper {
	private static final String TAG = "FavoriteEventsHelper";
	private static FavoriteEventsHelper instance;
	private static final String FAVORITE_PREFERENCE_FILE_KEY = "FAVORITE_PREFERENCE_FILE_KEY";
	private static final String FAVORITE_EVENTS_KEY = "favoriteEvents";

	private List<EventSummary> favoriteEvents;
	private Context mContext;
	private SharedPreferences sharedPref;
	private Gson gson;

	private FavoriteEventsHelper() {
		mContext = MyApplication.getAppContext();
		sharedPref = mContext.getSharedPreferences(FAVORITE_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
		gson = new Gson();

		loadFavoriteEvents();
	}

	public static FavoriteEventsHelper getInstance() {
		if (instance == null) {
			instance = new FavoriteEventsHelper();
		}

		return instance;
	}

	public void loadFavoriteEvents() {
		String favoriteEventsJsonString = sharedPref.getString(FAVORITE_EVENTS_KEY, null);

		if (favoriteEventsJsonString != null) {
			Type listType = new TypeToken<List<EventSummary>>(){}.getType();
			favoriteEvents = gson.fromJson(favoriteEventsJsonString, listType);
		}

		else {
			favoriteEvents = new ArrayList<EventSummary>();
		}
	}

	public void saveFavoriteEvents() {
		if (favoriteEvents != null) {
			Type listType = new TypeToken<List<EventSummary>>(){}.getType();
			String favoriteEventsJsonString = gson.toJson(favoriteEvents, listType);

			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(FAVORITE_EVENTS_KEY, favoriteEventsJsonString);

			editor.commit();
		}
	}

	public boolean checkIfFavorite(EventSummary eventSummary) {
		return favoriteEvents.contains(eventSummary);
	}

	public void add(EventSummary eventSummary) {
		favoriteEvents.add(eventSummary);
		saveFavoriteEvents();
	}

	public void remove(EventSummary eventSummary) {
		favoriteEvents.remove(eventSummary);
		saveFavoriteEvents();
	}

	public List<EventSummary> getFavoriteEvents() {
		return favoriteEvents;
	}
}

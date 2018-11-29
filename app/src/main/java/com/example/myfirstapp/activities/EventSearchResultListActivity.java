package com.example.myfirstapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapp.R;
import com.example.myfirstapp.models.EventSummary;
import com.example.myfirstapp.models.SearchQueryParameters;
import com.example.myfirstapp.viewadapters.EventSearchResultListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class EventSearchResultListActivity extends AppCompatActivity {
	private static final String TAG = "EventSearchResultListAc";
	private static EventSearchResultListAdapter recyclerViewAdapter;

	RecyclerView eventsRecyclerView;
	RelativeLayout progressWrapper;
	TextView listEmptyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_search_result_list);

		this.setTitle("Search Results");

		this.eventsRecyclerView = findViewById(R.id.searchResultListRecyclerView);
		this.progressWrapper = findViewById(R.id.searchResultListProgressWrapper);
		this.listEmptyText = findViewById(R.id.searchResultListEmptyText);

		this.eventsRecyclerView.setVisibility(View.GONE);
		this.progressWrapper.setVisibility(View.GONE);
		this.listEmptyText.setVisibility(View.GONE);

		Log.d(TAG, "onCreate: getIntent().getExtras()=" + getIntent().getExtras());
		Log.d(TAG, "onCreate: savedInstanceState=" + savedInstanceState);

		SearchQueryParameters searchQueryParameters = null;

		if (getIntent().getExtras() != null) {
			searchQueryParameters = (SearchQueryParameters) getIntent().getExtras().getParcelable("searchQueryParameters");

			Log.d(TAG, "onCreate: searchQueryParameters");
			Log.d(TAG, searchQueryParameters.toString());

			getEventSearchResult(searchQueryParameters);

			Log.d(TAG, "onCreate: started.");
		}

		else {
			showRecyclerView();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState: ");

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onRestoreInstanceState: ");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(TAG, "onPause: paused");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "onResume: resumed");
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.d(TAG, "onStop: stopped");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy: destroyed");
	}

	private void getEventSearchResult(SearchQueryParameters p) {
		// Show progress circle
		this.progressWrapper.setVisibility(View.VISIBLE);

		RequestQueue httpRequestQueue = Volley.newRequestQueue(this);

		String baseUrl = "http://ticketmaster-v1.us-west-1.elasticbeanstalk.com/api/v1.0/event/search";

		Uri builtUri = Uri.parse(baseUrl)
				.buildUpon()
				.appendQueryParameter("keyword", "lakers")
				.appendQueryParameter("categoryId", "")
				.appendQueryParameter("distance", "30")
				.appendQueryParameter("distanceMetric", "miles")
				.appendQueryParameter("useCurrentLocation", "true")
				.appendQueryParameter("originLocation", "")
				.appendQueryParameter("userLat", "34.0584")
				.appendQueryParameter("userLng", "-118.278")
				.build();
/*

		Uri builtUri = Uri.parse(baseUrl)
				.buildUpon()
				.appendQueryParameter("keyword", searchQueryParameters.keyword)
				.appendQueryParameter("categoryId", searchQueryParameters.categoryId)
				.appendQueryParameter("distance", searchQueryParameters.distance)
				.appendQueryParameter("distanceMetric", searchQueryParameters.distanceMetric)
				.appendQueryParameter("useCurrentLocation", searchQueryParameters.useCurrentLocation)
				.appendQueryParameter("originLocation", searchQueryParameters.originLocation)
				.appendQueryParameter("userLat", Double.toString(searchQueryParameters.userLat))
				.appendQueryParameter("userLng", Double.toString(searchQueryParameters.userLng))
				.build();
*/

		String urlWithParams = builtUri.toString();

		Log.d("http", "pre-request url=" + urlWithParams);

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.GET, urlWithParams, null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				Log.d("http", "Response: " + response.toString());

				Gson gson = new Gson();
				Type listType = new TypeToken<List<EventSummary>>(){}.getType();
				List<EventSummary> eventSummaries = gson.fromJson(response.toString(), listType);

				Log.d(TAG, "getEventSearchResult: retrieving event search result");

				if (eventSummaries.size() == 0) {
					progressWrapper.setVisibility(View.GONE);
					listEmptyText.setVisibility(View.VISIBLE);
				}

				initRecyclerView(eventSummaries);
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("http", "onErrorResponse");
				Log.e("http", error.getMessage());
			}
		}
		);

		httpRequestQueue.add(jsonArrayRequest);
	}
	
	private void initRecyclerView(List<EventSummary> eventSummaries) {
		if (recyclerViewAdapter == null) {
			recyclerViewAdapter = new EventSearchResultListAdapter(this, eventSummaries);
		}

		showRecyclerView();
	}

	private void showRecyclerView() {
		Log.d(TAG, "showRecyclerView: recyclerViewAdapter=" + recyclerViewAdapter);

		eventsRecyclerView.setAdapter(recyclerViewAdapter);
		eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		progressWrapper.setVisibility(View.GONE);
		eventsRecyclerView.setVisibility(View.VISIBLE);
	}
}

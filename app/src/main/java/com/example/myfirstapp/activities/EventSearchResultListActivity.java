package com.example.myfirstapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

	private List<EventSummary> mEventSummaries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_search_result_list);

		this.setTitle("Search Results");

		SearchQueryParameters p = (SearchQueryParameters) getIntent().getExtras().getParcelable("searchQueryParameters");

		Log.d(TAG, "onCreate: searchQueryParameters");
		Log.d(TAG, p.toString());

		getEventSearchResult(p);

		Log.d(TAG, "onCreate: started.");
	}

	private void getEventSearchResult(SearchQueryParameters p) {
		RequestQueue httpRequestQueue = Volley.newRequestQueue(this);

		String baseUrl = "http://ticketmaster-v1.us-west-1.elasticbeanstalk.com/api/v1.0/event/search";

		Uri builtUri = Uri.parse(baseUrl)
				.buildUpon()
				.appendQueryParameter("keyword", "christmas")
				.appendQueryParameter("categoryId", "")
				.appendQueryParameter("distanceMetric", "miles")
				.appendQueryParameter("useCurrentLocation", "true")
				.appendQueryParameter("originLocation", "")
				.appendQueryParameter("userLat", "34.0584")
				.appendQueryParameter("userLng", "-118.278")
				.build();

		String urlWithParams = builtUri.toString();

		Log.d("http", "pre-request url=" + urlWithParams);

		JsonArrayRequest jsonObjectRequest2 = new JsonArrayRequest(
				Request.Method.GET, urlWithParams, null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				Log.d("http", "Response: " + response.toString());

				Gson gson = new Gson();
				Type listType = new TypeToken<List<EventSummary>>(){}.getType();
				mEventSummaries = gson.fromJson(response.toString(), listType);

				Log.d(TAG, "getEventSearchResult: retrieving event search result");

				initRecyclerView();
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("http", "onErrorResponse");
				Log.e("http", error.getMessage());
			}
		}
		);

		httpRequestQueue.add(jsonObjectRequest2);
	}
	
	private void initRecyclerView() {
		RecyclerView recyclerView = findViewById(R.id.search_result_list_recycler_view);

		EventSearchResultListAdapter adapter = new EventSearchResultListAdapter(this, mEventSummaries);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
}

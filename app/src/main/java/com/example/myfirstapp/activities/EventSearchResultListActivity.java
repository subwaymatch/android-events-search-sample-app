package com.example.myfirstapp.activities;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.viewadapters.EventSearchResultListAdapter;

import java.util.ArrayList;

public class EventSearchResultListActivity extends AppCompatActivity {
	private static final String TAG = "EventSearchResultListAc";

	private ArrayList<String> mEventNames = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_search_result_list);

		getEventSearchResult();

		Log.d(TAG, "onCreate: started.");
	}

	private void getEventSearchResult() {
		Log.d(TAG, "getEventSearchResult: retrieving event search result");

		mEventNames.add("Event 1");
		mEventNames.add("Event 2");
		mEventNames.add("Event 3");
		mEventNames.add("Event 4");

		initRecyclerView();
	}
	
	private void initRecyclerView() {
		Log.d(TAG, "initRecyclerView: init recyclerview.");

		RecyclerView recyclerView = findViewById(R.id.search_result_list_recycler_view);
		EventSearchResultListAdapter adapter = new EventSearchResultListAdapter(this, mEventNames);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}
}

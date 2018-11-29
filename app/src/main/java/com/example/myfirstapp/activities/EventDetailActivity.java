package com.example.myfirstapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapp.R;
import com.example.myfirstapp.helpers.FavoriteEventsHelper;
import com.example.myfirstapp.models.EventDetail;
import com.example.myfirstapp.models.EventSummary;
import com.example.myfirstapp.models.SearchQueryParameters;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class EventDetailActivity extends AppCompatActivity {
	private static final String TAG = "EventDetailActivity";

	private EventSummary eventSummary;
	private EventDetail eventDetail;
	private FavoriteEventsHelper favoriteEventsHelper;

	private MenuItem eventDetailFavoriteIcon;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);

		favoriteEventsHelper = FavoriteEventsHelper.getInstance();

		if (getIntent().getExtras() != null) {
			eventSummary = (EventSummary) getIntent().getExtras().getParcelable("eventSummary");

			Log.d(TAG, "onCreate: searchQueryParameters");
			Log.d(TAG, eventSummary.toString());

			getEventDetail(eventSummary.id, eventSummary.venueId);
		}

		// If no EventSummary object has been passed, do nothing
		else {
			return;
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		toolbar.setTitle(eventSummary.name);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.eventDetailViewPagerContainer);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_event_detail, menu);

		eventDetailFavoriteIcon = menu.findItem(R.id.eventDetailFavoriteIcon);

		if (favoriteEventsHelper.checkIfFavorite(eventSummary.id)) {
			eventDetailFavoriteIcon.setIcon(R.drawable.heart_fill_red);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.eventDetailFavoriteIcon) {
			Log.d(TAG, "onOptionsItemSelected: favorite icon");

			if (!favoriteEventsHelper.checkIfFavorite(eventSummary.id)) {
				favoriteEventsHelper.add(eventSummary);
				eventDetailFavoriteIcon.setIcon(R.drawable.heart_fill_red);
				Toast.makeText(this, eventSummary.name + " was added to favorites", Toast.LENGTH_SHORT);
			}

			else {
				favoriteEventsHelper.remove(eventSummary);
				eventDetailFavoriteIcon.setIcon(R.drawable.heart_fill_white);
				Toast.makeText(this, eventSummary.name + " was removed from favorites", Toast.LENGTH_SHORT);
			}

			return true;
		}

		else if (id == R.id.eventDetailTwitterIcon) {
			Log.d(TAG, "onOptionsItemSelected: twitter icon");

			if (eventDetail != null) {
				String tweetBaseURL = "https://twitter.com/intent/tweet?text=";
				String tweetText = "Check out " + this.eventSummary.name + " at " + this.eventDetail.venueInfo.name + ". Website: " + this.eventDetail.eventInfo.buyTicketAt + "  #CSCI571EventSearch";

				String tweetUrl = tweetBaseURL + Uri.encode(tweetText);

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
				startActivity(browserIntent);
			}

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
			textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}
	}

	private void getEventDetail(final String eventId, final String venueId) {
		// Show progress circle
		// this.progressWrapper.setVisibility(View.VISIBLE);

		RequestQueue httpRequestQueue = Volley.newRequestQueue(this);

		String baseUrl = "http://ticketmaster-v1.us-west-1.elasticbeanstalk.com/api/v1.0/event/detail";

		Uri builtUri = Uri.parse(baseUrl)
				.buildUpon()
				.appendQueryParameter("eventId", eventId)
				.appendQueryParameter("venueId", venueId)
				.build();

		String urlWithParams = builtUri.toString();

		Log.d("http", "pre-request url=" + urlWithParams);

		JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
				Request.Method.GET, urlWithParams, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.d("http", "Response: " + response.toString());

				Gson gson = new Gson();
				eventDetail = gson.fromJson(response.toString(), EventDetail.class);

				// Fill in event id and venue id
				eventDetail.eventInfo.id = eventId;
				eventDetail.venueInfo.id = venueId;

				Log.d(TAG, "getEventDetail: retrieving event detail result");
				Log.d(TAG, eventDetail.toString());
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

}

package com.yejoopark.eventsearch.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yejoopark.eventsearch.R;
import com.yejoopark.eventsearch.fragments.eventdetail.ArtistTeamPhotosFragment;
import com.yejoopark.eventsearch.fragments.eventdetail.EventInfoFragment;
import com.yejoopark.eventsearch.fragments.eventdetail.UpcomingEventsFragment;
import com.yejoopark.eventsearch.fragments.eventdetail.VenueInfoFragment;
import com.yejoopark.eventsearch.helpers.FavoriteEventsHelper;
import com.yejoopark.eventsearch.models.EventDetail;
import com.yejoopark.eventsearch.models.EventSummary;
import com.google.gson.Gson;

import org.json.JSONObject;

public class EventDetailActivity extends AppCompatActivity {
	private static final String TAG = "EventDetailActivity";

	private EventSummary eventSummary;
	private EventDetail eventDetail;
	private FavoriteEventsHelper favoriteEventsHelper;

	private MenuItem eventDetailFavoriteIcon;
	private ViewPager viewPagerContainer;
	private RelativeLayout progressWrapper;

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

		viewPagerContainer = findViewById(R.id.eventDetailViewPagerContainer);
		progressWrapper = findViewById(R.id.eventDetailProgressWrapper);

		if (getIntent().getExtras() == null) {
			return;
		}

		eventSummary = (EventSummary) getIntent().getExtras().getParcelable("eventSummary");

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		toolbar.setTitle(eventSummary.name);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Show progress circle and hide view pager
		this.viewPagerContainer.setVisibility(View.GONE);
		this.progressWrapper.setVisibility(View.VISIBLE);

		fetchEventDetail(eventSummary.id, eventSummary.venueId);
	}

	private void initializeViewWithEventDetail() {
		Log.d(TAG, "initializeViewWithEventDetail: " + eventDetail);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.eventDetailViewPagerContainer);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

		// Show progress circle and hide view pager
		this.viewPagerContainer.setVisibility(View.VISIBLE);
		this.progressWrapper.setVisibility(View.GONE);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_event_detail, menu);

		eventDetailFavoriteIcon = menu.findItem(R.id.eventDetailFavoriteIcon);

		if (favoriteEventsHelper.checkIfFavorite(eventSummary)) {
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

			if (!favoriteEventsHelper.checkIfFavorite(eventSummary)) {
				favoriteEventsHelper.add(eventSummary);
				eventDetailFavoriteIcon.setIcon(R.drawable.heart_fill_red);
				Toast.makeText(this, eventSummary.name + " was added to favorites", Toast.LENGTH_SHORT).show();
			}

			else {
				favoriteEventsHelper.remove(eventSummary);
				eventDetailFavoriteIcon.setIcon(R.drawable.heart_fill_white);
				Toast.makeText(this, eventSummary.name + " was removed from favorites", Toast.LENGTH_SHORT).show();
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
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return EventInfoFragment.newInstance();
			}

			else if (position == 1) {
				return ArtistTeamPhotosFragment.newInstance();
			}

			else if (position == 2) {
				return VenueInfoFragment.newInstance();
			}

			else {
				return UpcomingEventsFragment.newInstance();
			}
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}
	}

	private void fetchEventDetail(final String eventId, final String venueId) {
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

				initializeViewWithEventDetail();

				Log.d(TAG, "fetchEventDetail: retrieving event detail result");
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

	public EventDetail getEventDetail() {
		return eventDetail;
	}
}

package com.example.myfirstapp.activities;

import android.content.Context;
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
import android.util.AttributeSet;
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
import com.example.myfirstapp.R;
import com.example.myfirstapp.fragments.eventdetail.ArtistTeamPhotosFragment;
import com.example.myfirstapp.fragments.eventdetail.EventInfoFragment;
import com.example.myfirstapp.fragments.eventdetail.UpcomingEventsFragment;
import com.example.myfirstapp.fragments.eventdetail.VenueInfoFragment;
import com.example.myfirstapp.helpers.FavoriteEventsHelper;
import com.example.myfirstapp.models.EventDetail;
import com.example.myfirstapp.models.EventSummary;
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
		// TODO: Remove test code
		if (true) {
			String sportEventJson = "{\"eventInfo\":{\"name\":\"Los Angeles Lakers vs. Minnesota Timberwolves\",\"artistTeam\":[\"Los Angeles Lakers\",\"Minnesota Timberwolves\"],\"venue\":\"STAPLES Center\",\"time\":\"Jan 24, 2019 19:30:00\",\"category\":\"Sports | Basketball\",\"priceRange\":null,\"ticketStatus\":\"Onsale\",\"buyTicketAt\":\"https://www.ticketmaster.com/los-angeles-lakers-vs-minnesota-timberwolves-los-angeles-california-01-24-2019/event/2C005508EF040B4F\",\"seatmap\":\"https://s1.ticketm.net/tmimages/venue/maps/la1/la1257c.gif\"},\"venueInfo\":{\"address\":null,\"city\":null,\"phoneNumber\":null,\"openHours\":null,\"generalRule\":null,\"childRule\":null,\"lat\":null,\"lng\":null},\"artistInfos\":null,\"photos\":[{\"name\":\"Los Angeles Lakers\",\"links\":[\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://lakeshowlife.com/wp-content/uploads/getty-images/2018/02/924704066-los-angeles-lakers-v-atlanta-hawks.jpg.jpg&\",\"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Los_Angeles_Lakers_logo.svg/2000px-Los_Angeles_Lakers_logo.svg.png\",\"https://cdn.vox-cdn.com/uploads/chorus_image/image/52871659/usa_today_9639188.0.jpg\",\"https://cdn.nba.net/nba-drupal-prod/styles/landscape/s3/2018-07/lebronjames-lakers.jpg?itok=NJl_vJMq\",\"http://www.michael-weinstein.com/wp-content/uploads/2016/10/nba_logo_redesigns-la_lakers-full.png\",\"http://latfusa.com/media/uploads/2017/08/14/lakers-logo-2.jpg\",\"http://www.magic925.com/wp-content/uploads/2016/05/New-Los-Angeles-Lakers-Wallpapers.jpg\",\"https://clutchpoints.com/wp-content/uploads/2018/08/t34.jpg\",\"https://img.vavel.com/lakerspv-3009423115.jpg\"]},{\"name\":\"Minnesota Timberwolves\",\"links\":[\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dunkingwithwolves.com/wp-content/uploads/getty-images/2017/07/984673622.jpeg&c=sc&w=3200&h=2133\",\"https://www.nba.com/.element/media/2.0/teamsites/timberwolves/media/mntw-nw-30-lgo-2018-main.png\",\"https://www.targetcenter.com/assets/img/TimberwolvesLogo_665x374-79b4be5528.jpg\",\"https://www.nba.com/timberwolves/sites/timberwolves/files/1819_cityunveil_hero_rose.jpg\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dunkingwithwolves.com/wp-content/uploads/getty-images/2017/07/950864324.jpeg&c=sc&w=3200&h=2133\",\"https://www.nba.com/timberwolves/sites/timberwolves/files/tickets_9_25_17.jpg\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dunkingwithwolves.com/wp-content/uploads/getty-images/2017/09/852045868-2017-18-minnesota-timberwolves-media-day.jpg.jpg&\",\"https://timedotcom.files.wordpress.com/2018/11/ap18305596074357.jpg\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dunkingwithwolves.com/wp-content/uploads/getty-images/2017/09/852045858-2017-18-minnesota-timberwolves-media-day.jpg.jpg&c=sc&w=3483&h=3366\"]}],\"upcomingEvents\":[]}";
		}

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

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
		// TODO: Remove mock code later
		if (true) {
			String sportEventId = "Z7r9jZ1Ae3f0x";
			String sportVenueId = "ZFr9jZe6vA";
			String sportEventJson = "{\"eventInfo\":{\"name\":\"Premier Boxing Champions: Wilder vs Fury\",\"artistTeam\":[\"Premier Boxing Champions\",\"Tyson Fury\"],\"venue\":\"STAPLES Center\",\"time\":\"Dec 01, 2018 15:25:00\",\"category\":\"Sports | Boxing\",\"priceRange\":null,\"ticketStatus\":\"Onsale\",\"buyTicketAt\":\"http://www.ticketsnow.com/InventoryBrowse/TicketList.aspx?PID=2494922\",\"seatmap\":\"http://resale.ticketmaster.com.au/akamai-content/graphics/TMResale/2/VenueMaps/1604-34737-1-0-STAPLESCenterPBC80632.png\"},\"venueInfo\":{\"name\":\"STAPLES Center\",\"address\":\"1111 S. Figueroa St.\",\"city\":\"Los Angeles\",\"phoneNumber\":null,\"openHours\":null,\"generalRule\":null,\"childRule\":null,\"lat\":34.053101,\"lng\":-118.2649},\"artistInfos\":null,\"photos\":[{\"name\":\"Premier Boxing Champions\",\"links\":[\"https://www.premierboxingchampions.com/sites/all/themes/haymon/images/global/logo-black.svg\",\"http://images.performgroup.com/di/library/sporting_news/2a/21/premier-boxing-champions-465554072-getty-ftrjpg_2yhvld4jl8u31ptsi3ji1gl4j.jpg?t=775217932\",\"https://www.gannett-cdn.com/-mm-/b7b38e7f5aef3bc921faffff10c44c4ad0dbfb3c/c=0-82-2384-1429/local/-/media/2015/03/18/USATODAY/USATODAY/635622715888247233-PBC1-3713-copy.jpg?width=3200&height=1680&fit=crop\",\"http://www.premierboxingchampions.com/sites/default/files/field/image/PBC.jpg\",\"https://www.premierboxingchampions.com/sites/default/files/styles/responsive_1180_x_664/public/PBConSHO_Web_0.jpg\",\"http://www.premierboxingchampions.com/sites/default/files/field/image/PBConSpike.jpg\",\"https://storage.googleapis.com/btvwp-uploads/2015/10/24c49f23-pbc_showimage.png\",\"https://www.premierboxingchampions.com/sites/default/files/field/image/ShowtimeWeb_0.jpg\",\"http://ucnlive.com/wp-content/uploads/2017/02/Vasquez-vs-Collazo_02_02_2017_Fight_Ryan-Greene-_-Premier-Boxing-Champions-2.jpg\"]},{\"name\":\"Tyson Fury\",\"links\":[\"https://static.standard.co.uk/s3fs-public/thumbnails/image/2018/10/26/08/tysonfury2610.jpg?w968\",\"https://res.cloudinary.com/jpress/image/fetch/c_fill,f_auto,h_1131,q_auto:eco,w_1700/https://inews.co.uk/wp-content/uploads/2018/08/GettyImages-1008334836.jpg\",\"https://cdn.vox-cdn.com/thumbor/WI33I6YfHCNobOdz3alr2fqSXf4=/1400x1400/filters:format(jpeg)/cdn.vox-cdn.com/uploads/chorus_asset/file/13336201/015_Tyson_Fury.jpg\",\"https://www.foxsportsasia.com/uploads/2018/10/tysonfury_1ta2qwiuxsdvz1hmexb4n3wvpb.jpg\",\"http://wstale.com/wp-content/uploads/2018/10/ccelebritiesfotoSPORT-PREVIEW-Tyson-Fury-Ferrari-and-Depression.jpg\",\"https://static.independent.co.uk/s3fs-public/thumbnails/image/2018/08/18/23/tyson-fury.jpg\",\"https://leadership.ng/wp-content/uploads/2018/04/619cd807-joshua2.jpg\",\"https://res.cloudinary.com/jpress/image/fetch/c_fill,f_auto,h_1247,q_auto:eco,w_1700/https://inews.co.uk/wp-content/uploads/2018/06/GettyImages-969759612.jpg\",\"https://static.standard.co.uk/s3fs-public/thumbnails/image/2018/08/18/22/PianetaFury1808abci.jpg\"]}],\"upcomingEvents\":[{\"id\":35738954,\"name\":\"Calibash at Staples Center (January 19, 2019)\",\"artist\":\"Calibash\",\"datetime\":\"Jan 20, 2019 03:00:00\",\"timestamp\":1547953200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35738954-calibash-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":36034024,\"name\":\"Calibash at Staples Center (January 20, 2019)\",\"artist\":\"Calibash\",\"datetime\":\"Jan 21, 2019 03:00:00\",\"timestamp\":1548039600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36034024-calibash-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":32606024,\"name\":\"Elton John at Staples Center (January 22, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 22, 2019\",\"timestamp\":1548115200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/32606024-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":32606029,\"name\":\"Elton John at Staples Center (January 23, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 23, 2019\",\"timestamp\":1548201600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/32606029-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":33294174,\"name\":\"Elton John at Staples Center (January 25, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 26, 2019 03:00:00\",\"timestamp\":1548471600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33294174-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35338329,\"name\":\"Kelly Clarkson with Kelsea Ballerini and Brynn Cartelli at Staples Center (January 26, 2019)\",\"artist\":\"Kelly Clarkson\",\"datetime\":\"Jan 27, 2019 02:00:00\",\"timestamp\":1548554400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35338329-kelly-clarkson-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35049954,\"name\":\"Elton John at Staples Center (January 30, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 31, 2019 03:00:00\",\"timestamp\":1548903600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35049954-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35160729,\"name\":\"Pat Braxton at Staples Center (February 10, 2019)\",\"artist\":\"Pat Braxton\",\"datetime\":\"Feb 11, 2019 00:00:00\",\"timestamp\":1549843200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35160729-pat-braxton-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35950199,\"name\":\"Michael Bublé at Staples Center (April 2, 2019)\",\"artist\":\"Michael Bublé\",\"datetime\":\"Apr 03, 2019 02:00:00\",\"timestamp\":1554256800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35950199-michael-buble-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":36002809,\"name\":\"Julia Michaels at Staples Center (April 15, 2019)\",\"artist\":\"Julia Michaels\",\"datetime\":\"Apr 16, 2019 03:00:00\",\"timestamp\":1555383600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36002809-julia-michaels-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35810809,\"name\":\"Ariana Grande at Staples Center (May 6, 2019)\",\"artist\":\"Ariana Grande\",\"datetime\":\"May 07, 2019 02:30:00\",\"timestamp\":1557196200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35810809-ariana-grande-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35397844,\"name\":\"Eric Church at Staples Center (May 17, 2019)\",\"artist\":\"Eric Church\",\"datetime\":\"May 18, 2019 02:00:00\",\"timestamp\":1558144800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35397844-eric-church-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35397849,\"name\":\"Eric Church at Staples Center (May 18, 2019)\",\"artist\":\"Eric Church\",\"datetime\":\"May 19, 2019 02:00:00\",\"timestamp\":1558231200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35397849-eric-church-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":33861654,\"name\":\"Shawn Mendes at Staples Center (July 5, 2019)\",\"artist\":\"Shawn Mendes\",\"datetime\":\"Jul 06, 2019 01:30:00\",\"timestamp\":1562376600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33861654-shawn-mendes-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35966204,\"name\":\"Backstreet Boys at Staples Center (August 3, 2019)\",\"artist\":\"Backstreet Boys\",\"datetime\":\"Aug 04, 2019 02:00:00\",\"timestamp\":1564884000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35966204-backstreet-boys-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":34902109,\"name\":\"Maddie and Tae at Staples Center (September 12, 2019)\",\"artist\":\"Maddie and Tae\",\"datetime\":\"Sep 13, 2019 01:00:00\",\"timestamp\":1568336400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/34902109-maddie-and-tae-at-staples-center?utm_source=51967&utm_medium=partner\"}]}";

			String musicEventId = "";
			String musicVenueId = "";
			String musicEventJson = "";

			Gson gson = new Gson();
			eventDetail = gson.fromJson(sportEventJson, EventDetail.class);

			// Fill in event id and venue id
			eventDetail.eventInfo.id = sportEventId;
			eventDetail.venueInfo.id = sportVenueId;

			initializeViewWithEventDetail();

			return;

			// END: TEST CODE (MOCK DATA)
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

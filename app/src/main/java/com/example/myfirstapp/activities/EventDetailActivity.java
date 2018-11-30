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
			String sportEventId = "G5eYZ48NmT8c5";
			String sportVenueId = "KovZpZAEdntA";
			String sportEventJson = "{\"eventInfo\":{\"name\":\"Los Angeles Lakers vs. Chicago Bulls\",\"artistTeam\":[\"Los Angeles Lakers\",\"Chicago Bulls\"],\"venue\":\"STAPLES Center\",\"time\":\"Jan 15, 2019 19:30:00\",\"category\":\"Sports | Basketball\",\"priceRange\":null,\"ticketStatus\":\"Onsale\",\"buyTicketAt\":\"https://www.ticketmaster.com/los-angeles-lakers-vs-chicago-bulls-los-angeles-california-01-15-2019/event/2C005508EEFD0B4B\",\"seatmap\":\"https://s1.ticketm.net/tmimages/venue/maps/la1/la1257c.gif\"},\"venueInfo\":{\"name\":\"STAPLES Center\",\"address\":\"1111 S. Figueroa St\",\"city\":\"Los Angeles\",\"phoneNumber\":\"213-742-7340\",\"openHours\":\"Box office is located on North side of building at 11th and South Figueroa. Box office hours are 10am to 6pm, Monday through Saturday. It is open extended hours on event day. Phone: 213-742-7340 SUMMER HOURS Closed Saturdays and Sundays unless there is an event, the box office will open at 9am on Saturdays or 10am on Sundays only if there is an event. The box office will have extended hours on all event days.\",\"generalRule\":\"No Bottles, Cans, Or Coolers. No Smoking In Arena. No Cameras Or Recording Devices At Concerts! Cameras w/No Flash Allowed For Sporting Events Only!\",\"childRule\":\"Some events require all attendees, regardless of age, to present a ticket for entry. Please check the event ticket policies at the time of purchase. Children age three (3) and above require a ticket for Los Angeles Lakers, Los Angeles Clippers, Los Angeles Kings and Los Angeles Sparks games.\",\"lat\":34.043003,\"lng\":-118.267253},\"artistInfos\":null,\"photos\":[{\"name\":\"Chicago Bulls\",\"links\":[\"https://www.nba.com/bulls/sites/bulls/files/styles/hi_res_full_width/public/1718-generic.jpg?itok=w1AU-m5_\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dawindycity.com/wp-content/uploads/getty-images/2017/12/879138480-charlotte-hornets-v-chicago-bulls.jpg.jpg&\",\"https://www.nba.com/bulls/sites/bulls/files/styles/hi_res_full_width/public/20180718-blakeney-dunks.jpg?itok=vORXRKS4\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://dawindycity.com/wp-content/uploads/getty-images/2016/04/928710602.jpeg&c=sc&w=1600&h=1271\",\"http://www.trbimg.com/img-59e8a78d/turbine/ct-chicago-bulls-roster-photos\",\"https://worldsportlogos.com/wp-content/uploads/2018/03/Chicago-Bulls-symbol.png\",\"http://www.umphreys.com/wp-content/uploads/2016/12/chicago-bulls35.jpg\",\"https://worldsportlogos.com/wp-content/uploads/2018/03/Chicago-Bulls-logo.png\",\"https://www.nba.com/bulls/sites/bulls/files/styles/hi_res_full_width/public/team-huddle.jpg?itok=nrwyDv08\"]},{\"name\":\"Los Angeles Lakers\",\"links\":[\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://lakeshowlife.com/wp-content/uploads/getty-images/2017/07/1061178646.jpeg&c=sc&w=3200&h=2130\",\"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Los_Angeles_Lakers_logo.svg/2000px-Los_Angeles_Lakers_logo.svg.png\",\"https://imagesvc.timeincapp.com/v3/fan/image?url=https://lakeshowlife.com/wp-content/uploads/getty-images/2018/02/924704066-los-angeles-lakers-v-atlanta-hawks.jpg.jpg&\",\"https://cdn.vox-cdn.com/uploads/chorus_image/image/52871659/usa_today_9639188.0.jpg\",\"https://cdn.nba.net/nba-drupal-prod/styles/landscape/s3/2018-07/lebronjames-lakers.jpg?itok=NJl_vJMq\",\"http://www.michael-weinstein.com/wp-content/uploads/2016/10/nba_logo_redesigns-la_lakers-full.png\",\"http://latfusa.com/media/uploads/2017/08/14/lakers-logo-2.jpg\",\"http://www.magic925.com/wp-content/uploads/2016/05/New-Los-Angeles-Lakers-Wallpapers.jpg\",\"https://clutchpoints.com/wp-content/uploads/2018/08/t34.jpg\"]}],\"upcomingEvents\":[{\"id\":35738954,\"name\":\"Calibash at Staples Center (January 19, 2019)\",\"artist\":\"Calibash\",\"datetime\":\"Jan 20, 2019 03:00:00\",\"timestamp\":1547953200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35738954-calibash-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":36034024,\"name\":\"Calibash at Staples Center (January 20, 2019)\",\"artist\":\"Calibash\",\"datetime\":\"Jan 21, 2019 03:00:00\",\"timestamp\":1548039600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36034024-calibash-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":32606024,\"name\":\"Elton John at Staples Center (January 22, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 22, 2019\",\"timestamp\":1548115200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/32606024-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":32606029,\"name\":\"Elton John at Staples Center (January 23, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 23, 2019\",\"timestamp\":1548201600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/32606029-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":33294174,\"name\":\"Elton John at Staples Center (January 25, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 26, 2019 03:00:00\",\"timestamp\":1548471600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33294174-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35338329,\"name\":\"Kelly Clarkson with Kelsea Ballerini and Brynn Cartelli at Staples Center (January 26, 2019)\",\"artist\":\"Kelly Clarkson\",\"datetime\":\"Jan 27, 2019 02:00:00\",\"timestamp\":1548554400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35338329-kelly-clarkson-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35049954,\"name\":\"Elton John at Staples Center (January 30, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Jan 31, 2019 03:00:00\",\"timestamp\":1548903600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35049954-elton-john-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35160729,\"name\":\"Pat Braxton at Staples Center (February 10, 2019)\",\"artist\":\"Pat Braxton\",\"datetime\":\"Feb 11, 2019 00:00:00\",\"timestamp\":1549843200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35160729-pat-braxton-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35950199,\"name\":\"Michael Bublé at Staples Center (April 2, 2019)\",\"artist\":\"Michael Bublé\",\"datetime\":\"Apr 03, 2019 02:00:00\",\"timestamp\":1554256800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35950199-michael-buble-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":36002809,\"name\":\"Julia Michaels at Staples Center (April 15, 2019)\",\"artist\":\"Julia Michaels\",\"datetime\":\"Apr 16, 2019 03:00:00\",\"timestamp\":1555383600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36002809-julia-michaels-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35810809,\"name\":\"Ariana Grande at Staples Center (May 6, 2019)\",\"artist\":\"Ariana Grande\",\"datetime\":\"May 07, 2019 02:30:00\",\"timestamp\":1557196200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35810809-ariana-grande-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35397844,\"name\":\"Eric Church at Staples Center (May 17, 2019)\",\"artist\":\"Eric Church\",\"datetime\":\"May 18, 2019 02:00:00\",\"timestamp\":1558144800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35397844-eric-church-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35397849,\"name\":\"Eric Church at Staples Center (May 18, 2019)\",\"artist\":\"Eric Church\",\"datetime\":\"May 19, 2019 02:00:00\",\"timestamp\":1558231200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35397849-eric-church-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":33861654,\"name\":\"Shawn Mendes at Staples Center (July 5, 2019)\",\"artist\":\"Shawn Mendes\",\"datetime\":\"Jul 06, 2019 01:30:00\",\"timestamp\":1562376600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33861654-shawn-mendes-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":35966204,\"name\":\"Backstreet Boys at Staples Center (August 3, 2019)\",\"artist\":\"Backstreet Boys\",\"datetime\":\"Aug 04, 2019 02:00:00\",\"timestamp\":1564884000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35966204-backstreet-boys-at-staples-center?utm_source=51967&utm_medium=partner\"},{\"id\":34902109,\"name\":\"Maddie and Tae at Staples Center (September 12, 2019)\",\"artist\":\"Maddie and Tae\",\"datetime\":\"Sep 13, 2019 01:00:00\",\"timestamp\":1568336400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/34902109-maddie-and-tae-at-staples-center?utm_source=51967&utm_medium=partner\"}]}";

			String musicEventId = "vv170Z4fGkHRalhK";
			String musicVenueId = "KovZpZAEkn6A";
			String musicEventJson = "{\"eventInfo\":{\"name\":\"Disturbed: Evolution World Tour Presented by KLOS\",\"artistTeam\":[\"Disturbed\",\"Three Days Grace\"],\"venue\":\"The Forum\",\"time\":\"Jan 11, 2019 19:30:00\",\"category\":\"Music | Metal\",\"priceRange\":\"$49.00 ~ $94.50\",\"ticketStatus\":\"Onsale\",\"buyTicketAt\":\"https://www.ticketmaster.com/disturbed-evolution-world-tour-presented-by-inglewood-california-01-11-2019/event/0900554F2F26B6D3\",\"seatmap\":\"https://s1.ticketm.net/tmimages/venue/maps/la1/89876s.gif\"},\"venueInfo\":{\"name\":\"The Forum\",\"address\":\"3900 W Manchester Blvd.\",\"city\":\"Inglewood\",\"phoneNumber\":null,\"openHours\":\"Box office Hours: The Forum Box Office is located on the West side (Prairie Avenue side) of the venue. The Box Office is open Monday through Friday from 11:00 am - 7:00 pm, Saturdays & Sundays (on event days only), opens 12 noon\",\"generalRule\":\"ARRIVE EARLY: Please arrive at least one-hour prior to show time. All bags, including purses, will be inspected prior to entry while all patrons will go through a screening process including the use of metal detectors and pat downs as needed. Please be mindful of traffic conditions and local street closures/construction. For a list of prohibited items, please visit www.thefabulousforum.com. No smoking of any substance and no electronic cigarettes are permitted in the Forum No recording devices are permitted No reentry No outside food or beverage is permitted There are no bag or coat check facilities Alcohol Management For most events at the Forum, alcoholic beverages are available for purchase. Staff is trained in the National Restaurant Association Training program. Alcohol sales will be limited to two alcoholic drinks per customer per transaction. Guests are not permitted to bring alcoholic beverages from outside the Forum and may not leave with alcohol purchased at the Forum. Management reserves the right to refuse the sale of alcohol to any guest. All guests may be required to show ID to purchase alcohol. Please be aware it is the policy of the Forum to require all guests who appear to be under forty (40) years of age or younger to present a valid form of ID with proof of age in order to purchase alcoholic beverages at the Forum. Pursuant to applicable state law, The Forum accepts only ID cards issued by a governmental agency that include a current description and picture of the person presenting it, which reasonably describes the person as to date of birth, weight, height, sex, and colors of eyes/hair. The Forum will not accept an ID that has been altered or is expired. We will also not accept a registration certificate issued under the Federal Selective Service Act.\",\"childRule\":\"Events are all ages (unless noted). Everyone must have a ticket to enter the venue, regardless of age. For age restricted events a valid government issues ID will be required for entry.\",\"lat\":33.9583,\"lng\":-118.341868},\"artistInfos\":[{\"name\":\"Three Days Grace\",\"followers\":2544317,\"popularity\":77,\"checkAt\":\"https://open.spotify.com/artist/2xiIXseIJcq3nG7C8fHeBj\"},{\"name\":\"Disturbed\",\"followers\":2471821,\"popularity\":80,\"checkAt\":\"https://open.spotify.com/artist/3TOqt5oJwL9BE2NG9MEwDa\"}],\"photos\":[{\"name\":\"Disturbed\",\"links\":[\"http://cdn1-www.musicfeeds.com.au/assets/uploads/fbba95eefa261d547c280dd4c5300e86.jpg\",\"https://i.ytimg.com/vi/MwMp5OkYjd4/maxresdefault.jpg\",\"https://cdn.wegow.com/media/artists/disturbed/disturbed-1519657856.14.2560x1440.jpg\",\"https://stmed.net/sites/default/files/disturbed-wallpapers-30339-6533847.jpg\",\"https://images-na.ssl-images-amazon.com/images/I/81Gv9Uu8dML._SL1425_.jpg\",\"https://i.ytimg.com/vi/emp6pFb3Ytg/maxresdefault.jpg\",\"https://images.musicstore.de/images/1600/alfred-music-disturbed-indestructible_1_NOT0005303-000.jpg\",\"https://images1.westword.com/imager/u/original/8345026/disturbed_travis_shinn.jpg\",\"https://up-1.cdn-fullscreendirect.com/photos/5559/original/20181018_015445_5559_1061806.jpeg\"]},{\"name\":\"Three Days Grace\",\"links\":[\"https://www.allthingsloud.com/wp-content/uploads/2018/05/2018_0121_5559_7848.jpeg\",\"https://media.socastsrm.com/wordpress/wp-content/blogs.dir/684/files/2018/08/three-days-grace-infra-red.jpg\",\"http://cdn.stagebloc.com/production/photos/5559/original/20150331_172706_5559_712189.jpeg\",\"https://i.ytimg.com/vi/jNgq1s7Z1w4/maxresdefault.jpg\",\"https://up-1.cdn-fullscreendirect.com/photos/customfield/5559/20180309_121630_5559_6223.jpeg\",\"https://cdn.wegow.com/media/artists/three-days-grace/three-days-grace-1519661555.0.2560x1440.jpg\",\"https://www.theedgesusu.co.uk/wp-content/uploads/2018/10/three-days-grace.jpeg\",\"http://loudwire.com/files/2018/02/Foo-Fighters-Rise-Against-Three-Days-Grace.jpg\",\"https://i.ytimg.com/vi/50WH8OpSTS4/maxresdefault.jpg\"]}],\"upcomingEvents\":[{\"id\":35634484,\"name\":\"Cardi B, Calvin Harris, Camila Cabello, Khalid, and 5 more… at The Forum (November 30, 2018)\",\"artist\":\"Cardi B\",\"datetime\":\"Dec 01, 2018 03:30:00\",\"timestamp\":1543635000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35634484-cardi-b-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":34723859,\"name\":\"Marc Anthony and Mau y Ricky at The Forum (December 2, 2018)\",\"artist\":\"Marc Anthony\",\"datetime\":\"Dec 03, 2018 03:00:00\",\"timestamp\":1543806000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/34723859-marc-anthony-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35932824,\"name\":\"KROQ Almost Acoustic Christmas 2018\",\"artist\":\"Mike Shinoda\",\"datetime\":\"Dec 09, 2018 00:00:00\",\"timestamp\":1544313600,\"type\":\"Festival\",\"link\":\"http://www.songkick.com/festivals/6351-kroq-almost-acoustic-christmas/id/35932824-kroq-almost-acoustic-christmas-2018?utm_source=51967&utm_medium=partner\"},{\"id\":36045379,\"name\":\"KROQs Absolut Almost Acoustic Christmas 2018\",\"artist\":\"The Smashing Pumpkins\",\"datetime\":\"Dec 09, 2018 00:00:00\",\"timestamp\":1544313600,\"type\":\"Festival\",\"link\":\"http://www.songkick.com/festivals/2747054-kroqs-absolut-almost-acoustic-christmas/id/36045379-kroqs-absolut-almost-acoustic-christmas-2018?utm_source=51967&utm_medium=partner\"},{\"id\":35926459,\"name\":\"Kroq Absolut Almost Acoustic Christmas 2018\",\"artist\":\"Bastille\",\"datetime\":\"Dec 10, 2018 00:00:00\",\"timestamp\":1544400000,\"type\":\"Festival\",\"link\":\"http://www.songkick.com/festivals/2728949-kroq-absolut-almost-acoustic-christmas/id/35926459-kroq-absolut-almost-acoustic-christmas-2018?utm_source=51967&utm_medium=partner\"},{\"id\":33919044,\"name\":\"Fleetwood Mac at The Forum (December 11, 2018)\",\"artist\":\"Fleetwood Mac\",\"datetime\":\"Dec 11, 2018\",\"timestamp\":1544486400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33919044-fleetwood-mac-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":33919054,\"name\":\"Fleetwood Mac at The Forum (December 13, 2018)\",\"artist\":\"Fleetwood Mac\",\"datetime\":\"Dec 13, 2018\",\"timestamp\":1544659200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/33919054-fleetwood-mac-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":36054394,\"name\":\"Fleetwood Mac at The Forum (December 15, 2018)\",\"artist\":\"Fleetwood Mac\",\"datetime\":\"Dec 15, 2018\",\"timestamp\":1544832000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36054394-fleetwood-mac-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35500694,\"name\":\"Childish Gambino and Rae Sremmurd at The Forum (December 16, 2018)\",\"artist\":\"Childish Gambino\",\"datetime\":\"Dec 17, 2018 03:30:00\",\"timestamp\":1545017400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35500694-childish-gambino-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35500679,\"name\":\"Childish Gambino and Vince Staples at The Forum (December 17, 2018)\",\"artist\":\"Childish Gambino\",\"datetime\":\"Dec 18, 2018 03:30:00\",\"timestamp\":1545103800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35500679-childish-gambino-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35026779,\"name\":\"Travis Scott, Trippie Redd, Gunna, Sheck Wes, and 1 more… at The Forum (December 19, 2018)\",\"artist\":\"Travis Scott\",\"datetime\":\"Dec 20, 2018 03:30:00\",\"timestamp\":1545276600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35026779-travis-scott-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35055204,\"name\":\"Travis Scott, Trippie Redd, Gunna, Sheck Wes, and 1 more… at The Forum (December 20, 2018)\",\"artist\":\"Travis Scott\",\"datetime\":\"Dec 21, 2018 03:30:00\",\"timestamp\":1545363000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35055204-travis-scott-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35762329,\"name\":\"Ozzfest 2018\",\"artist\":\"Marilyn Manson\",\"datetime\":\"Dec 31, 2018 22:30:00\",\"timestamp\":1546295400,\"type\":\"Festival\",\"link\":\"http://www.songkick.com/festivals/1324-ozzfest/id/35762329-ozzfest-2018?utm_source=51967&utm_medium=partner\"},{\"id\":35733889,\"name\":\"Disturbed with Three Days Grace at The Forum (January 11, 2019)\",\"artist\":\"Disturbed\",\"datetime\":\"Jan 12, 2019 03:30:00\",\"timestamp\":1547263800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35733889-disturbed-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35534569,\"name\":\"iHeartRadio ALTer EGO 2019\",\"artist\":\"The Killers\",\"datetime\":\"Jan 20, 2019 03:30:00\",\"timestamp\":1547955000,\"type\":\"Festival\",\"link\":\"http://www.songkick.com/festivals/2661154-iheartradio-alter-ego/id/35534569-iheartradio-alter-ego-2019?utm_source=51967&utm_medium=partner\"},{\"id\":35845989,\"name\":\"A$AP Rocky at The Forum (January 31, 2019)\",\"artist\":\"A$AP Rocky\",\"datetime\":\"Feb 01, 2019 04:00:00\",\"timestamp\":1548993600,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35845989-asap-rocky-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35049969,\"name\":\"Elton John at The Forum (February 1, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Feb 02, 2019 03:00:00\",\"timestamp\":1549076400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35049969-elton-john-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35049984,\"name\":\"Elton John at The Forum (February 2, 2019)\",\"artist\":\"Elton John\",\"datetime\":\"Feb 03, 2019 03:00:00\",\"timestamp\":1549162800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35049984-elton-john-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35029414,\"name\":\"Bring Me The Horizon with FEVER 333 at The Forum (February 13, 2019)\",\"artist\":\"Bring Me The Horizon\",\"datetime\":\"Feb 14, 2019 03:00:00\",\"timestamp\":1550113200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35029414-bring-me-the-horizon-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":36057359,\"name\":\"Los Temerarios at The Forum (February 14, 2019)\",\"artist\":\"Los Temerarios\",\"datetime\":\"Feb 15, 2019 04:00:00\",\"timestamp\":1550203200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36057359-los-temerarios-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":34293034,\"name\":\"Panic! At the Disco with Two Feet at The Forum (February 15, 2019)\",\"artist\":\"Panic! At the Disco\",\"datetime\":\"Feb 16, 2019 03:00:00\",\"timestamp\":1550286000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/34293034-panic-at-the-disco-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35843394,\"name\":\"Kiss at The Forum (February 16, 2019)\",\"artist\":\"Kiss\",\"datetime\":\"Feb 16, 2019\",\"timestamp\":1550275200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35843394-kiss-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":30993174,\"name\":\"Bob Seger & The Silver Bullet Band at The Forum (February 23, 2019)\",\"artist\":\"Bob Seger & The Silver Bullet Band\",\"datetime\":\"Feb 24, 2019 04:00:00\",\"timestamp\":1550980800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/30993174-bob-seger-and-the-silver-bullet-band-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":34016599,\"name\":\"Bob Seger at The Forum (February 23, 2019)\",\"artist\":\"Bob Seger\",\"datetime\":\"Feb 23, 2019\",\"timestamp\":1550880000,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/34016599-bob-seger-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35888724,\"name\":\"Pandora and Yuri at The Forum (March 9, 2019)\",\"artist\":\"Pandora\",\"datetime\":\"Mar 10, 2019 04:00:00\",\"timestamp\":1552190400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35888724-pandora-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":36103589,\"name\":\"Camila and Sin Bandera at The Forum (April 12, 2019)\",\"artist\":\"Camila\",\"datetime\":\"Apr 13, 2019 03:30:00\",\"timestamp\":1555126200,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/36103589-camila-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35144369,\"name\":\"Pink at The Forum (April 19, 2019)\",\"artist\":\"Pink\",\"datetime\":\"Apr 20, 2019 02:30:00\",\"timestamp\":1555727400,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35144369-pink-at-forum?utm_source=51967&utm_medium=partner\"},{\"id\":35810819,\"name\":\"Ariana Grande at The Forum (May 10, 2019)\",\"artist\":\"Ariana Grande\",\"datetime\":\"May 11, 2019 02:30:00\",\"timestamp\":1557541800,\"type\":\"Concert\",\"link\":\"http://www.songkick.com/concerts/35810819-ariana-grande-at-forum?utm_source=51967&utm_medium=partner\"}]}";

			Gson gson = new Gson();
			eventDetail = gson.fromJson(musicEventJson, EventDetail.class);

			// Fill in event id and venue id
			eventDetail.eventInfo.id = musicEventId;
			eventDetail.venueInfo.id = musicVenueId;
/*
			initializeViewWithEventDetail();

			return;*/

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

package com.yejoopark.eventsearch.fragments.eventdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yejoopark.eventsearch.R;
import com.yejoopark.eventsearch.activities.EventDetailActivity;
import com.yejoopark.eventsearch.helpers.ViewHelper;
import com.yejoopark.eventsearch.models.ArtistInfo;
import com.yejoopark.eventsearch.models.ArtistTeamPhotos;
import com.yejoopark.eventsearch.models.EventDetail;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistTeamPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistTeamPhotosFragment extends Fragment {
	private static final String TAG = "ArtistTeamPhotosFragmen";
	private static final int MAX_NUM_ARTISTS_TO_DISPLAY = 2;
	private static final int MAX_NUM_PHOTOS_TO_DISPLAY = 8;

	private EventDetail eventDetail;
	private ArtistInfo[] artistInfos;
	private ArtistTeamPhotos[] artistTeamPhotosArray;

	private LinearLayout artist1InfoWrapper;
	private TextView artist1Name;
	private TableLayout artist1InfoTableLayout;
	private LinearLayout artist1PhotosWrapper;

	private LinearLayout artist2InfoWrapper;
	private TextView artist2Name;
	private TableLayout artist2InfoTableLayout;
	private LinearLayout artist2PhotosWrapper;

	public ArtistTeamPhotosFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment ArtistTeamPhotosFragment.
	 */
	public static ArtistTeamPhotosFragment newInstance() {
		ArtistTeamPhotosFragment fragment = new ArtistTeamPhotosFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get event detail data from parent activity
		eventDetail = ((EventDetailActivity) getActivity()).getEventDetail();
		artistInfos = eventDetail.artistInfos;
		artistTeamPhotosArray = eventDetail.photos;

		if (artistTeamPhotosArray != null && artistTeamPhotosArray.length >= MAX_NUM_ARTISTS_TO_DISPLAY) {
			artistTeamPhotosArray = Arrays.copyOfRange(artistTeamPhotosArray, 0, MAX_NUM_ARTISTS_TO_DISPLAY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_artist_team_photos, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		artist1InfoWrapper = view.findViewById(R.id.artist1InfoWrapper);
		artist1Name = view.findViewById(R.id.artist1Name);
		artist1InfoTableLayout = view.findViewById(R.id.artist1InfoTableLayout);
		artist1PhotosWrapper = view.findViewById(R.id.artist1PhotosWrapper);

		artist2InfoWrapper = view.findViewById(R.id.artist2InfoWrapper);
		artist2Name = view.findViewById(R.id.artist2Name);
		artist2InfoTableLayout = view.findViewById(R.id.artist2InfoTableLayout);
		artist2PhotosWrapper = view.findViewById(R.id.artist2PhotosWrapper);

		artist1InfoWrapper.setVisibility(View.GONE);
		artist2InfoWrapper.setVisibility(View.GONE);

		int imageViewPaddingPixel = ViewHelper.dpToPixel(getActivity(), 10);

		if (artistTeamPhotosArray == null) {
			// Do nothing
			return;
		}

		// First artist/team
		if (artistTeamPhotosArray.length > 0) {
			ArtistTeamPhotos artistTeamPhotos = artistTeamPhotosArray[0];

			artist1Name.setText(artistTeamPhotos.name);

			ArtistInfo artistInfo = getArtistInfo(artistTeamPhotos.name);

			if (artistInfo != null) {
				// populate table here
				if (artistInfo.name != null) {
					ViewHelper.addTextRowToTable(artist1InfoTableLayout, getActivity(), "Name", artistInfo.name);
				}

				String followers = NumberFormat.getNumberInstance(Locale.US).format(artistInfo.followers);

				ViewHelper.addTextRowToTable(artist1InfoTableLayout, getActivity(), "Followers", followers);
				ViewHelper.addTextRowToTable(artist1InfoTableLayout, getActivity(), "Popularity", Integer.toString(artistInfo.popularity));

				if (artistInfo.checkAt != null) {
					ViewHelper.addLinkedTextRowToTable(artist1InfoTableLayout, getActivity(), "Check At", "Spotify", artistInfo.checkAt);
				}
			}

			artistTeamPhotos.links = Arrays.copyOfRange(artistTeamPhotos.links, 0, MAX_NUM_PHOTOS_TO_DISPLAY);

			for (String imageUrl : artistTeamPhotos.links) {
				// Create an empty ImageView
				ImageView newImage = new ImageView(getActivity());

				newImage.setPadding(imageViewPaddingPixel, imageViewPaddingPixel, imageViewPaddingPixel, imageViewPaddingPixel);

				// Load image using Glide
				Glide.with(getActivity())
						.asBitmap()
						.load(imageUrl)
						.into(newImage);

				artist1PhotosWrapper.addView(newImage);
			}

			artist1InfoWrapper.setVisibility(View.VISIBLE);
		}

		// Second artist/team
		if (artistTeamPhotosArray.length > 1) {
			ArtistTeamPhotos artistTeamPhotos = artistTeamPhotosArray[1];

			artist2Name.setText(artistTeamPhotos.name);

			ArtistInfo artistInfo = getArtistInfo(artistTeamPhotos.name);

			if (artistInfo != null) {
				// populate table here
				if (artistInfo.name != null) {
					ViewHelper.addTextRowToTable(artist2InfoTableLayout, getActivity(), "Name", artistInfo.name);
				}

				String followers = NumberFormat.getNumberInstance(Locale.US).format(artistInfo.followers);

				ViewHelper.addTextRowToTable(artist2InfoTableLayout, getActivity(), "Followers", followers);
				ViewHelper.addTextRowToTable(artist2InfoTableLayout, getActivity(), "Popularity", Integer.toString(artistInfo.popularity));

				if (artistInfo.checkAt != null) {
					ViewHelper.addLinkedTextRowToTable(artist2InfoTableLayout, getActivity(), "Check At", "Spotify", artistInfo.checkAt);
				}
			}

			artistTeamPhotos.links = Arrays.copyOfRange(artistTeamPhotos.links, 0, MAX_NUM_PHOTOS_TO_DISPLAY);

			for (String imageUrl : artistTeamPhotos.links) {
				// Create an empty ImageView
				ImageView newImage = new ImageView(getActivity());

				newImage.setAdjustViewBounds(true);
				newImage.setPadding(imageViewPaddingPixel, imageViewPaddingPixel, imageViewPaddingPixel, imageViewPaddingPixel);

				// Load image using Glide
				Glide.with(getActivity())
						.asBitmap()
						.load(imageUrl)
						.into(newImage);

				artist2PhotosWrapper.addView(newImage);
			}

			artist2InfoWrapper.setVisibility(View.VISIBLE);
		}
	}

	private ArtistInfo getArtistInfo(String artistName) {
		if (artistInfos != null) {
			for (ArtistInfo artistInfo : artistInfos) {
				if (artistInfo.name.equalsIgnoreCase(artistName)) {
					return artistInfo;
				}
			}
		}

		return null;
	}
}

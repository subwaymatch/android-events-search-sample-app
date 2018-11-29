package com.example.myfirstapp.fragments.eventdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.myfirstapp.R;
import com.example.myfirstapp.activities.EventDetailActivity;
import com.example.myfirstapp.helpers.ViewHelper;
import com.example.myfirstapp.models.EventDetail;
import com.example.myfirstapp.models.EventInfo;
import com.example.myfirstapp.models.VenueInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenueInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueInfoFragment extends Fragment implements OnMapReadyCallback {
	private static final String TAG = "VenueInfoFragment";

	private EventDetail eventDetail;
	private VenueInfo venueInfo;
	private TableLayout venueInfoTableLayout;

	private MapView mapView;
	private GoogleMap map;

	public VenueInfoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment VenueInfoFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static VenueInfoFragment newInstance() {
		VenueInfoFragment fragment = new VenueInfoFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get event detail data from parent activity
		eventDetail = ((EventDetailActivity) getActivity()).getEventDetail();
		venueInfo = eventDetail.venueInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_venue_info, container, false);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapView = view.findViewById(R.id.venueMap);
		mapView.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate: mapView=" + mapView);

		mapView.getMapAsync(this);
		return view;
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		venueInfoTableLayout = view.findViewById(R.id.venueInfoTableLayout);

		if (venueInfo.name != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "Name", venueInfo.name);
		}

		if (venueInfo.address != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "Address", venueInfo.address);
		}

		if (venueInfo.city != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "City", venueInfo.city);
		}

		if (venueInfo.phoneNumber != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "Phone Number", venueInfo.phoneNumber);
		}

		if (venueInfo.openHours != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "Open Hours", venueInfo.openHours);
		}

		if (venueInfo.generalRule != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "General Rule", venueInfo.generalRule);
		}

		if (venueInfo.childRule != null) {
			ViewHelper.addTextRowToTable(venueInfoTableLayout, getActivity(), "Child Rule", venueInfo.childRule);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mapView != null) {
			mapView.onResume();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mapView != null) {
			mapView.onDestroy();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		if (mapView != null) {
			mapView.onLowMemory();
		}
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		Log.d(TAG, "onMapReady: map loaded");

		map = googleMap;

		// Add a marker to venue location and move the camera
		LatLng venueLatLng = new LatLng(venueInfo.lat, venueInfo.lng);
		map.addMarker(new MarkerOptions().position(venueLatLng));
		map.moveCamera(CameraUpdateFactory.newLatLng(venueLatLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
	}
}

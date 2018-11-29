package com.example.myfirstapp.fragments.eventdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myfirstapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenueInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueInfoFragment extends Fragment {
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_venue_info, container, false);
	}
}

package com.example.myfirstapp.fragments.eventdetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.activities.EventDetailActivity;
import com.example.myfirstapp.helpers.ViewHelper;
import com.example.myfirstapp.models.EventDetail;
import com.example.myfirstapp.models.EventInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventInfoFragment extends Fragment {
	private static final String TAG = "EventInfoFragment";

	private EventDetail eventDetail;
	private EventInfo eventInfo;
	private TableLayout eventInfoTableLayout;

	public EventInfoFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment EventInfoFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static EventInfoFragment newInstance() {
		EventInfoFragment fragment = new EventInfoFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get event detail data from 
		eventDetail = ((EventDetailActivity) getActivity()).getEventDetail();
		eventInfo = eventDetail.eventInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_event_info, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		eventInfoTableLayout = getActivity().findViewById(R.id.eventInfoTableLayout);

		if (eventInfo.artistTeam != null && eventInfo.artistTeam.length > 0) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Artist/Team(s)", TextUtils.join(" | ", eventInfo.artistTeam));
		}

		if (eventInfo.venue != null) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Venue", eventInfo.venue);
		}

		if (eventInfo.time != null) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Venue", eventInfo.time);
		}

		if (eventInfo.category != null) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Category", eventInfo.category);
		}

		if (eventInfo.priceRange != null) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Price Range", eventInfo.priceRange);
		}

		if (eventInfo.ticketStatus != null) {
			ViewHelper.addTextRowToTable(eventInfoTableLayout, getActivity(), "Ticket Status", eventInfo.ticketStatus);
		}

		if (eventInfo.buyTicketAt != null) {
			ViewHelper.addLinkedTextRowToTable(eventInfoTableLayout, getActivity(), "Buy Ticket At", "Ticketmaster", eventInfo.buyTicketAt);
		}

		if (eventInfo.seatmap != null) {
			ViewHelper.addLinkedTextRowToTable(eventInfoTableLayout, getActivity(), "Seat Map", "View Here", eventInfo.seatmap);
		}
	}
}

package com.example.myfirstapp.fragments.eventdetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.activities.EventDetailActivity;
import com.example.myfirstapp.models.EventDetail;
import com.example.myfirstapp.models.UpcomingEvent;
import com.example.myfirstapp.viewadapters.UpcomingEventsListAdapter;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingEventsFragment extends Fragment {
	private static final String TAG = "UpcomingEventsFragment";
	private static final int numOfItemsToDisplay = 5;

	private EventDetail eventDetail;
	private UpcomingEvent[] upcomingEvents;

	private UpcomingEventsListAdapter recyclerViewAdapter;

	private ScrollView upcomingEventsListWrapper;
	private RecyclerView eventsRecyclerView;
	private TextView listEmptyText;
	private Spinner upcomingEventsSortBySpinner;
	private Spinner upcomingEventsSortDirectionSpinner;

	public UpcomingEventsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment UpcomingEventsFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static UpcomingEventsFragment newInstance() {
		UpcomingEventsFragment fragment = new UpcomingEventsFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get event detail data from parent activity
		eventDetail = ((EventDetailActivity) getActivity()).getEventDetail();
		upcomingEvents = eventDetail.upcomingEvents;

		if (upcomingEvents != null && upcomingEvents.length > 0) {
			upcomingEvents = Arrays.copyOfRange(upcomingEvents, 0, numOfItemsToDisplay);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_upcoming_events, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		upcomingEventsListWrapper = view.findViewById(R.id.upcomingEventsListWrapper);
		eventsRecyclerView = view.findViewById(R.id.upcomingEventsRecyclerView);
		listEmptyText = view.findViewById(R.id.upcomingEventsEmptyText);

		initRecyclerView();
	}

	private void initRecyclerView() {
		if (upcomingEvents != null && upcomingEvents.length > 0) {
			recyclerViewAdapter = new UpcomingEventsListAdapter(getActivity(), Arrays.asList(upcomingEvents));

			eventsRecyclerView.setAdapter(recyclerViewAdapter);
			eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

			upcomingEventsListWrapper.setVisibility(View.VISIBLE);
			listEmptyText.setVisibility(View.GONE);
		} else {
			showEmptyMessage();
		}
	}

	public void showEmptyMessage() {
		upcomingEventsListWrapper.setVisibility(View.GONE);
		listEmptyText.setVisibility(View.VISIBLE);
	}
}

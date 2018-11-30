package com.yejoopark.eventsearch.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yejoopark.eventsearch.R;
import com.yejoopark.eventsearch.helpers.FavoriteEventsHelper;
import com.yejoopark.eventsearch.models.EventSummary;
import com.yejoopark.eventsearch.viewadapters.FavoriteEventsListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteEventsFragment extends Fragment {
	private static final String TAG = "FavoriteEventsFragment";

	private FavoriteEventsListAdapter recyclerViewAdapter;

	private RecyclerView eventsRecyclerView;
	private TextView listEmptyText;

	public FavoriteEventsFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment FavoriteEventsFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static FavoriteEventsFragment newInstance() {
		FavoriteEventsFragment fragment = new FavoriteEventsFragment();
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
		return inflater.inflate(R.layout.fragment_favorite_events, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		eventsRecyclerView = view.findViewById(R.id.favoriteEventsRecyclerView);
		listEmptyText = view.findViewById(R.id.favoriteEventsEmptyText);
	}

	@Override
	public void onResume() {
		super.onResume();

		initRecyclerView();
	}

	private void initRecyclerView() {
		List<EventSummary> favoriteEventSummaries = FavoriteEventsHelper.getInstance().getFavoriteEvents();

		if (!favoriteEventSummaries.isEmpty()) {
			recyclerViewAdapter = new FavoriteEventsListAdapter(getActivity(), this, favoriteEventSummaries);

			eventsRecyclerView.setAdapter(recyclerViewAdapter);
			eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

			eventsRecyclerView.setVisibility(View.VISIBLE);
			listEmptyText.setVisibility(View.GONE);
		} else {
			showEmptyMessage();
		}
	}

	public void showEmptyMessage() {
		eventsRecyclerView.setVisibility(View.GONE);
		listEmptyText.setVisibility(View.VISIBLE);
	}
}

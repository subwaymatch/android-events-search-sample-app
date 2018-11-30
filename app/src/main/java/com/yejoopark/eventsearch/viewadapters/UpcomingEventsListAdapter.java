package com.yejoopark.eventsearch.viewadapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yejoopark.eventsearch.R;
import com.yejoopark.eventsearch.models.UpcomingEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpcomingEventsListAdapter extends RecyclerView.Adapter<UpcomingEventsListAdapter.ViewHolder> {
	private static final String TAG = "UpcomingEventsListAdapt";

	private Context mContext;

	private List<UpcomingEvent> mUpcomingEvents;

	private String sortBy;
	private int sortDirectionMultiplier;

	public UpcomingEventsListAdapter(Context mContext, List<UpcomingEvent> upcomingEvents) {
		this.mContext = mContext;

		this.mUpcomingEvents = upcomingEvents;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;

		Log.d(TAG, "setSortBy: new sortBy=" + this.sortBy);

		reorderList();
	}

	public void setSortDirection(String sortDirection) {
		// TODO: Set sort direction
		this.sortDirectionMultiplier = "Ascending".equals(sortDirection) ? 1 : -1;

		Log.d(TAG, "setSortDirection: new sortDirectionMultipler=" + this.sortDirectionMultiplier);

		reorderList();
	}

	public void reorderList() {
		Collections.sort(mUpcomingEvents, new Comparator<UpcomingEvent>() {
			public int compare(UpcomingEvent event1, UpcomingEvent event2) {
				int compareResult = 0;

				if ("Event Name".equals(sortBy)) {
					compareResult = event1.name.compareTo(event2.name);
				}

				else if ("Time".equals(sortBy)) {
					compareResult = ((Integer) event1.timestamp).compareTo(event2.timestamp);
				}

				else if ("Artist".equals(sortBy)) {
					compareResult = event1.artist.compareTo(event2.artist);
				}

				else if ("Type".equals(sortBy)) {
					compareResult = event1.type.compareTo(event2.type);
				}

				else {
					compareResult = event1.id.compareTo(event2.id);
				}

				if (!"Default".equals(sortBy)) {
					compareResult *= sortDirectionMultiplier;
				}

				return compareResult;
			}
		});

		this.notifyDataSetChanged();
	}


	// Responsible for inflating view
	@Override
	public UpcomingEventsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_event_item, parent, false);
		UpcomingEventsListAdapter.ViewHolder holder = new UpcomingEventsListAdapter.ViewHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(final UpcomingEventsListAdapter.ViewHolder viewHolder, final int position) {
		final UpcomingEvent upcomingEvent = mUpcomingEvents.get(position);

		viewHolder.eventName.setText(upcomingEvent.name);
		viewHolder.artistName.setText(upcomingEvent.artist);
		viewHolder.eventDate.setText(upcomingEvent.datetime);
		viewHolder.eventType.setText("Type: " + upcomingEvent.type);

		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(upcomingEvent.link));
				mContext.startActivity(browserIntent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mUpcomingEvents.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		RelativeLayout itemLayout;
		TextView eventName;
		TextView artistName;
		TextView eventDate;
		TextView eventType;

		// Holds the individual widgets in memory
		public ViewHolder(View itemView) {
			super(itemView);

			itemLayout = itemView.findViewById(R.id.upcoming_event_item_layout);
			eventName = itemView.findViewById(R.id.upcomingEventName);
			artistName = itemView.findViewById(R.id.upcomingEventArtist);
			eventDate = itemView.findViewById(R.id.upcomingEventDate);
			eventType = itemView.findViewById(R.id.upcomingEventType);
		}
	}
}

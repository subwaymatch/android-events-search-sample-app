package com.example.myfirstapp.viewadapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.models.UpcomingEvent;

import java.util.List;

public class UpcomingEventsListAdapter extends RecyclerView.Adapter<UpcomingEventsListAdapter.ViewHolder> {
	private static final String TAG = "UpcomingEventsListAdapt";

	private Context mContext;

	private List<UpcomingEvent> mUpcomingEvents;

	public UpcomingEventsListAdapter(Context mContext, List<UpcomingEvent> upcomingEvents) {
		this.mContext = mContext;

		this.mUpcomingEvents = upcomingEvents;
	}

	public void reorderList(String sortBy, boolean isAscending) {
		// TODO: Reorder upcoming events here
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
		viewHolder.eventType.setText(upcomingEvent.type);

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

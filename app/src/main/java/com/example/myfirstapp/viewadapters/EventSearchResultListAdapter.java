package com.example.myfirstapp.viewadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp.R;
import com.example.myfirstapp.helpers.FavoriteEventsHelper;
import com.example.myfirstapp.helpers.ViewHelper;
import com.example.myfirstapp.models.EventSummary;

import java.util.List;

public class EventSearchResultListAdapter extends RecyclerView.Adapter<EventSearchResultListAdapter.ViewHolder> {
	private static final String TAG = "EventSearchResultListAd";
	private List<EventSummary> mEventSummaries;
	private Context mContext;
	private final FavoriteEventsHelper favoriteEventsHelper;

	public EventSearchResultListAdapter(Context mContext, List<EventSummary> eventSummaries) {
		this.mEventSummaries = eventSummaries;
		this.mContext = mContext;
		this.favoriteEventsHelper = FavoriteEventsHelper.getInstance();
	}

	// Responsible for inflating view
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_list_row, parent, false);
		ViewHolder holder = new ViewHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		final EventSummary eventSummary = mEventSummaries.get(position);

		// Set category
		ViewHelper.setCategoryIconToImageView(viewHolder.categoryIcon, eventSummary.category);

		viewHolder.eventName.setText(eventSummary.name);
		viewHolder.venueName.setText(eventSummary.venueInfo);
		viewHolder.eventDate.setText(eventSummary.date);

		int favoriteIconId = favoriteEventsHelper.checkIfFavorite(eventSummary.id) ?
				R.drawable.heart_fill_red : R.drawable.heart_outline_black;

		viewHolder.favoriteIcon.setImageResource(favoriteIconId);

		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, eventSummary.name, Toast.LENGTH_SHORT).show();
			}
		});

		viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: favorite icon clicked");

				if (favoriteEventsHelper.checkIfFavorite(eventSummary.id)) {
					favoriteEventsHelper.remove(eventSummary.id);
					viewHolder.favoriteIcon.setImageResource(R.drawable.heart_outline_black);
					Toast.makeText(mContext,  eventSummary.name + " was removed from favorites", Toast.LENGTH_SHORT).show();
				}

				else {
					favoriteEventsHelper.add(eventSummary);
					viewHolder.favoriteIcon.setImageResource(R.drawable.heart_fill_red);
					Toast.makeText(mContext,  eventSummary.name + " was added to favorites", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mEventSummaries.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		RelativeLayout itemLayout;
		ImageView categoryIcon;
		TextView eventName;
		TextView venueName;
		TextView eventDate;
		ImageView favoriteIcon;

		// Holds the individual widgets in memory
		public ViewHolder(View itemView) {
			super(itemView);

			Log.d(TAG, "ViewHolder: itemView=" + itemView);

			categoryIcon = itemView.findViewById(R.id.categoryIcon);
			eventName = itemView.findViewById(R.id.eventName);
			venueName = itemView.findViewById(R.id.venueName);
			eventDate = itemView.findViewById(R.id.eventDate);
			itemLayout = itemView.findViewById(R.id.event_summary_item_layout);
			favoriteIcon = itemView.findViewById(R.id.favoriteIcon);

			Log.d(TAG, "ViewHolder: categoryIcon=" + categoryIcon);
			Log.d(TAG, "ViewHolder: eventName=" + eventName);
		}
	}
}

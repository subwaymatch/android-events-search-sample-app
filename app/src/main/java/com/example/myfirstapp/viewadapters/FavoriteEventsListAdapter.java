package com.example.myfirstapp.viewadapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp.R;
import com.example.myfirstapp.activities.EventDetailActivity;
import com.example.myfirstapp.fragments.FavoriteEventsFragment;
import com.example.myfirstapp.helpers.FavoriteEventsHelper;
import com.example.myfirstapp.helpers.ViewHelper;
import com.example.myfirstapp.models.EventSummary;

import java.util.List;

public class FavoriteEventsListAdapter extends RecyclerView.Adapter<FavoriteEventsListAdapter.ViewHolder> {
	private static final String TAG = "FavoriteEventsListAdapt";

	private Context mContext;
	private FavoriteEventsFragment favoriteEventsFragment;

	private List<EventSummary> mEventSummaries;
	private final FavoriteEventsHelper favoriteEventsHelper;

	public FavoriteEventsListAdapter(Context mContext, FavoriteEventsFragment favoriteEventsFragment, List<EventSummary> eventSummaries) {
		this.mContext = mContext;
		this.favoriteEventsFragment = favoriteEventsFragment;

		this.mEventSummaries = eventSummaries;
		this.favoriteEventsHelper = FavoriteEventsHelper.getInstance();
	}

	public void updateList(List<EventSummary> newList) {
		mEventSummaries = newList;
	}

	// Responsible for inflating view
	@Override
	public FavoriteEventsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_list_item, parent, false);
		FavoriteEventsListAdapter.ViewHolder holder = new FavoriteEventsListAdapter.ViewHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(final FavoriteEventsListAdapter.ViewHolder viewHolder, final int position) {
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
				Intent intent = new Intent(mContext, EventDetailActivity.class);
				intent.putExtra("eventSummary", eventSummary);
				mContext.startActivity(intent);
			}
		});

		viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (favoriteEventsHelper.checkIfFavorite(eventSummary.id)) {
					favoriteEventsHelper.remove(eventSummary);

					mEventSummaries.remove(eventSummary);
					notifyItemRemoved(position);
					notifyItemRangeChanged(position, mEventSummaries.size());

					if (mEventSummaries.isEmpty()) {
						favoriteEventsFragment.showEmptyMessage();
					}

					Toast.makeText(mContext,  eventSummary.name + " was removed from favorites", Toast.LENGTH_SHORT).show();
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

			categoryIcon = itemView.findViewById(R.id.searchResultRowCategoryIcon);
			eventName = itemView.findViewById(R.id.searchResultRowEventName);
			venueName = itemView.findViewById(R.id.searchResultRowVenueName);
			eventDate = itemView.findViewById(R.id.searchResultRowEventDate);
			itemLayout = itemView.findViewById(R.id.event_summary_item_layout);
			favoriteIcon = itemView.findViewById(R.id.searchResultRowFavoriteIcon);
		}
	}
}

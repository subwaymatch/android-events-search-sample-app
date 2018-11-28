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
import com.example.myfirstapp.models.EventSummary;

import java.util.List;

public class EventSearchResultListAdapter extends RecyclerView.Adapter<EventSearchResultListAdapter.ViewHolder> {
	private static final String TAG = "EventSearchResultListAd";
	private List<EventSummary> mEventNames;
	private Context mContext;

	public EventSearchResultListAdapter(Context mContext, List<EventSummary> mEventNames) {
		this.mEventNames = mEventNames;
		this.mContext = mContext;
	}

	// Responsible for inflating view
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_list_row, parent, false);
		ViewHolder holder = new ViewHolder(view);

		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		Log.d(TAG, "onBindViewHolder: called.");
		Log.d(TAG, "onBindViewHolder: viewHolder=" + viewHolder);

		/*
		Glide.with(mContext)
				.asBitmap()
				.load(mCategoryIcons.get(position))
				.into(viewHolder.categoryIcon);
				*/

		viewHolder.eventName.setText(mEventNames.get(position).name);

		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: clicked on: " + mEventNames.get(position));

				Toast.makeText(mContext, mEventNames.get(position).name, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return mEventNames.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		RelativeLayout itemLayout;
		ImageView categoryIcon;
		TextView eventName;
		ImageView favoriteIcon;

		// Holds the individual widgets in memory
		public ViewHolder(View itemView) {
			super(itemView);

			Log.d(TAG, "ViewHolder: itemView=" + itemView);

			categoryIcon = itemView.findViewById(R.id.categoryIcon);
			eventName = itemView.findViewById(R.id.eventName);
			itemLayout = itemView.findViewById(R.id.event_summary_item_layout);

			Log.d(TAG, "ViewHolder: categoryIcon=" + categoryIcon);
			Log.d(TAG, "ViewHolder: eventName=" + eventName);
		}
	}
}

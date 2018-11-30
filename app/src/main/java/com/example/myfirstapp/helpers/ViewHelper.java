package com.example.myfirstapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myfirstapp.R;

public class ViewHelper {
	public static void setCategoryIconToImageView(ImageView imageView, String category) {
		int categoryImageId = R.drawable.miscellaneous_icon;

		if (category.startsWith("Music")) {
			categoryImageId = R.drawable.music_icon;
		}

		else if (category.startsWith("Sports")) {
			categoryImageId = R.drawable.sport_icon;
		}

		else if (category.startsWith("Film")) {
			categoryImageId = R.drawable.film_icon;
		}

		else if (category.startsWith("Arts")) {
			categoryImageId = R.drawable.art_icon;
		}

		imageView.setImageResource(categoryImageId);
	}

	public static TableRow addTextRowToTable(TableLayout tableLayout, final Context context, String rowLabelText, String rowValueText) {
		int fontSizeSp = 18;
		int paddingDp = 8;

		int paddingPixel = dpToPixel(context, paddingDp);

		TableRow tableRow = new TableRow(context);

		TableRow.LayoutParams headerCellLayoutParams = new TableRow.LayoutParams(0);
		headerCellLayoutParams.width = 0;
		headerCellLayoutParams.weight = 1;

		TextView headerCell = new TextView(context);
		headerCell.setText(rowLabelText);
		headerCell.setLayoutParams(headerCellLayoutParams);
		headerCell.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
		headerCell.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeSp);
		headerCell.setTypeface(headerCell.getTypeface(), Typeface.BOLD);

		TableRow.LayoutParams valueCellLayoutParams = new TableRow.LayoutParams(0);
		valueCellLayoutParams.width = 0;
		valueCellLayoutParams.weight = 2;

		TextView valueCell = new TextView(context);
		valueCell.setText(rowValueText);
		valueCell.setLayoutParams(valueCellLayoutParams);
		valueCell.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeSp);
		valueCell.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);

		tableRow.addView(headerCell);
		tableRow.addView(valueCell);
		tableLayout.addView(tableRow);

		return tableRow;
	}

	public static void addLinkedTextRowToTable(TableLayout tableLayout, final Context context, String rowLabelText, String rowLinkDisplayText, final String rowLinkUri) {
		TableRow addedRow = addTextRowToTable(tableLayout, context, rowLabelText, rowLinkDisplayText);

		// TODO: Set link style and attach click event handler
		TextView valueCell = (TextView) addedRow.getChildAt(1);
		valueCell.setTextColor(context.getResources().getColor(R.color.colorAccent));
		valueCell.setPaintFlags(valueCell.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		valueCell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rowLinkUri));
				context.startActivity(browserIntent);
			}
		});
	}

	public static int dpToPixel(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;

		return Math.round(dp * density);
	}
}

package com.example.myfirstapp.helpers;

import android.widget.ImageView;

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
}

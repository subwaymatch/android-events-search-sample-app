package com.example.myfirstapp.helpers;

public class DataSharingHelper {
	private static DataSharingHelper instance;

	private DataSharingHelper() {}

	public static DataSharingHelper getInstance() {
		if (instance == null) {
			instance = new DataSharingHelper();
		}

		return instance;
	}
}

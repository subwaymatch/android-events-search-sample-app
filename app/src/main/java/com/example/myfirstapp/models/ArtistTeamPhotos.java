package com.example.myfirstapp.models;

import java.util.Arrays;

public class ArtistTeamPhotos {
	public String name;
	public String[] links;

	@Override
	public String toString() {
		return "ArtistTeamPhotos{" +
				"name='" + name + '\'' +
				", links=" + Arrays.toString(links) +
				'}';
	}
}

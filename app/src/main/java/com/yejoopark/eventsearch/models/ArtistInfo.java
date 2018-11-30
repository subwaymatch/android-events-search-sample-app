package com.yejoopark.eventsearch.models;

public class ArtistInfo {
	public String name;
	public int followers;
	public int popularity;
	public String checkAt;

	@Override
	public String toString() {
		return "ArtistInfo{" +
				"name='" + name + '\'' +
				", followers=" + followers +
				", popularity=" + popularity +
				", checkAt='" + checkAt + '\'' +
				'}';
	}
}

package com.lochend.pharmacy.cloud;

import android.text.format.Time;

public class OpeningTime {
	@com.google.gson.annotations.SerializedName("Id")
	private String Id;
	
	@com.google.gson.annotations.SerializedName("StartTime")
	private String StartTime;
	
	@com.google.gson.annotations.SerializedName("EndTime")
	private String EndTime;
	
	@com.google.gson.annotations.SerializedName("Day")
	private int Day;
	
	public OpeningTime(){
		
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof OpeningTime && ((OpeningTime) o).getId() == getId();
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getStartTime() {
		return StartTime.toString();
	}

	public void setStartTime(String startTime) {
		StartTime =  startTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public int getDay() {
		return Day;
	}

	public void setDay(int day) {
		Day = day;
	}
	
}

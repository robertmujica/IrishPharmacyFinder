package com.lochend.pharmacylocation.entity;

import com.lochend.location.GlobalApp;

public class OpeningHour {
	private String _id;
	private int _day;
	private String _from;
	private String _to;
	private String _dayName;
	
	public int get_day() {
		return _day;
	}
	public void set_day(int _day) {
		this._day = _day;
		
		String[] weekDays = GlobalApp.getResources().getStringArray(com.lochend.pharmacylocation.R.array.weekDays);
		
		this.set_dayName(weekDays[_day + -1]);
		
	}
	
	public String get_from() {
		return _from;
	}
	public void set_from(String _from) {
		this._from = _from;
	}
	public String get_to() {
		return _to;
	}
	public void set_to(String _to) {
		this._to = _to;
	}
	public String get_dayName() {
		return _dayName;
	}
	
	private void set_dayName(String _dayName) {
		this._dayName = _dayName;
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
}

package com.lochend.pharmacylocation.entity;

import java.text.DecimalFormat;
import java.util.List;

public class PharmacyBase {
	private int _id;
	private String _name;
	private String _registrationNumber;
	private String _address;
	private String lat;
	private String lng;
	private List<OpeningHour> _openingHourList;
	private double distanceInKm;
	private String distance;
	
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name.trim();
	}
	public String getRegistrationNumber() {
		return _registrationNumber;
	}
	public void setRegistrationNumber(String _registrationNumber) {
		this._registrationNumber = _registrationNumber;
	}
	public String getAddress() {
		return _address;
	}
	public void setAddress(String _address) {
		this._address = _address.trim();
	}
	
	public List<OpeningHour> getOpeningHourList() {
		return _openingHourList;
	}
	public void setOpeningHourList(List<OpeningHour> _openingHourList) {
		this._openingHourList = _openingHourList;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
	public Object getDetails(){
		return this;
	}
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	
	public void setDistance(double distance) {
		distanceInKm = distance;
		DecimalFormat format = new DecimalFormat("#.##"); 
		this.distance = format.format(distance) + " km";
	}
	public void setDistanceString(String distance){
		this.distance = distance;
	}
	
	public String getDistance() {
		return distance;
	}
	
	public double getDistanceInKm(){
		return distanceInKm;
	}
}

package com.lochend.pharmacy.cloud;

public class Pharmacy {

	@com.google.gson.annotations.SerializedName("Id")
	private String Id;
	
	@com.google.gson.annotations.SerializedName("RegistrationNumber")
	private String RegistrationNumber;
	
	@com.google.gson.annotations.SerializedName("Name")
	private String Name;
	
	@com.google.gson.annotations.SerializedName("City")
	private String City;
	
	@com.google.gson.annotations.SerializedName("Address")
	private String Address;
	
	@com.google.gson.annotations.SerializedName("WebURL")
	private String WebURL;
	
	@com.google.gson.annotations.SerializedName("Email")
	private String Email;
	
	@com.google.gson.annotations.SerializedName("PhoneNumber")
	private String PhoneNumber;
	
	@com.google.gson.annotations.SerializedName("Lat")
	private String Lat;
	
	@com.google.gson.annotations.SerializedName("Long")
	private String Long;
	
	@com.google.gson.annotations.SerializedName("DistanceInKilometers")
	private double DistanceInKilometers;
	
	public Pharmacy(){
		
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Pharmacy && ((Pharmacy) o).getId() == getId();
	}



	public String getId() {
		return Id;
	}



	public void setId(String id) {
		Id = id;
	}



	public String getName() {
		return Name;
	}



	public void setName(String name) {
		Name = name;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}



	public String getWebURL() {
		return WebURL;
	}



	public void setWebURL(String webURL) {
		WebURL = webURL;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		PhoneNumber = phoneNumber;
	}

	public String getLat() {
		return Lat;
	}

	public void setLat(String lat) {
		Lat = lat;
	}

	public String getLong() {
		return Long;
	}

	public void setLong(String _long) {
		Long = _long;
	}

	public double getDistanceInKilometers() {
		return DistanceInKilometers;
	}

	public void setDistanceInKilometers(double distanceInKilometers) {
		DistanceInKilometers = distanceInKilometers;
	}
}

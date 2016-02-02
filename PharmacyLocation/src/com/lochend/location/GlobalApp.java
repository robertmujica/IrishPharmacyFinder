package com.lochend.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Location;
import android.text.format.Time;

import com.lochend.pharmacylocation.ApplicationContextProvider;
import com.lochend.pharmacylocation.IPharmacyQueryActivity;
import com.lochend.pharmacylocation.entity.PharmacyDetails;

public class GlobalApp {
	private static GlobalApp instance = null;
	private static PharmacyDetails currentPharmacyDetails;
	private static ArrayList<PharmacyDetails> currentPharmacyList;
	private static IPharmacyQueryActivity currentActivity;
	private static int _currentTabIndex;
	private static String _currentTabIndexClassName;
	private static String searchText;
	private static Location lastKownLocation;
	private static int openLastSearchRadius;
	private static int allLastSearchRadius;
	private static int lastDayOfWeekInCache;
	private static boolean allPharmaciesInCache;
	private static boolean openPharmaciesInCache;
	private static long openLastSearchTime;
	
	static Map<Integer,Integer> daysMapper = new HashMap<Integer, Integer>(){{
	    put(1, 7);
	    put(2, 1);
	    put(3, 2);
	    put(4, 3);
	    put(5, 4);
	    put(6, 5);
	    put(7, 6);
	}};
	
	public static GlobalApp getInstance() {
		if(instance == null)
			instance = new GlobalApp();
		return instance;
	}

	public static PharmacyDetails getCurrentPharmacyDetails() {
		return currentPharmacyDetails;
	}

	public static void setCurrentPharmacyDetails(PharmacyDetails pharmacyDetails) {
		currentPharmacyDetails = pharmacyDetails;
	}

	public static ArrayList<PharmacyDetails> getCurrentPharmacyList() {
		return currentPharmacyList;
	}

	public static void setCurrentPharmacyList(ArrayList<PharmacyDetails> pharmacyList) {
		currentPharmacyList = pharmacyList;
	}
	
	public static void AddToCurrentPharmacyList(PharmacyDetails pharmacy) {
		currentPharmacyList.add(pharmacy);
	}
	
	public static void setCurrentPharmacyByMarkerId(String markerId){
		for(PharmacyDetails pharmacy : currentPharmacyList){
			if(pharmacy.getMarkerId().equals(markerId)){
				setCurrentPharmacyDetails(pharmacy);
				break;
			}
		}
	}
	
	private static int getDayOfWeekJava()
	{
		Date currentDate = new Date(System.currentTimeMillis());
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getCurrentDayOfWeek(){
		int dayTemp = getDayOfWeekJava();
		return daysMapper.get(dayTemp);
	}
	
	public static int getDayOfWeek()
	{
		int dayOfWeek = getDayOfWeekJava();
		int day = dayOfWeek - 1;
		if(day == 0)
			return 7;
		else{
			return day;
		}
	}
	
	public static String getCurrentTime()
	{
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		Date t = new Date();
		
		String time = String.format("%02d:%02d:%02d.%03d", today.hour, today.minute, today.second, 0);
		return time;
	}
	
	public static String getCurrentTimeWithDate()
	{
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		Date t = new Date();
		
		String time = String.format("1900-01-01T%02d:%02d:%02d.%03dZ", today.hour, today.minute, today.second, 0);
		return time;
	}
	
	public static Resources getResources(){
		return  ApplicationContextProvider.getContext().getResources();
	}

	public static IPharmacyQueryActivity getCurrentActivity() {
		return currentActivity;
	}

	public static void setCurrentActivity(IPharmacyQueryActivity currentActivity) {
		GlobalApp.currentActivity = currentActivity;
	}

	public static int getCurrentTabIndex() {
		return _currentTabIndex;
	}

	public static void setCurrentTabIndex(String className, int currentTabIndex) {
		setCurrentTabIndexClassName(className);
		_currentTabIndex = currentTabIndex;
	}

	public static String getSearchText() {
		return searchText;
	}

	public static void setSearchText(String searchText) {
		GlobalApp.searchText = searchText;
	}

	public static String getCurrentTabIndexClassName() {
		if(_currentTabIndexClassName == null)
			return "";
		else
			return _currentTabIndexClassName;
	}

	public static void setCurrentTabIndexClassName(
			String currentTabIndexClassName) {
		_currentTabIndexClassName = currentTabIndexClassName;
	}

	public static Location getLastKownLocation() {
		if(lastKownLocation == null){
			lastKownLocation = new Location("");
		}
		return lastKownLocation;
	}

	public static void setLastKnownLocation(Location lastKownLocation) {
		GlobalApp.lastKownLocation = lastKownLocation;
	}
	
	public static boolean needRefresh(Location currentLocation, int radius, int lastSearchRadius){
		double distance = LocationUtils.distance(
				getLastKownLocation().getLatitude(), getLastKownLocation().getLongitude(), 
				currentLocation.getLatitude(),currentLocation.getLongitude());
				
		if(distance > LocationUtils.MAX_TOLERANCE_REFRESH_DISTANCE
				|| radius != lastSearchRadius){
			return true;
		}
		return false;
	}
	
	public static boolean allPharmacyNeedRefresh(Location currentLocation, int radius){
		return (needRefresh(currentLocation, radius, allLastSearchRadius) && isAllPharmaciesInCache())
				|| (needRefresh(currentLocation, radius, allLastSearchRadius) && !isAllPharmaciesInCache());
	}
	
	public static boolean onlyOpenPharmacyNeedRefresh(Location currentLocation, int radius, int dayOfWeek){
		return ((needRefresh(currentLocation, radius, openLastSearchRadius) && isOpenPharmaciesInCache())
				|| (needRefresh(currentLocation, radius, openLastSearchRadius) && !isOpenPharmaciesInCache())) 
				|| dayOfWeek != lastDayOfWeekInCache
				|| timeDiff(openLastSearchTime, new Date().getTime()) >= 30;
	}

	public static int getOpenLastSearchRadius() {
		return openLastSearchRadius;
	}

	public static void setOpenLastSearchRadius(int lastSearchRadius) {
		GlobalApp.openLastSearchRadius = lastSearchRadius;
	}

	public static boolean isAllPharmaciesInCache() {
		return allPharmaciesInCache;
	}

	public static void setAllPharmaciesInCache(boolean allPharmaciesIsCache) {
		GlobalApp.allPharmaciesInCache = allPharmaciesIsCache;
	}

	public static boolean isOpenPharmaciesInCache() {
		return openPharmaciesInCache;
	}

	public static void setOpenPharmaciesInCache(boolean openPharmaciesIsCache) {
		GlobalApp.openPharmaciesInCache = openPharmaciesIsCache;
	}

	public static int getLastDayOfWeekInCache() {
		return lastDayOfWeekInCache;
	}

	public static void setLastDayOfWeekInCache(int lastDayOfWeekInCache) {
		GlobalApp.lastDayOfWeekInCache = lastDayOfWeekInCache;
	}

	public static int getAllLastSearchRadius() {
		return allLastSearchRadius;
	}

	public static void setAllLastSearchRadius(int allLastSearchRadius) {
		GlobalApp.allLastSearchRadius = allLastSearchRadius;
	}

	public static long getOpenLastSearchTime() {
		return openLastSearchTime;
	}

	public static void setOpenLastSearchTime(long openLastSearchTime) {
		GlobalApp.openLastSearchTime = openLastSearchTime;
	}
	
	public static long timeDiff(long start, long end){
		long diff = end - start;
		return TimeUnit.MILLISECONDS.toMinutes(diff);
	}
}

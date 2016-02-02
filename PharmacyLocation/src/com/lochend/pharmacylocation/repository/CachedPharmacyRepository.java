package com.lochend.pharmacylocation.repository;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import GPSTracker.GPSTracker;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.lochend.location.GlobalApp;
import com.lochend.location.LocationUtils;
import com.lochend.pharmacy.cloud.OpeningTime;
import com.lochend.pharmacy.cloud.Pharmacy;
import com.lochend.pharmacylocation.entity.OpeningHour;
import com.lochend.pharmacylocation.entity.PharmacyBase;
import com.lochend.pharmacylocation.entity.PharmacyDetails;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;

public class CachedPharmacyRepository extends BaseRepository implements IPharmacyRepository {
	
	static final Comparator<PharmacyBase> DISTANCE_ORDER = 
            new Comparator<PharmacyBase>() {
		
		public int compare(PharmacyBase e1, PharmacyBase e2) {

			return e1.getDistanceInKm() < e2.getDistanceInKm() ? -1 : 1;
		}
	};

	
	public CachedPharmacyRepository(Context context, ProgressBar mProgressBar) {
		super(context, mProgressBar);
	}

	public void refreshDataFromCloud(final IPharmaciesListener _parentListener){
		datasource.deleteAllBusinessPoints();
		//datasource.deleteAllOpenBusinessPoints();
		datasource.deleteAllOpenningTime();
		
		GPSTracker gpsTracker = new GPSTracker();
		final Location currentLocation = gpsTracker.getLocation();
		IPharmaciesListener listener = new RefreshDataListener(_parentListener, currentLocation);
		getDataFromCloud(listener, currentLocation, false);
	}
	
	class RefreshDataListener implements IPharmaciesListener{
		
		IPharmaciesListener _parentListener;
		Location _currentLocation;
		public RefreshDataListener(final IPharmaciesListener _listener, Location currentLocation){
			_parentListener = _listener;
			_currentLocation = currentLocation;
		}

		@Override
		public void getPharmaciesCallback(ArrayList<PharmacyBase> pharmacyList) {
			// TODO Auto-generated method stub
			getOpeningTimeFromCloud(_parentListener, _currentLocation, null);
		}
		
	}
	
	
	@Override
	public void getAllNearestPharmacies(final IPharmaciesListener _listener) {
		// Turn it on
				if(_progressBar != null)
					_progressBar.setVisibility(ProgressBar.VISIBLE);
				try {
					mClient = new MobileServiceClient(
							"https://pharmacylocation.azure-mobile.net/",
							"cQmgDhBrYphWTrGjesEMWWKoGTdTUX37", _activity)
							.withFilter(new ProgressFilter(_activity, _progressBar));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				GPSTracker gpsTracker = new GPSTracker();
		    	final Location currentLocation = gpsTracker.getLocation();
		    	
		    	/*if(!GlobalApp.allPharmacyNeedRefresh(currentLocation, AppPreferencesRepository.getRadiusToInt())){
		    		if(_progressBar != null)
						_progressBar.setVisibility(ProgressBar.GONE);
		    		return;
		    	}*/
		    	
		    	ArrayList<PharmacyBase> cachedList = datasource.getAllPharmacies();
		    	GlobalApp.setLastKnownLocation(currentLocation);
		    	GlobalApp.setAllLastSearchRadius(AppPreferencesRepository.getRadiusToInt());
		    	
		    	if(cachedList.size() > 0){
		    		ArrayList<PharmacyBase> filteredList = filterByRadiusAndLocation(cachedList, currentLocation, AppPreferencesRepository.getRadiusToInt());
		    		ArrayList<PharmacyBase> sortedList = sortByDistance(filteredList);
		    		
		    		_listener.getPharmaciesCallback(sortedList);
		    		if(_progressBar != null)
						_progressBar.setVisibility(ProgressBar.GONE);
		    		return;
		    	}
		    	
				getDataFromCloud(_listener, currentLocation, true);
	}
	
	@Override
	public void getOpenOnlyNearestPharmacies(IPharmaciesListener _listener) {
		
			if(_progressBar.getVisibility() == ProgressBar.VISIBLE)
				return;
		
		// Turn it on
				if(_progressBar != null)
					_progressBar.setVisibility(ProgressBar.VISIBLE);
				try {
					mClient = new MobileServiceClient(
							"https://pharmacylocation.azure-mobile.net/",
							"cQmgDhBrYphWTrGjesEMWWKoGTdTUX37", _activity)
							.withFilter(new ProgressFilter(_activity, _progressBar));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				GPSTracker gpsTracker = new GPSTracker();
		    	final Location currentLocation = gpsTracker.getLocation();
		    	
		    	if(!GlobalApp.onlyOpenPharmacyNeedRefresh(currentLocation, AppPreferencesRepository.getRadiusToInt(), GlobalApp.getCurrentDayOfWeek())){
		    		if(_progressBar != null)
						_progressBar.setVisibility(ProgressBar.GONE);
		    		return;
		    	}
		    	
		    	ArrayList<PharmacyBase> cachedList = datasource.getAllPharmacies();
		    	GlobalApp.setLastKnownLocation(currentLocation);
		    	GlobalApp.setOpenLastSearchRadius(AppPreferencesRepository.getRadiusToInt());
		    	
		    	if(cachedList.size() > 0){
		    		ArrayList<PharmacyBase> filteredList = filterByRadiusAndLocation(cachedList, currentLocation, AppPreferencesRepository.getRadiusToInt());
		    		ArrayList<OpeningHour> times = datasource.getAllOpeningTimes();
		    		
		    		if(times.size() > 0){
		    			applyFiltersAndSortingCallback(_listener, currentLocation,
								filteredList, times);
		    			return;
		    		}
		    		getOpeningTimeFromCloud(_listener, currentLocation, filteredList);
		    	
		    		return;
		    	}
		    	
				getDataFromCloud(_listener, currentLocation, true);
	}

	/**
	 * @param _listener
	 * @param currentLocation
	 * @param filteredList
	 * @param times
	 */
	private void applyFiltersAndSortingCallback(IPharmaciesListener _listener,
			final Location currentLocation,
			ArrayList<PharmacyBase> filteredList, ArrayList<OpeningHour> times) {
		filteredList = filterByOpenOnly(filteredList, times, currentLocation, 
				AppPreferencesRepository.getRadiusToInt(), GlobalApp.getDayOfWeek(), GlobalApp.getCurrentTimeWithDate());
		ArrayList<PharmacyBase> sortedList = sortByDistance(filteredList);
		
		_listener.getPharmaciesCallback(sortedList);
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.GONE);
	}
	
	private ArrayList<PharmacyBase> filterByRadiusAndLocation(ArrayList<PharmacyBase> list, Location currentLocation, int radius){
		final ArrayList<PharmacyBase> filteredList = new ArrayList<PharmacyBase>();
		
		for(int i = 0; i < list.size(); i++){
			PharmacyBase pharmacyBase = list.get(i);
			double distance = LocationUtils.distance(currentLocation.getLatitude(), currentLocation.getLongitude(), 
					Double.parseDouble(pharmacyBase.getLat()), Double.parseDouble(pharmacyBase.getLng()));
			
			if(distance <= (radius / 1000)){
				filteredList.add(pharmacyBase);
				pharmacyBase.setDistance(distance);
			}
			
		}
		
		return filteredList;
	}
	
	private ArrayList<PharmacyBase> filterByOpenOnly(ArrayList<PharmacyBase> list, ArrayList<OpeningHour> times, 
			Location currentLocation, int radius, int dayOfWeek, String time){
		final ArrayList<PharmacyBase> filteredList = new ArrayList<PharmacyBase>();
		
		for(int i = 0; i < list.size(); i++){
			PharmacyBase pharmacyBase = list.get(i);
			OpeningHour dayTime = null;
			
			boolean found = false;
			for(int j = 0; j < times.size(); j++){
				if(times.get(j).get_day() == dayOfWeek && times.get(j).get_id().equals(Integer.toString(pharmacyBase.getId()))){
					found = true;
					dayTime = times.get(j);
				}
			}
			
			if(found){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				try {
					Date currentTime = sdf.parse(time);
					Date fromTime = sdf.parse(dayTime.get_from());
					Date toTime = sdf.parse(dayTime.get_to());
					
					if ( fromTime.before( currentTime ) && toTime.after(currentTime)) {
						filteredList.add(pharmacyBase);
				    }
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//if(time >= dayTime.get_from())
			}
		}
		
		return filteredList;
	}
	
	/**
	 * @param _listener
	 * @param currentLocation
	 */
	private void getDataFromCloud(final IPharmaciesListener _listener,final Location currentLocation,
			final boolean hideProgressBar) {
		mClient.invokeApi("getallpharmacies", null, "GET", null,
				new ApiJsonOperationCallback() {
					@Override
					public void onCompleted(JsonElement jsonElement,
							Exception exception,
							ServiceFilterResponse serviceFilterResponse) {
						GsonBuilder gsonb = new GsonBuilder();
						Gson gson = gsonb.create();
						final ArrayList<PharmacyBase> list = new ArrayList<PharmacyBase>();

						if (exception == null) {
							JsonArray array = jsonElement.getAsJsonArray();
							for (int i = 0; i < array.size(); i++) {
								Pharmacy item = gson.fromJson(array.get(i)
										.getAsJsonObject().toString(),
										Pharmacy.class);
								final PharmacyDetails pharmacy = new PharmacyDetails();
								pharmacy.setId(Integer.parseInt(item.getId()));
								pharmacy.setName(item.getName());
								pharmacy.setAddress(item.getAddress());
								pharmacy.setEmail(item.getEmail());
								pharmacy.setPhoneNumber(item.getPhoneNumber());
								pharmacy.setLat(item.getLat());
								pharmacy.setLng(item.getLong());
								pharmacy.setDistance(item.getDistanceInKilometers());
								list.add(pharmacy);
							}
							
							ArrayList<PharmacyBase> filteredList = null;
							
							//if(openOnly){
							//	filteredList = filterByOpenOnly(list, currentLocation, AppPreferencesRepository.getRadiusToInt(),
							//			GlobalApp.getDayOfWeek(), GlobalApp.getCurrentTime());
							//}else{
								filteredList = filterByRadiusAndLocation(list, currentLocation, AppPreferencesRepository.getRadiusToInt());
							//}
							
							_listener.getPharmaciesCallback(filteredList);
							datasource.addBusinessPoints(list);
							if(_progressBar != null && hideProgressBar)
								_progressBar.setVisibility(ProgressBar.GONE);
						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});
	}
	
	private void getOpeningTimeFromCloud(final IPharmaciesListener _listener,
			final Location currentLocation,
			final ArrayList<PharmacyBase> filteredList) {
		
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.VISIBLE);
		
		mClient.invokeApi("getallopeningtime", null, "GET", null,
				new ApiJsonOperationCallback() {
					@Override
					public void onCompleted(JsonElement jsonElement,
							Exception exception,
							ServiceFilterResponse serviceFilterResponse) {
						GsonBuilder gsonb = new GsonBuilder();
						Gson gson = gsonb.create();
						final ArrayList<OpeningHour> list = new ArrayList<OpeningHour>();

						if (exception == null) {
							JsonArray array = jsonElement.getAsJsonArray();
							for (int i = 0; i < array.size(); i++) {
								OpeningTime item = gson.fromJson(array.get(i)
										.getAsJsonObject().toString(),
										OpeningTime.class);
								final OpeningHour time = new OpeningHour();
								time.set_id(item.getId());
								time.set_from(item.getStartTime());
								time.set_to(item.getEndTime());
								time.set_day(item.getDay());
								list.add(time);
							}
							
							datasource.addOpeningTime(list);
							if(filteredList != null){
								applyFiltersAndSortingCallback(_listener, currentLocation, filteredList, list);
							}
							
						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});
	}
	
	private ArrayList<PharmacyBase> sortByDistance(ArrayList<PharmacyBase> list){
		final ArrayList<PharmacyBase> sortedList = new ArrayList<PharmacyBase>();
		
		Collections.sort(list, DISTANCE_ORDER);
		
		return list;
	}

	@Override
	public void searchPharmacies(IPharmaciesListener _listener,
			String searchText) {
		
		final ArrayList<PharmacyBase> list = new ArrayList<PharmacyBase>();
		ArrayList<PharmacyBase> cachedList = datasource.getAllPharmacies();
		
		for(int i = 0; i < cachedList.size(); i++){
			PharmacyBase pharmacyBase = cachedList.get(i);
			if(pharmacyBase.getAddress() == null || pharmacyBase.getName() == null)
				continue;
			
			if(pharmacyBase.getAddress().toLowerCase().contains(searchText.toLowerCase())
					|| pharmacyBase.getName().toLowerCase().contains(searchText.toLowerCase())){
					//|| pharmacyBase.getRegistrationNumber().contains(searchText)){
				list.add(pharmacyBase);
			}
		}
		
		_listener.getPharmaciesCallback(list);
		
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.GONE);
	}
	
	@Override
	public void getPharmacyOpeningHours(int pharmacyId,
			IPharmacyOpeningHourListener _openingHourListener) {
		
		List<OpeningHour> openingHourList = new ArrayList<OpeningHour>();
		
		// Turn it on
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.VISIBLE);
		
		ArrayList<OpeningHour> times = datasource.getAllOpeningTimes();
		
		openingHourList = findOpeningTimesByPharmacyId(times, pharmacyId);
		
		_openingHourListener.getPharmacyOpeningHoursCallback(openingHourList);
		
		// Turn it off
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.GONE);
		
	}
	
	private List<OpeningHour> findOpeningTimesByPharmacyId(ArrayList<OpeningHour> times, int id){
		
		final List<OpeningHour> openingHourList = new ArrayList<OpeningHour>();
		int daysCount = 1;
		for(int j = 0; j < times.size() && daysCount <= 7; j++){
			if(times.get(j).get_id().equals(Integer.toString(id))){
				openingHourList.add(times.get(j));
				daysCount++;
			}
		}
		
		return openingHourList;
	}

}

package com.lochend.pharmacylocation.repository;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import GPSTracker.GPSTracker;
import android.location.Location;
import android.util.Pair;
import android.widget.ProgressBar;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.lochend.location.GlobalApp;
import com.lochend.pharmacy.cloud.OpeningTime;
import com.lochend.pharmacy.cloud.Pharmacy;
import com.lochend.pharmacylocation.entity.OpeningHour;
import com.lochend.pharmacylocation.entity.PharmacyBase;
import com.lochend.pharmacylocation.entity.PharmacyDetails;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class PharmacyRepository extends BaseRepository implements IPharmacyRepository {

	public PharmacyRepository(Context context, ProgressBar mProgressBar)  {
		super(context, mProgressBar);
	}

	/* (non-Javadoc)
	 * @see com.lochend.pharmacylocation.repository.IPharmacyRepository#getAllPharmacies(com.lochend.pharmacylocation.repository.IPharmaciesListener)
	 */
	/*@Override
	public void getAllPharmacies(final IPharmaciesListener _listener) {
		// Turn it on
		if(_progressBar != null)
			_progressBar.setVisibility(ProgressBar.VISIBLE);
		try {
			mClient = new MobileServiceClient(
					"https://pharmacylocation.azure-mobile.net/",
					"cQmgDhBrYphWTrGjesEMWWKoGTdTUX37", _activity)
					.withFilter(new ProgressFilter());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mPharmacyTable = mClient.getTable("vPharmacy", Pharmacy.class);

		// setProgressBarIndeterminateVisibility(true);
		// Get the items that weren't marked as completed and add them in the
		// adapter
		mPharmacyTable.execute(new TableQueryCallback<Pharmacy>() {

			public void onCompleted(List<Pharmacy> result, int count,
					Exception exception, ServiceFilterResponse response) {
				final ArrayList<PharmacyBase> list = new ArrayList<PharmacyBase>();

				if (exception == null) {
					for (Pharmacy item : result) {
						final PharmacyDetails pharmacy = new PharmacyDetails();
						pharmacy.setName(item.getName());
						pharmacy.setAddress(item.getAddress());
						pharmacy.setEmail(item.getEmail());
						pharmacy.setPhoneNumber(item.getPhoneNumber());
						pharmacy.setLat(item.getLat());
						pharmacy.setLng(item.getLong());
						list.add(pharmacy);
						// mAdapter.add(item);
					}
					_listener.getPharmaciesCallback(list);
					
					// Turn it off
					// setProgressBarIndeterminateVisibility(false);
					if(_progressBar != null)
						_progressBar.setVisibility(ProgressBar.GONE);

				} else {
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}*/

	/* (non-Javadoc)
	 * @see com.lochend.pharmacylocation.repository.IPharmacyRepository#getAllNearestPharmacies(com.lochend.pharmacylocation.repository.IPharmaciesListener)
	 */
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
    	
    	if(!GlobalApp.allPharmacyNeedRefresh(currentLocation, AppPreferencesRepository.getRadiusToInt())){
    		_listener.getPharmaciesCallback(datasource.getAllPharmacies());
    		if(_progressBar != null)
				_progressBar.setVisibility(ProgressBar.GONE);
    		return;
    	}

		ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("longitude", String.valueOf(currentLocation.getLongitude()))); //"-6.332655"));
		parameters.add(new Pair<String, String>("latitude", String.valueOf(currentLocation.getLatitude()))); //"53.370961"));
		parameters.add(new Pair<String, String>("distance", String.valueOf(AppPreferencesRepository.getRadiusToInt())));

		mClient.invokeApi("getallnearestpharmacies", null, "GET", parameters,
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
							GlobalApp.setLastKnownLocation(currentLocation);
							GlobalApp.setAllLastSearchRadius(AppPreferencesRepository.getRadiusToInt());
							GlobalApp.setAllPharmaciesInCache(true);
							_listener.getPharmaciesCallback(list);
							datasource.addBusinessPoints(list);
							if(_progressBar != null)
								_progressBar.setVisibility(ProgressBar.GONE);
						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});
	}
	
	/* (non-Javadoc)
	 * @see com.lochend.pharmacylocation.repository.IPharmacyRepository#searchPharmacies(com.lochend.pharmacylocation.repository.IPharmaciesListener, java.lang.String)
	 */
	@Override
	public void searchPharmacies(final IPharmaciesListener _listener, String searchText) {
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
    	Location currentLocation = gpsTracker.getLocation();

		ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("longitude", String.valueOf(currentLocation.getLongitude()))); //"-6.332655"));
		parameters.add(new Pair<String, String>("latitude", String.valueOf(currentLocation.getLatitude()))); //"53.370961"));
		parameters.add(new Pair<String, String>("searchText", "%" + searchText + "%"));

		mClient.invokeApi("searchpharmacies", null, "GET", parameters,
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
							_listener.getPharmaciesCallback(list);
							if(_progressBar != null)
								_progressBar.setVisibility(ProgressBar.GONE);
						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});
	}
	
	@Override
	public void getPharmacyOpeningHours(int pharmacyId, final IPharmacyOpeningHourListener _openingHourListener) {
		
		final List<OpeningHour> openingHourList = new ArrayList<OpeningHour>();
		
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

		MobileServiceTable<OpeningTime> openingTable = mClient.getTable("vOpeningTime", OpeningTime.class);

		openingTable.where().field("Id").eq(String.valueOf(pharmacyId))
		.execute(new TableQueryCallback<OpeningTime>() {

			@Override
			public void onCompleted(List<OpeningTime> items, int arg1,
					Exception exception, ServiceFilterResponse arg3) {
				
				if (exception == null) {
					for (OpeningTime item : items) {
						final OpeningHour pharmacy = new OpeningHour();
						pharmacy.set_day(item.getDay());
						pharmacy.set_from(item.getStartTime());
						pharmacy.set_to(item.getEndTime());
						openingHourList.add(pharmacy);
					}
					_openingHourListener.getPharmacyOpeningHoursCallback(openingHourList);
					
					// Turn it off
					if(_progressBar != null)
						_progressBar.setVisibility(ProgressBar.GONE);

				} else {
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.lochend.pharmacylocation.repository.IPharmacyRepository#getOpenOnlyNearestPharmacies(com.lochend.pharmacylocation.repository.IPharmaciesListener)
	 */
	@Override
	public void getOpenOnlyNearestPharmacies(final IPharmaciesListener _listener) {
		
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
    	
    	if(!GlobalApp.onlyOpenPharmacyNeedRefresh(currentLocation, AppPreferencesRepository.getRadiusToInt(), GlobalApp.getDayOfWeek())){
    		_listener.getPharmaciesCallback(datasource.getOpenPharmacies());
    		if(_progressBar != null)
				_progressBar.setVisibility(ProgressBar.GONE);
    		return;
    	}

		ArrayList<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
		parameters.add(new Pair<String, String>("longitude", String.valueOf(currentLocation.getLongitude()))); //"-6.332655"));
		parameters.add(new Pair<String, String>("latitude", String.valueOf(currentLocation.getLatitude()))); //"53.370961"));
		parameters.add(new Pair<String, String>("distance", String.valueOf(AppPreferencesRepository.getRadiusToInt())));
		
		parameters.add(new Pair<String, String>("dayOfWeek", String.valueOf(GlobalApp.getDayOfWeek())));
		parameters.add(new Pair<String, String>("currentTime", GlobalApp.getCurrentTime()));

		mClient.invokeApi("getopenonlynearestpharmacies", null, "GET", parameters,
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
								pharmacy.setName(item.getName().trim());
								pharmacy.setAddress(item.getAddress());
								pharmacy.setEmail(item.getEmail());
								pharmacy.setPhoneNumber(item.getPhoneNumber());
								pharmacy.setLat(item.getLat());
								pharmacy.setLng(item.getLong());
								pharmacy.setDistance(item.getDistanceInKilometers());
								list.add(pharmacy);
							}
							_listener.getPharmaciesCallback(list);
							GlobalApp.setLastKnownLocation(currentLocation);
							GlobalApp.setOpenLastSearchRadius(AppPreferencesRepository.getRadiusToInt());
							datasource.addOpenBusinessPoints(list);
							GlobalApp.setOpenPharmaciesInCache(true);
							GlobalApp.setLastDayOfWeekInCache(GlobalApp.getDayOfWeek());
							GlobalApp.setOpenLastSearchTime(new Date().getTime());
							if(_progressBar != null)
								_progressBar.setVisibility(ProgressBar.GONE);
						} else {
							createAndShowDialog(exception, "Error");
						}
					}
				});
	}

	public class PharmacyResult {

		@SerializedName("results")
		public List<Pharmacy> result;
	}
}

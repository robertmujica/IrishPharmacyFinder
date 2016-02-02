package com.lochend.pharmacylocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lochend.location.GlobalApp;
import com.lochend.location.LocationUtils;
import com.lochend.pharmacylocation.entity.PharmacyBase;
import com.lochend.pharmacylocation.entity.PharmacyDetails;
import com.lochend.pharmacylocation.repository.AppPreferencesRepository;
import com.lochend.pharmacylocation.repository.IPharmaciesListener;
import com.lochend.pharmacylocation.repository.IPharmacyRepository;
import com.lochend.pharmacylocation.repository.PharmacyRepository;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		IPharmaciesListener,
		OnMarkerClickListener,
		View.OnTouchListener,
		GoogleMap.OnInfoWindowClickListener,
		IPharmacyQueryActivity{
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	Marker locationMarker;
	private Marker marker;
	Location currentLocation;
	private CharSequence mTitle;
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;
	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;
	private TextView mAddress;
	private ProgressBar mActivityIndicator;
	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;
	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;
	boolean mUpdatesRequested = false;
	private IPharmacyRepository _repository;
	private GoogleMap mMap;
	private int _xDelta;
	private int _yDelta;
	private ViewGroup _root;
	CustomDrawerAdapter adapter;
	int currentRadius = 0;
	ActionBar bar;
	boolean isLoading = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// mMap.setMyLocationEnabled(true);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		_root = (ViewGroup)findViewById(R.id.root);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		mActivityIndicator = (ProgressBar) findViewById(R.id.progressBar);
		mActivityIndicator.setVisibility(ProgressBar.GONE);
		mActivityIndicator.getIndeterminateDrawable().setColorFilter(Color.argb(105, 105, 105, 105), android.graphics.PorterDuff.Mode.MULTIPLY);

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();
		 
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Note that location updates are off until the user turns them on
		mUpdatesRequested = true;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();
		 
		mLocationClient = new LocationClient(this, this, this);
		GlobalApp.setCurrentPharmacyList(new ArrayList<PharmacyDetails>());
		_repository = new PharmacyRepository(this, mActivityIndicator);
		addNavigationDrawer();
		GlobalApp.setCurrentActivity(this);
	}
	
	private void addNavigationDrawer(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        getResources().getStringArray(R.array.operating_systems);
        mTitle = getTitle();
        
        List<DrawerItem> dataList;
        dataList = new ArrayList<DrawerItem>();
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
        
        dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.preference_search_radius), R.drawable.ic_action_search, true, false)); // adding a header to the list
        String[] radius = GlobalApp.getResources().getStringArray(com.lochend.pharmacylocation.R.array.radius_list);
        
        for(int i = 0; i < radius.length; i++){
        	if(AppPreferencesRepository.getRadius().equals(radius[i]))
        		currentRadius = i + 1;
        	dataList.add(new DrawerItem(radius[i],0, true, false));
        }
        
        dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.action_settings), R.drawable.ic_action_settings, false, true));
		dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.about_title), R.drawable.ic_action_about, false, true));
	
        adapter = new CustomDrawerAdapter(this, R.layout.drawer_item,
				dataList);
        mDrawerList.setAdapter(adapter);
        
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this, dataList, mDrawerLayout, mDrawerList));
        
        mDrawerToggle = new ActionBarDrawerToggle(this,                   
                mDrawerLayout,          
                R.drawable.ic_drawer,   
                R.string.drawer_open,   
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(GlobalApp.getResources().getString(R.string.drawer_open));
            }
        };
        
     // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		final ActionBar bar = getActionBar();
		
		bar.setTitle("Pharmacies");
		if(AppPreferencesRepository.ALL_RADIUS_INDEX != AppPreferencesRepository.getRadiusIndex()){
			bar.setSubtitle("in " + AppPreferencesRepository.getRadius());
		}
		
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if(GlobalApp.getCurrentTabIndexClassName().equals("MainActivity"))
					if(!isLoading){
						GlobalApp.setCurrentTabIndex("MainActivity", tab.getPosition());
					}
					
				refresh();
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				
			}
		};

		bar.addTab(bar.newTab().setText(getResources().getString(R.string.all)).setTabListener(tabListener));

		bar.addTab(bar.newTab().setText(getResources().getString(R.string.onlyOpen))
				.setTabListener(tabListener));
		
		bar.selectTab(bar.getTabAt(GlobalApp.getCurrentTabIndex()));
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        
		Log.v("LocationActivity", "onOptionsItemSelected");
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			String parentName = NavUtils.getParentActivityName(this);
			if (parentName != null)
				NavUtils.navigateUpFromSameTask(this);
			else
				super.finish();
			return true;
		case R.id.newLoction:
			this.stopUpdates(null);
			GlobalApp.setCurrentTabIndexClassName("PharmacyListActivity");
			// Passing -1 as position to indicate it is a new Locale
			// LocaleListFragment.showDetails(this, -1, mDualPane);
			Intent intentList = new Intent(this.getApplicationContext(),
					PharmacyListActivity.class);
			startActivity(intentList);
			this.finish();
			return true;
		case R.id.action_settings:
			Intent intentLoc = new Intent(this.getApplicationContext(),
					AppPreferencesActivity.class);
			startActivity(intentLoc);
			return true;
		case R.id.refresh:
			_repository.refreshDataFromCloud(this);
			return true;
		default:
			return false;
		}
	}
	 
	@Override
	public void onStop() {

		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();

		super.onStop();
	}
	 
	@Override
	public void onPause() {

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED,
				mUpdatesRequested);
		mEditor.commit();

		super.onPause();
	}


	@Override
	public void onStart() {

		super.onStart();
		 
		mLocationClient.connect();

	}

	
	 //Called when the system detects that this Activity is now visible.
	 
	@Override
	public void onResume() {
		super.onResume();
		
		mDrawerLayout.closeDrawer(mDrawerList);
		mDrawerList.setItemChecked(AppPreferencesRepository.getRadiusIndex(), true);

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(
					LocationUtils.KEY_UPDATES_REQUESTED, true);
			mUpdatesRequested = true;

			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}

	}

	
	 /* Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed()
	 * in LocationUpdateRemover and LocationUpdateRequester may call
	 * startResolutionForResult() to start an Activity that handles Google Play
	 * services problems. The result of this call returns here, to
	 * onActivityResult.
	 * */
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:
				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
				break;

			// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));
				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(LocationUtils.APPTAG,
					getString(R.string.unknown_activity_request_code,
							requestCode));

			break;
		}
	}

	/*
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getSupportFragmentManager(),
						LocationUtils.APPTAG);
			}
			return false;
		}
	}

	/*
	 * Invoked by the "Get Location" button.
	 * 
	 * Calls getLastLocation() to get the current location
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void getLocation() {

		// If Google Play Services is available
		if (servicesConnected()) {

			if(locationMarker != null){
				locationMarker.remove();
			}
			
			// Get the current location
			currentLocation = mLocationClient.getLastLocation();
			
			LatLng currentLotLong = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			
			locationMarker = mMap.addMarker(new MarkerOptions().position(
					currentLotLong).title("MyLocation"));

			mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLotLong));
			mMap.animateCamera(CameraUpdateFactory.zoomTo((float)11.5), 100,
					MyCancelableCallback);
		}
	}

	/*
	 * Invoked by the "Get Address" button. Get the address of the current
	 * location, using reverse geocoding. This only works if a geocoding service
	 * is available.
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	// For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
	@SuppressLint("NewApi")
	public void getAddress(View v) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a
		// geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(this, R.string.no_geocoder_available,
					Toast.LENGTH_LONG).show();
			return;
		}

		if (servicesConnected()) {

			// Get the current location
			currentLocation = mLocationClient.getLastLocation();

			// Turn the indefinite activity indicator on
			mActivityIndicator.setVisibility(View.VISIBLE);

			// Start the background task
			(new MainActivity.GetAddressTask(this)).execute(currentLocation);
		}
	}

	/*
	 * Invoked by the "Start Updates" button Sends a request to start location
	 * updates
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void startUpdates(View v) {
		mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/*
	 * Invoked by the "Stop Updates" button Sends a request to remove location
	 * updates request them.
	 * 
	 * @param v
	 *            The view object associated with this method, in this case a
	 *            Button.
	 */
	public void stopUpdates(View v) {
		mUpdatesRequested = false;

		if (servicesConnected()) {
			stopPeriodicUpdates();
		}
	}

	
	 /* Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 * */
	 
	@Override
	public void onConnected(Bundle bundle) {
		//mConnectionStatus.setText(R.string.connected);

		if (mUpdatesRequested) {
			getLocation();
			startPeriodicUpdates();
		}
	}

	
	 /* Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	 
	@Override
	public void onDisconnected() {

	}

	
	//Called by Location Services if the attempt to Location Services fails.
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		 /* Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		 
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				
				 /* Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
				 

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	CancelableCallback MyCancelableCallback = new CancelableCallback() {

		@Override
		public void onCancel() {
		}

		@Override
		public void onFinish() {
		}
	};

	@Override
	public void onLocationChanged(Location location) {
		
		if(isLoading || !GlobalApp.allPharmacyNeedRefresh(location, AppPreferencesRepository.getRadiusToInt())){
			return;
		}
		
		currentLocation = location;
		
			LatLng currentLotLong = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
			mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLotLong));
			mMap.animateCamera(CameraUpdateFactory.zoomTo((float)11.5), 100,
					MyCancelableCallback);
		
		refresh();
	}

	/*
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/*
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
	}

	/*
	 * An AsyncTask that calls getFromLocation() in the background. The class
	 * uses the following generic types: Location - A
	 * {@link android.location.Location} object containing the current location,
	 * passed as the input parameter to doInBackground() Void - indicates that
	 * progress units are not used by this subclass String - An address passed
	 * to onPostExecute()
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it,
		 * format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			
			 /* Get a new geocoding service instance, set for localized
			 * addresses. This example uses android.location.Geocoder, but other
			 * geocoders that conform to address standards can also be used.
			 */
			 
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or
			// network problems.
			try {

				
				 /* Call the synchronous getFromLocation() method with the
				 * latitude and longitude of the current location. Return at
				 * most 1 address.
				 */
				 
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG,
						getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(
						R.string.illegal_argument_exception,
						location.getLatitude(), location.getLongitude());
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);

				// Format the first line of address
				String addressText = getString(
						R.string.address_output_string,

						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",

						// Locality is usually a city
						address.getLocality(),

						// The country of the address
						address.getCountryName());

				// Return the text
				return addressText;

				// If there aren't any addresses, post a message
			} else {
				return getString(R.string.no_address_found);
			}
		}

		/*
		 * A method that's called once doInBackground() completes. Set the text
		 * of the UI element that displays the address. This method runs on the
		 * UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {

			// Turn off the progress bar
			mActivityIndicator.setVisibility(View.GONE);

			// Set the address in the UI
			mAddress.setText(address);
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(),
					LocationUtils.APPTAG);
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// This method must return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	public void getPharmaciesCallback(ArrayList<PharmacyBase> pharmacyList) {
		
		for (PharmacyBase item : pharmacyList) {
			PharmacyDetails pharmacyItem = (PharmacyDetails)item;
			
			LatLng location = new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng()));
			Marker pharmacyMarker = mMap.addMarker(new MarkerOptions()
				.position(location)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pharmacy))
				.title(item.getName() + " (" + pharmacyItem.getDistance() + ")")
				.snippet(item.getAddress()));
		 	pharmacyItem.setMarkerId(pharmacyMarker.getId());
		 	GlobalApp.AddToCurrentPharmacyList(pharmacyItem);
		}
		
		setNumberOfPharmaciesFound(pharmacyList);
		isLoading = false;
	}

	private void setNumberOfPharmaciesFound(ArrayList<PharmacyBase> pharmacyList) {
		if(getActionBar() != null){
			Tab currentTab = getActionBar().getSelectedTab();
			String tabText = "";
			
			if(currentTab.getPosition() != GlobalApp.getCurrentTabIndex())
				return;
			
			if(GlobalApp.getCurrentTabIndex() == 0){
				tabText = getResources().getString(R.string.all);
			}
			else{
				tabText = getResources().getString(R.string.onlyOpen);
			}
			
			currentTab.setText(tabText + " (" + pharmacyList.size()  + ")");
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {	
		return false;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		final int X = (int) event.getRawX();
	    final int Y = (int) event.getRawY();
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            _xDelta = X - lParams.leftMargin;
	            _yDelta = Y - lParams.topMargin;
	            break;
	        case MotionEvent.ACTION_UP:
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	            break;
	        case MotionEvent.ACTION_POINTER_UP:
	            break;
	        case MotionEvent.ACTION_MOVE:
	            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
	            layoutParams.leftMargin = X - _xDelta;
	            layoutParams.topMargin = Y - _yDelta;
	            layoutParams.rightMargin = 10; //-250;
	            layoutParams.bottomMargin = 10; //-250;
	            
	            view.setLayoutParams(layoutParams);
	            break;
	    }
	    _root.invalidate();
	    return true;
	}
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {
		 
        private View view;
 
        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
        }
 
        @Override
        public View getInfoContents(Marker marker) {
 
            if (MainActivity.this.marker != null
                    && MainActivity.this.marker.isInfoWindowShown()) {
                MainActivity.this.marker.hideInfoWindow();
                MainActivity.this.marker.showInfoWindow();
            }
            return null;
        }
 
        @Override
        public View getInfoWindow(final Marker marker) {
            MainActivity.this.marker = marker;
 
            if(marker.getId().equals(locationMarker.getId()))
            	return null;
            
            final ImageView image = ((ImageView) view.findViewById(R.id.badge));
 
            image.setImageResource(R.drawable.ic_pharmacy);
 
            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }
 
            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }
 
            return view;
        }
    }
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if(!marker.getId().equals(locationMarker.getId())){
			Intent intent = new Intent(this, PharmacyDetailsActivity.class);
		    GlobalApp.setCurrentPharmacyByMarkerId(marker.getId());
		    startActivity(intent);
		}
	}

	@Override
	public void refresh() {
		if(locationMarker != null){
			locationMarker.remove();
		}
		mMap.clear();
		
		if(currentLocation != null){
			LatLng currentLatLong = new LatLng(currentLocation.getLatitude(),
				currentLocation.getLongitude());

			locationMarker = mMap.addMarker(new MarkerOptions().position(
	    		currentLatLong).title("MyLocation"));
		}
		
	    if(GlobalApp.getCurrentTabIndex() == 0)
	    		_repository.getAllNearestPharmacies(this);
	    else
	    	_repository.getOpenOnlyNearestPharmacies(this);
	    
	    if(getActionBar() != null){
	    	if(AppPreferencesRepository.ALL_RADIUS_INDEX != AppPreferencesRepository.getRadiusIndex()){
	    		getActionBar().setSubtitle("in " + AppPreferencesRepository.getRadius());
			}
	    	else{
	    		getActionBar().setSubtitle("");
	    	}
	    	
	    	GlobalApp.setCurrentTabIndexClassName("MainActivity");
	    }
	}
}
package com.lochend.pharmacylocation;

import GPSTracker.GPSTracker;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class OnNavigateListener implements OnClickListener {

	private Activity _activity;
	
	public OnNavigateListener(Activity container){
		_activity = container;
	}
	
	@Override
	public void onClick(View v)
    {   
    	GPSTracker gpsTracker = new GPSTracker();
    	Location currentLocation = gpsTracker.getLocation();
    	
    	String pharmacyName = (String) v.getTag(R.id.pharmacyName);
    	String latFrom = String.valueOf(currentLocation.getLatitude());
    	String longFrom = String.valueOf(currentLocation.getLongitude());
    	
    	String latTo = (String) v.getTag(R.id.latitude);
    	String longTo = (String) v.getTag(R.id.longitude);
    	
    	Uri routeUri = Uri.parse("http://maps.google.com/maps?&saddr=" +
        latFrom + "," +
        longFrom + " ( " + _activity.getResources().getString(R.string.myLocation) + " )" + "&daddr=" + latTo + "," + longTo + " (" + pharmacyName + ") ");

    	Intent i = new Intent(Intent.ACTION_VIEW, routeUri);
    	_activity.startActivity(i);
    	
    	//String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLatitude, destinationLongitude, "Where the party is at");
    	//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    	//intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
    	//startActivity(intent);
    } 

}

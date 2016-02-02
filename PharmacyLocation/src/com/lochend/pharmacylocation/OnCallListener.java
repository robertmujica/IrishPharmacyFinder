package com.lochend.pharmacylocation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

public class OnCallListener implements OnClickListener {

	private Activity _activity;
	
	public OnCallListener(Activity container){
		_activity = container;
	}
	
	@Override
	public void onClick(View v)
    {   
    	String number = (String) v.getTag(R.id.phoneNumber);
    	Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+ Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _activity.startActivity(callIntent);
    } 

}

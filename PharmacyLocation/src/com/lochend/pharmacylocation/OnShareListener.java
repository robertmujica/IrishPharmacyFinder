package com.lochend.pharmacylocation;

import com.lochend.pharmacylocation.entity.PharmacyDetails;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnShareListener implements OnClickListener {

	private Activity _activity;
	private PharmacyDetails _pharmacy;
	
	public OnShareListener(Activity container, PharmacyDetails pharmacy){
		_activity = container;
		_pharmacy = pharmacy;
	}
	
	@Override
	public void onClick(View v) {
		
		Intent intent= new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		// Add data to the intent, the receiving app will decide what to do with it.
		intent.putExtra(Intent.EXTRA_SUBJECT, _pharmacy.getName());
		intent.putExtra(Intent.EXTRA_TEXT, _pharmacy.getAddress() + " " + _pharmacy.getPhoneNumber());
		
		_activity.startActivity(intent);
		
	}

}

package com.lochend.pharmacylocation.repository;

import android.R;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.lochend.location.GlobalApp;
import com.lochend.pharmacylocation.ApplicationContextProvider;
import com.lochend.pharmacylocation.DrawerItem;

public class AppPreferencesRepository {
	
	public static int ALL_RADIUS_INDEX = 1; 
	static SharedPreferences sp;
	
	static {
		sp = PreferenceManager.getDefaultSharedPreferences(ApplicationContextProvider.getContext());
	}
	
	public static String getRadius(){
		return sp.getString("pref_pharmacy_search_radius","3 km");
	}
	
	public static void setRadius(String radius){
		SharedPreferences.Editor editor1 = sp.edit();
		  editor1.putString("pref_pharmacy_search_radius",radius);
		  editor1.commit();
	}
	
	public static int getRadiusToInt(){
		String value = getRadius();
		int radiusIndex = getRadiusIndex();
		
		if(radiusIndex == ALL_RADIUS_INDEX){
			return 1000000; // big number to query all pharmacies in database no matter the distance
		}else{
			String[] values = value.split(" ");
			return Integer.parseInt(values[0]) * 1000;
		}
	}
	
	public static String getMode(){
		return sp.getString("pref_pharmacy_open_mode","All");
	}
	
	public static String[] getRadiusList(){
		Resources res = GlobalApp.getResources();
		return res.getStringArray(com.lochend.pharmacylocation.R.array.entryvalues_radius_list);
	}
	
	public static String[] getRadiusTitleList(){
		Resources res = GlobalApp.getResources();
		return res.getStringArray(com.lochend.pharmacylocation.R.array.radius_list);
	}

	public static int getRadiusIndex(){
		int index = 0;
		String[] radius = getRadiusTitleList();
		
		for(int i = 0; i < radius.length; i++){
        	if(getRadius().equals(radius[i])){
        		index = i;
        		break;
        	}	
        }
		
		return index + 1;
	}
}

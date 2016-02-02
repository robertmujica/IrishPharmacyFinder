package com.lochend.pharmacylocation.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.lochend.pharmacylocation.entity.*;

public class BusinessPointDataSource {

	// Database fields
	private SQLiteDatabase database;
	private BusinessPointSQLiteHelper dbHelper;
	private String[] allColumns = { BusinessPointSQLiteHelper.COLUMN_ID,
			BusinessPointSQLiteHelper.NAME, BusinessPointSQLiteHelper.ADDRESS, BusinessPointSQLiteHelper.EMAIL,
			BusinessPointSQLiteHelper.PHONE_NUMBER, BusinessPointSQLiteHelper.LAT, BusinessPointSQLiteHelper.LNG,
			BusinessPointSQLiteHelper.DISTANCE};
	private Lock lock = new ReentrantLock();
	
	private String[] openningTimeColumns = { BusinessPointSQLiteHelper.COLUMN_ID, BusinessPointSQLiteHelper.START_TIME, 
			BusinessPointSQLiteHelper.END_TIME, BusinessPointSQLiteHelper.DAY};

	public BusinessPointDataSource(Context context) {
		dbHelper = new BusinessPointSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addBusinessPoints(List<PharmacyBase> pharmacies){
		deleteAllBusinessPoints();
		for (int i = 0; i < pharmacies.size(); i++) {
			createPharmacy((PharmacyDetails)pharmacies.get(i), BusinessPointSQLiteHelper.TABLE_BUSINESS_POINT);
		}
	}
	
	public void addOpenBusinessPoints(List<PharmacyBase> pharmacies){
		deleteAllOpenBusinessPoints();
		for (int i = 0; i < pharmacies.size(); i++) {
			createPharmacy((PharmacyDetails)pharmacies.get(i), BusinessPointSQLiteHelper.TABLE_OPEN_BUSINESS_POINT);
		}
	}
	
	public void addOpeningTime(final List<OpeningHour> openingTimes){
		/*deleteAllOpenningTime();
		for (int i = 0; i < openingTimes.size(); i++) {
			createOpeningTime((OpeningHour)openingTimes.get(i), BusinessPointSQLiteHelper.TABLE_OPENING_TIME);
		}*/
		  
		  Runnable r = new Runnable() {
		         public void run() {
		        	 lock.lock();
				    	deleteAllOpenningTime();
						for (int i = 0; i < openingTimes.size(); i++) {
							createOpeningTime((OpeningHour)openingTimes.get(i), BusinessPointSQLiteHelper.TABLE_OPENING_TIME);
						}
						lock.unlock();
		         }
		     };

		     new Thread(r).start();
	}
	
	private void createPharmacy(PharmacyDetails pharmacy, String tableName) {
	    ContentValues values = new ContentValues();
	    values.put(BusinessPointSQLiteHelper.COLUMN_ID, pharmacy.getId());
	    values.put(BusinessPointSQLiteHelper.NAME, pharmacy.getName());
	    values.put(BusinessPointSQLiteHelper.ADDRESS, pharmacy.getAddress());
	    values.put(BusinessPointSQLiteHelper.EMAIL, pharmacy.getEmail());
	    values.put(BusinessPointSQLiteHelper.PHONE_NUMBER, pharmacy.getPhoneNumber());
	    values.put(BusinessPointSQLiteHelper.LAT, pharmacy.getLat());
	    values.put(BusinessPointSQLiteHelper.LNG, pharmacy.getLng());
	    values.put(BusinessPointSQLiteHelper.DISTANCE, pharmacy.getDistance());
	    database.insert(tableName, null, values);
	    System.out.println("BusinessPoint : " + pharmacy.getRegistrationNumber());
	  }
	
	private void createOpeningTime(OpeningHour time, String tableName) {
	    ContentValues values = new ContentValues();
	    values.put(BusinessPointSQLiteHelper.COLUMN_ID, time.get_id());
	    values.put(BusinessPointSQLiteHelper.START_TIME, time.get_from());
	    values.put(BusinessPointSQLiteHelper.END_TIME, time.get_to());
	    values.put(BusinessPointSQLiteHelper.DAY, time.get_day());
	    
	    database.insert(tableName, null, values);
	    System.out.println("OpeningTime : " + time.get_id());
	  }
	
	public void deleteAllBusinessPoints() {
	    System.out.println("all businessPoints deleted");
	    database.delete(BusinessPointSQLiteHelper.TABLE_BUSINESS_POINT,null, null);
	  }
	
	public void deleteAllOpenningTime() {
	    System.out.println("all openingTime deleted");
	    database.delete(BusinessPointSQLiteHelper.TABLE_OPENING_TIME,null, null);
	  }
	
	public void deleteAllOpenBusinessPoints() {
	    System.out.println("all businessPoints deleted");
	    database.delete(BusinessPointSQLiteHelper.TABLE_OPEN_BUSINESS_POINT,null, null);
	  }

	public ArrayList<PharmacyBase> getAllPharmacies() {
		return getPharmacies(BusinessPointSQLiteHelper.TABLE_BUSINESS_POINT);
	}

	public ArrayList<PharmacyBase> getOpenPharmacies() {
		return getPharmacies(BusinessPointSQLiteHelper.TABLE_OPEN_BUSINESS_POINT);
	}

	public ArrayList<OpeningHour> getAllOpeningTimes() {
		ArrayList<OpeningHour> list = new ArrayList<OpeningHour>();
		lock.lock();
		Cursor cursor = database.query(BusinessPointSQLiteHelper.TABLE_OPENING_TIME, openningTimeColumns, null, null, null, null, "id ASC");
		lock.unlock();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			OpeningHour time = cursorToOpeningTime(cursor);
			list.add(time);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		
		return list;
	}

	private ArrayList<PharmacyBase> getPharmacies(String tableName) {
		ArrayList<PharmacyBase> pharmacy_list = new ArrayList<PharmacyBase>();

		Cursor cursor = database.query(tableName, allColumns, null, null, null,
				null, "distance ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PharmacyDetails pharmacy = cursorToPharmacyDetails(cursor);
			pharmacy_list.add(pharmacy);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return pharmacy_list;
	}

	private PharmacyDetails cursorToPharmacyDetails(Cursor cursor) {
		PharmacyDetails pharmacy = new PharmacyDetails();
		pharmacy.setId(cursor.getInt(0));
		pharmacy.setName(cursor.getString(1));
		pharmacy.setAddress(cursor.getString(2));
		pharmacy.setEmail(cursor.getString(3));
		pharmacy.setPhoneNumber(cursor.getString(4));
		pharmacy.setLat(cursor.getString(5));
		pharmacy.setLng(cursor.getString(6));
		pharmacy.setDistanceString(cursor.getString(7));

		return pharmacy;
	}
	
	private OpeningHour cursorToOpeningTime(Cursor cursor) {
		OpeningHour time = new OpeningHour();
		time.set_id(Integer.toString(cursor.getInt(0)));
		time.set_from(cursor.getString(1));
		time.set_to(cursor.getString(2));
		time.set_day(cursor.getInt(3));

		return time;
	}

}

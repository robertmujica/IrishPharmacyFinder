package com.lochend.pharmacylocation.repository;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BusinessPointSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_BUSINESS_POINT = "businessPoints";
	public static final String TABLE_OPEN_BUSINESS_POINT = "openBusinessPoints";
	public static final String TABLE_OPENING_TIME = "openingTime";
	public static final String COLUMN_ID = "id";
	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String EMAIL = "email";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String DISTANCE = "distance";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String DAY = "day";

	private static final String DATABASE_NAME = "businessPoint.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE_BUSINESS_POINT = "create table "
			+ TABLE_BUSINESS_POINT + "(" + COLUMN_ID
			+ " integer primary key, " + NAME + " text not null, " + ADDRESS
			+ " text not null, " + EMAIL + " text null," + PHONE_NUMBER
			+ " text not null, " + LAT + " text not null," + LNG
			+ " text not null," + DISTANCE + " text not null" + ");";
			
	private static final String DATABASE_CREATE_OPEN_BUSINESS_POINT = "create table "
			+ TABLE_OPEN_BUSINESS_POINT + "(" + COLUMN_ID
			+ " integer primary key, " + NAME + " text not null, " + ADDRESS
			+ " text not null, " + EMAIL + " text null," + PHONE_NUMBER
			+ " text not null, " + LAT + " text not null," + LNG
			+ " text not null," + DISTANCE + " text not null" + ");";
	
	private static final String DATABASE_CREATE_OPENING_TIME = "create table "
			+ TABLE_OPENING_TIME + "(" + COLUMN_ID
			+ " integer, " + START_TIME + " text not null, " + END_TIME
			+ " text not null, " + DAY + " integer not null, PRIMARY KEY(id, day)" + ");";

	public BusinessPointSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_BUSINESS_POINT);
		database.execSQL(DATABASE_CREATE_OPEN_BUSINESS_POINT);
		database.execSQL(DATABASE_CREATE_OPENING_TIME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BusinessPointSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSINESS_POINT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPEN_BUSINESS_POINT);
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_OPENING_TIME);
		onCreate(db);
	}
}

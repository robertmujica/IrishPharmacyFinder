package com.lochend.pharmacylocation.repository;

import com.lochend.pharmacy.cloud.Pharmacy;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.ProgressBar;

public abstract class BaseRepository {
	
	protected Activity _activity;

	/**
	 * Mobile Service Client reference
	 */
	protected MobileServiceClient mClient;

	/**
	 * Mobile Service Table used to access data
	 */
	protected MobileServiceTable<Pharmacy> mPharmacyTable;

	/**
	 * Progress spinner to use for table operations
	 */
	protected ProgressBar _progressBar;
	
	protected BusinessPointDataSource datasource;
	
	public BaseRepository(Context context, ProgressBar mProgressBar) {
		_activity = (Activity) context;
		_progressBar = mProgressBar;
		datasource = new BusinessPointDataSource(_activity);
	    datasource.open();
	}
	
	/**
	 * Creates a dialog and shows it
	 * 
	 * @param exception
	 *            The exception to show in the dialog
	 * @param title
	 *            The dialog title
	 */
	protected void createAndShowDialog(Exception exception, String title) {
		Throwable ex = exception;
		if (exception.getCause() != null) {
			ex = exception.getCause();
		}
		createAndShowDialog(ex.getMessage(), title);
	}

	/**
	 * Creates a dialog and shows it
	 * 
	 * @param message
	 *            The dialog message
	 * @param title
	 *            The dialog title
	 */
	protected void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(_activity);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
	
	public void refreshDataFromCloud(IPharmaciesListener _listener) {
		// TODO Auto-generated method stub
		
	}
}

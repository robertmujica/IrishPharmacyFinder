package com.lochend.pharmacylocation.repository;

import android.app.Activity;
import android.widget.ProgressBar;

import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;

public class ProgressFilter implements ServiceFilter {

	/**
	 * Progress spinner to use for table operations
	 */
	private ProgressBar _progressBar;
	
	private Activity _activity;
	
	public ProgressFilter(Activity activity, ProgressBar progressBar){
		_progressBar = progressBar;
		_activity = activity;
	}
	
	@Override
	public void handleRequest(ServiceFilterRequest request,
			NextServiceFilterCallback nextServiceFilterCallback,
			final ServiceFilterResponseCallback responseCallback) {
		_activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (_progressBar != null)
					_progressBar.setVisibility(ProgressBar.VISIBLE);
			}
		});

		nextServiceFilterCallback.onNext(request,
				new ServiceFilterResponseCallback() {

					@Override
					public void onResponse(ServiceFilterResponse response,
							Exception exception) {
						_activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (_progressBar != null)
									_progressBar
											.setVisibility(ProgressBar.GONE);
							}
						});

						if (responseCallback != null)
							responseCallback
									.onResponse(response, exception);
					}
				});
	}
}

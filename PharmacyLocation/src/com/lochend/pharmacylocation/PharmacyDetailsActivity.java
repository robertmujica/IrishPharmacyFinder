package com.lochend.pharmacylocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lochend.location.GlobalApp;
import com.lochend.pharmacylocation.entity.OpeningHour;
import com.lochend.pharmacylocation.entity.PharmacyDetails;
import com.lochend.pharmacylocation.repository.CachedPharmacyRepository;
import com.lochend.pharmacylocation.repository.IPharmacyOpeningHourListener;
import com.lochend.pharmacylocation.repository.IPharmacyRepository;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

public class PharmacyDetailsActivity extends Activity implements IPharmacyOpeningHourListener {
	
	private PharmacyDetails pharmacyDetails;
	private ProgressBar mProgressBar;
	private TableLayout openingTimeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_pharmacy_details);
		Bundle extras = getIntent().getExtras();
		pharmacyDetails = GlobalApp.getCurrentPharmacyDetails();
		
		((TextView)findViewById(R.id.pharmacyName)).setText(pharmacyDetails.getName());
		((TextView)findViewById(R.id.pharmacyAddress)).setText(pharmacyDetails.getAddress());
		((TextView)findViewById(R.id.distance)).setText(" ( " + pharmacyDetails.getDistance() + " )");
		((TextView)findViewById(R.id.phoneNumber)).setText(pharmacyDetails.getPhoneNumber());
		
		ImageButton btnCall = (ImageButton)findViewById(R.id.btnCall);
        btnCall.setTag(R.id.phoneNumber, pharmacyDetails.getPhoneNumber());
        btnCall.setOnClickListener(new OnCallListener(this));
        
        ImageButton btnNav = (ImageButton)findViewById(R.id.btnNav);
        btnNav.setTag(R.id.latitude, pharmacyDetails.getLat());
        btnNav.setTag(R.id.longitude, pharmacyDetails.getLng());
        btnNav.setTag(R.id.pharmacyName, pharmacyDetails.getName());
        btnNav.setOnClickListener(new OnNavigateListener(this));
        
        ImageButton btnShare = (ImageButton)findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new OnShareListener(this, pharmacyDetails));
        
        openingTimeLayout = (TableLayout)findViewById(R.id.openingTimeLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		// Initialize the progress bar
		mProgressBar.setVisibility(ProgressBar.GONE);
		
        IPharmacyRepository repository = new CachedPharmacyRepository(this, mProgressBar);
        repository.getPharmacyOpeningHours(pharmacyDetails.getId(), this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final ActionBar bar = getActionBar();
		int flags = 0;
		flags = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE;
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		//bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			String parentName = NavUtils.getParentActivityName(this);
			if (parentName != null)
				NavUtils.navigateUpFromSameTask(this);
			else
				super.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void getPharmacyOpeningHoursCallback(List<OpeningHour> pharmacyList) {
		TableRow row;
        TextView day, dayTime;
        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());
        
        for (OpeningHour item : pharmacyList){
        	
        	SimpleDateFormat formatter, FORMATTER;
        	formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        	String fromTime = null;
        	String toTime = null;
        	try {
				Date dateFrom = formatter.parse(item.get_from());
				Date dateTo = formatter.parse(item.get_to());
				FORMATTER = new SimpleDateFormat("HH:mm");
				fromTime = FORMATTER.format(dateFrom);
				toTime = FORMATTER.format(dateTo);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        	String todayOpeningTime = String.format("%s - %s", fromTime, toTime);
            row = new TableRow(this);
 
            day = new TextView(this);
            day.setTextColor(getResources().getColor(R.color.textColor));
            dayTime = new TextView(this);
            dayTime.setTextColor(getResources().getColor(R.color.textColor));
 
            day.setText(item.get_dayName());
            dayTime.setText(todayOpeningTime);
 
            day.setTypeface(Typeface.SANS_SERIF, 0);
            dayTime.setTypeface(Typeface.SANS_SERIF, 0);
 
            day.setTextSize(22);
            dayTime.setTextSize(15);
            dayTime.setPadding(0, 0, 10, 0);
            dayTime.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            
            if(GlobalApp.getCurrentDayOfWeek() == item.get_day() ){
            	day.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                dayTime.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                ((TextView)findViewById(R.id.todayOpeningTime)).setText(todayOpeningTime);
            }
            
            //t1.setWidth(50 * dip);
            //t2.setWidth(150 * dip);
            //t1.setPadding(20*dip, 0, 0, 0);
            row.addView(day);
            row.addView(dayTime);
 
            openingTimeLayout.addView(row, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	}
}

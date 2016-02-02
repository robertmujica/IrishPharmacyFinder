package com.lochend.pharmacylocation;

import com.lochend.location.GlobalApp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_details);
		
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			//Date lastUpdated = new Date(getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo. * 1000);
			TextView versionText = (TextView)findViewById(R.id.version);
			versionText.setText(GlobalApp.getResources().getString(R.string.version) + " " + versionName);
			
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		TextView devWebSiteUrl = (TextView)findViewById(R.id.devWebSite);
		devWebSiteUrl.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
	            i.setAction("android.intent.action.VIEW");
	            i.addCategory("android.intent.category.BROWSABLE");
	            Uri uri = Uri.parse("https://www.linkedin.com/pub/robert-mujica/8/378/106");
	            i.setData(uri);
	            startActivity(i);	
			}
		});
		
		TextView webSiteUrl = (TextView)findViewById(R.id.webSite);
		webSiteUrl.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
	            i.setAction("android.intent.action.VIEW");
	            i.addCategory("android.intent.category.BROWSABLE");
	            Uri uri = Uri.parse("http://irishpharmacyfinder.azurewebsites.net");
	            i.setData(uri);
	            startActivity(i);	
			}
		});
		
		TextView feedback = (TextView)findViewById(R.id.feedback);
		feedback.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{"irishpharmacyfinder@gmail.com"});		  
				email.putExtra(Intent.EXTRA_SUBJECT, "Irish Pharmacy finder feedback");
				email.putExtra(Intent.EXTRA_TEXT, "");
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
			}
		});
		
		Button btnRateUs = (Button)findViewById(R.id.btnRateus);
		btnRateUs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=" + ApplicationContextProvider.getContext().getPackageName());
				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

				  try {
				        startActivity(myAppLinkToMarket);

				      } catch (ActivityNotFoundException e) {

				        //the device hasn't installed Google Play
				        Toast.makeText(AboutActivity.this, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
				      }
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final ActionBar bar = this.getActionBar();
		int flags = 0;
		flags = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE;
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle(GlobalApp.getResources().getText(R.string.about_title));

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			String parentName = NavUtils.getParentActivityName(this);
			if (parentName != null){
				Intent intent = new Intent(this, MainActivity.class);
				this.startActivity(intent);
			}
			else
				super.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

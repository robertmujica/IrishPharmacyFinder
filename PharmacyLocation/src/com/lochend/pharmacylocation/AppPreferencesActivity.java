package com.lochend.pharmacylocation;

import com.lochend.pharmacylocation.repository.AppPreferencesRepository;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class AppPreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener { 
	ListPreference mode;
	ListPreference radius;
	Resources mResources;
	EditTextPreference version;
	Preference developedByUrl;
	Preference webSiteUrl;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        radius = (ListPreference) findPreference("pref_pharmacy_search_radius");
        mode = (ListPreference) findPreference("pref_pharmacy_open_mode");
        version = (EditTextPreference)findPreference("pref_version");
        developedByUrl = (Preference)findPreference("pref_developedBy");
        webSiteUrl = (Preference)findPreference("pref_website");

        radius.setOnPreferenceChangeListener(this);
        mode.setOnPreferenceChangeListener(this);
        developedByUrl.setOnPreferenceClickListener(this);
        webSiteUrl.setOnPreferenceClickListener(this);
        radius.setSummary(AppPreferencesRepository.getRadius());
        mode.setSummary(AppPreferencesRepository.getMode());
        mResources = getResources();
        
        try {
			PackageInfo pinfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			version.setSummary(pinfo.versionName);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue.toString());
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final ActionBar bar = this.getActionBar();
		int flags = 0;
		flags = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE;
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle(mResources.getText(R.string.general_settings));
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
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();
		
		if(key.equals("pref_developedBy") || key.equals("pref_website")){
			Intent i = new Intent();
            i.setAction("android.intent.action.VIEW");
            i.addCategory("android.intent.category.BROWSABLE");
            Uri uri = Uri.parse(preference.getSummary().toString());
            i.setData(uri);
            startActivity(i);
		}
		
		return false;
	}
}

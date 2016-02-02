package com.lochend.pharmacylocation;

import java.util.ArrayList;
import java.util.List;

import com.lochend.location.GlobalApp;
import com.lochend.pharmacylocation.entity.PharmacyBase;
import com.lochend.pharmacylocation.entity.PharmacyDetails;
import com.lochend.pharmacylocation.repository.AppPreferencesRepository;
import com.lochend.pharmacylocation.repository.IPharmaciesListener;
import com.lochend.pharmacylocation.repository.IPharmacyRepository;
import com.lochend.pharmacylocation.repository.PharmacyRepository;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class PharmacyListActivity extends ExpandableListActivity implements IPharmaciesListener, 
	IPharmacyQueryActivity,
	OnQueryTextListener{
	private ArrayList<PharmacyBase> _pharmacies;
	private IPharmacyRepository _repository;
	private ProgressBar mProgressBar;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
	CustomDrawerAdapter adapter;
	int currentRadius = 0;
	SearchView searchView;
	Menu mainMenu;
	boolean isLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		isLoading = true;
		setContentView(R.layout.pharmacy_list_activity);
		
		mProgressBar = (ProgressBar) findViewById(R.id.address_progress);
		// Initialize the progress bar
		mProgressBar.setVisibility(ProgressBar.GONE);
				
		this.getResources();

		// Set ExpandableListView values
		getExpandableListView().setGroupIndicator(null);
		registerForContextMenu(getExpandableListView());
		
		_repository = new PharmacyRepository(this, mProgressBar);
		addNavigationDrawer();
		GlobalApp.setCurrentActivity(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDrawerLayout.closeDrawer(mDrawerList);
		mDrawerList.setItemChecked(AppPreferencesRepository.getRadiusIndex(), true);
	}
	
	private void addNavigationDrawer(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        getResources().getStringArray(R.array.operating_systems);
        mTitle = getTitle();
        
        List<DrawerItem> dataList;
        dataList = new ArrayList<DrawerItem>();
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
        
        dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.preference_search_radius), R.drawable.ic_action_search, true, false)); // adding a header to the list
        String[] radius = AppPreferencesRepository.getRadiusTitleList();
        
        for(int i = 0; i < radius.length; i++){
        	if(AppPreferencesRepository.getRadius().equals(radius[i]))
        		currentRadius = i;
        	dataList.add(new DrawerItem(radius[i],0, true, false));
        }
        
        dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.action_settings), R.drawable.ic_action_settings, false, true));
		dataList.add(new DrawerItem(GlobalApp.getResources().getString(R.string.about_title), R.drawable.ic_action_about, false, true));
		
        adapter = new CustomDrawerAdapter(this, R.layout.drawer_item,
				dataList);

        mDrawerList.setAdapter(adapter);
        
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this, dataList, mDrawerLayout, mDrawerList));
        
        mDrawerToggle = new ActionBarDrawerToggle(this,                   
                mDrawerLayout,          
                R.drawable.ic_drawer,   
                R.string.drawer_open,   
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Settings");
            }
        };
        
     // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setItemChecked(currentRadius, true);
        mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		mainMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_main, menu);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle("Pharmacies");
		if(AppPreferencesRepository.ALL_RADIUS_INDEX != AppPreferencesRepository.getRadiusIndex()){
			actionBar.setSubtitle("in " + AppPreferencesRepository.getRadius());
		}else{
			actionBar.setSubtitle("");
		}
	
		searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        
        int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        // Get the search close button image view
        ImageView closeButton = (ImageView)searchView.findViewById(searchCloseButtonId);
        
     // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                GlobalApp.setSearchText("");
                refresh();
            }
        });
        
		TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				
				if(GlobalApp.getCurrentTabIndexClassName().equals("PharmacyListActivity")){
					if(!isLoading){
						GlobalApp.setCurrentTabIndex("PharmacyListActivity", tab.getPosition());
					}
					else{
						isLoading = false;
					}
					
					if(tab.getPosition() == 1){
						searchView.setVisibility(View.GONE);
					}
					else{
						menu.findItem(R.id.action_search).setVisible(true);
						searchView.setVisibility(View.VISIBLE);
					}
				}
				else{
					if(GlobalApp.getCurrentTabIndex() == 1){
						menu.findItem(R.id.action_search).setVisible(false);
						searchView.setVisibility(View.GONE);
					}
					else{
						searchView.setVisibility(View.VISIBLE);
					}
					GlobalApp.setCurrentTabIndexClassName("PharmacyListActivity");
				}
					
				refresh();
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
		};

		//http://www.vogella.com/tutorials/AndroidActionBar/article.html
		Tab allTab = actionBar.newTab();
		allTab.setText(getResources().getString(R.string.all));
		allTab.setTag("All");
		allTab.setTabListener(tabListener);
		actionBar.addTab(allTab);
		
		Tab onlyOpenTab = actionBar.newTab();
		onlyOpenTab.setText(getResources().getString(R.string.onlyOpen));
		onlyOpenTab.setTag("OnlyOpen");
		onlyOpenTab.setTabListener(tabListener);
		actionBar.addTab(onlyOpenTab);
		actionBar.selectTab(actionBar.getTabAt(GlobalApp.getCurrentTabIndex()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        
		Log.v("LocationActivity", "onOptionsItemSelected");
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			String parentName = NavUtils.getParentActivityName(this);
			if (parentName != null)
				NavUtils.navigateUpFromSameTask(this);
			else
				super.finish();
			return true;
		case R.id.newLoction:
			Intent intentMain = new Intent(this.getApplicationContext(),
					MainActivity.class);
			startActivity(intentMain);
			finish();
			return true;
		case R.id.action_settings:
			Intent intentLoc = new Intent(this.getApplicationContext(),
					AppPreferencesActivity.class);
			startActivity(intentLoc);
			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	public void getPharmaciesCallback(ArrayList<PharmacyBase> pharmaciesFound) {
		if (pharmaciesFound == null)
			return;

		_pharmacies = pharmaciesFound;

		final ExpandablePharmacyListAdapter mAdapter = new ExpandablePharmacyListAdapter(this, _pharmacies);

		// Set Adapter to ExpandableList Adapter
		this.setListAdapter(mAdapter);			
		setNumberOfPharmaciesFound(_pharmacies);
	}
	
	private void setNumberOfPharmaciesFound(ArrayList<PharmacyBase> pharmacyList) {
		if(getActionBar() != null){
			Tab currentTab = getActionBar().getSelectedTab();
			String tabText = "";
			
			if(currentTab.getPosition() != GlobalApp.getCurrentTabIndex())
				return;
			
			if(GlobalApp.getCurrentTabIndex() == 0){
				tabText = getResources().getString(R.string.all);
			}
			else{
				tabText = getResources().getString(R.string.onlyOpen);
			}
			
			currentTab.setText(tabText + " (" + pharmacyList.size()  + ")");
		}
	}

	@Override
	public void refresh() {
		
		if(GlobalApp.getCurrentTabIndex() == 0 && GlobalApp.getSearchText() != null && !GlobalApp.getSearchText().equals("")){
			searchView.setQuery(GlobalApp.getSearchText(), true);
			searchView.setIconifiedByDefault(false);
			getActionBar().setSubtitle("");
		}
		else{
			if(searchView != null){
				searchView.setIconified(true);
				searchView.setIconified(true);
			}
			getActionBar().setTitle("Pharmacies");
			if(AppPreferencesRepository.ALL_RADIUS_INDEX != AppPreferencesRepository.getRadiusIndex()){
				getActionBar().setSubtitle("in " + AppPreferencesRepository.getRadius());
			}
			else{
				getActionBar().setSubtitle("");
			}
			if(GlobalApp.getCurrentTabIndex() == 0)
		    	_repository.getAllNearestPharmacies(this);
		    else{
		    	GlobalApp.setSearchText("");
		    	_repository.getOpenOnlyNearestPharmacies(this);
		    }
		}
		GlobalApp.setCurrentTabIndexClassName("PharmacyListActivity");
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		GlobalApp.setSearchText(query);
		_repository.searchPharmacies(this,  query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return true;
	}
}

package com.lochend.pharmacylocation;

import java.util.List;

import com.lochend.location.GlobalApp;
import com.lochend.pharmacylocation.repository.AppPreferencesRepository;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener {
	
	private List<DrawerItem> _dataList;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Activity _activity;
	
	public DrawerItemClickListener(Activity activity, List<DrawerItem> dataList, DrawerLayout drawerLayout, ListView drawerList){
		_dataList = dataList;
		this.mDrawerLayout = drawerLayout;
		this.mDrawerList = drawerList;
		_activity = activity;
	}
	
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
    	if (_dataList.get(position).getTitle() == null && !_dataList.get(position).isNavigate()) {
    		selectItem(position);
		}
    	
    	if(GlobalApp.getResources().getString(R.string.action_settings).equals(_dataList.get(position).getItemName())){
    		Intent intent = new Intent(_activity, AppPreferencesActivity.class);
    		_activity.startActivity(intent);
    		
    	}
    	
    	if(GlobalApp.getResources().getString(R.string.about_title).equals(_dataList.get(position).getItemName())){
    		Intent intent = new Intent(_activity, AboutActivity.class);
    		_activity.startActivity(intent);
    		
    	}
    	
    	// Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    private void selectItem(int position) {
    	
    	String[] radiusList = AppPreferencesRepository.getRadiusTitleList();
    	
    	if(position >= 1){
    		String radiusSelected = radiusList[position - 1];
        	AppPreferencesRepository.setRadius(radiusSelected);
        	GlobalApp.setSearchText("");
        	GlobalApp.getCurrentActivity().refresh();
    	}

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //getActionBar().setTitle(("test"));
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}

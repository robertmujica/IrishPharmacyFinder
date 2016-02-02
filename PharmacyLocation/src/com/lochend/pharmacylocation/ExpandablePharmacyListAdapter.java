package com.lochend.pharmacylocation;

import java.util.ArrayList;

import com.lochend.location.GlobalApp;
import com.lochend.pharmacylocation.entity.PharmacyBase;
import com.lochend.pharmacylocation.entity.PharmacyDetails;

import GPSTracker.GPSTracker;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandablePharmacyListAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private ArrayList<PharmacyBase> _pharmacies;
	private int ParentClickStatus=-1;
    private int ChildClickStatus=-1;
    private Activity _activity;
	 
    public ExpandablePharmacyListAdapter(Activity activity, ArrayList<PharmacyBase> pharmacies)
    {
    	_activity = activity;
    	_pharmacies = pharmacies;
        // Create Layout Inflator
        //inflater = LayoutInflater.from(PharmacyListActivity.this);
    	inflater = LayoutInflater.from(_activity);
    }
     
    // This Function used to inflate parent rows view
     
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, 
            View convertView, ViewGroup parentView)
    {
        final PharmacyDetails pharmacy = (PharmacyDetails)_pharmacies.get(groupPosition);
         
        // Inflate grouprow.xml file for parent rows
        convertView = inflater.inflate(R.layout.pharmacy_row, parentView, false); 
         
        // Get grouprow.xml file elements and set values
        ((TextView) convertView.findViewById(R.id.text1)).setText(pharmacy.getName());
        ((TextView) convertView.findViewById(R.id.text)).setText(pharmacy.getAddress());
        ((TextView) convertView.findViewById(R.id.distance)).setText(pharmacy.getDistance());
        
        if(isExpanded){
        	ImageView indicator = (ImageView)convertView.findViewById(R.id.rowIndicator);
        	indicator.setImageBitmap(null);
        	indicator.destroyDrawingCache();
            indicator.setImageResource(R.drawable.ic_navigation_collapse);
            //convertView.setBackgroundResource(R.drawable.round_corner_button);
        }
        else{
        	ImageView indicator = (ImageView)convertView.findViewById(R.id.rowIndicator);
        	indicator.setImageBitmap(null);
        	indicator.destroyDrawingCache();
            indicator.setImageResource(R.drawable.ic_navigation_expand);
        }
        
        
        /*ImageView image=(ImageView)convertView.findViewById(R.id.image);
         
        image.setImageResource(
        		_activity.getResources().getIdentifier(
               "com.androidexample.customexpandablelist:drawable/setting"+pharmacy.getName(),null,null));*/
                   
        //ImageView rightcheck=(ImageView)convertView.findViewById(R.id.rightcheck);
         
        //Log.i("onCheckedChanged", "isChecked: "+parent.isChecked());
         
        // Change right check image on parent at runtime
        /*if(pharmacy.isChecked()==true){
            rightcheck.setImageResource(
            		_activity.getResources().getIdentifier(
                      "com.androidexample.customexpandablelist:drawable/rightcheck",null,null));
        }   
        else{
            rightcheck.setImageResource(
            		_activity.getResources().getIdentifier(
                      "com.androidexample.customexpandablelist:drawable/button_check",null,null));
        }   */
         
        // Get grouprow.xml file checkbox elements
        //CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        //checkbox.setChecked(parent.isChecked());
         
        // Set CheckUpdateListener for CheckBox (see below CheckUpdateListener class)
        //checkbox.setOnCheckedChangeListener(new CheckUpdateListener(parent));
         
        return convertView;
    }

     
    // This Function used to inflate child rows view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
            View convertView, ViewGroup parentView)
    {
    	
        final PharmacyBase parent = _pharmacies.get(groupPosition);
        final PharmacyDetails child = (PharmacyDetails) parent.getDetails();
         
        // Inflate childrow.xml file for child rows
        convertView = inflater.inflate(R.layout.pharmacy_details, parentView, false);
        
        ImageButton btnCall = (ImageButton)convertView.findViewById(R.id.btnCall);
        btnCall.setTag(R.id.phoneNumber, child.getPhoneNumber());
        btnCall.setOnClickListener(new OnCallListener(_activity));
        ImageButton btnNav = (ImageButton)convertView.findViewById(R.id.btnNav);
        btnNav.setTag(R.id.latitude, child.getLat());
        btnNav.setTag(R.id.longitude, child.getLng());
        btnNav.setTag(R.id.pharmacyName, child.getName());
        btnNav.setOnClickListener(new OnNavigateListener(_activity));
        ImageButton btnMore = (ImageButton)convertView.findViewById(R.id.btnMore);
    	GlobalApp.setCurrentPharmacyDetails(child);
        btnMore.setOnClickListener(btnMoreListener);
        
        return convertView;
    }
    
    public OnClickListener btnMoreListener = new OnClickListener()
    {
        public void onClick(View v)
        {   
        	Intent intent = new Intent(_activity, PharmacyDetailsActivity.class);
        	_activity.startActivity(intent);
        } 
    };  
      
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        //Log.i("Childs", groupPosition+"=  getChild =="+childPosition);
        return _pharmacies.get(groupPosition).getDetails();
    }

    //Call when child row clicked
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        /****** When Child row clicked then this function call *******/
         
        //Log.i("Noise", "parent == "+groupPosition+"=  child : =="+childPosition);
        if( ChildClickStatus!=childPosition)
        {
           ChildClickStatus = childPosition;
            
           //Toast.makeText(_activity.getApplicationContext(), "Parent :"+groupPosition + " Child :"+childPosition , 
           //         Toast.LENGTH_LONG).show();
        }  
         
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }
  
     
    @Override
    public Object getGroup(int groupPosition)
    {
        Log.i("Parent", groupPosition+"=  getGroup ");
         
        return _pharmacies.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return _pharmacies.size();
    }

    //Call when parent row clicked
    @Override
    public long getGroupId(int groupPosition)
    {
        Log.i("Parent", groupPosition+"=  getGroupId "+ParentClickStatus);
         
        if(groupPosition==2 && ParentClickStatus!=groupPosition){
             
            //Alert to user
            //Toast.makeText(_activity.getApplicationContext(), "Parent :"+groupPosition , 
            //        Toast.LENGTH_LONG).show();
        }
         
        ParentClickStatus=groupPosition;
        if(ParentClickStatus==0)
            ParentClickStatus=-1;
         
        return groupPosition;
    }
    
    public void setList(ArrayList<PharmacyBase> pharmacies)
    {
    	_pharmacies = pharmacies;
    }

    @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty()
    {
        return ((_pharmacies == null) || _pharmacies.isEmpty());
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }
     
    /******************* Checkbox Checked Change Listener ********************/
     
    /*private final class CheckUpdateListener implements OnCheckedChangeListener
    {
        private final PharmacyBase parent;
         
        private CheckUpdateListener(PharmacyBase parent)
        {
            this.parent = parent;
        }
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            Log.i("onCheckedChanged", "isChecked: "+isChecked);
            parent.setChecked(isChecked);
             
            ((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
             
            final Boolean checked = parent.isChecked();
            Toast.makeText(getApplicationContext(), 
                  "Parent : "+parent.getName() + " " + (checked ? STR_CHECKED : STR_UNCHECKED), 
                       Toast.LENGTH_LONG).show();
        }
    }*/
    /***********************************************************************/
    
}

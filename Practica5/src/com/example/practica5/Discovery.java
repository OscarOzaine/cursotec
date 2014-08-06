package com.example.practica5;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Discovery extends Activity {
	
	ListView listViewDiscover;
	ArrayAdapter<String> adapterDiscover;
	ArrayList<String> valuesDiscovery;
	private SingBroadcastReceiver mReceiver;
	BluetoothAdapter mBluetoothAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discovery);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		listViewDiscover = (ListView) findViewById(R.id.listViewDiscovery);
		
		valuesDiscovery =  new ArrayList<String>();
		
		adapterDiscover = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, valuesDiscovery);
		
		listViewDiscover.setAdapter(adapterDiscover);
		/*
		// ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() 
        {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				// ListView Clicked item index
				int itemPosition     = position;
	               
				// ListView Clicked item value
				String  itemValue    = (String) listView.getItemAtPosition(position);
	                  
				// Show Alert 
				Toast.makeText(getApplicationContext(),
						"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
						.show();
			}
         }); 
        */
		mBluetoothAdapter.startDiscovery();
		
		//let's make a broadcast receiver to register our things
	    mReceiver = new SingBroadcastReceiver();
	    IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    this.registerReceiver(mReceiver, ifilter);
       
	}
	
	class SingBroadcastReceiver extends BroadcastReceiver {
	
		
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction(); //may need to chain this to a recognizing function
	        if (BluetoothDevice.ACTION_FOUND.equals(action)){
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a Toast
	            String deviceDiscovered = device.getName() + " - " + device.getAddress();
	            adapterDiscover.add(deviceDiscovered);
	            listViewDiscover.setAdapter(adapterDiscover); 
	            adapterDiscover.notifyDataSetChanged();
	            Toast.makeText(context, deviceDiscovered, Toast.LENGTH_LONG).show();
	        }
	        
	        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
	        {
	        	Log.d("ACA", "mensaje");
	        	Toast.makeText(context, R.string.bt_searchfinished , Toast.LENGTH_LONG).show();
	        }
	        
	    }
	    
	}
	//// Register the BroadcastReceiver
	//IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	//registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

	
	@Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        //this.finish();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
}

package com.example.practica6;

import java.util.ArrayList;

import com.example.practica6.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Discovery extends Activity {
	
	private static final int REQUEST_ENABLE_BT = 1;
	
	
	ListView listViewDiscover;
	ArrayAdapter<String> adapterDiscover;
	ArrayList<String> valuesDiscovery;
	private SingBroadcastReceiver mReceiver;
	BluetoothAdapter mBluetoothAdapter;
	Button btnDiscovery;
	AlertDialog.Builder alertBuilder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discovery);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		listViewDiscover = (ListView) findViewById(R.id.listViewDiscovery);
		btnDiscovery = (Button) findViewById(R.id.btn_discovery);
		
		valuesDiscovery =  new ArrayList<String>();
		
		adapterDiscover = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, valuesDiscovery);
		
		listViewDiscover.setAdapter(adapterDiscover);
		
		btnDiscovery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mBluetoothAdapter.startDiscovery();
            }
        });
		
		alertBuilder = new AlertDialog.Builder(this);
		
		
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
	            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a Toast
	            String deviceDiscovered = device.getName() + " - " + device.getAddress();
	            adapterDiscover.add(deviceDiscovered);
	            listViewDiscover.setAdapter(adapterDiscover); 
	            adapterDiscover.notifyDataSetChanged();
	            
	            
	            listViewDiscover.setOnItemClickListener(new OnItemClickListener() 
	            {
	    			@Override
	    			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) 
	    			{
	    				Intent intent = new Intent("android.intent.action.MAIN");
	    				// ListView Clicked item index
	    				final int itemPosition     = position;
	    	               
	    				// ListView Clicked item value
	    				final String  itemValue    = (String) listViewDiscover.getItemAtPosition(position);
	    				
	    				final String[] deviceArray = itemValue.split(" - ");
	    				
	    				
	    				alertBuilder.setTitle("Confirmar conexion con: "+deviceArray[0]);
		               	alertBuilder.setMessage("Direccion MAC: "+deviceArray[1]);
		               	 
		               	// Add the buttons
		               	alertBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
		               		public void onClick(DialogInterface dialog, int id) {
		               			Log.d("debug", "Button Ok");
		               			
			    				
			    				com.example.practica6.MainActivity.deviceSelected = null;
			    				com.example.practica6.MainActivity.hasDevice = 0;
			    				
			    				com.example.practica6.MainActivity.deviceSelected = new Device();
			    				
			    				com.example.practica6.MainActivity.hasDevice = 1;
			    				com.example.practica6.MainActivity.deviceSelected.setName(deviceArray[0]);
			    				com.example.practica6.MainActivity.deviceSelected.setMacAddress(deviceArray[1]);
			    				
			    				// Show Alert 
			    				Toast.makeText(getApplicationContext(),
			    						"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
			    						.show();
		               		}
		               	});
		               	 
		               	alertBuilder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
		               		public void onClick(DialogInterface dialog, int id) {
		               			Log.d("debug", "Button Cancel");
		               		
		               		}
		               	});
		               	
		            	alertBuilder.show();
	    				
               			 
              
              
	    			}
	             }); 
	            //Toast.makeText(context, deviceDiscovered, Toast.LENGTH_LONG).show();
	        }
	        else
	        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
	        {
	        	Log.d("ACA", "mensaje");
	        	Toast.makeText(context, R.string.bt_searchfinished , Toast.LENGTH_LONG).show();
	        }
	        
	    }
	    
	}
	
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
    	listViewDiscover = null;
    	adapterDiscover = null;
    	valuesDiscovery = null;
    	unregisterReceiver(mReceiver);
    	mBluetoothAdapter = null;
    	btnDiscovery = null;
        super.onDestroy();
        // The activity is about to be destroyed.
    }
}

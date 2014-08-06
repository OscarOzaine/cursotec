package com.example.practica5;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private static final int REQUEST_ENABLE_BT = 1;
	
	ListView listView;
	Button button;
	ArrayList<String> values;
	ArrayAdapter<String> adapter;
	EditText mEdit;
	String texto = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		
		// Get ListView object from xml
        listView = (ListView) findViewById(R.id.listView1);
        button = (Button) findViewById(R.id.button1);
        mEdit   = (EditText)findViewById(R.id.editText1);
        
        values =  new ArrayList<String>();
        
       adapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter); 
        
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
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	texto = mEdit.getText().toString();
            	if(!texto.equals("")){
            		adapter.add(texto);
                	listView.setAdapter(adapter); 
                    adapter.notifyDataSetChanged();
                    mEdit.setText("");
            	}
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 
		// Handle item selection
	    switch (item.getItemId()) {
	    
	    	case R.id.action_btDetectable:
	    		Intent discoverableIntent = new
	    		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	    		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	    		startActivity(discoverableIntent);
	    		return true;
	    	
	    	case R.id.action_btSearch:
	    		Intent intentDiscovery = new Intent(this, Discovery.class);
                startActivity(intentDiscovery);   
	    		return true;
	    		
	    	case R.id.action_btPaired:
	    		Intent intentPaired = new Intent(this, Paired.class);
                startActivity(intentPaired);   
	    		return true;
	      
	        case R.id.action_about:
	        	/*
	        	Toast.makeText(getApplicationContext(),
						"Help", Toast.LENGTH_LONG)
						.show();
	        	*/
	        	Intent intentHelp = new Intent(this, About.class);
                startActivity(intentHelp);   
	        	return true;
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}
}

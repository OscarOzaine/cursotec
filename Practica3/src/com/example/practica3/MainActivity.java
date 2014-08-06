package com.example.practica3;

import java.util.ArrayList;



import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
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
		
		
		// Get ListView object from xml
        listView = (ListView) findViewById(R.id.listView1);
        button = (Button) findViewById(R.id.button1);
        mEdit   = (EditText)findViewById(R.id.editText1);
        
        values =  new ArrayList<String>();
        
        
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

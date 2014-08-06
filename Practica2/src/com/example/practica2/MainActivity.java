package com.example.practica2;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
		 
		 final Button button = (Button) findViewById(R.id.button1);
		 final TextView text = (TextView) findViewById(R.id.text1);
    	 
		 
		 
         button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 
            	 builder.setTitle("Confirmacion");
            	 builder.setMessage("Favor de confirmar");
            	 
            	 // Add the buttons
            	 builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            		 public void onClick(DialogInterface dialog, int id) {
            			 Log.d("debug", "Button Ok");
            			 text.setText("Texto cambiado");
            		 }
            	 });
            	 
            	 builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            		 public void onClick(DialogInterface dialog, int id) {
            			 Log.d("debug", "Button Cancel");
            			 text.setText(R.string.change_text);
            		 }
            	 });
            	 
            	 // Set other dialog properties
            	

            	 // Create the AlertDialog
            	 AlertDialog dialog = builder.show();
            	 
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

package com.example.practica6;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {
	// Debugging
    public static final String TAG = "BluetoothChat";
    public static final boolean D = true;
    
	 // Message types sent from the BlueInterfaceService Handler
    public static final int MESSAGE_STATE_CHANGE = 1, 
    						MESSAGE_READ = 2, 
    						MESSAGE_WRITE = 3, 
    						MESSAGE_DEVICE_NAME = 4, 
    						MESSAGE_TOAST = 5, 
    						SYNC_CONNECTION = 6,
    						// Intent request codes
    						REQUEST_CONNECT_DEVICE = 1, 
    						REQUEST_ENABLE_BT = 2;
    
	static int hasDevice = 0;
	static Device deviceSelected = new Device();
	
	// String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
	ListView listView;
	Button button;
	ArrayList<String> values;
	ArrayAdapter<String> adapter;
	EditText mEdit;
	String texto = "";
	TextView deviceTxt;
	BluetoothAdapter mBluetoothAdapter;
	
	BluetoothSocket mmSocket;
    InputStream mmInStream;
    OutputStream mmOutStream;
    
    BluetoothDevice mmDevice;
    private String mConnectedDeviceName = null;
    
    private final String NAME = "BluetoothChat"; 
    private final UUID MY_UUID = UUID.fromString("00010101-1100-0000-0000-1234567890AB");
    
    // Key names received from the BlueInterfaceService Handler
    public static final String DEVICE_NAME = "device_name", TOAST = "toast";
   
    BluetoothServerSocket mmServerSocket = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        deviceTxt = (TextView)findViewById(R.id.deviceTxt);
        
        
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
            		
                    sendMessage(texto);
                    //connectedThread.write(bytes);
            	}
            }
        });
		
        
	}
	
	@Override
    protected void onDestroy() {
		
		super.onDestroy();
		if (BluetoothService.mBlueService != null){
	        BluetoothService.mBlueService.stop();
	       }
	}
	
	
	@Override
    protected void onStart() {
        super.onStart();
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (BluetoothService.mBlueService == null) setupChat();
        }
        // The activity is about to become visible.
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume()","1");
        
        
        if (BluetoothService.mBlueService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BluetoothService.mBlueService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              BluetoothService.mBlueService.start();
              //BluetoothService.mBlueService.
            }
        }
        
        if (hasDevice == 1){
        	Log.d("onResume()","HasDevice");
        	deviceTxt.setText(deviceSelected.getName()+" - "+deviceSelected.getMacAddress());
			Log.d("name",deviceSelected.getName());
			Log.d("address",deviceSelected.getMacAddress());
			BluetoothDevice device=null ;
			
			try{
            	device = mBluetoothAdapter.getRemoteDevice(deviceSelected.getMacAddress());
            	// Attempt to connect to the device
			}catch(IllegalArgumentException e){
				Log.e(TAG,"@Main-getRemoteDevice(address)",e);
				Toast.makeText(getApplicationContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
			}
           
            if(device != null)
            	BluetoothService.mBlueService.connect(device);
			
		}
		
        // The activity has become visible (it is now "resumed").
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
	
	
	
	private void setupChat() {
        Log.d(TAG, "setupChat()");
        
        // Initialize the BlueInterfaceService to perform bluetooth connections
        BluetoothService.mBlueService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
	
	public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (BluetoothService.mBlueService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BlueInterfaceService to write
            byte[] send = message.getBytes();
            BluetoothService.mBlueService.write(send);
            if(D)
             Log.d(TAG,"Message Sent: "+message);
            adapter.add(message);
        	listView.setAdapter(adapter); 
            adapter.notifyDataSetChanged();
            mEdit.setText("");
        	
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
	
	 // The Handler that gets information back from the BlueInterfaceService
    private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    break;
                case BluetoothService.STATE_CONNECTING:
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    break;
                }
                break;
            case SYNC_CONNECTION:
             if(D)
         Log.i(TAG,"SYNCED Connection: "+msg.obj);
             break;
            case MESSAGE_WRITE:
                //byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
               // String writeMessage = new String(writeBuf);
                try {
            
             }catch(NumberFormatException e) {}
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                if(msg.arg1>0) {
                 String readMessage = new String(readBuf, 0, msg.arg1);
                 try {
                 if(D)
                	 Log.i(TAG,"RemoteMsg: \""+readMessage+"\"");
                 	 if(!readMessage.equals("test"))
                 	 {
                 		 adapter.add(readMessage);
                 	 	 listView.setAdapter(adapter); 
             		     adapter.notifyDataSetChanged();
             		     mEdit.setText("");
                 	 }
                 }catch(NumberFormatException e) {}//tv.setText(readMessage);}
                }
                break;
                
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };



	
	
}

package com.itmexicali.sunshine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
  * A placeholder fragment containing a simple view.
  */
public class ForecastFragment extends Fragment {
	
		private ArrayAdapter<String> mForecastAdapter;
    	
        public ForecastFragment() {
        }

        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add this line in order for this fragment to handle menu events.
            setHasOptionsMenu(true);
        }
        
        
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.forecastfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_refresh) {
            	FetchWeatherTask weatherTask = new FetchWeatherTask();
            	weatherTask.execute("94043");
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            String[] forecastArray = 
            	{
            		"Lunes - Calor - 114/80",
            		"Martes - Calor - 110/82",
            		"Miercoles - Calor - 115/86",
            		"Jueves - Mucho Calor - 124/100",
            		"Viernes - Calor - 100/76",
            		"Sabado - Bastante Calor - 134/105",
            		"Domingo - Calor - 112/84"
            	};
            
            ArrayList<String> weekForecast = new ArrayList<String>(
            		Arrays.asList(forecastArray));
            
            
            mForecastAdapter = 
            		new ArrayAdapter<String>(
            				getActivity(),
            				R.layout.list_item_forecast,
            				R.id.list_item_forecast_textview,
            				weekForecast);
            
            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);
            
            return rootView;
        }
        
        
        public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        	
        	
        	private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        	
        	@Override
			protected String[] doInBackground(String... params) {


				// These two need to be declared outside the try/catch
		         // so that they can be closed in the finally block.
		         HttpURLConnection urlConnection = null;
		         BufferedReader reader = null;
		          
		         // Will contain the raw JSON response as a string.
		         String forecastJsonStr = null;
		        
		         String format = "json";
		         String units = "metric";
		         int numDays = 7;
		         
		         try {
		             // Construct the URL for the OpenWeatherMap query
		             // Possible parameters are available at OWM's forecast API page, at
		             // http://openweathermap.org/API#forecast
		             
		        	 final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
		        	 final String QUERY_PARAM = "q";
		        	 final String FORMAT_PARAM = "mode";
		        	 final String UNITS_PARAM = "units";
		        	 final String DAYS_PARAM = "cnt";
		        	 
		        	 
		        	 Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
		        	 				.appendQueryParameter(QUERY_PARAM, params[0])
		        	 				.appendQueryParameter(FORMAT_PARAM, format)
		        	 				.appendQueryParameter(UNITS_PARAM, units)
		        	 				.appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
		        	 				.build();
		        	 
		        	 URL url = new URL(builtUri.toString());
		        	 
		        	 Log.v(LOG_TAG, "Built Uri = "+builtUri.toString());
		          
		             // Create the request to OpenWeatherMap, and open the connection
		             urlConnection = (HttpURLConnection) url.openConnection();
		             urlConnection.setRequestMethod("GET");
		             urlConnection.connect();
		          
		             // Read the input stream into a String
		             InputStream inputStream = urlConnection.getInputStream();
		             StringBuffer buffer = new StringBuffer();
		             if (inputStream == null) {
		                 // Nothing to do.
		                 return null;
		             }
		             reader = new BufferedReader(new InputStreamReader(inputStream));
		          
		             String line;
		             while ((line = reader.readLine()) != null) {
		                 // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
		                 // But it does make debugging a *lot* easier if you print out the completed
		                 // buffer for debugging.
		                 buffer.append(line + "\n");
		             }
		          
		             if (buffer.length() == 0) {
		                 // Stream was empty.  No point in parsing.
		                 forecastJsonStr = null;
		             }
		             forecastJsonStr = buffer.toString();
		         } catch (IOException e) {
		             Log.e(LOG_TAG, "Error ", e);
		             // If the code didn't successfully get the weather data, there's no point in attempting
		             // to parse it.
		             forecastJsonStr = null;
		         } finally{
		             if (urlConnection != null) {
		                 urlConnection.disconnect();
		             }
		             if (reader != null) {
		                 try {
		                     reader.close();
		                 } catch (final IOException e) {
		                     Log.e(LOG_TAG, "Error closing stream", e);
		                 }
		             }
		         }
		         
		         try{
		        	 return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr,numDays);
		         }catch(JSONException e){
		        	 Log.e(LOG_TAG,e.getMessage(),e);
		        	 e.printStackTrace();
		         }
				
		        
		         return null;
			}

        	
        	protected void onPostExecute(String[] result){
        		if(result != null){
        			mForecastAdapter.clear();
        			for(String dayForecastStr : result){
        				mForecastAdapter.add(dayForecastStr);
        			}
        		}
        	}
        }
    }
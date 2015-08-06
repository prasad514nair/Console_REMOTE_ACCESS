package com.cavsusa.ccastconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DirectTvChannels extends DialogFragment {

	/** Called when the activity is first created. */
	/*@Override
	
	    // TODO Auto-generated method stub
	    
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	     // Get the layout inflater
		    LayoutInflater inflater = getActivity().getLayoutInflater();
		 // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(inflater.inflate(R.layout.choose_channel, null))
		    // Add action buttons
		           .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		                   // sign in the user ...
		               }
		           })
		           .setNegativeButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   //LoginDialogFragment.this.getDialog().cancel();
		               }
		           });      
		    return builder.create();
	        builder.setMessage(R.string.app_name)
	               .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                   }
	               })
	               .setNegativeButton(R.string.app_name, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }*/
	
	/*private static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		
		public EfficientAdapter(Context context) {
		mInflater = LayoutInflater.from(context);

		}

		public int getCount() {
		return country.length;
		}

		public Object getItem(int position) {
		return position;
		}

		public long getItemId(int position) {
		return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
		convertView = mInflater.inflate(R.layout.direct_tv_channels_row, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView
		.findViewById(R.id.DirectTvChannelNumber);
		holder.text2 = (TextView) convertView
		.findViewById(R.id.DirectTvChannelName);

		convertView.setTag(holder);
		} else {
		holder = (ViewHolder) convertView.getTag();
		}

		holder.text.setText(curr[position]);
		holder.text2.setText(country[position]);

		return convertView;
		}

		static class ViewHolder {
		TextView text;
		TextView text2;
		}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direct_tv_channels);
		ListView l1 = (ListView) findViewById(R.id.DirectTvChannelsListView);
		l1.setAdapter(new EfficientAdapter(this));
		}*/

		private static final String[] country = { "Iceland", "India", "Indonesia",
		"Iran", "Iraq", "Ireland", "Israel", "Italy", "Laos", "Latvia",
		"Lebanon", "Lesotho ", "Liberia", "Libya", "Lithuania",
		"Luxembourg" };
		private static final String[] curr = { "ISK", "INR", "IDR", "IRR", "IQD",
		"EUR", "ILS", "EUR", "LAK", "LVL", "LBP", "LSL ", "LRD", "LYD",
		"LTL ", "EUR"

		};
		
		private ListViewAdapter mAdapter;
		private ListView listView;
		
	    private ArrayList<String> channelName;
	    private ArrayList<String> channelNumber;
	    
	    
	    //JSON

	    private ProgressDialog pDialog;

		// URL to get contacts JSON
		private static String url = "http://api.androidhive.info/contacts/";

		// JSON Node names
		private static final String TAG_CONTACTS = "contacts";
		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_EMAIL = "email";
		private static final String TAG_ADDRESS = "address";
		private static final String TAG_GENDER = "gender";
		private static final String TAG_PHONE = "phone";
		private static final String TAG_PHONE_MOBILE = "mobile";
		private static final String TAG_PHONE_HOME = "home";
		private static final String TAG_PHONE_OFFICE = "office";

		// contacts JSONArray
		JSONArray contacts = null;

		// Hashmap for ListView
		ArrayList<HashMap<String, String>> contactList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//setContentView(R.layout.activity_main);

			contactList = new ArrayList<HashMap<String, String>>();

			// Calling async task to get json
			new GetContacts().execute();
		}

		/**
		 * Async task class to get json by making HTTP call
		 * */
		private class GetContacts extends AsyncTask<Void, Void, Void> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				//pDialog = new ProgressDialog(MainActivity.this);
				//pDialog.setMessage("Please wait...");
				//pDialog.setCancelable(false);
				//pDialog.show();

			}

			@Override
			protected Void doInBackground(Void... arg0) {
				// Creating service handler class instance
				DirectTvJsonServicehandler sh = new DirectTvJsonServicehandler();

				// Making a request to url and getting response
				String jsonStr = sh.makeServiceCall(url, DirectTvJsonServicehandler.GET);

				Log.d("Response: ", "> " + jsonStr);

				if (jsonStr != null) {
					try {
						JSONObject jsonObj = new JSONObject(jsonStr);
						
						// Getting JSON Array node
						contacts = jsonObj.getJSONArray(TAG_CONTACTS);

						// looping through All Contacts
						for (int i = 0; i < contacts.length(); i++) {
							JSONObject c = contacts.getJSONObject(i);
							
							String id = c.getString(TAG_ID);
							String name = c.getString(TAG_NAME);
							String email = c.getString(TAG_EMAIL);
							String address = c.getString(TAG_ADDRESS);
							String gender = c.getString(TAG_GENDER);

							// Phone node is JSON Object
							JSONObject phone = c.getJSONObject(TAG_PHONE);
							String mobile = phone.getString(TAG_PHONE_MOBILE);
							String home = phone.getString(TAG_PHONE_HOME);
							String office = phone.getString(TAG_PHONE_OFFICE);

							// tmp hashmap for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							contact.put(TAG_ID, id);
							contact.put(TAG_NAME, name);
							contact.put(TAG_EMAIL, email);
							contact.put(TAG_PHONE_MOBILE, mobile);

							// adding contact to contact list
							contactList.add(contact);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
/*				// Dismiss the progress dialog
				if (pDialog.isShowing())
					pDialog.dismiss();
				*//**
				 * Updating parsed JSON data into ListView
				 * *//*
				ListAdapter adapter = new SimpleAdapter(
						MainActivity.this, contactList,
						R.layout.list_item, new String[] { TAG_NAME, TAG_EMAIL,
								TAG_PHONE_MOBILE }, new int[] { R.id.name,
								R.id.email, R.id.mobile });

				setListAdapter(adapter);*/
			}

		}
	    
	    
	    
	    
	    
	    
	    
	    
	    
private void oneMoreJson()
{
	DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
	HttpPost httppost = new HttpPost(url);
	// Depends on your web service
	httppost.setHeader("Content-type", "application/json");

	InputStream inputStream = null;
	String result = null;
	try {
	    HttpResponse response = httpclient.execute(httppost);           
	    HttpEntity entity = response.getEntity();

	    inputStream = entity.getContent();
	    // json is UTF-8 by default
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    while ((line = reader.readLine()) != null)
	    {
	        sb.append(line + "\n");
	    }
	    result = sb.toString();
		JSONObject jObject = new JSONObject(result);
		String aJsonString = jObject.getString("STRINGNAME");
		channelName.add("Channel 4");
		Log.e("ServiceHandler", "Couldn't get any data from the url");
	} catch (Exception e) { 
	    // Oops
	}
	finally {
	    try{
	    	if(inputStream != null)inputStream.close();
	    }
	    catch(Exception squish)
	    {}
	}
}    
	    
	    
	    
		//DirectTvJsonServicehandler sh = new DirectTvJsonServicehandler();		
        //String jsonStr = sh.makeServiceCall(url, DirectTvJsonServicehandler.GET);
        //Log.d("Response: ", "> " + jsonStr);
	    
	    
	    

		//private HandleJSON obj;
        
        
		@Override
	    // TODO Auto-generated method stub
	    
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
			//MainActivity act = new MainActivity();
			//act.attachJsontoListView();
			 
            // Making a request to url and getting response


            
/*			       String finalUrl = url;
			       obj = new HandleJSON(finalUrl);
			       obj.fetchJSON();

			      // while(obj.parsingComplete);
			       channelName.add(Integer.toString(obj.getCode()));
			       channelNumber.add(obj.getQuery());*/
		    
			//LayoutInflater inflater = getActivity().getLayoutInflater();
		    channelName = new ArrayList<String>();
	        channelName.add("Channel 1");
	        channelName.add("Channel 2");
	        channelName.add("Channel 3");
	        
	        channelNumber = new ArrayList<String>();
	          channelNumber.add("Ch1");
	          channelNumber.add("Ch2");
	          channelNumber.add("Ch3");
		        oneMoreJson();		    
		 // prepared arraylist and passed it to the Adapter class
	        mAdapter = new ListViewAdapter(this, channelNumber, channelName);
	        

		    
			
			View v = getActivity().getLayoutInflater().inflate(R.layout.direct_tv_channels, null);
		    
	        listView = (ListView)v.findViewById(R.id.DirectTvChannelsListView);
	        listView.setAdapter(mAdapter);
			
//		    ArrayAdapter<String> adapterArray = new ArrayAdapter<String>(getActivity(),
//		            R.layout.direct_tv_channels_row, country);
		    

		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			builder.setView(v)
					    // Add action buttons
		           .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		                   // sign in the user ...
		               }
		           })
		           .setNegativeButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   //LoginDialogFragment.this.getDialog().cancel();
		               }
		           });
			
	        // Calling async task to get json
	       // new GetContacts().execute();
			

			
			
			
			
			
			
			
		    //mLocationList = (ListView)inflater.findViewById(R.id.DirectTvChannelsListView);

		    //final EfficientAdapter adapter = new EfficientAdapter(getActivity());
		    //mLocationList.setAdapter(adapter);



		    //builder.setTitle(getArguments().getInt("title") + "").setView(v);

		    return builder.create();
			
		    
		    
		    
			
/*		    final EfficientAdapter adapter = new EfficientAdapter(getActivity(), R.layout.direct_tv_channels_row,
		            accountMetadataFactory.getAccountsAsList());
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	     // Get the layout inflater
		 //   LayoutInflater inflater = getActivity().getLayoutInflater();
		    
		    
            builder.setTitle("Direct TV Channels List");
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.direct_tv_channels, null);
            builder.setView(dialogLayout);
		    
            ListView list = (ListView) dialogLayout.findViewById(R.id.DirectTvChannelsListView);
            list.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.direct_tv_channels_row, country), 
            //list.setAdapter(new ArrayAdapter<String>(activity, R.layout.direct_tv_channels_row, 
            //        activity.getResources().getStringArray(R.array.ageArray)));
		    
		 // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    //ListView v = (ListView) inflater.inflate(R.layout.direct_tv_channels, null, false);
		    //v.setAdapter(new EfficientAdapter(this));
		    
		  //  ListView list = (ListView) dialogLayout.findViewById(R.id.DirectTvChannelsListView);
		    //list.setAdapter(new ArrayAdapter<String>(activity, R.layout.dialoglist, 
		    //        activity.getResources().getStringArray(R.array.ageArray)));
		    list.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		                long arg3) {
		            editText.setText(activity.getResources().getStringArray(R.array.ageArray)[arg2]);
		            dismiss();
		        }
		    
		    builder.setView(inflater.inflate(R.layout.direct_tv_channels, null))
		    // Add action buttons
		           .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		                   // sign in the user ...
		               }
		           })
		           .setNegativeButton(R.string.app_name, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		                   //LoginDialogFragment.this.getDialog().cancel();
		               }
		           });      
		    return builder.create();*/
	    }

		
		
		
		
		 
	   /* *//**
	     * Async task class to get json by making HTTP call
	     * *//*
	    private class GetContacts extends AsyncTask<Void, Void, Void> {
	 
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            // Showing progress dialog
	            pDialog = new ProgressDialog(getActivity());
	            pDialog.setMessage("Please wait...");
	            pDialog.setCancelable(false);
	            pDialog.show();
	 
	        }
	 
	        @Override
	        protected Void doInBackground(Void... arg0) {
	            // Creating service handler class instance
	            DirectTvJsonServicehandler sh = new DirectTvJsonServicehandler();
	 
	            // Making a request to url and getting response
	            String jsonStr = sh.makeServiceCall(url, DirectTvJsonServicehandler.GET);
	 
	            Log.d("Response: ", "> " + jsonStr); 
	            if (jsonStr != null) {
	                try {
	                    JSONObject jsonObj = new JSONObject(jsonStr);
	                     
	                   // Getting JSON Array node
	                    JSONArray jsonMainNode = jsonObj.getJSONArray("");
	                    int lengthJsonArr = jsonMainNode.length();
	                    if(lengthJsonArr>1)
	                    {
	                    	for(int i=0; i < lengthJsonArr; i++) 
		                    {
		                    	JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
		                    	//int song_id        = Integer.parseInt(jsonChildNode.optString("song_id").toString());
		                    	channelName.add(jsonChildNode.optString("callsign").toString());
		                    	channelNumber.add(jsonChildNode.optString("major").toString());
		                        Log.i("JSON parse", jsonChildNode.optString("callsign").toString());
		                    }
	                    }
	                   // contacts = jsonObj.getJSONArray(TAG_CONTACTS);
	                 
	 
	                    // looping through All Contacts
	                      for (int i = 0; i < contacts.length(); i++) {
	                        JSONObject c = contacts.getJSONObject(i);
	                         Log.d("Response: ", "> " + c);
	                        
	                        String id = c.getString(TAG_ID);
	                        String name = c.getString(TAG_NAME);
	                        String email = c.getString(TAG_EMAIL);
	                        String address = c.getString(TAG_ADDRESS);
	                        String gender = c.getString(TAG_GENDER);
	 
	                        // Phone node is JSON Object
	                        JSONObject phone = c.getJSONObject(TAG_PHONE);
	                        String mobile = phone.getString(TAG_PHONE_MOBILE);
	                        String home = phone.getString(TAG_PHONE_HOME);
	                        String office = phone.getString(TAG_PHONE_OFFICE);
	 
	                        // tmp hashmap for single contact
	                        HashMap<String, String> contact = new HashMap<String, String>();
	 
	                        // adding each child node to HashMap key => value
	                        contact.put(TAG_ID, id);
	                        contact.put(TAG_NAME, name);
	                        contact.put(TAG_EMAIL, email);
	                        contact.put(TAG_PHONE_MOBILE, mobile);
	 
	                        // adding contact to contact list
	                        contactList.add(contact);
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                Log.e("ServiceHandler", "Couldn't get any data from the url");
	            }
	 
	            return null;
	        }
	 
	        @Override
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            // Dismiss the progress dialog
	            if (pDialog.isShowing())
	                pDialog.dismiss();
	            *//**
	             * Updating parsed JSON data into ListView
	             * *//*
	            ListAdapter adapter = new SimpleAdapter(
	                    getActivity(), contactList,
	                    R.layout.direct_tv_channels_row, new String[] { TAG_NAME, TAG_EMAIL }, 
	                    new int[] { R.id.DirectTvChannelNumber,
	                            R.id.DirectTvChannelName });
	 
	            View v = getActivity().getLayoutInflater().inflate(R.layout.direct_tv_channels, null);
	            ListView listView;
	            listView = (ListView)v.findViewById(R.id.DirectTvChannelsListView);
	            listView.setAdapter(adapter);
	          
	        }
	    }			*/
	}

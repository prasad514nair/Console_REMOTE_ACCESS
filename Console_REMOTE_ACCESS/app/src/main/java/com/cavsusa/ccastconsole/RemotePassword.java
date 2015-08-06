package com.cavsusa.ccastconsole;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RemotePassword extends Activity {
    private int channel_count;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.remote_password);

        Intent intent = getIntent();

//        channel_count = Integer.parseInt(intent.getStringExtra(MainActivity.channel_number));

  //      System.out.println("channel_count" + channel_count);


        final String  pass;
        final String retypepass;
        EditText old_password = (EditText) findViewById(R.id.oldPassword);
        TextView textoldPass = (TextView) findViewById(R.id.textoldPass);
        if ((MainActivity.channelNames.getString("serverPassword","")=="")){
            textoldPass.setVisibility(View.INVISIBLE);
            old_password.setVisibility(View.INVISIBLE);
        }
        final EditText p = (EditText) findViewById(R.id.NewServerPassword);
        pass=p.getText().toString();
        final EditText rp = (EditText) findViewById(R.id.ReTypeServerPassword);
        retypepass=rp.getText().toString();
        final TextView passerror =(TextView)findViewById(R.id.passerror);
      //  final Button apply =(Button)findViewById(R.id.buttonApply);
        rp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {


                if (p.getText().toString().equals(rp.getText().toString())) {
                    passerror.setVisibility(View.INVISIBLE);
                 //   apply.setClickable(true);
                }
                else{
                    passerror.setVisibility(View.VISIBLE);
                   // apply.setClickable(false);
                }


            }
        });
        p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (p.getText().toString().equals(rp.getText().toString())) {
                    passerror.setVisibility(View.INVISIBLE);
        //            apply.setClickable(true);
                }
                else{
                    passerror.setVisibility(View.VISIBLE);
          //          apply.setClickable(false);
                }


            }
        });
    }




    //-----------------------------Send Remote Access  AsyncTask Server Data Class---------------------------------------------
    private class AddServer extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            //	super.onPostExecute(result);

            try {
                String message= result.getString("message");



                JSONObject data = result.getJSONObject("data");
                Log.d("OnPostexecute: ", "> " + data.getInt("serverid"));
                Log.d("OnPostexecute: ", "> " + data.getString("name"));

                //	MainActivity.updateServerInformation();

                SharedPreferences.Editor editor = MainActivity.channelNames.edit();
                editor.putInt("serverID", data.getInt("serverid"));
                editor.putString("serverPassword", data.getString("pass"));
                editor.commit();


                int userId = MainActivity.channelNames.getInt("serverID", 0);
                Log.d("SharedPchannelNames: ", "> " + MainActivity.channelNames.getInt("serverID", 0));
                Log.d("SharedPreferences prefs: ", "> " + userId);
                Toast.makeText(RemotePassword.this,"Remote Server created Successfully",Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d("ErrorOnPostexecute: ", "> " +e);
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d("PARAMS: ", "> " +params[0]);
            JSONObject result= new JSONObject();
            HttpClient hc = new DefaultHttpClient();
            String authString = params[0]+ ":" + params[1];
            //	    String authStringEnc = new String(Base64.encodeToString(authString.getBytes(),Base64.DEFAULT));
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            //	    Log.d("In add_sever header: ", "> " +authStringEnc);

            String message;
            String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/";
            HttpPost p = new HttpPost(url);
            HttpEntity entity = null;
            BufferedReader reader;


            JSONObject object = new JSONObject();
            //  JSONObject body = new JSONObject();
            try {

                object.put("timezone", "Europe/Berlin");
                int mono = MainActivity.servInfoGetAudioModeMono();
                if(mono==0) {
                    object.put("mode", "stereo");
                    if (MainActivity.servInfoGetChannelCount() == 20)
                        object.put("model", "WAD-120");
                    else if (MainActivity.servInfoGetChannelCount() == 10)
                        object.put("model", "WAD-110");
                    else if (MainActivity.servInfoGetChannelCount() == 4)
                        object.put("model", "WAD-104");
                }
                else {
                    object.put("mode", "mono");
                    if (MainActivity.servInfoGetChannelCount() == 40)
                        object.put("model", "WAD-120");
                    else if (MainActivity.servInfoGetChannelCount() == 20)
                        object.put("model", "WAD-110");
                    else if (MainActivity.servInfoGetChannelCount() == 8)
                        object.put("model", "WAD-104");
                }
                //  body.put("body", object);
                Log.d("JSONObject","object created");
            } catch (Exception ex) {
                Log.d("JSONExceptionObject",ex.toString());
            }

            try {
                message = object.toString();


                p.setEntity(new StringEntity(message, "UTF8"));
                p.setHeader("Content-type", "application/json");
                p.setHeader("Authorization", basicAuth);
                Log.d("JSONMessage","Message created");
                HttpResponse resp = hc.execute(p);
                Log.d("JSONMessageSend","Message send");
                if (resp != null) {
                    if (resp.getStatusLine().getStatusCode() == 200)
                        // result = true;

                        entity = resp.getEntity();
                    reader = new BufferedReader(new InputStreamReader(entity.getContent()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp="";
                    while((tmp=reader.readLine())!=null)
                        json.append(tmp).append("\n");
                    reader.close();

                    JSONObject dataresp = new JSONObject(json.toString());
                    result=dataresp;
                    // This value will be 404 if the request was not
                    // successful


                }

                Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("JSONExceptionOuter",e.toString());

            }


            return result;

        }


    }
//----------------------------End of Send Remote Access  AsyncTask Create Server-----------------------------------------------------------------

    //-----------------------------Send Remote Access  Change Servername and Password---------------------------------------------
    private class ChangeServerAuth extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            //	super.onPostExecute(result);
            try {
                String message =result.getString("message");


                JSONObject data = result.getJSONObject("data");
                Log.d("CSAOnPostexecute: ", "> " + data.getString("name"));
                Log.d("CSAOnPostexecute: ", "> " + data.getString("pass"));
                //		MainActivity.kServerID= data.getInt("serverid");

                //		EditText t = (EditText) findViewById(R.id.editServerName);

                //	updateServerInformation();

                SharedPreferences.Editor editor = MainActivity.channelNames.edit();

                editor.putString("Password", data.getString("pass"));
                editor.commit();
                Toast.makeText(RemotePassword.this,"Remote Server updated successfully",Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d("CSAErrorOnPostexecute: ", "> " +e);
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d("JSON CSAPARAMS: ", "> " +params[0]);
            Log.d("JSON CSAPARAMS: ", "> " +params[1]);
            JSONObject result= new JSONObject();
            HttpClient hc = new DefaultHttpClient();
            String authString = params[0]+ ":" + MainActivity.channelNames.getString("serverPassword","");
            //	    String authStringEnc = new String(Base64.encodeToString(authString.getBytes(),Base64.DEFAULT));
            final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            //	    Log.d("In add_sever header: ", "> " +authStringEnc);

            String message;
            Log.d("JSON CSAserverid): ", "> " +MainActivity.channelNames.getInt("serverID", 0));
            int SID = MainActivity.channelNames.getInt("serverID", 0);
            Log.d("CSASID: ", "> " +SID);
            String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID;
            HttpPut p = new HttpPut(url);
            HttpEntity entity = null;
            BufferedReader reader;


            JSONObject object = new JSONObject();
            //  JSONObject body = new JSONObject();
            try {

                //	object.put("newpass", MainActivity.channelNames.getString("serverPassword",""));
                object.put("newpass", params[1]);
                object.put("updateserver", 1);
                object.put("newname", params[0]);

                //  body.put("body", object);
                Log.d("JSON CSAJSONObject","object created");

                message = object.toString();
                Log.d("JSON CSAJSONMessage",message);

                p.setEntity(new StringEntity(message, "UTF8"));
                p.setHeader("Content-type", "application/json");
                p.setHeader("Authorization", basicAuth);
                Log.d("JSON CSAJSONMessage","Message created");
                HttpResponse resp = hc.execute(p);
                Log.d("CSAJSONMessageSend","Message send");
                if (resp != null) {
                    if (resp.getStatusLine().getStatusCode() == 200)
                        // result = true;

                        entity = resp.getEntity();
                    reader = new BufferedReader(new InputStreamReader(entity.getContent()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp="";
                    while((tmp=reader.readLine())!=null)
                        json.append(tmp).append("\n");
                    reader.close();

                    JSONObject dataresp = new JSONObject(json.toString());
                    result=dataresp;
                    // This value will be 404 if the request was not
                    // successful


                }

                Log.d("JSON Status line", "" + resp.getStatusLine().getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("CSAJSONExceptionOuter",e.toString());

            }


            return result;

        }


    }
//----------------------------End of Send Remote Access  Change Servername and Password-----------------------------------------------------------------

    public void cancelRemoteAccess(View view) {
        finish();
    }


    public void connectRemoteAccess(View view) {

        int dirty, n;

        int channels = MainActivity.servInfoGetChannelCount();

        System.out.println(" channels updatesysinfo" + channels);

        String str;

        EditText t = (EditText) findViewById(R.id.oldPassword);

        str = t.getText().toString();

        String pass;

        EditText p = (EditText) findViewById(R.id.NewServerPassword);

        pass = p.getText().toString();
        String retypepass;
        EditText rp = (EditText) findViewById(R.id.ReTypeServerPassword);
        retypepass = rp.getText().toString();
//-----------------------------Send Remote Access Create Server-----------------------------------------------


        // TODO Auto-generated method stub
        //	EditText servername =(EditText) findViewById(R.id.editServerName);
        String servername = MainActivity.servInfoGetServerName();
        TextView old_password = (TextView) findViewById(R.id.oldPassword);
        TextView password = (TextView) findViewById(R.id.NewServerPassword);
        Log.d("Editor Server name : ", "> " + MainActivity.channelNames.getString("serverName", ""));
        Log.d("Textfield Password : ", "> " + password.getText().toString());

        Log.d("JSON servername : ", "> " + (MainActivity.channelNames.getString("serverName", "") == ""));
        Log.d("JSON passservername : ", "> " + (MainActivity.channelNames.getString("serverPassword", "") == ""));
        if ((MainActivity.channelNames.getString("serverPassword","")=="")){


            new AddServer().execute(servername, password.getText().toString());
        }
        else {
            Log.d("JSON passservername : ", "> " + (MainActivity.channelNames.getString("serverPassword", "") ));
            Log.d("JSON old_password : ", "> " + (old_password.getText().toString()));
            if(old_password.getText().toString().equals(MainActivity.channelNames.getString("serverPassword", ""))) {
                Log.d("JSON Paswords : ", "> INSIDEEEEE");
                new ChangeServerAuth().execute(servername, password.getText().toString());
            }
        }

        //-------------------------------------------------------------------------------------------------------------

        finish();

        //System.out.println("channels" + MainActivity.servInfoGetChannelName(3));



    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.edit_channels, menu);

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


package com.cavsusa.ccastconsole;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



public class EditChannels extends Activity {



	private int channel_count;

	//SharedPreferences channelNames;

	private int	mnUpdateServerInfoTimeout;

	public final static int	kTimerTick				= 300;	// in msec


	private final int	mIdsLabel[] = {
			R.id.textStatus, R.id.TextView02, R.id.TextView03, R.id.TextView04, R.id.TextView05, R.id.TextView06, R.id.TextView07, R.id.TextView08,	
			R.id.TextView09, R.id.TextView10, R.id.TextView11, R.id.TextView12, R.id.TextView13, R.id.TextView14, R.id.TextView15, R.id.TextView16,	
			R.id.TextView17, R.id.TextView18, R.id.TextView19, R.id.TextView20,
			R.id.TextView21, R.id.TextView22, R.id.TextView23, R.id.TextView24, R.id.TextView25, R.id.TextView26, R.id.TextView27, R.id.TextView28,	
			R.id.TextView29, R.id.TextView30, R.id.TextView31, R.id.TextView32, R.id.TextView33, R.id.TextView34, R.id.TextView35, R.id.TextView36,	
			R.id.TextView37, R.id.TextView38, R.id.TextView39, R.id.TextView40
//			R.id.TextView21, R.id.TextView22, R.id.TextView23, R.id.TextView24,	
		};
	
	private final int	mIdsName[] = {
			R.id.EditChannel01, R.id.EditChannel02, R.id.EditChannel03, R.id.EditChannel04, R.id.EditChannel05, R.id.EditChannel06, R.id.EditChannel07, R.id.EditChannel08,	
			R.id.EditChannel09, R.id.EditChannel10, R.id.EditChannel11, R.id.EditChannel12, R.id.EditChannel13, R.id.EditChannel14, R.id.EditChannel15, R.id.EditChannel16,	
			R.id.EditChannel17, R.id.EditChannel18, R.id.EditChannel19, R.id.EditChannel20,
			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24, R.id.EditChannel25, R.id.EditChannel26, R.id.EditChannel27, R.id.EditChannel28,	
			R.id.EditChannel29, R.id.EditChannel30, R.id.EditChannel31, R.id.EditChannel32, R.id.EditChannel33, R.id.EditChannel34, R.id.EditChannel35, R.id.EditChannel36,	
			R.id.EditChannel37, R.id.EditChannel38, R.id.EditChannel39, R.id.EditChannel40
//			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24,	
		};

	

	@Override

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_channels);

		Intent intent = getIntent();

		channel_count = Integer.parseInt(intent.getStringExtra(MainActivity.channel_number));

		System.out.println("channel_count" + channel_count);

		modifySystemView();

	}

	

	

	private void modifySystemView() {

		int n, channels;

		EditText t;

		String label;

	

		channels= channel_count;

		System.out.println("channels" + channels);

		for (n = 0; n < channels; n++) {

			enableEntry(true, mIdsLabel[n], mIdsName[n]);

			t = (EditText) findViewById(mIdsName[n]);

			t.setFocusable(true);

			label = MainActivity.servInfoGetChannelName(n);

			System.out.println("channels in modify system view" + label + "n" + n);

			t.setText(label);

		}

		for ( ; n < 40; n++)

			enableEntry(false, mIdsLabel[n], mIdsName[n]);



		t = (EditText) findViewById(R.id.editServerName);

		label = MainActivity.servInfoGetServerName();

		if(label!= null)

			t.setText(label);

		

	}

	

	private void enableEntry(boolean bShow, int idLabel, int idName) {

		View vw;

		vw = findViewById(idLabel);

		if (vw != null) {

			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);

		}

		vw = findViewById(idName);

		if (vw != null) {

			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);

		}

	}



	public void updateChannelInfo(View view) {

		int dirty, n;

		int channels = MainActivity.servInfoGetChannelCount();

		System.out.println(" channels updatesysinfo" + channels);

		String str;

		EditText t = (EditText) findViewById(R.id.editServerName);

		str = t.getText().toString();

		dirty = MainActivity.servInfoSetServerName(str);

		

		SharedPreferences.Editor editor = MainActivity.channelNames.edit();

	      editor.putString("serverName", str);

	      editor.commit();



		for (n = 0; n < channels; n++) {

			t = (EditText) findViewById(mIdsName[n]);

			System.out.println("t" + t.getText().toString());

			if (t.getText().toString() != "\t") {

				str = t.getText().toString();

				dirty += MainActivity.servInfoSetChannelName(n, str);

			    editor.putString("channel"+n, str);

			   editor.commit();

			}else{

				if(n%2 == 1){

					int ch_temp = n;

					ch_temp--;

					t = (EditText) findViewById(mIdsName[ch_temp]);

					if (t != null) {

						str = t.getText().toString();

						dirty += MainActivity.servInfoSetChannelName(n, str);



				      editor.putString("channel"+n, str);

				      editor.commit();

					}

					

				}

			}

		}

		
		System.out.println("editor" + MainActivity.channelNames);
		mnUpdateServerInfoTimeout = 5000 / kTimerTick;
		
		for(int i = 0 ; i< channels; i++){
			System.out.println(MainActivity.channelNames.getString("channel" + i, " "));
			MainActivity.servInfoSetChannelName(i,MainActivity.channelNames.getString("channel" + i, " "));
		}
		
		MainActivity.servInfoV2Update();
		
			//mnUpdateServerInfoTimeout--;
			//if (mnUpdateServerInfoTimeout == 0) {
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setNegativeButton("Dismiss", null);

		builder.setMessage("Update Successfull");

		AlertDialog alert = builder.create();

		alert.show();		
			//}
		

		
		
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


package com.cavsusa.ccastconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;



public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{	
	private boolean	mbRelease = true;
	ccastConsoleApp	mApp;
	private Timer	mTimer;
	private boolean	mbBackPressed;
	private PowerManager.WakeLock	mWl = null;
	private WifiManager.WifiLock	mWifiLock = null;
	private boolean first_iteration = true;

	static WifiManager	mwifiManager;
	ArrayAdapter <String>	net_array_adapter;
	List <ScanResult> wlanList;
	
	private boolean flag = false;
	private boolean OnTyping = false;
	private Random	mRandom;
	private Fragment	monitorSect;
	private Fragment	systemSect;
	private Fragment	directTvSect;
	private ViewFlipper sysEditFlipper;
	
	private int	mnSystemControl_PowerOff;
	private boolean	mbMonitorViewUpdated;
	private int	mnReceiveServerInfoTimeout;
	private int	mnControlServerTimeout;
	private int	mnUpdateServerInfoTimeout;
	private boolean	mbServerInfo;
	private int	mnPreviousChannel;
	private MainHandler	mHandler;
	private final Handler updateHandler= new Handler();
	private ProgressDialog	mDlgProgress;

	private boolean	mbInitialized;
	private static int audiomode ;
	public static int kServerID ;
	public final static String channel_number = "com.cinet.wad.MESSAGE";
	private static final String	strPrivateSSID1 = "Top Audio";
	private static final String	strPrivateSSID2 = "abCiNet";
	private static final String TAG = "CCAST";

	public final static int	kTimerTick				= 300;	// in msec
	public final static int	refreshTime				= 15000;	// in msec
	public final static int	kMonitorSection			= 1;
	public final static int	kSystemSection			= 0;
	
	public final static int	kDirectTvSection		= 2;
	public final static int	kMsgRefreshStatus		= 0x0001;
	public final static int	kMsgServerInfoTimeout	= 0x0010;
	public final static int	kMsgServerInfoAvailable	= 0x0011;
	public final static int	kMsgServerInfoAvailable2	= 0x0051;
	public final static int	kMsgServerUpdateTimeout	= 0x0020;
	public final static int	kMsgServerUpdated		= 0x0021;
	public final static int	kMsgServerControlled	= 0x0031;
	ArrayList<Integer> visited = new ArrayList<Integer>(Collections.nCopies(40, 0));
	
	//Direct TV
	public static final String TEMP_PREFS_NAME = "tempIpList";
	public static final String PREFS_NAME = "IpList";
	public static final String direct_mono = "IpListmono";
	public static final String CHANNEL_NAMES_FILE = "ChannelNames";
	public static final String TEMP_CHANNEL_NAMES_FILE = "TempChannelNames";
	SharedPreferences settings;
	SharedPreferences temp_settings;
	SharedPreferences directTv_mono;
	SharedPreferences temp_channelNames;
	static SharedPreferences channelNames;
		static SharedPreferences serverID;
	public boolean watching = false;
	public int get_modified = 0;
	public int put_modified = 0;
	public int power = 0;
	public int reboot = 0;
	public int thread_flag = 0;
	public int get_value_flag = 0;
	public int put_value_flag = 0;
	public int power_reboot_flag = 0;
//Web Console app
    String message;
    JSONObject result;
	//
	
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
	
	private final int	mIdsLabelIp[] = {
			R.id.textStatusIp, R.id.TextViewIp02, R.id.TextViewIp03, R.id.TextViewIp04, R.id.TextViewIp05, R.id.TextViewIp06, R.id.TextViewIp07, R.id.TextViewIp08,	
			R.id.TextViewIp09, R.id.TextViewIp10, R.id.TextViewIp11, R.id.TextViewIp12, R.id.TextViewIp13, R.id.TextViewIp14, R.id.TextViewIp15, R.id.TextViewIp16,	
			R.id.TextViewIp17, R.id.TextViewIp18, R.id.TextViewIp19, R.id.TextViewIp20,
			R.id.TextViewIp21, R.id.TextViewIp22, R.id.TextViewIp23, R.id.TextViewIp24, R.id.TextViewIp25, R.id.TextViewIp26, R.id.TextViewIp27, R.id.TextViewIp28,	
			R.id.TextViewIp29, R.id.TextViewIp30, R.id.TextViewIp31, R.id.TextViewIp32, R.id.TextViewIp33, R.id.TextViewIp34, R.id.TextViewIp35, R.id.TextViewIp36,	
			R.id.TextViewIp37, R.id.TextViewIp38, R.id.TextViewIp39, R.id.TextViewIp40,
//			R.id.TextView21, R.id.TextView22, R.id.TextView23, R.id.TextView24,	
		};
	
	private final int	mIdsIp[] = {
			R.id.EditIp01, R.id.EditIp02, R.id.EditIp03, R.id.EditIp04, R.id.EditIp05, R.id.EditIp06, R.id.EditIp07, R.id.EditIp08,	
			R.id.EditIp09, R.id.EditIp10, R.id.EditIp11, R.id.EditIp12, R.id.EditIp13, R.id.EditIp14, R.id.EditIp15, R.id.EditIp16,	
			R.id.EditIp17, R.id.EditIp18, R.id.EditIp19, R.id.EditIp20,
			R.id.EditIp21, R.id.EditIp22, R.id.EditIp23, R.id.EditIp24, R.id.EditIp25, R.id.EditIp26, R.id.EditIp27, R.id.EditIp28,	
			R.id.EditIp29, R.id.EditIp30, R.id.EditIp31, R.id.EditIp32, R.id.EditIp33, R.id.EditIp34, R.id.EditIp35, R.id.EditIp36,	
			R.id.EditIp37, R.id.EditIp38, R.id.EditIp39, R.id.EditIp40,
//			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24,	
		};
	
	private final boolean mIdsBool[] = {
			false, false, false, false, false, false, false, false,	
			false, false, false, false, false, false, false, false,	
			false, false, false, false,
			false, false, false, false, false, false, false, false,	
			false, false, false, false, false, false, false, false,	
			false, false, false, false
//			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24,	
		};
	
	private final String mIdsChannelName[] = {
			null, null, null, null, null, null, null, null,	
			null, null, null, null, null, null, null, null,	
			null, null, null, null,
			null, null, null, null, null, null, null, null,	
			null, null, null, null, null, null, null, null,	
			null, null, null, null
//			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24,	
		};
	
	private final String mIdsTitleName[] = {
			null, null, null, null, null, null, null, null,	
			null, null, null, null, null, null, null, null,	
			null, null, null, null,
			null, null, null, null, null, null, null, null,	
			null, null, null, null, null, null, null, null,	
			null, null, null, null
//			R.id.EditChannel21, R.id.EditChannel22, R.id.EditChannel23, R.id.EditChannel24,	
		};


	class LocalTimerTask extends TimerTask {
		public void run() {
			if (mbInitialized == false)
				return;

			mHandler.sendMessage(mHandler.obtainMessage(kMsgRefreshStatus));

			if (ccnetServerFound() && mnPreviousChannel < 0) {
				ccnetSetChannel(0);
				mnPreviousChannel = 0;
			}

			if (mnReceiveServerInfoTimeout > 0) {
				if (servInfoValid()) {
					mnReceiveServerInfoTimeout = 0;
					mHandler.sendMessage(mHandler.obtainMessage(kMsgServerInfoAvailable));
				}
				else {
					Log.e("server info", "server information is invalid");
					mnReceiveServerInfoTimeout--;
					if (mnReceiveServerInfoTimeout == 0) {
						mHandler.sendMessage(mHandler.obtainMessage(kMsgServerInfoTimeout));
					}
				}
			}

			if (mnUpdateServerInfoTimeout > 0) {
				if (servInfoUpdateStat() != 0) {
					mnUpdateServerInfoTimeout = 0;
					mHandler.sendMessage(mHandler.obtainMessage(kMsgServerUpdated));
				}
				else {
					mnUpdateServerInfoTimeout--;
					if (mnUpdateServerInfoTimeout == 0) {
						mHandler.sendMessage(mHandler.obtainMessage(kMsgServerUpdateTimeout));
					}
				}
			}

			if (mnControlServerTimeout > 0) {
				if (servInfoUpdateStat() != 0) {
					mnControlServerTimeout = 0;
					mHandler.sendMessage(mHandler.obtainMessage(kMsgServerControlled));
				}
				else {
					mnControlServerTimeout--;
					if (mnControlServerTimeout == 0) {
						mHandler.sendMessage(mHandler.obtainMessage(kMsgServerUpdateTimeout));
					}
				}
			}
		}
	}
	
	class SystemTimerTask extends TimerTask {
		public void run() {
			mHandler.sendMessage(mHandler.obtainMessage(kMsgServerInfoAvailable2));
		}
	};

	private PanelGLSurfaceView mPanelView;
	
	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			Log.d(TAG, "finished WIFI scan.");
			List <ScanResult> wifiList = mwifiManager.getScanResults();
			for(int i = 0; i < wifiList.size(); i++){
			}
		}
	}
	
	/*
	 * 	setupWLAN
	 *		connect AP automatically
	 */
	private void setupWLAN(WifiManager wm) {
		WifiInfo wi = wm.getConnectionInfo();

//		Log.d(TAG, "wifi enabled " + wi);

		String ssid = wi.getSSID();

		if (ssid != null) {
			if (ssid.contains(strPrivateSSID1))
				return;
			if (ssid.contains(strPrivateSSID2))
				return;
		}

		wm.startScan();

		//Changed by Anton... Program didn't start correctly. 5.6.2014
		/*try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		// check ssid
		boolean found = false;
		int netId = -1;
		List <WifiConfiguration> list = wm.getConfiguredNetworks();

		for (WifiConfiguration c : list) {
			//Changed by Anton... Program didn't start correctly. 5.6.2014
			//if (c.SSID != null && c.SSID.contains(strPrivateSSID1)) 
			//{
				found = true;
				netId = c.networkId;
			//Changed by Anton... Program didn't start correctly. 5.6.2014
			//}
			//else if (c.SSID != null && c.SSID.contains(strPrivateSSID2)) {
			//	found = true;
			//	netId = c.networkId;
			//}
		}

		if (found == true) {
			wm.disconnect();
			wm.enableNetwork(netId, true);
			wm.reconnect();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setNegativeButton("Close", null);
			builder.setMessage("Can not detect CiNet.com Server.");
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	//Temporarily hide this method to fix some bugs. Edited by Anton.
	private void checkCinetWifiConnection() {
		int n;
		boolean wifiEnabled;

		ProgressBar waitCursor = (ProgressBar) findViewById(R.id.progressBar1);

		mwifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiReceiver receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		wifiEnabled = false;

		if (mwifiManager != null) {
			//Temporarily hide this method to fix some bugs. Edited by Anton.
			/*wifiEnabled = mwifiManager.isWifiEnabled();
			if (wifiEnabled != true) {
				waitCursor.setVisibility(View.VISIBLE);

				mwifiManager.setWifiEnabled(true);
				for (n = 0; n < 10; n++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				waitCursor.setVisibility(View.INVISIBLE);
			}

			setupWLAN(mwifiManager);

			int ip = mwifiManager.getConnectionInfo().getIpAddress();
			MulticastLock mcLock = mwifiManager.createMulticastLock("ccast");
			mcLock.acquire();
			Log.d(TAG, "ip " + ip);*/
		}
		else {
			Log.d(TAG, "failed to get wifi manager.");

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setNegativeButton("Close", null);
			builder.setMessage("Can not detect WiFi.");
			AlertDialog alert = builder.create();
			alert.show();
		}

		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifiState = conMan.getNetworkInfo(1).getState(); //wifi

		int delay = 20;

		while (delay > 0 && wifiState != NetworkInfo.State.CONNECTED && wifiState != NetworkInfo.State.CONNECTING) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wifiState = conMan.getNetworkInfo(1).getState(); //wifi
			delay--;
		}

		if (wifiState == NetworkInfo.State.CONNECTED || wifiState == NetworkInfo.State.CONNECTING) {
			Log.i(TAG , "WIFI is ON.");
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setNegativeButton("Close", null);
			builder.setMessage("turn on WiFi.");
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	Timer directTvTimer = new Timer();
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    MenuItem item= menu.findItem(R.id.menu_settings);
	    item.setVisible(false);
	    return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//		setProgressBarIndeterminateVisibility(false);
		
/*        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            System.out.println("*** My thread is now configured to allow connection");
        }
        */
		setContentView(R.layout.activity_main);
		
		
		Log.d(TAG, "Create");

		mApp = (ccastConsoleApp)getApplicationContext();

		mbInitialized = false;
		mWl = null;
		mWifiLock = null;

		ProgressBar waitCursor = (ProgressBar) findViewById(R.id.progressBar1);

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_direct_tv).setTabListener(this));
//		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3).setTabListener(this));

		mPanelView = new PanelGLSurfaceView(this);

	//
	// check wifi connection
	//
		if (mbRelease == true) {
			checkCinetWifiConnection();
		}

		waitCursor.setVisibility(View.GONE);
		System.out.println("audio mode" + servInfoGetAudioModeMono());
		System.out.println("channels" + servInfoGetChannelCount());
		
		mHandler = new MainHandler(this);

		mbServerInfo = false;
		mnPreviousChannel = -1;
		mnReceiveServerInfoTimeout = 0;
		mnUpdateServerInfoTimeout = 0;

		mRandom = new Random();

		mTimer = new Timer();
		LocalTimerTask myTimerTask = new LocalTimerTask();
		mTimer.schedule(myTimerTask, 100, kTimerTick);
		
		SystemTimerTask systemTimerTask =new SystemTimerTask();
		mTimer.schedule(systemTimerTask, 0, refreshTime);

		
		if (checkScreenResolution() != true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setNegativeButton("Close", null);
			builder.setMessage("this App requires 1024x700 screen.");
			AlertDialog alert = builder.create();
			alert.show();
		}
		System.out.println("mIdsName.length" + mIdsName.length);
		
		
		
		//DIRECT TV
		// Calling async task to get json
		settings = getSharedPreferences(PREFS_NAME, 0);
		//clear channel first
		channelNames = getSharedPreferences(CHANNEL_NAMES_FILE, 0);
		//clear channel name firstly
		Map<String, ?> temp1 = settings.getAll();
		System.out.println("in select audio mode settings" + temp1);
		channelNames.edit().clear().commit();
		new GetDirectTv().execute();
		
		if(flag == false){
		directTvTimer.schedule( new TimerTask() {
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if(getActionBar().getSelectedTab().getPosition() == kSystemSection && mbBackPressed != true){
									if(first_iteration == true){
										//modifySystemView();
										refreshChannelNames();
										first_iteration = false;
									}else{
										new GetDirectTv().execute();
										refreshChannelNames();
										updateServerInformation();
									}
								}else if(getActionBar().getSelectedTab().getPosition() == kMonitorSection && mbBackPressed != true){
									new GetDirectTv().execute();
									refreshChannelNames();
									updateServerInformation();
								}
							}
						});
					}
				}, 1*1000, 3*1000); 
		}
	}
	
	public void popup(View view){
		updateHandler.removeCallbacks(sendUpdatetoRemote);
		Intent intent = new Intent(this, EditChannels.class);
		intent.putExtra(channel_number, Integer.toString(servInfoGetChannelCount()));
	   	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);

	//	setupRemoteHandler();
	}

//-----------------------------------Prasad Changes Begins-------------------------------------------------------
	public void remote_access_popup(View view){
		updateHandler.removeCallbacks(sendUpdatetoRemote);
		Intent intent = new Intent(this, RemotePassword.class);
	//	intent.putExtra(channel_number, Integer.toString(servInfoGetChannelCount()));
	//	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		setupRemoteHandler();
	}
//-----------------------------------Prasad Changes Ends-------------------------------------------------------
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void onPressedMessage() {
		Toast.makeText(this, "Pressed msg", Toast.LENGTH_LONG).show();
	}
	
	private void setOnTyping(){
		OnTyping = true;
	}

	
	private float initialX;

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		ActionBar.Tab tab = getActionBar().getSelectedTab();

		if ((tab.getPosition() != kSystemSection) && (tab.getPosition() != kDirectTvSection))
			return false;

		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initialX = touchevent.getX();
			break;
		case MotionEvent.ACTION_UP:
			float finalX = touchevent.getX();
			if (initialX > finalX) {
				if (sysEditFlipper.getDisplayedChild() == 1)
					break;
				sysEditFlipper.showNext();
			}
			else {
				if (sysEditFlipper.getDisplayedChild() == 0)
					break;
				sysEditFlipper.showPrevious();
			}
			break;
		}
		return false;
	}
	private int[] setFlags={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private boolean serverNameselected=false;
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the container view.
		//init setFlags
		for(int i=0;i<setFlags.length;i++){
			setFlags[i]=0;
		}
		//init serverName
		serverNameselected=false;
		Fragment fragment;

		if (tab.getPosition() == kMonitorSection) {
			updateHandler.removeCallbacks(sendUpdatetoRemote);
			fragment = new MonitorSectFragment();
			monitorSect = fragment;
			mbMonitorViewUpdated = false;
		}
		else if (tab.getPosition() == kSystemSection) {
			updateHandler.removeCallbacks(sendUpdatetoRemote);

			fragment = new SystemSectFragment();
			systemSect = fragment;
			((SystemSectFragment)fragment).version = mApp.version;
			getWindow().getDecorView().getRootView().setVisibility(View.VISIBLE);
			setupRemoteHandler();
//			getWindow().getDecorView().getRootView().setVisibility(View.INVISIBLE);
		}
		else if (tab.getPosition() == kDirectTvSection) {
			updateHandler.removeCallbacks(sendUpdatetoRemote);
			fragment = new DirectTvSectFragment();
			directTvSect = fragment;
			((DirectTvSectFragment)fragment).version = mApp.version;
			setupRemoteHandler();
		}
		else {
			updateHandler.removeCallbacks(sendUpdatetoRemote);
			fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
			fragment.setArguments(args);
			getWindow().getDecorView().getRootView().setVisibility(View.VISIBLE);
			setupRemoteHandler();
		}

		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
//			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			textView.setText(" ");
			return textView;
		}
	}

	public static class MonitorSectFragment extends Fragment {
		public MonitorSectFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.monitoring, null);
			return fl;
		}
		
		@Override
		public void onResume() {
			super.onResume();

			MainActivity act = (MainActivity) getActivity();
			act.startDirectTv();
		}
	}

	public static class SystemSectFragment extends Fragment {
		public int	version;

		public SystemSectFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			FrameLayout fl;
//			private static ccastConsoleApp app = (ccastConsoleApp)getApplicationContext();
			if (version >= 2)
				fl = (FrameLayout) inflater.inflate(R.layout.sys_edit, null);
			else
				fl = (FrameLayout) inflater.inflate(R.layout.system, null);
				//fl = (FrameLayout) inflater.inflate(R.layout.edit_direct_tv, null);
			return fl;
		}
		
		
		@Override
		public void onResume() {
			super.onResume();
			MainActivity act = (MainActivity) getActivity();
			act.startSystemEdit();
		}

	}
	
	public static class DirectTvSectFragment extends Fragment {
		public int	version;

		public DirectTvSectFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			FrameLayout fl;
//			private static ccastConsoleApp app = (ccastConsoleApp)getApplicationContext();	
			if (version >= 2)
			{
				fl = (FrameLayout) inflater.inflate(R.layout.edit_direct_tv, null);
			}
			else
			{
				fl = (FrameLayout) inflater.inflate(R.layout.edit_direct_tv, null);
			}

			return fl;
		}
		@Override
		public void onResume() {
			super.onResume();

			MainActivity act = (MainActivity) getActivity();
			act.startDirectTv();
		}

	}

	public void startSystemEdit() {
		mDlgProgress = ProgressDialog.show(this, null, "Request system information...", true);
		mnReceiveServerInfoTimeout = 5000 / kTimerTick;
		servInfoV2Request();
	}
	
	public void startDirectTv() {
		mnReceiveServerInfoTimeout = 5000 / kTimerTick;
		servInfoV2Request();
	}


	public native String  ccnetInit();
	public native int	ccnetClose();
	public native boolean	ccnetServerFound();
	public native void	ccnetSetChannel(int chan_index);

	public native int	ccnetGetChannelLevel(int chindex, int right);
	/*
	 *	ccnetGetChannelLevel()
	 *		channel index number is physical channel index
	 */

	public native int	ccnetGetChannelCount();
	/*
	 *	ccnetGetChannelCount()
	 *		returns logical channel count.
	 * 		i.e. logical channel count is equal to physical channel count
	 *		when audio mode is stereo. (default)
	 *		logical channel count is TWO times of physical channel count when audio mode is MONO.
	 */

	public native String	ccnetGetChannelName(int chindex);
	public native int	ccnetGetSubscribers(int chindex);
	public native String	ccnetGetServerName();
	public native int	ccnetGetPacketCount(int chindex);

	public native int	ccnetGetAudioModeMono();
	public native void	ccnetSetAudioModeMono(int mode_mode);

	public native void	servInfoRequest();
	public native void	servInfoV2Request();
	public native boolean	servInfoValid();
	public native static int	servInfoGetChannelCount();
	public native static String	servInfoGetServerName();
	public native static String	servInfoGetChannelName(int index);
	public native static int	servInfoSetServerName(String str);
	public native static int	servInfoSetChannelName(int index, String str);
	public native int	servInfoPowerOff(int off);
	public native int	servInfoUpdate();

	public native static int	servInfoV2Update();
	/*
	 *	servInfoV2Update()
	 *		servInfoV2Update updates audio mode and logical channel names.
	 * 
	 * 
	 */

	public native static int	servInfoUpdateStat();

	public native static int	servInfoGetAudioModeMono();
	public native void	servInfoSetAudioModeMono(int mode_mode);


	public native void	oslCreateEngine();
	public native void	oslCreateBufferQueueAudioPlayer();
	public native void	oslClose();

	static {
		System.loadLibrary("ccastnet");
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == android.view.KeyEvent.KEYCODE_BACK){
			if(mbBackPressed == false){
				mbBackPressed = true;
				Toast.makeText(this, "Press again to Exit.", Toast.LENGTH_LONG).show();
				channelNames.edit().clear().commit();
				for(int i = 0; i < servInfoGetChannelCount(); i++){
					channelNames.edit().putString("channel"+i, servInfoGetChannelName(i)).commit();
				}
				return true;
			}
		}
		else{
			mbBackPressed = false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		Log.d(TAG, "Pause");

		ccnetClose();
		oslClose();

		if(mWifiLock != null){
			mWifiLock.release();
			mWifiLock = null;
		}

		if(mWl != null){
			mWl.release();
			mWl = null;
		}
		
		/*if(directTvTimer != null){
		directTvTimer.cancel();
		}*/
		mbInitialized = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroy");

//		ccnetClose();
//		oslClose();

		if(mTimer != null){
			mTimer.cancel();
		}
		if(directTvTimer != null){
		directTvTimer.cancel();
		}
		//unregisterReceiver(null);
		super.onDestroy();
	}

	@Override
	public void onResume(){
		super.onResume();

		String str = ccnetInit();
		oslCreateEngine();
		oslCreateBufferQueueAudioPlayer();

		if (mbRelease == true) {
			lockPower();
		}

//		FragmentManager fm;
//		Fragment frag;
		ActionBar.Tab tab;

		tab = getActionBar().getSelectedTab();

		Log.d(TAG, "Resume " + tab.getPosition());
		
		//new GetDirectTv().execute();

//		checkItemOption();

		mbMonitorViewUpdated = false;
		mbInitialized = true;
		
	}

	public void lockPower(){
		if(mWl == null){
			PowerManager pm = (PowerManager)getApplicationContext().getSystemService(POWER_SERVICE);
			mWl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
			mWl.acquire();
		}

		if(mWifiLock == null){
			WifiManager wm = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
			mWifiLock = wm.createWifiLock("wifilock");
			mWifiLock.setReferenceCounted(true);
			mWifiLock.acquire();
		}
	}

	private void hideLevel(View fragmentVw, int channel, int idLeft, int idRight) {
		VolumeLevelView vw;

		vw = (VolumeLevelView)fragmentVw.findViewById(idLeft);
		if (vw != null)
			vw.setVisibility(View.INVISIBLE);
		vw = (VolumeLevelView)fragmentVw.findViewById(idRight);
		if (vw != null)
			vw.setVisibility(View.INVISIBLE);
	}

	private void updateLevel(View fragmentVw, int channel, int idLeft, int idRight) {
		VolumeLevelView vw;
		VolumeLevelView vwLeft;
		VolumeLevelView vwRight;
		if(servInfoGetAudioModeMono() == 0){
			vw = (VolumeLevelView)fragmentVw.findViewById(idLeft);
		//	System.out.println(" channel" + channel + "ccnetGetChannelLevel(channel, 0)" + ccnetGetChannelLevel(channel, 0));
		//	System.out.println(" channel" + channel+"ccnetGetChannelLevel(channel, 1)" + ccnetGetChannelLevel(channel, 1));
			
			if (vw != null) {
				vw.setLevel(ccnetGetChannelLevel(channel, 0));
			}
			vw = (VolumeLevelView)fragmentVw.findViewById(idRight);
			if (vw != null) {
				vw.setLevel(ccnetGetChannelLevel(channel, 1));
			}
		}else{
			vwLeft = (VolumeLevelView)fragmentVw.findViewById(idLeft);
			vwRight = (VolumeLevelView)fragmentVw.findViewById(idRight);
			//System.out.println(" channel" + channel + "idleft" + idLeft);
			//System.out.println(" channel" + channel+"idright" + idRight);
			
			if(channel%2 == 0){
				//System.out.println(" channel" + channel + "ccnetGetChannelLevel(channel/2, 0)" + ccnetGetChannelLevel(channel/2, 0));
				//System.out.println(" channel" + channel+"ccnetGetChannelLevel(channel/2, 0)" + ccnetGetChannelLevel(channel/2, 0));
				
				vwLeft.setLevel(ccnetGetChannelLevel(channel/2, 0));
				vwRight.setLevel(ccnetGetChannelLevel(channel/2, 0));
			}else{
				//System.out.println(" channel" + channel + "ccnetGetChannelLevel((channel-1)/2, 1)" + ccnetGetChannelLevel((channel-1)/2, 1));
				//System.out.println(" channel" + channel+"ccnetGetChannelLevel((channel-1)/2, 1)" + ccnetGetChannelLevel((channel-1)/2, 1));
				
				vwLeft.setLevel(ccnetGetChannelLevel((channel-1)/2, 1));
				vwRight.setLevel(ccnetGetChannelLevel((channel-1)/2, 1));
			}
		}
	}

	private void updateStatus(View fragmentVw, int channel, int id) {
		TextView vw;

		String st;
		st = String.format("%d / %d", ccnetGetPacketCount(channel) / 10, ccnetGetSubscribers(channel));

		vw = (TextView)fragmentVw.findViewById(id);
		if (vw != null) {
			vw.setTextColor(Color.WHITE);
			vw.setText(st);
		}
	}

	private void enableItems(View fragmentVw, boolean bShow, int idLeft, int idRight, int idName, int idStatus) {
		View vw;

		vw = (View)fragmentVw.findViewById(idLeft);
		if (vw != null) {
			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);
		}
		vw = (View)fragmentVw.findViewById(idRight);
		if (vw != null) {
			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);
		}
		vw = (View)fragmentVw.findViewById(idName);
		if (vw != null) {
			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);
		}
		vw = (View)fragmentVw.findViewById(idStatus);
		if (vw != null) {
			vw.setVisibility(bShow == true ? View.VISIBLE : View.GONE);
		}
	}

	private final int	kMaxChannel		= 40;		// kmaxAudioChannels
	private final int	mLevelId[] = {
			R.id.view01Left, R.id.view01Right, R.id.view02Left, R.id.view02Right, R.id.view03Left, R.id.view03Right,
			R.id.view04Left, R.id.view04Right, R.id.view05Left, R.id.view05Right, R.id.view06Left, R.id.view06Right,
			R.id.view07Left, R.id.view07Right, R.id.view08Left, R.id.view08Right, R.id.view09Left, R.id.view09Right,
			R.id.view10Left, R.id.view10Right,
			R.id.view11Left, R.id.view11Right, R.id.view12Left, R.id.view12Right,
			R.id.view13Left, R.id.view13Right, R.id.view14Left, R.id.view14Right,
			R.id.view15Left, R.id.view15Right, R.id.view16Left, R.id.view16Right,
			R.id.view17Left, R.id.view17Right, R.id.
			view18Left, R.id.view18Right,
			R.id.view19Left, R.id.view19Right, R.id.view20Left, R.id.view20Right,
			
			R.id.view21Left, R.id.view21Right, R.id.view22Left, R.id.view22Right, R.id.view23Left, R.id.view23Right,
			R.id.view24Left, R.id.view24Right, R.id.view25Left, R.id.view25Right, R.id.view26Left, R.id.view26Right,
			R.id.view27Left, R.id.view27Right, R.id.view28Left, R.id.view28Right, R.id.view29Left, R.id.view29Right,
			R.id.view30Left, R.id.view30Right,
			R.id.view31Left, R.id.view31Right, R.id.view32Left, R.id.view32Right,
			R.id.view33Left, R.id.view33Right, R.id.view34Left, R.id.view34Right,
			R.id.view35Left, R.id.view35Right, R.id.view36Left, R.id.view36Right,
			R.id.view37Left, R.id.view37Right, R.id.view38Left, R.id.view38Right,
			R.id.view39Left, R.id.view39Right, R.id.view40Left, R.id.view40Right,
			
	};
	private final int	mChannelId[] = {
			R.id.text01Name, R.id.text02Name, R.id.text03Name, R.id.text04Name, R.id.text05Name,
			R.id.text06Name, R.id.text07Name, R.id.text08Name, R.id.text09Name, R.id.text10Name,
			R.id.text11Name, R.id.text12Name, R.id.text13Name, R.id.text14Name, R.id.text15Name,
			R.id.text16Name, R.id.text17Name, R.id.text18Name, R.id.text19Name, R.id.text20Name,
			
			R.id.text21Name, R.id.text22Name, R.id.text23Name, R.id.text24Name, R.id.text25Name,
			R.id.text26Name, R.id.text27Name, R.id.text28Name, R.id.text29Name, R.id.text30Name,
			R.id.text31Name, R.id.text32Name, R.id.text33Name, R.id.text34Name, R.id.text35Name,
			R.id.text36Name, R.id.text37Name, R.id.text38Name, R.id.text39Name, R.id.text40Name,
//			R.id.text11Name,
	};
	private final int	mStatusId[] = {
			R.id.text01Status, R.id.text02Status, R.id.text03Status, R.id.text04Status, R.id.text05Status,
			R.id.text06Status, R.id.text07Status, R.id.text08Status, R.id.text09Status, R.id.text10Status,
			R.id.text11Status, R.id.text12Status, R.id.text13Status, R.id.text14Status, R.id.text15Status,
			R.id.text16Status, R.id.text17Status, R.id.text18Status, R.id.text19Status, R.id.text20Status,
			
			R.id.text21Status, R.id.text22Status, R.id.text23Status, R.id.text24Status, R.id.text25Status,
			R.id.text26Status, R.id.text27Status, R.id.text28Status, R.id.text29Status, R.id.text30Status,
			R.id.text31Status, R.id.text32Status, R.id.text33Status, R.id.text34Status, R.id.text35Status,
			R.id.text36Status, R.id.text37Status, R.id.text38Status, R.id.text39Status, R.id.text40Status,
//			R.id.text11Name,
	};

	private boolean channelReceiving;
	private void refreshChannelNames()
	{
		ActionBar.Tab tab = getActionBar().getSelectedTab();
		int n, channels;
		View button;
		TextView t;
		String label;
		if (tab.getPosition() == kSystemSection){
		//	try {
			//modifySystemView();
			int temp = servInfoGetAudioModeMono();
			channels = servInfoGetChannelCount();
			View fragmentVw = systemSect.getView();
			System.out.println("audio mode" + servInfoGetAudioModeMono());
			System.out.println("channels" + servInfoGetChannelCount());
			
			System.out.println("chanel count" + channels + " temp mono" + temp);
			Map<String,?> temp1  = channelNames.getAll();
			System.out.println("refreshchannelnames" + temp1);
			for (n = 0; n < channels; n++) {
				String chname = channelNames.getString("channel"+n, " ");
				enableEntry(true, mIdsLabel[n], mIdsName[n]);
				t = (TextView) fragmentVw.findViewById(mIdsName[n]);
				if(chname.contains(":")){
					String v = settings.getString("ip"+n, "");
					if(v.equals("")){
						servInfoSetChannelName(n," ");
						chname = " ";
					}
					t.setText(chname);
					t.setTextColor(Color.WHITE);
				}
			else{
					if(!chname.contains("")){
						label = chname;
					}else{
						label = servInfoGetChannelName(n);				
					}
					t.setText(label);
					t.setTextColor(Color.WHITE);
				}
			}
			if(n == 0){
				button = findViewById(R.id.buttonApply);
				button.setVisibility(View.GONE);
				button = findViewById(R.id.toggleButton1);
				button.setVisibility(View.GONE);
				button = findViewById(R.id.buttonReboot);
				button.setVisibility(View.GONE);
			}else{
				button = findViewById(R.id.buttonApply);
				button.setVisibility(View.VISIBLE);
				button = findViewById(R.id.toggleButton1);
				button.setVisibility(View.VISIBLE);
				button = findViewById(R.id.buttonReboot);
				button.setVisibility(View.VISIBLE);
			}
			for ( ; n < 40; n++)
				enableEntry(false, mIdsLabel[n], mIdsName[n]);
	
			t = (TextView) fragmentVw.findViewById(R.id.editServerName);
			label = servInfoGetServerName();
			if(label!= null)
				t.setText(label);
	
	String pass;
	pass = channelNames.getString("serverPassword","");
	t = (TextView) fragmentVw.findViewById(R.id.ServerPassword);
	if(pass!= "")
		t.setText(pass);
		}
		if (tab.getPosition() == kMonitorSection){
			int temp = servInfoGetAudioModeMono();
			channels = servInfoGetChannelCount();
			View fragmentVw = monitorSect.getView();
			System.out.println("chanel count" + channels + " temp mono" + temp);
			for (n = 0; n < channels; n++) {
				String chname = channelNames.getString("channel"+n, " ");
				
				//enableEntry(true, mIdsLabel[n], mIdsName[n]);
				TextView tv = (TextView)fragmentVw.findViewById(mChannelId[n]);
				
				if(chname.contains(":")){
					String v = settings.getString("ip"+n, "");
					if(v.equals("")){
						servInfoSetChannelName(n," ");
						chname = " ";
					}else{
						String[] parts = chname.split(":");
						String part1 = parts[0];
						String part2 = parts[1];
						chname = part1 + " \n" + part2;
					}
					tv.setText(chname);
					tv.setTextColor(Color.WHITE);
				}else{
					label = servInfoGetChannelName(n);
					System.out.println("channels in modify system view" + label + "n" + n);
					tv.setText(label);
					tv.setTextColor(Color.WHITE);
				}
			}
	
			TextView s = (TextView) fragmentVw.findViewById(R.id.textServerName);
			label = servInfoGetServerName();
			if(label!= null)
				s.setText(label);
		}
	}
	
	private void setChannelName(View fragmentVw, int index) {
	
		TextView vw = (TextView)fragmentVw.findViewById(mChannelId[index]);
		if (vw == null)
			return;

		String chname;

		chname = String.format("%s", servInfoGetChannelName(index));
		vw.setText(chname);
		vw.setTextColor(Color.WHITE);

	}


	private void updateLevelMeters() {
		View fragmentVw;
		int n;
		int count = 0;
		if(channelReceiving == false)
		{
			count = servInfoGetChannelCount();
		}

	//	updateHandler.removeCallbacks(sendUpdatetoRemote);

		Log.d("updateLevelMeters: ", "> " + count);
		
		fragmentVw = monitorSect.getView();
		if (fragmentVw == null)
			return;

		for (n = 0; n < count; n++) {
			updateLevel(fragmentVw, n, mLevelId[n * 2 + 0], mLevelId[n * 2 + 1]);



			updateStatus(fragmentVw, n, mStatusId[n]);
		}

		for ( ; n < kMaxChannel; n++) {
			hideLevel(fragmentVw, n, mLevelId[n * 2 + 0], mLevelId[n * 2 + 1]);
		}
		
		if(count > 0)
		{
			this.channels = count;
		}
		//For Direct TV
		//channels = count;
		if(updatingDirectTV == false)
		{
			//new UpdateDirectTv().execute();
			//updatingDirectTV = true;

		}


		// display wifi signal
		TextView txtVw = (TextView) fragmentVw.findViewById(R.id.textWlanStatus);

		if (txtVw != null && mbRelease == true) {
			WifiInfo wi = mwifiManager.getConnectionInfo();
			if (wi != null) {
				String str = String.format("%s; Spd:%d Sig:%d", wi.getSSID(), wi.getLinkSpeed(), wi.getRssi());
				txtVw.setText(str);
			}
		}

		if (mbServerInfo == false) {
			if (ccnetServerFound() == true) {
				mbServerInfo = true;
			}
		}

//		n = ((ViewGroup) vw).getChildCount();
//		vw = ((ViewGroup)vw).getChildAt(0);
//		vw.postInvalidate();
//		Log.d("cct", "child count " + n);
		
	}

	//Added by Anton
	TextView textView;
	
	PopupWindow popUp;
    LinearLayout layout;
    TextView tv;
    LayoutParams params;
    LinearLayout mainLayout;
    Button but;
    boolean click = true;
	
	private void checkMonitorView() {
		int n;
		int count = servInfoGetChannelCount();

		System.out.println( "monitorview channel count " + count);

		View vw = monitorSect.getView();
		if (vw == null) {
			Log.d(TAG, "monitorSect null.");
			return;
		}

		
		/*
		//Added by Anton
		//HIDDEN TEMORARILY. NEED TO FINISH. CHANNELS DIALOG
		textView = (TextView)findViewById(R.id.textServerName);
		textView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			// Perform action on click
				
				DialogFragment newFragment = new DirectTvChannels();
			    newFragment.show(getSupportFragmentManager(), "channels");
				
		
		
		//This section can be deleted.
/*				window.setBackgroundDrawable(  
				    getResources().getDrawable(android.R.color.transparent) );
				textView.setText("test");*/
		//This section can be deleted.
		
		
		//	}
		//});
		//*/
		for (n = 0; n < count; n++) {
			enableItems(vw, true, mLevelId[n * 2 + 0], mLevelId[n * 2 + 1], mChannelId[n], mStatusId[n]);
				setChannelName(vw, n);
				mbMonitorViewUpdated = true;
		}

		for ( ; n < kMaxChannel; n++)
			enableItems(vw, false, mLevelId[n * 2 + 0], mLevelId[n * 2 + 1], mChannelId[n], mStatusId[n]);

		TextView txtServerName = (TextView)vw.findViewById(R.id.textServerName);
		if (txtServerName != null) {
			
			String str = servInfoGetServerName();
			txtServerName.setText(str == null ? "Server not ready" : str);

			TextView txtVw = (TextView) vw.findViewById(R.id.textWlanStatus);
			if (txtVw != null) {
				txtVw.setText("...");
			}
		}
	}

	private void controlSystem() {
		if (mnSystemControl_PowerOff == 0) {
			mDlgProgress = ProgressDialog.show(this, null, "reboot server...", true);
			updateHandler.removeCallbacks(sendUpdatetoRemote);
			new PutPowerRebootValues().execute(servInfoGetServerName(), "reboot");
		//	setupRemoteHandler();
		}
		else {
			mDlgProgress = ProgressDialog.show(this, null, "turn off server...", true);
			updateHandler.removeCallbacks(sendUpdatetoRemote);
			new PutPowerRebootValues().execute(servInfoGetServerName(), "poweroff");
		//	setupRemoteHandler();
		}
		mnControlServerTimeout = 10000 / kTimerTick;

		servInfoPowerOff(mnSystemControl_PowerOff);

		setupRemoteHandler();
	}

	private void requestSystemPowerOff(int off) {
		mnSystemControl_PowerOff = off;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Control Server");
		builder.setMessage(off == 0 ? "reboot?" : "power off?");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "U Clicked Cancel ", Toast.LENGTH_LONG).show();
			}
		});
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				controlSystem();
				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();		

	}

	private void runEditChannelName() {
		Intent i = new Intent(this, EditChannelName.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	private void setSystemEdit2(boolean enabled) {
		Button btnEditChannelName = (Button)findViewById(R.id.buttonChannelName);
		Button btnEditInput = (Button)findViewById(R.id.buttonAssign);
		Button btnEditVolume = (Button)findViewById(R.id.buttonVolume);
		Button btnReboot = (Button)findViewById(R.id.buttonReboot);

		enabled = true;	// test

/*
 		if (enabled == true) {
 			btnReboot.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					requestSystemPowerOff(0);
					
				}
			});

			btnEditChannelName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					runEditChannelName();
				}
			});
		}
		else {
			btnReboot.setEnabled(false);
			btnEditChannelName.setEnabled(false);
		}
 		*/
	}
	private void selectAudioMode() {
	
		Map<String, ?> temp = channelNames.getAll();
		System.out.println("in select audio mode channelnames" + temp);
		
		//temp_settings = settings;
		Map<String, ?> temp1 = settings.getAll();
		System.out.println("in select audio mode settings" + temp1);
		int mono = servInfoGetAudioModeMono();

		temp_settings = getSharedPreferences(TEMP_PREFS_NAME, 0);
		SharedPreferences.Editor temp_editor = temp_settings.edit();
		temp_channelNames = getSharedPreferences(TEMP_CHANNEL_NAMES_FILE, 0);
		
		if (mono == 0) {
			
			int n;
			String ipFromSettings;
			EditText t1,t2;
			for (n = 0; n < servInfoGetChannelCount(); n++)
			{
				ipFromSettings = settings.getString("ip"+n, "");
				System.out.println("ipFromSettings" + ipFromSettings + "n" + n);
				if(ipFromSettings!= null && ipFromSettings!= ""){
					String str = "ip"+2*n;
					String str1 = "ip"+(2*n+1);
					temp_editor.putString(str, ipFromSettings);
					temp_editor.putString(str1, ipFromSettings);
					temp_editor.commit();
					Map<String, ?> temp5 = temp_settings.getAll();
					System.out.println("in select audio mode settings" + temp5);
				}else{
					//servInfoSetChannelName(2*n+1, servInfoGetChannelName(n));
					channelNames.edit().remove("channel"+n).commit();
					channelNames.edit().remove("channel"+2*n).commit();
					channelNames.edit().remove("channel"+2*n+1).commit();
				}
			}
			
			Map<String, ?> temp6 = channelNames.getAll();
			Map<String, ?> temp5 = temp_settings.getAll();
			copyPreferences(settings, temp_settings);
			temp_editor.clear().commit();
			servInfoSetAudioModeMono(1);
		}
		else {
			int n;
			String ipFromSettings;
			EditText t1,t2;
			int mono_channels = servInfoGetChannelCount();
			int stereo_channels = servInfoGetChannelCount()/2;
			for (n = 0; n < mono_channels; n++)
			{
				ipFromSettings = settings.getString("ip"+n, "");
				System.out.println("ipFromSettings" + ipFromSettings + "n" + n);
				if(ipFromSettings!= null && ipFromSettings!= ""){
					if(n%2 == 0){
						String str = "ip"+n/2;
						temp_editor.putString(str, ipFromSettings);
						temp_editor.commit();
						channelNames.edit().remove("channel"+n).commit();
					}else{
						channelNames.edit().remove("channel"+n).commit();
					}	
				}
			}
			
			Map<String, ?> temp5 = temp_settings.getAll();
			System.out.println("in select audio mode settings" + temp5);
			copyPreferences(settings, temp_settings);
			temp_editor.clear().commit();
			servInfoSetAudioModeMono(0);
			for(int i = stereo_channels ; i < mono_channels ; i++){
				servInfoSetChannelName(i, "");
			}
		}
		Map<String, ?> temp3 = channelNames.getAll();
		Map<String, ?> temp4 = settings.getAll();
try {
		modifySystemView();
} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void copyPreferences(SharedPreferences sp1, SharedPreferences sp2){
		SharedPreferences.Editor ed = sp1.edit(); 
		SharedPreferences sp = sp2; //The shared preferences to copy from
		ed.clear(); 
		for(Entry<String,?> entry : sp.getAll().entrySet()){ 
		 Object v = entry.getValue(); 
		 String key = entry.getKey();
		 
		 if(v instanceof Boolean) 
		   ed.putBoolean(key, ((Boolean)v).booleanValue());
		 else if(v instanceof Float)
		    ed.putFloat(key, ((Float)v).floatValue());
		 else if(v instanceof Integer)
		    ed.putInt(key, ((Integer)v).intValue());
		 else if(v instanceof Long)
		    ed.putLong(key, ((Long)v).longValue());
		 else if(v instanceof String)
		    ed.putString(key, ((String)v));         
		}
		ed.commit(); //save it.
		
	}
	private void refreshSystemConfig(boolean enabled) {
		mnReceiveServerInfoTimeout = 0;
		mDlgProgress.dismiss();

		if (mApp.version >= 2) {
			setSystemEdit2(enabled);
			return;
		}

		Button btnApply = (Button) findViewById(R.id.buttonApply);
//		boolean refresh=true;
		if (enabled == true) {

			//modifySystemView();

			
			//updateSystemInfo();
//			for(int i=0;i<mIdsBool.length;i++){
//				if(mIdsBool[i]==false&&setFlags[i]==1){
//					refresh=false;
//				}
//			}
//			
//			if((!serverNameselected)&&refresh){
//				updateSystemInfo();
//			}
			btnApply.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				// Perform action on click
	try {
					updateSystemInfo();
} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OnTyping = false;
					
				}
			});
		}
		else {
//			btnApply.setEnabled(false);
			flag = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setNegativeButton("Close", null);
			builder.setMessage("Failed to connect to the server");
			AlertDialog alert = builder.create();
			alert.show();
			
		}
//		findViewById(R.id.buttonApply).setOnClickListener(mbuttonClickListener);

		Button btnReboot = (Button)findViewById(R.id.buttonReboot);
		if (enabled == true) {
			btnReboot.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					requestSystemPowerOff(0);
					
				}
			});
		}
		else {
			btnReboot.setEnabled(false);
		}
		
		ToggleButton btnAudioMode = (ToggleButton)findViewById(R.id.toggleButton1);
		System.out.println("audio mode" + servInfoGetAudioModeMono());
		btnAudioMode.setChecked(servInfoGetAudioModeMono() > 0 ? true : false);
		btnAudioMode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectAudioMode();
				
	TextView servername = (TextView)findViewById (R.id.editServerName);
				Log.d("Server name: ", "> " +servername.getText().toString());
				get_value_flag = 0;
				put_value_flag = 0;
				power_reboot_flag = 0;
				new UpdateServerDetails().execute(servername.getText().toString());
			}
		});

	}
	//-----------------------------------Prasad Changes Begins-------------------------------------------------------
private void setupRemoteHandler(){
	updateHandler.removeCallbacks(sendUpdatetoRemote);

		Log.d("WATCHING","false");
	//	if(watching==true) {
			Log.d("WATCHING","true");
			updateHandler.postDelayed(sendUpdatetoRemote, 6000);
	//	}
	}
	private Runnable sendUpdatetoRemote= new Runnable() {
		public void run() {
			TextView servername = (TextView)findViewById (R.id.editServerName);
			if(put_modified==0){
				new UpdateChannels().execute(servername.getText().toString());

			}else{



				if(get_value_flag ==1) {
					if (thread_flag == 0) {
						Log.d("PutJSONThread","Stopped");
						Log.d("GetJSONThread","Started");
						new GetServerChannelValues().execute();

					}
				}
			//	if(get_modified>put_modified)
				if(put_value_flag ==1) {
					if (thread_flag == 1) {
						Log.d("PutJSONThread", "Stopped");
						Log.d("GetJSONThread", "Started");
						new UpdateChannels().execute(servername.getText().toString());
					}
				}
				if(power_reboot_flag ==1)
					if(reboot==0&&power==0)
						new GetPowerRebootValues().execute();


			}


			updateHandler.postDelayed(this, 6000);
		}
	};
	//-----------------------------Send Remote Access  AsyncTask PUT Update Server Details---------------------------------------------
		private class UpdateServerDetails extends AsyncTask<String,Void,JSONObject>{

			@Override
			protected void onPostExecute(JSONObject result) {
				// TODO Auto-generated method stub
			//	super.onPostExecute(result);
				try {
					JSONObject data= result.getJSONObject("data");
					Log.d("USDOnPostexecute: ", "> " +data.getString("mode"));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("ErrorOnPostexecute: ", "> " +e);
					e.printStackTrace();
				}
				get_value_flag = 1;
				put_value_flag = 1;
				power_reboot_flag = 1;
			}

			@Override
			protected JSONObject doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.d("PARAMS: ", "> " +params[0]);
				JSONObject result= new JSONObject();
			    HttpClient hc = new DefaultHttpClient();
			    String authString = params[0]+ ":" + channelNames.getString("serverPassword","");
		//	    String authStringEnc = new String(Base64.encodeToString(authString.getBytes(),Base64.DEFAULT));
			    final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
		//	    Log.d("In add_sever header: ", "> " +authStringEnc);
			    String message;
		//	    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+MainActivity.kServerID;
			    Log.d("channelNames.getInt): ", "> " +channelNames.getInt("serverID", 0));
			    int SID = channelNames.getInt("serverID", 0);
			    Log.d("SID: ", "> " +SID);
			    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID;;
		//	    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/13839";
			    HttpPut p = new HttpPut(url);
			    HttpEntity entity = null;
			    BufferedReader reader;


			    JSONObject object = new JSONObject();
			  //  JSONObject body = new JSONObject();
			    try {


			    	int mono = servInfoGetAudioModeMono();
			    	if(mono==0)
			    		object.put("mode", "stereo");
			    	else
			    		object.put("mode", "mono");
//			    	object.put("mode", "mono");
			    	object.put("timezone", "Europe/Berlin");
			        object.put("updateserver", "1");
			      //  body.put("body", object);
			        Log.d("USJSONObject","object created");
			    } catch (Exception ex) {
			    	 Log.d("USJSONExceptionObject",ex.toString());
			    }

			    try {
			    message = object.toString();


			    p.setEntity(new StringEntity(message, "UTF8"));
			    p.setHeader("Content-type", "application/json");
			    p.setHeader("Authorization", basicAuth);
			    Log.d("USJSONMessage","Message created");
			    HttpResponse resp = hc.execute(p);
			    Log.d("USJSONMessageSend","Message send");
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

		            		JSONObject data = new JSONObject(json.toString());
		                    result=data;
		            		// This value will be 404 if the request was not
		            		// successful
		            		Log.d("USIn Response: ", "> " +data.getJSONArray("data"));

			        }

			        Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
			    } catch (Exception e) {
			        e.printStackTrace();
			        Log.d("USJSONExceptionOuter",e.toString());

			    }


				return result;

			}


		}
	//----------------------------End of Send Remote Access PUT Update Server Details-----------------------------------------------------------------

//-----------------------------Send Remote Access  AsyncTask Update Channels Class---------------------------------------------
	private class UpdateChannels extends AsyncTask<String,Void,JSONObject>{

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
		//	super.onPostExecute(result);


			try {
			//	JSONObject data= result.getJSONObject("message");
			//	Log.d("UCJSONOnPostexecute: ", "> " +data.getInt("message"));
				put_modified=result.getInt("modified");
				Log.d("UCJSONOnPostexe_put_mod", ">" +put_modified);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.d("UCErrorOnPostexecute: ", "> " +e);
				e.printStackTrace();
			}
			thread_flag=0;
			put_value_flag = 1;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			get_value_flag = 1;
			put_value_flag = 0;
			power_reboot_flag = 1;
			JSONObject result= new JSONObject();
		    HttpClient hc = new DefaultHttpClient();
		    String authString = params[0]+ ":" + channelNames.getString("serverPassword","");
		    final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
		    String message;
		    Log.d("channelNames.getInt): ", "> " +channelNames.getInt("serverID", 0));
		    int SID = channelNames.getInt("serverID", 0);
		    Log.d("SID: ", "> " +SID);
		    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID;
	//	    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/13843";
	//	    HttpPost p = new HttpPost(url);
		    HttpPut p =new HttpPut(url);
		    HttpEntity entity = null;
		    BufferedReader reader;
		    JSONObject final_object = new JSONObject();

			JSONObject obj_channels= new JSONObject();
			JSONArray channelinfo = new JSONArray();



			try {

				for(int i=0;i<servInfoGetChannelCount();i++){
					JSONObject object= new JSONObject();


					object.put("id",i+1);
					object.put("name",servInfoGetChannelName(i));
					String v=settings.getString("ip"+i,"");
					object.put("ip",v);
		//			Log.d("JSON ChannelLevel(i,0)", "Level1:-" +  (ccnetGetChannelLevel(i, 0)));
		//			Log.d("JSON ChannelLevel(i,0)","Level2"+(ccnetGetChannelLevel(i,1)));
					object.put("level1",(ccnetGetChannelLevel(i,0)));
					object.put("level2",(ccnetGetChannelLevel(i,1)));
					object.put("users",ccnetGetSubscribers(i));
					object.put("packetcount",ccnetGetPacketCount(i));
					channelinfo.put(object);
				}


			} catch (JSONException e) {
				e.printStackTrace();
			}


		    try {
				final_object.put("updatechannels",1);
				final_object.put("channels",channelinfo);
		        message = final_object.toString();
				Log.d("USCInBackgroud: ", "> " +message);

		    p.setEntity(new StringEntity(message, "UTF8"));
		    p.setHeader("Content-type", "application/json");
		    p.setHeader("Authorization", basicAuth);
		    Log.d("UCJSONMessage","Message created");
		    HttpResponse resp = hc.execute(p);
		    Log.d("UCJSONMessageSend","Message send");
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

	            		JSONObject data = new JSONObject(json.toString());
	                    result=data;
	            		// This value will be 404 if the request was not
	            		// successful


		        }


		    } catch (Exception e) {
		        e.printStackTrace();
		        Log.d("UCJSONExceptionOuter",e.toString());

		    }


			return result;

		}


	}
//-----------------------End of Send Remote Access  AsyncTask Update Channels Class---------------------------------------------

//-----------------------------------Prasad Changes Ends-------------------------------------------------------

	private void updatedSysInfo(boolean success) {
		mnUpdateServerInfoTimeout = 0;
		//mDlgProgress.dismiss();

		if (success == true) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setNegativeButton("Dismiss", null);
//			builder.setMessage("Updated server information successfully.");
//			AlertDialog alert = builder.create();
//			alert.show();
		}
		else {
			if(flag == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setNegativeButton("Dismiss", null);
			builder.setMessage("Failed to update server information.");
			AlertDialog alert = builder.create();
			alert.show();
		}
		}
	}

	private void quit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		builder.setMessage("server down. restart this app.");
		AlertDialog alert = builder.create();
		alert.show();
	}

//	private Handler mHandler = new Handler(){
	static class MainHandler extends Handler {
		private MainActivity mActivity;

		public MainHandler(FragmentActivity mainActivity) {
			mActivity = (MainActivity)mainActivity;
		}

		@Override
		public void handleMessage(Message msg){
//			Intent intent;
//			FragmentManager fm;
//			Fragment frag;
			ActionBar ab;
			ActionBar.Tab tab;

			ab = mActivity.getActionBar();
			tab = ab.getSelectedTab();

			switch (msg.what) {
			case kMsgRefreshStatus:
				if (tab.getPosition() == kMonitorSection) {
					if (mActivity.mbMonitorViewUpdated == true) {
						if (mActivity.ccnetServerFound() != true) {
							mActivity.mbMonitorViewUpdated = false;
							mActivity.checkMonitorView();
						}
					}
					else {
						mActivity.checkMonitorView();
					}
					mActivity.updateLevelMeters();
				}
				break;

			case kMsgServerInfoAvailable:
				{
					if (tab.getPosition() == kSystemSection) {
						Log.d("Response: ", "> " + "System Tab Pressed");
						mActivity.refreshSystemConfig(true);
					}
					else if (tab.getPosition() == kDirectTvSection) {
						Log.d("Response: ", "> " + "DirectTV Tab Pressed");
						mActivity.refreshSystemIps(true);
					}
				}
				break;
			case kMsgServerInfoTimeout:
				if (tab.getPosition() == kSystemSection) {
					mActivity.refreshSystemConfig(false);
				}
				else if (tab.getPosition() == kDirectTvSection) {
					Log.d("Response: ", "> " + "DirectTV Tab Pressed");
					mActivity.refreshSystemIps(false);
				}
				break;
			case kMsgServerUpdated:
				if (tab.getPosition() == kSystemSection) {
					mActivity.updatedSysInfo(true);
				}
				else if (tab.getPosition() == kDirectTvSection) {
					Log.d("Response: ", "> " + "DirectTV Tab Pressed");
					mActivity.refreshSystemIps(true);
				}
/*				else if (tab.getPosition() == kMonitorSection) {
					mActivity.updatedSysInfo(true);
				}*/
				break;
			case kMsgServerUpdateTimeout:
				if (tab.getPosition() == kSystemSection) {
					mActivity.updatedSysInfo(false);
				}
				else if (tab.getPosition() == kDirectTvSection) {
					Log.d("Response: ", "> " + "DirectTV Tab Pressed");
					mActivity.refreshSystemIps(false);
				}
/*				else if (tab.getPosition() == kMonitorSection) {
					mActivity.updatedSysInfo(false);
				}*/
				break;
			case kMsgServerControlled:
				mActivity.quit();
				break;
			case kMsgServerInfoAvailable2:
			{
				if (tab.getPosition() == kSystemSection) {
					Log.d("Response: ", "> " + "DirectTV Tab Pressed");
					mActivity.refreshSystemConfig(true);
				}
			}
			break;
			default:
				break;
			}
		}
	};

	/*@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		float sx, sy;
		int action = e.getAction();

		sx = e.getX();
		sy = e.getY();

		String mesg = String.format("double tab %d %d", (it)sx, (int)sy);

		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}*/

	//	@Override
//	public boolean onDoubleTap(MotionEvent event) {
//		float sx, sy;
//		int action = event.getAction();

//		sx = event.getX();
//		sy = event.getY();

//		String mesg = String.format("double tab %d %d", (int)sx, (int)sy);

//		Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
//		return false;
//	}

	private boolean checkScreenResolution() {
//		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		float scale = getResources().getDisplayMetrics().density;
		int wid, ht, t;

		wid = getResources().getDisplayMetrics().widthPixels;
		ht = getResources().getDisplayMetrics().heightPixels;

		if (wid < ht) {
			t = wid;
			wid = ht;
			ht = t;
		}
//		dpi = (int)(scale * 160);

		Log.d(TAG, "screen wid " + wid + ", ht " + ht);

		if (wid < 1024)
			return false;
		if (ht < 700)
			return false;
		return true;
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

	//For System Tab
	private void modifySystemView() throws MalformedURLException, IOException{
		int n, channels;
		TextView t;
		String label;
	String str;
		View button;

		channels = servInfoGetChannelCount();
		View fragmentVw = systemSect.getView();
		Map<String,?> temp1  = channelNames.getAll();
		for (n = 0; n < channels; n++) {
			String chname = channelNames.getString("channel"+n, " ");
			enableEntry(true, mIdsLabel[n], mIdsName[n]);
			t = (TextView) fragmentVw.findViewById(mIdsName[n]);
			if(chname.contains(":")){

				//String[] parts = chname.split(":");
				//String part1 = parts[0];
				//String part2 = parts[1];
				//chname = part1 + " \n" + part2;

				String v = settings.getString("ip"+n, "");
				if(v.equals("")){
					servInfoSetChannelName(n," ");
					chname = " ";
				}
				t.setText(chname);
				t.setTextColor(Color.WHITE);
			}
		else{
				label = servInfoGetChannelName(n);
				if (label.contains(":")) {
					String v = settings.getString("ip"+n, "");
					if(v.equals("")){
						servInfoSetChannelName(n," ");
						label = " ";
					}
				}
				t.setText(label);
				t.setTextColor(Color.WHITE);
			}
		}
		if(n == 0){
			button = findViewById(R.id.buttonApply);
			button.setVisibility(View.GONE);
			button = findViewById(R.id.toggleButton1);
			button.setVisibility(View.GONE);
			button = findViewById(R.id.buttonReboot);
			button.setVisibility(View.GONE);
		}
		for ( ; n < 40; n++)
			enableEntry(false, mIdsLabel[n], mIdsName[n]);

		t = (TextView)findViewById(R.id.editServerName);
		label = servInfoGetServerName();
		if(label!= "")
			t.setText(label);
String pass;
		pass = channelNames.getString("serverPassword","");

		t = (TextView) fragmentVw.findViewById(R.id.ServerPassword);

		if(pass!= "")
			t.setText(pass);

	}

	private void updateSystemInfo() throws MalformedURLException, IOException{
		int dirty, n;
		channels = servInfoGetChannelCount();
		String str;
		String pass;
		View fragmentVw = systemSect.getView();
		TextView t = (TextView) findViewById(R.id.editServerName);
		str = t.getText().toString();
		dirty = servInfoSetServerName(str);
		EditText p = (EditText) findViewById(R.id.ServerPassword);
		pass = p.getText().toString();

		SharedPreferences.Editor editor = channelNames.edit();
	      editor.putString("serverName", str);
editor.putString("serverPassword", pass);
	      editor.commit();

		for (n = 0; n < channels; n++) {
			t = (TextView) findViewById(mIdsName[n]);
			if (t.getText().toString() != "\t") {
				str = t.getText().toString();
				dirty += servInfoSetChannelName(n, str);

			   //   editor.putString("channel"+n, str);
			   //   editor.commit();
			}else{
				if(n%2 == 1){
					int ch_temp = n;
					ch_temp--;
					t = (TextView) findViewById(mIdsName[ch_temp]);
					if (t != null) {
						str = t.getText().toString();
						dirty += servInfoSetChannelName(n, str);

				//	      editor.putString("channel"+n, str);
				//	      editor.commit();
					}

				}
			}
		}
		//if (dirty > 0) {
			//mDlgProgress = ProgressDialog.show(this, null, "Updating server information...", true);
			mnUpdateServerInfoTimeout = 5000 / kTimerTick;
			servInfoV2Update();
			if(OnTyping == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setNegativeButton("Dismiss", null);
			builder.setMessage("Updated server information successfully.");
			AlertDialog alert = builder.create();
			alert.show();
			}
		//}
		/*int dirty, n;
		int channels = servInfoGetChannelCount();
		String str;
		EditText t = (EditText) findViewById(R.id.editServerName);
		str = t.getText().toString();
		dirty = servInfoSetServerName(str);

		for (n = 0; n < channels; n++) {
			t = (EditText) findViewById(mIdsName[n]);
			if (t != null) {
				str = t.getText().toString();
				dirty += servInfoSetChannelName(n, str);
			}
		}
//		if (dirty > 0) {
			mDlgProgress = ProgressDialog.show(this, null, "Updating server information...", true);
			mnUpdateServerInfoTimeout = 5000 / kTimerTick;
//			servInfoUpdate();
// audio_mode_mono version
			servInfoV2Update();
//		}*/
	}


	//For DirectTV

	//Because of this method (updateServerInformation()), the sound stops for a second...
	// I don't understand the conditions, when I can call this line... "dirty += servInfoSetChannelName(n, str);"
	// Does it allows any punctuation? Does it use the same port as "updateLevelMeters()"? May be these methods are conflicting.
	private void updateServerInformation() {
		int dirty = 0;
		int n;
		String str;

		ActionBar.Tab tab = getActionBar().getSelectedTab();

		//Checking the TAB.
		if (tab.getPosition() == kMonitorSection)
			{
			Log.d("Update Server Information: ", "> " + tab.getPosition());
				channels = servInfoGetChannelCount();
				Log.d("Update Server Information: ", "> " + channels);
				TextView t = (TextView)findViewById(R.id.textServerName);
				str = t.getText().toString();
				if(str != "" || str != "Server not ready")
				{
					Log.d("Update Server Information: ", "> " + str);
					dirty = servInfoSetServerName(str);
				}
				for (n = 0; n < channels; n++) {
					if(mIdsBool[n] == true)
					{
						t = (TextView) findViewById(mChannelId[n]);
						if (t != null) {
							str = t.getText().toString();
							if(null!=str&&!str.trim().isEmpty())
							{
								String tempContent = " \n";
								if (str.contains(tempContent)) {
									String[] parts = str.split(tempContent);
									String part1 = parts[0];
									String part2 = parts[1];
									str = part1 + "\n " + part2;
								}
								Log.d("Update Server Information: ", "> " +n+ " "+ str);
								TextView tSer = (TextView)findViewById(R.id.textServerName);
								String strServer = tSer.getText().toString();
								if(strServer != "Server not ready")
								{
									dirty += servInfoSetChannelName(n, str);
								}
							}
						}
					}
					else
					{
						t = (TextView) findViewById(mChannelId[n]);
						if (t != null) {
							//str = t.getText().toString();
							str=servInfoGetChannelName(n);
							if(null!=str&&!str.trim().isEmpty())
							{
								String tempContent = " \n";
								if (str.contains(tempContent)) {
									String[] parts = str.split(tempContent);
									String part1 = parts[0];
									String part2 = parts[1];
									str = part1 + "\n " + part2;
								}
								Log.d("Update Server Information: ", "> " +n+ " "+ str);

								TextView tSer = (TextView)findViewById(R.id.textServerName);
								String strServer = tSer.getText().toString();
								if(strServer != "Server not ready")
								{
									dirty += servInfoSetChannelName(n, str);
								}
							}
						}
					}
				}
				Log.d("Update Server Information: ", "> " +n+ " "+ dirty);
			//	if (dirty > 0)
			//	{
					//mDlgProgress = ProgressDialog.show(this, null, "Updating server information...", true);
					mnUpdateServerInfoTimeout = 5000 / kTimerTick;
					TextView tSer = (TextView)findViewById(R.id.textServerName);
					String strServer = tSer.getText().toString();
					if(strServer != "Server not ready")
					{
						servInfoV2Update();
					}
			//	}







/*					for (n = 0; n < channels; n++) {
						if(mIdsBool[n] == true)
						{
							str = mIdsChannelName[n];
								if (str != null) {
									str = mIdsChannelName[n] + ": " + mIdsTitleName[n];
									servInfoSetChannelName(n, str);
								}
						}
					}
			mnUpdateServerInfoTimeout = 5000 / kTimerTick;
			servInfoUpdate();*/
		}
		if (tab.getPosition() == kSystemSection)
		{
		Log.d("Update Server Information: ", "> " + tab.getPosition());
			channels = servInfoGetChannelCount();
			Log.d("Update Server Information: ", "> " + channels);
			TextView t = (TextView)findViewById(R.id.editServerName);
			str = t.getText().toString();
			if(str != "" || str != "Server not ready")
			{
				Log.d("Update Server Information: ", "> " + str);
				dirty = servInfoSetServerName(str);
			}
			for (n = 0; n < channels; n++) {
				if(mIdsBool[n] == true)
				{
					t = (TextView) findViewById(mIdsName[n]);
					if (t != null) {
						str = t.getText().toString();
						if(null!=str&&!str.trim().isEmpty())
						{
							if (str.contains(":")) {
								String v = settings.getString("ip"+n, "");
								if(v==""){
									str = " ";
								}
							}
							Log.d("Update Server Information: ", "> " +n+ " "+ str);
							TextView tSer = (TextView)findViewById(R.id.editServerName);
							String strServer = tSer.getText().toString();
							if(strServer != "Server not ready")
							{
								dirty += servInfoSetChannelName(n, str);
							}
						}
					}
				}
				else
				{
					t = (TextView) findViewById(mIdsName[n]);
					if (t != null) {
						//str = t.getText().toString();
						str=servInfoGetChannelName(n);
						if(null!=str&&!str.trim().isEmpty())
						{
							if (str.contains(":")) {
								String v = settings.getString("ip"+n, "");
								if(v==""){
									str = " ";
								}
							}
							TextView tSer = (TextView)findViewById(R.id.editServerName);
							String strServer = tSer.getText().toString();
							if(strServer != "Server not ready")
							{
								dirty += servInfoSetChannelName(n, str);
							}
						}
					}
				}
			}
			Log.d("Update Server Information: ", "> " +n+ " "+ dirty);
		//	if (dirty > 0)
		//	{
				//mDlgProgress = ProgressDialog.show(this, null, "Updating server information...", true);
				mnUpdateServerInfoTimeout = 5000 / kTimerTick;
				TextView tSer = (TextView)findViewById(R.id.editServerName);
				String strServer = tSer.getText().toString();
				if(strServer != "Server not ready")
				{
					servInfoV2Update();
				}
		//	}
		}
	}



	//DIRECT TV SECTION
	private void updateIpAdresses() {
		int n;
		String str;
		EditText t;
		settings.edit().clear().commit();
		channelNames.edit().clear().commit();
		for (n = 0; n < kMaxChannel; n++) {
			t = (EditText) findViewById(mIdsIp[n]);
			if (t != null) {
				str = t.getText().toString();
			      SharedPreferences.Editor editor = settings.edit();
			      editor.putString("ip"+n, str);
			      editor.commit();
			}
		}
		Map<String, ?> temp5 = settings.getAll();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setNegativeButton("Dismiss", null);
		builder.setMessage("Updated ip address successfully.");
		AlertDialog alert = builder.create();
		alert.show();
		int channels = servInfoGetChannelCount();
		View systemView = systemSect.getView();
		for(int i = 0; i < channels ; i++){
			if(servInfoGetChannelName(i).contains(":")){
				servInfoSetChannelName(i, " ");
			}
		}
	}

	private void refreshSystemIps(boolean enabled) {
		//mnReceiveServerInfoTimeout = 0;
		//mDlgProgress.dismiss();

		/*if (mApp.version >= 2) {
			setSystemEdit2(enabled);
			return;
		}*/


		Button btnApplyIp = (Button) findViewById(R.id.buttonApplyIp);

		if (enabled == true) {
			modifyIp();
			Log.d("Response: ", "> " + "In refreshSystemIps");
			btnApplyIp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				// Perform action on click
					updateIpAdresses();
					get_value_flag = 0;
					put_value_flag = 0;
					power_reboot_flag = 0;
					updateHandler.removeCallbacks(sendUpdatetoRemote);
					new UpdateChannels().execute(servInfoGetServerName());
					setupRemoteHandler();

				}
			});
		}
		if (enabled == true) {
		Button btnPowerOff = (Button)findViewById(R.id.buttonPoweroff);
		if (enabled == true) {
			btnPowerOff.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					requestSystemPowerOff(1);

				}
			});
			
		}
		else {
			btnPowerOff.setEnabled(false);
		}

		Button btnReboot = (Button)findViewById(R.id.buttonReboot);
		if (enabled == true) {
			btnReboot.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					requestSystemPowerOff(0);

				}
			});
		}
		else {
			btnReboot.setEnabled(false);
		}
		}
	}

//-----------------------------------Prasad Changes Begins-------------------------------------------------------
//-----------------------------Remote Access GET server channel values---------------------------------------------
		private class GetServerChannelValues extends AsyncTask<String,Void,JSONObject>{
			
			@Override
			protected void onPostExecute(JSONObject result) {
				// TODO Auto-generated method stub
			//	super.onPostExecute(result);

				try {

					JSONObject obj_channels= new JSONObject();
					JSONArray channelinfo = result.getJSONArray("channels");
					get_modified=result.getInt("modified");
					Log.d("GUJSONOnPostexe_get_mod",">" +get_modified);

			//		if(get_modified!=put_modified) {
						SharedPreferences.Editor editor = MainActivity.channelNames.edit();
						SharedPreferences.Editor ipeditor = settings.edit();
						EditText t;
						for (int i = 0; i < channelinfo.length(); i++) {
							JSONObject object = channelinfo.getJSONObject(i);

						//	if (object.getString("name") != ""){
							servInfoSetChannelName(i, object.getString("name"));
							editor.putString("channel" + i, object.getString("name"));
							editor.commit();
						//	}
						//	if(object.getString("ip")!="") {
							ipeditor.putString("ip" + i, object.getString("ip"));
							ipeditor.commit();
							t = (EditText) findViewById(mIdsIp[i]);
							modifyIp();
					//		updateIpAdresses();
			//					t.setText(object.getString("ip"));

						//	}


						}

			//		}
					if(result.getInt("watching")==1)
						watching=true;
					else
						watching=false;

					if(MainActivity.servInfoGetChannelCount()== (channelinfo.length()/2)) {
						ToggleButton btnAudioMode = (ToggleButton) findViewById(R.id.toggleButton1);

						btnAudioMode.setChecked(servInfoGetAudioModeMono() > 0 ? true : false);

						selectAudioMode();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("ErrorOnPostexecute: ", "> " +e);
					e.printStackTrace();
				}
				thread_flag=1;
				get_value_flag = 1;

			}

			@Override
			protected JSONObject doInBackground(String... params) {
				// TODO Auto-generated method stub
		//		Log.d("PARAMS: ", "> " +params[0]);
				get_value_flag = 0;
				put_value_flag = 1;
				power_reboot_flag = 1;
				JSONObject result= new JSONObject();
			    HttpClient hc = new DefaultHttpClient();
			    String message;
				int SID = channelNames.getInt("serverID", 0);
				Log.d("SID: ", "> " +SID);
				String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID;
		//	    HttpPut p = new HttpPut(url);
			    HttpGet p= new HttpGet(url);
			    HttpEntity entity = null;
			    BufferedReader reader;
	    
			   try{
			    p.setHeader("Content-type", "application/json");

			    Log.d("GSCVJSONMessage","Message created");
			    HttpResponse resp = hc.execute(p);
			    Log.d("GSCVJSONMessageSend","Message send");
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
		                 
		            		JSONObject data = new JSONObject(json.toString());
		                    result=data;
		            		// This value will be 404 if the request was not
		            		// successful
		            		Log.d("GSCVIn Response: ", "> " +data.getJSONArray("channelnames"));

			        }

			        Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
			    } catch (Exception e) {
			        e.printStackTrace();
			        Log.d("GSCVJSONExceptionOuter",e.toString());

			    }


				return result;

			}

			
		}
//----------------------------End of Remote Access GET server channel values-----------------------------------------------------------------
//-----------------------------Remote Access GET Power_Reboot values---------------------------------------------
private class GetPowerRebootValues extends AsyncTask<String,Void,JSONObject>{

	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		//	super.onPostExecute(result);

		power_reboot_flag = 1;
		try {

			if(result.getInt("poweroff")!=0){
				requestSystemPowerOff(1);
				power=1;
			}

			if(result.getInt("reboot")!=0){
				requestSystemPowerOff(0);
				reboot=1;
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("ErrorOnPostexecute: ", "> " +e);
			e.printStackTrace();
		}
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		// TODO Auto-generated method stub
		//		Log.d("PARAMS: ", "> " +params[0]);
		get_value_flag = 1;
		put_value_flag = 1;
		power_reboot_flag = 0;
		JSONObject result= new JSONObject();
		HttpClient hc = new DefaultHttpClient();
		String message;
		int SID = channelNames.getInt("serverID", 0);
		Log.d("SID: ", "> " +SID);
		String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID+"/status";
		//	    HttpPut p = new HttpPut(url);
		HttpGet p= new HttpGet(url);
		HttpEntity entity = null;
		BufferedReader reader;

		try{
			p.setHeader("Content-type", "application/json");

			Log.d("GSCVJSONMessage","Message created");
			HttpResponse resp = hc.execute(p);
			Log.d("GSCVJSONMessageSend","Message send");
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

				JSONObject data = new JSONObject(json.toString());
				result=data;
				// This value will be 404 if the request was not
				// successful
				Log.d("GSCVIn Response: ", "> " +data.getJSONArray("channelnames"));

			}

			Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("GSCVJSONExceptionOuter",e.toString());

		}


		return result;

	}


}
//----------------------------End of Remote Access GET Power_Reboot values-----------------------------------------------------------------
//-----------------------------Remote Access GET Power_Reboot values---------------------------------------------
private class PutPowerRebootValues extends AsyncTask<String,Void,JSONObject>{

	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		//	super.onPostExecute(result);


		try {
			//	JSONObject data= result.getJSONObject("message");
			//	Log.d("UCJSONOnPostexecute: ", "> " +data.getInt("message"));
			put_modified=result.getInt("modified");
			Log.d("UCJSONOnPostexe_put_mod", ">" +put_modified);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("UCErrorOnPostexecute: ", "> " +e);
			e.printStackTrace();
		}
		thread_flag=0;
		put_value_flag = 0;
		get_value_flag = 0;
		power_reboot_flag=0;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		// TODO Auto-generated method stub
		JSONObject result= new JSONObject();
		HttpClient hc = new DefaultHttpClient();
		String authString = params[0]+ ":" + channelNames.getString("serverPassword","");
		final String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
		String message;
		Log.d("channelNames.getInt): ", "> " +channelNames.getInt("serverID", 0));
		int SID = channelNames.getInt("serverID", 0);
		Log.d("SID: ", "> " +SID);
		String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/"+SID;
		//	    String url ="https://gocinet.gbr.sculptor.uberspace.de/api/servers/13843";
		//	    HttpPost p = new HttpPost(url);
		HttpPut p =new HttpPut(url);
		HttpEntity entity = null;
		BufferedReader reader;
		JSONObject object = new JSONObject();



		try {
			if(params[1].equals("reboot")) {
				object.put("reboot", 1);
				object.put("poweroff", 0);
			}
			else {
				object.put("reboot", 0);
				object.put("poweroff",1);
			}

			object.put("updatestatus",1);


		} catch (JSONException e) {
			e.printStackTrace();
		}


		try {
			message = object.toString();
			Log.d("USCInBackgroud: ", "> " +message);

			p.setEntity(new StringEntity(message, "UTF8"));
			p.setHeader("Content-type", "application/json");
			p.setHeader("Authorization", basicAuth);
			Log.d("UCJSONMessage","Message created");
			HttpResponse resp = hc.execute(p);
			Log.d("UCJSONMessageSend","Message send");
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

				JSONObject data = new JSONObject(json.toString());
				result=data;
				// This value will be 404 if the request was not
				// successful


			}


		} catch (Exception e) {
			e.printStackTrace();
			Log.d("UCJSONExceptionOuter",e.toString());

		}


		return result;

	}



}
//----------------------------End of Remote Access GET Power_Reboot values-----------------------------------------------------------------
//-----------------------------------Prasad Changes Ends-------------------------------------------------------
	private void modifyIp() {
		int n;
		EditText t;
		String label;
		int channel=servInfoGetChannelCount();
		for (n = 0; n < channel; n++) {
			enableEntry(true, mIdsLabelIp[n], mIdsIp[n]);
			t = (EditText) findViewById(mIdsIp[n]);
		    label = settings.getString("ip"+n, "---.---.---.---");
		    //Log.d("testIp", label);
		    if(null!=t){
		    	t.setText(label);
		    }
		}
		for ( ; n < 40; n++)
			enableEntry(false, mIdsLabelIp[n], mIdsIp[n]);
	}
	
	//JSON Section
	private ProgressDialog pDialog;
	int channels;
	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	
		private class GetDirectTv extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updatingDirectTV = true;
			// Showing progress dialog
			//pDialog = new ProgressDialog(MainActivity.this);
			//pDialog.setMessage("Please wait...");
			//pDialog.setCancelable(false);
			//pDialog.show();
		}

		
			@Override
		protected Void doInBackground(Void... arg0) {
			Log.e("1111111111111111111111", String.valueOf(System.currentTimeMillis()));
			// Creating service handler class instance
			DirectTvJsonServicehandler sh = new DirectTvJsonServicehandler();
			
//			JsonThroughHTTP jth = new JsonThroughHTTP();
//			try {
//				jth.acceptUrl(null);
//			} catch (MalformedURLException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			try {
//				Thread.sleep(30000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			int n;
			String ipFromSettings;
			for (n = 0; n < kMaxChannel; n++)
			{
				ipFromSettings = settings.getString("ip"+n, "");
				//Log.d("Response: ", "> " + ipFromSettings);
				if(validIP(ipFromSettings))
				{
					ipFromSettings = "http://" + ipFromSettings + ":8080/tv/getTuned";
				}
				else
				{
					mIdsBool[n] = false;
					continue;
				}
				
				String jsonStr = null;
				
				//for(int i=0; i<3; i++)
				//{
					if(jsonStr == null)
					{
						Log.e("Response", "ip is:"+ipFromSettings);
						jsonStr = sh.makeServiceCall(ipFromSettings, DirectTvJsonServicehandler.GET);
						Log.e("Response: ", ">>>>>>>>>>>>> " + jsonStr);
					}
				//}
				
				
				if (jsonStr != null) {
					mIdsBool[n] = true;
					try {
						JSONObject jsonObj = new JSONObject(jsonStr);
						
						String channelNameFromDirectTV = jsonObj.getString("callsign");
						String titleNameFromDirectTV = jsonObj.getString("title");
						
						if(channelNameFromDirectTV != null)
						{
							mIdsChannelName[n] = channelNameFromDirectTV;
						}
						if(titleNameFromDirectTV != null)
						{
							mIdsTitleName[n] = titleNameFromDirectTV;
						}
						
						String fullName = mIdsChannelName[n] + ": " + mIdsTitleName[n];
						if(null!=mIdsChannelName[n]&&!mIdsChannelName[n].trim().isEmpty()
								&&null!=mIdsTitleName[n]&&!mIdsTitleName[n].trim().isEmpty()){
							
						SharedPreferences.Editor editor = channelNames.edit();
					      editor.putString("channel"+n, fullName);
					      editor.commit();
						}
						Log.d("Response: ", "> " + fullName);
						/*View fragmentVw = monitorSect.getView();
						TextView vw = (TextView)fragmentVw.findViewById(mChannelId[n]);
						vw.setText(channelNameFromDirectTV);
						vw.setTextColor(Color.BLUE);*/
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					mIdsBool[n] = false;
					Log.e("ServiceHandler", "Couldn't get any data from the url");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			//if (pDialog.isShowing())
			//	pDialog.dismiss();
//			*//**
//			 * Updating parsed JSON data into ListView
//			 * *//*
//			ListAdapter adapter = new SimpleAdapter(
//					MainActivity.this, contactList,
//					R.layout.list_item, new String[] { TAG_NAME, TAG_EMAIL,
//							TAG_PHONE_MOBILE }, new int[] { R.id.name,
//							R.id.email, R.id.mobile });
//
//			setListAdapter(adapter);
			updatingDirectTV = false;
		}

	}
		
		
		// Check is String - IP or not
		public static boolean validIP (String ip) {
		    try {
		        if (ip == null || ip.isEmpty()||ip.trim().length()==0) {
		            return false;
		        }

		        String[] parts = ip.split( "\\." );
		        if ( parts.length != 4 ) {
		            return false;
		        }

		        for ( String s : parts ) {
		            int i = Integer.parseInt( s );
		            if ( (i < 0) || (i > 255) ) {
		                return false;
		            }
		        }
		        if(ip.endsWith(".")) {
		                return false;
		        }

		        return true;
		    } catch (NumberFormatException nfe) {
		        return false;
		    }
	}

		boolean updatingDirectTV = false;
		//boolean updatingDirectTV = true;
		private class UpdateDirectTv extends AsyncTask<Void, Void, Void> {
			
			@Override
			protected void onPreExecute() {
				updatingDirectTV = true;
				super.onPreExecute();
			}

			
				@Override
			protected Void doInBackground(Void... arg0) {
				// Creating service handler class instance
				DirectTvJsonServicehandler sh = new DirectTvJsonServicehandler();
/*				JsonThroughHTTP jth = new JsonThroughHTTP();
				try {
					jth.acceptUrl(null);
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				
				int n;
				String ipFromSettings;
				for (n = 0; n < 40; n++)
				{
					ipFromSettings = settings.getString("ip"+n, "");
					if(ipFromSettings != null)
					{
						if(validIP(ipFromSettings))
						{
							ipFromSettings = "http://" + ipFromSettings + ":8080/tv/getTuned";
							Log.d("Update Direct TV: ", "> " + ipFromSettings);
						}
					}
/*					else
					{
						break;
					}*/
					
					String jsonStr = sh.makeServiceCall(ipFromSettings, DirectTvJsonServicehandler.GET);
					Log.d("Update Direct TV: ", "> " + jsonStr);
					
					if(jsonStr == null)
					{
						jsonStr = sh.makeServiceCall(ipFromSettings, DirectTvJsonServicehandler.GET);
						Log.d("Update Direct TV: ", "> " + jsonStr);
					}
					
					if (jsonStr != null) {
						mIdsBool[n] = true;
						try {
							JSONObject jsonObj = new JSONObject(jsonStr);
							
							String channelNameFromDirectTV = jsonObj.getString("callsign");
							Log.d("Update Direct TV: ", "> " + channelNameFromDirectTV);
							
							View fragmentVw = monitorSect.getView();
							TextView vw = (TextView)fragmentVw.findViewById(mChannelId[n]);
							vw.setText(channelNameFromDirectTV);
							vw.setTextColor(Color.BLUE);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						//mIdsBool[n] = false;
						Log.e("ServiceHandler", "Couldn't get any data from the url");
					}
				}
				
				return null;	
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				//updatingDirectTV = false;
			}

		}

		
}

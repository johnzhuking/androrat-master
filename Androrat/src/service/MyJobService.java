/**
 * 
 */
package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import my.app.client.Client;
import my.app.client.LauncherActivity;
import my.app.client.R;
import utils.LogToFile;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2017-5-25 下午2:03:49 
 * 类说明 
 */
/**
 * @author Administrator
 * 
 */
public class MyJobService extends JobService {
	private int kJobId = 0;

	private String myIp = "127.0.0.1"; // Put your IP in these quotes.
	private int myPort = 9999; // Put your port there, notice that there are no
								// quotes here.
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MyJobService", "jobService启动");
		LogToFile.i("MyJobService", "jobService启动");
		scheduleJob(getJobInfo());
		flags = START_NOT_STICKY;
		return super.onStartCommand(intent, flags, startId);
		
	}

	@Override
	public boolean onStartJob(JobParameters params) {
		Log.e("MyJobService", "执行了onStartJob方法");
		LogToFile.e("MyJobService", "执行了onStartJob方法");
		boolean isLocalServiceWork = isServiceWork(this, "my.app.client.Client");
		boolean isRemoteServiceWork = isServiceWork(this, "my.app.client.service.StartService");
//		if (!isLocalServiceWork || !isRemoteServiceWork) {
			
			Intent client = new Intent(this, Client.class);
			client.setAction(LauncherActivity.class.getName());
			getConfig();
			client.putExtra("IP", myIp);
			client.putExtra("PORT", myPort);
			this.startService(client);
			// Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
			LogToFile.e("MyJobService", "启动StartService");
			Log.e("MyJobService", "启动StartService");
//		}
		return true;
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
	}
	
	@Override
	public boolean onStopJob(JobParameters params) {
		Log.e("MyJobService", "执行了onStopJob方法");
		LogToFile.e("MyJobService", "执行了onStopJob方法");
		scheduleJob(getJobInfo());
		return true;
	}

	// 将任务作业发送到作业调度中去
	public void scheduleJob(JobInfo t) {
		Log.e("MyJobService", "调度job");
		LogToFile.e("MyJobService", "调度job");
		JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
		tm.schedule(t);
	}

	public JobInfo getJobInfo() {
		JobInfo.Builder builder = new JobInfo.Builder(kJobId++,
				new ComponentName(this, MyJobService.class));
		builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
		builder.setPersisted(true);
		builder.setRequiresCharging(false);
		builder.setRequiresDeviceIdle(false);
		// 间隔1000毫秒
		builder.setPeriodic(1000);
		return builder.build();
	}

	// 判断服务是否正在运行
	public boolean isServiceWork(Context mContext, String serviceName) {
		boolean isWork = false;
		ActivityManager myAM = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> myList = myAM
				.getRunningServices(100);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				isWork = true;
				break;
			}
		}
		return isWork;
	}

	public void startKeepLiveService(Context context, int timeMillis,
			String action) {
		// 获取AlarmManager系统服务
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// 包装Intent
		Intent intent = new Intent(context, MyJobService.class);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 添加到AlarmManager
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), timeMillis, pendingIntent);

	}
}

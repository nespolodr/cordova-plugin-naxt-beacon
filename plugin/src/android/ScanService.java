package br.com.naxt.sdk.service;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import br.com.naxt.sdk.service.ServiceUtils;

public class ScanService extends Service
{
	private final String main = "MainActivity";

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	
	//	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	private static final Region REGION_01 = new Region("iberika_app_04", "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 4, null);
	private BeaconManager beaconManager;

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Intent serviceIntent = new Intent(this, ScanService.class);
		this.startService(serviceIntent);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		Log.i("init", "SharedPreferences");
		sharedPref = getSharedPreferences("NativeStorage", MODE_PRIVATE);
		editor = sharedPref.edit();

//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);

		beaconManager = new BeaconManager(this);
		//		beaconManager.setRangingListener(new BeaconManager.RangingListener()
		//		{
		//			@Override
		//			public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons)
		//			{
		//				Log.i("ScanService", "onBeaconsDiscovered");
		//				execute(rangedBeacons);
		//			}
		//		});
		beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener()
		{
			@Override
			public void onEnteredRegion(Region region, List<Beacon> list)
			{
				Log.i("NextActivity", "onEnteredRegion");
				Context context = ScanService.this;

				int mNotificationId = 001;

				try
				{
					String className = ServiceUtils.getFullName(context, main);
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Class.forName(className)),
							PendingIntent.FLAG_UPDATE_CURRENT);
					Notification notification = new Notification.Builder(context)
							.setContentTitle(context.getApplicationInfo().loadLabel(getPackageManager()).toString())
							.setContentText("Você está em uma área interativa da Feira.").setSmallIcon(R.drawable.ic_dialog_email).setContentIntent(contentIntent)
							.setOngoing(true).build();
					NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					mNotifyMgr.notify(mNotificationId, notification);
					Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(1500);
					Log.i("NextActivity", className);

					editor.putString("rangeBeacon", "\"true\"").commit();
					String rangeBeacon = sharedPref.getString("rangeBeacon", "\"false\"");
					Log.i("NATIVE: ", rangeBeacon);
				} catch(Exception e)
				{
					Log.e("NextActivity", "error loading main activity");
					e.printStackTrace();
				}

			}

			@Override
			public void onExitedRegion(Region region)
			{
				Log.i("NextActivity", "onExitedRegion");
				NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNotifyMgr.cancel(001);

				editor.putString("rangeBeacon", "\"false\"").commit();
				String rangeBeacon = sharedPref.getString("rangeBeacon", "\"false\"");
				Log.i("NATIVE: ", rangeBeacon);
			}
		});

		beaconManager.connect(new BeaconManager.ServiceReadyCallback()
		{
			@Override
			public void onServiceReady()
			{
				try
				{
					Log.i("ScanService", "onServiceReady");
					// beaconManager.startRanging(region);
					beaconManager.setBackgroundScanPeriod(10 * 1000, 1 * 1000);
					beaconManager.setForegroundScanPeriod(10 * 1000, 1 * 1000);
					//					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
					beaconManager.startMonitoring(REGION_01);
				} catch(RemoteException e)
				{
					beaconManager.disconnect();
				}
			}
		});
	}

	private String region(Region region)
	{
		return "id[" + region.getIdentifier() + "] major[" + region.getMajor() + "] minor[" + region.getMinor() + "] ";
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
	}

	public final void showMessage(String message, int delay)
	{
		showMessage(Toast.makeText(this, message, delay), delay);
	}

	protected final void showMessage(final Toast toast, int duration)
	{
		toast.show();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

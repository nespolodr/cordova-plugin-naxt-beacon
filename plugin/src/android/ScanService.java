package br.com.naxt.sdk.service;

import java.util.List;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class ScanService extends Service
{
	//	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	private static final Region BEACON_30 = new Region("rid", "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 23772, 39582);
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
			//	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
			//			PendingIntent.FLAG_UPDATE_CURRENT);
			//	Notification notification = new Notification.Builder(context).setContentTitle("Iberika").setContentText("Voce tem uma notificacao")
			//			.setContentIntent(contentIntent).build();
				Notification notification = new Notification.Builder(context).setContentTitle("Iberika").setContentText("Voce tem uma notificacao")
						.build();
				NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNotifyMgr.notify(mNotificationId, notification);
			}

			@Override
			public void onExitedRegion(Region region)
			{
				Log.i("NextActivity", "onExitedRegion");
				NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNotifyMgr.cancel(001);
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
					beaconManager.setBackgroundScanPeriod(10 * 1000, 2 * 1000);
					beaconManager.setForegroundScanPeriod(10 * 1000, 2 * 1000);
					//					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
					beaconManager.startMonitoring(BEACON_30);
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

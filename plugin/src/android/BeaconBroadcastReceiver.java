package br.com.naxt.sdk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.com.naxt.sdk.service.ServiceUtils;

public class BeaconBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i("BeaconBroadcastReceiver", "onReceive");
		if(!ServiceUtils.isMyServiceRunning(context, ScanService.class))
		{
			Log.i("BeaconBroadcastReceiver", "Starting ScanService ... ...");
			Intent serviceIntent = new Intent(context, ScanService.class);
			context.startService(serviceIntent);
		}
	}
}

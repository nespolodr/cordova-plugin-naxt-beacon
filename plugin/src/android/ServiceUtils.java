package br.com.naxt.sdk.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils
{
	public static boolean isMyServiceRunning(Context context, Class<?> serviceClass)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if(serviceClass.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
}

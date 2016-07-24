package br.com.naxt.sdk.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import dalvik.system.DexFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

	public static String getFullName(Context context, String className) throws IOException
	{
		DexFile df = new DexFile(context.getPackageCodePath());
		Enumeration<String> iter = df.entries();
		while(iter.hasMoreElements())
		{
			String resource = iter.nextElement();
			if(resource.endsWith(className))
				return resource;
		}
		return null;
	}

}

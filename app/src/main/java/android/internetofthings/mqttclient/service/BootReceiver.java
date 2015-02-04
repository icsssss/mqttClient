package android.internetofthings.mqttclient.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * https://developer.motorolasolutions.com/docs/DOC-2315
 */

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		Log.d(getClass().getCanonicalName(), "onReceive");
		context.startService(new Intent(context, MQTTservice.class));
	}
}
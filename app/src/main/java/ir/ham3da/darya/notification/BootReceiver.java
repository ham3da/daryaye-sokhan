package ir.ham3da.darya.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.ham3da.darya.utility.AppSettings;


public class BootReceiver extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {
        AppSettings.Init(context.getApplicationContext());
       if( AppSettings.checkRandomNotifyIsActive() && !PoemService.getIsRunning())
       {
           Intent i = new Intent(context, PoemService.class);
           context.startService(i);
       }


    }

}
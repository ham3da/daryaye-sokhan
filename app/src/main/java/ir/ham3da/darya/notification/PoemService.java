package ir.ham3da.darya.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import ir.ham3da.darya.utility.AppSettings;

public class PoemService extends Service
{


    private static boolean isisRunning = false;
    String TAG = "PoemService";

    public PoemService()
    {
    }

    public static boolean getIsRunning()
    {
        return isisRunning;
    }

    public static void setIsisRunning(boolean isisRunning)
    {
        PoemService.isisRunning = isisRunning;
    }

    @Override
    public void onDestroy()
    {
        isisRunning = false;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        isisRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e(TAG, "onCreate");

        AppSettings.ReInit(getBaseContext());
        if (AppSettings.checkRandomNotifyIsActive())
        {
            startPoemAlarm();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    private void startPoemAlarm()
    {
        String getRandomNotifyTime = AppSettings.getRandomNotifyTime();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        Calendar calendar = Calendar.getInstance();

        String[] time = getRandomNotifyTime.split(":");

        Log.e(TAG, "startPoemAlarm: "+time[0] + ":" + time[1] );
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        myIntent = new Intent(this, AlarmNotificationReceiver.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        isisRunning = true;

        //if (!isRepeat)
        //manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000, pendingIntent);
        // else
    }

}

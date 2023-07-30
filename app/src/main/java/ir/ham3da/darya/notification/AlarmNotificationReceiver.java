package ir.ham3da.darya.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.Serializable;
import java.util.List;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorVerse;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.SerializableNotify;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class AlarmNotificationReceiver extends BroadcastReceiver
{
    String TAG = "AlarmNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {

        NotificationCompat.Builder builder;

        builder = new NotificationCompat.Builder(context, AppSettings.NOTIFICATION_CHANNEL_ID_DAILY);

        GanjoorDbBrowser ganjoorDbBrowser = new GanjoorDbBrowser(context.getApplicationContext());
        List<GanjoorVerse> ganjoorVerseList = ganjoorDbBrowser.getRandomPoemNotify();


        if (ganjoorVerseList == null)
        {
            return;
        }
        if (ganjoorVerseList.size() >= 2)
        {

            String rnd_poem_text = ganjoorVerseList.get(0)._Text + System.lineSeparator() + ganjoorVerseList.get(1)._Text;

            int poem_id = ganjoorVerseList.get(0)._PoemID;

            Intent myIntent = new Intent(context, ActivityMain.class);
            myIntent.setAction(Intent.ACTION_MAIN);
            myIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            SerializableNotify serializableNotify = new SerializableNotify(poem_id, ganjoorVerseList.get(0)._Text, ganjoorVerseList.get(0)._Order);

            myIntent.putExtra("serializableNotifyVerse", serializableNotify);

            Log.e(TAG, "rnd_poem_id: " + poem_id);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            {
                pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        myIntent,
                        PendingIntent.FLAG_MUTABLE);
            }
            else
            {
                pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        myIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            builder.setAutoCancel(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(ganjoorVerseList.get(0)._Text + "...")
                    .setContentIntent(pendingIntent)
                    .setSilent(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(rnd_poem_text));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {

                NotificationChannel channel = new NotificationChannel(AppSettings.NOTIFICATION_CHANNEL_ID_DAILY,
                        context.getString(R.string.daily_poem),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            notificationManager.notify(1, builder.build());

            Log.e(TAG, "onReceive: "+rnd_poem_text);

        }
    }
}

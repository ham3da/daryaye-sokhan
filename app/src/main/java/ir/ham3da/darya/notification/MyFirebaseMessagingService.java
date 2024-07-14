package ir.ham3da.darya.notification;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.PreferenceHelper;

import java.util.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    String TAG = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        Log.e("NEW_TOKEN_2", s);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        Map<String, String> remoteMessageData = remoteMessage.getData();

        String notify_title = remoteMessage.getNotification().getTitle();
        String notify_msg = remoteMessage.getNotification().getBody();
        String notify_url = remoteMessageData.get("MyUrl");
        String notify_url_text = remoteMessageData.get("MyUrlText");
        String notify_text = remoteMessageData.get("Text");

        PreferenceHelper PreferenceManager1 = new PreferenceHelper(getApplicationContext());

        PreferenceManager1.setKey("notify_title", notify_title);
        PreferenceManager1.setKey("notify_url", notify_url);
        PreferenceManager1.setKey("notify_text", notify_text);
        PreferenceManager1.setKey("MyUrlText", notify_url_text);

        // JSONObject json = new JSONObject(remoteMessage.getData());
        Log.e("notify_url", "url: " + notify_url);

        this.sendNotification(notify_msg, notify_title);
        //Log.e("getBody", remoteMessage.getNotification().getBody());
    }

    @Override
    public void onSendError(String var1, Exception var2)
    {
        Log.e("onSendError", "msg: " + var2.getMessage());
    }

    private void sendNotification(String messageBody, String messageTitle)
    {
        try
        {

            Intent notificationIntent = new Intent(getApplicationContext(), ActivityMain.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            {
                pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            }
            else
            {
                pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            }


            NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {

                mBuilder =
                        // Builder class for devices targeting API 26+ requires a channel ID
                        new NotificationCompat.Builder(this, AppSettings.default_notification_channel_id)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(messageTitle)
                                .setContentText(messageBody);
            }
            else
            {
                mBuilder =
                        // this Builder class is deprecated
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(messageTitle)
                                .setContentText(messageBody);

            }

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            manager.notify(0, mBuilder.build());

        } catch (Exception ex) {
            Log.e("ExceptionNotification", ex.getMessage());
        }


    }
}
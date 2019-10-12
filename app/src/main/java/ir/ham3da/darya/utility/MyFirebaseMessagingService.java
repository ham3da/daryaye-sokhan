package ir.ham3da.darya.utility;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;

import java.util.Map;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "MyFirebaseMessagingService";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN_2", s);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
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
        Log.e("notify_url","url: "+ notify_url);

        this.sendNotification(notify_msg, notify_title);
        //Log.e("getBody", remoteMessage.getNotification().getBody());
    }

    @Override
    public void onSendError(String var1, Exception var2) {
        Log.e("onSendError", "msg: "+ var2.getMessage());
    }

    @SuppressWarnings("deprecation")
    private void sendNotification(String messageBody, String messageTitle) {
        try {

            Intent notificationIntent = new Intent(getApplicationContext(), ActivityMain.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                mBuilder =
                        // Builder class for devices targeting API 26+ requires a channel ID
                        new NotificationCompat.Builder(this, "darya")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(messageTitle)
                                .setContentText(messageBody);
            } else {
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

            manager.notify(0, mBuilder.build());

        } catch (Exception ex) {
            Log.e("ExceptionNotification", ex.getMessage());
        }


    }
}
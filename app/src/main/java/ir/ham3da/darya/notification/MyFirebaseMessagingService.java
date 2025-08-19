package ir.ham3da.darya.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.PreferenceHelper;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
       // Log.e(TAG, "New FCM Token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("general_nots")
                .addOnCompleteListener(subTask -> {
                    if (subTask.isSuccessful()) {
                        Log.d("FCM", "subscribe To Topic is Successful");
                    }
                });

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationChannels.createAllChannels(getApplicationContext());

        Map<String, String> data = remoteMessage.getData();

        String notifyTitle = null;
        String notifyBody = null;

        if (remoteMessage.getNotification() != null) {
            notifyTitle = remoteMessage.getNotification().getTitle();
            notifyBody = remoteMessage.getNotification().getBody();
        }

        String notifyUrl = data.get("MyUrl");
        String notifyUrlText = data.get("MyUrlText");
        String notifyText = data.get("Text");

        PreferenceHelper prefs = new PreferenceHelper(getApplicationContext());
        prefs.setKey("notify_title", notifyTitle);
        prefs.setKey("notify_url", notifyUrl);
        prefs.setKey("notify_text", notifyText);
        prefs.setKey("MyUrlText", notifyUrlText);

        Log.e(TAG, "Notification URL: " + notifyUrl);

        sendNotification(notifyBody, notifyTitle);
    }

    private void sendNotification(String messageBody, String messageTitle) {
        try {
            if (messageTitle == null || messageBody == null) {
                return;
            }
            Intent intent = new Intent(this, ActivityMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannels.CHANNEL_ID_FCM)
                    .setSmallIcon(R.drawable.ic_artboard1)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Notification permission not granted");
                    return;
                }
            }

            notificationManager.notify(1001, builder.build());

        } catch (Exception e) {
            Log.e(TAG, "Notification Exception: " + e.getMessage());
        }
    }
}
package ir.ham3da.darya.notification;
import ir.ham3da.darya.R;
import android.content.Context;
import android.os.Build;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannels {

    public static final String CHANNEL_ID_DAILY_POEM = "daily_poem_channel";
    public static final String CHANNEL_ID_FCM = "fcm_channel";

    public static void createAllChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // کانال شعر روزانه
            NotificationChannel poemChannel = new NotificationChannel(
                    CHANNEL_ID_DAILY_POEM,
                    context.getString(R.string.daily_poem),
                    NotificationManager.IMPORTANCE_LOW
            );
            poemChannel.setDescription(context.getString(R.string.daily_poem_switch));
            manager.createNotificationChannel(poemChannel);

            // کانال اعلان‌های فایربیس
            NotificationChannel fcmChannel = new NotificationChannel(
                    CHANNEL_ID_FCM,
                    context.getString(R.string.GeneralNotifications),
                    NotificationManager.IMPORTANCE_HIGH
            );
            fcmChannel.setDescription(context.getString(R.string.GeneralNotsDes));
            manager.createNotificationChannel(fcmChannel);
        }
    }
}
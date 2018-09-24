package com.arunditti.android.todolist.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.ui.activities.AddTaskActivity;
import com.arunditti.android.todolist.ui.activities.MainActivity;

/**
 * Created by arunditti on 9/24/18.
 */

public class NotificationUtils {

    private static final int TASK_REMINDER_NOTIFICATION_ID = 100;
    private static final int TASK_REMINDER_PENDING_INTENT_ID = 200;
    private static final String TASK_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";

    public static void remindUser(Context context) {
        // Get the NotificationManager using context.getSystemService
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    TASK_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        // In the remindUser method use NotificationCompat.Builder to create a notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,TASK_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        // f the build version is greater than JELLY_BEAN and lower than OREO, set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // Trigger the notification by calling notify on the NotificationManager. Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(TASK_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    // This method will create the pending intent which will trigger when the notification is pressed. This pending intent should open up the MainActivity.
    private static PendingIntent contentIntent(Context context) {
        // Create an intent that opens up the MainActivity
        Intent startActivityIntent = new Intent(context, AddTaskActivity.class);

        return PendingIntent.getActivity(
                context,
                TASK_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
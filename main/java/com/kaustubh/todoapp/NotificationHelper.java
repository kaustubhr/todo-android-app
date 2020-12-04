/*package com.kaustubh.todoapp;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import com.google.firebase.messaging.RemoteMessage;

public class NotificationHelper {
    public static void  displayNotification(Context context,String title,String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,TodoTasks.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_check_circle_24)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1,mBuilder.build());


    }
}
*/
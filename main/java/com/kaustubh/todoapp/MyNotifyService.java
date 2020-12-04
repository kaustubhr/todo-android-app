package com.kaustubh.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyNotifyService extends IntentService {
    String title;
    public MyNotifyService() {
        super("MyNotifyService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String body = "press here to open app";
        this.title = intent.getStringExtra("title");

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,TodoTasks.CHANNEL_ID)
                        .setChannelId(TodoTasks.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_check_circle_24)
                        .setContentTitle(this.title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setColor(Color.argb(50,255,0,0))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(PendingIntent.getActivity(this,0,new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
        //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
        //r.play();

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(this);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel(TodoTasks.CHANNEL_ID,TodoTasks.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);
            mNotificationMgr.createNotificationChannel(mChannel);
        }

        mNotificationMgr.notify(1,mBuilder.build());
        Log.d("MyNotifyService","inside MyNotifyService onHandleIntent");
    }
}
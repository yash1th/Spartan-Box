package com.kirandama.spartanbox.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kirandama.spartanbox.activities.MainActivity;
import com.kirandama.spartanbox.activities.UserActivity;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by sasankmacherla on 12/9/15.
 */
public class Receiver extends ParsePushBroadcastReceiver {

//    protected Class<? extends Activity> getActivity(Context context,
//                                                    Intent intent) {
//        return MainActivity.class;
//    }

//    @Override
//    public void onPushOpen(Context context, Intent intent) {
//        Intent i = new Intent(context, MainActivity.class);
//        i.putExtras(intent.getExtras());
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//    }

    NotificationManager mNotificationManager;
    int notification_id = 0;

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        mNotificationManager.cancel(notification_id);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager)context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        super.onReceive(context, intent);

    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification n = super.getNotification(context, intent);
        notification_id = intent.getExtras().getInt("NOTIFICATION_TYPE");
        mNotificationManager.notify(notification_id, n);
        return null;
    }
}

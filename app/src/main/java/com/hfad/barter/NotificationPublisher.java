package com.hfad.barter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.N;
import static java.lang.Integer.parseInt;


public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: beginning");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        String id = intent.getStringExtra(NOTIFICATION_ID);
        notificationManager.notify(parseInt(id),notification);
    }
}

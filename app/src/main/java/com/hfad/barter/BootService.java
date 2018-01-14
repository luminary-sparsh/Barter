package com.hfad.barter;

import android.app.IntentService;
import android.content.Intent;


public class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    private void setAlarm() {
        // Set your alarm here as you do in "1. First I set an alarm in alarm manager"
    }

    private void setAlarmsFromDatabase() {
        // Set your alarms from database here
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setAlarm();
        setAlarmsFromDatabase(); // A nice a approach is to store alarms on a database, you may not need it
        Intent service = new Intent(this, BootService.class);
        stopService(service);
    }

}

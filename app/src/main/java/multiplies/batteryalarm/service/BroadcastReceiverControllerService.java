package multiplies.batteryalarm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import multiplies.batteryalarm.activity.MainActivity;

public class BroadcastReceiverControllerService extends Service {
    public BroadcastReceiverControllerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("BTAG", "Service, onCreate(1), Line 21");
        getApplicationContext().registerReceiver(MainActivity.batteryReceiver, MainActivity.intentFilter);
        Log.i("BTAG", "Service, onCreate(3), Line 23");
    }

    @Override
    public void onDestroy() {
        Log.i("BTAG", "Service, onDestroy(1), Line 28");
        getApplicationContext().unregisterReceiver(MainActivity.batteryReceiver);
        Log.i("BTAG", "Service, onDestroy(3), Line 30");
    }
}

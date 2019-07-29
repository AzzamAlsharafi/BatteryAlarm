package multiplies.batteryalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import multiplies.batteryalarm.service.BroadcastReceiverControllerService;

public class PowerConnectingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BTAG", "BroadcastReceiver P, onReceive(1), Line 14");
        switch (intent.getAction()){
            case Intent.ACTION_POWER_CONNECTED:
                Log.i("BTAG", "BroadcastReceiver PC, onReceive(4), Line 17");
                context.startService(new Intent(context, BroadcastReceiverControllerService.class));
                Log.i("BTAG", "BroadcastReceiver PC, onReceive(6), Line 19");
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Log.i("BTAG", "BroadcastReceiver PD, onReceive(9), Line 22");
                context.stopService(new Intent(context, BroadcastReceiverControllerService.class));
                Log.i("BTAG", "BroadcastReceiver PD, onReceive(11), Line 24");
                if(BatteryReceiver.mediaPlayer != null && BatteryReceiver.mediaPlayer.isPlaying()){
                    Log.i("BTAG", "BroadcastReceiver PD(if), onReceive(13), Line 26");
                    BatteryReceiver.mediaPlayer.stop();
                }
                break;
            default:
                Log.i("BTAG", "BroadcastReceiver P(default), onReceive(18), Line 32");
        }
    }
}

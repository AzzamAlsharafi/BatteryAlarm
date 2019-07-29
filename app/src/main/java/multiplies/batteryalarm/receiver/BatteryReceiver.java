package multiplies.batteryalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import multiplies.batteryalarm.R;
import multiplies.batteryalarm.activity.AlarmActivity;
import multiplies.batteryalarm.activity.MainActivity;

public class BatteryReceiver extends BroadcastReceiver {

    public static String BATTERY_LEVEL = "battery_level";
    private Uri uri;
    public static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BTAG", "BroadcastReceiver B, onReceive(1), Line 29");
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(MainActivity.instance != null){
            MainActivity.instance.updateInfo(intent);
        }

        int AVAILABLE_TYPE = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM) != null ?
                RingtoneManager.TYPE_ALARM : RingtoneManager.TYPE_RINGTONE;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        if (preferences.getBoolean(MainActivity.ALARM_STATE_KEY, true)
                && level >= preferences.getInt(MainActivity.ALARM_PERCENTAGE_KEY, 100)
                && intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) != 0
                && (mediaPlayer == null || !mediaPlayer.isPlaying())) {

            uri = defaultPreferences.getBoolean(context.getString(R.string.original_alarm_ringtone_key), true) ?
                    RingtoneManager.getActualDefaultRingtoneUri(context, AVAILABLE_TYPE) :
                    Uri.parse(defaultPreferences.getString(context.getString(R.string.alarm_ringtone_key), ""));

            if(uri.toString().equals(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())
                    && RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM) == null){
                uri = Uri.parse("");
            }

            if(uri.toString().isEmpty()){
                uri = RingtoneManager.getActualDefaultRingtoneUri(context, AVAILABLE_TYPE);
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent alarmActivityIntent = new Intent(context, AlarmActivity.class);
            alarmActivityIntent.putExtra(BATTERY_LEVEL, level);
            alarmActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmActivityIntent);
        }
    }
}

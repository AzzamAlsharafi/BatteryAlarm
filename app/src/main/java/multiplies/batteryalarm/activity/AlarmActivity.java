package multiplies.batteryalarm.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

import multiplies.batteryalarm.R;
import multiplies.batteryalarm.receiver.BatteryReceiver;

public class AlarmActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        textView = findViewById(R.id.textView);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            if(BatteryReceiver.mediaPlayer != null && BatteryReceiver.mediaPlayer.isPlaying()){
                BatteryReceiver.mediaPlayer.stop();
            }
            this.finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        textView.setText(String.format(Locale.getDefault(), getString(R.string.alarm_activity_format_message), getIntent().getIntExtra(BatteryReceiver.BATTERY_LEVEL, -1), "%"));
    }
}

package multiplies.batteryalarm.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import multiplies.batteryalarm.R;
import multiplies.batteryalarm.dialog.SetPercentageDialog;
import multiplies.batteryalarm.receiver.BatteryReceiver;
import multiplies.batteryalarm.service.BroadcastReceiverControllerJobService;
import multiplies.batteryalarm.service.BroadcastReceiverControllerService;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    public static BatteryReceiver batteryReceiver = new BatteryReceiver();
    public static IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    public static String PREFERENCES_NAME = "my_preferences";
    public static String ALARM_STATE_KEY = "alarm_state";
    public static String ALARM_PERCENTAGE_KEY = "alarm_percentage";
    public static int JOB_ID = 100;
    public static MainActivity instance = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();

        if(Build.VERSION.SDK_INT >= 26) {
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, BroadcastReceiverControllerJobService.class));
            builder.setRequiresCharging(true);
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        }else {
            Intent batteryIntent = registerReceiver(null, intentFilter);
            if((batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 1) : 1) != 0) {
                startService(new Intent(this, BroadcastReceiverControllerService.class));
            }
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> new SetPercentageDialog().show(getFragmentManager(), "tag"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem switchItem = menu.findItem(R.id.action_switch);
        Switch aSwitch =(Switch) switchItem.getActionView();
        aSwitch.setChecked(preferences.getBoolean(ALARM_STATE_KEY, true));
        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean(ALARM_STATE_KEY, b);
            editor.commit();
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent batteryIntent = registerReceiver(null, intentFilter);
        updateInfo(batteryIntent);
        instance = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        instance = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, PrefsActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void updateInfo(Intent intent){
        Log.i("BTAG", "Activity, updateInfo(1), Line 109");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        int value;

        if(width <= height){
            value = width / 10;
        }else {
            value = width / 10;
        }

        int value2 = value * 2;


        //Health
        ImageView health = findViewById(R.id.health);
        ViewGroup.MarginLayoutParams healthParams = (ViewGroup.MarginLayoutParams) health.getLayoutParams();
        healthParams.width = value2;
        healthParams.height = value2;
        healthParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            healthParams.setMarginStart(value);
        }
        health.setLayoutParams(healthParams);

        switch (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 1)){
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknown));
                health.setContentDescription(getString(R.string.unknown_health));
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_good));
                health.setContentDescription(getString(R.string.good_health));
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_overheat));
                health.setContentDescription(getString(R.string.overheat_health));
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_dead));
                health.setContentDescription(getString(R.string.dead_health));
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_over_voltage));
                health.setContentDescription(getString(R.string.over_voltage_health));
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_failure));
                health.setContentDescription(getString(R.string.unspecified_failure_health));
                break;
            case BatteryManager.BATTERY_HEALTH_COLD:
                health.setImageDrawable(getResources().getDrawable(R.drawable.ic_cold));
                health.setContentDescription(getString(R.string.cold_health));
        }

        health.setOnClickListener(this);


        //Plugged
        ImageView plugged = findViewById(R.id.plugged);
        ViewGroup.MarginLayoutParams pluggedParams = (ViewGroup.MarginLayoutParams) plugged.getLayoutParams();
        pluggedParams.width = value2;
        pluggedParams.height = value2;
        pluggedParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            pluggedParams.setMarginStart(value);
        }
        plugged.setLayoutParams(pluggedParams);

        switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 5)){
            case 0:
                plugged.setImageDrawable(getResources().getDrawable(R.drawable.ic_unplugged));
                plugged.setContentDescription(getString(R.string.nothing_source));
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugged.setImageDrawable(getResources().getDrawable(R.drawable.ic_plugged_ac));
                plugged.setContentDescription(getString(R.string.ac_source));
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugged.setImageDrawable(getResources().getDrawable(R.drawable.ic_plugged_usb));
                plugged.setContentDescription(getString(R.string.usb_source));
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugged.setImageDrawable(getResources().getDrawable(R.drawable.ic_charging_wireless));
                plugged.setContentDescription(getString(R.string.wireless_source));
                break;
            default:
                plugged.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknown));
                plugged.setContentDescription(getString(R.string.unknown_source));
        }

        plugged.setOnClickListener(this);


        //Status
        ImageView status = findViewById(R.id.status);
        ViewGroup.MarginLayoutParams statusParams = (ViewGroup.MarginLayoutParams) status.getLayoutParams();
        statusParams.width = value2;
        statusParams.height = value2;
        statusParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            statusParams.setMarginStart(value);
        }
        status.setLayoutParams(statusParams);

        switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 1)){

            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                status.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknown));
                status.setContentDescription(getString(R.string.unknown_status));
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                status.setImageDrawable(getResources().getDrawable(R.drawable.ic_charging_s));
                status.setContentDescription(getString(R.string.charging_status));
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                status.setImageDrawable(getResources().getDrawable(R.drawable.ic_discharging_s));
                status.setContentDescription(getString(R.string.discharging_status));
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                status.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_charging_s));
                status.setContentDescription(getString(R.string.not_charging_status));
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                status.setImageDrawable(getResources().getDrawable(R.drawable.ic_full_s));
                status.setContentDescription(getString(R.string.full_status));
        }

        status.setOnClickListener(this);


        //Technology
        TextView technology = findViewById(R.id.technology);
        ViewGroup.MarginLayoutParams technologyParams = (ViewGroup.MarginLayoutParams) technology.getLayoutParams();
        technologyParams.width = value2;
        technologyParams.height = value2;
        technologyParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            technologyParams.setMarginStart(value);
        }
        technology.setLayoutParams(technologyParams);

        technology.setText(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
        technology.setTextSize((technology.length()-2) * -5 + 40);
        technology.setContentDescription(getString(R.string.tech) + technology.getText().toString());
        technology.setOnClickListener(this);


        //Temperature
        TextView temperature = findViewById(R.id.temperature);
        ViewGroup.MarginLayoutParams temperatureParams = (ViewGroup.MarginLayoutParams) temperature.getLayoutParams();
        temperatureParams.width = value2;
        temperatureParams.height = value2;
        temperatureParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            temperatureParams.setMarginStart(value);
        }
        temperature.setLayoutParams(temperatureParams);

        int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -999999999);
        if(temp == -999999999){
            temperature.setBackground(getResources().getDrawable(R.drawable.ic_unknown));
            temperature.setText("");
            temperature.setContentDescription(getString(R.string.unknown_temp));
        }else{
            temperature.setText(String.format(getString(R.string.temp_format), temp / 10));
            temperature.setContentDescription(getString(R.string.temp) + temperature.getText().toString());
        }
        temperature.setOnClickListener(this);


        //Voltage
        TextView voltage = findViewById(R.id.voltage);
        ViewGroup.MarginLayoutParams voltageParams = (ViewGroup.MarginLayoutParams) voltage.getLayoutParams();
        voltageParams.width = value2;
        voltageParams.height = value2;
        voltageParams.setMargins(value, value,0, 0);
        if(Build.VERSION.SDK_INT > 17){
            voltageParams.setMarginStart(value);
        }
        voltage.setLayoutParams(voltageParams);

        int volt = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -999999999);
        if(volt == -999999999){
            voltage.setBackground(getResources().getDrawable(R.drawable.ic_unknown));
            voltage.setText("");
            voltage.setContentDescription(getString(R.string.unknown_voltage));
        }else{
            voltage.setText(String.format(getString(R.string.voltage_format), (float) volt / 1000));
            voltage.setContentDescription(getString(R.string.voltage) + voltage.getText().toString());
        }
        voltage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, view.getContentDescription(), Toast.LENGTH_SHORT).show();
    }
}

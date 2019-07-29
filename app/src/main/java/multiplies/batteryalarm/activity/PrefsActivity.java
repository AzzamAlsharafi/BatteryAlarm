package multiplies.batteryalarm.activity;

import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import multiplies.batteryalarm.R;

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        getFragmentManager().beginTransaction().add(R.id.prefs_frame, new SettingsFragment()).commit();


    }

    public static class SettingsFragment extends PreferenceFragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Preference checkboxPref = getPreferenceScreen().findPreference(getString(R.string.original_alarm_ringtone_key));
            Preference ringtonePref = getPreferenceScreen().findPreference(getString(R.string.alarm_ringtone_key));


            ringtonePref.setEnabled(!preferences.getBoolean(checkboxPref.getKey(), true));

            if(!preferences.getString(ringtonePref.getKey(), "").isEmpty()) {
                ringtonePref.setSummary(RingtoneManager
                        .getRingtone(getActivity(), Uri.parse(preferences.getString(ringtonePref.getKey(), "")))
                        .getTitle(getActivity()));
            }

            checkboxPref.setOnPreferenceChangeListener((preference, o) -> {
                ringtonePref.setEnabled(!Boolean.parseBoolean(o.toString()));
                return true;
            });

            ringtonePref.setOnPreferenceChangeListener((preference, o) -> {
                if (!o.toString().isEmpty()) {
                    ringtonePref.setSummary(RingtoneManager
                            .getRingtone(getActivity(), Uri.parse(o.toString()))
                            .getTitle(getActivity()));
                } else {
                    ringtonePref.setSummary("");
                }
                return true;
            });
        }
    }
}

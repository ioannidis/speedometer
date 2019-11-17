package eu.ioannidis.speedometer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import eu.ioannidis.speedometer.config.SharedPreferencesConfig;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferencesConfig sharedPreferencesConfig;

    private EditTextPreference speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        System.out.println("========= Settings Activity ====================================");
        settings.getAll().forEach((k,v) -> System.out.println(k + v));
        System.out.println(settings.getString("speed_limit_value", "aaaa"));

//        SharedPreferences rootPreferences =
//                PreferenceManager.getDefaultSharedPreferences(this);
//        int speedLimit = rootPreferences.getInt("speed_limit", 50);
//        System.out.println("========= Settings Activity ====================================");
//        System.out.println(speedLimit);

//        SharedPreferences speedometerPreferences = getSharedPreferences("speedometer_preferences", MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = speedometerPreferences.edit();
//        editor.putInt("speed_limit_value", rootPreferences.getInt("speed_limit", 50));
//        editor.apply();

//        Toast.makeText(this, "The speedo pref is " + speedLimit, Toast.LENGTH_LONG).show();

    }



    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

}
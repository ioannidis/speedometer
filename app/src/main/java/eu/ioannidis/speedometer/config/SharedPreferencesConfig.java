package eu.ioannidis.speedometer.config;

import android.content.Context;
import android.content.SharedPreferences;

import eu.ioannidis.speedometer.R;

public class SharedPreferencesConfig {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferencesConfig(Context context) {
        this.context = context;
//        sharedPreferences = context.getSharedPreferences(context.getResources().getString(), context.MODE_PRIVATE);
    }

//    public void setSpeedPreference(int speed) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(context.getResources().getString(R.string.speed_preference), speed);
//        editor.apply();
//    }
//
//    public int getSpeedPreference() {
//        return sharedPreferences.getInt(context.getResources().getString(R.string.speed_preference), 50);
//    }
}

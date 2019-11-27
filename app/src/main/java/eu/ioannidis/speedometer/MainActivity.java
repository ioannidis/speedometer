package eu.ioannidis.speedometer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import eu.ioannidis.speedometer.config.DatabaseConfig;
import eu.ioannidis.speedometer.ui.main.PlaceholderFragment;
import eu.ioannidis.speedometer.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements ViolationsFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener,
        LocationListener, TextToSpeech.OnInitListener {

    TextView speedTextView;
    Button enableButton;
    Button disableButton;
    FloatingActionButton speechRecognitionButton;
    Boolean isEnabled = true;

    private boolean speedViolation = false;

    private SharedPreferences sharedPreferences;
    private int speedLimit;

    private TextToSpeech textToSpeech;

    private SpeechRecognizer speechRecognizer;

    private DatabaseConfig dbHandler = new DatabaseConfig(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        /*FloatingActionButton speechRecognitionButton = findViewById(R.id.speech_recognition_fab);
        speechRecognitionButton.setOnClickListener((View view) -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2000);
            } else {
                System.out.println("FAB clicked");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

                speechRecognizer.startListening(intent);
            }
        });*/

    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage(Locale.ENGLISH);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("DEBUG" , "Language Not Supported");
            }
        }
        else{
            Log.i("DEBUG" , "MISSION FAILED");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the speech recognizer and text to speech engines upon resuming from background
        initializeSpeechRecognition();
        textToSpeech = new TextToSpeech(this, this);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.violations:
                intent = new Intent(this, ViolationsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.map:
                intent = new Intent(this, MapsActivity.class);
                this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println(uri);
    }
}
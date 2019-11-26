package eu.ioannidis.speedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import eu.ioannidis.speedometer.config.DatabaseConfig;
import eu.ioannidis.speedometer.models.ViolationModel;
import eu.ioannidis.speedometer.service.SpeechRecognitionService;

import static androidx.preference.PreferenceManager.setDefaultValues;

public class MainActivity extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SpeechRecognitionService.createThread();
        initializeSpeechRecognition();

//        System.out.println((new Timestamp(System.currentTimeMillis())).toString());
//        ViolationModel speedViolationModel = new ViolationModel(100.25214, 200.2569, 50, new Timestamp(System.currentTimeMillis()));
//        dbHandler.addViolation(speedViolationModel);
        System.out.println("======== From db =============================");
        dbHandler.getViolations().forEach(System.out::println);
        System.out.println("======== End From db =============================");


        // Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speedLimit = Integer.valueOf(sharedPreferences.getString("speed_limit_value", "50"));


        speedTextView = findViewById(R.id.speedTextView);

        enableButton = findViewById(R.id.enableButton);
        enableButton.setBackgroundColor(Color.parseColor("#00796b"));
        enableButton.setTextColor(Color.WHITE);
        enableButton.setOnClickListener((View view) -> {
            isEnabled = true;
            buttonBgColor();
            accessData();
            Toast.makeText(this, "Speed capture is enabled", Toast.LENGTH_SHORT).show();
        });

        disableButton = findViewById(R.id.disableButton);
        disableButton.setBackgroundColor(Color.LTGRAY);
        disableButton.setTextColor(Color.DKGRAY);
        disableButton.setOnClickListener((View view) -> {
            isEnabled = false;
            buttonBgColor();
            speedTextView.setText("---");
            Toast.makeText(this, "Speed capture is disabled", Toast.LENGTH_SHORT).show();
        });

        speechRecognitionButton = findViewById(R.id.speech_recognition_fab);
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
        });

        // Check permission for location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            accessData();
        }

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Initialize text to speech
        textToSpeech = new TextToSpeech(this, this);
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        textToSpeech.stop();
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the speech recognizer and text to speech engines upon resuming from background
        initializeSpeechRecognition();
        textToSpeech = new TextToSpeech(this, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                accessData();
            else
                finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (isEnabled) {
                float currentSpeed = location.getSpeed();
                speedTextView.setText(String.valueOf(SpeedConverter.mPerSecToKmPerHr(currentSpeed)));

                speedLimit = Integer.valueOf(sharedPreferences.getString("speed_limit_value", "50"));

                if (currentSpeed > speedLimit) {
                    textToSpeech.speak("Caution! You have exceeded the speed limit.", TextToSpeech.QUEUE_ADD, null, null);
                } else {
                    textToSpeech.stop();
                }

                if (currentSpeed <= speedLimit && speedViolation) {
                    speedViolation = false;
                }

                if (currentSpeed > speedLimit && !speedViolation) {
                    speedViolation = true;
                    ViolationModel violationModel = new ViolationModel(location.getLongitude(), location.getLatitude(), SpeedConverter.mPerSecToKmPerHr(currentSpeed), new Timestamp(System.currentTimeMillis()));
                    dbHandler.addViolation(violationModel);
                    Toast.makeText(this, violationModel.toString(), Toast.LENGTH_LONG).show();
                }

            }
        } else {
            speedTextView.setText("---");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @SuppressLint("MissingPermission")
    private void accessData() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && isEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection", Toast.LENGTH_SHORT).show();
    }

    private void buttonBgColor() {
        if (isEnabled) {
            enableButton.setBackgroundColor(Color.parseColor("#00796b"));
            enableButton.setTextColor(Color.WHITE);
            disableButton.setBackgroundColor(Color.LTGRAY);
            disableButton.setTextColor(Color.DKGRAY);
        } else {
            enableButton.setBackgroundColor(Color.LTGRAY);
            enableButton.setTextColor(Color.DKGRAY);
            disableButton.setBackgroundColor(Color.parseColor("#00796b"));
            disableButton.setTextColor(Color.WHITE);
        }
    }

    private void initializeSpeechRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );
                    processSpeechResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    // Process the command gathered from microphone
    private void processSpeechResult(String command) {
        command = command.toLowerCase();

        if (command.contains("speed") || command.contains("speedometer")) {

            if (command.contains("start")) {
                enableButton.performClick();
            }else if (command.contains("stop")) {
                disableButton.performClick();
            } else {
                Toast.makeText(this, "Available commands are 'start' or 'stop'", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Initialize speech recognition say 'speed' or 'speedometer' following by your command!", Toast.LENGTH_LONG).show();
            textToSpeech.speak("Starting with 'speed' or speedometer following by your command!", TextToSpeech.QUEUE_ADD, null, null);
        }


    }

}

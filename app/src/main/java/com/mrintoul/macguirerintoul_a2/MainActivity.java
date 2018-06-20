package com.mrintoul.macguirerintoul_a2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private RecyclerView sensorRecyclerView; // get the recyclerview used for listing sensors
    private RecyclerView.Adapter sensorAdapter; // get the adapter used for loading sensors into recyclerview
    private RecyclerView.LayoutManager sensorLayoutManager; // get the layoutmanager used by recyclerview
    public static SensorManager sensorManager; // create a sensormanager
    public static List<Sensor> sensorList; // create a list to hold the onboard sensors
    private Vibrator vibrator; // create a vibrator
    private Button motionActivityButton, soundButton; // create variables for buttons
    private boolean hasVibrated, micPresent; // create boolean variables
    private SoundMeter soundMeter; // create a soundmeter variable
    private double amplitude; // create a variable for the audio amplitude

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); // instantiate the sensor manager
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL); // populate the sensor list

        sensorRecyclerView = findViewById(R.id.sensorRecyclerView); // find the recyclerview
        motionActivityButton = findViewById(R.id.motionActivityButton); // find the motion activity button
        soundButton = findViewById(R.id.soundButton); // find the sound detector button

        sensorLayoutManager = new LinearLayoutManager(this); // create a linear layout manager
        sensorRecyclerView.setLayoutManager(sensorLayoutManager); // set the linear layout manager as the recyclerview layout manager

        sensorAdapter = new MyAdapter(sensorList); // instantiate an adapter for the recyclerview
        sensorRecyclerView.setAdapter(sensorAdapter); // set that adapter as the one for the recyclerview

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); // get the vibrator service

        // set click listeners
        motionActivityButton.setOnClickListener(this);
        soundButton.setOnClickListener(this);

        PackageManager pm = getPackageManager(); // get the package manager
        micPresent = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE); // check for an onboard microphone
        soundMeter = new SoundMeter(); // instantiate a soundmeter to check audio levels
    }

    // check the amplitude read by the microphone. called every 1000ms by a timer
    public void checkAudio() {
        amplitude = soundMeter.getAmplitude(); // get the amplitude from the soundmeter object
        soundButton.setText(Double.toString(amplitude)); // set the text on the sound button to the value
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start reading values from sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        soundButton.setText("SOUND DETECTOR");
        sensorManager.unregisterListener(this); // stop reading values from sensors
        soundMeter.stop(); // stop listening via the microphone
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType(); // get the type of the changed values' sensor
        float[] values = event.values; // put the sensor's values into an array
        if (type == Sensor.TYPE_LIGHT && values[0] < 5) {
            // beep if the light sensor reads lower than 5 illuminance
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP,500);
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            // calculate how much the device is tilted
            float x = values[0];
            float y = values[1];
            float z = values[2];
            float norm_Of_g = (float) Math.sqrt(x * x + y * y + z * z);
            z = (z / norm_Of_g);
            int tilt = (int) Math.round(Math.toDegrees(Math.acos(z)));

            // if the device is laying flat...
            if (tilt < 15 || tilt > 165) {
                if (!hasVibrated) {
                    // and hasn't vibrated already...
                    if (vibrator.hasVibrator()) {
                        // and can vibrate...
                        vibrator.vibrate(5000); //vibrate for 5 seconds
                        Toast.makeText(this, "device flat - beep", Toast.LENGTH_SHORT).show(); // show a toast
                    } else {
                        // device does not have a vibrator
                        Toast.makeText(this, "device flat - beep (no vibrator)", Toast.LENGTH_SHORT).show();
                    }
                    hasVibrated = true; // device has now vibrated from this 'flatness'. wait for a separate 'flatness'
                }
            } else {
                // the device has come out of the 'flat' position, so reset the vibration for this 'flatness'
                hasVibrated = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.motionActivityButton) {
            // take us to the motion detector activity
            Intent i = new Intent(v.getContext(), MotionActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.soundButton) {
            // start reading sound level
            if (!micPresent) {
                // sorry, no microphone here :(
                Toast.makeText(this, "No microphone on this device.", Toast.LENGTH_SHORT).show();
                return;
            }
            soundMeter.start(); // start reading microphone level
            // create a timer and check the audio level every second
            Timer t = new Timer();
            t.scheduleAtFixedRate(
                    new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    checkAudio();
                                }
                            });
                        }
                    },
                    0,      // run first occurrence immediately
                    1000 // run every second
            );
        }
    }
}

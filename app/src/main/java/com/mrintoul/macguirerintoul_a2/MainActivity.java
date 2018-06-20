package com.mrintoul.macguirerintoul_a2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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
    private RecyclerView sensorRecyclerView;
    private RecyclerView.Adapter sensorAdapter;
    private RecyclerView.LayoutManager sensorLayoutManager;
    public static SensorManager sensorManager;
    public static List<Sensor> sensorList;
    private Vibrator vibrator;
    private Button motionActivityButton, soundButton;
    boolean hasVibrated, micPresent;
    private SoundMeter soundMeter;
    private double amplitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        sensorRecyclerView = findViewById(R.id.sensorRecyclerView);

        sensorLayoutManager = new LinearLayoutManager(this);
        sensorRecyclerView.setLayoutManager(sensorLayoutManager);

        sensorAdapter = new MyAdapter(sensorList);
        sensorRecyclerView.setAdapter(sensorAdapter);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        motionActivityButton = findViewById(R.id.motionActivityButton);
        motionActivityButton.setOnClickListener(this);

        soundButton = findViewById(R.id.soundButton);
        soundButton.setOnClickListener(this);
        PackageManager pm = getPackageManager();
        micPresent = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        soundMeter = new SoundMeter();
    }

    public void checkAudio() {
        amplitude = soundMeter.getAmplitude();
        soundButton.setText(Double.toString(amplitude));
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        soundMeter.stop();
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] values = event.values;
        if (type == Sensor.TYPE_LIGHT && values[0] < 5) {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,500);
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            float x = values[0];
            float y = values[1];
            float z = values[2];
            float norm_Of_g = (float) Math.sqrt(x * x + y * y + z * z);
            z = (z / norm_Of_g);
            int tilt = (int) Math.round(Math.toDegrees(Math.acos(z)));
            if (tilt < 15 || tilt > 165) {
                if (!hasVibrated) {
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(5000); //vibrate for 5 seconds
                        Toast.makeText(this, "device flat - beep", Toast.LENGTH_SHORT).show();
                    } else {
                        // device does not have a vibrator
                        Toast.makeText(this, "device flat - beep (no vibrator)", Toast.LENGTH_SHORT).show();
                    }
                    hasVibrated = true;
                }
            } else {
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
            Intent i = new Intent(v.getContext(), MotionActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.soundButton) {
            if (!micPresent) {
                Toast.makeText(this, "No microphone on this device.", Toast.LENGTH_SHORT).show();
                return;
            }
            soundMeter.start();
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

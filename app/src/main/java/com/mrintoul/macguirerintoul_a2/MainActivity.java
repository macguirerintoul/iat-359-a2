package com.mrintoul.macguirerintoul_a2;

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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private RecyclerView sensorRecyclerView;
    private RecyclerView.Adapter sensorAdapter;
    private RecyclerView.LayoutManager sensorLayoutManager;
    public static SensorManager sensorManager;
    public static List<Sensor> sensorList;
    private Vibrator vibrator;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
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
            float norm_Of_g =(float) Math.sqrt(x * x + y * y + z * z);
            z = (z / norm_Of_g);
            int tilt = (int) Math.round(Math.toDegrees(Math.acos(z)));
            if (tilt < 15 || tilt > 165) {
                if(vibrator.hasVibrator()) {
                    vibrator.vibrate(5000); //vibrate for 5 seconds
                    Toast.makeText(this,"device flat - beep",Toast.LENGTH_SHORT).show();
                }
                else {
                    // device does not have a vibrator
                    Toast.makeText(this, "device flat - beep (no vibrator)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

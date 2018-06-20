package com.mrintoul.macguirerintoul_a2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.mrintoul.macguirerintoul_a2.MainActivity.sensorManager;

public class MotionActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private Sensor accelerometer;
    private TextView motionTextView;
    private Button detectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        motionTextView = findViewById(R.id.motionTextView);
        detectButton = findViewById(R.id.detectButton);
        detectButton.setOnClickListener(this);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detectButton) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        if (    values[0] > 0.5
                || values[0] < -0.5
                || values[1] > 0.5
                || values[1] < -0.5
                || values[2] > 0.5
                || values[2] < -0.5) {
            motionTextView.setText("Device is in motion");
        } else {
            motionTextView.setText("Device is not in motion");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

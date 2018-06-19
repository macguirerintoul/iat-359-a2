package com.mrintoul.macguirerintoul_a2;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class DataActivity extends AppCompatActivity implements SensorEventListener {
    TextView sensorName, valuesTextView;
    SensorManager sensorManager;
    Sensor thisSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        sensorManager = MainActivity.sensorManager;
        sensorName = findViewById(R.id.sensorName);
        valuesTextView = findViewById(R.id.valuesTextView);

        Intent i = getIntent();
        thisSensor = sensorManager.getDefaultSensor(i.getIntExtra("SENSOR_TYPE", Sensor.TYPE_ALL));
        sensorName.setText(thisSensor.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, thisSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        String valueText = values[0] + "\n";
        if (values.length > 1) {
            valueText += values[1] + "\n";
        }
        if (values.length > 2) {
            valueText += values[2] + "\n";
        }
        valuesTextView.setText(valueText);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

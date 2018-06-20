package com.mrintoul.macguirerintoul_a2;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DataActivity extends AppCompatActivity implements SensorEventListener {
    TextView sensorName, valuesTextView, infoTextView;
    SensorManager sensorManager;
    Sensor thisSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        sensorManager = MainActivity.sensorManager;
        sensorName = findViewById(R.id.sensorName);
        valuesTextView = findViewById(R.id.valuesTextView);
        infoTextView = findViewById(R.id.infoTextView);

        Intent i = getIntent();
        thisSensor = sensorManager.getDefaultSensor(i.getIntExtra("SENSOR_TYPE", Sensor.TYPE_ALL));
        sensorName.setText(thisSensor.getName());
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, thisSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        String valueText = "Value 1: " + values[0] + "\n";
        if (values.length > 1) {
            valueText += "Value 2: " + values[1] + "\n";
        }
        if (values.length > 2) {
            valueText += "Value 3: " + values[2] + "\n";
        }
        valuesTextView.setText(valueText);

        String infoText = "Sensor type: " + thisSensor.getType() + "\n";
        infoText += "Vendor: " + thisSensor.getVendor() + "\n";
        infoText += "Version: " + thisSensor.getVersion() + "\n";
        infoText += "Maximum value: " + thisSensor.getMaximumRange() + "\n";
        infoText += "Resolution: " + thisSensor.getResolution()+ "\n";
        infoTextView.setText(infoText);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

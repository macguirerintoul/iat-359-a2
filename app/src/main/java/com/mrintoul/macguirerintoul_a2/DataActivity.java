package com.mrintoul.macguirerintoul_a2;

import android.content.Intent;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class DataActivity extends AppCompatActivity {
    TextView sensorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        sensorName = findViewById(R.id.sensorName);
        Intent i = getIntent();
        Sensor thisSensor = MainActivity.mySensorManager.getDefaultSensor(i.getIntExtra("SENSOR_TYPE", Sensor.TYPE_ALL));
        sensorName.setText(thisSensor.getName());
    }
}

package com.estadium.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {

    private Button stopButton;
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        text = (TextView) findViewById(R.id.acceleration_TEXT);
        stopButton = (Button) findViewById(R.id.StopButton);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClicked();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("Sensor changed", "acceleration");
        float xAcceleration = event.values[0];
        float yAcceleration = event.values[1];
        float zAcceleration = event.values[2];

        text.setText("X: " + xAcceleration + "m/s^2\n"
                + "Y: " + yAcceleration + "m/s^2\n"
                + "Z: " + zAcceleration + "m/s^2");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void onStopClicked() {
        super.onStop();
        sensorManager.unregisterListener(this, sensor);
    }
}

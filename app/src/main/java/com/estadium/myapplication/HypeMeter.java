package com.estadium.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HypeMeter extends AppCompatActivity implements SensorEventListener {

    private static final double HYPE_VALUE = 6.00;
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView hype_level;
    private Button goToAccelerometerData;

    private final float GRAVITY_VAL = (float) 9.81; //9.80665;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hype_meter_activity);
        hype_level = (TextView) findViewById(R.id.hype_level);
        hype_level.setGravity(Gravity.CENTER);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        goToAccelerometerData = (Button) findViewById(R.id.HypeToAccelerometer);
        goToAccelerometerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterSensor();
                Intent i = new Intent(HypeMeter.this, AccelerometerData.class);
                startActivity(i);
            }
        });

    }

    @SuppressLint("NewApi")
    @Override
    public void onSensorChanged(SensorEvent event) {
        double resultant = Math.sqrt(
                Math.pow(event.values[0],2)
                + Math.pow(event.values[1] - GRAVITY_VAL,2)
                + Math.pow(event.values[2],2));
        if (resultant > HYPE_VALUE) {
            hype_level.setText("YES: " + resultant + " m/s^2");
            hype_level.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            hype_level.setText("NO: " + resultant + " m/s^2");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    private void unregisterSensor() {
        sensorManager.unregisterListener(this, sensor);
    }

}

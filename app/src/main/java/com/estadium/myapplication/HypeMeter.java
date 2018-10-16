package com.estadium.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class HypeMeter extends AppCompatActivity implements SensorEventListener {

    private static final double HYPE_THRESHOLD = 6.00;
    private final String route = "http://testapi.vip.gatech.edu/api/accelerometer";
    private final String contentType = "Application/json";
    private final float GRAVITY_VAL = (float) 9.81; //9.80665;

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView hype_level;
    private Button goToAccelerometerData;

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
        float xValue = event.values[0];
        float yValue = event.values[1] - GRAVITY_VAL;
        float zValue = event.values[2];

        double resultant = Math.sqrt(Math.pow(xValue, 2)
                        + Math.pow(yValue, 2)
                        + Math.pow(zValue, 2));

        if (resultant > HYPE_THRESHOLD) {
            hype_level.setText("YES: " + resultant + " m/s^2");
            hype_level.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            postToDatabase("mock_user_id", xValue, yValue, zValue);
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

    private void postToDatabase(String user_id, double x, double y, double z) {
        JSONObject jsonObject = createJSON(user_id, x, y, z);
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(null, route, stringEntity, contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Attempt post JSON", "Success: " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Attempt post JSON", "Failure: " + statusCode);
            }
        });
    }

    @NonNull
    private JSONObject createJSON(String user_id, double x, double y, double z) {
        HashMap<String, Object> schema = new HashMap<>();
        schema.put("user_id", user_id);
        schema.put("X_Coordinate", x);
        schema.put("Y_Coordinate", y);
        schema.put("Z_Coordinate", z);
        return new JSONObject(schema);
    }
}

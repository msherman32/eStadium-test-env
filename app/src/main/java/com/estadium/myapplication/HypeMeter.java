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

public class HypeMeter extends AppCompatActivity implements SensorEventListener { //FIXME: change  to Activity

    private static final double HYPE_THRESHOLD = 10.765;
    private final String accelerometerRoute = "http://testapi.vip.gatech.edu/api/accelerometer";
    private final String hypemeterRoute = "http://testapi.vip.gatech.edu/api/hypemeter";
    private final String contentType = "application/json";
    private final float GRAVITY_VAL = (float) 9.81; //9.80665;

    private final boolean writeEnable = false; //set to true if we want to the database
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView hypeOrNot;
    private TextView hype_level;
    private Button goToAccelerometerData;
    private Button goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hype_meter_activity);
        hype_level = (TextView) findViewById(R.id.hype_level);
//        hype_level.setGravity(Gravity.CENTER);
        hypeOrNot = (TextView) findViewById(R.id.hypeOrNot);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //TODO use calibrated or uncalibrated accelerometer
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL, 3000); //todo: add another int to this method to reduce power consumption
        goToAccelerometerData = (Button) findViewById(R.id.HypeToAccelerometer);
        goHome = (Button) findViewById(R.id.hype_meter_to_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        goToAccelerometerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterSensor();
                Intent i = new Intent(HypeMeter.this, AccelerometerData.class);
                startActivity(i);
            }
        });
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterSensor();
                Intent i = new Intent(HypeMeter.this, HomePage.class);
                startActivity(i);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onSensorChanged(SensorEvent event) {
        float xValue = event.values[0];
        float yValue = event.values[1]; //Todo: use gyroscope to calculate which value is UP (receiving gravity)
        float zValue = event.values[2];

        float resultant = (float) Math.sqrt(Math.pow(xValue, 2)
                        + Math.pow(yValue, 2)
                        + Math.pow(zValue, 2));

        if (resultant > HYPE_THRESHOLD) {
            hypeOrNot.setText("YES:");
            hype_level.setText(resultant + " m/s^2");
            if (writeEnable) {
                hype_level.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                postToDatabase("Tanner's Phone WIFI", xValue, yValue, zValue);
                postToDatabase("Tanner's Phone WIFI", resultant);
            }
        } else {
            hypeOrNot.setText("NO:");
            hype_level.setText(resultant + " m/s^2");
        }
    }

    @Override
    public void onPause() { //this is the same as leaving the activity
        //ALways unregsiter the sensor so that we don't get data while not shaking?
        //Or should this constantly be going on in the background??
        super.onPause();
        unregisterSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    private void unregisterSensor() {
        sensorManager.unregisterListener(this, sensor);
    }

    private void postToDatabase(String user_id, float x, float y, float z) {
        JSONObject jsonObject = createJSON(user_id, x, y, z);
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }

        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(null, accelerometerRoute, stringEntity, contentType, new AsyncHttpResponseHandler() {
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

    private void postToDatabase(String user_id, float resultant) {
        JSONObject resultantJSON = createJSON(user_id, resultant);
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(resultantJSON.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }

        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(null, hypemeterRoute, stringEntity, contentType, new AsyncHttpResponseHandler() {
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
    private JSONObject createJSON(String user_id, float resultant) {
        HashMap<String, Object> schema = new HashMap<>();
        schema.put("user_id", user_id);
        schema.put("hypemeter", resultant);
        return new JSONObject(schema);
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

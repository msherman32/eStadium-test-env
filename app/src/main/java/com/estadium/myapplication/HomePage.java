package com.estadium.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    private Button accelerationButton;
    private Button hypeMeterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);
        accelerationButton = (Button) findViewById(R.id.go_to_accel_data);
        accelerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Acceleration Clicked", "Going to acceleration");
                Intent i = new Intent(HomePage.this, AccelerometerData.class);
                startActivity(i);
            }
        });
        hypeMeterButton = (Button) findViewById(R.id.go_to_hype_meter);
        hypeMeterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HypeMeter Clicked", "Going to hype detector");
                Intent i = new Intent(HomePage.this, HypeMeter.class);
                startActivity(i);
            }
        });
    }
}

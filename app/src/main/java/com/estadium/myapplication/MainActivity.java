package com.estadium.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button accelerationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerationButton = (Button) findViewById(R.id.go_to_accel_data);
        accelerationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Clicked", "Going to acceleration");
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);
            }
        });
    }
}

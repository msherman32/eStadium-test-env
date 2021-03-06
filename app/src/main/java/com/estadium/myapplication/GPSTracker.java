package com.estadium.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPSTracker extends Activity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location theLocation;
    private Button homeButton;
    private Button requestLocationButton;
    private TextView latitude;
    private TextView longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstracker);

        homeButton = (Button) findViewById(R.id.GPStoHome);
        requestLocationButton = (Button) findViewById(R.id.gps_request_location);

        latitude = (TextView) findViewById(R.id.lat_text);
        longitude = (TextView) findViewById(R.id.long_text);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(GPSTracker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(GPSTracker.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            requestPermissions(new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.INTERNET}, 10);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GPSTracker.this, HomePage.class);
                startActivity(intent);
            }
        });

        requestLocationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
//                latitude.setText("Latitiude: " + "ABCDEFG" + theLocation.getLatitude());
//                longitude.setText("Longitude: " + theLocation.getLongitude());
            }
        });
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            theLocation = location;
            latitude.setText("Latitiude: " + location.getLatitude());
            longitude.setText("Longitude: " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS); //go to the settings to enable location services
            startActivity(intent);
        }
    }
}

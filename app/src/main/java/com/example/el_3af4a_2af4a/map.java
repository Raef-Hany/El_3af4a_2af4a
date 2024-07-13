package com.example.el_3af4a_2af4a;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

import java.util.ArrayList;
import java.util.List;

public class map extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "SpeedBumpDetection";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private LocationManager locationManager;

    private MapView mapView;
    private MapController mapController;
    private MyLocationNewOverlay locationOverlay;
    private GpsMyLocationProvider locationProvider;

    private List<Float> gyroXData = new ArrayList<>();
    private List<Float> gyroYData = new ArrayList<>();
    private List<Float> accYData = new ArrayList<>();

    private long lastTimestamp = 0;
    private static final int WINDOW_SIZE = 95 * 2; // 2 seconds at 95 Hz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // OSMDroid Initialization
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid_preferences", MODE_PRIVATE));

        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(15.0); // Set initial zoom level

        // Location setup
        locationProvider = new GpsMyLocationProvider(this);
        locationOverlay = new MyLocationNewOverlay(locationProvider, mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(locationOverlay);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create a separate LocationListener
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setCenter(startPoint);
                locationOverlay.onLocationChanged(location, locationProvider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1F, locationListener);
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        // Sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mapView.onResume();
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        mapView.onPause();
        locationOverlay.disableMyLocation();
        locationOverlay.disableFollowLocation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // ... (rest of the method remains the same)
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ... (remains the same)
    }

    private void processWindow() {
        // ... (remains the same)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1F, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            mapController.setCenter(startPoint);
                            locationOverlay.onLocationChanged(location, locationProvider);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}
                    });
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
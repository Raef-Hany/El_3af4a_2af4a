package com.example.el_3af4a_2af4a;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import org.osmdroid.views.overlay.Marker;

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
    private Marker potholeMarker; // Accessible to other methods

    private List<Float> gyroXData = new ArrayList<>();
    private List<Float> gyroYData = new ArrayList<>();
    private List<Float> accYData = new ArrayList<>();

    private long lastTimestamp = 0;
    private static final int WINDOW_SIZE = 95 * 2; // 2 seconds at 95 Hz

    private static final GeoPoint POTHOLE_LOCATION = new GeoPoint(30.025160365564137, 31.487229544714964);

    private static final int MIN_ZOOM_LEVEL_FOR_MARKER = 15;

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
        mapController.setZoom(13.0); // Set initial zoom level

        // Location setup
        locationProvider = new GpsMyLocationProvider(this);
        locationOverlay = new MyLocationNewOverlay(locationProvider, mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(locationOverlay);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        addPotholeMarker(POTHOLE_LOCATION);

        // Zoom Handling Logic
        mapView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scaleMarkerWithZoom();
            }
        });

        // Create a separate LocationListener
        LocationListener locationListener = new LocationListener() {
            private boolean firstLocationUpdate = true; // Flag to center only once
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                // mapController.setCenter(startPoint);  // Remove this line

                if (firstLocationUpdate) {
                    // Center the map only on the first location update
                    mapController.setCenter(POTHOLE_LOCATION);
                    firstLocationUpdate = false;
                }

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

    // ... (Your onResume(), onPause(), etc. methods) ...

    private void addPotholeMarker(GeoPoint location) {
        potholeMarker = new Marker(mapView);
        potholeMarker.setPosition(location);
        potholeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        potholeMarker.setTitle("Pothole Alert!");
        potholeMarker.setRelatedObject("pothole_marker");
        potholeMarker.setVisible(false); // Start hidden

        // Set the small icon
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.pothole);
        if (icon != null) {
            int initialWidth = 30; // Initial size in pixels
            int initialHeight = 30;
            icon.setBounds(0, 0, initialWidth, initialHeight);
            potholeMarker.setIcon(icon);
        }

        mapView.getOverlays().add(potholeMarker);
        mapView.invalidate();
    }


    private void showPotholeMarker(boolean show) {
        if (potholeMarker != null) {
            potholeMarker.setVisible(show);
            mapView.invalidate();
        }
    }

    private void scaleMarkerWithZoom() {
        double zoomLevel = mapView.getZoomLevelDouble();

        if (zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKER) {
            // ... your implementation for the method

        } else {
            potholeMarker.setVisible(false);
            mapView.invalidate();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
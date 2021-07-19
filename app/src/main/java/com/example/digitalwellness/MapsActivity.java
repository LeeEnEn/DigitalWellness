package com.example.digitalwellness;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.digitalwellness.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMapsBinding binding;
    private boolean toggleText = true;
    private boolean toggleStart = true;
    private boolean isMapFullScreen = false;
    private Button moreDetailsButton;
    private Button startTrackingButton;
    private CameraPosition cameraPosition;
    private EditText editText;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private Location mLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private PolylineOptions polylineOptions;
    private SupportMapFragment mapFragment;
    private Spinner spinner;
    private TextView distanceCovered;

    private static GoogleMap mMap;
    private static Polyline polyline;

    private long distance = 0L;
    private long userDistanceInput = 0L;

    private final LatLng DEFAULT_LOCATION = new LatLng(1.3521, 103.8198);
    private LatLng mCurrentLocation;
    private LatLng mStartingLocation;
    private LatLng mTemp;

    private List<LatLng> coordinates = new ArrayList<>();

    private final String DEFAULT_MARKER_TITLE = "Singapore";
    private final String CURRENT_POSITION_TITLE = "You are here!";
    private final String STARTING_POSITION_TITLE = "Starting location";
    private final String[] options = {"Normal", "Satellite", "Terrain"};

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isNetworkAvailable = CheckNetwork.isInternetAvailable(this);

        if (!isNetworkAvailable) {

        }

        // Initialize variables.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        moreDetailsButton = (Button) findViewById(R.id.show_maps);
        startTrackingButton = (Button) findViewById(R.id.distance_start);
        distanceCovered = (TextView) findViewById(R.id.distance_covered);
        editText = (EditText) findViewById(R.id.distance_edit_text);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.distance_progress);

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.mapType(GoogleMap.MAP_TYPE_NONE)
                .tiltGesturesEnabled(false)
                .rotateGesturesEnabled(false);

        // Initialize spinner to set the look of maps.
        spinner = (Spinner) findViewById(R.id.map_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    if (position == 0) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else if (position == 1) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else if (position == 2) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Spinner's adapter.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.GONE);

        // Get user's location every few seconds.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (!mTemp.equals(mCurrentLocation) && toggleStart) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(mStartingLocation).title(STARTING_POSITION_TITLE));
                        mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title(CURRENT_POSITION_TITLE));
                        coordinates.add(mCurrentLocation);
                        // Redraw line whenever there's a change in location.
                        coordinates.add(mCurrentLocation);
                        polyline = mMap.addPolyline(new PolylineOptions()
                                .width(10)
                                .color(Color.BLUE)
                                .addAll(coordinates));
                        distance += mLocation.distanceTo(location);
                        // Update text.
                        distanceCovered.setText(String.valueOf(distance));
                        // Update progress bar.
                        progressBar.setProgress((int) distance);
                        // Set temp to current location.
                        mTemp = mCurrentLocation;
                        mLocation = location;
                        if (distance == userDistanceInput) {
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            long[] pattern = {0, 500, 500};
                            vibrator.vibrate(pattern, -1);
                        }
                    }
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);

        moreDetailsButton.setVisibility(View.GONE);
        moreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set map and spinner to be visible.
                mapFragment.getView().setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Tap once to minimize the map.", Toast.LENGTH_LONG).show();
                moreDetailsButton.setVisibility(View.GONE);
                startTrackingButton.setVisibility(View.GONE);
                isMapFullScreen = true;
            }
        });

        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleText) {
                    String userInput = editText.getText().toString();
                    // Check if there is a valid input.
                    if (!userInput.isEmpty()) {
                        int validInt = Integer.parseInt(userInput);

                        if (validInt != 0) {
                            userDistanceInput = validInt;
                            // Hide edit text view.
                            editText.setVisibility(View.GONE);
                            // Show maps button.
                            moreDetailsButton.setVisibility(View.VISIBLE);
                            // Change button text.
                            startTrackingButton.setText(R.string.stop);
                            // Change values to fit user's input.
                            progressBar.setMax(validInt);
                            progressBar.setProgress(0);
                            distance = 0L;
                            distanceCovered.setText(String.valueOf(distance));

                            // Remove markings on map.
                            mMap.clear();
                            polyline.remove();
                            coordinates.clear();

                            toggleStart = true;
                            toggleText = !toggleText;

                            getCurrentLocation();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "There is no target distance or \ndistance is zero!",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "There is no target distance or \ndistance is zero!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Show edit text view.
                    editText.setVisibility(View.VISIBLE);
                    // Hide maps button.
//                    moreDetailsButton.setVisibility(View.GONE);
                    // Change button text.
                    startTrackingButton.setText(R.string.start);
                    toggleStart = false;
                    toggleText = !toggleText;
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Show maps as full screen.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // To toggle screen size dynamically.
//                DisplayMetrics displayMetrics = MapsActivity.this.getResources().getDisplayMetrics();
//                ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
//                mapFragment.getView().setLayoutParams(params);

                changeView();
            }
        });

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

        getCurrentLocation();

        createLocationRequest();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isMapFullScreen) {
                changeView();
            } else {
                onBackPressed();
                (new Handler()).postDelayed(this::finish, 500);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof LatLng) {
            LatLng latLng = (LatLng) object;
            return latLng.longitude == mCurrentLocation.longitude
                    && latLng.longitude == mCurrentLocation.latitude;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void changeView() {
        // Remove map and spinner from view.
        mapFragment.getView().setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        // Add button back to view.
        moreDetailsButton.setVisibility(View.VISIBLE);
        startTrackingButton.setVisibility(View.VISIBLE);
        isMapFullScreen = false;
    }

    /**
     * Get user's current location if internet connection is available. Otherwise a default
     * location will be set in Singapore.
     */
    private void getCurrentLocation() {
        boolean isNetworkAvailable = CheckNetwork.isInternetAvailable(MapsActivity.this);

        if (isNetworkAvailable) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mStartingLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mTemp = new LatLng(location.getLatitude(), location.getLongitude());
                        mLocation = location;
                        setMarker(mStartingLocation, CURRENT_POSITION_TITLE);

                        coordinates.add(mStartingLocation);
                        polylineOptions = new PolylineOptions().width(10).color(Color.BLUE).addAll(coordinates);
                        polyline = mMap.addPolyline(polylineOptions);
                    }

                }
            });
        } else {
            // Not connected to the network. Set default positions.
            setMarker(DEFAULT_LOCATION, DEFAULT_MARKER_TITLE);
        }
    }

    /**
     * Set current location with a marker.
     * @param markerLocation Location to be marked.
     * @param markerTitle Location title.
     */
    private void setMarker(LatLng markerLocation, String markerTitle) {
        mMap.addMarker(new MarkerOptions().position(markerLocation).title(markerTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocation));
        mMap.setMinZoomPreference(15);
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Taken from Google Android Developer's Docs.
     * https://developer.android.com/training/location
     */
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
}
package com.simtech.app1.pojo.layout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.simtech.app1.LoginActivity;
import com.simtech.app1.MainMenuActivity;
import com.simtech.app1.R;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apputils.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldLayoutActivity extends AppCompatActivity implements View.OnClickListener {
    private String observation;
    private APIInterface apiInterface;
    private SharedPreferences mCredentialsStorage;
    private String token;
    private String userName, planningDate, locationName, locationId, trialTypeId, trialTypeName, observationType;
    private TextView farmersNameTextView, locationTextView, plantingDateTextView, selectedTrialTextView;
    private ProgressBar progressBar;
    private RecyclerView ParentRecyclerViewItem;
    private LinearLayout lytHeader;
    private Button btnCaptureGps;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_LOCATION_SETTINGS = 2;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private double latitude, longitude;
    private String locationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_field_layout_activity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        farmersNameTextView = (TextView) findViewById(R.id.farmersNameTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        plantingDateTextView = (TextView) findViewById(R.id.plantingDateTextView);
        selectedTrialTextView = (TextView) findViewById(R.id.selectedTrialTextView);
        lytHeader = (LinearLayout) findViewById(R.id.lytHeader);
        btnCaptureGps = (Button) findViewById(R.id.btnCaptureGps);

        mCredentialsStorage = getSharedPreferences("AppSharedPreferences", MODE_PRIVATE);
        token = mCredentialsStorage.getString(LoginActivity.TOKEN, null);
        userName = mCredentialsStorage.getString(LoginActivity.USERNAME, null);

        observationType = getIntent().getStringExtra("observationType");
        planningDate = getIntent().getStringExtra("startDate");
        locationName = getIntent().getStringExtra("locationName");
        locationId = getIntent().getStringExtra("locationId");
        trialTypeId = getIntent().getStringExtra("trialTypeId");
        trialTypeName = getIntent().getStringExtra("trialTypeName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(observationType);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent splashScreen = new Intent(FieldLayoutActivity.this, MainMenuActivity.class);
                startActivity(splashScreen);
            }
        });

        btnCaptureGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermissions();
                fieldCustomDialog(latitude, longitude);
            }
        });

        ParentRecyclerViewItem = findViewById(R.id.recyclerListView);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Add your custom logic here
                // If you want to prevent the default back button behavior, don't call isEnabled()
            }
        };

        FieldLayoutActivity.this.getOnBackPressedDispatcher().addCallback(FieldLayoutActivity.this, callback);
    }
    private void captureGps() {
        // Initialize FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(FieldLayoutActivity.this);
        // Request location permissions at runtime
        requestLocationPermissions();
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            // Permission is already granted, proceed with location requests
            initGps();
        }
    }

    private void initGps() {
        // Initialize LocationRequest
        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();

        // Initialize LocationCallback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };

        // Initialize LocationSettingsRequest
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        // Request location updates
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Ensure mLocationCallback is not null before using it
            if (mLocationCallback == null) {
                Log.e("MainActivity", "mLocationCallback is null");
                return;
            }

            try {
                // Check if location settings are satisfied
                checkLocationSettings();
            } catch (SecurityException e) {
                e.printStackTrace();
                // Handle permission-related exception
            }
        } else {
            // Handle the case where location permissions are not granted
            requestLocationPermissions();
        }
    }

    private void checkLocationSettings() {
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(mLocationSettingsRequest);

        task.addOnSuccessListener(locationSettingsResponse -> {
            // Location settings are satisfied, initiate location requests
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
            );
        });

        ((Task<?>) task).addOnFailureListener(e -> {
            // Handle failure, prompt user to enable location services
            if (e instanceof ResolvableApiException) {
                try {
                    ((ResolvableApiException) e).startResolutionForResult(
                            this,
                            REQUEST_LOCATION_SETTINGS
                    );
                } catch (IntentSender.SendIntentException sendEx) {
                    // Handle the exception
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User enabled location settings, proceed with location requests
                startLocationUpdates();
            } else {
                // User did not enable location settings, handle accordingly
                // You may want to show a message or take appropriate action
            }
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }
    }

    private void callLayoutAPI() {
        showProgressBar();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LayoutDetailsPojo> call = apiInterface.layoutDetails(userName, planningDate, locationName, locationId, trialTypeName, trialTypeId);
        call.enqueue(new Callback<LayoutDetailsPojo>() {
            @Override
            public void onResponse(Call<LayoutDetailsPojo> call, Response<LayoutDetailsPojo> response) {
                if (response.isSuccessful()) {
                    LayoutDetailsPojo mainMenuResponse = response.body();
                    if (mainMenuResponse != null && mainMenuResponse.getData() != null) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(FieldLayoutActivity.this);

                        if (mainMenuResponse.getData().size() != 0 && mainMenuResponse.getData() != null) {
                            hideProgressBar();
                            LocationDetailsDataPojo data = mainMenuResponse.getData().get(0);
                            String farmerName = data.getFarmer_name();
                            String locationName = data.getLocation_name();
                            String plantingDate = data.getStart_date();
                            String nRelications = data.getN_replications();
                            String nObservationLine = data.getN_observation_lines();

                            farmersNameTextView.setText("Farmer's Name: " + farmerName);
                            locationTextView.setText("Location: " + locationName);
                            plantingDateTextView.setText("Planting Date: " + plantingDate);
                            selectedTrialTextView.setText("Trial Type: " + trialTypeName);

                            LocationDetailsAdapter parentItemAdapter = new LocationDetailsAdapter(FieldLayoutActivity.this, observationType, data);
                            ParentRecyclerViewItem.setAdapter(parentItemAdapter);
                            ParentRecyclerViewItem.setLayoutManager(layoutManager);
                        } else {
                            UIUtils.customToastMsg(FieldLayoutActivity.this, "No data Found");
                            hideProgressBar();
                        }
                    }
                    // Handle the successful response here
                } else {
                    // Handle the unsuccessful response here
                    UIUtils.customToastMsg(FieldLayoutActivity.this, "Error");
                }
            }

            @Override
            public void onFailure(Call<LayoutDetailsPojo> call, Throwable t) {
                // Handle the failure here
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        ParentRecyclerViewItem.setVisibility(View.GONE);
        lytHeader.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        ParentRecyclerViewItem.setVisibility(View.VISIBLE);
        lytHeader.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureGps();
        if (UIUtils.isNetworkAvailable(FieldLayoutActivity.this)) {
            callLayoutAPI();
        } else {
            Toast.makeText(FieldLayoutActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void fieldCustomDialog(double latitude, double longitude) {
        // Create custom dialog instance
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.field_custom_dialog);

        // Find buttons in the custom dialog
        Button topLeftButton = customDialog.findViewById(R.id.topLeftButton);
        Button topRightButton = customDialog.findViewById(R.id.topRightButton);
        Button bottomLeftButton = customDialog.findViewById(R.id.bottomLeftButton);
        Button bottomRightButton = customDialog.findViewById(R.id.bottomRightButton);

        // Set click listeners for buttons
        topLeftButton.setOnClickListener(this);
        topRightButton.setOnClickListener(this);
        bottomLeftButton.setOnClickListener(this);
        bottomRightButton.setOnClickListener(this);

        // Show the custom dialog
        customDialog.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topLeftButton:
                // Handle top left button click
                locationString = "Latitude: " + mCurrentLocation.getLatitude()
                        + "\nLongitude: " + mCurrentLocation.getLongitude();
                Toast.makeText(this, locationString, Toast.LENGTH_SHORT).show();
                break;

            case R.id.topRightButton:
                // Handle top right button click
                locationString = "Latitude: " + mCurrentLocation.getLatitude()
                        + "\nLongitude: " + mCurrentLocation.getLongitude();
                Toast.makeText(this, locationString, Toast.LENGTH_SHORT).show();
                Log.e("GPS", locationString);
                break;

            case R.id.bottomLeftButton:
                // Handle bottom left button click
                locationString = "Latitude: " + mCurrentLocation.getLatitude()
                        + "\nLongitude: " + mCurrentLocation.getLongitude();
                Toast.makeText(this, locationString, Toast.LENGTH_SHORT).show();
                Log.e("GPS", locationString);
                break;

            case R.id.bottomRightButton:
                // Handle bottom right button click
                locationString = "Latitude: " + mCurrentLocation.getLatitude()
                        + "\nLongitude: " + mCurrentLocation.getLongitude();
                Toast.makeText(this, locationString, Toast.LENGTH_SHORT).show();
                Log.e("GPS", locationString);
                break;
        }
    }
}
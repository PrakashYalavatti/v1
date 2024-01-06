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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.google.gson.Gson;
import com.simtech.app1.LoginActivity;
import com.simtech.app1.MainMenuActivity;
import com.simtech.app1.R;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.apputils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldLayoutActivity extends AppCompatActivity {
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
    private TextView tvNoData;
    private CardView cardViewLyt1;
    private Dialog customDialog;

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
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        cardViewLyt1 = (CardView) findViewById(R.id.cardViewLyt1);

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
                customDialog = new Dialog(FieldLayoutActivity.this);
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
        Call<LayoutDetailsPojo> call = apiInterface.layoutDetails(userName, planningDate, locationName, locationId, trialTypeName, trialTypeId);
        call.enqueue(new Callback<LayoutDetailsPojo>() {
            @Override
            public void onResponse(Call<LayoutDetailsPojo> call, Response<LayoutDetailsPojo> response) {
                if (response.isSuccessful()) {
                    LayoutDetailsPojo mainMenuResponse = response.body();
                    if (mainMenuResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                LinearLayoutManager layoutManager = new LinearLayoutManager(FieldLayoutActivity.this);
                                if (mainMenuResponse.getData().size() != 0 && mainMenuResponse.getData() != null) {
                                    LocationDetailsDataPojo data = mainMenuResponse.getData().get(0);
                                    if(data != null){
                                        progressBar.setVisibility(View.GONE);
                                        tvNoData.setVisibility(View.GONE);
                                        lytHeader.setVisibility(View.VISIBLE);
                                        String farmerName = data.getFarmer_name();
                                        String locationName = data.getLocation_name();
                                        String plantingDate = data.getStart_date();
                                        String nRelications = data.getN_replications();
                                        String nObservationLine = data.getN_observation_lines();

                                        farmersNameTextView.setText("Farmer's Name: " + farmerName);
                                        locationTextView.setText("Location: " + locationName);
                                        plantingDateTextView.setText("Planting Date: " + plantingDate);
                                        selectedTrialTextView.setText("Trial Type: " + trialTypeName);
                                        if(data.getPurposes() != null && data.getPurposes().size() != 0){
                                            ArrayList<ObservationPojo> pojoData = data.getPurposes().get(0).getObservations();
                                            if(pojoData != null && pojoData.size() != 0){
                                                ParentRecyclerViewItem.setVisibility(View.VISIBLE);
                                                lytHeader.setVisibility(View.VISIBLE);
                                                cardViewLyt1.setVisibility(View.VISIBLE);
                                                LocationDetailsAdapter parentItemAdapter = new LocationDetailsAdapter(FieldLayoutActivity.this, observationType, data);
                                                ParentRecyclerViewItem.setAdapter(parentItemAdapter);
                                                ParentRecyclerViewItem.setLayoutManager(layoutManager);
                                            }
                                        }
                                    } else {
                                        cardViewLyt1.setVisibility(View.GONE);
                                        tvNoData.setVisibility(View.VISIBLE);
                                        lytHeader.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                } else {
                                    UIUtils.customToastMsg(FieldLayoutActivity.this, "No data Found");
                                    progressBar.setVisibility(View.GONE);
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
                                break;
                            // Add more cases for other response codes as needed
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(FieldLayoutActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(FieldLayoutActivity.this, "Response body is empty or null.");
                    }
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

    /*private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        ParentRecyclerViewItem.setVisibility(View.VISIBLE);
        lytHeader.setVisibility(View.VISIBLE);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        captureGps();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        if (UIUtils.isNetworkAvailable(FieldLayoutActivity.this)) {
            if(token != null && userName != null) {
                callServerDateTimeAPi(token, userName);
            }
            callLayoutAPI();
            /*String jsonString = "{ \"data\": [ { \"farmer_name\": \"Amruta\", \"location_id\": \"LOC-25\", \"location_name\": \"Hubballi\", \"n_observation_lines\": \"4\", \"n_replications\": \"3\", \"purposes\": [ { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5012\", \"varietyname\": \"K.Pukhraj\" }, { \"observedvalue\": null, \"varietycode\": \"5190\", \"varietyname\": \"Tribute\" }, { \"observedvalue\": null, \"varietycode\": \"5192\", \"varietyname\": \"Sorrento\" }, { \"observedvalue\": null, \"varietycode\": \"5195\", \"varietyname\": \"ElMundo\" }, { \"observedvalue\": null, \"varietycode\": \"5196\", \"varietyname\": \"Everest\" }, { \"observedvalue\": null, \"varietycode\": \"5221\", \"varietyname\": \"03.MT.78 A 4\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5012\", \"varietyname\": \"K.Pukhraj\" }, { \"observedvalue\": null, \"varietycode\": \"5190\", \"varietyname\": \"Tribute\" }, { \"observedvalue\": null, \"varietycode\": \"5192\", \"varietyname\": \"Sorrento\" }, { \"observedvalue\": null, \"varietycode\": \"5195\", \"varietyname\": \"ElMundo\" }, { \"observedvalue\": null, \"varietycode\": \"5196\", \"varietyname\": \"Everest\" }, { \"observedvalue\": null, \"varietycode\": \"5221\", \"varietyname\": \"03.MT.78 A 4\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5012\", \"varietyname\": \"K.Pukhraj\" }, { \"observedvalue\": null, \"varietycode\": \"5190\", \"varietyname\": \"Tribute\" }, { \"observedvalue\": null, \"varietycode\": \"5192\", \"varietyname\": \"Sorrento\" }, { \"observedvalue\": null, \"varietycode\": \"5195\", \"varietyname\": \"ElMundo\" }, { \"observedvalue\": null, \"varietycode\": \"5196\", \"varietyname\": \"Everest\" }, { \"observedvalue\": null, \"varietycode\": \"5221\", \"varietyname\": \"03.MT.78 A 4\" } ] } ], \"purpose\": \"Table\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5036\", \"varietyname\": \"L.R\" }, { \"observedvalue\": null, \"varietycode\": \"5201\", \"varietyname\": \"Cr 2002-1\" }, { \"observedvalue\": null, \"varietycode\": \"5219\", \"varietyname\": \"11.MRS.26 A 2\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5036\", \"varietyname\": \"L.R\" }, { \"observedvalue\": null, \"varietycode\": \"5201\", \"varietyname\": \"Cr 2002-1\" }, { \"observedvalue\": null, \"varietycode\": \"5219\", \"varietyname\": \"11.MRS.26 A 2\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5036\", \"varietyname\": \"L.R\" }, { \"observedvalue\": null, \"varietycode\": \"5201\", \"varietyname\": \"Cr 2002-1\" }, { \"observedvalue\": null, \"varietycode\": \"5219\", \"varietyname\": \"11.MRS.26 A 2\" } ] } ], \"purpose\": \"Crisp\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5191\", \"varietyname\": \"Reiver\" }, { \"observedvalue\": null, \"varietycode\": \"5214\", \"varietyname\": \"10.Z.381 A 3\" }, { \"observedvalue\": null, \"varietycode\": \"5215\", \"varietyname\": \"07.Z.31 C 21\" }, { \"observedvalue\": null, \"varietycode\": \"5216\", \"varietyname\": \"07.Z.21 A 12\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5191\", \"varietyname\": \"Reiver\" }, { \"observedvalue\": null, \"varietycode\": \"5214\", \"varietyname\": \"10.Z.381 A 3\" }, { \"observedvalue\": null, \"varietycode\": \"5215\", \"varietyname\": \"07.Z.31 C 21\" }, { \"observedvalue\": null, \"varietycode\": \"5216\", \"varietyname\": \"07.Z.21 A 12\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5191\", \"varietyname\": \"Reiver\" }, { \"observedvalue\": null, \"varietycode\": \"5214\", \"varietyname\": \"10.Z.381 A 3\" }, { \"observedvalue\": null, \"varietycode\": \"5215\", \"varietyname\": \"07.Z.31 C 21\" }, { \"observedvalue\": null, \"varietycode\": \"5216\", \"varietyname\": \"07.Z.21 A 12\" } ] } ], \"purpose\": \"Table/Baker\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5193\", \"varietyname\": \"Pioneer\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5193\", \"varietyname\": \"Pioneer\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5193\", \"varietyname\": \"Pioneer\" } ] } ], \"purpose\": \"Low GI/Table\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5200\", \"varietyname\": \"Tr 2015 -138\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5200\", \"varietyname\": \"Tr 2015 -138\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5200\", \"varietyname\": \"Tr 2015 -138\" } ] } ], \"purpose\": \"Crisp/Purple flash with cream skin\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5204\", \"varietyname\": \"Cr 2015-097\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5204\", \"varietyname\": \"Cr 2015-097\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5204\", \"varietyname\": \"Cr 2015-097\" } ] } ], \"purpose\": \"Table/R in R\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5211\", \"varietyname\": \"10.Z.342 A 5\" }, { \"observedvalue\": null, \"varietycode\": \"5212\", \"varietyname\": \"10.Z.353 A 7\" }, { \"observedvalue\": null, \"varietycode\": \"5213\", \"varietyname\": \"10.Z.380 A 3\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5211\", \"varietyname\": \"10.Z.342 A 5\" }, { \"observedvalue\": null, \"varietycode\": \"5212\", \"varietyname\": \"10.Z.353 A 7\" }, { \"observedvalue\": null, \"varietycode\": \"5213\", \"varietyname\": \"10.Z.380 A 3\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5211\", \"varietyname\": \"10.Z.342 A 5\" }, { \"observedvalue\": null, \"varietycode\": \"5212\", \"varietyname\": \"10.Z.353 A 7\" }, { \"observedvalue\": null, \"varietycode\": \"5213\", \"varietyname\": \"10.Z.380 A 3\" } ] } ], \"purpose\": \"Table/Crisp\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5217\", \"varietyname\": \"10.MRS.56 A 21\" }, { \"observedvalue\": null, \"varietycode\": \"5218\", \"varietyname\": \"10.MRS.2 A 9\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5217\", \"varietyname\": \"10.MRS.56 A 21\" }, { \"observedvalue\": null, \"varietycode\": \"5218\", \"varietyname\": \"10.MRS.2 A 9\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5217\", \"varietyname\": \"10.MRS.56 A 21\" }, { \"observedvalue\": null, \"varietycode\": \"5218\", \"varietyname\": \"10.MRS.2 A 9\" } ] } ], \"purpose\": \"Low GI Table\" }, { \"observations\": [ { \"observation\": \"R1\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5220\", \"varietyname\": \"04.PD.2 A 7\" } ] }, { \"observation\": \"R2\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5220\", \"varietyname\": \"04.PD.2 A 7\" } ] }, { \"observation\": \"R3\", \"varieties\": [ { \"observedvalue\": null, \"varietycode\": \"5220\", \"varietyname\": \"04.PD.2 A 7\" } ] } ], \"purpose\": \"Crisp/Low GI\" } ], \"start_date\": \"2023-12-08\", \"state\": \"Karnataka\", \"state_id\": \"KA\", \"trial_type_id\": \"TRL-1\", \"trial_type_name\": \"Regular PET\", \"trial_year\": \"2023\" } ] }";
            LayoutDetailsPojo mainMenuResponse = parseJsonToAccessTokenResponse(jsonString);
            LinearLayoutManager layoutManager = new LinearLayoutManager(FieldLayoutActivity.this);
            if (mainMenuResponse.getData().size() != 0 && mainMenuResponse.getData() != null) {
                LocationDetailsDataPojo data = mainMenuResponse.getData().get(0);
                if(data != null){
                    progressBar.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.GONE);
                    lytHeader.setVisibility(View.VISIBLE);
                    String farmerName = data.getFarmer_name();
                    String locationName = data.getLocation_name();
                    String plantingDate = data.getStart_date();
                    String nRelications = data.getN_replications();
                    String nObservationLine = data.getN_observation_lines();

                    farmersNameTextView.setText("Farmer's Name: " + farmerName);
                    locationTextView.setText("Location: " + locationName);
                    plantingDateTextView.setText("Planting Date: " + plantingDate);
                    selectedTrialTextView.setText("Trial Type: " + trialTypeName);
                    if(data.getPurposes() != null && data.getPurposes().size() != 0){
                        ArrayList<ObservationPojo> pojoData = data.getPurposes().get(0).getObservations();
                        if(pojoData != null && pojoData.size() != 0){
                            ParentRecyclerViewItem.setVisibility(View.VISIBLE);
                            lytHeader.setVisibility(View.VISIBLE);
                            cardViewLyt1.setVisibility(View.VISIBLE);
                            LocationDetailsAdapter parentItemAdapter = new LocationDetailsAdapter(FieldLayoutActivity.this, observationType, data);
                            ParentRecyclerViewItem.setAdapter(parentItemAdapter);
                            ParentRecyclerViewItem.setLayoutManager(layoutManager);
                        }
                    }
                } else {
                    cardViewLyt1.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                    lytHeader.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                UIUtils.customToastMsg(FieldLayoutActivity.this, "No data Found");
                progressBar.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }*/
        } else {
            Toast.makeText(FieldLayoutActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void callServerDateTimeAPi(String token, String userName) {
        Call<UserLoginResponse> call = apiInterface.getServerDateTime(token, userName);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    UserLoginResponse serverDateTime = response.body();
                    if (serverDateTime != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                if (serverDateTime != null) {
                                    String serverDateTimeString = serverDateTime.datetime;
                                    String deviceCurrentDateTime = UIUtils.getCurrentDateTime();
                                    int timeDiff = Integer.parseInt(UIUtils.findDifference(serverDateTimeString, deviceCurrentDateTime));
                                    // 335 is the converted minutes for indian timing -- 5:30 + 5 minutes
                                    if((Math.abs(timeDiff) >= 335)){
                                        Toast.makeText(FieldLayoutActivity.this, getString(R.string.time_diff) + 5 + " minutes", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                        dateSettingsLauncher.launch(intent);
                                    }
                                } else {
                                    UIUtils.customToastMsg(FieldLayoutActivity.this, "No Data Found...");
                                }
                                break;
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(FieldLayoutActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(FieldLayoutActivity.this, "Response body is empty or null.");
                    }
                    // Handle the successful response here
                } else {
                    // Handle the unsuccessful response here
                    UIUtils.customToastMsg(FieldLayoutActivity.this, "Error in response");
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                UIUtils.customToastMsg(FieldLayoutActivity.this, "Error in response");
            }
        });
    }

    private final ActivityResultLauncher<Intent> dateSettingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Handle the result if needed
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    // Your logic here
                }
            }
    );

    private LayoutDetailsPojo parseJsonToAccessTokenResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, LayoutDetailsPojo.class);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }*/

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void fieldCustomDialog(double latitude, double longitude) {
        // Create custom dialog instance
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.field_custom_dialog);

        // Find buttons in the custom dialog
        Button btnCaptureGps = customDialog.findViewById(R.id.btnCaptureGps);
        Button btnCapturedMap = customDialog.findViewById(R.id.btnCapturedMap);

        // Set click listeners for buttons
        btnCaptureGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(FieldLayoutActivity.this, locationString, Toast.LENGTH_SHORT).show();
                LocalDate date = null;
                int yearOnly = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    date = LocalDate.parse(planningDate, DateTimeFormatter.ISO_LOCAL_DATE);
                    yearOnly = date.getYear();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date parsedDate = sdf.parse(planningDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(parsedDate);
                        yearOnly = calendar.get(Calendar.YEAR);
                    } catch (ParseException e) {
                        // Handle parsing exception
                        e.printStackTrace();
                    }
                }
                CapturedGPSPojo capturedGPSPojo = new CapturedGPSPojo();
                capturedGPSPojo.trialyear = String.valueOf(yearOnly);
                capturedGPSPojo.plantationdate = planningDate;
                capturedGPSPojo.locationid = locationId;
                capturedGPSPojo.trialtypeid = trialTypeId;
                capturedGPSPojo.Latitude = String.valueOf(latitude);
                capturedGPSPojo.Longitude = String.valueOf(longitude);
                if(latitude>0 && longitude>0){
                    Gson gson = new Gson();
                    // Convert POJO to JSON
                    String jsonString = gson.toJson(capturedGPSPojo);
                    // Print the JSON representation
                    System.out.println(jsonString);
//                    Toast.makeText(FieldLayoutActivity.this, jsonString, Toast.LENGTH_SHORT).show();
                    callCapturedGPSAPI(capturedGPSPojo);
                } else {
                    Toast.makeText(FieldLayoutActivity.this, "Please re-capture the GPS coordinates", Toast.LENGTH_SHORT).show();
                    if(customDialog.isShowing()){
                        customDialog.dismiss();
                    }
                }
            }
        });

        btnCapturedMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Show the custom dialog
        customDialog.show();
    }

    private void callCapturedGPSAPI(CapturedGPSPojo capturedGPSPojo) {
        Call<UserLoginResponse> call = apiInterface.capturedgpsdata(capturedGPSPojo);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    UserLoginResponse dapResponse = response.body();
                    if (dapResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                if(customDialog.isShowing()){
                                    customDialog.dismiss();
                                }
                                Toast.makeText(FieldLayoutActivity.this, dapResponse.message, Toast.LENGTH_SHORT).show();
                                break;
                            case 500:
                                Toast.makeText(FieldLayoutActivity.this, dapResponse.message, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                UIUtils.customToastMsg(FieldLayoutActivity.this, "Response body is empty or null.");
                        }
                    }
                } else {
                    UIUtils.customToastMsg(FieldLayoutActivity.this, "Response body is empty or null.");
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                UIUtils.customToastMsg(FieldLayoutActivity.this, "Error");
            }
        });
    }
}
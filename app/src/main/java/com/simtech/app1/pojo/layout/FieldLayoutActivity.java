package com.simtech.app1.pojo.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.LoginActivity;
import com.simtech.app1.MainMenuActivity;
import com.simtech.app1.R;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apputils.UIUtils;

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

    private void callLayoutAPI() {
        showProgressBar();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LayoutDetailsPojo> call = apiInterface.layoutDetails(userName, planningDate, locationName, locationId, trialTypeName, trialTypeId);
        call.enqueue(new Callback<LayoutDetailsPojo>() {
            @Override
            public void onResponse(Call<LayoutDetailsPojo> call, Response<LayoutDetailsPojo> response) {
                if (response.isSuccessful()) {
                    LayoutDetailsPojo mainMenuResponse = response.body();
                    if(mainMenuResponse != null && mainMenuResponse.getData() != null){
                        LinearLayoutManager layoutManager = new LinearLayoutManager(FieldLayoutActivity.this);

                        if(mainMenuResponse.getData().size() != 0 && mainMenuResponse.getData() != null){
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
        if(UIUtils.isNetworkAvailable(FieldLayoutActivity.this)) {
            callLayoutAPI();
        } else {
            Toast.makeText(FieldLayoutActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
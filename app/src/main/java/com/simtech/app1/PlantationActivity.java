package com.simtech.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.adapter.PlantationAdapter;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.apputils.UIUtils;
import com.simtech.app1.pojo.RVChildItem;
import com.simtech.app1.pojo.RVParentItem;
import com.simtech.app1.pojo.layout.FieldLayoutActivity;
import com.simtech.app1.pojo.planting.PlantatingDetailsPojo;
import com.simtech.app1.pojo.planting.PlantatingVarietyDataPojo;
import com.simtech.app1.pojo.planting.PlantingPojo;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantationActivity extends AppCompatActivity implements EditPlantingSamples{
    private int observation;
    private ArrayList<RVParentItem> parentItemList;

    private ArrayList<RVChildItem> childItemList;
    private RecyclerView rvPlantation;

    private SharedPreferences mCredentialsStorage;
    private String token;
    private String userName, observationType, startDate, locationName, locationId, trialTypeId,
            trialTypeName, nReplications, varietyCode;
    private APIInterface apiInterface;
    private TextView farmersNameTextView, locationTextView, plantingDateTextView, selectedTrialTypeTextView, tvSample3;
    private Button btnSave;
    private PlantationAdapter plantationAdapter;
    private PlantatingDetailsPojo data;
    private PlantingPojo plantingResponse;
    private ProgressBar progressBar;
    private LinearLayout lytHeader, linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plantation_activity);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.at_planting));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent layoutIntent = new Intent(PlantationActivity.this, FieldLayoutActivity.class);
                layoutIntent.putExtra("observationType", observationType);
                layoutIntent.putExtra("startDate", startDate);
                layoutIntent.putExtra("locationName", locationName);
                layoutIntent.putExtra("locationId", locationId);
                layoutIntent.putExtra("trialTypeId", trialTypeId);
                layoutIntent.putExtra("trialTypeName", trialTypeName);
                layoutIntent.putExtra("nReplications", nReplications);
                startActivity(layoutIntent);
            }
        });

        mCredentialsStorage = getSharedPreferences("AppSharedPreferences", MODE_PRIVATE);
        token = mCredentialsStorage.getString(LoginActivity.TOKEN, null);
        userName = mCredentialsStorage.getString(LoginActivity.USERNAME, null);

        observationType = getIntent().getStringExtra("observationType");
        startDate = getIntent().getStringExtra("startDate");
        locationName = getIntent().getStringExtra("locationName");
        locationId = getIntent().getStringExtra("locationId");
        trialTypeId = getIntent().getStringExtra("trialTypeId");
        trialTypeName = getIntent().getStringExtra("trialTypeName");
        nReplications = getIntent().getStringExtra("nReplications");
        varietyCode = getIntent().getStringExtra("varietyCode");

        farmersNameTextView = findViewById(R.id.farmersNameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        plantingDateTextView = findViewById(R.id.plantingDateTextView);
        selectedTrialTypeTextView = findViewById(R.id.selectedTrialTypeTextView);
        linearLayout = findViewById(R.id.linearLayout);
        lytHeader = findViewById(R.id.lytHeader);
        tvSample3 = findViewById(R.id.tvSample3);
        rvPlantation = findViewById(R.id.rvPlantation);
        btnSave = findViewById(R.id.btnSave);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Add your custom logic here
                // If you want to prevent the default back button behavior, don't call isEnabled()
            }
        };

        PlantationActivity.this.getOnBackPressedDispatcher().addCallback(PlantationActivity.this, callback);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UIUtils.isNetworkAvailable(PlantationActivity.this)){
                    callInsertPlantingAPI();
                } else {
                    Toast.makeText(PlantationActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callInsertPlantingAPI() {
        showProgressBar();

        ArrayList<PlantatingVarietyDataPojo> insertPlantationData = plantingResponse.data.get(0).plantation_data;

        Iterator<PlantatingVarietyDataPojo> iterator = insertPlantationData.iterator();

        while (iterator.hasNext()) {
            PlantatingVarietyDataPojo dataPojo = iterator.next();
            if(dataPojo.sample1==null && dataPojo.sample2 == null && dataPojo.sample3 == null) {
                iterator.remove();
            } else if(dataPojo.sample1!=null && dataPojo.sample2!=null && dataPojo.sample3 == null){
                dataPojo.sample3="";
            }
        }

        Call<UserLoginResponse> call = apiInterface.plantationupdate(plantingResponse);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    hideProgressBar();
                    UserLoginResponse plantingResponse1 = response.body();
                    Toast.makeText(PlantationActivity.this, plantingResponse1.message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlantationActivity.this, FieldLayoutActivity.class);
                    intent.putExtra("observationType", observationType);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("locationName", locationName);
                    intent.putExtra("locationId", locationId);
                    intent.putExtra("trialTypeId", trialTypeId);
                    intent.putExtra("trialTypeName", trialTypeName);
                    intent.putExtra("nReplications", nReplications);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                UIUtils.customToastMsg(PlantationActivity.this, "Error");
            }
        });
    }

    private void callApi() {
        showProgressBar();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PlantingPojo> call = apiInterface.planting(userName, startDate, locationName, locationId, trialTypeName, trialTypeId);
        call.enqueue(new Callback<PlantingPojo>() {
            @Override
            public void onResponse(Call<PlantingPojo> call, Response<PlantingPojo> response) {
                if (response.isSuccessful()) {
                    plantingResponse = response.body();
                    if (plantingResponse != null && plantingResponse.data != null && plantingResponse.data.size() != 0) {
                        plantingResponse.data.get(0).username = userName;
                        data = plantingResponse.data.get(0);
                        farmersNameTextView.setText("Farmer's Name: " + data.farmername);
                        locationTextView.setText("Location: " + data.locationname);
                        plantingDateTextView.setText("Planting Date: " + data.startdate);
                        selectedTrialTypeTextView.setText("Trial Type: " + data.trialtypename);

                        if(data.trialtypename.contains("FastTrack")){
                            tvSample3.setVisibility(View.GONE);
                        } else {
                            tvSample3.setVisibility(View.VISIBLE);
                        }
                        plantationAdapter = new PlantationAdapter(PlantationActivity.this, data.plantation_data, varietyCode, data.trialtypename);
                        rvPlantation.setAdapter(plantationAdapter);
                        moveItemToLastPosition(0,data.plantation_data);
                        hideProgressBar();
                        /*int positionToFocus = plantationAdapter.findPositionByItemId(varietyCode);

                        if (positionToFocus != RecyclerView.NO_POSITION) {
                            // Set the focused position in the adapter
                            plantationAdapter.setFocusedPosition(positionToFocus);

                            // Smooth scroll to the focused position
                            rvPlantation.smoothScrollToPosition(positionToFocus);
                        }*/
                    } else {
                        UIUtils.customToastMsg(PlantationActivity.this, "No Data Found AT Planting");
                    }
                }
            }

            @Override
            public void onFailure(Call<PlantingPojo> call, Throwable t) {

            }
        });
    }

    private void moveItemToLastPosition(int index, ArrayList<PlantatingVarietyDataPojo> plantationData) {
        PlantatingVarietyDataPojo itemToMove = plantationData.get(index);
        plantationData.remove(index);
        plantationData.add(itemToMove);
        plantationAdapter.notifyItemMoved(index, plantationData.size() - 1);
    }


    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        rvPlantation.setVisibility(View.GONE);
        lytHeader.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        rvPlantation.setVisibility(View.VISIBLE);
        lytHeader.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditData(PlantatingVarietyDataPojo data) {
        UIUtils.customToastMsg(PlantationActivity.this, "Hi");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UIUtils.isNetworkAvailable(PlantationActivity.this)){
            callApi();
        } else {
            Toast.makeText(PlantationActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.simtech.app1.adapter.PlantationAdapter;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.MainMenuResponse;
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

public class PlantationActivity extends AppCompatActivity implements EditPlantingSamples {
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
    private TextView tvNoData;
    private CardView cardViewLyt1;

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
        tvNoData = findViewById(R.id.tvNoData);
        cardViewLyt1 = findViewById(R.id.cardViewLyt1);

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
                if (UIUtils.isNetworkAvailable(PlantationActivity.this)) {
                    callInsertPlantingAPI();
                } else {
                    Toast.makeText(PlantationActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callInsertPlantingAPI() {
        progressBar.setVisibility(View.VISIBLE);

        ArrayList<PlantatingVarietyDataPojo> insertPlantationData = plantingResponse.data.get(0).plantation_data;

        Iterator<PlantatingVarietyDataPojo> iterator = insertPlantationData.iterator();

        while (iterator.hasNext()) {
            PlantatingVarietyDataPojo dataPojo = iterator.next();
            if (dataPojo.sample1 == null && dataPojo.sample2 == null && dataPojo.sample3 == null) {
                iterator.remove();
            } else if (dataPojo.sample1 != null && dataPojo.sample2 != null && dataPojo.sample3 == null) {
                dataPojo.sample3 = "";
            }
        }

        Call<UserLoginResponse> call = apiInterface.plantationupdate(plantingResponse);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    UserLoginResponse plantingResponse1 = response.body();
                    if (plantingResponse1 != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                progressBar.setVisibility(View.GONE);
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
                                break;
                            default:
                                UIUtils.customToastMsg(PlantationActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(PlantationActivity.this, "Response body is empty or null.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                UIUtils.customToastMsg(PlantationActivity.this, "Error");
            }
        });
    }

    private void callApi() {
        progressBar.setVisibility(View.VISIBLE);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<PlantingPojo> call = apiInterface.planting(userName, startDate, locationName, locationId, trialTypeName, trialTypeId);
        call.enqueue(new Callback<PlantingPojo>() {
            @Override
            public void onResponse(Call<PlantingPojo> call, Response<PlantingPojo> response) {
                if (response.isSuccessful()) {
                    plantingResponse = response.body();
                    if (plantingResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                if (plantingResponse.data != null && plantingResponse.data.size() != 0) {
                                    plantingResponse.data.get(0).username = userName;
                                    data = plantingResponse.data.get(0);
                                    farmersNameTextView.setText("Farmer's Name: " + data.farmername);
                                    locationTextView.setText("Location: " + data.locationname);
                                    plantingDateTextView.setText("Planting Date: " + data.startdate);
                                    selectedTrialTypeTextView.setText("Trial Type: " + data.trialtypename);

                                    /*if(data.trialtypename.contains("FastTrack")){
                                        tvSample3.setVisibility(View.GONE);
                                    } else {
                                        tvSample3.setVisibility(View.VISIBLE);
                                    }*/

                                    if (data.plantation_data.size() != 0 && data.plantation_data != null) {
                                        tvNoData.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        rvPlantation.setVisibility(View.VISIBLE);
                                        lytHeader.setVisibility(View.VISIBLE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        btnSave.setVisibility(View.VISIBLE);
                                        cardViewLyt1.setVisibility(View.VISIBLE);
                                        plantationAdapter = new PlantationAdapter(PlantationActivity.this, data.plantation_data, varietyCode, data.trialtypename);
                                        rvPlantation.setAdapter(plantationAdapter);
                                        moveItemToLastPosition(0, data.plantation_data);
                                    } else {
                                        tvNoData.setVisibility(View.VISIBLE);
                                        rvPlantation.setVisibility(View.GONE);
                                        lytHeader.setVisibility(View.GONE);
                                        linearLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        btnSave.setVisibility(View.GONE);
                                        cardViewLyt1.setVisibility(View.GONE);
                                    }
                                } else {
                                    UIUtils.customToastMsg(PlantationActivity.this, "No Data Found AT Planting");
                                }
                                break;
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(PlantationActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(PlantationActivity.this, "Response body is empty or null.");
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

    @Override
    public void onEditData(PlantatingVarietyDataPojo data) {
        UIUtils.customToastMsg(PlantationActivity.this, "Hi");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UIUtils.isNetworkAvailable(PlantationActivity.this)) {
            callApi();
            /*String jsonString = "{ \"data\": [ { \"farmername\": \"Amruta\", \"locationid\": \"LOC-25\", \"locationname\": \"Hubballi\", \"plantation_data\": [ { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5012\", \"varietyname\": \"K.Pukhraj\" }, { \"purpose\": \"Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5036\", \"varietyname\": \"L.R\" }, { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5190\", \"varietyname\": \"Tribute\" }, { \"purpose\": \"Table/Baker\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5191\", \"varietyname\": \"Reiver\" }, { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5192\", \"varietyname\": \"Sorrento\" }, { \"purpose\": \"Low GI/Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5193\", \"varietyname\": \"Pioneer\" }, { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5195\", \"varietyname\": \"ElMundo\" }, { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5196\", \"varietyname\": \"Everest\" }, { \"purpose\": \"Crisp/Purple flash with cream skin\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5200\", \"varietyname\": \"Tr 2015 -138\" }, { \"purpose\": \"Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5201\", \"varietyname\": \"Cr 2002-1\" }, { \"purpose\": \"Table/R in R\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5204\", \"varietyname\": \"Cr 2015-097\" }, { \"purpose\": \"Table/Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5211\", \"varietyname\": \"10.Z.342 A 5\" }, { \"purpose\": \"Table/Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5212\", \"varietyname\": \"10.Z.353 A 7\" }, { \"purpose\": \"Table/Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5213\", \"varietyname\": \"10.Z.380 A 3\" }, { \"purpose\": \"Table/Baker\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5214\", \"varietyname\": \"10.Z.381 A 3\" }, { \"purpose\": \"Table/Baker\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5215\", \"varietyname\": \"07.Z.31 C 21\" }, { \"purpose\": \"Table/Baker\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5216\", \"varietyname\": \"07.Z.21 A 12\" }, { \"purpose\": \"Low GI Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5217\", \"varietyname\": \"10.MRS.56 A 21\" }, { \"purpose\": \"Low GI Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5218\", \"varietyname\": \"10.MRS.2 A 9\" }, { \"purpose\": \"Crisp\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5219\", \"varietyname\": \"11.MRS.26 A 2\" }, { \"purpose\": \"Crisp/Low GI\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5220\", \"varietyname\": \"04.PD.2 A 7\" }, { \"purpose\": \"Table\", \"sample1\": null, \"sample2\": null, \"sample3\": null, \"varietycode\": \"5221\", \"varietyname\": \"03.MT.78 A 4\" } ], \"startdate\": \"2023-12-08\", \"state\": \"Karnataka\", \"stateid\": \"KA\", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\", \"trialyear\": \"2023\", \"username\": \"abc\" } ] }";
            PlantingPojo plantingResponse = parseJsonToAccessTokenResponse(jsonString);
            if (plantingResponse.data != null && plantingResponse.data.size() != 0) {
                plantingResponse.data.get(0).username = userName;
                data = plantingResponse.data.get(0);
                farmersNameTextView.setText("Farmer's Name: " + data.farmername);
                locationTextView.setText("Location: " + data.locationname);
                plantingDateTextView.setText("Planting Date: " + data.startdate);
                selectedTrialTypeTextView.setText("Trial Type: " + data.trialtypename);

                                    *//*if(data.trialtypename.contains("FastTrack")){
                                        tvSample3.setVisibility(View.GONE);
                                    } else {
                                        tvSample3.setVisibility(View.VISIBLE);
                                    }*//*

                if (data.plantation_data.size() != 0 && data.plantation_data != null) {
                    tvNoData.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    rvPlantation.setVisibility(View.VISIBLE);
                    lytHeader.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    cardViewLyt1.setVisibility(View.VISIBLE);
                    plantationAdapter = new PlantationAdapter(PlantationActivity.this, data.plantation_data, varietyCode, data.trialtypename);
                    rvPlantation.setAdapter(plantationAdapter);
                    moveItemToLastPosition(0, data.plantation_data);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvPlantation.setVisibility(View.GONE);
                    lytHeader.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    btnSave.setVisibility(View.GONE);
                    cardViewLyt1.setVisibility(View.GONE);
                }
            }*/
        } else {
            Toast.makeText(PlantationActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private PlantingPojo parseJsonToAccessTokenResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, PlantingPojo.class);
    }
}

package com.simtech.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.simtech.app1.adapter.MainMenuAdapter;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.MainMenuResponse;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.apputils.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuActivity extends AppCompatActivity {
    APIInterface apiInterface;
    RecyclerView rvDashboard;
    private SharedPreferences mCredentialsStorage;
    private String token;
    private String userName;
    private Button btnDashboard;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.main_menu));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        apiInterface = APIClient.getClient().create(APIInterface.class);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent splashScreen = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(splashScreen);
            }
        });

        mCredentialsStorage = getSharedPreferences("AppSharedPreferences", MODE_PRIVATE);
        token = mCredentialsStorage.getString(LoginActivity.TOKEN, null);
        userName = mCredentialsStorage.getString(LoginActivity.USERNAME, null);

        rvDashboard = findViewById(R.id.rvDashboard);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Add your custom logic here
                // If you want to prevent the default back button behavior, don't call isEnabled()
            }
        };

        MainMenuActivity.this.getOnBackPressedDispatcher().addCallback(MainMenuActivity.this, callback);
    }

    private void callMainMenuAPI() {
        Call<MainMenuResponse> call = apiInterface.mainMenu(userName, token, userName);
        call.enqueue(new Callback<MainMenuResponse>() {
            @Override
            public void onResponse(Call<MainMenuResponse> call, Response<MainMenuResponse> response) {
                if (response.isSuccessful()) {
                    MainMenuResponse mainMenuResponse = response.body();
                    if (mainMenuResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                if (mainMenuResponse != null && mainMenuResponse.getData() !=null) {
                                    hideProgressBar();
                                    MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(mainMenuResponse.getData(), MainMenuActivity.this);
                                    rvDashboard.setAdapter(mainMenuAdapter);
                                } else {
                                    hideProgressBar();
                                    UIUtils.customToastMsg(MainMenuActivity.this, "No Data Found...");
                                }
                                break;
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(MainMenuActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(MainMenuActivity.this, "Response body is empty or null.");
                    }
                    // Handle the successful response here
                } else {
                    // Handle the unsuccessful response here
                    UIUtils.customToastMsg(MainMenuActivity.this, "Error in response");
                }
            }

            @Override
            public void onFailure(Call<MainMenuResponse> call, Throwable t) {
                // Handle the failure here
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        rvDashboard.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        rvDashboard.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

        } catch (Exception e){

        }
        if(UIUtils.isNetworkAvailable(MainMenuActivity.this)) {
            showProgressBar();
            if(token != null && userName != null){
                callServerDateTimeAPi(token, userName);
                callMainMenuAPI();
            }

            /*String jsonString = "{ \"data\": [ { \"locationid\": \"LOC-24\", \"locationname\": \"Bangaluru\", \"startdate\": \"2023-10-24\", \"trialtype\": [ { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" }, { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-25\", \"locationname\": \"Hubballi\", \"startdate\": \"2023-12-08\", \"trialtype\": [ { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-23\", \"locationname\": \"Chikkaballapura\", \"startdate\": \"2023-11-28\", \"trialtype\": [ { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-26\", \"locationname\": \"Gadag\", \"startdate\": \"2023-10-14\", \"trialtype\": [ { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" }, { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" } ], \"username\": \"abc\" } ] }";
            MainMenuResponse response = parseJsonToAccessTokenResponse(jsonString);
            hideProgressBar();
            MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(response.getData(), MainMenuActivity.this);
            rvDashboard.setAdapter(mainMenuAdapter);*/
        } else {
            Toast.makeText(MainMenuActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
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
                                    hideProgressBar();
                                    String serverDateTimeString = serverDateTime.datetime;
                                    String deviceCurrentDateTime = UIUtils.getCurrentDateTime();
                                    int timeDiff = Integer.parseInt(UIUtils.findDifference(serverDateTimeString, deviceCurrentDateTime));
                                    // 335 is the converted minutes for indian timing -- 5:30 + 5 minutes
                                    if((Math.abs(timeDiff) >= 335)){
                                        Toast.makeText(MainMenuActivity.this, getString(R.string.time_diff) + 5 + " minutes", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                        dateSettingsLauncher.launch(intent);
                                    }
                                } else {
                                    hideProgressBar();
                                    UIUtils.customToastMsg(MainMenuActivity.this, "No Data Found...");
                                }
                                break;
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(MainMenuActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(MainMenuActivity.this, "Response body is empty or null.");
                    }
                    // Handle the successful response here
                } else {
                    // Handle the unsuccessful response here
                    UIUtils.customToastMsg(MainMenuActivity.this, "Error in response");
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {

            }
        });
    }

    private MainMenuResponse parseJsonToAccessTokenResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, MainMenuResponse.class);
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
}


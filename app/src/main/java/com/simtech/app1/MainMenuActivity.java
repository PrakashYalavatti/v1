package com.simtech.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
        apiInterface = APIClient.getClient().create(APIInterface.class);
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
        if(UIUtils.isNetworkAvailable(MainMenuActivity.this)) {
            showProgressBar();
            callMainMenuAPI();
            /*String jsonString = "{ \"data\": [ { \"locationid\": \"LOC-24\", \"locationname\": \"Bangaluru\", \"startdate\": \"2023-10-24\", \"trialtype\": [ { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" }, { \"trialstatus\": \"00 days for 30 DAP \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-25\", \"locationname\": \"Hubballi\", \"startdate\": \"2023-12-08\", \"trialtype\": [ { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"00 days for Planting \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-23\", \"locationname\": \"Chikkaballapura\", \"startdate\": \"2023-11-28\", \"trialtype\": [ { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" }, { \"trialstatus\": \"10 days for 20 DAP \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" } ], \"username\": \"abc\" }, { \"locationid\": \"LOC-26\", \"locationname\": \"Gadag\", \"startdate\": \"2023-10-14\", \"trialtype\": [ { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-3\", \"trialtypename\": \"CT\" }, { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-1\", \"trialtypename\": \"Regular PET\" }, { \"trialstatus\": \"35 days for Harvest \", \"trialtypeid\": \"TRL-2\", \"trialtypename\": \"FastTrack PET\" } ], \"username\": \"abc\" } ] }";
            MainMenuResponse response = parseJsonToAccessTokenResponse(jsonString);
            hideProgressBar();
            MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(response.getData(), MainMenuActivity.this);
            rvDashboard.setAdapter(mainMenuAdapter);*/
        } else {
            Toast.makeText(MainMenuActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private MainMenuResponse parseJsonToAccessTokenResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, MainMenuResponse.class);
    }
}


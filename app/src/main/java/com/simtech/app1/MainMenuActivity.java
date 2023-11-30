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

import com.simtech.app1.adapter.MainMenuAdapter;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.MainMenuResponse;
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
                    if (mainMenuResponse != null && !mainMenuResponse.getData().isEmpty()) {
                        hideProgressBar();
                        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(mainMenuResponse.getData(), MainMenuActivity.this);
                        rvDashboard.setAdapter(mainMenuAdapter);
                    }
                    // Handle the successful response here
                } else {
                    // Handle the unsuccessful response here
                    UIUtils.customToastMsg(MainMenuActivity.this, "Error");
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
        } else {
            Toast.makeText(MainMenuActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}


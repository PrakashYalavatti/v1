package com.simtech.app1.apiservices;

import com.simtech.app1.apiservices.apirequestresponse.MainMenuResponse;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginRequest;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.pojo.dap.DAPPojo;
import com.simtech.app1.pojo.harvest.HarvestDetailPojo;
import com.simtech.app1.pojo.layout.CapturedGPSPojo;
import com.simtech.app1.pojo.layout.LayoutDetailsPojo;
import com.simtech.app1.pojo.planting.PlantingPojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("/api/login")
    Call<UserLoginResponse> createUser(@Body UserLoginRequest userLoginResponse);

    @GET("/api/main_menu")
    Call<MainMenuResponse> mainMenu(@Query("username") String username, @Header("Access-token") String token, @Header("Userid") String userId);

    @GET("/api/layout")
    Call<LayoutDetailsPojo> layoutDetails(@Query("username") String username, @Query("startdate") String startdate,
                                          @Query("locationname") String locationname, @Query("locationid") String locationid,
                                          @Query("trialtypename") String trialtypename, @Query("trialtypeid") String trialtypeid);

    @GET("/api/plantation")
    Call<PlantingPojo> planting(@Query("username") String username, @Query("startdate") String startdate,
                                @Query("locationname") String locationname, @Query("locationid") String locationid,
                                @Query("trialtypename") String trialtypename, @Query("trialtypeid")String trialtypeid);
    @POST("/api/plantationupdate")
    Call<UserLoginResponse> plantationupdate(@Body PlantingPojo PlantingPojo);

    @POST("/api/observation")
    Call<UserLoginResponse> observation(@Body DAPPojo DAP);

    @POST("api/capturedgpsdata")
    Call<UserLoginResponse> capturedgpsdata(@Body CapturedGPSPojo GPS);

    @GET("api/get_datetime")
    Call<UserLoginResponse> getServerDateTime(@Header("Access-token") String token, @Header("Userid") String Userid);

    @POST("api/observationharvest")
    Call<UserLoginResponse> observationharvest(@Body HarvestDetailPojo harvestDetailPojos);
}

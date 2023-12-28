package com.simtech.app1;

import static com.simtech.app1.apputils.UIUtils.appendLog;

import android.app.Activity;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginRequest;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.apputils.UIUtils;
import com.simtech.app1.sqlite.DBManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private DBManager dbManager;
    private Button idBtnLogin;
    APIInterface apiInterface;

    String URL_APP_LINK = "https://simtechitsolutionspvtltd562-my.sharepoint.com/:u:/g/personal/prakash_yalavatti_simtechitsolutions_in1/EdnfBfLL7cxIsjT7lgRb4b8BglfeFQrebT52WfdfwGRKsg?e=A6i8qa";
    private String deviceAppVersion;
    private boolean mDownloadStarted = false;
    private boolean mUpdateStarted = false;
    private boolean in_complete_download = false;
    private File apkfileName;

    public static int UPDATE_APK_REQUESTCODE = 102;
    private EditText idEdtUserName, idEdtPassword;
    private SharedPreferences mCredentialsStorage;

    public static final String PREFS_NAME = "AppSharedPreferences";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    private String strUserName, strUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.harvest);
        // assigning ID of the toolbar to a variable
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.user_login));
        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        idBtnLogin = (Button) findViewById(R.id.idBtnLogin);
        idEdtUserName = (EditText) findViewById(R.id.idEdtUserName);
        idEdtPassword = (EditText) findViewById(R.id.idEdtPassword);
        dbManager = new DBManager(this);
        dbManager.open();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Add your custom logic here
                // If you want to prevent the default back button behavior, don't call isEnabled()
            }
        };

        LoginActivity.this.getOnBackPressedDispatcher().addCallback(LoginActivity.this, callback);

        idBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upgrade
                /*try {
                    deviceAppVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                if (!(deviceAppVersion.equals("1"))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        showUpgradeDialog(URL_APP_LINK, "");
                    } else {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        Permissions.check(LoginActivity.this, permissions, null, null, new PermissionHandler() {

                            @Override
                            public void onGranted() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    if (!checkPermission()) {
                                        requestPermission();
                                    }
                                } else {
                                    showUpgradeDialog(URL_APP_LINK, "");
                                }
                            }

                            @Override
                            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                                super.onDenied(context, deniedPermissions);
                                Log.i("Srinu>> ", "onDenied");
                            }
                        });
                    }

                }*/

                strUserName = idEdtUserName.getText().toString();
                strUserPassword = idEdtPassword.getText().toString();
                if ((!TextUtils.isEmpty(strUserName) || !strUserName.equals("")) && (!TextUtils.isEmpty(strUserPassword) || !strUserPassword.equals(""))) {
                    if(UIUtils.isNetworkAvailable(LoginActivity.this)){
                        loginApiCall(strUserName, strUserPassword);

                        /*String jsonString = "{ \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcwMjAxMjEzOCwianRpIjoiMTk1YzgyOGMtMDEwMi00ZDUxLWFiNjgtYmIwMmU3MmZiOTM1IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6ImFiYyIsIm5iZiI6MTcwMjAxMjEzOCwiZXhwIjoxNzAyMDEzMDM4fQ.RKX4iAz1B_i0uMh5nCLTmrqVyYIgaUZmrCyotyMAuBI\", \"message\": \"Login successful\", \"status\": \"1\" }";;
                        UserLoginResponse response = parseJsonToAccessTokenResponse(jsonString);
                        mCredentialsStorage = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = mCredentialsStorage.edit();
                        editor.putString(TOKEN, response.getAccess_token());
                        editor.putString(USERNAME, "shivani");
                        editor.putString(PASSWORD, "123456");
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                        startActivity(intent);*/

                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    UIUtils.customToastMsg(LoginActivity.this, "Please enter proper credentials");
                }
            }
        });
    }

    private UserLoginResponse parseJsonToAccessTokenResponse(String jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, UserLoginResponse.class);
    }

    private void loginApiCall(String strUserName, String strUserPassword) {
        UserLoginRequest user = new UserLoginRequest(strUserName, strUserPassword);
        Call<UserLoginResponse> call = apiInterface.createUser(user);
        call.enqueue(new Callback<UserLoginResponse>() {

            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    UserLoginResponse userResponse = response.body();
                    if (userResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                // Successful response (HTTP 200)
                                if (userResponse.status == 1) {
                                    mCredentialsStorage = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mCredentialsStorage.edit();
                                    editor.putString(TOKEN, userResponse.getAccess_token());
                                    editor.putString(USERNAME, user.username);
                                    editor.putString(PASSWORD, user.password);
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                                    startActivity(intent);
                                    /*Intent intent = new Intent(LoginActivity.this, IntroPagerActivity.class);
                                    startActivity(intent);*/
                                } else {
                                    UIUtils.customToastMsg(LoginActivity.this, userResponse.message);
                                }
                                break;
                            case 401:
                                // Unauthorized (HTTP 401)
                                UIUtils.customToastMsg(LoginActivity.this, "Unauthorized. Please login again.");
                                break;
                            // Add more cases for other response codes as needed
                            default:
                                // Handle other response codes
                                UIUtils.customToastMsg(LoginActivity.this, "Unexpected response code: " + statusCode);
                        }
                    } else {
                        // Handle the case where the response body is null
                        UIUtils.customToastMsg(LoginActivity.this, "Response body is empty or null.");
                    }
                } else {
                    // Handle non-successful response (e.g., HTTP error codes)
                    int statusCode = response.code();
                    UIUtils.customToastMsg(LoginActivity.this, "HTTP Error: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return false;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        }
    }

    /*private void showUpgradeDialog(String url_app_link, String message) {
        String info = "Download And Install";

        final AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        } else {
            builder.setMessage("Please upgrade to the new version");
        }

        builder.setCancelable(false).setPositiveButton(info, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface d, final int id) {
                        if (!TextUtils.isEmpty(url_app_link)) {
                            startDownloadNInstall(url_app_link);
                        }
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    private void startDownloadNInstall(String url_app_link) {
        mDownloadStarted = true;
        UIUtils.customToastMsg(LoginActivity.this, "Downloading latest apk Please Wait...");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    downloadAbove13(url_app_link);
                } else {
                    downloadInstall(url_app_link);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                UIUtils.hideProgressDialog();
            }
        }.execute();
    }

    public void downloadInstall(String apkurl) {

        if (apkurl.contains("https")) {
            URI uri2 = null;
            try {
                uri2 = new URI(apkurl);
            } catch (URISyntaxException e) {
//                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = uri2.toURL();
            } catch (MalformedURLException e) {
//                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }


            HttpsURLConnection c = null;
            try {
                c = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
//                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }

            try {
                c.setRequestMethod("GET");
            } catch (ProtocolException e) {
//                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            c.setAllowUserInteraction(true);
            c.setUseCaches(false);
            try {
                c.connect();
            } catch (IOException e) {
//                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            int resp = 0;
            try {
                resp = c.getResponseCode();
            } catch (IOException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            if (resp == HttpsURLConnection.HTTP_OK) {
                try {
                    ContextWrapper cw = new ContextWrapper(this);
                    File file = new File(Environment.getExternalStorageDirectory() + "/apkdir/");*//*cw.getDir("apkdir", Context.MODE_PRIVATE);*//*

                    if (!file.exists())
                        file.mkdirs();
                    File outputFile = new File(file, "Sim.apk");
                    if (outputFile.exists())
                        outputFile.delete();


                    FileOutputStream fos = null;

                    fos = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[1024];
                    InputStream inputStream = null;
                    inputStream = c.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(
                            inputStream);

                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    int bytesRead = 0;
                    while (true) {
                        if (-1 == (bytesRead = bis.read(buffer, 0, buffer.length))) break;
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    inputStream.close();
                    downloadAndInstallUsingHttps(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    mDownloadStarted = false;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Download failed. Please retry.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }

    public void downloadAbove13(String apkurl) {
        Log.d("inside", "downloadInstallA13");
        in_complete_download = false;
        if (apkurl.contains("https")) {
            URI uri2 = null;
            try {
                uri2 = new URI(apkurl);
            } catch (URISyntaxException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = uri2.toURL();
            } catch (MalformedURLException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }


            HttpsURLConnection c = null;
            try {
                c = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }

            try {
                c.setRequestMethod("GET");
            } catch (ProtocolException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            c.setAllowUserInteraction(true);
            c.setUseCaches(false);
            try {
                c.connect();
            } catch (IOException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            int resp = 0;
            try {
                resp = c.getResponseCode();
            } catch (IOException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            if (resp == HttpsURLConnection.HTTP_OK) {
                ContextWrapper cw = new ContextWrapper(this);

//                String filename = "Sim.apk";
                Date apkDate = new Date();
                SimpleDateFormat tstampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String tstamp_date = tstampFormat.format(apkDate);
                String filename = "Sim-" + tstamp_date + ".apk";
                Log.d("inside ", "filename = " + filename);
                File destination = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    destination = new File(String.valueOf(MediaStore.Downloads.EXTERNAL_CONTENT_URI), filename);
                }
                if (destination.exists())
                    destination.delete();
                ContentValues values = new ContentValues();

                Log.d("inside = ", "destination = " + destination);
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "apk");
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

                Uri uri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    Log.d("inside = ", "MediaStore.Downloads.EXTERNAL_CONTENT_URI = " + MediaStore.Downloads.EXTERNAL_CONTENT_URI);
                }

                try {
                    ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "w"); //"w" specify's write mode
                    FileDescriptor fileDescriptor = descriptor.getFileDescriptor();

                    InputStream dataInputStream = new URL(apkurl).openStream();

                    OutputStream output = new FileOutputStream(fileDescriptor);
                    byte[] buf = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = dataInputStream.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }
                    dataInputStream.close();
                    output.close();

                    Log.i("File Size: ", destination.length() + "");
                    Log.d("inside", "File Size: " + destination.length() + "");
                    downloadAndInstallUsingHttpsA13(destination);
                } catch (Exception e) {
                    e.printStackTrace();
                    in_complete_download = true;
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(LoginActivity.this, "Download failed. Please retry.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }
    }

    public void downloadAndInstallUsingHttpsA13(File filePath) {
        try {
            File fileName = filePath;
            Log.d("inside = ", "fileName = " + fileName);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                fileName = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "simTech.apk");
                //fileName = new File(String.valueOf(MediaStore.Downloads.EXTERNAL_CONTENT_URI), downloadedFileName);
            } else {
                fileName = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Sim.apk");
            }
            Log.d("inside = ", "new fileName = " + fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                apkfileName = fileName;
                checkPermissionToInstallFromUnknownResources();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("TAG", "download completed trying to open application::::::::" + fileName.getAbsolutePath());
                Intent installApplicationIntent = new Intent(Intent.ACTION_VIEW);
                if (fileName.exists()) {
                    Log.i("TAG", "is readable:::::::::" + fileName.canRead());
                    fileName.setReadable(true);
                    installApplicationIntent.setDataAndType(FileProvider.getUriForFile(LoginActivity.this,
                            getApplicationContext().getPackageName() + ".provider",
                            fileName), "application/vnd.android.package-archive");
                } else {
                    Log.i("TAG", "file not found after downloading");
                }
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installApplicationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installApplicationIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", fileName);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                intent.setDataAndType(Uri.fromFile(fileName), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        } catch (Exception e) {
            UIUtils.hideProgressDialog();
//            appendLog(android.util.Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public void downloadAndInstallUsingHttps(File filePath) {
        try {
            File fileName = new File(filePath, "Sim.apk");

            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i("TAG", "download completed trying to open application::::::::" + fileName.getAbsolutePath());
                Intent installApplicationIntent = new Intent(Intent.ACTION_VIEW);
                if (fileName.exists()) {
                    Log.i("TAG", "is readable:::::::::" + fileName.canRead());
                    fileName.setReadable(true);
                    installApplicationIntent.setDataAndType(FileProvider.getUriForFile(LoginActivity.this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            fileName), "application/vnd.android.package-archive");
                } else {
                    Log.i("TAG", "file not found after downloading");
                }
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installApplicationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installApplicationIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", fileName);
                intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                intent.setDataAndType(Uri.fromFile(fileName), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        } catch (Exception e) {
            UIUtils.hideProgressDialog();
            appendLog(android.util.Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public void checkPermissionToInstallFromUnknownResources() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !getPackageManager().canRequestPackageInstalls()) {

            *//*Log.i("TAG", "download completed trying to open application::::::::" + .getAbsolutePath());
            Log.i("File Size ", "download completed trying to open application:::2401:::::" + apkfileName.length());
            long appSizeDownloaded = apkfileName.length();
//            long appSizeFromServer = PreferenceManager.getInstance(SalesMainActivity.this).getAppSize();
            Log.d("inside", "*************************");
            Log.d("inside", "appSizeDownloaded = " + appSizeDownloaded);
            Log.d("inside", "appSizeFromServer = " + appSizeFromServer);
            Log.d("inside", "*************************");
            if (appSizeFromServer == appSizeDownloaded) {
                Intent unknownAppSourceIntent = new Intent()
                        .setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse(String.format("package:%s", getPackageName())));

                ActivityResultLanucherforAPKfromUnknownSource.launch(unknownAppSourceIntent);
            } else {
                in_complete_download = true;
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(LoginActivity.this, "Download failed. Please retry.", Toast.LENGTH_LONG).show();
                    }
                });
            }*//*

            Intent unknownAppSourceIntent = new Intent()
                    .setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse(String.format("package:%s", getPackageName())));

            ActivityResultLanucherforAPKfromUnknownSource.launch(unknownAppSourceIntent);
        } else {
            *//*Log.i("TAG", "download completed trying to open application::::::::" + apkfileName.getAbsolutePath());
            Log.i("File Size ", "download completed trying to open application:::2401:::::" + apkfileName.length());
            long appSizeDownloaded = apkfileName.length();
            long appSizeFromServer = PreferenceManager.getInstance(LoginActivity.this).getAppSize();
            Log.d("inside", "*************************");
            Log.d("inside", "appSizeDownloaded = " + appSizeDownloaded);
            Log.d("inside", "appSizeFromServer = " + appSizeFromServer);
            Log.d("inside", "*************************");
            if (appSizeFromServer == appSizeDownloaded) {
                Intent installApplicationIntent = new Intent(Intent.ACTION_VIEW);
                if (apkfileName.exists()) {
                    Log.i("TAG", "is readable:::::::::" + apkfileName.canRead());
                    apkfileName.setReadable(true);
                    installApplicationIntent.setDataAndType(FileProvider.getUriForFile(LoginActivity.this,
                            getApplicationContext().getPackageName() + ".provider",
                            apkfileName), "application/vnd.android.package-archive");
                } else {
                    Log.i("TAG", "file not found after downloading");
                }
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installApplicationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installApplicationIntent);
            } else {
                in_complete_download = true;
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(LoginActivity.this, "Download failed. Please retry.", Toast.LENGTH_LONG).show();
                    }
                });
            }*//*

            Intent installApplicationIntent = new Intent(Intent.ACTION_VIEW);
            if (apkfileName.exists()) {
                Log.i("TAG", "is readable:::::::::" + apkfileName.canRead());
                apkfileName.setReadable(true);
                installApplicationIntent.setDataAndType(FileProvider.getUriForFile(LoginActivity.this,
                        getApplicationContext().getPackageName() + ".provider",
                        apkfileName), "application/vnd.android.package-archive");
            } else {
                Log.i("TAG", "file not found after downloading");
            }
            installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installApplicationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installApplicationIntent);
        }
    }

    ActivityResultLauncher<Intent> ActivityResultLanucherforAPKfromUnknownSource = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (!in_complete_download) {
                        Log.d("inside", "if launcher in_complete_download = " + in_complete_download);
                        Log.i("TAG", "download completed trying to open application::::::::" + apkfileName.getAbsolutePath());
                        Intent installApplicationIntent = new Intent(Intent.ACTION_VIEW);
                        if (apkfileName.exists()) {
                            Log.i("TAG", "is readable:::::::::" + apkfileName.canRead());
                            apkfileName.setReadable(true);
                            installApplicationIntent.setDataAndType(FileProvider.getUriForFile(LoginActivity.this,
                                    getApplicationContext().getPackageName() + ".provider",
                                    apkfileName), "application/vnd.android.package-archive");
                        } else {
                            Log.i("TAG", "file not found after downloading");
                        }
                        installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        installApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installApplicationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(installApplicationIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Download again while continuous Internet Connectivity", Toast.LENGTH_LONG).show();
                    }

                }
            });*/

    @Override
    protected void onPause() {
        super.onPause();
        if (!mDownloadStarted)
            mUpdateStarted = false;
    }

    // To hide and show system bottom navigation
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // You may also want to show the system navigation bar again
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UIUtils.isNetworkAvailable(LoginActivity.this)){
            SharedPreferences mCredentialsStorage;
            mCredentialsStorage = getSharedPreferences("AppSharedPreferences", MODE_PRIVATE);
            String userName = mCredentialsStorage.getString(LoginActivity.USERNAME, null);
            if(userName!=null && !userName.isEmpty()){
                idEdtUserName.setText(userName);
            }
        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
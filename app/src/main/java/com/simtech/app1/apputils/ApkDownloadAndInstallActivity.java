package com.simtech.app1.apputils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.TokenWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.simtech.app1.R;

public class ApkDownloadAndInstallActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    String apkPath = "https://simtechitsolutionspvtltd562-my.sharepoint.com/:u:/g/personal/prakash_yalavatti_simtechitsolutions_in1/EdnfBfLL7cxIsjT7lgRb4b8BglfeFQrebT52WfdfwGRKsg?e=A6i8qa";
    String targetSdkVersion = "31"; // Replace with the target SDK version of your app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.upgrade_apk);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Check if the app has the required permissions
            if (!hasRequiredPermissions()) {
                // Request the required permissions
                UIUtils.customToastMsg(ApkDownloadAndInstallActivity.this, "ask permission");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                // Permissions are granted, proceed with the installation
                UIUtils.customToastMsg(ApkDownloadAndInstallActivity.this, "before install");
                installApk(apkPath);
                UIUtils.customToastMsg(ApkDownloadAndInstallActivity.this, "after install");
            }
        } else {
            // Android version is lower than 13, proceed with the installation
            installApk(apkPath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the installation
                installApk(apkPath);
            } else {
                // Permission denied, handle the situation
            }
        }
    }

    private boolean hasRequiredPermissions() {
        // Check if the app has the required permissions
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void installApk(String apkPath) {
        // Execute the ADB command to install or upgrade the app
        try {
            Process process = Runtime.getRuntime().exec("adb install -r " + apkPath);
            process.waitFor(); // Wait for the installation to complete
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                UIUtils.customToastMsg(ApkDownloadAndInstallActivity.this, "APK installed");
                // APK installed/updated successfully
            } else {
                UIUtils.customToastMsg(ApkDownloadAndInstallActivity.this, "APK fail to install");
                // Failed to install/update the APK
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

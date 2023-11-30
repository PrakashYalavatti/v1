package com.simtech.app1;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

public class ModelApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    private static ModelApplication sSambhavApplication;
    public boolean activityVisible;
    public boolean activityDestroyed;

    public static ModelApplication getModelApplication() {
        return sSambhavApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sSambhavApplication = this;
        registerActivityLifecycleCallbacks(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        /*RetailerHomeActivity.sToken = PreferenceManager.getInstance(this).getToken();
        RetailerHomeActivity.sHulId = PreferenceManager.getInstance(this).getStoreID();*/

        backNofifyAction();

    }

    public void backNofifyAction() {
        this.notifyServerDown();
    }

    public void notifyServerDown() { //type_id = 506
        /*try {
            String message = PreferenceManager.getInstance(this).getServerInfo();
            int urlType = PreferenceManager.getInstance(this).getServerInfoCode();
//            Log.i("Srinu>> FCm1",urlType+" "+message);
            if (!TextUtils.isEmpty(message)) {
                Intent intent = new Intent(getApplicationContext(), ServerDownActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("URL-PATH", message);
                intent.putExtra("URL-TYPE", urlType);
                startActivity(intent);
            }
        } catch (Exception e) {
            appendLog(android.util.Log.getStackTraceString(e));
            e.printStackTrace();
        }*/
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        /*if (activity instanceof RetailerHomeActivity) {
            activityVisible = true;
            activityDestroyed = false;
        } else if (activity instanceof SalesMainActivity) {
            activityVisible = true;
            activityDestroyed = false;
        } else if (activity instanceof OutletInformationActivity) {
            activityVisible = true;
            activityDestroyed = false;
        }*/
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        /*if (activity instanceof RetailerHomeActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof SalesMainActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof OutletInformationActivity) {
            activityVisible = false;
            activityDestroyed = false;
        }*/
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        /*if (activity instanceof RetailerHomeActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof SalesMainActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof OutletInformationActivity) {
            activityVisible = false;
            activityDestroyed = false;
        }*/
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
        /*if (activity instanceof RetailerHomeActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof SalesMainActivity) {
            activityVisible = false;
            activityDestroyed = false;
        } else if (activity instanceof OutletInformationActivity) {
            activityVisible = false;
            activityDestroyed = false;
        }*/
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        /*if (activity instanceof RetailerHomeActivity) {
            activityVisible = false;
            activityDestroyed = true;
        } else if (activity instanceof SalesMainActivity) {
            activityVisible = false;
            activityDestroyed = true;
        } else if (activity instanceof OutletInformationActivity) {
            activityVisible = false;
            activityDestroyed = true;
        }*/
    }
}

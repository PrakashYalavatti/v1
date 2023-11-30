package com.simtech.app1.apputils;

import static com.simtech.app1.ModelApplication.getModelApplication;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogCreation {
    static final String TAG = LogCreation.class.getSimpleName();
    public static final String logFileExtension = "log.file";

    public static String getCurrentDate() {
        DateFormat dateFormatTemp = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        return dateFormatTemp.format(date1);
    }
    public static void createLogFileBelowAndroid10(String text) {
        try {
            File logFile = new File(Environment.getExternalStorageDirectory() + "/" + getCurrentDate() + "-" + logFileExtension);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, android.util.Log.getStackTraceString(e));
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, android.util.Log.getStackTraceString(e));
        }
    }

    public static void createLogFileAndroid10AndAbove(String text) {
        File filePath = createMainFolderIfNotExist();
        try {
            File logFile = new File(filePath + "/" + getCurrentDate() + "-" + logFileExtension);
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, android.util.Log.getStackTraceString(e));
        }
    }

    private static File createMainFolderIfNotExist() {
        File file = new File(getModelApplication().getExternalFilesDir(null) + "/salesEdge");
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                Log.e(TAG, android.util.Log.getStackTraceString(e));
            }
        }
        return file;
    }
}

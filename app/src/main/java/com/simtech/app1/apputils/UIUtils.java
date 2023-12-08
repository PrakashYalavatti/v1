package com.simtech.app1.apputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.simtech.app1.BuildConfig;
import com.simtech.app1.LoginActivity;
import com.simtech.app1.PlantationActivity;
import com.simtech.app1.R;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.pojo.dap.DAPPojo;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UIUtils {

    private static ProgressDialog sProgressDialog;
    private static APIInterface apiInterface;

    public static void customToastMsg(Context context, String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.custom_toast_red, null);
                View view = inflater.inflate(R.layout.custom_toast, null);
                TextView tv = (TextView) view.findViewById(R.id.custom_text);
                tv.setText(message);
                Toast toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }
        } catch (Exception e) {
            appendLog(android.util.Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public static void appendLog(String text) {
        text = CommonServices.getDateTime() + " " + BuildConfig.VERSION_CODE + " " + text;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            LogCreation.createLogFileBelowAndroid10(text);
        } else {
            LogCreation.createLogFileAndroid10AndAbove(text);
        }
    }

    public static void displayAlertDialog(Context activity, String msg, String title, boolean shouldCancellable, DialogInterface.OnClickListener okClickListener
            , DialogInterface.OnClickListener cancelClickListener, DialogInterface.OnDismissListener dismissListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        SpannableString spannableString = new SpannableString(msg);
//        Typeface typefaceSpan = Typeface.createFromAsset(activity.getAssets(), "fonts/sf_pro_display_regular");
//        spannableString.setSpan(typefaceSpan, 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialogBuilder.setMessage(msg);


        if (!TextUtils.isEmpty(title)) {
            SpannableString spannabletitle = new SpannableString(title);
//            Typeface typefaceSpan1 = Typeface.createFromAsset(activity.getAssets(), "fonts/sf_pro_display_bold");
//            spannabletitle.setSpan(typefaceSpan1, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            alertDialogBuilder.setTitle(spannabletitle);
        }


        if (okClickListener != null) {
            alertDialogBuilder.setPositiveButton(activity.getString(R.string.ok), okClickListener);
        }

        if (cancelClickListener != null) {
            alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel), cancelClickListener);
        }

        if (dismissListener != null) {
            alertDialogBuilder.setOnDismissListener(dismissListener);
        }


        try {
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(shouldCancellable);
            alertDialog.setCanceledOnTouchOutside(shouldCancellable);
            alertDialog.show();
        } catch (Exception e) {
            appendLog(android.util.Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            try {
                sProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                appendLog(android.util.Log.getStackTraceString(e));
                e.printStackTrace();
            }
            sProgressDialog = null;
        }
    }

    public static String getCurrentDateYYYY_MM_DD() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date(Calendar.getInstance().getTimeInMillis());
        return sdfDate.format(now).toString();
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long getDaysDifference(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();
        return diff / (24 * 60 * 60 * 1000); // Convert milliseconds to days
    }

    public static long getDiffNoOfDays(String plantingDate, String currentDate) {
        Date date1 = parseDate(plantingDate);
        Date date2 = parseDate(currentDate);

        // Calculate the difference in days
        long daysDifference = getDaysDifference(date1, date2);

        System.out.println("The difference in days is: " + daysDifference + " days");
        return daysDifference;
    }

    public static void showDialogWithL20DAP(Context context, String observationType, String startDate,
                                        String locationId, String trialTypeId, String replicationName, String vatietyCode, String observationLines) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dap_20_30_l1_4, null);

        // Initialize the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView tvReplication = dialogView.findViewById(R.id.tvReplication);
        TextView tvVarietyCode = dialogView.findViewById(R.id.tvVarietyCode);
        tvReplication.setText(replicationName);
        tvVarietyCode.setText(vatietyCode);
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Find the EditText fields in the custom layout
        TextView tvCanopy = dialogView.findViewById(R.id.tvCanopy);
        TextView tvL1 = dialogView.findViewById(R.id.tvL1);
        TextView tvL2 = dialogView.findViewById(R.id.tvL2);
        TextView tvL3 = dialogView.findViewById(R.id.tvL3);
        TextView tvL4 = dialogView.findViewById(R.id.tvL4);
        EditText editTextCanopy = dialogView.findViewById(R.id.editTextCanopy);
        EditText editTextL1 = dialogView.findViewById(R.id.editTextL1);
        EditText editTextL2 = dialogView.findViewById(R.id.editTextL2);
        EditText editTextL3 = dialogView.findViewById(R.id.editTextL3);
        EditText editTextL4 = dialogView.findViewById(R.id.editTextL4);
        EditText etRemarks = dialogView.findViewById(R.id.etRemarks);

        editTextL1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>1) {
                    Double sample1 = Double.valueOf(editable.toString());
                    if (sample1 <= 25) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample1);
                        editTextL1.setText(sample1Str);
                    } else {
                        editTextL1.setError("Please enter value below 25");
                        editTextL1.setText("");
                    }
                }
            }
        });
        editTextL2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>1) {
                    Double sample2 = Double.valueOf(editable.toString());
                    if (sample2 <= 25) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample2);
                        editTextL2.setText(sample1Str);
                    } else {
                        editTextL2.setError("Please enter value below 25");
                        editTextL2.setText("");
                    }
                }
            }
        });

        editTextL3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>1) {
                    Double sample3 = Double.valueOf(editable.toString());
                    if (sample3 <= 25) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample3);
                        editTextL3.setText(sample1Str);
                    } else {
                        editTextL3.setError("Please enter value below 25");
                        editTextL3.setText("");
                    }
                }
            }
        });

        editTextL4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>1) {
                    Double sample4 = Double.valueOf(editable.toString());
                    if (sample4 <= 25) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample4);
                        editTextL4.setText(sample1Str);
                    } else {
                        editTextL4.setError("Please enter value below 25");
                        editTextL4.setText("");
                    }
                }
            }
        });

        if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-1"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-2"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if (observationType.contains("45 DAP") && trialTypeId.equalsIgnoreCase("TRL-2")) {
            tvCanopy.setVisibility(View.VISIBLE);
            editTextCanopy.setVisibility(View.VISIBLE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        } else if (observationType.contains("45 DAP") && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.VISIBLE);
            editTextCanopy.setVisibility(View.VISIBLE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        }
        // Access the other EditText fields (editText3, editText4, and editText5) as needed
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle OK button click

            String textCanopy = editTextCanopy.getText().toString();
            String textL1 = editTextL1.getText().toString();
            String textL2 = editTextL2.getText().toString();
            String textL3 = editTextL3.getText().toString();
            String textL4 = editTextL4.getText().toString();
            String textRemarks = etRemarks.getText().toString();

            LocalDate date = null;
            int yearOnly = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
                yearOnly = date.getYear();
            }
            String observation = "";
            if(observationType.contains("20 DAP")){
                observation = "20dap";
            } else if(observationType.contains("30 DAP")){
                observation = "30dap";
            } else if(observationType.contains("45 DAP")){
                observation = "45dap";
            }
            // Create a JSONObject and add key-value pairs
            DAPPojo dapPojo = new DAPPojo();
            dapPojo.locationid = locationId;
            dapPojo.trialtypeid = trialTypeId;
            dapPojo.trialyear = yearOnly + "";
            dapPojo.varietycode = vatietyCode;
            dapPojo.replication = replicationName;
            dapPojo.l1 = textL1;
            dapPojo.l2 = textL2;
            dapPojo.l3 = textL3;
            dapPojo.l4 = textL4;
            dapPojo.canopy = textCanopy;
            dapPojo.remarks = textRemarks;
            dapPojo.fromdap = observation;
            if (UIUtils.isNetworkAvailable(context)) {
                callApiForDapInsertion(context, dapPojo);
            } else {
                Toast.makeText(context, context.getString(R.string.data_not_saved_internet), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle Cancel button click
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialogWithL5(Context context, String observationType, String startDate,
                                        String locationId, String trialTypeId, String replicationName, String vatietyCode, String observationLines) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dap_45_l1_4, null);

        // Initialize the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        TextView tvReplication = dialogView.findViewById(R.id.tvReplication);
        TextView tvVarietyCode = dialogView.findViewById(R.id.tvVarietyCode);
        tvReplication.setText(replicationName);
        tvVarietyCode.setText(vatietyCode);
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Find the EditText fields in the custom layout
        TextView tvCanopy = dialogView.findViewById(R.id.tvCanopy);
        TextView tvL1 = dialogView.findViewById(R.id.tvL1);
        TextView tvL2 = dialogView.findViewById(R.id.tvL2);
        TextView tvL3 = dialogView.findViewById(R.id.tvL3);
        TextView tvL4 = dialogView.findViewById(R.id.tvL4);
        EditText editTextCanopy = dialogView.findViewById(R.id.editTextCanopy);
        EditText editTextL1 = dialogView.findViewById(R.id.editTextL1);
        EditText editTextL2 = dialogView.findViewById(R.id.editTextL2);
        EditText editTextL3 = dialogView.findViewById(R.id.editTextL3);
        EditText editTextL4 = dialogView.findViewById(R.id.editTextL4);
        EditText etRemarks = dialogView.findViewById(R.id.etRemarks);

        if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-1"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-2"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if (observationType.contains("45 DAP") && trialTypeId.equalsIgnoreCase("TRL-2")) {
            tvCanopy.setVisibility(View.VISIBLE);
            editTextCanopy.setVisibility(View.VISIBLE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.GONE);
            editTextCanopy.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        } else if (observationType.contains("45 DAP") && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.VISIBLE);
            editTextCanopy.setVisibility(View.VISIBLE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        }
        // Access the other EditText fields (editText3, editText4, and editText5) as needed
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle OK button click

            String textCanopy = editTextCanopy.getText().toString();
            String textL1 = editTextL1.getText().toString();
            String textL2 = editTextL2.getText().toString();
            String textL3 = editTextL3.getText().toString();
            String textL4 = editTextL4.getText().toString();
            String textRemarks = etRemarks.getText().toString();

            LocalDate date = null;
            int yearOnly = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
                yearOnly = date.getYear();
            }
            String observation = "";
            if(observationType.contains("20 DAP")){
                observation = "20dap";
            } else if(observationType.contains("30 DAP")){
                observation = "30dap";
            } else if(observationType.contains("45 DAP")){
                observation = "45dap";
            }
            // Create a JSONObject and add key-value pairs
            DAPPojo dapPojo = new DAPPojo();
            dapPojo.locationid = locationId;
            dapPojo.trialtypeid = trialTypeId;
            dapPojo.trialyear = yearOnly + "";
            dapPojo.varietycode = vatietyCode;
            dapPojo.replication = replicationName;
            dapPojo.l1 = textL1;
            dapPojo.l2 = textL2;
            dapPojo.l3 = textL3;
            dapPojo.l4 = textL4;
            dapPojo.canopy = textCanopy;
            dapPojo.remarks = textRemarks;
            dapPojo.fromdap = observation;
            if (UIUtils.isNetworkAvailable(context)) {
                callApiForDapInsertion(context, dapPojo);
            } else {
                Toast.makeText(context, context.getString(R.string.data_not_saved_internet), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle Cancel button click
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void callApiForDapInsertion(Context context, DAPPojo dapPojo) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserLoginResponse> call = apiInterface.observation(dapPojo);
        Gson gson = new Gson();

        // Convert POJO to JSON
        String jsonString = gson.toJson(dapPojo);

        // Print the JSON representation
        System.out.println(jsonString);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful()) {
                    UserLoginResponse dapResponse = response.body();
                    if (dapResponse != null) {
                        int statusCode = response.code();
                        switch (statusCode) {
                            case 200:
                                Toast.makeText(context, dapResponse.message, Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                UIUtils.customToastMsg(context, "Response body is empty or null.");
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                UIUtils.customToastMsg(context, "Error");
            }
        });
    }

    private void openKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}

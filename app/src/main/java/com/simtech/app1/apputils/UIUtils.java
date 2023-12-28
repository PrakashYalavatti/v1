package com.simtech.app1.apputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.simtech.app1.BuildConfig;
import com.simtech.app1.R;
import com.simtech.app1.apiservices.APIClient;
import com.simtech.app1.apiservices.APIInterface;
import com.simtech.app1.apiservices.apirequestresponse.UserLoginResponse;
import com.simtech.app1.pojo.dap.DAPPojo;
import com.simtech.app1.pojo.harvest.HarvestDataPojo;
import com.simtech.app1.pojo.harvest.HarvestDetailPojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        TextView tvTask = dialogView.findViewById(R.id.tvTask);
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

        editTextL1.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(observationType.contains("20")){
            tvTask.setText("Plant data for 20 days");
        } else if(observationType.contains("30")){
            tvTask.setText("Plant data for 30 days");
        }

        /*editTextL1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length()>1) {
                    Integer sample1 = Integer.valueOf(editable.toString());
                    if (sample1 <= 25) {
                        editTextL1.setText(editable.toString());
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
                        editTextL2.setText(editable.toString());
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
                        editTextL3.setText(editable.toString());
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
                        editTextL4.setText(editable.toString());
                    } else {
                        editTextL4.setError("Please enter value below 25");
                        editTextL4.setText("");
                    }
                }
            }
        });*/

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
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date parsedDate = sdf.parse(startDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    yearOnly = calendar.get(Calendar.YEAR);
                } catch (ParseException e) {
                    // Handle parsing exception
                    e.printStackTrace();
                }
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
        final String[] selectedItem = new String[1];
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
        Spinner spinnerCanopy = dialogView.findViewById(R.id.spinnerCanopy);
        EditText editTextL1 = dialogView.findViewById(R.id.editTextL1);
        EditText editTextL2 = dialogView.findViewById(R.id.editTextL2);
        EditText editTextL3 = dialogView.findViewById(R.id.editTextL3);
        EditText editTextL4 = dialogView.findViewById(R.id.editTextL4);
        EditText etRemarks = dialogView.findViewById(R.id.etRemarks);

        if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-1"))) {
            tvCanopy.setVisibility(View.GONE);
            spinnerCanopy.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-2"))) {
            tvCanopy.setVisibility(View.GONE);
            spinnerCanopy.setVisibility(View.GONE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if (observationType.contains("45 DAP") && trialTypeId.equalsIgnoreCase("TRL-2")) {
            tvCanopy.setVisibility(View.VISIBLE);
            spinnerCanopy.setVisibility(View.VISIBLE);
            tvL3.setVisibility(View.GONE);
            editTextL3.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
        } else if ((observationType.contains("20 DAP") || observationType.contains("30 DAP")) && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.GONE);
            spinnerCanopy.setVisibility(View.GONE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        } else if (observationType.contains("45 DAP") && (trialTypeId.equalsIgnoreCase("TRL-3"))) {
            tvCanopy.setVisibility(View.VISIBLE);
            spinnerCanopy.setVisibility(View.VISIBLE);
            tvL4.setVisibility(View.GONE);
            editTextL4.setVisibility(View.GONE);
            tvL1.setText("Sample1");
            tvL2.setText("Sample2");
            tvL3.setText("Sample3");
        }


        String[] canopyPerValue = new String[51];
        canopyPerValue[0] = "Select %";
        canopyPerValue[1] = "<50%";
        for (int i = 2; i <= 50; i++) {
            canopyPerValue[i] = (50 + i) + "%";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, canopyPerValue);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCanopy.setAdapter(adapter);

        // Set up the OnItemSelectedListener
        spinnerCanopy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from the Spinner
                selectedItem[0] = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here if nothing is selected
            }
        });
        // Access the other EditText fields (editText3, editText4, and editText5) as needed
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle OK button click
            String textCanopy = selectedItem[0].toString();
            String textL1 = editTextL1.getText().toString();
            String textL2 = editTextL2.getText().toString();
            String textL3 = editTextL3.getText().toString();
            String textL4 = editTextL4.getText().toString();
            String textRemarks = etRemarks.getText().toString();
            handleSaveButtonClick(context, dialog, startDate, observationType, locationId, trialTypeId, vatietyCode,
                    replicationName, textRemarks, textCanopy, textL1, textL2, textL3, textL4);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle Cancel button click
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
        dialog.show();
    }

    public static void showDialogForHarvest(Context context, String observationType, String startDate, String locationId, String trialTypeId, String replicationName, String vatietyCode, String nObservationLines) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.harvest, null);
        final String[] selectedItem = new String[1];
        // Initialize the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView tvReplication = dialogView.findViewById(R.id.tvReplication);
        TextView tvVarietyCode = dialogView.findViewById(R.id.tvVarietyCode);
        tvReplication.setText(replicationName);
        tvVarietyCode.setText(vatietyCode);

        TextView tvoverallLess45 = dialogView.findViewById(R.id.overallLess45);
        TextView tvoverallLess45Weight = dialogView.findViewById(R.id.overallLess45Weight);
        TextView tvgreeningLess45 = dialogView.findViewById(R.id.greeningLess45);
        TextView tvgreeningLess45Weight = dialogView.findViewById(R.id.greeningLess45Weight);
        TextView tvcrackingLess45 = dialogView.findViewById(R.id.crackingLess45);
        TextView tvcrackingLess45Weight = dialogView.findViewById(R.id.crackingLess45Weight);
        TextView tvabnoramlityLess45 = dialogView.findViewById(R.id.abnoramlityLess45);
        TextView tvabnoramlityLess45Weight = dialogView.findViewById(R.id.abnoramlityLess45Weight);
        TextView tvoverallGreater45 = dialogView.findViewById(R.id.overallGreater45);
        TextView tvoverallGreater45Weight = dialogView.findViewById(R.id.overallGreater45Weight);
        TextView tvgreeningGreater45 = dialogView.findViewById(R.id.greeningGreater45);
        TextView tvgreeningGreater45Weight = dialogView.findViewById(R.id.greeningGreater45Weight);
        TextView tvcrackingGreater45 = dialogView.findViewById(R.id.crackingGreater45);
        TextView tvcrackingGreater45Weight = dialogView.findViewById(R.id.crackingGreater45Weight);
        TextView tvabnoramlityGreater45 = dialogView.findViewById(R.id.abnoramlityGreater45);
        TextView tvabnoramlityGreater45Weight = dialogView.findViewById(R.id.abnoramlityGreater45Weight);

        String overallLess45text = tvoverallLess45.getText().toString();
        if(overallLess45text.isEmpty())
            overallLess45text = "";
        String overallLess45Weighttext = tvoverallLess45Weight.getText().toString();
        if(overallLess45Weighttext.isEmpty())
            overallLess45Weighttext = "";
        String greeningLess45text = tvgreeningLess45.getText().toString();
        if(greeningLess45text.isEmpty())
            greeningLess45text = "";
        String greeningLess45Weighttext = tvgreeningLess45Weight.getText().toString();
        if(greeningLess45Weighttext.isEmpty())
            greeningLess45Weighttext = "";
        String crackingLess45text = tvcrackingLess45.getText().toString();
        if(crackingLess45text.isEmpty())
            crackingLess45text = "";
        String crackingLess45Weighttext = tvcrackingLess45Weight.getText().toString();
        if(crackingLess45Weighttext.isEmpty())
            crackingLess45Weighttext = "";
        String abnoramlityLess45text = tvabnoramlityLess45.getText().toString();
        if(abnoramlityLess45text.isEmpty())
            abnoramlityLess45text = "";
        String abnoramlityLess45Weighttext = tvabnoramlityLess45Weight.getText().toString();
        if(abnoramlityLess45Weighttext.isEmpty())
            abnoramlityLess45Weighttext = "";
        String overallGreater45text = tvoverallGreater45.getText().toString();
        if(overallGreater45text.isEmpty())
            overallGreater45text = "";
        String overallGreater45Weighttext = tvoverallGreater45Weight.getText().toString();
        if(overallGreater45Weighttext.isEmpty())
            overallGreater45Weighttext = "";
        String greeningGreater45text = tvgreeningGreater45.getText().toString();
        if(greeningGreater45text.isEmpty())
            greeningGreater45text = "";
        String greeningGreater45Weighttext = tvgreeningGreater45Weight.getText().toString();
        if(greeningGreater45Weighttext.isEmpty())
            greeningGreater45Weighttext = "";
        String crackingGreater45text = tvcrackingGreater45.getText().toString();
        if(crackingGreater45text.isEmpty())
            crackingGreater45text = "";
        String crackingGreater45Weighttext = tvcrackingGreater45Weight.getText().toString();
        if(crackingGreater45Weighttext.isEmpty())
            crackingGreater45Weighttext = "";
        String abnoramlityGreater45text = tvabnoramlityGreater45.getText().toString();
        if(abnoramlityGreater45text.isEmpty())
            abnoramlityGreater45text = "";
        String abnoramlityGreater45Weighttext = tvabnoramlityGreater45Weight.getText().toString();
        if(abnoramlityGreater45Weighttext.isEmpty())
            abnoramlityGreater45Weighttext = "";

        builder.setCancelable(false);

        String finalOverallLess45text = overallLess45text;
        String finalOverallLess45Weighttext = overallLess45Weighttext;
        String finalGreeningLess45text = greeningLess45text;
        String finalGreeningLess45Weighttext = greeningLess45Weighttext;
        String finalCrackingLess45text = crackingLess45text;
        String finalCrackingLess45Weighttext = crackingLess45Weighttext;
        String finalAbnoramlityLess45text = abnoramlityLess45text;
        String finalAbnoramlityLess45Weighttext = abnoramlityLess45Weighttext;
        String finalOverallGreater45text = overallGreater45text;
        String finalOverallGreater45Weighttext = overallGreater45Weighttext;
        String finalGreeningGreater45text = greeningGreater45text;
        String finalGreeningGreater45Weighttext = greeningGreater45Weighttext;
        String finalCrackingGreater45text = crackingGreater45text;
        String finalCrackingGreater45Weighttext = crackingGreater45Weighttext;
        String finalAbnoramlityGreater45text = abnoramlityGreater45text;
        String finalAbnoramlityGreater45Weighttext = abnoramlityGreater45Weighttext;
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Handle OK button click
            LocalDate date = null;
            int yearOnly = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
                yearOnly = date.getYear();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date parsedDate = sdf.parse(startDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    yearOnly = calendar.get(Calendar.YEAR);
                } catch (ParseException e) {
                    // Handle parsing exception
                    e.printStackTrace();
                }
            }

            ArrayList<HarvestDetailPojo> dataList = new ArrayList<>();

            HarvestDataPojo data1 = new HarvestDataPojo("Overall","<45mm", "No. Of Tubers", finalOverallLess45text);
            HarvestDataPojo data2 = new HarvestDataPojo("Overall","<45mm", "Tuber Weight", finalOverallLess45Weighttext);
            HarvestDataPojo data3 = new HarvestDataPojo("Greening","<45mm", "No. Of Tubers", finalGreeningLess45text);
            HarvestDataPojo data4 = new HarvestDataPojo("Greening","<45mm", "Tuber Weight", finalGreeningLess45Weighttext);
            HarvestDataPojo data5 = new HarvestDataPojo("Cracking","<45mm", "No. Of Tubers", finalCrackingLess45text);
            HarvestDataPojo data6 = new HarvestDataPojo("Cracking","<45mm", "Tuber Weight", finalCrackingLess45Weighttext);
            HarvestDataPojo data7 = new HarvestDataPojo("Abnormality","<45mm", "No. Of Tubers", finalAbnoramlityLess45text);
            HarvestDataPojo data8 = new HarvestDataPojo("Abnormality","<45mm", "Tuber Weight", finalAbnoramlityLess45Weighttext);
            HarvestDataPojo data9 = new HarvestDataPojo("Overall",">45mm", "No. Of Tubers", finalOverallGreater45text);
            HarvestDataPojo data10 = new HarvestDataPojo("Overall",">45mm", "Tuber Weight", finalOverallGreater45Weighttext);
            HarvestDataPojo data11= new HarvestDataPojo("Greening",">45mm", "No. Of Tubers", finalGreeningGreater45text);
            HarvestDataPojo data12 = new HarvestDataPojo("Greening",">45mm", "Tuber Weight", finalGreeningGreater45Weighttext);
            HarvestDataPojo data13 = new HarvestDataPojo("Cracking",">45mm", "No. Of Tubers", finalCrackingGreater45text);
            HarvestDataPojo data14 = new HarvestDataPojo("Cracking",">45mm", "Tuber Weight", finalCrackingGreater45Weighttext);
            HarvestDataPojo data15 = new HarvestDataPojo("Abnormality",">45mm", "No. Of Tubers", finalAbnoramlityGreater45text);
            HarvestDataPojo data16 = new HarvestDataPojo("Abnormality",">45mm", "Tuber Weight", finalAbnoramlityGreater45Weighttext);

            HarvestDetailPojo pojo = new HarvestDetailPojo(locationId, yearOnly+"", trialTypeId, vatietyCode, replicationName);
            pojo.harvest_data = new ArrayList<>();
            pojo.harvest_data.add(data1);
            pojo.harvest_data.add(data2);
            pojo.harvest_data.add(data3);
            pojo.harvest_data.add(data4);
            pojo.harvest_data.add(data5);
            pojo.harvest_data.add(data6);
            pojo.harvest_data.add(data7);
            pojo.harvest_data.add(data8);
            pojo.harvest_data.add(data9);
            pojo.harvest_data.add(data10);
            pojo.harvest_data.add(data11);
            pojo.harvest_data.add(data12);
            pojo.harvest_data.add(data13);
            pojo.harvest_data.add(data14);
            pojo.harvest_data.add(data15);
            pojo.harvest_data.add(data16);

            dataList.add(pojo);

            Gson gson = new Gson();
            String jsonString = gson.toJson(pojo);

            if (UIUtils.isNetworkAvailable(context)) {
                callAPiForHarvest(context, pojo);
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
        // Set "adjust pan" windowSoftInputMode
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );
        dialog.show();
    }

    private static void callAPiForHarvest(Context context, HarvestDetailPojo dataList) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserLoginResponse> call = apiInterface.observationharvest(dataList);
        /*Gson gson = new Gson();

        // Convert POJO to JSON
        String jsonString = gson.toJson(dapPojo);

        // Print the JSON representation
        System.out.println(jsonString);*/
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

    private static void handleSaveButtonClick(Context context, DialogInterface dialog, String startDate, String observationType,
                                              String locationId, String trialTypeId, String vatietyCode, String replicationName, String textRemarks, String textCanopy, String textL1, String textL2, String textL3, String textL4) {
        if(textCanopy.equalsIgnoreCase("Select %")){
            Toast.makeText(context, "Please select canopy percentage", Toast.LENGTH_SHORT).show();
        } else if (textL1.length()<1){
            Toast.makeText(context, "Please enter L1 reading", Toast.LENGTH_SHORT).show();
        } else if(textL2.length()<1){
            Toast.makeText(context, "Please enter L2 reading", Toast.LENGTH_SHORT).show();
        } else if(textL3.length()<1){
            Toast.makeText(context, "Please enter L3 reading", Toast.LENGTH_SHORT).show();
        } else if(textL4.length()<1){
            Toast.makeText(context, "Please enter L4 reading", Toast.LENGTH_SHORT).show();
        } else {
            LocalDate date = null;
            int yearOnly = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
                yearOnly = date.getYear();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date parsedDate = sdf.parse(startDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    yearOnly = calendar.get(Calendar.YEAR);
                } catch (ParseException e) {
                    // Handle parsing exception
                    e.printStackTrace();
                }
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
        }
    }

    private static void callApiForDapInsertion(Context context, DAPPojo dapPojo) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<UserLoginResponse> call = apiInterface.observation(dapPojo);
        /*Gson gson = new Gson();

        // Convert POJO to JSON
        String jsonString = gson.toJson(dapPojo);

        // Print the JSON representation
        System.out.println(jsonString);*/
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

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String findDifference(String serverDateTimeString, String deviceCurrentDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // parse method is used to parse the text from a string to produce the date
            Date d1 = sdf.parse(serverDateTimeString);
            Date d2 = sdf.parse(deviceCurrentDateTime);

            // Calculate time difference in minutes
            long difference_In_Time = d2.getTime() - d1.getTime();
            long difference_In_Minutes = difference_In_Time / (1000 * 60);

            Log.d("difference_In_Minutes", difference_In_Minutes +"");
            return String.valueOf(difference_In_Minutes);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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

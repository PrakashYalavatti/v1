package com.simtech.app1.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.R;
import com.simtech.app1.apputils.UIUtils;
import com.simtech.app1.pojo.layout.FieldLayoutActivity;
import com.simtech.app1.pojonew.TrialtypePojo;

import java.util.ArrayList;

public class MainMenuTrialObservationAdapter extends RecyclerView.Adapter<MainMenuTrialObservationAdapter.ViewHolder> {
    private ArrayList<TrialtypePojo> data;
    private Context context;
    String planningDate, locationName, locationId;
    private int integerValue;

    public MainMenuTrialObservationAdapter(Context context, ArrayList<TrialtypePojo> data, String planingDate, String locationName, String locationId) {
        this.context = context;
        this.data = data;
        this.planningDate = planingDate;
        this.locationName = locationName;
        this.locationId = locationId;

        startAnimationLoop();
    }

    private void startAnimationLoop() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final int animationDelay = 2000; // Set the delay between animations in milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged(); // Trigger onBindViewHolder to be called again
                handler.postDelayed(this, animationDelay);
            }
        }, animationDelay);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTrialName;
        public Button tvObservation;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTrialName = itemView.findViewById(R.id.tvTrialName);
            tvObservation = itemView.findViewById(R.id.tvObservation);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrialtypePojo trialData = data.get(position);
        String observationType = trialData.getTrialstatus();
        String observation = "";
        String trialTypeId = trialData.getTrialtypeid();
        String trialTypeName = trialData.getTrialtypename();
        String currentDate = UIUtils.getCurrentDateYYYY_MM_DD();
        holder.tvTrialName.setText(trialTypeName);
        long noOfDaysDifference = UIUtils.getDiffNoOfDays(planningDate, currentDate);

        if(observationType != null){
            String firstThreeCharacters = observationType.substring(0, 2);
            // Convert the substring to an integer
            integerValue = Integer.parseInt(firstThreeCharacters);
            if(integerValue < 5 ){
//            holder.tvObservation.setBackgroundColor(Color.RED);
                if (shouldRunAnimation(position)) {
                    // Run the animation
                    startInfiniteAnimation(holder.tvObservation);
                } else {
                    // Optionally, stop the animation or set the view to its initial state
                }
            }
        }

//        noOfDaysDifference = 0;
        if (noOfDaysDifference == 0) {
            observationType = context.getText(R.string.at_planting).toString();
            holder.tvObservation.setText(observationType);
        } else if (noOfDaysDifference == 20) {
            observationType = context.getText(R.string.dap_20).toString();
            holder.tvObservation.setText(observationType);
        } else if (noOfDaysDifference == 30) {
            observationType = context.getText(R.string.dap_30).toString();
            holder.tvObservation.setText(observationType);
        } else if (noOfDaysDifference == 45) {
            observationType = context.getText(R.string.dap_45).toString();
            holder.tvObservation.setText(observationType);
        } else if(noOfDaysDifference == 90){
//            observation = "invalid";
            observationType = context.getText(R.string.harvest).toString();
            holder.tvObservation.setText(observationType);
        } else if(noOfDaysDifference > 90){
            observationType = context.getText(R.string.cook_test).toString();
            holder.tvObservation.setText(observationType);
        }else {
            holder.tvObservation.setText(observationType);
        }

        String finalObservation = observation;
        String finalObservationType = observationType;
        holder.tvObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(noOfDaysDifference <= 2 ){
                    Intent plantationIntent = new Intent(context, FieldLayoutActivity.class);
                    plantationIntent.putExtra("observationType", finalObservationType);
                    plantationIntent.putExtra("startDate", planningDate);
                    plantationIntent.putExtra("locationName", locationName);
                    plantationIntent.putExtra("locationId", locationId);
                    plantationIntent.putExtra("trialTypeId", trialTypeId);
                    plantationIntent.putExtra("trialTypeName", trialTypeName);
                    context.startActivity(plantationIntent);
//                } else {
//                    Toast.makeText(context, "Observation date beyond 2 days", Toast.LENGTH_SHORT).show();
//                }

                /*if (finalObservation.equalsIgnoreCase("invalid")) {
                    Toast.makeText(context, finalObservationType, Toast.LENGTH_SHORT).show();
                } else {
                    Intent plantationIntent = new Intent(context, FieldLayoutActivity.class);
                    plantationIntent.putExtra("observationType", finalObservationType);
                    plantationIntent.putExtra("startDate", planningDate);
                    plantationIntent.putExtra("locationName", locationName);
                    plantationIntent.putExtra("locationId", locationId);
                    plantationIntent.putExtra("trialTypeId", trialTypeId);
                    plantationIntent.putExtra("trialTypeName", trialTypeName);
                    context.startActivity(plantationIntent);
                }*/
            }
        });
    }

    private boolean shouldRunAnimation(int position) {
        // Implement your condition here
        // For example, return true if you want to run the animation for every item
        return true;
    }

    private void startInfiniteAnimation(TextView view) {
        // Smooth Color Animation
        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(view, "textColor",
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.red));

        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(1000); // Set the duration as needed
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);

        colorAnimator.start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
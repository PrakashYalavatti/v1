package com.simtech.app1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    public MainMenuTrialObservationAdapter(Context context, ArrayList<TrialtypePojo> data, String planingDate, String locationName, String locationId) {
        this.context = context;
        this.data = data;
        this.planningDate = planingDate;
        this.locationName = locationName;
        this.locationId = locationId;
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

        // Production remove

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
        } else {
            observation = "invalid";
            holder.tvObservation.setText(observationType);
        }

        String finalObservation = observation;
        String finalObservationType = observationType;
        holder.tvObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plantationIntent = new Intent(context, FieldLayoutActivity.class);
                plantationIntent.putExtra("observationType", finalObservationType);
                plantationIntent.putExtra("startDate", planningDate);
                plantationIntent.putExtra("locationName", locationName);
                plantationIntent.putExtra("locationId", locationId);
                plantationIntent.putExtra("trialTypeId", trialTypeId);
                plantationIntent.putExtra("trialTypeName", trialTypeName);
                context.startActivity(plantationIntent);
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

    @Override
    public int getItemCount() {
        return data.size();
    }
}
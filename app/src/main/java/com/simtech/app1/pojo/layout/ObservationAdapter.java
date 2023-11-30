package com.simtech.app1.pojo.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.R;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ViewHolder> {
    private Context context;
    private String startDate, observationType, locationName, locationId, trialTypeName,
            trialTypeId, nReplications, nObservationLines, purpose;
    private ArrayList<ObservationPojo> observations;

    public ObservationAdapter(Context context, String observationType, String purpose, ArrayList<ObservationPojo> observations, String startDate, String locationName, String locationId, String trialTypeName, String trialTypeId, String nReplications, String nObservationLines) {
        this.context = context;
        this.observationType = observationType;
        this.startDate = startDate;
        this.locationName = locationName;
        this.locationId = locationId;
        this.trialTypeName = trialTypeName;
        this.trialTypeId = trialTypeId;
        this.purpose = purpose;
        this.observations = observations;
        this.nReplications = nReplications;
        this.nObservationLines = nObservationLines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ObservationPojo observation = observations.get(position);
        holder.observationTextView.setText(observation.getObservation());
        // ... (bind other data)

        // Set up nested RecyclerView for varieties with GridLayout
        VarietyAdapter varietyAdapter = new VarietyAdapter(context, purpose, observationType, observation, startDate, locationName, locationId, trialTypeName,
                trialTypeId, nReplications, nObservationLines);
        // span count is for showing 1 variety code in one cell
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        holder.varietyRecyclerView.setLayoutManager(layoutManager);
        holder.varietyRecyclerView.setAdapter(varietyAdapter);
    }

    @Override
    public int getItemCount() {
        return observations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView observationTextView;
        RecyclerView varietyRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            observationTextView = itemView.findViewById(R.id.observationTextView);
            varietyRecyclerView = itemView.findViewById(R.id.varietyRecyclerView);
            // ... (initialize other views)
        }
    }
}

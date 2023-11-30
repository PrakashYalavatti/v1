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

public class PurposeAdapter extends RecyclerView.Adapter<PurposeAdapter.ViewHolder> {
    private String startDate, observationType, locationName, locationId, trialTypeName, trialTypeId, nReplications, nObservationLines;
    private Context context;
    private ArrayList<PurposePojo> purposes;

    public PurposeAdapter(Context context, String observationType, ArrayList<PurposePojo> purposes, String startDate,
                          String locationName, String locationId, String trialTypeName, String trialTypeId, String nReplications, String nObservationLines) {
        this.context = context;
        this.observationType = observationType;
        this.startDate = startDate;
        this.locationName = locationName;
        this.locationId = locationId;
        this.trialTypeName = trialTypeName;
        this.trialTypeId = trialTypeId;
        this.purposes = purposes;
        this.nReplications = nReplications;
        this.nObservationLines = nObservationLines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurposePojo purpose = purposes.get(position);
        holder.purposeTextView.setText(purpose.getPurpose());
        // ... (bind other data)

        // Set up nested RecyclerView for observations with horizontal layout
        ObservationAdapter observationAdapter = new ObservationAdapter(context, observationType, purpose.getPurpose(), purpose.getObservations(), startDate, locationName, locationId, trialTypeName, trialTypeId, nReplications, nObservationLines);
        GridLayoutManager layoutManager = new GridLayoutManager(context, Integer.parseInt(nReplications));
        holder.observationRecyclerView.setLayoutManager(layoutManager);
        holder.observationRecyclerView.setAdapter(observationAdapter);
    }

    @Override
    public int getItemCount() {
        return purposes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView purposeTextView;
        RecyclerView observationRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            purposeTextView = itemView.findViewById(R.id.purposeTextView);
            observationRecyclerView = itemView.findViewById(R.id.observationRecyclerView);
            // ... (initialize other views)
        }
    }
}

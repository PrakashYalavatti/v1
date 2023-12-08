package com.simtech.app1.pojo.layout;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.MainMenuActivity;
import com.simtech.app1.PlantationActivity;
import com.simtech.app1.R;
import com.simtech.app1.apputils.UIUtils;

import java.util.ArrayList;

public class VarietyAdapter extends RecyclerView.Adapter<VarietyAdapter.ViewHolder> {
    private Context context;
    private String startDate, observationType, locationName, locationId, trialTypeName, trialTypeId,
            purposeName, nReplications, nObservationLines;
    private ObservationPojo varieties;
    private String replicationName;

    public VarietyAdapter(Context context, String purposeName, String observationType, ObservationPojo varieties, String startDate, String locationName, String locationId, String trialTypeName, String trialTypeId, String nReplications, String nObservationLines) {
        this.context = context;
        this.observationType = observationType;
        this.startDate = startDate;
        this.locationName = locationName;
        this.locationId = locationId;
        this.trialTypeName = trialTypeName;
        this.trialTypeId = trialTypeId;
        this.purposeName = purposeName;
        this.varieties = varieties;
        this.nReplications = nReplications;
        this.nObservationLines = nObservationLines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout4, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        VarietyDetailsPojo variety = varieties.getVarieties().get(position);
        String vatietyCode = variety.getVarietycode();
        String variatyName = variety.getVarietyname();
        replicationName = varieties.getObservation();
        holder.varietyCodeTextView.setText(vatietyCode);
        holder.varietyNameTextView.setText(variatyName);

        // ... (bind other data)
        holder.lyVariety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UIUtils.isNetworkAvailable(context)){
                    // Need to add the condition for Observation type whether it is planting, 20DAP, 30DAP or 45DAP
                    if (observationType.equalsIgnoreCase(context.getString(R.string.at_planting)) || observationType.contains("Harvest")) {
                        Intent plantingIntent = new Intent(context, PlantationActivity.class);
                        plantingIntent.putExtra("observationType", observationType);
                        plantingIntent.putExtra("startDate", startDate);
                        plantingIntent.putExtra("locationName", locationName);
                        plantingIntent.putExtra("locationId", locationId);
                        plantingIntent.putExtra("trialTypeId", trialTypeId);
                        plantingIntent.putExtra("trialTypeName", trialTypeName);
                        plantingIntent.putExtra("nReplications", nReplications);
                        plantingIntent.putExtra("varietyCode", vatietyCode);
                        context.startActivity(plantingIntent);
                    } else if(observationType.contains("20 DAP")){
                        UIUtils.showDialogWithL20DAP(context, observationType, startDate, locationId, trialTypeId, replicationName, vatietyCode, nObservationLines);
                    } else if(observationType.contains("30 DAP")){
                        UIUtils.showDialogWithL20DAP(context, observationType, startDate, locationId, trialTypeId, replicationName, vatietyCode, nObservationLines);
                    } else if(observationType.contains("45 DAP")){
                        UIUtils.showDialogWithL5(context, observationType, startDate, locationId, trialTypeId, replicationName, vatietyCode, nObservationLines);
                    }
                }else {
                    Toast.makeText(context, context.getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return varieties.getVarieties().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lyVariety;
        TextView varietyCodeTextView;
        TextView varietyNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lyVariety = itemView.findViewById(R.id.lyVariety);
            varietyCodeTextView = itemView.findViewById(R.id.varietyCodeTextView);
            varietyNameTextView = itemView.findViewById(R.id.varietyNameTextView);
            // ... (initialize other views)
        }
    }
}

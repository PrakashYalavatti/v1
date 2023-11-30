package com.simtech.app1.pojo.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.R;

public class LocationDetailsAdapter extends RecyclerView.Adapter<LocationDetailsAdapter.ViewHolder> {
    private Context context;
    String observationType;
    private LocationDetailsDataPojo data;

    public LocationDetailsAdapter(Context context, String observationType, LocationDetailsDataPojo data) {
        this.context = context;
        this.observationType = observationType;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set up nested RecyclerView for purposes
        PurposeAdapter purposeAdapter = new PurposeAdapter(context, observationType, data.getPurposes(), data.getStart_date(),
                data.getLocation_name(), data.getLocation_id(), data.getTrial_type_name(), data.getTrial_type_id(), data.getN_replications(), data.getN_observation_lines());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.purposeRecyclerView.setLayoutManager(layoutManager);
        holder.purposeRecyclerView.setAdapter(purposeAdapter);
    }

    @Override
    public int getItemCount() {
        return 1; // Assuming there's only one LocationDetailsDataPojo in your example
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView purposeRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            purposeRecyclerView = itemView.findViewById(R.id.purposeRecyclerView);
            // ... (initialize other views)
        }
    }
}

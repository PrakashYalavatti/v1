package com.simtech.app1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.R;
import com.simtech.app1.apiservices.apirequestresponse.MainMenuResponse;
import com.simtech.app1.pojo.TrialObservationPojo;

import java.util.ArrayList;


public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ExperimentViewHolder> {
    private ArrayList<TrialObservationPojo> data;
    private Context context;

    public MainMenuAdapter(ArrayList<TrialObservationPojo> data, Context context) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public ExperimentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainmenu_item_layout, parent, false);
        return new ExperimentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperimentViewHolder holder, int position) {
        TrialObservationPojo trialObservationPojo = data.get(position);
        if(trialObservationPojo!=null){
            String planingDate = trialObservationPojo.getStartdate();
            String locationName = trialObservationPojo.getLocationname();
            String locationId = trialObservationPojo.getLocationid();
            holder.tvDate.setText(trialObservationPojo.getStartdate());
            holder.tvLocation.setText(trialObservationPojo.getLocationname());

            MainMenuTrialObservationAdapter  mainMenuTrialObservationAdapter = new MainMenuTrialObservationAdapter(context, trialObservationPojo.getTrialtype(), planingDate, locationName, locationId);
            holder.rvTrialObservation.setAdapter(mainMenuTrialObservationAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ExperimentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvLocation;

        RecyclerView rvTrialObservation;

        public ExperimentViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            rvTrialObservation = itemView.findViewById(R.id.rvTrialObservation);
        }
    }
}

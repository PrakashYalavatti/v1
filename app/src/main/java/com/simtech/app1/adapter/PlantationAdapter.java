package com.simtech.app1.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simtech.app1.EditPlantingSamples;
import com.simtech.app1.R;
import com.simtech.app1.pojo.planting.PlantatingVarietyDataPojo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlantationAdapter extends RecyclerView.Adapter<PlantationAdapter.ViewHolder> {
    private String n_replications;
    private ArrayList<PlantatingVarietyDataPojo> data;
    private Context context;
    String varietyCodeFromActivity, trialtypename;
    private int focusedPosition = RecyclerView.NO_POSITION;

    private EditPlantingSamples editDataListener;

    public PlantationAdapter(Context context, ArrayList<PlantatingVarietyDataPojo> data, String varietyCode, String trialtypename, String n_replications) {
        this.context = context;
        this.data = data;
        this.varietyCodeFromActivity = varietyCode;
        this.trialtypename = trialtypename;
        this.editDataListener = editDataListener;
        this.n_replications = n_replications;

    }

    /*public int findPositionByItemId(String itemId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getVarietycode().equals(itemId)) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION; // Item not found
    }
    public void setFocusedPosition(int position) {
        focusedPosition = position;
    }

    public int getFocusedPosition() {
        return focusedPosition;
    }*/

    @NonNull
    @Override
    public PlantationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plantation_item, parent, false);
        return new PlantationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantationAdapter.ViewHolder holder, int position) {
        PlantatingVarietyDataPojo varietyDataPojo = data.get(position);
        String varietyCode = varietyDataPojo.varietycode;
        String variatyName = varietyDataPojo.varietyname;
        String purpose = varietyDataPojo.purpose;
        holder.etSample1.setText(varietyDataPojo.sample1);
        holder.etSample2.setText(varietyDataPojo.sample2);
        holder.etRemarks.setText(varietyDataPojo.remarks);

        holder.tvVerityCode.setText(varietyCode);
        /*holder.tvVerityName.setText(variatyName);*/
        holder.tvPurpose.setText(purpose);

        varietyDataPojo.sample1 = holder.etSample1.getText().toString();
        varietyDataPojo.sample2 = holder.etSample2.getText().toString();

        /*if (trialtypename.contains("FastTrack")) {
            holder.etSample3.setVisibility(View.GONE);
        } else {*/
            holder.etSample3.setVisibility(View.VISIBLE);
            holder.etSample3.setText(varietyDataPojo.sample3);
            varietyDataPojo.sample3 = holder.etSample3.getText().toString();
        /*}*/
        varietyDataPojo.remarks = holder.etRemarks.getText().toString();

        if(n_replications.equals("1")){
            holder.etSample2.setVisibility(View.GONE);
            holder.etSample3.setVisibility(View.GONE);
        } else if(n_replications.equals("2")){
            holder.etSample3.setVisibility(View.GONE);
        }

        holder.etSample1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(".") && editable.toString().length() == 1){
                    holder.etSample1.setText("");
                    holder.etSample1.setSelection(holder.etSample1.getText().length());
                }
                if (editable.toString().length()>1) {
                    Double sample1 = Double.valueOf(editable.toString());
                    if (sample1 < 16) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample1);
                        varietyDataPojo.setSample1(sample1Str);
                    } else {
                        holder.etSample1.setError("Please enter value below 16");
                        holder.etSample1.setText("");
                    }
                }
            }
        });

        holder.etSample2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(".") && editable.toString().length() == 1){
                    holder.etSample2.setText("");
                    holder.etSample2.setSelection(holder.etSample2.getText().length());
                }
                if (editable.toString().length()>1) {
                    Double sample2 = Double.valueOf(editable.toString());
                    if (sample2 < 16) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample2);
                        varietyDataPojo.setSample2(sample1Str);
                    } else {
                        holder.etSample2.setError("Please enter value below 16");
                        holder.etSample2.setText("");
                    }
                }
            }
        });

        holder.etSample3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(".") && editable.toString().length() == 1){
                    holder.etSample3.setText("");
                    holder.etSample3.setSelection(holder.etSample3.getText().length());
                }
                if (editable.toString().length()>1) {
                    Double sample3 = Double.valueOf(editable.toString());
                    if (sample3 < 16) {
                        DecimalFormat decimalFormat = new DecimalFormat("##.###");
                        // Format the number
                        String sample1Str = decimalFormat.format(sample3);
                        varietyDataPojo.setSample3(sample1Str);
                    } else {
                        holder.etSample3.setError("Please enter value below 16");
                        holder.etSample3.setText("");
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVerityCode, /*tvVerityName,*/
                tvPurpose;
        EditText etSample1, etSample2, etSample3, etRemarks;

        RecyclerView rvTrialObservation;

        public ViewHolder(View itemView) {
            super(itemView);
            tvVerityCode = itemView.findViewById(R.id.tvVerityCode);
            /*tvVerityName = itemView.findViewById(R.id.tvVerityName);*/
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            etSample1 = itemView.findViewById(R.id.etSample1);
            etSample2 = itemView.findViewById(R.id.etSample2);
            etSample3 = itemView.findViewById(R.id.etSample3);
            etRemarks = itemView.findViewById(R.id.etRemarks);
            etSample1.setText("");
            etSample2.setText("");
            etSample3.setText("");
            etRemarks.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

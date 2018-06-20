package com.mrintoul.macguirerintoul_a2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Sensor> sensorDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout sensorLayout;
        public TextView sensorTextView;
        public int sensorType;
        Context context;

        public ViewHolder(View v) {
            super(v);
            sensorLayout = (LinearLayout) v;
            sensorTextView = v.findViewById(R.id.sensorTextView);
            v.setOnClickListener(this);
            context = v.getContext();
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), DataActivity.class);
            i.putExtra("SENSOR_TYPE", sensorType);
            v.getContext().startActivity(i);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Sensor> myDataset) {
        sensorDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sensorTextView.setText(sensorDataset.get(position).getName());
        holder.sensorType = sensorDataset.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return sensorDataset.size();
    }
}

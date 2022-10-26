package com.lopez.julz.readandbillhv.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lopez.julz.readandbillhv.R;
import com.lopez.julz.readandbillhv.ReadingConsoleActivity;
import com.lopez.julz.readandbillhv.dao.ReadingSchedules;
import com.lopez.julz.readandbillhv.helpers.AlertHelpers;
import com.lopez.julz.readandbillhv.helpers.ObjectHelpers;

import java.util.List;

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingListAdapter.ViewHolder>{

    public List<ReadingSchedules> readingSchedulesList;
    public Context context;
    public String userId;

    public ReadingListAdapter(List<ReadingSchedules> readingSchedulesList, Context context, String userId) {
        this.readingSchedulesList = readingSchedulesList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_layout_reading_schedules, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadingSchedules readingSchedule = readingSchedulesList.get(position);

        holder.bapaName.setText(readingSchedule.getBAPAName());
        holder.billingMonth.setText("Billing Month: " + ObjectHelpers.formatShortDate(readingSchedule.getServicePeriod()));

        if (readingSchedule.getDisabled() != null && readingSchedule.getDisabled().equals("Yes")) {
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertHelpers.showMessageDialog(context, "Prohibited", "You need to finish reading the last reading schedules first before you can proceed to this schedule.");
                }
            });
        } else {
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ReadingConsoleActivity.class);
                    intent.putExtra("USERID", userId);
                    intent.putExtra("BAPANAME", readingSchedule.getBAPAName());
                    intent.putExtra("SERVICEPERIOD", readingSchedule.getServicePeriod());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return readingSchedulesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public MaterialCardView parent;
        public TextView bapaName, billingMonth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
            bapaName = itemView.findViewById(R.id.bapaName);
            billingMonth = itemView.findViewById(R.id.billingMonth);
        }
    }
}

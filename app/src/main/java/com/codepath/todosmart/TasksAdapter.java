package com.codepath.todosmart;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by anjalik on 7/21/16.
 */
public class TasksAdapter extends ArrayAdapter<TaskRecord> {

        public TasksAdapter(Context context, ArrayList<TaskRecord> taskRecords) {
        super(context, 0, taskRecords);
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i("TaskAdapter", "get view called");
        // Get the data item for this position
            TaskRecord taskRecord = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }
        // Lookup view for data population
        TextView tvTask = (TextView) convertView.findViewById(R.id.tvTask);
        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
        // Populate the data into the template view using the data object
        tvTask.setText(taskRecord.taskName);
        if(taskRecord.priority.equals("HIGH"))
            tvPriority.setTextColor(Color.parseColor("#ff4d4d"));
        else  if(taskRecord.priority.equals("MED"))
            tvPriority.setTextColor(Color.parseColor("#ff9900"));
        else
            tvPriority.setTextColor(Color.parseColor("#00e6e6"));
        tvPriority.setText(taskRecord.priority);
        tvDueDate.setText(taskRecord.dueDate);
        // Return the completed view to render on screen
        return convertView;
    }
}


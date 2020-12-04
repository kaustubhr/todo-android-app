package com.kaustubh.todoapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<TaskInformation> {
    private Context mContext;
    int mResource;

    public TaskAdapter(Context context, int resource, ArrayList<TaskInformation> taskObjects) {
        super(context,resource, taskObjects);
        mContext = context;
        mResource = resource;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String task = getItem(position).getTask();
        String date = getItem(position).getDate();
        String time = getItem(position).getTime();
        String isAlarm = getItem(position).getIsAlarm();
        String day = getItem(position).getDay();
        //int pendingIntentNumber = getItem(position).getPendingIntentNumber();
        //TaskInformation taskInformation = new TaskInformation(task,time,isAlarm,date,day,pendingIntentNumber);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        if (position % 2 == 1) {


            convertView.setBackgroundResource(R.drawable.rounded_corners);


        } else {

            convertView.setBackgroundResource(R.drawable.rounded_corners_dark);

        }

        //taskName,taskTime,completeSwitch,TaskDay,TaskDate

        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        TextView taskTime = (TextView) convertView.findViewById(R.id.taskTime);
        TextView TaskDay = (TextView) convertView.findViewById(R.id.TaskDay);
        TextView TaskDate = (TextView) convertView.findViewById(R.id.TaskDate);
        SwitchCompat alarmSwitch = (SwitchCompat) convertView.findViewById(R.id.alarmSwitch);

        taskName.setText(task);
        taskTime.setText(time);
        TaskDay.setText(day);
        TaskDate.setText(date);
        if(isAlarm.equalsIgnoreCase("yes"))
        {
            alarmSwitch.setChecked(true);
        }
        else
        {
            alarmSwitch.setChecked(false);
        }

        return convertView;
    }
}

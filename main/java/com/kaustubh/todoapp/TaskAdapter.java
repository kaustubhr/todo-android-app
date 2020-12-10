package com.kaustubh.todoapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Path;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class TaskAdapter extends ArrayAdapter<TaskInformation> {
    private Context mContext;
    int mResource;
    FirebaseUser user;
    DatabaseReference mDatabase;
    //SwitchCompat alarmSwitch;

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



        Integer pendingIntentNumber = getItem(position).getPendingIntentNumber();
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

        taskName.setMovementMethod(new ScrollingMovementMethod());
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
            if(pendingIntentNumber == 0 || pendingIntentNumber == -1){
                alarmSwitch.setClickable(false);
            }
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + userID);



        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){


                    Random rand = new Random();

                    Integer pending = (Integer)rand.nextInt(Integer.MAX_VALUE-1);




                    mDatabase.orderByChild("task").equalTo(task).addListenerForSingleValueEvent(new ValueEventListener() {
                        Long milliSeconds=null;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot innerSnap:snapshot.getChildren()) {

                                milliSeconds = (Long) innerSnap.child("millis").getValue();

                               /* if((Long)innerSnap.child("pendingIntentNumber").getValue()==-1L) {
                                    alarmSwitch.setChecked(false);
                                    return;
                                }*/

                                innerSnap.getRef().child("pendingIntentNumber").setValue(pending);
                                innerSnap.getRef().child("isAlarm").setValue("yes");


                            }


                            setAlarm(task,pending,milliSeconds);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(!isChecked){
                    Toast.makeText(mContext,"Switch is OFF",Toast.LENGTH_SHORT).show();
                    mDatabase.orderByChild("task").equalTo(task).addListenerForSingleValueEvent(new ValueEventListener() {
                        Long milliSeconds=null;
                        Integer pending=0;
                        String isAlm="no";
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot innerSnap:snapshot.getChildren()){
                                pending=Integer.valueOf(innerSnap.child("pendingIntentNumber").getValue().toString());
                                milliSeconds=(Long)innerSnap.child("millis").getValue();
                                innerSnap.getRef().child("isAlarm").setValue(isAlm);

                            }
                            cancelAlarm(pending,milliSeconds);
                        }



                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });





        return convertView;
    }

    public void setAlarm(String t,int p,Long m) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext,MyNotifyService.class);
        intent.putExtra("title",t);
        Log.d("TaskAdapterSetAlarm",""+t);




        PendingIntent pendingIntent = PendingIntent.getService(mContext,p,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if(android.os.Build.VERSION.SDK_INT<= Build.VERSION_CODES.O)
            alarmManager.set(AlarmManager.RTC_WAKEUP,m,pendingIntent);

        else
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,m,pendingIntent);
        Log.d("alarmSetSwitchBtn",String.valueOf(m));
        Toast.makeText(mContext, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
    public void cancelAlarm(int pending,Long millis){
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext,MyNotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(mContext,pending,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(mContext, "Cancelled Alarm", Toast.LENGTH_SHORT).show();
    }

}

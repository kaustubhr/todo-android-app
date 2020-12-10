package com.kaustubh.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;


//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.initialization.InitializationStatus;
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class TodoTasks extends AppCompatActivity {
    ImageButton addTaskBtn;
    EditText addTaskField;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String timeString,dateString,dayString;
    TaskInformation taskInformation;
    String taskFromField;
    //TimeZone tz1 = TimeZone.getTimeZone("EST");
    Calendar calendar= Calendar.getInstance();
    //int YEAR;int MONTH;int DATE; int HOUR;
    //    int MINUTE;
    String DAY;
    Integer pendingIntentNumber;
    String isAlarm;

    ListView list ;
    ArrayList<TaskInformation> taskObject = new ArrayList();

    TaskAdapter adapter;
    //private AdView mAdView;
    String recentKey;
    public static final String CHANNEL_ID = "TodoTasksApp";
    public static final String CHANNEL_NAME = "Todo Tasks App";
    public static String TITLE;
    Vibrator vibe;








    void pushToDatabase() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + userID);
        //String dayofmonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        //String monthShort= calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        taskInformation = new TaskInformation(addTaskField.getText().toString(), timeString,isAlarm,dateString,DAY,pendingIntentNumber,calendar.getTimeInMillis());
        DatabaseReference temp = mDatabase;
        recentKey=temp.push().getKey();
        temp.child(recentKey).setValue(taskInformation);
        Log.d("mandtemp","mDatabase is "+mDatabase.toString()+" and temp is "+temp.toString());

        //Toast.makeText(TodoTasks.this, "end of function reached", Toast.LENGTH_SHORT).show();

        Toast.makeText(TodoTasks.this, addTaskField.getText(), Toast.LENGTH_LONG).show();
        addTaskField.setText("");

    }

    void displayTasks(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + userID);



        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String task="";
                String date="";
                String time ="";
                String isAlarm="";
                String day = "";
                Integer pending=0;
                Long m=null;

                for(DataSnapshot innerSnapshot : snapshot.getChildren()){
                    for(DataSnapshot innermost : innerSnapshot.getChildren())
                    {
                        if(innermost.getKey().toString().equalsIgnoreCase("task")){

                            //names.add(innermost.getValue().toString());
                        task= innermost.getValue().toString();
                        }

                        else if(innermost.getKey().toString().equalsIgnoreCase("date")){
                            date = innermost.getValue().toString();
                        }
                       else if(innermost.getKey().toString().equalsIgnoreCase("time")){
                            time = innermost.getValue().toString();
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("isAlarm")){
                            isAlarm = innermost.getValue().toString();
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("day"))
                        {
                            day = innermost.getValue().toString();
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("pending")){
                            pending = (Integer) innermost.getValue();
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("calendar")){
                            m = (Long)innermost.getValue();
                        }

                    }
                    Log.d("Dravid", time.toString() + " " + date.toString() + " " + task + " " + isAlarm);
                    taskObject.add(0,new TaskInformation(task,time,isAlarm,date,day,pending,m));



                }
                //Log.d("namesTag",taskObject.toString());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    void displayAddedTask() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DatabaseReference tempRef = mDatabase;

        if (!recentKey.isEmpty()) {
            tempRef = FirebaseDatabase.getInstance().getReference("users/" + userID + "/" +recentKey);

            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    String task = "";
                    String date = "";
                    String time = "";
                    String isAlarm = "";
                    String day = "";
                    Integer pending=0;
                    Long m = null;


                    for (DataSnapshot innermost : snapshot.getChildren()) {
                        if (innermost.getKey().toString().equalsIgnoreCase("task")) {
                            //names.add(innermost.getValue().toString());
                            task = innermost.getValue().toString();
                        } else if (innermost.getKey().toString().equalsIgnoreCase("date")) {
                            date = innermost.getValue().toString();
                        } else if (innermost.getKey().toString().equalsIgnoreCase("time")) {
                            time = innermost.getValue().toString();
                        } else if (innermost.getKey().toString().equalsIgnoreCase("isAlarm")) {
                            isAlarm = innermost.getValue().toString();
                        } else if (innermost.getKey().toString().equalsIgnoreCase("day")) {
                            day = innermost.getValue().toString();
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("pendingIntentNumber")){
                            pending =  Integer.valueOf(innermost.getValue().toString());
                        }
                        else if(innermost.getKey().toString().equalsIgnoreCase("calendar")){
                            m = (Long)innermost.getValue();
                        }

                    }
                    Log.d("Kumble", time + " " + date + " " + task + " " + isAlarm+String.valueOf(pending));
                    taskObject.add(0,new TaskInformation(task, time, isAlarm, date, day,pending,m));
                    adapter.notifyDataSetChanged();

                }
                //Log.d("namesTag",taskObject.toString());


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        Log.d("displayAddedDataRef","mdatabase is "+mDatabase+" and tempRef is"+tempRef);
    }


    /*@Override
    protected void onStart() {
        super.onStart();
        list.setOnItemLongClickListener(abc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        list.setOnItemLongClickListener(abc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.setOnItemLongClickListener(abc);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        list.setOnItemLongClickListener(abc);
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_tasks);
        addTaskBtn = (ImageButton) findViewById(R.id.addTasksBtn);
        addTaskField = findViewById(R.id.addTaskField);
        list = (ListView) findViewById(R.id.listView);
        recentKey="";
        isAlarm="";
        DAY="";
        pendingIntentNumber=null;
        //calendar=null;
        dayString="";
        timeString="";
        vibe = (Vibrator) TodoTasks.this.getSystemService(Context.VIBRATOR_SERVICE);

        Calendar calForPicker = Calendar.getInstance();
        int YEAR= calForPicker.get(Calendar.YEAR);
        int MONTH= calForPicker.get(Calendar.MONTH);
        int DATE=calForPicker.get(Calendar.DAY_OF_MONTH);
        //DAY = calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.SHORT, Locale.getDefault());
        int HOUR= calForPicker.get(Calendar.HOUR);
        int MINUTE = calForPicker.get(Calendar.MINUTE);
        Log.d("tempCal",String.valueOf(YEAR) + String.valueOf(MONTH) + String.valueOf(DATE) + String.valueOf(HOUR) + String.valueOf(MINUTE));
        //taskFromField = addTaskField.toString();

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)

        mAdView.loadAd(adRequest);
        */



        displayTasks();
        adapter = new TaskAdapter(this, R.layout.list_item_layout,taskObject);


        list.setAdapter(adapter);

        list.setOnItemLongClickListener(abc);

        //code for keyboard in huawei
       /* addTaskField.requestFocus();


        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(0, 0);*/








        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                //hiding keyboard after clicking + button
               addTaskField.onEditorAction(EditorInfo.IME_ACTION_DONE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addTaskField.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if(addTaskField.getText().toString().isEmpty())
                    return;
               // addTaskField.setText("");


                AlertDialog.Builder  alertForReminder = new AlertDialog.Builder(TodoTasks.this);
                alertForReminder.setMessage("Do you want to set Date and Time for this Task?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(TodoTasks.this,"Date-Time to be Set for this Task",Toast.LENGTH_SHORT).show();
                                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                                        Date tempDate = new Date(year-1900,month,dayOfMonth);


                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E",Locale.US);
                                        DAY = simpleDateFormat.format(tempDate);

                                        calendar.set(Calendar.YEAR,year);
                                        calendar.set(Calendar.MONTH,month);
                                        calendar.set(Calendar.DATE,dayOfMonth);

                                        Log.d("cow", " inside tempDate "+tempDate);
                                        String monthInWord = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
                                        dateString= String.valueOf(calendar.get(Calendar.DATE)) +" " + monthInWord ;
                                        Toast.makeText(TodoTasks.this,"reminder set on "+dateString,Toast.LENGTH_LONG).show();
                                        Log.d("toastMsg",dateString);

                                        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),2, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                //Toast.makeText(TodoTasks.this,"reminder time set on "+ timeString,Toast.LENGTH_LONG).show();

                                                //code to set alarm
                                                //Calendar c = Calendar.getInstance();
                                                Log.d("hourofday",""+hourOfDay);

                                                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay); calendar.set(Calendar.MINUTE,minute);calendar.set(Calendar.SECOND,0);
                                                TITLE=addTaskField.getText().toString();
                                                String ampm="AM";
                                                /*if(hourOfDay>=12){
                                                    calendar.set(Calendar.AM_PM,Calendar.PM);
                                                    ampm = "PM";
                                                }
                                                else
                                                    calendar.set(Calendar.AM_PM,Calendar.AM);
                                                    */
                                                 if(hourOfDay>=12)
                                                     ampm="PM";
                                                Log.d("calendarTime",""+calendar.get(Calendar.HOUR_OF_DAY));



                                                    if(calendar.get(Calendar.MINUTE)<10)
                                                        timeString = calendar.get(Calendar.HOUR) + ":" + "0"+calendar.get(Calendar.MINUTE)+" "+ ampm;
                                                    else
                                                        timeString = calendar.get(Calendar.HOUR) + ":" +calendar.get(Calendar.MINUTE)+" "+ ampm;

                                                if(calendar.getTimeInMillis() + 900000 < Calendar.getInstance().getTimeInMillis() )
                                                {
                                                    isAlarm = "no";
                                                    pendingIntentNumber = -1;
                                                }
                                                else{
                                                    isAlarm="yes";
                                                    startAlarm(calendar);
                                                }




                                               // startAlarm(calendar);

                                               //call pushToDatabase
                                                pushToDatabase();

                                                //call display here itself??
                                                displayAddedTask();





                                            }
                                        },HOUR,MINUTE,false);
                                        timePickerDialog.setTitle("Set Reminder Time");

                                        timePickerDialog.show();



                                    }
                                },YEAR,MONTH,DATE);
                                datePickerDialog.setTitle("Set Reminder Date");
                                datePickerDialog.show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //just add to database with default values for date and time
                                dateString = "";
                                timeString = "";
                                dayString="";
                                DAY = "";
                                isAlarm="no";
                                pendingIntentNumber=0;
                                calendar.setTimeInMillis(0L);

                                //take care of empty calendar too here
                                pushToDatabase();
                                Toast.makeText(TodoTasks.this,"No Reminder Set for this Task",Toast.LENGTH_SHORT).show();
                                //call display task here itself?
                                displayAddedTask();
                            }
                        });


                AlertDialog alert = alertForReminder.create();
                alertForReminder.setTitle("Reminder Option");
                alertForReminder.show();



            }
        });






    }

    public void startAlarm(Calendar c) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this,MyNotifyService.class);
            intent.putExtra("title",TITLE);
            Log.d("requestCodeRandom",""+TITLE);
            Random rand = new Random();
            Integer requestCodeRandom = (Integer)rand.nextInt(Integer.MAX_VALUE-1);
            pendingIntentNumber = requestCodeRandom;


            PendingIntent pendingIntent = PendingIntent.getService(this,requestCodeRandom,intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if(android.os.Build.VERSION.SDK_INT<= Build.VERSION_CODES.O)
                alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);

            else
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
            //this.startService(intent);
            Log.d("alarmset",String.valueOf(c));

    }

    public void cancelAlarm(int pending,Long millis){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,MyNotifyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,pending,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
       // Toast.makeText(this, "Cancelled Alarm", Toast.LENGTH_SHORT).show();
    }



   public  AdapterView.OnItemLongClickListener abc = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //taskObject.remove(position);
                //adapter.notifyDataSetChanged();
                vibe.vibrate(80);
               new AlertDialog.Builder(TodoTasks.this)
                       .setIcon(R.drawable.trashbtn_foreground)
                       .setTitle("Do you want to delete this task?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               String taskNameToDelete = taskObject.get(position).getTask();
                               Toast.makeText(TodoTasks.this, taskNameToDelete+" supposed to be deleted", Toast.LENGTH_SHORT).show();
                               Log.d("magane",""+mDatabase.orderByChild("task").equalTo(taskNameToDelete).getRef());

                               mDatabase.orderByChild("task").equalTo(taskNameToDelete).addListenerForSingleValueEvent(new ValueEventListener() {
                                   Long miSeconds = null;
                                   Integer pendForCancel = 0;

                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       for (DataSnapshot innerSnap : snapshot.getChildren()) {
                                           pendForCancel = Integer.valueOf(innerSnap.child("pendingIntentNumber").getValue().toString());
                                           miSeconds = (Long) innerSnap.child("millis").getValue();
                                           Log.d("WHYU",""+innerSnap.getRef());
                                           innerSnap.getRef().removeValue();
                                           Toast.makeText(TodoTasks.this, taskNameToDelete+" Task Deleted", Toast.LENGTH_SHORT).show();
                                           if(pendForCancel!=0 && miSeconds!=0L)
                                               cancelAlarm(pendForCancel, miSeconds);
                                           taskObject.remove(position);
                                           adapter.notifyDataSetChanged();
                                           //mDatabase.orderByChild("task").equalTo(taskNameToDelete).removeEventListener(this);


                                       }


                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       Toast.makeText(TodoTasks.this, "Error in removing task", Toast.LENGTH_SHORT).show();

                                   }
                               });//end of mDatabase



                           }
                       })

                       .setNegativeButton("No",null)
                       .show();

                return true;
            }
        };

    public void onBackPressed() {

        AlertDialog.Builder  alertForQuit = new AlertDialog.Builder(TodoTasks.this);
        alertForQuit.setTitle("Quitting App")
                .setMessage("Do you want to exit?")
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alertForQuit.show();
    }
}
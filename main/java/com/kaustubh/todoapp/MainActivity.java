package com.kaustubh.todoapp;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.initialization.InitializationStatus;
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText mEmail,mPassword;
    Button mRegisterBtn,mSignInBtn;
    //private AdView mAdView;
    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = (Button)findViewById(R.id.createBtn);
        mSignInBtn = (Button)findViewById(R.id.sign_in_btn);

        //code for saving login details
        sp = getSharedPreferences("login",MODE_PRIVATE);
        if(sp.getBoolean("logged",false)){
            goTodoTasksActivity();
        }




        //google banner ad code
        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)

        
        mAdView.loadAd(adRequest);
        */



        //sign up button listener
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length()<6){
                    mPassword.setError("Password must have 6 or more characters");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this,"User created",Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(MainActivity.this,TodoTasks.class);
                                    startActivity(homeIntent);

                                } else {
                                    Toast.makeText(MainActivity.this,"Error! "+task.getException(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length()<6){
                    mPassword.setError("Password must have 6 or more characters. Please retype password");

                    return;
                }

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this,"User Signed-In successfully",Toast.LENGTH_SHORT).show();
                                    mPassword.setText("");
                                    mEmail.setText("");
                                    sp.edit().putBoolean("logged",true).apply();
                                    Intent homeIntent = new Intent(MainActivity.this,TodoTasks.class);
                                    startActivity(homeIntent);


                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Error! "+task.getException(),Toast.LENGTH_SHORT).show();
                                    mPassword.setText("");
                                    mEmail.setText("");

                                }

                            }
                        });

            }
        });


    }
    public void goTodoTasksActivity(){
        Intent homeIntent = new Intent(MainActivity.this,TodoTasks.class);
        startActivity(homeIntent);
    }


}
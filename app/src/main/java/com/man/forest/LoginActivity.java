package com.man.forest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText mobilenumber;
    private Button requestotp;
SessionManager sessionManager;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context=LoginActivity.this;
        sessionManager=new SessionManager(context);
        mobilenumber=findViewById(R.id.mobilenumber);
        requestotp=findViewById(R.id.request_otp_button);
        FirebaseMessaging.getInstance().subscribeToTopic("weather");

        if (!TextUtils.isEmpty(sessionManager.getstring("status"))){
            if (sessionManager.getstring("status").equals("login")){
                Intent intent=new Intent(context,DashboardActivity.class);
                startActivity(intent);
            }
        }



        requestotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mobilenumber.getText().toString())&&mobilenumber.getText().length()==10) {
                    Intent intent = new Intent(context, OtpVerificationActivity.class);
                    String num = mobilenumber.getText().toString();
                    intent.putExtra("mobilenumber",num );
                    startActivity(intent);
                }else {
                    mobilenumber.setError("Enter Mobile Number");
                }
            }
        });


    }
}

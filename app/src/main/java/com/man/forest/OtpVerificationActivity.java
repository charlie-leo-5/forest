package com.man.forest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otptext;
    private Button submit;
    TextView resend;


    private Context context;
    String mobilenumber ,verifycode;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken OtpResentToken;



    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpverification_activity);

        mobilenumber = getIntent().getStringExtra("mobilenumber");
        context = OtpVerificationActivity.this;
        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager=new SessionManager(context);
        otptext=findViewById(R.id.otptext);
        submit=findViewById(R.id.verify);
        resend=findViewById(R.id.resend_token);
        merchantOtpVerificationSendOtpMethod(mobilenumber);



        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                merchantOtpVerificationRequestOTP(mobilenumber, OtpResentToken);

                otptext.setText("");

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                String otpVerificationCode = otptext.getText().toString().trim();

                if (otpVerificationCode.isEmpty() || otpVerificationCode.equals(null) || otpVerificationCode.length() < 6) {

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    otptext.setError("Enter a valid OTP");
                    otptext.requestFocus();
                    return;

                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                merchantVerifyVerificationCode(otpVerificationCode);


            }
        });




    }

    private void merchantOtpVerificationRequestOTP(String mMerchantOtpVerificationMobileNumber, PhoneAuthProvider.ForceResendingToken mMerchantOtpResentToken) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mMerchantOtpVerificationMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                merchantOtpVerificationCallBacks,
                mMerchantOtpResentToken);

    }

    private void merchantOtpVerificationSendOtpMethod(String mMerchantOtpVerificationMobileNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mMerchantOtpVerificationMobileNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                merchantOtpVerificationCallBacks);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks merchantOtpVerificationCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            signInWithPhoneAuthCredential(phoneAuthCredential);
//            codestates.setText("Verfication Success");
//            String merchantOtpVerificationCode = phoneAuthCredential.getSmsCode();
//
//            if (merchantOtpVerificationCode != null) {
//
//                mMerchantOtpVerificationOTPCodeEditText.setText(merchantOtpVerificationCode);
//
//                merchantVerifyVerificationCode(merchantOtpVerificationCode);
//
//            }
        }



        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

//            codestates.setText("Verfication Failed");
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verifycode = s;
            OtpResentToken = forceResendingToken;

//            codestates.setText(" Code Sended");
        }
    };


    private void merchantVerifyVerificationCode(String merchantOTP) {

//        mMerchantOtpVerificationProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        PhoneAuthCredential merchantOtpVerificationCredentials = PhoneAuthProvider.getCredential(verifycode, merchantOTP);

        signInWithPhoneAuthCredential(merchantOtpVerificationCredentials);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential merchantOtpVerificationCredentials) {



        firebaseAuth.signInWithCredential(merchantOtpVerificationCredentials)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            sessionManager.setstring("status","login");
                            Intent intent = new Intent(context,DashboardActivity.class);
                            startActivity(intent);


                        }
                    }
                });
    }

}

package com.man.forest;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {


    Button startvideo,logout;
    Context context;
    private String  FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
            "key=" + "AAAAFvrc5-g:APA91bHdZfWDQLcYAArp9i1Z8mJJpdJhK2Y7qrPZ0kmkk4TCEcPusTZqb_V0stntO1CV2NKEQrec_8L6sfRVCe1lyCrBlYEkWoLbblWG0RBWhT8GvTRj9txYCGDcKVhS9Fr96fUR52ei";
    private String contentType = "application/json";
    private Object object;
    RequestQueue queue;
    SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        context=DashboardActivity.this;
        startvideo=findViewById(R.id.start_video);
        logout=findViewById(R.id.logout);
        sessionManager=new SessionManager(context);

        String topic = "new";

        List<String> registrationTokens = Arrays.asList(
                "ebhu-kkPaMI:APA91bFNAebJxGZmuZHzlpkzMp5KXrDvVwtF2AJNhg4kDE42v3BsYiSgmIWiz8t_9vrAAD6ET9Ncjwau_BpPOLPfQVvAhlQpJn81F32PBJVaS79kAx9vxv-cmibZf4t_v9ZPxY5jU1gE",
                "ebhu-kkPaMI:APA91bFNAebJxGZmuZHzlpkzMp5KXrDvVwtF2AJNhg4kDE42v3BsYiSgmIWiz8t_9vrAAD6ET9Ncjwau_BpPOLPfQVvAhlQpJn81F32PBJVaS79kAx9vxv-cmibZf4t_v9ZPxY5jU1gE"

        );
         queue = Volley.newRequestQueue(context);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString("message", token);
                        Log.d("TAG", token);
//                        Toast.makeText(DashboardActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("hero")
//        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        /*String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);*/
                        Log.d("ddd", "onComplete: Success");
//                        Toast.makeText(DashboardActivity.this, "successs", Toast.LENGTH_SHORT).show();
                    }
                });



        logout.setOnClickListener(new View.OnClickListener() {
//            @lombok.SneakyThrows
            @Override
            public void onClick(View v) {
              /*  String topic = "/topics/hero";

                JSONObject notification = new JSONObject();
                JSONObject notificationbody = new JSONObject();
                try {
                    notificationbody.put("title", "Enter_title");
                    notificationbody.put("message", "herer is there");   //Enter your notification message
                    notification.put("to", topic);
                    notification.put("data", notificationbody);

                    Log.d("ddd", "notifica: comes in ");


                }catch (Exception e ){

                }

                sendNotification(notification);*/
                sessionManager.setstring("status","logout");
                Intent intent=new Intent(context,LoginActivity.class);
                startActivity(intent);

            }
        });


startvideo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context,MainActivity.class);
        startActivity(intent);
    }
});

    }


    public void sendNotification(JSONObject notification){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("ddd", "onResponse: " +response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("dsdsd", "onErrorResponse: "+error);
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String ,String > params = new HashMap<>();
                params.put("Authorization",serverKey);
                params.put("Content-Type",contentType);
                return params;
            }
        };

        queue.add(jsonObjectRequest);
    }
}

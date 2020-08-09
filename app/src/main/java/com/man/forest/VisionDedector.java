package com.man.forest;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

import static android.content.ContentValues.TAG;

public class VisionDedector extends AsyncTask<InputStream,String,String> {

    private final String API_KEY="e1edc224df6f489791c4bd342cf307dc";
    private final String API_LINK ="https://rakshan.cognitiveservices.azure.com/vision/v1.0";
    ArrayList<String> responselist = new ArrayList<>();
    private String  FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =
            "key=" + "AAAAFvrc5-g:APA91bHdZfWDQLcYAArp9i1Z8mJJpdJhK2Y7qrPZ0kmkk4TCEcPusTZqb_V0stntO1CV2NKEQrec_8L6sfRVCe1lyCrBlYEkWoLbblWG0RBWhT8GvTRj9txYCGDcKVhS9Fr96fUR52ei";
    private String contentType = "application/json";
        Context context;
    VisionServiceClient visionServiceClient=new VisionServiceRestClient(API_KEY,API_LINK);
    RequestQueue queue;

    public VisionDedector(MainActivity mainActivity) {
        this.context=mainActivity;
        queue = Volley.newRequestQueue(context);
    }

    @Override
    protected String doInBackground(InputStream... inputStreams) {
        try {


            String[] features = {"description"};
            String[] details = {};
            AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0], features, details);

            String resulttb=new Gson().toJson(result);
            return resulttb;
        }catch (Exception e){

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (TextUtils.isEmpty(s)){

        }else {
            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
            StringBuilder stringBuilder = new StringBuilder();
            String value = "";
            for (Caption caption : result.description.captions) {
                stringBuilder.append(caption.text);

                responselist.add(stringBuilder.toString());
                 value=stringBuilder.toString();


                Log.d(TAG, "onPostExecute: "+responselist.toString());
            }

            boolean human = false;
            boolean animal = false;

            if(value.contains("human being")||value.contains("person")||value.contains("boy")||value.contains("male")||value.contains("men")||value.contains("man")||value.contains("girl")||value.contains("woman")||value.contains("female")||value.contains("lady")||value.contains("people")) {
//                Toast.makeText(context, "The Frame Contain the Person...", Toast.LENGTH_SHORT).show();
                String message ="Warrning.....!! Dedected Human...";
                String title = "Humans Alert";
                senddata(title,message);
                human=true;

            }
//            Toast.makeText(context,value, Toast.LENGTH_SHORT).show();

            if (value.contains("animal")||value.contains("dog")||value.contains("cat")||value.contains("buffalo")||value.contains("lion")||value.contains("tiger")||value.contains("cow")){
//                Toast.makeText(context, "The Frame Contain the animal...", Toast.LENGTH_SHORT).show();
               /* String message ="Warrning.....!! Dedected Animal...";
                String title = "Animals Alert";
                senddata(title,message);*/
                animal=true;
            }

            if (human&&animal){
                String message ="Need Help.....!! Dedected Animal & Human...";
                String title = "Danger Alert";
                senddata(title,message);
            }

        }
    }



    public void senddata(String title,String message){
//        String topic = "/topics/hero";
        String topic = "/topics/weather";

        JSONObject notification = new JSONObject();
        JSONObject notificationbody = new JSONObject();
        try {
            notificationbody.put("title", title);
            notificationbody.put("message", message);   //Enter your notification message
            notification.put("to", topic);
            notification.put("data", notificationbody);

            Log.d("ddd", "notifica: comes in ");


        }catch (Exception e ){

        }

        sendNotification(notification);

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

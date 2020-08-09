package com.man.forest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
ImageView imageView;
TextView textView;
Button button;
private final String API_KEY="e1edc224df6f489791c4bd342cf307dc";
private final String API_LINK ="https://rakshan.cognitiveservices.azure.com/vision/v1.0";
int i=0;

private DatabaseReference databaseReference;

ArrayList<String> responselist = new ArrayList<>();

Bitmap currentBitmap;
VisionServiceClient visionServiceClient=new VisionServiceRestClient(API_KEY,API_LINK);

        JavaCameraView cameraBridgeViewBase;
        BaseLoaderCallback baseLoaderCallback;
        int counter = 0;
        Mat mRGBA,mRGBAT;
Button stopplayer;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            stopplayer=findViewById(R.id.stop_btn);
            cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
            cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
            cameraBridgeViewBase.setCvCameraViewListener(this);

            databaseReference = FirebaseDatabase.getInstance().getReference();

//            databaseReference.child("user").setValue("manoj");

            stopplayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraBridgeViewBase.disableView();
                    Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                    startActivity(intent);

                }
            });

            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            baseLoaderCallback = new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(int status) {
                    super.onManagerConnected(status);

                    switch(status){

                        case BaseLoaderCallback.SUCCESS:
                            cameraBridgeViewBase.enableView();
                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }


                }

            };




        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

           /* Mat frame = inputFrame.rgba();

            if (counter % 2 == 0){

                Core.flip(frame, frame, 1);
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);


            }

            counter = counter + 1;
            return null;*/

           mRGBA=inputFrame.rgba();
         mRGBAT=mRGBA.t();
         Core.flip(mRGBA.t(),mRGBAT,1);
         Imgproc.resize(mRGBAT,mRGBAT,mRGBA.size());

            Mat grayMat = new Mat();
            Mat blur1 = new Mat();
            Mat blur2 = new Mat();

            //Converting the image to grayscale
            Imgproc.cvtColor(mRGBA, grayMat, Imgproc.COLORMAP_SPRING);
//
            Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
            Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);

            //Subtracting the two blurred images
            Mat DoG = new Mat();
            Core.absdiff(blur1, blur2, DoG);

            //Inverse Binary Thresholding
//            Core.multiply(DoG, new Scalar(100), DoG);
//            Imgproc.threshold(DoG, DoG, 50, 255, Imgproc.THRESH_BINARY);
            final Bitmap currentBitmap = Bitmap.createBitmap(mRGBAT.cols(), mRGBAT.rows(), Bitmap.Config.RGB_565);
            //Converting Mat back to Bitmap
            Utils.matToBitmap(mRGBA, currentBitmap);

//            imageView.setImageBitmap(currentBitmap);


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.JPEG,70,outputStream);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());



            /*final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];

            Bitmap bmp = BitmapFactory.decodeFile(mRGBA,options);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, 960, 730, false);*/


//            if (visiontest.getStatus()==AsyncTask.Status.PENDING){
////                visiontest.execute(inputStream);
////                new visiontest().execute(inputStream);
//
//                new VisionDedector(MainActivity.this).execute(inputStream);
//
//            }else if (visiontest.getStatus()==AsyncTask.Status.FINISHED){
//                visiontest.execute(inputStream);
                new VisionDedector(MainActivity.this).execute(inputStream);
//                visiontest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,inputStream);
//            }




//            if (visiontest.getStatus()==AsyncTask.Status.FINISHED){
//
//                visiontest.execute(inputStream);
//            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    imageView.setImageBitmap(currentBitmap);
                    // Stuff that updates the UI
//                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                    Bitmap bitmap = drawable.getBitmap();
//        storebitmap(bitmap);
                }
            });



         return mRGBAT;
        }


        @Override
        public void onCameraViewStarted(int width, int height) {

            mRGBA=new Mat(height,width, CvType.CV_8UC4);

        }


        @Override
        public void onCameraViewStopped() {
            mRGBA.release();
        }


    @Override
    public void onBackPressed() {

        cameraBridgeViewBase.disableView();
        /*Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
        startActivity(intent);*/
    }

    public void storebitmap(Bitmap bitmapImage){

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/camtest");
            dir.mkdirs();

            String fileName = String.format(i+".jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            i++;


            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            return directory.getAbsolutePath();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


            if (OpenCVLoader.initDebug()){
                Log.d("dddd", "static initializer: Opencv is working " );

                baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
            }else {
                Log.d("dddd", " : is not working ");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,baseLoaderCallback);
            }


    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    AsyncTask<InputStream,String ,String > visiontest = new AsyncTask<InputStream, String, String>() {



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
                for (Caption caption : result.description.captions) {
                    stringBuilder.append(caption.text);

                    responselist.add(stringBuilder.toString());

                    Toast.makeText(MainActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


}
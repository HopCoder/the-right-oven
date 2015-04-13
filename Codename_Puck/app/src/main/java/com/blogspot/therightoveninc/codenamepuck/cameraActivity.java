package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import java.io.ByteArrayOutputStream;

/**
 * Created by Timothy D. Mahon on 2/28/2015.
 */
public class cameraActivity extends Activity{
    private Camera theCamera;
    private cameraView theCameraView;
    private FrameLayout cameraLayout;
    private boolean cameraBusy = false;
    private byte[] photoPreview, croppedPhoto;
    private int upperHeight;
    private String postUrl = phoneSettings.postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


    }
    @Override
    protected void onPause(){
        super.onPause();
        releaseCamera();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.camera_view);
        if (!checkCameraHardware(this)){
            return;
        }
        theCamera = getCameraInstance();
        if (theCamera == null){
            Log.d("CameraActivity:", "Error null camera!");
            return;
        }
        theCameraView = new cameraView(this, theCamera);
        cameraLayout= (FrameLayout) findViewById(R.id.camera_preview);
        cameraLayout.addView(theCameraView);
    }

    // ensure views are accessed after being loaded
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        setOverlay();
    }

    private void setOverlay()
    {
        // get navigation bar height
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int navigationBarHeight = resources.getDimensionPixelSize(resourceId);

        // get status bar height
        int resourceId2 = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = resources.getDimensionPixelSize(resourceId2);

        ImageView upper = (ImageView) findViewById(R.id.upper_overlay);
        TextView lower = (TextView) findViewById(R.id.lower_overlay);

        // convert dp to px
        upperHeight = (int)math.convertDpToPixel(upper.getLayoutParams().height);

        lower.getLayoutParams().height = phoneSettings.yPixels
                - upperHeight
                - phoneSettings.xPixels
                + navigationBarHeight
                + statusBarHeight;

        // set button locations
        Button captureButton = (Button) findViewById(R.id.button_capture);
        RelativeLayout.LayoutParams captureParams = (RelativeLayout.LayoutParams)captureButton.getLayoutParams();
        captureParams.setMargins(0,0,0,navigationBarHeight);
        captureButton.setLayoutParams(captureParams);

        Button uploadButton = (Button) findViewById(R.id.button_upload);
        RelativeLayout.LayoutParams uploadParams = (RelativeLayout.LayoutParams) uploadButton.getLayoutParams();
        uploadParams.setMargins(0,0,0,navigationBarHeight);
        uploadButton.setLayoutParams(uploadParams);

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    private void releaseCamera(){
        if (theCamera != null){
            theCamera.stopPreview();
            theCamera.release();        // release the camera for other applications
            theCamera = null;
        }
    }

    public void onCaptureClick(View v) {
        if (!cameraBusy) {
            theCamera.takePicture(null, null, mPicture);
            cameraBusy = true;
        }
    }

    public void onUploadClick(View v)
    {
        if (cameraBusy)
        {
            //the size of the array is the dimensions of the sub-photo
          //  int[] pixels = new int[phoneSettings.xPixels*phoneSettings.xPixels];
  //          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          //  Bitmap bitmap = BitmapFactory.decodeByteArray(photoPreview, 0, photoPreview.length);

            // (int[] pixels, int offset, int stride, int x, int y, int width, int height)
        //    bitmap.getPixels(pixels, 0, phoneSettings.xPixels, 0, upperHeight+1, phoneSettings.xPixels, phoneSettings.xPixels);

            //ARGB_8888 is a good quality configuration
       //     bitmap = Bitmap.createBitmap(pixels, 0, 100, 100, 100, Bitmap.Config.ARGB_8888);

            //100 is the best quality possible
//            bitmap.compress(Bitmap.CompressFormat.PNG, 1, bos);

//            croppedPhoto = bos.toByteArray();

            croppedPhoto = photoPreview;
            photoPreview = null;

            // TODO : send croppedPhoto to server
            new UploadPhotoAsyncTask().execute();

            cameraBusy = false;
            theCamera.startPreview();
        }
    }

    private class UploadPhotoAsyncTask extends AsyncTask<URL, Void, Void>
    {
        protected Void doInBackground(URL... urls) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postUrl);

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, phoneSettings.cookieStore);

            httpPost.addHeader("csrftoken", phoneSettings.cfsr);
            httpPost.addHeader("csrfmiddlewaretoken", phoneSettings.cfsr);

            String param = "csrfmiddlewaretoken=";
            param = param.concat(phoneSettings.cookieStore.getCookies().get(0).getValue());
            param = param.concat("&");
            Log.e("qwer",param);
            byte[] postData = param.getBytes(Charset.forName("UTF-8"));

            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(croppedPhoto.length + param.length());
            byteArrayBuffer.append(postData, 0, postData.length);
            byteArrayBuffer.append(croppedPhoto, 0, croppedPhoto.length);

            Log.e("zxcv", byteArrayBuffer.toByteArray().toString());

            httpPost.setEntity(new ByteArrayEntity(byteArrayBuffer.toByteArray()));
            try {
                HttpResponse response = httpClient.execute(httpPost, localContext);
                String result = EntityUtils.toString(response.getEntity());
                Log.e("yyy", phoneSettings.cookieStore.toString());
                int x = 0;
                for (x =0; x< result.length()/500; x = x+1)
                {
                    Log.e("o",result.substring(x*500,(x+1)*500));
                }
                Log.e("o", result.substring(x*500));
            }
            catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            // free memory here
            croppedPhoto = null;
            byteArrayBuffer = null;

            return null;
        }
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            photoPreview = data;
            //data = null;

            if(data != null) {
                return;
            }

            File pictureFile = getOutputMediaFile(ImageFormat.JPEG);
            if (pictureFile == null){
                Log.d("a", "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                Log.e("q","hi");
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                Log.e("q","bye");
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("e", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("e", "Error accessing file: " + e.getMessage());
            }

            return;
        }
    };

    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");


        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == ImageFormat.JPEG){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            Log.e("a", mediaFile.toString());
        } else {
            return null;
        }

        return mediaFile;
    }
}

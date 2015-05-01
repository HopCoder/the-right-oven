package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Timothy D. Mahon on 2/28/2015.
 * Here is the camera activity class responsible for creating the camera view class (which actually preivews the camera to the user).
 */
public class cameraActivity extends Activity{
    private Camera theCamera; //Camera object for taking photos.
    private cameraView theCameraView; //the preview of the camera
    private FrameLayout cameraLayout; //The layout of the preview
    private boolean cameraBusy = false; //A boolean to block the camera as it takes photos.
    private byte[] photoPreview, croppedPhoto; //Image byte arrays. Containers for recieving and sending photos.
    private int upperHeight; //A parameter to help fit the preview better
    private String postUrl = phoneSettings.postUrl; //The url utilized in the post repsonse to the server

    //The activities create method responsible for grabbing the activie display and creating the camera preview.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    //On pause method, responsible for releasing the camera so other processes can use it while the application is paused.
    @Override
    protected void onPause(){
        super.onPause();
        releaseCamera();
    }

    //Destructor called when the camera activity ends.
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //Resume method. Grabs the camera and starts it. Presents the preview to the user.
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

    //Method for setting the puck GUI overlay ontop of the camera preview.
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
            theCamera.takePicture(shutter, null, mPicture);
            cameraBusy = true;
        }
    }

    //The upload method. On a click to upload button this starts an Asyncronous task to send the newly created photo
    //To the puck server.
    public void onUploadClick(View v)
    {
        if (cameraBusy)
        {
            croppedPhoto = photoPreview;
            photoPreview = null;

            // TODO : send croppedPhoto to server
            new UploadPhotoAsyncTask().execute();

            cameraBusy = false;
            theCamera.startPreview();
        }
    }
    //A Aysncronous Task created to send photos to the puck server.
    private class UploadPhotoAsyncTask extends AsyncTask<URL, Void, Void>
    {
        protected Void doInBackground(URL... urls) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postUrl);

            if (croppedPhoto == null){
                return null;
            }
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(croppedPhoto.length);
            byteArrayBuffer.append(croppedPhoto, 0, croppedPhoto.length);

            httpPost.setEntity(new ByteArrayEntity(croppedPhoto));
            try {
                HttpResponse response = httpClient.execute(httpPost);
                String result = EntityUtils.toString(response.getEntity());
//                Log.e("yyy", phoneSettings.cookieStore.toString());
                int x = 0;
                for (; x< result.length()/500; x = x+1)
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

    //A shutter callback method created to introduce sound into the application upon capturing a picture.
    Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    //The actual picture taking callback responsible for storing the photo int oa byte array to be sent to the
    //Puck server.
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
            return;
        }
    };

    //method to obtain outside storage space to save the photo.
    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Puck");

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

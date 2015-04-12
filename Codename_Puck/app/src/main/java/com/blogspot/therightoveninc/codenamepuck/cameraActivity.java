package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;

/**
 * Created by Timothy D. Mahon on 2/28/2015.
 */
public class cameraActivity extends Activity{
    private Camera theCamera;
    private cameraView theCameraView;
    private FrameLayout cameraLayout;
    private boolean cameraBusy = false;
    private byte[] photoPreview;
    private int upperHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ImageView upper = (ImageView) findViewById(R.id.upper_overlay);
        RelativeLayout lower = (RelativeLayout) findViewById(R.id.lower_overlay);

        // convert dp to px
        upperHeight = (int)math.convertDpToPixel(upper.getLayoutParams().height);

        lower.getLayoutParams().height = phoneSettings.yPixels - upperHeight - phoneSettings.yPixels;
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
            int[] pixels = new int[phoneSettings.xPixels*phoneSettings.xPixels];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoPreview, 0, photoPreview.length);

            // (int[] pixels, int offset, int stride, int x, int y, int width, int height)
            bitmap.getPixels(pixels, 0, phoneSettings.xPixels, 0, upperHeight+1, phoneSettings.xPixels, phoneSettings.xPixels);

            //ARGB_8888 is a good quality configuration
            bitmap = Bitmap.createBitmap(pixels, 0, 100, 100, 100, Bitmap.Config.ARGB_8888);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//100 is the best quality possible

            byte[] croppedPhoto = bos.toByteArray();

            // TODO : send croppedPhoto to server

            cameraBusy = false;
            theCamera.startPreview();
        }
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            photoPreview = data;
            return;
        }
    };
}

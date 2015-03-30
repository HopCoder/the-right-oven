package com.blogspot.therightoveninc.codenamepuck;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by Timothy D. Mahon on 2/28/2015.
 */
public class cameraActivity extends Activity{
    private Camera theCamera;
    private cameraView theCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
        if (!checkCameraHardware(this)){
            return;
        }
        theCamera = getCameraInstance();
        theCameraView = new cameraView(this, theCamera);
        FrameLayout cameraView = (FrameLayout) findViewById(R.id.camera_preview);
        cameraView.addView(theCameraView);
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onDestroy(){
        theCamera.release();
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

}

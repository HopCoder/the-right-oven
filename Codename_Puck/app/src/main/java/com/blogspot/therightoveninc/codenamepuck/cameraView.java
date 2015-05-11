package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by Timothy D. Mahon on 3/1/2015.
 * Here we have the cameraView class responsible for making a preview of the camera to the user.
 */
public class cameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder; //The surface on which the preview will be displayed
    private Camera theCamera; //The camera responsible for taking pictures.

    //Constructor to make the camera preview and attach it to the current user view
    public cameraView(Context context, Camera camera) {
        super(context);
        theCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //callback responsible for setting the display to the camera preview
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            theCamera.setPreviewDisplay(holder);
            previewCamera();
        } catch (IOException e) {
            Log.d("CameraView: ", "Error setting camera preview: " + e.getMessage());
        }
    }

    //callback responsible for changing the display as the view changes (i.e. phone is turned)
    //Also responsible for correcting the orientation of the preview based on the camera hardware
    //Detected by the android API
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //Check to look for surface
        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // fix orientation problems
        Camera.Parameters parameters = theCamera.getParameters();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);

        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        parameters.setPictureSize(optimalSize.width, optimalSize.height);
        theCamera.setParameters(parameters);

        switch(display.getRotation())
        {
            case Surface.ROTATION_0:
                theCamera.setDisplayOrientation(90);
                break;
            case Surface.ROTATION_90:
                break;
            case Surface.ROTATION_180:
                break;
            case Surface.ROTATION_270:
                theCamera.setDisplayOrientation(180);
                break;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
            theCamera.setDisplayOrientation(result);

        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        // start preview with new settings
        previewCamera();
    }

    //callback to destroy the surface
    public void surfaceDestroyed(SurfaceHolder holder) {
        // purposely left blank.
    }

    //private method to set up the camera preview into the newly created holder and camera view.
    private void previewCamera()
    {
        //Try to set up the camera view if the camera can not be started catch the exception and output in the log.
        try
        {
            theCamera.setPreviewDisplay(holder);
            theCamera.startPreview();
        }
        catch(Exception e)
        {
            Log.d("cameraView:", "Cannot start preview", e);
        }
    }

    //Method for determining the optimal preview size.
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) 3/4;

        if (sizes==null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}

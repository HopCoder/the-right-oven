package com.blogspot.therightoveninc.codenamepuck;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.List;

/**
 * Created by Timothy D. Mahon on 3/1/2015.
 */
public class cameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera theCamera;
    private boolean isPreviewRunning = false;


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

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            theCamera.setPreviewDisplay(holder);
            theCamera.startPreview();
        } catch (IOException e) {
            Log.d("CameraView: ", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        releaseCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        if (isPreviewRunning)
        {
            theCamera.stopPreview();
        }

        // fix orientation problems
        Camera.Parameters parameters = theCamera.getParameters();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : sizes)
        {
            Log.e("A", Integer.toString(size.width) + " " + Integer.toString(size.height));
        }
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        Log.e("x", Integer.toString(w) + " " + Integer.toString(h));
        Log.e("q", Integer.toString(optimalSize.width));
        Log.e("q", Integer.toString(optimalSize.height));


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

/*

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(optimalSize.height, optimalSize.width);
            theCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(optimalSize.height, optimalSize.width);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            theCamera.setDisplayOrientation(180);
        }
*/

        // start preview with new settings
        previewCamera();
    }

    public void previewCamera()
    {
        try
        {
            theCamera.setPreviewDisplay(holder);
            theCamera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d("previewC", "Cannot start preview", e);
        }
    }

    private void releaseCamera(){
        if (theCamera != null){
            theCamera.stopPreview();
            isPreviewRunning = false;
            theCamera.release();        // release the camera for other applications
            theCamera = null;
        }
    }

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

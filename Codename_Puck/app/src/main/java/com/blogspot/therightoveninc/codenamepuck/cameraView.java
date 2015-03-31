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

import java.io.IOException;

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

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(h, w);
            theCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(w, h);
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(h, w);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(w, h);
            theCamera.setDisplayOrientation(180);
        }

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
}

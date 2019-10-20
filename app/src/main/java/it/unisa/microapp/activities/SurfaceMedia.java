package it.unisa.microapp.activities;

import it.unisa.microapp.utils.Utils;

import java.io.IOException;

import android.content.Context;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceMedia extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder mHolder;
    private Camera mCamera;

    public SurfaceMedia(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
        	Utils.error("Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	// mCamera.stopPreview();
    	// mCamera.release();		// togliere commenti per far funzionare surfacecamera (cameratake)
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // make any resize, rotate or reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
        	Utils.error("Error starting camera preview: " + e.getMessage());
        }
    }
    
    
    
	
	
}

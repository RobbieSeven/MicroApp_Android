package it.unisa.microapp.activities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unisa.microapp.R;
import it.unisa.microapp.R.id;
import it.unisa.microapp.data.UriData;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MediaRecVideoActivity extends MAActivity {

	private Camera mCamera;
	private SurfaceMedia mPreview;
	FrameLayout preview;
	private MediaRecorder mMediaRecorder;
	private boolean isRecording = false;
	private Button recButton;
	private URI jUri;
	
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.recvideo;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		mCamera = getCameraInstance();
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		mPreview = new SurfaceMedia(this, mCamera);
		preview.addView(mPreview);
		recButton = (Button) findViewById(id.button_capture);
		recButton.setOnClickListener(
		    new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            if (isRecording) {
		                // stop recording and release camera
		                mMediaRecorder.stop();  // stop the recording
		                releaseMediaRecorder(); // release the MediaRecorder object
		                mCamera.lock();         // take camera access back from MediaRecorder
		                next();

		                // inform the user that recording has stopped
		                setCaptureButtonText("Rec");
		                isRecording = false;
		                
		            } else {
		                // initialize video camera
		                if (prepareVideoRecorder()) {
		                    // Camera is available and unlocked, MediaRecorder is prepared,
		                    // now you can start recording
		                	mMediaRecorder.start();
		                	
		                    // inform the user that recording has started
		                    setCaptureButtonText("Stop");
		                    isRecording = true;
		                } else {
		                    // prepare didn't work, release the camera
		                    releaseMediaRecorder();
		                    // inform user
		                }
		            }
		        }
		    }
		);
	}	
	
	@Override
	protected void execute() {
		

	}	
		
	@Override
	public void restart() {
		//super.onRestart();
		prepareView(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	private void setCaptureButtonText(String string) {
		recButton.setText(string);
		
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}
	
	
	private boolean prepareVideoRecorder(){
	    mMediaRecorder = new MediaRecorder();
	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);
	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
  //      mMediaRecorder.setVideoSize(320, 240);
	    // Step 4: Set output file
        jUri =  MediaRecVideoActivity.getOutputMediaFile().toURI();
        mMediaRecorder.setOutputFile(jUri.getRawPath());
	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	    	Utils.debug("prepare() IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	    	Utils.debug("relasemediarecorder IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	  @Override
	    protected void pause() {
	        //super.onPause();
	        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
	        releaseCamera();              // release the camera immediately on pause event
	    }

	    private void releaseMediaRecorder(){
	        if (mMediaRecorder != null) {
	        	mMediaRecorder.reset();   // clear recorder configuration
	            mMediaRecorder.release(); // release the recorder object
	            mMediaRecorder = null;
	            mCamera.lock();           // lock camera for later use
	        }
	    }

	    private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	            mCamera = null;
	        }
	    }
	    
	 private static File getOutputMediaFile(){
	   // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
	   File mediaStorageDir = new File(FileManagement.getDefaultPath(), "Video");
	        // This location works best if you want the created images to be shared
	        // between applications and persist after your app has been uninstalled.
	        // Create the storage directory if it does not exist
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                Utils.debug("Video failed to create directory");
	                return null;
	            }
	        }

	        // Create a media file name
	        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(new Date());
	        File mediaFile;
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "vid_" + timeStamp + ".mp4");
	        return mediaFile;
	 }


	public void initInputs() {
	}

	@Override
	public void beforeNext() {
		UriData c = new UriData(mycomponent.getId(), Uri.parse(jUri.getRawPath())); 
		application.putData(mycomponent, c);
	}
	
	@Override 
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}

}

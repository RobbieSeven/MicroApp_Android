package it.unisa.microapp.activities;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unisa.microapp.R;
import it.unisa.microapp.R.id;
import it.unisa.microapp.data.UriData;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Utils;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MediaRecAudioActivity extends MAActivity {

    private MediaRecorder recorder;
    private Button recButton;
	private boolean isRecording = false;
	private URI jUri;
	private File mediaFile;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.recaudio;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		recButton = (Button) findViewById(id.button_rec);
		recButton.setOnClickListener(
			    new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
			            if (isRecording) {
			            	try {
								stopRecording();
							} catch (Exception e) {
								 releaseMediaRecorder();
								 Utils.error(e);
							}
			                next();
			                setCaptureButtonText("Rec");
			                isRecording = false;
			            } else {
			            		try {
									beginRecording();
								} catch (Exception e) {
									 releaseMediaRecorder();
									 Utils.error(e);
								}
			                    recorder.start();
			                    setCaptureButtonText("Stop");
			                    isRecording = true;
			                    
			            }
			        }
			    }
			);
		
	}	
	
	@Override
	protected void execute() {
		

	}	
	
	private void setCaptureButtonText(String string) {
		recButton.setText(string);		
	}
	
	private void beginRecording() throws Exception {
        File mediaStorageDir = new File(FileManagement.getDefaultPath(), "Audio");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
            	Utils.debug("Audio failed to create directory");
            }
        }
        
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "aud_" + timeStamp + ".mp3"); 
        jUri = mediaFile.toURI();
             
        releaseMediaRecorder();
        if(mediaFile.exists()) {
        	mediaFile.delete();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(jUri.getRawPath());
        recorder.prepare();
    }
	
	 private void stopRecording() throws Exception {
	        if (recorder != null) {
	            recorder.stop();
	        }
	    }
	
	 private void releaseMediaRecorder() {
	        if (recorder != null) {
	            recorder.release();
	        }
	    }

	 @Override
	    public void destroy() {
	        //super.onDestroy();
	        releaseMediaRecorder();
	    }
	

	@Override
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

package it.unisa.microapp.activities;

import java.util.Iterator;
import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.UriData;
import it.unisa.microapp.utils.Utils;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MediaPlayAudioActivity extends MAActivity {

	private MediaPlayer mediaPlayer;
    private Uri aUri;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.playaudio;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		

	}	
	
	@Override
	protected void execute() {
		

	}	
	        	
	public void doClick(View view) {
	    	int id = view.getId();
			if (id == R.id.button_play) {
				try {
        			playAudio();
				} catch (Exception e) {
					 releaseMediaPlayer();
					 Utils.error(e);
				}
				this.activateSpeech(false);
				mediaPlayer.start();
			} else if (id == R.id.button_stop) {
				try {
            		stopPlaying();
				} catch (Exception e) {
					 releaseMediaPlayer();
					 Utils.error(e);
				}
				this.activateSpeech(true);
			}
	    }
	
	private void playAudio() throws Exception {
		releaseMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(aUri.toString());
        mediaPlayer.prepare();
    }

    private void stopPlaying() throws Exception {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    
	private void releaseMediaPlayer() {
	        if (mediaPlayer != null) {
	            try {
	                mediaPlayer.release();
	            } catch (Exception e) {
	            	Utils.error(e);
	            }
	        }
	    }
	
	
	 @Override
	 public void destroy() {
	        //super.onDestroy();
	        releaseMediaPlayer();
	 }
	 
			
		@Override
		public void initInputs() {
			Iterator<GenericData<?>> i=application.getData(mycomponent.getId(), DataType.URI).iterator();
			 if (i.hasNext())
				aUri= (Uri) i.next().getSingleData();
		}

		@Override
		public void beforeNext() {
			UriData c = new UriData(mycomponent.getId(), aUri);
			application.putData(mycomponent, c);
		}
		
		@Override
		protected void resume(){
			 //metodi per speech Vincenzo Savarese
		}
}

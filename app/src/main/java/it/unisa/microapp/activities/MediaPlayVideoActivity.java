package it.unisa.microapp.activities;

import java.util.Iterator;
import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.UriData;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class MediaPlayVideoActivity extends MAActivity {

    private Uri aUri;
    private VideoView videoView;
		
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.playvideo;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
	    videoView = (VideoView)this.findViewById(R.id.videoView);
	    MediaController mc = new MediaController(this);
	    videoView.setMediaController(mc);
	    videoView.setVideoPath(aUri.toString());
	    videoView.requestFocus();
	    videoView.start();

	}	
	
	@Override
	protected void execute() {
		

	}	


	@Override
    public void destroy() {
        //super.onDestroy();
        videoView.stopPlayback();
    }
	
	public void previous() {
        videoView.stopPlayback();
		application.prevStep();
		finish();
	}
		
		@Override
		public void initInputs() {
			Iterator<GenericData<?>> i=application.getData(mycomponent.getId(), DataType.URI).iterator();
			 if (i.hasNext())
				aUri=(Uri) i.next().getSingleData();
		}

		@Override
		public void beforeNext() {
	        videoView.stopPlayback();
			UriData c = new UriData(mycomponent.getId(), aUri);
			application.putData(mycomponent, c);
		}
		
		@Override
		protected void resume(){
			 //metodi per speech Vincenzo Savarese
		}
}

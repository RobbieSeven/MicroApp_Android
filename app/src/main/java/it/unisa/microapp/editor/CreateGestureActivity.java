
package it.unisa.microapp.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.gesture.GestureLibraries;
import android.gesture.GestureOverlayView;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.Prediction;
import android.widget.TextView;
import android.widget.Toast;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.support.FileManagement;

import java.util.ArrayList;

public class CreateGestureActivity extends Activity {
    private static final float LENGTH_THRESHOLD = 120.0f;

    private Gesture mGesture;
    private View mDoneButton;
    GestureLibrary mLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.create_gesture);

        mDoneButton = findViewById(R.id.done);

		final Intent intent = getIntent();
		String extra = intent.getStringExtra("namefile");
		if(extra != null && !extra.equals(""))
		{
            final TextView input = (TextView) findViewById(R.id.gesture_name);
            input.setText(extra);
			
		}
        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        overlay.addOnGestureListener(new GesturesProcessor());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            final GestureOverlayView overlay =
                    (GestureOverlayView) findViewById(R.id.gestures_overlay);
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }

    public void addGesture(View v) {
        if (mGesture != null) 
        {
            final TextView input = (TextView) findViewById(R.id.gesture_name);
            final CharSequence name = input.getText();
            //il nome del gesto non può essere nullo
            if (name.length() == 0)
            {
                input.setError(getString(R.string.error_missing_name));
                return;
            }
            MicroAppGenerator app = (MicroAppGenerator) getApplication();
    		app.setGesture(mGesture);
    		app.setFlagGesture(true);
    		app.setGestureName(name.toString());
            setResult(RESULT_OK);
            Toast.makeText(this,"Gesture is valid", Toast.LENGTH_LONG).show();
        } 
        else 
        {
            setResult(RESULT_CANCELED);
        }

        finish();   
    }
    
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mDoneButton.setEnabled(false);
            mGesture = null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }
        
        //solamente se il mio gesto � abbastanza lungo abilito il bottone
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() > LENGTH_THRESHOLD) {
            	
            	try
            	{
            		mLibrary = GestureLibraries.fromFile(FileManagement.getGestureDir());
            		mLibrary.load();
            		ArrayList<Prediction> predictions = mLibrary.recognize(mGesture);
            		Prediction prediction = predictions.get(0);
            		if(prediction.score>5)
            		{	
            		
            			show();
            			return;
            		}
            	
            	}
            	
            	catch(Exception e)
            	{
            	}
            	
            	
                
                mDoneButton.setEnabled(true);
            }
            else
            	
            {
            	overlay.clear(false);
            	mDoneButton.setEnabled(false);
            }
            
            
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }

	public void show() {
		Toast.makeText(this,"This gesture is already used by another MicroApp", Toast.LENGTH_LONG).show();
	}
}

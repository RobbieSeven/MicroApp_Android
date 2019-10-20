package it.unisa.microapp.editor;

import it.unisa.microapp.R;
//import it.unisa.microapp.activities.MAActivity;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;

public class ControllerConditionActivity extends Activity implements SensorEventListener{
	
	//private MAActivity maActivity;
	
	private PreCondition [] listCondition=null;
	public SensorManager mSensorManager;
	public Sensor mBrightness;
	private boolean useSensorBrightness=false;
	private float valueBrightness;
	//private int i=0;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		
		Intent intent = getIntent();
		Parcelable[] p = intent.getParcelableArrayExtra("listcondition");
		listCondition = new PreCondition[p.length];

		if(p!=null){

			for(int i=0; i<p.length ;i++){
				PreCondition pre = (PreCondition) p[i];
		 		listCondition[i] = new PreCondition();
					listCondition[i].setCheck(pre.isCheck());
					listCondition[i].setCondition(pre.getCondition());
					listCondition[i].setOperator(pre.getOperator());
					listCondition[i].setValue(pre.getValue());
					
					if(listCondition[i].isCheck()==true && listCondition[i].getCondition()==2){
						useSensorBrightness = hasSensorBrightness();
					}
			}
		}
		
	}
	@Override
	  public void onResume() {
		super.onResume();  
		 if(!useSensorBrightness){
			 boolean verification = verification(listCondition);
			    Intent data = new Intent(); 
			    data.putExtra("verification", verification);
			    setResult(Constants.LIST_CONDITION, data);
			    
			    finish();
		 }
	}
	
	@Override
	  public void onPause() {
	    super.onPause(); 
	/*	 if(useSensorBrightness){
				mSensorManager.unregisterListener(this);
		 }    
	    */
	  }

	
/*	public ControllerCondition(MAActivity maAct){
		maActivity = maAct;
	}
	*/
/*	public boolean useSensor(PreCondition[] condition){
		boolean use = false;

		for(int i=0; i < condition.length ; i++){
			if((condition[i].getCondition()==2) && (condition[i].isCheck())){
				use=true;
				break;
			}
		}
		return use;
	}
	*/
	public boolean verification(PreCondition[] condition){
		boolean t = true;

		for(int i=0; i < condition.length ; i++){

			if(condition[i].isCheck()){

				switch(condition[i].getCondition()){
				case 0:			// Temperature
					break;
					
				case 1:			//Proximity
					break;
					
				case 2:			//Brightness
					
				//	if(sensorBrightness()){
						float input;
						try{
						input = Float.parseFloat(condition[i].getValue());
						}catch(Exception e){
							input=-1;
						}
						
						switch(condition[i].getOperator()){
							case 0:		// equal 
								if(input != getBrightness()){
									t = false;
								}
								break;
								
							case 1:		// not equal 
								if(input == getBrightness()){
									t = false;
								}
								break;
								
							case 2:		// less than 
								if(input <= getBrightness()){
									t = false;
								}
								break;
								
							case 3:		// less equal 
								if(input < getBrightness()){
									t = false;
								}
								break;
								
							case 4:		// greater than
								if(input >= getBrightness()){
									t = false;
								}
								break;
								
							case 5:		// greater equal
								if(input > getBrightness()){
									t = false;
								}
								break;
							
							default:
								break;
							}					
				//	}			
				break;
					
				case 3:			//Network
					
					switch(condition[i].getOperator()){
					case 0:		// is 
						if(((this.isConnected() == true) && (condition[i].getValue().equals("false")))
								|| ((this.isConnected() == false) && (condition[i].getValue().equals("true"))))
							t = false;
						break;
						
					case 1:		// not is 
						if(((this.isConnected() == true) && (condition[i].getValue().equals("true")))
								|| ((this.isConnected() == false) && (condition[i].getValue().equals("false"))))
							t = false;
						break;
						
					default:
						break;
					}
					
					break;
				default:
					break;
				}
					
				
			}
			
			if(!t)
				break;
		}

		return t;
	}
	
	private boolean isConnected(){
		boolean isConnected;
		
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		try{
			isConnected = activeNetwork.isConnectedOrConnecting();
		}catch(Exception e){
			isConnected = false;
		}

		return isConnected;
	}

	public boolean hasSensorBrightness(){
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    if((mBrightness = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)) != null){
		    	mSensorManager.registerListener(this, mBrightness,SensorManager.SENSOR_DELAY_FASTEST);
			    return true;
		    }else{
		    	valueBrightness = -1;
			    Utils.debug("brightness no hardware " + getBrightness());
			    return false;
		    }
	}
	
	@Override
	public final void onSensorChanged(SensorEvent event) {
	    setBrightness(event.values[0]);
	    Utils.debug("values "+ event.values[0] + " " + getBrightness());
//	   if(i==1){
		   mSensorManager.unregisterListener(this);
	    boolean verification = verification(listCondition);
	    Utils.debug("verification " + verification );
	    Intent data = new Intent(); 
	    data.putExtra("verification", verification);
	    setResult(Constants.LIST_CONDITION, data);
	    
	    finish();
//	    }	    i++;
	 
	    //  mSensorManager.unregisterListener(this);
	    // Do something with this sensor data.
	  }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public float getBrightness() {
		return valueBrightness;
	}

	public void setBrightness(float bright) {
		valueBrightness = bright;
	}
	
	
}

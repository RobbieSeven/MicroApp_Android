package it.unisa.microapp.editor;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SetConditionActivity extends ListActivity {
	public ListView listView;
	public DataAdapter d;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.condition);
		
		listView = (ListView) findViewById(android.R.id.list);
		String[] conditions = getResources().getStringArray(R.array.conditions);

		Intent intent = getIntent();
		Parcelable[] p = intent.getParcelableArrayExtra("precondition");
	
		List<DataHolder> container = new ArrayList<DataHolder>();
		

		for (int i = 0; i < conditions.length; i++) {
			DataHolder tmp = new DataHolder(this);
			if(p!=null){
				PreCondition pre = (PreCondition) p[i];
				tmp.setCheck(pre.isCheck());
				tmp.setSelectedOperator(pre.getOperator());
				tmp.setValue(pre.getValue());
			}
			container.add(tmp);
		}
		
		DataHolder[] datas = new DataHolder[container.size()];
		d = new DataAdapter(this, R.layout.rigacondition, container.toArray(datas));
		listView.setAdapter(d);

		final CheckBox checkbox = (CheckBox) this.findViewById(R.id.checkBox1);
		boolean mandatory = intent.getBooleanExtra("mandatory", false);
			if(mandatory)
				checkbox.setChecked(true);
			
		Button buttonSet = (Button) this.findViewById(R.id.button1);
		buttonSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
							
				Activity act = SetConditionActivity.this;

				Intent myIntent = act.getIntent();
				Bundle b = new Bundle();
				if (checkbox.isChecked())
					b.putInt("condition", Constants.MANDATORY_CONDITION);
				else
					b.putInt("condition", Constants.OPTIONAL_CONDITION);
								
				PreCondition[] ris = null;
				boolean checkCondition=false;
				if(d.getCount()!=0){
					ris = new PreCondition[d.getCount()];
					
					for(int i=0; i < d.getCount() ; i++){
						DataHolder h = d.getItem(i);
						ris[i] = new PreCondition(h.isCheck(),i,h.getSelectedOperator(),h.getValue());
						if(h.isCheck())
							checkCondition=true;
					}
				}
				if(checkCondition){
				b.putParcelableArray("precondition", ris);
				
				myIntent.putExtras(b);
				setResult(11, myIntent);
				finish();
				}else{
					Toast.makeText(getApplication(),
							"No Pre-Condition selected!",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		Button buttonunset = (Button) this.findViewById(R.id.button2);

		buttonunset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Activity act = SetConditionActivity.this;

				Intent myIntent = act.getIntent();
				Bundle b = new Bundle();
				b.putInt("condition", Constants.NO_CONDITION);
				
				PreCondition[] ris = new PreCondition[4];
				for(int i=0; i < 4;i++){
					ris[i] = new PreCondition();
				}
				b.putParcelableArray("precondition", ris);
				
				myIntent.putExtras(b);
				setResult(11, myIntent);
				finish();
			}

		});

	}

		
	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//TODO: manage result
		  if (requestCode == 1) {
			   
		     if(resultCode == RESULT_OK){      
					Bundle b=data.getExtras();		
					LatLng center = (LatLng) b.get("center");
					LatLng limit = (LatLng) b.get("limit");
					Float radius = (Float) b.get("radius");
					
					//TODO:copiare i result nei campi
					String val = center.toString()+";"+limit.toString()+";"+radius;
					DataAdapter.dataVector[1].setValue(val);
       
		     }
		     if (resultCode == RESULT_CANCELED) {    
		     }
		  }
		}//onActivityResult	

}

class DataHolder {

	private int conditionselected;
	private int operatorselected;
	private ArrayAdapter<CharSequence> condition;
	private ArrayAdapter<CharSequence> operator;
	private String value;
	private boolean check;
	
	public DataHolder(Context parent) {
		condition = ArrayAdapter.createFromResource(parent, R.array.conditions, android.R.layout.simple_spinner_item);
		condition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		operator = ArrayAdapter.createFromResource(parent, R.array.operators, android.R.layout.simple_spinner_item);
		operator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		value="";
		check=false;
	}
	
	public DataHolder(Context parent, int cselected, int oselected) {
		this(parent);

		conditionselected = cselected;
		operatorselected = oselected;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public ArrayAdapter<CharSequence> getAdapterCondition() {
		return condition;
	}

	public ArrayAdapter<CharSequence> getAdapterOperator() {
		return operator;
	}

	public String getTextCondition() {
		return (String) condition.getItem(conditionselected);
	}

	public String getTextOperator() {
		return (String) operator.getItem(operatorselected);
	}

	public int getSelectedCondition() {
		return conditionselected;
	}

	public int getSelectedOperator() {
		return operatorselected;
	}

	public void setSelectedCondition(int selected) {
		conditionselected = selected;
	}

	public void setSelectedOperator(int selected) {
		operatorselected = selected;
	}
}

class DataAdapter extends ArrayAdapter<DataHolder> {

	private static Activity myContext;
	public static DataHolder[] dataVector;

	public DataAdapter(Activity context, int textViewResourceId, DataHolder[] objects) {
		super(context, textViewResourceId, objects);
		myContext = context;
		dataVector=objects;
	}

	static class ViewHolder {
		protected DataHolder data;
		protected CheckBox check;

		protected TextView text;
		protected Spinner spin;
		protected Spinner spin2;
		protected Spinner spinValue;

		protected Button button;
		protected Switch switc;
		
		public ViewHolder(DataHolder data)
		{
			this.data = data;
		}
		
		protected void setEnabled(boolean state)
		{
			if(text != null) text.setEnabled(state); 
			if(spin != null) spin.setEnabled(state);
			if(spin2 != null) spin2.setEnabled(state); 
			if(button != null) button.setEnabled(state); 
			if(switc != null) switc.setEnabled(state); 
			if(spinValue != null) spinValue.setEnabled(state); 

		}
		
		protected void setupWidgets(View view)
		{
			switch(data.getSelectedCondition())
			{
			case 0:
				text = (EditText) view.findViewById(R.id.editText1);
				text.setText(dataVector[0].getValue());
				text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
				text.setOnFocusChangeListener(new OnFocusChangeListener(){
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						dataVector[0].setValue(text.getText().toString());
					}
				});
				
				spin = (Spinner) view.findViewById(R.id.spinner1);
				spin.setAdapter(data.getAdapterCondition());
				spin.setSelection(data.getSelectedCondition());
				spin.setEnabled(false);
		
				spin2 = (Spinner) view.findViewById(R.id.spinner2);
				spin2.setAdapter(data.getAdapterOperator());
				spin2.setSelection(dataVector[0].getSelectedOperator());
				spin2.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
						dataVector[0].setSelectedOperator(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				check = (CheckBox) view.findViewById(R.id.checkBox1);	
				Utils.debug("dataVector "+ dataVector[0].isCheck());
				if(dataVector[0].isCheck())
					check.setChecked(true);
				else
					this.setEnabled(false);
				
				check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton arg0,boolean arg1) {
						dataVector[0].setCheck(arg1);
					}
					
				});
				break;
			case 1: 
				text = (EditText) view.findViewById(R.id.editText1);
				//strip values
				text.setText(dataVector[1].getValue());
				text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);				
				text.setOnFocusChangeListener(new OnFocusChangeListener(){
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						dataVector[1].setValue(text.getText().toString());
					}
				});

				spin = (Spinner) view.findViewById(R.id.spinner1);
				spin.setAdapter(data.getAdapterCondition());
				spin.setSelection(data.getSelectedCondition());	
				spin.setEnabled(false);
				
				spin2 = (Spinner) view.findViewById(R.id.spinner2);
				spin2.setAdapter(data.getAdapterOperator());
				spin2.setSelection(dataVector[1].getSelectedOperator());
				spin2.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
						dataVector[1].setSelectedOperator(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				
				check = (CheckBox) view.findViewById(R.id.checkBox1);	
				if(dataVector[1].isCheck())
					check.setChecked(true);
				else
					this.setEnabled(false);
				check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0,boolean arg1) {
						dataVector[1].setCheck(arg1);
					}
				});				
				
				
				button = (Button) view.findViewById(R.id.button1); 
				button.setEnabled(false);
				button.setOnClickListener(new OnClickListener() {			
					@Override
					public void onClick(View v) {		
						//mostra mappa
						Intent in= new Intent(myContext, ConditionMapActivity.class);
					  	myContext.startActivityForResult(in, 1);							
					}
				});
				break;
			case 2:
				spinValue = (Spinner) view.findViewById(R.id.spinnerValue);
				
				ArrayAdapter<CharSequence> operatorBrightness = ArrayAdapter.createFromResource(myContext, R.array.operatorsBrightness, android.R.layout.simple_spinner_item);
				operatorBrightness.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spinValue.setAdapter(operatorBrightness);
				int value = 0;
				try{
					value = Integer.parseInt(dataVector[2].getValue());
				}catch(Exception e){
					value = 0;
				}
					switch(value){
					case 0:
						spinValue.setSelection(0);
						break;
					case 10:
						spinValue.setSelection(1);
						break;
					case 100:
						spinValue.setSelection(2);
						break;
					case 1000:
						spinValue.setSelection(3);
						break;
					case 10000:
						spinValue.setSelection(4);
						break;
					default:
						spinValue.setSelection(0);
						break;
				}
				
				spinValue.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
						switch(pos){
							case 0:
								dataVector[2].setValue("0");
								break;
							case 1:
								dataVector[2].setValue("10");
							break;
							case 2:
								dataVector[2].setValue("100");
							break;
							case 3:
								dataVector[2].setValue("1000");
							break;
							case 4:
								dataVector[2].setValue("10000");
							break;
							default:
								dataVector[2].setValue("0");
								break;
							}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				
				spin = (Spinner) view.findViewById(R.id.spinner1);
				spin.setAdapter(data.getAdapterCondition());
				spin.setSelection(data.getSelectedCondition());
				spin.setEnabled(false);
		
				spin2 = (Spinner) view.findViewById(R.id.spinner2);
				spin2.setAdapter(data.getAdapterOperator());
				spin2.setSelection(dataVector[2].getSelectedOperator());
				spin2.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
						dataVector[2].setSelectedOperator(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				check = (CheckBox) view.findViewById(R.id.checkBox1);	
				if(dataVector[2].isCheck())
					check.setChecked(true);
				else
					this.setEnabled(false);
				check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton arg0,boolean arg1) {
						dataVector[2].setCheck(arg1);
					}
				});
				break;	
			case 3:
				text = (EditText) view.findViewById(R.id.editText1);
				spin = (Spinner) view.findViewById(R.id.spinner1);
				spin.setAdapter(data.getAdapterCondition());
				spin.setSelection(data.getSelectedCondition());
				spin.setEnabled(false);
				
				spin2 = (Spinner) view.findViewById(R.id.spinner2);

				ArrayAdapter<CharSequence> operatorNetwork = ArrayAdapter.createFromResource(myContext, R.array.operatorsNetwork, android.R.layout.simple_spinner_item);
				operatorNetwork.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				spin2.setAdapter(operatorNetwork);
				spin2.setSelection(dataVector[3].getSelectedOperator());
				spin2.setOnItemSelectedListener(new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
						dataVector[3].setSelectedOperator(pos);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				
				check = (CheckBox) view.findViewById(R.id.checkBox1);	
				if(dataVector[3].isCheck())
					check.setChecked(true);
				else
					this.setEnabled(false);
				check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0,boolean arg1) {
						dataVector[3].setCheck(arg1);
					}
				});
				
				switc = (Switch) view.findViewById(R.id.switch1);
				if(dataVector[3].getValue().equals("true"))
					switc.setChecked(true);
				else
					switc.setChecked(false);
				switc.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton arg0,boolean arg1) {
						dataVector[3].setValue(""+arg1);
					}
				});
				break;				
			default:
				text = (EditText) view.findViewById(R.id.editText1);
				spin = (Spinner) view.findViewById(R.id.spinner1);
				spin.setAdapter(data.getAdapterCondition());
				spin.setSelection(data.getSelectedCondition());			
				spin2 = (Spinner) view.findViewById(R.id.spinner2);
				spin2.setAdapter(data.getAdapterOperator());
				spin2.setSelection(-1);
				check = (CheckBox) view.findViewById(R.id.checkBox1);
			}
			
			check.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {		
					setEnabled(check.isChecked());
				}
			});			
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		// Check to see if this row has already been painted once.
		if (convertView == null) {

			// If it hasn't, set up everything:
			LayoutInflater inflator = myContext.getLayoutInflater();

			switch(position)
			{
			case 0:
				view = inflator.inflate(R.layout.rigacondition, null);
				break;
			case 1:
				view = inflator.inflate(R.layout.rigaconditionproximity, null);
				break;
			case 2:
				view = inflator.inflate(R.layout.rigaconditionbrightness, null);
				break;
			case 3:
				view = inflator.inflate(R.layout.rigaconditionnetwork, null);
				break;				
			default:
				view = inflator.inflate(R.layout.rigacondition, null);
				break;				
			}
	
			final ViewHolder viewHolder = new ViewHolder(new DataHolder(myContext, position, 1));
			viewHolder.setupWidgets(view);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		// This is what gets called every time the ListView refreshes
		// ViewHolder holder = (ViewHolder) view.getTag();
		// holder.text.setText(getItem(position).getText());
		// holder.spin.setSelection(getItem(position).getSelected());

		return view;
	}
}

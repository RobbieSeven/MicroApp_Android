package it.unisa.microapp.editor;

import java.util.ArrayList;

import java.util.List;
import it.unisa.microapp.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SetStateActivity extends ListActivity {
	public ListView listView;
	public StateDataAdapter d;
	public RadioButton radiobtn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String[] s = intent.getStringArrayExtra("states");
		String nowst = intent.getStringExtra("nowState");
			
		setContentView(R.layout.state);
			
		listView = (ListView) findViewById(android.R.id.list);
		//String[] states = getResources().getStringArray(R.array.states);
		
		List<StateDataHolder> container = new ArrayList<StateDataHolder>();
		
		for (int i = 0; i < s.length; i++) {
			StateDataHolder tmp = new StateDataHolder(this);
			if(s!=null){
				String st = s[i];
				tmp.setCheck(false);
				tmp.setValue(st);
			}
				container.add(tmp);
		}
		
		StateDataHolder[] datas = new StateDataHolder[container.size()];
		d = new StateDataAdapter(this, R.layout.rigastate, container.toArray(datas), nowst);
		listView.setAdapter(d);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		radiobtn = (RadioButton) this.findViewById(R.id.selector);
		
		Button set = (Button) this.findViewById(R.id.buttonSet);
		
		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent();
				Bundle b = new Bundle();
				b.putString("state", d.getNow());
				setIntent.putExtras(b);
				setResult(14, setIntent);        
				finish();
			}

		});	
		
		Button back = (Button) this.findViewById(R.id.buttonBack);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);        
				finish();
			}

		});
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if(resultCode != RESULT_CANCELED){
			  if (requestCode == 1) {
			   
				  if(resultCode == RESULT_OK){      
       
				  }
			  }
		  }
	}
}

class StateDataHolder {
	
	private int stateselected;
	private String value;
	private boolean check;
	private ArrayAdapter<CharSequence> state;
	
	public StateDataHolder(Context parent) {
		try {
		state = ArrayAdapter.createFromResource(parent, R.array.states, android.R.layout.simple_list_item_1);
		} catch(Exception e) {
		}
		value = "";
		check = false;
	}
	
	public StateDataHolder(Context parent, int sselected) {
		this(parent);

		stateselected = sselected;
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
	
	public int getSelectedState() {
		return stateselected;
	}
	
	public void setSelectedState(int selected) {
		stateselected = selected;
	}
	
	public ArrayAdapter<CharSequence> getAdapterState() {
		return state;
	}
}

class StateDataAdapter extends ArrayAdapter<StateDataHolder> {
	
	private static Activity myContext;
	public static StateDataHolder[] dataVector;
    private static RadioButton mSelectedRB;
    private static int mSelectedPosition = -1;
    private static RadioButton generic;
    private static String now;
    protected boolean firsttime = true;
	
	public StateDataAdapter(Activity context, int textViewResourceId, StateDataHolder[] objects, String p) {
		super(context, textViewResourceId, objects);
		myContext = context;
		dataVector = objects;
		now = p;
	}
	
	public String getNow() {
		return now;
	}
	
	static class ViewHolder {
		protected StateDataHolder data;
		protected RadioButton check;
		protected TextView text;
		
		public ViewHolder(StateDataHolder data)
		{
			this.data = data;
		}
		
		protected void setEnabled(boolean state)
		{
			if(text != null) text.setEnabled(state);
		}
		
		protected void setupWidgets(View view)
		{
			generic = (RadioButton) view.findViewById(R.id.selector);
			switch(data.getSelectedState())
			{
			case 0:	
				text = (TextView) view.findViewById(R.id.textState);
				text.setText(dataVector[0].getValue());
				text.setEnabled(false);
				
				if(now.equals("visible")) {
					generic.setChecked(true);
					mSelectedRB = generic;
					mSelectedPosition = 0;
				}
				break;
			case 1: 
				text = (TextView) view.findViewById(R.id.textState);
				TextView textDescr = (TextView) view.findViewById(R.id.textDescr);
				text.setText(dataVector[1].getValue());
				if(dataVector[1].getValue().equals("progress")) {
					text.setTextColor(Color.parseColor("#00FF00"));
					textDescr.setText("Starts this component with a progress bar.");
				}
				text.setEnabled(false);
				
				if(now.equals("hidden")) {
					generic.setChecked(true);
					mSelectedRB = generic;
					mSelectedPosition = 1;
				}
				else if(now.equals("progress") && dataVector[1].getValue().equals("progress")) {
					generic.setChecked(true);
					mSelectedRB = generic;
					mSelectedPosition = 1;
				}
				break;
			case 2:
				text = (TextView) view.findViewById(R.id.textState);
				text.setText(dataVector[2].getValue());
				text.setEnabled(false);
				
				if(now.equals("progress")) {
					generic.setChecked(true);
					mSelectedRB = generic;
					mSelectedPosition = 2;
				}
				break;				
			default:
				text = (TextView) view.findViewById(R.id.textState);
				//check = (RadioButton) view.findViewById(R.id.selector);
			}
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;

		// Check to see if this row has already been painted once.
		if (convertView == null) {

			// If it hasn't, set up everything:
			LayoutInflater inflator = myContext.getLayoutInflater();

			switch(position)
			{
			case 0:
				view = inflator.inflate(R.layout.rigastatevisible, null);
				break;
			case 1:
				view = inflator.inflate(R.layout.rigastatehidden, null);
				break;
			case 2:
				view = inflator.inflate(R.layout.rigastateprocess, null);
				break;			
			default:
				view = inflator.inflate(R.layout.rigastate, null);
				break;				
			}
	
			final ViewHolder viewHolder = new ViewHolder(new StateDataHolder(myContext, position));
			viewHolder.setupWidgets(view);

			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		generic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if((position != mSelectedPosition && mSelectedRB != null)) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton) v;
                
                if(mSelectedPosition == 0)
                	now = "visible";
                else if (mSelectedPosition == 1 && dataVector[1].getValue().equals("hidden"))
                	now = "hidden";
                else 
                	now = "progress";
                
                firsttime = false;
            }
        });
		
		if(!firsttime) {
			if(mSelectedPosition != position){
        		generic.setChecked(false);
        	}else{
        		generic.setChecked(true);
            	if(mSelectedRB != null && generic != mSelectedRB){
                	mSelectedRB = generic;
            	}
        	}
		}
		
		return view;
	}
	
}
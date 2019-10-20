package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.data.StringData;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class BetweenNumberActivity extends MAActivity 
{
	private NumberPicker txt;
	private String dsc;
	private String tit;
	
	private String lower;
	private String upper;
	
	int iUp;
	int iLow;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		lower = "0";
		upper = "0";
		
		dsc="";
		tit="";
		
		iUp = 0;
		iLow = 0;

		tit= "Number";
		
	}	
	
	
	@Override
	public void initInputs() 
	{
		lower=this.mycomponent.getUserData("lower").iterator().next();
		upper=this.mycomponent.getUserData("upper").iterator().next();
		
		try {
			iUp = Integer.parseInt(upper);
			iLow = Integer.parseInt(lower);
		} catch(NumberFormatException e)
		{
			iUp = 0;
			iLow = 0;
		}
		
	}
	
	
	@Override
	protected void prepare() {
		dsc= "insert number for "+ mycomponent.getBindings(0)+ "in the range ["+ iLow +", "+ iUp+"]";
	}

	@Override
	protected void prepareView(View v) {
	}	
		
	@Override
	protected int onVisible() {
		return 0;
	}
	
	@Override
	protected View onVisibleView() {
		LinearLayout lin=new LinearLayout(this);
		lin.setOrientation(LinearLayout.VERTICAL);
		
		//this.setContentView(lin);
		TextView title=new TextView(this);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
		title.setText(tit);
		TextView desc=new TextView(this);
		desc.setText(dsc);
		
		lin.addView(title);
		lin.addView(desc);
		
		txt=new NumberPicker(this);
		
        txt.setMaxValue(iUp);
        txt.setMinValue(iLow);		
		lin.addView(txt);
		
		Button b=new Button(this);
		b.setBackground(this.getResources().getDrawable(R.drawable.mybutt));
		
		b.setTextColor(Color.WHITE);
		b.setText("Submit");
		
		b.setPadding(10, 10, 10, 10);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
					next();
			}
			
		});
		
		lin.addView(b);
		return lin;
	}

	@Override
	protected void execute() {
	}	
	
	@Override
	public void beforeNext() 
	{
		String s=Integer.valueOf(txt.getValue()).toString();
		StringData data=new StringData(this.mycomponent.getId(),s);
		this.application.putData(this.mycomponent, data);
	}
	
	@Override
	protected void resume(){}
}

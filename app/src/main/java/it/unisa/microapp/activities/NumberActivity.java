package it.unisa.microapp.activities;

import java.text.NumberFormat;
import java.text.ParsePosition;

import it.unisa.microapp.R;
import it.unisa.microapp.data.StringData;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberActivity extends MAActivity 
{
	private EditText txt;
	private String str="";
	private String dsc="";
	private String tit="";
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		tit = getString(R.string.number);
	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		LinearLayout lin=new LinearLayout(this);
		lin.setOrientation(LinearLayout.VERTICAL);
		
		//this.setContentView(lin); //modifico lo chiamo all'interno di setAppView
		TextView title=new TextView(this);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
		title.setText(tit);
		title.setId(1);
		txt=new EditText(this);
		txt.setId(2);
		txt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		txt.setText(str);
		TextView desc=new TextView(this);
		txt.setId(3);
		desc.setText(dsc);
		desc.setLabelFor(txt.getId());
		desc.setId(4);
		lin.addView(title);
		lin.addView(desc);
		
		
		lin.addView(txt);
		Button b=new Button(this);
		b.setId(5);
		//b.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.mybutt));
		b.setBackground(this.getResources().getDrawable(R.drawable.mybutt));
		
		b.setTextColor(Color.WHITE);
		b.setText("Submit");
		
		b.setPadding(10, 10, 10, 10);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				if(check(txt.getText().toString()))
					next();
			}
			
		});
		
		lin.addView(b);

		return lin;
	}

	@Override
	protected void prepareView(View v) {
		

	}	
	
	@Override
	protected void execute() {
		

	}	
			
	private boolean check(String s)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(s, pos);
		return s.length() == pos.getIndex();		
	}
	
	@Override
	public void initInputs() 
	{
		dsc= "insert number for "+ mycomponent.getBindings(0);
	}

	@Override
	public void beforeNext() 
	{
		String s=txt.getText().toString();
		StringData data=new StringData(this.mycomponent.getId(),s);
		this.application.putData(this.mycomponent, data);
	}

	public void resume(){
		
	}

}
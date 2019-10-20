package it.unisa.microapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import it.unisa.microapp.R;
import it.unisa.microapp.data.StringData;

public class StaticTextActivity extends MAActivity {

	private EditText txt;
	private String str="";
	private String dsc="";
	private String tit="";
	
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		LinearLayout lin=new LinearLayout(this);
		lin.setOrientation(LinearLayout.VERTICAL);
		
//		this.setContentView(lin);
		TextView title=new TextView(this);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
		title.setText(tit);
		title.setId(1);//vincenzo
		txt=new EditText(this);
		txt.setText(str);
		txt.setId(2);//vincenzo
		TextView desc=new TextView(this);
		desc.setText(dsc);
		desc.setLabelFor(txt.getId());//vincenzo
		desc.setId(3);//vincenzo
		
		lin.addView(title);
		lin.addView(desc);
		
		
		lin.addView(txt);
		Button b=new Button(this);
		b.setBackground(this.getResources().getDrawable(R.drawable.mybutt));
		b.setId(4);//vincenzo
		b.setTextColor(Color.WHITE);
		b.setText(R.string.submit);
		
		b.setPadding(10, 10, 10, 10);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				next();
			}
			
		});
		
		lin.addView(b);
		
		next();
		return lin;
	}

	@Override
	protected void prepareView(View v) {
		

	}	
	
	@Override
	protected void execute() {
			next();
	}	
	
	@Override
	public void initInputs() 
	{
		String binding=mycomponent.getBindings().get(0);
		tit=getString(R.string.text);
		dsc="insert text for "+binding;

		if(this.mycomponent.getUserData("text") != null)
		{
			str=this.mycomponent.getUserData("text").iterator().next();
		}		
	}

	@Override
	public void beforeNext() 
	{
		String s=txt.getText().toString();
		StringData data=new StringData(this.mycomponent.getId(),s);
		this.application.putData(this.mycomponent, data);
	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}	
}

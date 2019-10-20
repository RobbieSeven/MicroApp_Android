package it.unisa.microapp.activities;

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

public class PasswordTextActivity extends MAActivity 
{
	private EditText txt;
	private String str="";
	private String dsc="";
	private String tit="";
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		tit= getString(R.string.password);

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
		
		//this.setContentView(lin);
		TextView title=new TextView(this);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);
		title.setText(tit);
		title.setId(1);
		txt=new EditText(this);
		txt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		txt.setHint(getString(R.string.password));
		txt.setText(str);
		txt.setId(2);
		TextView desc=new TextView(this);
		desc.setText(dsc);
		desc.setId(3);
		desc.setLabelFor(2);
		
		lin.addView(title);
		lin.addView(desc);
		lin.addView(txt);
		
		Button b=new Button(this);
		b.setId(4);
		b.setBackground(this.getResources().getDrawable(R.drawable.mybutt));
		
		b.setTextColor(Color.WHITE);
		b.setText(getString(R.string.submit));
		
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
	protected void prepareView(View v) {
		
	}	
	
	@Override
	protected void execute() {
		
	}	

	
	@Override
	public void initInputs() 
	{
		String binding=mycomponent.getBindings().get(0);
		dsc="insert text for "+binding;
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

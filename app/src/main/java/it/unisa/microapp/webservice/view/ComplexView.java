package it.unisa.microapp.webservice.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public abstract class ComplexView extends LinearLayout
{

	public ComplexView(Context context) 
	{
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	public abstract Object getGUIValues();
	
	public void addElement(View v,String name)
	{
		v.setTag(name);
		this.addView(v);
	}
}

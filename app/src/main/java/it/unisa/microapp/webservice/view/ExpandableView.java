package it.unisa.microapp.webservice.view;

import it.unisa.microapp.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ExpandableView extends LinearLayout 
{
	private Context con;
	private boolean show;
	private View view;
	private Button b;
	private String name;
	
	public ExpandableView(Context context,AttributeSet attribute)
	{
		super(context,attribute);
		con=context;
		makeButton();
	}

	public ExpandableView(Context context)
	{
		super(context);
		con=context;
		
	}
	
	public ExpandableView(Context context,String n,View v) 
	{
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
		con=context;
		show=false;
		this.name=n;
		
		b=new Button(con);
		
		b.setText("show "+name);
		
		view=v;
		
		view.setPadding(0, 20, 0, 20);
		
		v.setVisibility(View.GONE);
		
		this.addView(b);
		this.addView(view);
		
		b.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(show)
				{
					show=false;
					view.setVisibility(View.GONE);
					b.setText("show "+name);
					b.setCompoundDrawablesWithIntrinsicBounds(null, null, ExpandableView.this.getResources().getDrawable(R.drawable.show), null);
					ExpandableView.this.invalidate();
				}
				else
				{
					show=true;
					view.setVisibility(View.VISIBLE);
					b.setText("hide "+name);
					b.setCompoundDrawablesWithIntrinsicBounds(null, null, ExpandableView.this.getResources().getDrawable(R.drawable.hide), null);
					ExpandableView.this.invalidate();
				}
			}
			
		});
		
		b.setCompoundDrawablesWithIntrinsicBounds(null, null, this.getResources().getDrawable(R.drawable.show), null);
		b.setTextColor(Color.WHITE);
		b.setBackgroundColor(android.R.color.transparent);
	}
	
	private void makeButton() 
	{
		b=new Button(con);
		
		b.setText("show "+name);
		b.setCompoundDrawablesWithIntrinsicBounds(null, null, this.getResources().getDrawable(R.drawable.show), null);
		b.setTextColor(Color.WHITE);
		b.setBackgroundColor(android.R.color.transparent);
		
		b.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(show)
				{
					show=false;
					view.setVisibility(View.GONE);
					b.setText("show "+name);
					b.setCompoundDrawablesWithIntrinsicBounds(null, null, ExpandableView.this.getResources().getDrawable(R.drawable.show), null);
					ExpandableView.this.invalidate();
				}
				else
				{
					show=true;
					view.setVisibility(View.VISIBLE);
					b.setText("hide "+name);
					b.setCompoundDrawablesWithIntrinsicBounds(null, null, ExpandableView.this.getResources().getDrawable(R.drawable.hide), null);
					ExpandableView.this.invalidate();
				}
			}
			
		});
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setView(View view) 
	{
		this.view = view;
	}

	public boolean isShow() {
		return show;
	}
	
	

	public void setShow(boolean show) 
	{
		this.show = show;
		if(show)
		{
			b.setText("hide "+name);
			b.setCompoundDrawablesWithIntrinsicBounds(null, null, this.getResources().getDrawable(R.drawable.hide), null);
			view.setVisibility(View.VISIBLE);
		}
		else
		{
			b.setText("show "+name);
			b.setCompoundDrawablesWithIntrinsicBounds(null, null, this.getResources().getDrawable(R.drawable.show), null);
			view.setVisibility(View.GONE);
		}
	}
	
	

}

package it.unisa.microapp.webservice.view;

import it.unisa.microapp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FlipperElementView extends LinearLayout 
{
	private final ViewFlipper flip;
	//private int curr;
	
	public FlipperElementView(Context context)
	{
		super(context);
		
		//curr=0;
		
		LayoutInflater inf=LayoutInflater.from(context);
		
		this.setOrientation(LinearLayout.VERTICAL);
		
		this.addView(inf.inflate(R.layout.sliderlayout, null));
		
		flip=(ViewFlipper) this.findViewById(R.id.slider_flipp);
		
		Button b=(Button) this.findViewById(R.id.slider_butt_next);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				TextView txt=(TextView) FlipperElementView.this.findViewById(R.id.slider_txt);
				
				txt.setText(flip.getCurrentView().getId()+"/"+flip.getChildCount());
				
				flip.setInAnimation(inFromLeft());
				flip.setOutAnimation(outToRight());
				
				flip.showNext();
			}
			
		});
		
		b=(Button) this.findViewById(R.id.slider_butt_prev);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				TextView txt=(TextView) FlipperElementView.this.findViewById(R.id.slider_txt);
				
				txt.setText(flip.getCurrentView().getId()+"/"+flip.getChildCount());
				
				flip.setInAnimation(inFromRight());
				flip.setOutAnimation(outToLeft());
				
				flip.showPrevious();
			}
			
		});
	}
	
	public void addElementView(View v)
	{
		flip.addView(v);
	}
	
	private Animation outToLeft() 
	{
		Animation anim=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,-1.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f);
		
		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());
		
		return anim;
	}

	private Animation inFromRight() 
	{
		Animation anim=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,1.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f);
		
		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());
		
		return anim;
	}

	private Animation outToRight() 
	{
		Animation anim=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,1.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f);
		
		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());
		
		return anim;
	}

	private Animation inFromLeft() 
	{
		Animation anim=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,-1.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f,Animation.RELATIVE_TO_PARENT,0.0f);
		
		anim.setDuration(350);
		anim.setInterpolator(new AccelerateInterpolator());
		
		return anim;
	}
}

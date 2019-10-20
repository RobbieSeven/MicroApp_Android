package it.unisa.microapp.webservice.view;

import it.unisa.microapp.R;
import it.unisa.microapp.webservice.entry.MAEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FlipperElementView2 extends LinearLayout 
{
	private List<MAEntry<String,Object>> list;
	private ViewFlipper flip;
	private View body;
	private MACursor<MAEntry<String,Object>> cursor;
	private Element elem;
	private static String max="<font color='#dcdcdc'><b>";
	private int curr;
	
	public FlipperElementView2(Context con, List<MAEntry<String, Object>> ll, Element e2)
	{
		super(con);
		LayoutInflater inf=LayoutInflater.from(con);
		
		this.setOrientation(LinearLayout.VERTICAL);
		
		list=new LinkedList<MAEntry<String,Object>>();
		
		elem=e2;
		
		for(int i=0;i<ll.size();i++)
		{
			MAEntry<String,Object> e=ll.get(i);
			list.add(new MAEntry<String,Object>(e.getKey(),e.getValue()));
			
		}
		
		cursor=new MACursor<MAEntry<String,Object>>(list);
		
		this.addView(inf.inflate(R.layout.sliderlayout, null));
		
		flip=(ViewFlipper) this.findViewById(R.id.slider_flipp);
		
		//creo la view da mostrare
		MAEntry<String,Object> entry=cursor.next();
		List<MAEntry<String,Object>> l=new ArrayList<MAEntry<String,Object>>();
		l.add(entry);
		
		GUICreator creator=new GUICreator(this.getContext());
		creator.setIsflip(true);
		body=creator.createGUIForResponse(elem, l);
		
		final TextView txt=(TextView) FlipperElementView2.this.findViewById(R.id.slider_txt);
		
		curr=cursor.getcursor()+1;
		txt.setText(Html.fromHtml(""+curr+"/"+max+list.size()+"</b></font>"));
		
		
		Button b=(Button) this.findViewById(R.id.slider_butt_next);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				
				flip.setInAnimation(inFromRight());
				flip.setOutAnimation(outToLeft());
				
				next();
				
				flip.showNext();
				
				curr=cursor.getcursor()+1;
				txt.setText(Html.fromHtml(""+curr+"/"+max+list.size()+"</b></font>"));
				
			}
			
		});
		
		b=(Button) this.findViewById(R.id.slider_butt_prev);
		
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{

				flip.setInAnimation(inFromLeft());
				flip.setOutAnimation(outToRight());
				
				
				previous();
				
				flip.showPrevious();
				
				curr=cursor.getcursor()+1;
				txt.setText(Html.fromHtml(""+curr+"/"+max+list.size()+"</b></font>"));
				
			}
			
		});
		
		flip.addView(body);
	}
	
	public void setView(View v)
	{
		body=v;
		flip.addView(body);
	}
	
	protected void previous() 
	{
		MAEntry<String,Object> entry=cursor.previous();
		
		List<MAEntry<String,Object>> l=new ArrayList<MAEntry<String,Object>>();
		l.add(entry);
		
		GUICreator creator=new GUICreator(this.getContext());
		creator.setIsflip(true);
		
		body=creator.createGUIForResponse(elem, l);
		
		flip.removeAllViews();
		flip.addView(body);
	}

	protected void next() 
	{
		MAEntry<String,Object> entry=cursor.next();
		
		List<MAEntry<String,Object>> l=new ArrayList<MAEntry<String,Object>>();
		l.add(entry);
		
		GUICreator creator=new GUICreator(this.getContext());
		creator.setIsflip(true);
		
		body=creator.createGUIForResponse(elem, l);
		
		flip.removeAllViews();
		flip.addView(body);
	}
	
	public void addEntry(MAEntry<String,Object> e)
	{
		list.add(new MAEntry<String,Object>(e.getKey(),e.getValue()));
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

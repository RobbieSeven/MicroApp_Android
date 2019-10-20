package it.unisa.microapp.webservice.view;

import it.unisa.microapp.webservice.entry.MAEntry;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.content.Context;
public class ArrayElementView extends ListElementView 
{
	private int max;
	public ArrayElementView(Context context)
	{
		super(context);
	}
	
	public ArrayElementView(Context context, List<Element> l, String max)
	{
		super(context,l);
		if(!max.equals("unbounded"))
			this.max=Integer.parseInt(max);
		else
			this.max=-1;//valore unbounded
	}
	
	protected void updateValues(Object r)
	{
		//se l'elemento ha il valore maxOccurs = unbounded aggiungi sempre
		if(max == -1)
		{
			this.objects.add(new MAEntry<String,Object>(tag,r));
			this.ls.removeAllViews();
			for(int i=0;i<objects.size();i++)
				ls.addView(this.makechildView(i));
			ls.invalidate();
		}
		else
		{
			//se il numero corrente di elementi e minore di max allora aggiungi
			if(this.objects.size() < max)
			{
				this.objects.add(new MAEntry<String,Object>(tag,r));
				this.ls.removeAllViews();
				for(int i=0;i<objects.size();i++)
					ls.addView(this.makechildView(i));
				ls.invalidate();
			}
		}
	}

	@Override
	public Object getGUIValues() 
	{
		//TODO:da aggiustare
		if(simple)
			return objects.toArray();
		else
		{
			List<Object> l=new ArrayList<Object>();
			
			for(int i=0;i<objects.size();i++)
			{
				Object[] obj=(Object[])objects.get(i).getValue();
				l.add(obj[0]);
			}
			
			return l.toArray();
		}
	}
	

}

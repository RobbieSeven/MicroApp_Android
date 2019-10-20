package it.unisa.microapp.webservice.view;

import it.unisa.microapp.webservice.entry.MAEntry;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class ChoiceView extends ComplexView
{
	private Context con;
	private int max;
	private List<Element> elemlist;
	private boolean exclusive;
	private AlertDialog dialog;
	private int count=0;
	private ArrayList<MAEntry<String,Object>> values;
	private ScrollView scroll;
	private int idSelect;
	
	public ChoiceView(Context context) 
	{
		super(context);
	}

	public ChoiceView(Context con, List<Element> elemlist, String max) 
	{
		super(con);
		this.con=con;
		this.elemlist=elemlist;
		
		if(!max.equals("unbounded"))
		{
			this.max=Integer.parseInt(max);
			//controllo se il valore di max e maggiore di 1 per gestire inserimento eclusivo o inclusivo
			if(this.max > 1)
				exclusive=false;
			else
				exclusive=true;
		}
		else
			this.max=-1;//unbounded
		
		dialog=makeDialog();
		TextView txt=new TextView(con);
		
		values=new ArrayList<MAEntry<String,Object>>(elemlist.size());
		
		if(exclusive)
		{
			
			txt.setText("choose one between ");
			for(int i=0;i<elemlist.size();i++)
			{
				String name=elemlist.get(i).getAttribute("name");
				txt.append(name+" ");
			}
			this.addView(txt);
			
		}
		else
		{
			txt.setText("choose one or more (max ");
			
			if(this.max == -1)
				txt.append(""+elemlist.size()+") between ");
			else
				txt.append(""+max+") between ");
			for(int i=0;i<elemlist.size();i++)
			{
				String name=elemlist.get(i).getAttribute("name");
				txt.append(name+" ");
				values.add(null);
			}
			this.addView(txt);
			
		}
		
		makeGUI();
	}

	private AlertDialog makeDialog() 
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(con);
		
		builder.setTitle("insert new element");
		scroll=new ScrollView(con);
		
		builder.setView(scroll);
		
		DialogListener listener=new DialogListener(scroll);
		
		builder.setPositiveButton("Add", listener);
		builder.setNegativeButton("Cancell", listener);
		
		return builder.create();
	}

	private void makeGUI() 
	{
		if(exclusive)
			makeExclusiveGui();
		else
			makeInclusiveGui();
	}

	private void makeInclusiveGui() 
	{
		for(int i=0;i<elemlist.size();i++)
		{
			Element e=elemlist.get(i);
			CheckBox c=new CheckBox(con);
			c.setText(e.getAttribute("name"));
			c.setTag(e.getAttribute("name"));
			c.setId(i);
			
			c.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) 
				{
					CheckBox cx=(CheckBox)v;
					
					if(cx.isChecked())
					{	
						count++;
					
						if((max != -1) && (count == max))
							setUnSelected();
						
						makeViewForDial(elemlist.get(v.getId()));
						idSelect=cx.getId();
						dialog.show();
						
					}
					else
					{
						count--;
						if((max != -1) && (count < max))
							setSelected();
						
						//TODO:eliminare valore da lista di ritorno
						((ArrayList<MAEntry<String,Object>>)values).set(cx.getId(),null);
					}
				}

				private void setSelected() 
				{
					for(Element e : elemlist)
					{
						CheckBox v=(CheckBox) ChoiceView.this.findViewWithTag(e.getAttribute("name"));
						
						if(!v.isChecked())
							v.setEnabled(true);
					}
				}

				private void setUnSelected() 
				{
					for(Element e : elemlist)
					{
						CheckBox v=(CheckBox) ChoiceView.this.findViewWithTag(e.getAttribute("name"));
						
						if(!v.isChecked())
							v.setEnabled(false);
					}
				}
				
			});
			
			this.addView(c);
		}
	}

	private void makeExclusiveGui() 
	{
		RadioGroup group=new RadioGroup(con);
		
		for(int i=0;i<elemlist.size();i++)
		{
			Element e=elemlist.get(i);
			RadioButton c=new RadioButton(con);
			c.setText(e.getAttribute("name"));
			c.setTag(e.getAttribute("name"));
			c.setId(i);
			
			c.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) 
				{
					makeViewForDial(elemlist.get(v.getId()));
					dialog.show();
				}
			});
			
			if(i == 0)
				c.setSelected(true);
			
			group.addView(c);
		}
		
		this.addView(group);
	}
	
	private void makeViewForDial(Element e) 
	{
		GUICreator creator=new GUICreator(con);
		
		if(scroll.getChildCount() != 0)
			scroll.removeAllViews();
		
		String type=e.getAttribute("type");
		
		if(!type.isEmpty())
		{
			View v=creator.getType(e, type);
			scroll.addView(v);
			v.setId(0);
			v.setTag(e.getAttribute("name"));
		}
		else
		{
			View v=creator.createGUIForRequest(e);
			scroll.addView(v);
			v.setId(0);
			v.setTag(e.getAttribute("name"));
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateValues(Object r, Object tag) 
	{
		
		if(exclusive)
		{
			if(values.size() == 0)
			{
				if(r instanceof Object[])
					values.add((MAEntry<String, Object>) ((Object[])r)[0]);
				else
					values.add(new MAEntry<String,Object>((String) tag,r));
			}
			else
			{
				if(r instanceof Object[])
					values.set(0,(MAEntry<String, Object>) ((Object[])r)[0]);
				else
					values.set(0,new MAEntry<String,Object>((String) tag,r));
			}
		}
		else
		{
			if(r instanceof Object[])
			{
				values.set(idSelect,(MAEntry<String, Object>) ((Object[])r)[0]);
			}
			else
			{
				values.set(idSelect,new MAEntry<String,Object>((String) tag,r));
			}
		}
	}

	@Override
	public Object getGUIValues() 
	{
		if(exclusive)
			return values.toArray();
		else
		{
			ArrayList<MAEntry<String,Object>> array=new ArrayList<MAEntry<String, Object>>();
			for(int i=0;i<values.size();i++)
			{
				if(values.get(i) != null)
					array.add(values.get(i));
			}
			return array.toArray();
		}
	}
	
	private class DialogListener implements DialogInterface.OnClickListener
	{
		private View dial;
		private Object ret;
		
		
		public DialogListener(View v)
		{
			dial=v;
		}

		@Override
		public void onClick(DialogInterface dialog, int buttid) 
		{
			if(buttid == DialogInterface.BUTTON_POSITIVE)
			{
				getValueFromGui();
			}
			else
				ret=null;
		}
		
		private void getValueFromGui()
		{
			View v=dial.findViewById(0);
			
			if(v instanceof ComplexView)
			{
				ret=((ComplexView)v).getGUIValues();
			}
			else
			{
				ret=getGuiValue(v);
			}
			
			updateValues(ret,v.getTag());
		}

		

		private Object getGuiValue(View childAt) 
		{
			if(childAt instanceof EditText)
				return ""+((EditText)childAt).getText().toString();
			
			if(childAt instanceof Spinner)
				return ""+((Spinner)childAt).getSelectedItem().toString();
			
			if(childAt instanceof DatePicker)
			{
				DatePicker d=(DatePicker)childAt;
				String date=d.getYear()+"-"+(d.getMonth()+1)+"-"+d.getDayOfMonth();
				return ""+date;
			}
			
			if(childAt instanceof TimePicker)
			{
				TimePicker t=(TimePicker)childAt;
				String time=t.getCurrentHour()+":"+t.getCurrentMinute()+":00";
				return ""+time;
			}
			
			if(childAt instanceof CheckBox)
			{
				CheckBox c=(CheckBox)childAt;
				String bool;
				if(c.isChecked())
					bool="true";
				else
					bool="false";
				
				return ""+bool;
			}
			
			return null;
		}
		
	}

}

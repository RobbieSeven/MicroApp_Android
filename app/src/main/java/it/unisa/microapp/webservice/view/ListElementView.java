package it.unisa.microapp.webservice.view;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class ListElementView extends ComplexView
{
	private Context con;
	private LayoutInflater inflate;
	private Button plus;
	//private ListView list;
	private AlertDialog dial;
	protected String tag;
	private int count=1;
	protected List<MAEntry<String,Object>> objects;
	protected LinearLayout ls;
	private List<Element> childlist;
	protected boolean simple;
	
	public ListElementView(Context context) 
	{
		super(context);
		
	}
	
	public ListElementView(Context context, List<Element> l) 
	{
		super(context);
		//childlist=l;
		con=context;
		inflate=LayoutInflater.from(context);
		
		this.addView(inflate.inflate(R.layout.listelementlayout, null));
		
		plus=(Button) this.findViewById(R.id.listbutt);
		
		objects=new ArrayList<MAEntry<String,Object>>();
		ls=(LinearLayout) this.findViewById(R.id.listlist);
		
		ls.setOrientation(LinearLayout.VERTICAL);
		
		
		
		childlist=l;
		dial=(AlertDialog) createDialog();
		
		
		plus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				dial.show();
			}
			
		});
	}

	private Dialog createDialog() 
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(con);
		
		builder.setTitle("insert new element");
		ScrollView scroll=new ScrollView(con);
		
		builder.setView(scroll);
		
		View v=makeView();
		
		if(v != null)
		{
			v.setId(0);
			scroll.addView(v);
		}
		
		DialogListener listener=new DialogListener(scroll);
		
		builder.setPositiveButton(R.string.add, listener);
		builder.setNegativeButton(R.string.cancel, listener);
		
		return builder.create();
	}

	protected View makeView() 
	{
		GUICreator creator=new GUICreator(con);
		for(Element e : childlist)
		{
			Utils.debug("elemtype:"+e.getAttribute("type"));
			//caso 1 elemento senza figli
			if(!e.getAttribute("type").isEmpty())
			{
				View v=creator.getType(e, e.getAttribute("type"));
				tag=((Element) e).getAttribute("name");
				v.setTag(((Element) e).getAttribute("name"));
				simple=true;
				return v;
			}
			//caso 2 elemento con figli
			else
			{
				View v=creator.createGUIForRequest(e);
				tag=((Element) e).getAttribute("name");
				v.setTag(e.getAttribute("name"));
				simple=false;
				return v;
			}
		}
		return null;
	}
/*
	private View getViewType(Element e) 
	{
		String type=e.getAttribute("type");
		int ind=type.indexOf(":");
		String s=type.substring(ind+1);
		if(s.equals("date"))
		{
			return new DatePicker(con);
		}
		else if(s.equals("time"))
		{
			return new TimePicker(con);
		}
		else if(s.equals("boolean"))
		{
			CheckBox c=new CheckBox(con);
			String name=((Element)e).getAttribute("name");
			c.setText(name);
			return c;
		}
		else
		{
			return new EditText(con);
		}
	}
	*/
	
	protected void updateValues(Object r) 
	{
		String val=(String) r;
		
		if(!val.isEmpty())
		{
			objects.add(new MAEntry<String,Object>(tag+"#"+(count++),r));
			ls.removeAllViews();
			for(int i=0;i<objects.size();i++)
				ls.addView(makechildView(i));
			ls.invalidate();
		}
	}

	protected View makechildView(int position) 
	{
		View row=inflate.inflate(R.layout.childlayout, null,false);
		final MAEntry<String,Object> entry=objects.get(position);
		
		TextView name=(TextView) row.findViewById(R.id.childname);
		
		name.setText(entry.getKey());
		
		TextView desc=(TextView) row.findViewById(R.id.childdesc);
		
		if(entry.getValue() instanceof String)
			desc.setText(entry.getValue().toString());
		else
			desc.setText(entry.getKey()+"#"+count);
		
		Button delete=(Button) row.findViewById(R.id.childbutt);
		
		delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				objects.remove(entry);
				ls.removeAllViews();
				for(int i=0;i<objects.size();i++)
					ls.addView(makechildView(i));
				ls.invalidate();
			}
			
		});
		return row;
	}

	@Override
	public Object getGUIValues() 
	{
		StringBuffer buff=new StringBuffer();
		
		List<MAEntry<String,Object>> l=objects;
		
		if(l.size() > 0)
		{
			for(int i=0;i<l.size()-1;i++)
			{
				buff.append(""+l.get(i).getValue()+" ");
			}
		
			buff.append(""+l.get(l.size()-1).getValue());
		}
		return buff.toString();
	}
	
	private class DialogListener implements DialogInterface.OnClickListener
	{
		private Object ret;
		private View dialog;
		
		public DialogListener(View d)
		{
			dialog=d;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int buttid) 
		{
			if(buttid == DialogInterface.BUTTON_POSITIVE)
			{
				getValuefromView();
				updateValues(ret);
			}
			else
				ret=null;
		}

		private void getValuefromView() 
		{
			
			View v=dialog.findViewById(0);
			
			if(v instanceof ComplexView)
			{
				ret=((ComplexView) v).getGUIValues();
			}
			else
				ret=getViewValue(v);
			
		}

		private Object getViewValue(View childAt) 
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

	public void addElemeList(List<Element> list) 
	{
		childlist=list;
	}

}

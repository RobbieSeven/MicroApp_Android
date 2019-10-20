package it.unisa.microapp.webservice.view;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressLint("SetJavaScriptEnabled")
public class GUICreator 
{
	private Context con;
	private int curr;
	private boolean isflip;
	private boolean first;
	
	public GUICreator(Context con)
	{
		this.con=con;
		isflip=false;
		first=true;
	}
	
	public View createGUIForRequest(Element e)
	{
		ComplexView comp = new LinearView(con);
		
		preorderTraversal(e,comp);
		return comp;
	}

	private void preorderTraversal(Element e, ComplexView comp) 
	{
		String name=e.getAttribute("name");
		String type=e.getAttribute("type");
		String layout=e.getAttribute("layout");
		String max=e.getAttribute("max");
		
		//caso 1: l'elemento e un nodo foglia
		if(!type.isEmpty())
		{
			if(!type.contains("boolean"))
			{
				TextView txt=new TextView(con);
				txt.setText(name);
								
				//txt.setBackgroundDrawable(con.getResources().getDrawable(R.drawable.gradientback));
				txt.setBackground(con.getResources().getDrawable(R.drawable.gradientback));
				
				txt.setTextColor(Color.WHITE);
				txt.setPadding(3, 3, 3, 3);
				comp.addView(txt);
			}
			
			//caso 1.1: elemento con max > 1 necessita oggetto ArrayElement
			if(!max.isEmpty() && !max.equals("1"))
			{
				List<Element> elemlist=new LinkedList<Element>();
				elemlist.add(e);
				ArrayElementView a=new ArrayElementView(con,elemlist,max);
				a.setPadding(0, 10, 0, 10);
				comp.addElement(a, name);				
			}
			//l'elemento e un elemento semplice allora creo la corrispondente GUI
			else
			{
				View v=getType(e,type);
				//v.setPadding(0, 10, 0, 10);
				comp.addElement(v, name);
			}
		}
		
		//TODO:caso 2 :l'elemento e un nodo interno
		if(e.hasChildNodes())
		{
			TextView txt=new TextView(con);
			txt.setText(name);
			//txt.setBackgroundDrawable(con.getResources().getDrawable(R.drawable.gradientback));
			txt.setBackground(con.getResources().getDrawable(R.drawable.gradientback));
			txt.setTextColor(Color.WHITE);
			txt.setPadding(3, 3, 3, 3);
			
			comp.addElement(txt, name);
			
			//TODO:caso 2.0 elemento complesso con max=unbounded
			if(e.getAttribute("max").equals("unbounded"))
			{
				List<Element> elemlist=new LinkedList<Element>();
				elemlist.add(e);
				e.removeAttribute("max");
				ArrayElementView a=new ArrayElementView(con,elemlist,"unbounded");
				a.setPadding(0, 10, 0, 10);
				comp.addElement(a, name);
			}
			
			//caso 2.1 elemento contenente tag enumeration
			else if(layout.equals("spinner"))
			{
				gestisciSpinner(e,comp);
			}
			//caso 2.2 elemento contenente tag list
			else if(layout.equals("list"))
			{	
				ListElementView l=gestisciList(e);
				l.setPadding(0, 10, 0, 10);
				comp.addElement(l, name);
			}
			//caso 2.3 elemento soap-array
			else if(layout.equals("array"))
			{
				//TODO : possibili bug 				
				ArrayElementView a=gestisciSoapArray(e);
				
				comp.addView(a);
				
			}
			//caso 2.4 elemento matrice
			else if(layout.equals("matrix"))
			{
				//TODO: incompleto
			}
			//caso 2.5 elemento con choice
			else if(layout.equals("choice"))
			{
				Utils.verbose(e.getAttribute("name:"));
				ChoiceView c=gestisciChoice(e,max);
				c.setPadding(0, 10, 0, 10);
				comp.addElement(c, name);
			}
			//caso 2.6 elemento contenente altri
			else
			{
				LinearView l=new LinearView(con);
				
				gestisciLinear(e,l);
				l.setPadding(0, 10, 0, 10);
				comp.addElement(l, name);
			}
		}
	}

	private ArrayElementView gestisciSoapArray(Element e) 
	{
		NodeList l=e.getChildNodes();
		List<Element> elem=new LinkedList<Element>();
		for(int i=0;i<l.getLength();i++)
		{
			if(l.item(i) instanceof Element)
				elem.add((Element) l.item(i));
		}
		
		
		return new ArrayElementView(con,elem,"unbounded");
	}

	private ChoiceView gestisciChoice(Element e, String max) 
	{
		NodeList list=e.getChildNodes();
		
		List<Element> elemlist=new LinkedList<Element>();
		for(int i=0;i<list.getLength();i++)
		{
			Node el=list.item(i);
			
			if(el instanceof Element)
			{
				Utils.debug(e.getAttribute("name"));
				elemlist.add((Element) el);
			}
		}
		ChoiceView c;
		if(!max.isEmpty())
			c=new ChoiceView(con,elemlist,max);
		else
			c=new ChoiceView(con,elemlist,"1");
		
		return c;
	}

	private ListElementView gestisciList(Element e) 
	{
		NodeList list=e.getChildNodes();
		
		List<Element> elemlist=new LinkedList<Element>();
		for(int i=0;i<list.getLength();i++)
		{
			Node el=list.item(i);
			
			if(el instanceof Element)
			{
				elemlist.add((Element) el);
			}
		}
		
		return new ListElementView(con,elemlist);
	}

	private void gestisciLinear(Element e, LinearView l) 
	{
		NodeList ls=e.getChildNodes();
		
		for(int i=0;i<ls.getLength();i++)
		{
			Node el=ls.item(i);
			if(el instanceof Element)
			{
				preorderTraversal((Element) el,l);
			}
		}
	}

	private void gestisciSpinner(Element e, ComplexView l) 
	{
		Spinner sp=new Spinner(l.getContext());
		
		NodeList ls=e.getChildNodes();
		
		List<String> lsa=new LinkedList<String>();
		for(int i=0;i<ls.getLength();i++)
		{
			if(ls.item(i) instanceof Element)
			{
				String type=((Element)ls.item(i)).getAttribute("name");
				lsa.add(type);
			}
		}
		ArrayAdapter<String> a=new ArrayAdapter<String>(con, android.R.layout.simple_spinner_dropdown_item,lsa);
		sp.setAdapter(a);
		String name=((Element)e).getAttribute("name");
		l.addElement(sp,name);
	}

	public View getType(Element e, String type) 
	{
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
	
	
	private class HttpImageGetter implements ImageGetter
	{

		@Override
		public Drawable getDrawable(String source) 
		{
			try {
			      URL url = new URL(source);
			      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			      conn.setDoInput(true);
			      conn.connect();
			      InputStream is = conn.getInputStream();
			      BitmapDrawable dr = new BitmapDrawable(con.getResources(), BitmapFactory.decodeStream(is));
			      dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
			      conn.disconnect();
			      return dr;
			   } catch (IOException e) {
					Utils.error(e.getMessage(), e);
					return null;

			   }
		}
		
	}
	
	public View createGUIForResponse(Element e,List<MAEntry<String,Object>> list)
	{
		LinearLayout comp=new LinearLayout(con);
		curr=0;
		comp.setOrientation(LinearLayout.VERTICAL);
		//Iterator<MAEntry<String,Object>> it=list.iterator();
		if(list.size() > 0)
			preoderTraversal(e,comp,list,0);
		else
		{
			TextView txt=new TextView(con);
			txt.setText("No response from server");
			comp.addView(txt);
		}
		
		return comp;
	}
	

	public void setIsflip(boolean isflip) 
	{
		this.isflip = isflip;
	}

	@SuppressWarnings("unchecked")
	private void preoderTraversal(Element e,LinearLayout comp, List<MAEntry<String, Object>> list, int curr) 
	{
		String name=e.getAttribute("name");
		String type=e.getAttribute("type");
		String max=e.getAttribute("max");
		
		MAEntry<String,Object> entry=list.get(curr);
				
		//TODO:caso 1 elemento foglia
		if(!type.isEmpty() && (entry.getValue() instanceof String))
		{
			TextView txt=new TextView(con);
			txt.setText(name);
			//txt.setBackgroundDrawable(con.getResources().getDrawable(R.drawable.gradientback));
			txt.setBackground(con.getResources().getDrawable(R.drawable.gradientback));
			txt.setTextColor(Color.WHITE);
			txt.setPadding(3, 3, 3, 3);
			comp.addView(txt);
			
			if(!max.isEmpty() && !max.equals("1"))
			{
				gestisciListaResponse(e,list,comp,curr);
				curr=this.curr;
				e.removeAttribute("max");
			}
			else
			{
				View v=getResponseViewType(name,type,(String) entry.getValue());
				v.setTag(name);
				comp.addView(v);
			}
		}
		//TODO:caso 2 elemento con figli
		else
		{
			if(!isflip)
			{
				TextView txt=new TextView(con);
				txt.setText(name);
				//txt.setBackgroundDrawable(con.getResources().getDrawable(R.drawable.gradientback));
				txt.setBackground(con.getResources().getDrawable(R.drawable.gradientback));
				txt.setTextColor(Color.WHITE);
				txt.setPadding(3, 3, 3, 3);
				comp.addView(txt);
			}
			//TODO:caso 2.1 lista di elementi complessi
			if(!max.isEmpty() && !max.equals("1"))
			{
				isflip=true;
				//TODO: da gestire
				gestisciListaComplex2(e,list,comp,curr);
				curr=this.curr;
				isflip=false;
			}
			//TODO:caso 2.2 elemento complesso
			else
			{
				if(entry.getValue() instanceof List)
				{
					List<MAEntry<String,Object>> ii=(List<MAEntry<String, Object>>) entry.getValue();
				
					NodeList l=e.getChildNodes();
				
					LinearLayout lin=new LinearLayout(con);
					lin.setOrientation(LinearLayout.VERTICAL);
				
					for(int i=0;i<l.getLength();i++)
					{
						if(l.item(i) instanceof Element)
						{
							Element el=(Element)l.item(i);
							preoderTraversal(el,lin,ii,i);
							curr++;
						}
					}
					if(!isflip || !first)
					{
						ExpandableView exp=new ExpandableView(con,name,lin);
						exp.setTag(name);
						//exp.setShow(true);
						comp.addView(exp);
					}
					else
					{
						lin.setTag(name);
						comp.addView(lin);
					}
				}
				
			}
		}
	}

	

	private void gestisciListaComplex2(Element e,List<MAEntry<String, Object>> list, LinearLayout comp, int curr) 
	{
		
		e.removeAttribute("max");
		List<MAEntry<String,Object>> ll=new LinkedList<MAEntry<String,Object>>();
		
		for(int i=curr;i<list.size();i++)
		{
			MAEntry<String,Object> entry=list.get(i);
			
			if(entry.getKey().equals(e.getAttribute("name")))
			{
				ll.add(entry);
				curr++;
			}
			else
			{
				curr-=1;
				break;
			}
		}
		FlipperElementView2 flip=new FlipperElementView2(con,ll,e);
		
		ExpandableView exp=new ExpandableView(con,e.getAttribute("name"),flip);
		comp.addView(exp);
		
		this.curr=curr;
	}
/*
	private void gestisciListaComplex(Element e,List<MAEntry<String, Object>> list, final LinearLayout comp, int curr) 
	{
		FlipperElementView flip=new FlipperElementView(con);
		ExpandableView exp=new ExpandableView(con,e.getAttribute("name"),flip);
		comp.addView(exp);
		
		e.removeAttribute("max");
		
		for(int i=curr;i<list.size();i++)
		{
			MAEntry<String,Object> entry=list.get(i);
			
			if(entry.getKey().equals(e.getAttribute("name")))
			{
				List<MAEntry<String,Object>> ll=new LinkedList<MAEntry<String,Object>>();
				ll.add(entry);
				LinearLayout lin=new LinearLayout(con);
				lin.setOrientation(LinearLayout.VERTICAL);
				
				this.preoderTraversal(e, lin, ll, 0);
				
				flip.addElementView(lin);
				
				curr++;
			}
			else
			{
				curr-=1;
				break;
			}
		}
		
		this.curr=curr;
	}
*/
	private void gestisciListaResponse(Element e,List<MAEntry<String, Object>> list, LinearLayout comp, int curr) 
	{
		boolean isimg=false;
		String type=e.getAttribute("type");
		
		List<MAEntry<String,Object>> imglist=new LinkedList<MAEntry<String,Object>>();
		
		LinearLayout l=new LinearLayout(con);
		l.setOrientation(LinearLayout.VERTICAL);
		
		for(int i=curr;i<list.size();i++)
		{
			MAEntry<String,Object> entry=list.get(i);
			
			if(entry.getKey().equals(e.getAttribute("name")))
			{
				//TODO: da controllare - modificare inserendo controllo su immagine invece di usare linearlayout usare slider 
				if(isImage((String)entry.getValue()))
					isimg=true;
				
				if(isimg)
					imglist.add(entry);
				else
					l.addView(getResponseViewType(e.getAttribute("name"),type,(String) entry.getValue()));
				
				curr++;
			}
			else
			{
				curr-=1;
				break;
			}
		}
		//TODO:da controllare
		if(isimg)
		{
			//TODO: da gestire
			FlipperElementView2 flip=new FlipperElementView2(con,imglist,e);
			comp.addView(flip);
		}
		else
		{
			//ExpandableView exp=new ExpandableView(con, ""+e.getAttribute("name")+" list", l);
			//exp.setTag(e.getAttribute("name"));
			//exp.setShow(true);
			comp.addView(l);
		}
		this.curr=curr;
	}

	private boolean isImage(String value) 
	{
		if(value.endsWith(".jpg") ||
		   value.endsWith(".jpeg")||
		   value.endsWith(".png") ||
		   value.endsWith(".gif") ||
		   value.endsWith(".bmp"))
				{
			return true;
				}
		else
			return false;
	}

	private View getResponseViewType(String name,String type, String value) 
	{
		View v=null;
		
		String val=value;
		value=value.toLowerCase(Locale.getDefault());
		
		//caso 1 controllo se value e un uri
		if(value.startsWith("http") || value.startsWith("www"))
			v=gestisciUri(name,value);
		//TODO:caso 2 controllo se value e un documento html o un tag iframe
		else if(value.startsWith("<html")  || 
				value.startsWith("<iframe")||
				value.startsWith("<?xml"))
		{
			
			WebView vb=new WebView(con);
			vb.getSettings().setJavaScriptEnabled(true);
			vb.getSettings().setBuiltInZoomControls(true);
			vb.getSettings().setPluginState(PluginState.ON);
			
			//TODO: da aggiustare
			if(value.startsWith("<?xml"))
				vb.loadData(value, "application/xhtml+xml", "utf-8");
			else
				vb.loadData(value, "text/html", "utf-8");
			
			v=new ExpandableView(con,name,vb);
			//((ExpandableView)v).setShow(true);
			v.setPadding(0, 5, 0, 5);
			v.setTag(name);
		}
		//TODO:caso 3 semplice testo
		else
		{
			v=new TextView(con);
			((TextView)v).setText(val);
			v.setPadding(0, 5, 0, 5);
			v.setTag(name);
		}
		return v;
	}

	private View gestisciUri(String name, String value) 
	{
		//controllo se l'uri si riferisce ad un'immagine
		if(value.endsWith(".jpg") ||
		   value.endsWith(".jpeg")||
		   value.endsWith(".png") ||
		   value.endsWith(".gif") ||
		   value.endsWith(".bmp"))
		{
			//TODO:controllare classe ImageView
			Utils.debug("immagine");
			ImageView img=new ImageView(con);
			//img.setImageURI(Uri.parse(value.substring(value.indexOf("http://")+7)));
			HttpImageGetter getter=new HttpImageGetter();
			img.setImageDrawable(getter.getDrawable(value));
			img.setPadding(0, 5, 0, 5);
			img.setTag(name);
			return img;
		}
		//allora e un uri generica
		else
		{
			//TODO: incompleto
			TextView txt=new TextView(con);
			//setto la maschera per il link
			txt.setText(value);
			Linkify.addLinks(txt, Linkify.ALL);
			txt.setPadding(0, 5, 0, 5);
			txt.setTextSize(TypedValue.COMPLEX_UNIT_PT,10);
			txt.setTag(name);
			return txt;
		}
	}
	
	
}

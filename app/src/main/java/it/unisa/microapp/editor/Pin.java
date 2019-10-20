package it.unisa.microapp.editor;


import java.io.Serializable;

import android.graphics.Color;

import it.unisa.microapp.data.Condition;
import it.unisa.microapp.utils.Utils;

enum PinEnumerator {
	NORMAL,
	MULTI,
	USER//freccia
}

enum PinParamEnumerator {
	CONTACT,
	STRING,
	LOCATION,
	IMAGE,
	MAIL,
	URI,	//Uri android
	OBJECT,
	VIEWER,
	CONDITION
}

public class Pin implements Serializable
{
	private static final long serialVersionUID = -375516328513491749L;
	private String aggancio;
	private String idPinAggancio;
	private PinEnumerator type;
	private PinParamEnumerator param;
	private String input = "string";
	
	private String text;
	private int color;
	private Object object;
	private OnPinClickListener listener;
	private boolean highlithed;
	
	
	public void setListener(OnPinClickListener listener)
	{
		this.listener = listener;
	}
	
	public void onPinClick()
	{
		if(listener != null)
			listener.onPinClick();
	}
		
	
	public Pin(PinEnumerator type, PinParamEnumerator param,String text, String id, String aggancio)
	{
		this(type,param,text);
		this.idPinAggancio = id;
		this.aggancio = aggancio;
	}		
		
	public Pin(PinEnumerator type, PinParamEnumerator param, String text)
	{
		this.type = type;
		this.param = param;
		this.text = text;
		this.color = associateColor(param);
		this.object = null;
		this.aggancio = null;
		this.idPinAggancio = null;
		this.highlithed = false;
	}	
	
	public void setInput(String input) {
		this.input = input;
	}	
	
	public String getInput() {
		return input;
	}	
	
	public String getIdPinAggancio() {
		return idPinAggancio;
	}

	public void setIdPinAggancio(String idPinAggancio) {
		this.idPinAggancio = idPinAggancio;
	}

	public String getAggancio() {
		return aggancio;
	}

	public void setAggancio(String aggancio) {
		this.aggancio = aggancio;
	}

	private int associateColor(PinParamEnumerator ppe)
	{
		switch(ppe)
		{
		case CONTACT: return Color.CYAN;
		case STRING: return Color.RED;
		case LOCATION: return Color.rgb(128, 0, 0);
		case IMAGE: return Color.MAGENTA;
		case MAIL: return Color.rgb(255, 201, 14);
		case URI: return Color.GREEN;
		case OBJECT: return Color.BLACK;
			case CONDITION: return  Color.GRAY;
		}

		return Color.BLACK;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
	public boolean isEmpty()
	{
		return (object==null);
	}

	
	public void setObject(Object obj)
	{
		this.object = obj;
	}

	public Object getObject()
	{
		return this.object;
	}	
	
	public PinEnumerator getType()
	{
		return type;
	}
	
	public void setType(PinEnumerator type)
	{
		this.type = type;
	}

	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	} 
	
	public PinParamEnumerator getParam()
	{
		return param;
	}

	public void setParam(PinParamEnumerator param)
	{
		this.param = param;
	}

	public boolean isHighlithed() {
		return highlithed;
	}

	public void setHighlithed(boolean highlithed) {
		this.highlithed = highlithed;
	}
	
	public String description()
	{
		String sparam = "";
		switch(this.param)
		{
			case CONTACT: sparam = "Contact";break;
			case STRING: sparam = "String";break;
			case LOCATION: sparam = "Location";break;
			case IMAGE: sparam = "Image";break;
			case MAIL: sparam = "Mail";break;
			case URI: sparam = "Uri";break;
			case OBJECT: sparam = "Object";break;
			case VIEWER: sparam = "Viewer";break;
			case CONDITION: sparam = "Condition"; break;
		}		
		return new String(text +": "+ this.type+" of type: "+ sparam);
	}
	
	public static PinParamEnumerator matchPinParam(String type)
	{
		if(type.equals("STRING"))
				type = "STRING";
		else type = "STRING";
		return PinParamEnumerator.valueOf(type);
	}
}

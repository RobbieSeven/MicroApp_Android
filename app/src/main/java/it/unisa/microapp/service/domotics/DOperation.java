package it.unisa.microapp.service.domotics;



import android.content.res.Resources;
import android.os.Parcel;

import it.unisa.microapp.editor.Grid;
import it.unisa.microapp.editor.Piece;
import it.unisa.microapp.utils.Constants;

public class DOperation extends Piece {

	private static final long serialVersionUID = 8042493841523776980L;
	private String state;
	private String link;
	private String name;
	private String type;
		
	public static final Creator<DOperation> CREATOR =
			new Creator<DOperation>() {
			public DOperation createFromParcel(Parcel in) {
			return new DOperation(in);
			}
			public DOperation[] newArray(int size) {
			return new DOperation[size];
			}
			};
	
	public DOperation(String idLogic, Resources act, String text, int x,
			int y, Grid grid) {
		super(idLogic, act, text, x, y, grid);
		name="";
		state="";
		link="";
		type="";
		allStates="visible/hidden/progress";
	}
	

	public DOperation(Parcel in) 
	{
		super(in);
	}
	
	public void readFromParcel(Parcel in)
	{
		super.readFromParcel(in);
		name=in.readString();
		state=in.readString();
		link=in.readString();
		type=in.readString();
		
	}

	
	public String getName(){ return name;}
	
	public void setName(String Name){name = Name;}
	
	
	public String getState(){ return state;}
	
	public void setState(String State){state = State;}
	
	
	public String getLink(){ return link;}
	
	public void setLink(String Link){link = Link;}
	
	
	public String getType(){ return type;}
	
	public void setType(String Type){type = Type;}
	
	
	public String showInfo()
	{
		String info = super.showInfo();
		if(Constants.__DEBUG)
			{ 
			info = info+"\n"
					+ "name:"+name+","
					+ "state:"+state+","
					+ "link:"+link+","
					+ "type:"+type;
				
			}	
		return info;
	}	
	
	public void writeToParcel(Parcel dest, int flags) 
	{
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeString(state);
		dest.writeString(link);
		dest.writeString(type);
		//dest.writeSerializable(context);
	}

}

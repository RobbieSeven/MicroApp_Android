package it.unisa.microapp.service.domotics;

import android.content.res.Resources;
import it.unisa.microapp.editor.Grid;
import it.unisa.microapp.editor.IconPiece;
import it.unisa.microapp.utils.Constants;

public class DPiece extends IconPiece {
	private static final long serialVersionUID = 7001346415006521573L;
	private String dstate;
	private String dlink;
	private String dname;
	private String dtype;

	public DPiece(String idlogic, Resources act, String text, int x, int y, Grid grid) {
		super(idlogic, act, text, x, y, grid);
	}

	public String showInfo()
	{
		String info = super.showInfo();
		if(Constants.__DEBUG)
			{ 
			info = info+"\n"
					+ "name:"+dname+","
					+ "state:"+dstate+","
					+ "link:"+dlink+","
					+ "type:"+dtype;
				
			}	
		return info;
	}	
	
	public String getName(){ return dname;}
	
	public void setName(String Name){dname = Name;}
	
	
	public String getState(){ return dstate;}
	
	public void setState(String State){dstate = State;}
	
	
	public String getLink(){ return dlink;}
	
	public void setLink(String Link){dlink = Link;}
	
	
	public String getType(){ return dtype;}
	
	public void setType(String Type){dtype = Type;}
	
}

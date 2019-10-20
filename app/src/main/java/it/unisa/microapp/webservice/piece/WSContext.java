package it.unisa.microapp.webservice.piece;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;


//web service context
/*TODO:quando viene aggiunto un nuovo contesto modificare le seguenti classi
 * 
 *LibraryParser.getContext()
 *MainPanel.writeWSContext()
 *component.xml aggiungere il tag relativo al nuovo contesto a tutte le funzioni
 *della categoria web service
 *
 *e modificare i metodi readFromParcel e writeToParcel di questa classe
**/
public class WSContext implements Serializable,Parcelable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6565247855630069242L;

	//contesto date time
	private Date date;
	
	//contesto location
	private Location location;
	
	public static final Creator<WSContext> CREATOR =
			new Creator<WSContext>() {
			public WSContext createFromParcel(Parcel in) {
			return new WSContext(in);
			}
			public WSContext[] newArray(int size) {
			return new WSContext[size];
			}
			};

	public WSContext(Parcel in) 
	{
		readFromParcel(in);
		
	}
	
	public WSContext()
	{
		Calendar c=Calendar.getInstance();
		
		date=c.getTime();
		location=new Location(LocationManager.GPS_PROVIDER);
	}
	
	public WSContext(Date d)
	{
		date=d;
	}

	private void readFromParcel(Parcel in) 
	{
		date=new Date(in.readLong());
		location=new Location(in.readString());
		location.setLatitude(in.readDouble());
		location.setLongitude(in.readDouble());
		location.setAltitude(in.readDouble());
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		//scrivo il contesto tempo su parcel
		dest.writeLong(date.getTime());
		//scrivo il contesto location su parcel
		dest.writeString(location.getProvider());
		dest.writeDouble(location.getLatitude());
		dest.writeDouble(location.getLongitude());
		dest.writeDouble(location.getAltitude());
		
		
	}
	
	public String toString()
	{
		return "Context[date_time:"+date.toString() /*toGMTString()*/+",location:"+location.toString()+"]";
	}

}

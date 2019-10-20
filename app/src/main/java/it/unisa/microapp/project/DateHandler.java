package it.unisa.microapp.project;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateHandler
{
    private int day;
    private int month;
    private int year;

    public DateHandler()
    {
        GregorianCalendar calendar = new GregorianCalendar();

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getDay()
    {
        return day;
    }

    public int getMonth()
    {
        return month;
    }

    public int getYear()
    {
        return year;
    }

    public String getDate()
    {
        this.month++;

        String date = this.getDay() + "/" + this.getMonth() + "/" + this.getYear();

        this.month--;

        return date;
    }
    
    public static long getDateInMillis(String formattedString){
    	
    	String[] data = formattedString.split("/");
    	
    	Calendar c = Calendar.getInstance();
    	int month = Integer.valueOf(data[1]);
    	month--;
    	c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(data[0]) );
    	c.set(Calendar.MONTH, month);
    	c.set(Calendar.YEAR, Integer.valueOf(data[2]));
    	
    	return c.getTimeInMillis();
    	
    }
}

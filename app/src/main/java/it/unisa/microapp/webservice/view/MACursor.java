package it.unisa.microapp.webservice.view;

import java.util.List;

public class MACursor<E> 
{
	private List<E> list;
	private int curr;
	private int size;
	
	public MACursor (List<E> l)
	{
		list=l;
		
		curr=-1;
		
		size=l.size()-1;
	}
	
	public boolean hasNext()
	{
		return (curr < size);
	}
	
	public boolean hasPrevious()
	{
		return (curr > 0);
	}
	
	public E next()
	{
		E elem;
		
		if(hasNext())
		{
			curr++;
			elem=list.get(curr);
		}
		else
		{
			curr=0;
			elem=list.get(curr);
		}
		
		return elem;
	}
	
	public E previous()
	{
		E elem;
		
		if(hasPrevious())
		{
			curr--;
			elem=list.get(curr);
		}
		else
		{
			curr=size;
			elem=list.get(size);
		}
		
		return elem;
	}
	
	public String toString()
	{
		return "cursor: curr="+curr+",size="+size;
	}
	
	public int getcursor()
	{
		return curr;
	}
}

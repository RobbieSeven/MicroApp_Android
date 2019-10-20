package it.unisa.microapp.webservice;

public class LevenshteinDistance 
{
	private static final double del=1;
	private static final double ins=1;
	private static final double sub=1;
	
	public static double getDistance1(String s1,String s2)
	{
		s1=s1.toLowerCase();
		s2=s2.toLowerCase();
		double[][] d=new double[s1.length()][s2.length()];
		
		for(int i=0;i<s1.length();i++)
			d[i][0]=i;
		
		for(int i=0;i<s2.length();i++)
			d[0][i]=i;
		
		for(int j=1;j<s2.length();j++)
			for(int i=1;i<s1.length();i++)
			{
				if(s1.charAt(i) == s2.charAt(j))
					d[i][j]=d[i-1][j-1];
				else
					d[i][j]=Math.min(d[i-1][j] + del, Math.min(d[i][j-1] + ins, d[i-1][j-1] + sub));
			}
		
		return d[s1.length()-1][s2.length()-1];
	}
	
	public static double getDistance(String s1,String s2)
	{
		if(s1 == null || s2 == null)
			throw new IllegalArgumentException();
		
		int lens1=s1.length();
		int lens2=s2.length();
		
		if(lens1 == 0)
			return lens2;
		if(lens2 == 0)
			return lens1;
		
		double[] prev=new double[lens1+1];
		double[] dist=new double[lens1+1];
		double[] tmp;
		int i,j;
		double cost;
		
		char c;
		
		for(i=0;i<=lens1;i++)
			prev[i]=i;
		
		for(j=1;j<=lens2;j++)
		{
			c=s2.charAt(j-1);
			dist[0]=j;
			
			for(i=1;i<=lens1;i++)
			{
				if(s1.charAt(i-1) == c)
					cost=0;
				else
				{
					cost=sub;
				}
				
				dist[i]=Math.min(prev[i-1]+cost, Math.min(dist[i-1]+ins, prev[i]+del));
			}
			
			tmp=prev;
			prev=dist;
			dist=tmp;
		}
		
		return prev[lens1];
	}
}

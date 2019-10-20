package it.unisa.microapp.webservice.piece;

public class WebService 
{
	private String name;
	private String description;
	private String wsdl;
	private double rating;
	
	public WebService()
	{
		name="";
		description="";
		wsdl="";
		rating=0.0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWsdl() {
		return wsdl;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public String toString()
	{
		return "name:"+name+",description:"+description+",wsdl:"+wsdl+",rating:"+rating;
	}
}

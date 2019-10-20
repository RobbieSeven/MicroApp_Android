package it.unisa.microapp.webservice.piece;

public class WSSettings 
{
	private boolean dotNet;
	private boolean implicitType;
	private int timeout;
	
	public WSSettings()
	{
		this(false,true,60000);
	}
	
	public WSSettings(boolean dotNet,boolean implicitType,int timeout)
	{
		this.dotNet=dotNet;
		this.implicitType=implicitType;
		this.timeout=timeout;
	}

	public boolean isDotNet() {
		return dotNet;
	}

	public void setDotNet(boolean dotNet) {
		this.dotNet = dotNet;
	}

	public boolean isImplicitType() {
		return implicitType;
	}

	public void setImplicitType(boolean implicitType) {
		this.implicitType = implicitType;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
}

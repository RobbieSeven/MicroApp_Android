package it.unisa.microapp.webservice.rpc;

import it.unisa.microapp.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.ksoap2.transport.ServiceConnection;

public class Http3GServiceConnection implements ServiceConnection 
{
	private HttpClient connection;
	private HttpRequestBase request;
	private URL Url;
	
	private String method;
	
	public Http3GServiceConnection(String url)
	{
		this(url,ServiceConnection.DEFAULT_TIMEOUT);
	}

	public Http3GServiceConnection(String url, int timeout) 
	{
		connection=new DefaultHttpClient();
		HttpProtocolParams.setUseExpectContinue(connection.getParams(), false);
		try {
			Url=new URL(url);
		} catch (MalformedURLException e) 
		{
			Utils.error(e);
		}
	}

	@Override
	public void connect() throws IOException 
	{
		
	}

	@Override
	public void disconnect() throws IOException 
	{
		
	}
	
	public void setRequestBody(byte[] req) throws UnsupportedEncodingException
	{
		if(method.equals("POST"))
		{
			StringEntity en=new StringEntity(new String(req));
			((HttpPost) request).setEntity(en);
		}
		
	}

	@Override
	public InputStream getErrorStream() 
	{
		
		return null;
	}

	@Override
	public String getHost() 
	{
		
		return Url.getHost();
	}

	@Override
	public String getPath() 
	{
		
		return Url.getPath();
	}

	@Override
	public int getPort() 
	{
		
		return Url.getPort();
	}

	@Override
	public List<Header> getResponseProperties() throws IOException 
	{
		List<Header> list=new LinkedList<Header>();
		
		Header[] headers=request.getAllHeaders();
		
		for(Header h : headers)
			list.add(h);
		
		return list;
	}

	@Override
	public InputStream openInputStream() throws IOException 
	{
		HttpResponse response=connection.execute(request);
		HttpEntity entity=response.getEntity();
		InputStream in=entity.getContent();
		return in;
	}

	@Override
	public OutputStream openOutputStream() throws IOException
	{
		
		return null;
	}

	@Override
	public void setRequestMethod(String method) throws IOException 
	{
		if(method.equals("POST"))
			request=new HttpPost(Url.toExternalForm());
		else if(method.equals("GET"))
			request=new HttpGet(Url.toExternalForm());
		
		this.method=method;
	}

	@Override
	public void setRequestProperty(String key, String value) throws IOException 
	{
		request.setHeader(key, value);
	}

}

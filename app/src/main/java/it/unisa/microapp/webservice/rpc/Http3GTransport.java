package it.unisa.microapp.webservice.rpc;

import it.unisa.microapp.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

public class Http3GTransport extends Transport 
{
	Http3GServiceConnection connection;
	
	
	public Http3GTransport(String url) 
	{
	    super(null, url);
	}
	
	public Http3GTransport(String url,int timeout) 
	 {
		super(url, timeout);
	 }
	
	public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        
        call(soapAction, envelope, null);
    }
	
	@Override
	public List<Header> call(String soapAction, SoapEnvelope envelope, @SuppressWarnings("rawtypes") List headers) throws IOException, XmlPullParserException 
	{
		if (soapAction == null) {
            soapAction = "\"\"";
        }

        byte[] requestData = createRequestData(envelope);
            
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;
            
        connection = (Http3GServiceConnection) getServiceConnection();
        
        connection.setRequestMethod("POST");
            
        connection.setRequestProperty("User-Agent", USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            connection.setRequestProperty("SOAPAction", soapAction);
        }

        if (envelope.version == SoapSerializationEnvelope.VER12) {
            connection.setRequestProperty("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            connection.setRequestProperty("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        connection.setRequestProperty("Connection", "close");
       
        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                connection.setRequestProperty(hp.getKey(), hp.getValue());
            }
        }
            
        InputStream is;
        List<Header> retHeaders = null;
        
        connection.setRequestBody(requestData);
        
        is=connection.openInputStream();
        retHeaders = connection.getResponseProperties();
        
        if (debug) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
                    
            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1) {
                    break;
                }
                bos.write(buf, 0, rd);
            }
                    
            bos.flush();
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new ByteArrayInputStream(buf);
        }
      
        parseResponse(envelope, is);
        return retHeaders;
	}

	public String getHost() {

        String retVal = null;
        
        try {
            retVal = new URL(url).getHost();
        } catch (MalformedURLException e) {
        	Utils.error(e);
        }
        
        return retVal;
    }
        
    public int getPort() {
        
        int retVal = -1;
        
        try {
            retVal = new URL(url).getPort();
        } catch (MalformedURLException e) {
        	Utils.error(e);
        }
        
        return retVal;
    }
        
    public String getPath() {
        
        String retVal = null;
        
        try {
            retVal = new URL(url).getPath();
        } catch (MalformedURLException e) {
        	Utils.error(e);
        }
        
        return retVal;
    }
	
	public ServiceConnection getConnection()
	{
		return connection;
	}
	
	protected ServiceConnection getServiceConnection() throws IOException
	{
		return new Http3GServiceConnection(url);
	}

}

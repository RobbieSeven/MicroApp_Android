package it.unisa.microapp.activities;

import java.util.Arrays;

import org.ksoap2.serialization.SoapObject;

import it.unisa.microapp.R;
import it.unisa.microapp.components.webservice.HillChipherComponent;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;
import it.unisa.microapp.webservice.rpc.KSoapRequest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class HillChipherActivity extends MAActivity 
{
	private HillChipherComponent component;
	private View body;
	private KSoapRequest client;
	private StringData response;
	private boolean dotNet=false;
	private boolean implicitTypes=true;
	private int time=60000;
	private AlertDialog dial;
	private ScrollView scroll;
	private boolean update;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		component=(HillChipherComponent) mycomponent;
	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.webservicebase;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		TextView title=(TextView) this.findViewById(R.id.wstitle);
		title.setText(component.getOperationName());
		
		TextView description=(TextView)this.findViewById(R.id.wsdesc);
		description.setText("Cipher a string using 2x2 Hill cipher.");
		
		body=this.getLayoutInflater().inflate(R.layout.hillcipher, null);
		
		ScrollView scroll=(ScrollView)this.findViewById(R.id.ws_scoll);
		scroll.addView(body);
		
		Button butt=(Button)this.findViewById(R.id.ws_send_butt);
		
		butt.setOnClickListener(new clickListener());
		
		Button setting=(Button) this.findViewById(R.id.settingsbutt1);
		
		setting.setOnClickListener(new clickListener());
		
		makeSettingDialog();
	}	
	
	@Override
	protected void execute() {
		

	}	
	
	@Override
	public void initInputs() 
	{
	}

	@Override
	public void beforeNext() 
	{
		component.setUpdate(update);
		
		if(client != null)
		{
			String response=((SoapObject)client.getResponse()).getPropertyAsString(0);
		
			Toast t=Toast.makeText(HillChipherActivity.this, "response:\n"+response, Toast.LENGTH_LONG);
			t.show();
		
			this.response=new StringData(mycomponent.getId(), response);
			this.application.putData(mycomponent, this.response);
		}
	}
	
	private class clickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if(v.getId() == R.id.settingsbutt1)
			{
				CheckBox c=(CheckBox) scroll.findViewById(0);
				c.setChecked(dotNet);
				c=(CheckBox) scroll.findViewById(1);
				c.setChecked(implicitTypes);
				
				EditText txt=(EditText) scroll.findViewById(2);
				
				txt.setText(""+(time/1000));
				
				dial.show();
			}
			else
			{
			Object[] params=new Object[5];
			EditText ed1=(EditText) body.findViewById(R.id.hill_text);
			EditText ed2=(EditText) body.findViewById(R.id.hill_a);
			EditText ed3=(EditText) body.findViewById(R.id.hill_b);
			EditText ed4=(EditText) body.findViewById(R.id.hill_c);
			EditText ed5=(EditText) body.findViewById(R.id.hill_d);
			
			MAEntry<String,Object> entry=new MAEntry<String,Object>("text",ed1.getText());
			params[0]=entry;
			
			entry=new MAEntry<String,Object>("a",ed2.getText());
			params[1]=entry;
			
			entry=new MAEntry<String,Object>("b",ed3.getText());
			params[2]=entry;
			
			entry=new MAEntry<String,Object>("c",ed4.getText());
			params[3]=entry;
			
			entry=new MAEntry<String,Object>("d",ed5.getText());
			params[4]=entry;
			
			Utils.verbose(Arrays.toString(params));
			
			boolean is3g=Utils.chkeckNetworkConnection3g(HillChipherActivity.this.getApplication());
			
			Utils.verbose("3g network:"+is3g);
			
			//prova invocazione
			client=new KSoapRequest(component.getWsdlURI(),component.getOperationName(),component.getTns(),component.getUri(),params,is3g);
			
			client.setDotNet(dotNet);
			client.setImplicitTypes(implicitTypes);
			client.setTimeout(time);
			
			if(client.sendRequest() < 0)
			{
				Toast t=Toast.makeText(HillChipherActivity.this, "errore durante l'invocazione\n"+client.getResponse(), Toast.LENGTH_LONG);
				t.show();
				//HillChipherActivity.this.next();
			}
			else
			{
				Toast t=Toast.makeText(HillChipherActivity.this, "Call ok", Toast.LENGTH_LONG);
				t.show();
				HillChipherActivity.this.next();
			}
			}
		}
		
	}

	public void makeSettingDialog() 
	{
		Context con=HillChipherActivity.this;
		AlertDialog.Builder builder=new AlertDialog.Builder(con);
		
		builder.setTitle("Settings");
		
		scroll=new ScrollView(con);
		
		LinearLayout lin=new LinearLayout(con);
		lin.setOrientation(LinearLayout.VERTICAL);
		
		CheckBox c=new CheckBox(con);
		c.setId(0);
		c.setText(".Net");
		c.setChecked(component.getSettings().isDotNet());
		TextView desc=new TextView(con);
		desc.setText("check if the service is an .Net-Service");
		
		lin.addView(c);
		lin.addView(desc);
		
		c=new CheckBox(con);
		c.setId(1);
		c.setText("implicit types");
		c.setChecked(component.getSettings().isImplicitType());
		desc=new TextView(con);
		desc.setText("check if you don't want that type definitions for complex types/objects are automatically generated (with type \"anyType\") in the XML-Request");
		
		lin.addView(c);
		lin.addView(desc);
		
		desc=new TextView(con);
		desc.setText("Timeout");
		desc.setPadding(0, 10, 0, 0);
		
		lin.addView(desc);
		
		EditText timeout=new EditText(con);
		timeout.setId(2);
		timeout.setInputType(InputType.TYPE_CLASS_NUMBER);
		timeout.setText(""+(component.getSettings().getTimeout()/1000));
		
		desc=new TextView(con);
		desc.setText("set the timeout for request");
		
		lin.addView(timeout);
		lin.addView(desc);
		
		scroll.addView(lin);
		
		builder.setView(scroll);
		
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				CheckBox c=(CheckBox) scroll.findViewById(0);
				dotNet=c.isChecked();
				c=(CheckBox) scroll.findViewById(1);
				implicitTypes=c.isChecked();
				
				EditText txt=(EditText) scroll.findViewById(2);
				
				time=Integer.parseInt(txt.getText().toString())*1000;
				
				update=checkUpdate();
				
				if(update)
				{
					component.getSettings().setDotNet(dotNet);
					component.getSettings().setImplicitType(implicitTypes);
					component.getSettings().setTimeout(time);
				}
			}
		});
		dial=builder.create();
		
		dotNet=component.getSettings().isDotNet();
		implicitTypes=component.getSettings().isImplicitType();
		time=component.getSettings().getTimeout();
	}
	
	private boolean checkUpdate() 
	{
		return (component.getSettings().isDotNet() != dotNet)||
			   (component.getSettings().isImplicitType() != implicitTypes)||
			   (component.getSettings().getTimeout() != time);
	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}
}

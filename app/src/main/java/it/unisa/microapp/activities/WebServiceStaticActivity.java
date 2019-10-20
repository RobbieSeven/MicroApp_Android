package it.unisa.microapp.activities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.BindingOperationImpl;

import it.unisa.microapp.R;
import it.unisa.microapp.components.webservice.WebServiceStaticComponent;
import it.unisa.microapp.data.ComplexDataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.NoElementException;
import it.unisa.microapp.webservice.TypeParser2;
import it.unisa.microapp.webservice.TypesUtils;
import it.unisa.microapp.webservice.WSDLParser;
import it.unisa.microapp.webservice.entry.MAEntry;
import it.unisa.microapp.webservice.rpc.KSoapRequest;
import it.unisa.microapp.webservice.view.ComplexView;
import it.unisa.microapp.webservice.view.ExpandableView;
import it.unisa.microapp.webservice.view.GUICreator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class WebServiceStaticActivity extends MAActivity {
	private WebServiceStaticComponent component;
	private String wsdl;
	private String operation;
	private String port;
	private String tns;
	private String endpoint;
	private String description;
	private Operation op;
	private WSDLParser parser;
	private TypeParser2 tp;
	private ScrollView scroll;
	private Element output;

	private boolean dotNet = false;
	private boolean implicitTypes = true;
	private int time = 60000;
	private AlertDialog dial;
	private ScrollView scrolldial;
	private boolean update;

	private KSoapRequest client;
	private String SOAPAction;

	protected void initialize(Bundle savedInstanceState) {
		component = (WebServiceStaticComponent) mycomponent;

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		LinearLayout lin = new LinearLayout(this);
		lin.setOrientation(LinearLayout.VERTICAL);

		return lin;
	}

	@Override
	protected void prepareView(View v) {
		if (v != null && v instanceof LinearLayout) {

			LinearLayout lin = (LinearLayout) v;

			TextView title = new TextView(this);
			TextView desc = new TextView(this);

			RelativeLayout top = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.webservice, null);

			title = (TextView) top.findViewById(R.id.ws2title);

			Button bu = (Button) top.findViewById(R.id.ws2butt);

			bu.setOnClickListener(new clickListener());

			lin.addView(top);

			ExpandableView exp = new ExpandableView(this, "description", desc);

			lin.addView(exp);

			lin.addView(this.getLayoutInflater().inflate(R.layout.webservicebase2, null));

			scroll = (ScrollView) this.findViewById(R.id.wsscrollView);
			Button butt = (Button) this.findViewById(R.id.wsbutton1);

			title.setText(operation);

			title.setTextSize(TypedValue.COMPLEX_UNIT_PT, 14);

			desc.setText(this.description);

			makeSettingDialog();

			if (op != null) {
				makeGUI();
			} else {
				Utils.errorDialog(this, "nome operazione errato");
				butt.setEnabled(false);
			}

			butt.setOnClickListener(new clickListener());

			parser = null;
			tp = null;

		}

	}

	@Override
	protected void execute() {
		

	}


	private class clickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ws2butt) {
				CheckBox c = (CheckBox) scrolldial.findViewById(0);
				c.setChecked(dotNet);
				c = (CheckBox) scrolldial.findViewById(1);
				c.setChecked(implicitTypes);

				EditText txt = (EditText) scrolldial.findViewById(2);

				txt.setText("" + (time / 1000));

				dial.show();
			} else {
				ComplexView V = (ComplexView) scroll.getChildAt(0);

				Object[] values = (Object[]) V.getGUIValues();

				boolean is3g = Utils.chkeckNetworkConnection(WebServiceStaticActivity.this.getApplication());

				Utils.verbose("3g network:" + is3g);

				if (values.length > 0) {
					MAEntry<?, ?> entry = (MAEntry<?, ?>) values[0];

					Utils.verbose(Arrays.toString((Object[]) entry.getValue()));

					client = new KSoapRequest(wsdl, operation, tns, endpoint, (Object[]) entry.getValue(), is3g);
				} else
					client = new KSoapRequest(wsdl, operation, tns, endpoint, values, is3g);

				client.setDotNet(dotNet);
				client.setImplicitTypes(implicitTypes);
				client.setTimeout(time);
				client.setSOAPAction(SOAPAction);

				if (client.sendRequest() < 0) {
					Toast t = Toast.makeText(WebServiceStaticActivity.this,
							"Error in the invocation\n" + client.getResponse(), Toast.LENGTH_LONG);
					t.show();
					Utils.verbose(client.getResponse().toString());
					next();
				} else {
					Toast t = Toast.makeText(WebServiceStaticActivity.this, "Call success", Toast.LENGTH_LONG);
					Utils.verbose(client.getResponse().toString());
					t.show();

					next();
				}
			}
		}

	}

	@Override
	public void initInputs() {
		wsdl = "http://" + this.mycomponent.getUserData("wsdlUri").iterator().next();
		operation = this.mycomponent.getUserData("operation").iterator().next();
		port = this.mycomponent.getUserData("port").iterator().next();

		description = component.getDescription();

		extractInfoFromWSDL();
	}

	@SuppressWarnings("static-access")
	private void extractInfoFromWSDL() {
		parser = new WSDLParser();

		parser.setallInfos();

		if (parser.parse(wsdl) == 0) {
			tns = parser.getTargerNameSpace();

			Port p = parser.getPort(port);

			if (p != null) {
				/*
				 * List list=p.getExtensibilityElements();
				 * 
				 * for(Object obj : list) { if(obj instanceof SOAPAddress) {
				 * SOAPAddress addr=(SOAPAddress)obj;
				 * endpoint=addr.getLocationURI(); break; } }
				 * 
				 * list=null;
				 */
				endpoint = getendPoint(p, parser);

				/*
				 * List<Operation> listop=parser.getOperationList(port);
				 * 
				 * for(Operation o : listop) { String name=o.getName();
				 * 
				 * if(name.equals(operation)) { op=o;
				 * if(op.getDocumentationElement() != null)
				 * description=op.getDocumentationElement().getTextContent();
				 * else description=""+operation+" operation";
				 * 
				 * break; } }
				 */

				SOAPAction = getSOAPAction(p);
				Utils.verbose("SOAPAction:" + SOAPAction);
			} else {
				Utils.errorDialog(this, "nome porta errato");
				// Button b=(Button)this.findViewById(R.id.ws_send_butt);
				// b.setEnabled(false);
			}
		} else {
			Utils.errorDialog(this, "uri documento wsdl errato");
			// Button b=(Button)this.findViewById(R.id.ws_send_butt);
			// b.setEnabled(false);
		}
	}

	private String getendPoint(Port p, WSDLParser parser) {

		if (p != null) {
			List<?> list = p.getExtensibilityElements();

			for (Object obj : list) {
				if (obj instanceof SOAPAddress) {
					SOAPAddress addr = (SOAPAddress) obj;
					return addr.getLocationURI();
				} else if (obj instanceof SOAP12Address) {
					SOAP12Address addr = (SOAP12Address) obj;
					return addr.getLocationURI();
				} else if (obj instanceof HTTPAddress) {
					HTTPAddress addr = (HTTPAddress) obj;
					return addr.getLocationURI();
				}
			}
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	private String getSOAPAction(Port p) {
		Binding b = null;
		BindingOperationImpl bindop = null;
		b = p.getBinding();

		if (b != null)
			bindop = (BindingOperationImpl) b.getBindingOperation(operation, null, null);
		else
			return "";

		if (bindop != null) {
			List<ExtensibilityElement> l = bindop.getExtensibilityElements();

			for (ExtensibilityElement e : l) {
				if (e instanceof SOAPOperation) {
					SOAPOperation soapOp = (SOAPOperation) e;

					return soapOp.getSoapActionURI();
				}
			}
		}
		return "";
	}

	private void makeGUI() {
		boolean builtin = false;

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// creo un nodo nel caso in cui un elemento part sia builtin
		Element root = null;

		try {
			docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			Utils.error(e.getMessage(), e);
		}

		GUICreator creator = new GUICreator(this);

		Input input = op.getInput();

		Message m = input.getMessage();

		List<?> list = (List<?>) m.getOrderedParts(null);

		for (int i = 0; i < list.size(); i++) {
			Part part = (Part) list.get(i);

			// caso 1 part non e un elemento built-in
			if (part.getElementName() != null) {
				String name = part.getElementName().getLocalPart();

				if (parser.getTypesInformation().getTypeMap() != null) {
					TypesUtils utils = new TypesUtils(parser.getTypesInformation());

					Element elem = null;

					try {
						// preprocesso il tipo eliminando le informazioni non
						// necessarie
						elem = (Element) utils.Element2Tree(name).getFirstChild();

						// aggiungo la GUI relativa al tipo dato
						scroll.addView(creator.createGUIForRequest(elem));
					} catch (NoElementException e) {
						Utils.error(e.getMessage(), e);
					}
				}
			} else {
				// TODO: possibili bug

				String name = part.getTypeName().getLocalPart();
				int ind = name.indexOf(":") + 1;

				if (parser.getTypesInformation() != null) {

					// controllo se � un elemento complesso
					if (parser.getTypesInformation().getTypeMap() != null
							&& parser.getTypesInformation().getTypeMap().get(name.substring(ind)) != null) {
						TypesUtils utils = new TypesUtils(parser.getTypesInformation());
						Element elem = null;

						try {
							// preprocesso il tipo eliminando le informazioni
							// non necessarie
							elem = (Element) utils.Element2Tree(name).getFirstChild();

							// aggiungo la GUI relativa al tipo dato
							scroll.addView(creator.createGUIForRequest(elem));
						} catch (NoElementException e) {
							Utils.error(e.getMessage(), e);
						}
					} else {
						// TODO:caso 2 allora creo un albero con nome = nome
						// operazione
						// ed aggiungo a tale albero tutti i tipi builtin
						// incontrati

						// prima volta
						if (builtin == false) {
							builtin = true;
							root = doc.createElement("element");
							root.setAttribute("name", op.getName());
						}

						Element node = doc.createElement("element");

						node.setAttribute("name", part.getName());
						node.setAttribute("type", part.getTypeName().getLocalPart());

						root.appendChild(node);
					}
				} else {
					// TODO:caso 2 allora creo un albero con nome = nome
					// operazione
					// ed aggiungo a tale albero tutti i tipi builtin incontrati

					// prima volta
					if (builtin == false) {
						builtin = true;
						root = doc.createElement("element");
						root.setAttribute("name", op.getName());
					}

					Element node = doc.createElement("element");

					node.setAttribute("name", part.getName());
					node.setAttribute("type", part.getTypeName().getLocalPart());

					root.appendChild(node);
				}

			}
		}

		if (builtin == true) {
			scroll.addView(creator.createGUIForRequest(root));
		}
		tp = parser.getTypesInformation();

		if (scroll.getChildAt(0) == null) {
			// TODO:DA TESTARE
			root = doc.createElement("element");
			root.setAttribute("name", op.getName());

			scroll.addView(creator.createGUIForRequest(root));

		}

		// se non e' un output semplice
		if (!component.isOutputSimple()) {
			// estraggo le info relative ai parametri di output
			extractOutput();
		}
	}

	private void extractOutput() {
		boolean builtin = false;

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;

		try {
			docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			Utils.error(e.getMessage(), e);
		}

		Output out = op.getOutput();

		Message m = out.getMessage();

		List<?> list = (List<?>) m.getOrderedParts(null);

		for (int i = 0; i < list.size(); i++) {
			Part part = (Part) list.get(i);

			// caso 1 part non e un elemento built-in
			if (part.getElementName() != null) {
				String name = part.getElementName().getLocalPart();

				if (tp.getTypeMap() != null) {
					TypesUtils utils = new TypesUtils(tp);

					try {
						// preprocesso il tipo eliminando le informazioni non
						// necessarie
						output = (Element) utils.Element2Tree(name).getFirstChild();

					} catch (NoElementException e) {
						Utils.error(e.getMessage(), e);
					}
				}
			} else {
				// TODO: possibili bug
				String name = part.getTypeName().getLocalPart();
				int ind = name.indexOf(":") + 1;

				if (parser.getTypesInformation() != null) {

					// controllo se � un elemento complesso
					if (parser.getTypesInformation().getTypeMap() != null
							&& parser.getTypesInformation().getTypeMap().get(name.substring(ind)) != null) {
						TypesUtils utils = new TypesUtils(parser.getTypesInformation());
						// Element elem=null;

						try {
							// preprocesso il tipo eliminando le informazioni
							// non necessarie
							output = (Element) utils.Element2Tree(name).getFirstChild();

						} catch (NoElementException e) {
							Utils.error(e.getMessage(), e);
						}
					} else {
						// TODO:caso 2 allora creo un albero con nome = nome
						// operazione
						// ed aggiungo a tale albero tutti i tipi builtin
						// incontrati

						// prima volta
						if (builtin == false) {
							builtin = true;
							output = doc.createElement("element");
							output.setAttribute("name", op.getName());
						}

						Element node = doc.createElement("element");

						node.setAttribute("name", part.getName());
						node.setAttribute("type", part.getTypeName().getLocalPart());

						output.appendChild(node);
					}
				} else {
					// TODO:caso 2 allora creo un albero con nome = nome
					// operazione
					// ed aggiungo a tale albero tutti i tipi builtin incontrati

					// prima volta
					if (builtin == false) {
						builtin = true;
						output = doc.createElement("element");
						output.setAttribute("name", op.getName());
					}

					Element node = doc.createElement("element");

					node.setAttribute("name", part.getName());
					node.setAttribute("type", part.getTypeName().getLocalPart());

					output.appendChild(node);
				}

			}
		}

		if (output == null) {
			output = doc.createElement("element");
			output.setAttribute("name", op.getName());
		}
	}

	@Override
	public void beforeNext() {
		component.setUpdate(update);

		if (client != null) {
			if (client.getResponse() instanceof SoapObject) {
				SoapObject obj = (SoapObject) client.getResponse();

				// TODO:creare nuovo tipo elemento per memorizzare valore di
				// ritorno
				LinkedList<MAEntry<String, Object>> complex = new LinkedList<MAEntry<String, Object>>();

				GenericData<?> ret = null;

				if (component.isOutputSimple()) {
					String s = obj.getPropertyAsString(0);
					ret = new StringData(mycomponent.getId(), s);
				} else {
					parseValues(obj, complex);
					ret = new ComplexDataType(mycomponent.getId(), complex);
					((ComplexDataType) ret).setComplexElement((Element) output.getFirstChild());
				}

				// extractOutput();
				/*
				 * DocumentBuilderFactory dbfac =
				 * DocumentBuilderFactory.newInstance(); DocumentBuilder
				 * docBuilder = null; Document doc=null;
				 * 
				 * try { docBuilder = dbfac.newDocumentBuilder(); doc=
				 * docBuilder.newDocument(); } catch
				 * (ParserConfigurationException e1) { }
				 * 
				 * Element elem=deepCopy(doc,(Element) output.getFirstChild());
				 */

				Utils.debug("ret:" + ret);

				application.putData(mycomponent, ret);

			} else {
				String s = (String) client.getResponse();
				LinkedList<MAEntry<String, Object>> complex = new LinkedList<MAEntry<String, Object>>();

				GenericData<?> ret = null;

				if (component.isOutputSimple()) {
					ret = new StringData(mycomponent.getId(), s);
				} else {
					complex.add(new MAEntry<String, Object>(op.getName() + "Fault", s));
					ret = new ComplexDataType(mycomponent.getId(), complex);

					DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = null;
					Document doc = null;

					try {
						docBuilder = dbfac.newDocumentBuilder();
						doc = docBuilder.newDocument();
					} catch (ParserConfigurationException e1) {
					}

					Element fault = doc.createElement("element");
					fault.setAttribute("name", op.getName() + "Fault");
					fault.setAttribute("type", "s:string");

					((ComplexDataType) ret).setComplexElement(fault);
					Utils.debug(((ComplexDataType) ret).getComplexElement().getAttribute("name"));
					// StringData ret=new StringData(mycomponent.getId(),s);
				}

				Utils.debug("ret:" + ret);

				application.putData(mycomponent, ret);

			}
		}
	}

	private void parseValues(SoapObject obj, LinkedList<MAEntry<String, Object>> complex) {
		for (int i = 0; i < obj.getPropertyCount(); i++) {
			PropertyInfo info = new PropertyInfo();
			obj.getPropertyInfo(i, info);
			complex.add(extractData(obj.getProperty(i), info.getName()));
		}
	}

	private MAEntry<String, Object> extractData(Object property, String string) {
		MAEntry<String, Object> ret = new MAEntry<String, Object>();

		ret.setKey(string);

		if (property instanceof SoapPrimitive) {
			ret.setValue(((SoapPrimitive) property).toString());
		} else {
			SoapObject obj = (SoapObject) property;

			LinkedList<MAEntry<String, Object>> values = new LinkedList<MAEntry<String, Object>>();

			parseValues(obj, values);

			ret.setValue(values);
		}

		return ret;
	}

	private void makeSettingDialog() {
		Context con = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(con);

		builder.setTitle("Settings");

		scrolldial = new ScrollView(con);

		LinearLayout lin = new LinearLayout(con);
		lin.setOrientation(LinearLayout.VERTICAL);

		CheckBox c = new CheckBox(con);
		c.setId(0);
		c.setText(".Net");
		c.setChecked(component.getSettings().isDotNet());
		TextView desc = new TextView(con);
		desc.setText("check if the service is an .Net-Service");

		lin.addView(c);
		lin.addView(desc);

		c = new CheckBox(con);
		c.setId(1);
		c.setText("implicit types");
		c.setChecked(component.getSettings().isImplicitType());
		desc = new TextView(con);
		desc.setText("check if you don't want that type definitions for complex types/objects are automatically generated (with type \"anyType\") in the XML-Request");

		lin.addView(c);
		lin.addView(desc);

		desc = new TextView(con);
		desc.setText("Timeout");
		desc.setPadding(0, 10, 0, 0);

		lin.addView(desc);

		EditText timeout = new EditText(con);
		timeout.setId(2);
		timeout.setInputType(InputType.TYPE_CLASS_NUMBER);
		timeout.setText("" + (component.getSettings().getTimeout() / 1000));

		desc = new TextView(con);
		desc.setText("set the timeout for request");

		lin.addView(timeout);
		lin.addView(desc);

		scrolldial.addView(lin);

		builder.setView(scrolldial);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				CheckBox c = (CheckBox) scrolldial.findViewById(0);
				dotNet = c.isChecked();
				c = (CheckBox) scrolldial.findViewById(1);
				implicitTypes = c.isChecked();

				EditText txt = (EditText) scrolldial.findViewById(2);

				time = Integer.parseInt(txt.getText().toString()) * 1000;

				update = checkUpdate();

				if (update) {
					component.getSettings().setDotNet(dotNet);
					component.getSettings().setImplicitType(implicitTypes);
					component.getSettings().setTimeout(time);
				}
			}
		});
		dial = builder.create();

		dotNet = component.getSettings().isDotNet();
		implicitTypes = component.getSettings().isImplicitType();
		time = component.getSettings().getTimeout();
	}

	private boolean checkUpdate() {
		return (component.getSettings().isDotNet() != dotNet)
				|| (component.getSettings().isImplicitType() != implicitTypes)
				|| (component.getSettings().getTimeout() != time);
	}
	
	@Override
	protected void resume(){
		//metodi per speech Vincenzo Savarese
	}

}

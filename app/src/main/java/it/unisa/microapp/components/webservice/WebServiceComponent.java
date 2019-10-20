package it.unisa.microapp.components.webservice;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

public class WebServiceComponent extends MAComponent {
	private String wsdlURI;
	private String serviceName;
	private String portName;
	private String operationName;
	private String tns;
	private String endpoint;
	private String description;
	private boolean update;

	private WSSettings settings;

	public WebServiceComponent(String id, String uri, String service,
			String port, String operation, String namespace, String endp,
			WSSettings s, String description) {
		super(id, "");
		wsdlURI = uri;
		serviceName = service;
		portName = port;
		operationName = operation;
		tns = namespace;
		endpoint = endp;
		this.description = description;

		settings = new WSSettings(s.isDotNet(), s.isImplicitType(),
				s.getTimeout());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WSSettings getSettings() {
		return settings;
	}

	public void setSettings(WSSettings settings) {
		this.settings = settings;
	}

	public String getWsdlURI() {
		return wsdlURI;
	}

	public void setWsdlURI(String wsdlURI) {
		this.wsdlURI = wsdlURI;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getTns() {
		return tns;
	}

	public void setTns(String tns) {
		this.tns = tns;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	protected String getLocationQName() {
		return "it.unisa.microapp.activities.WebServiceActivity";
	}

	@Override
	protected String getCompType(String id) {
		return "WEBSERVICE";
	}

	public void updateSettings(boolean settingUpdate) {
		update = settingUpdate;
	}

	public boolean isUpdate() {
		return update;
	}

}

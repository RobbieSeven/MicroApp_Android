package it.unisa.microapp;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.components.MAFirstComponent;
import it.unisa.microapp.data.DataCollection;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.exceptions.InvalidComponentException;
import it.unisa.microapp.exceptions.NoMoreSpaceException;
import it.unisa.microapp.project.ProjectTable;
import it.unisa.microapp.support.ConcreteComponentCreator;
import it.unisa.microapp.support.DeployParser;
import it.unisa.microapp.support.DepthFirstOrder;
import it.unisa.microapp.support.DirEdge;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.DirectedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.gesture.Gesture;
import android.support.multidex.MultiDex;
import android.util.Log;
//import android.support.multidex.MultiDex;

public class MicroAppGenerator extends Application {
	private int conditionActivity=2;
	private String deployPath;
	private String iconPath;
	private int iconId;
	private String description;
	private Stack<MAComponent> executing;
	private Stack<MAComponent> executed = new Stack<MAComponent>();
	private MAComponent startComponent;
	private static MAComponent defaultState;
	private MAComponent currentState = defaultState;
	private DeployParser parser = null;
	private InputStream schema;
	private Map<String, DataCollection> dataTable = new HashMap<String, DataCollection>();
	private Map<String, DataCollection> outputTable = new HashMap<String, DataCollection>();
	private ArrayList<MAComponent> componentList;
	private Set<DirEdge> edges;
	ArrayList<ArrayList<MAComponent>> listGraph;
	private Gesture gesture;
	private boolean flagGesture;
	private String gestureName="";
	private int downloaded = 0;


	static {
		defaultState = new MAFirstComponent("0","");
	}

	public MicroAppGenerator() {
		super();
		try {
			parser = new DeployParser(new ConcreteComponentCreator(), this);
		} catch (ParserConfigurationException e) {
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public  void setConditionActivity(int b){conditionActivity=b;}
	public int getConditionActivity(){return conditionActivity;}

	@Override
	protected void attachBaseContext(Context base){
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public void reset() {
		deployPath = null;
		iconPath = null;
		iconId = 0;
		description = null;
		executing = null;
		executed = new Stack<MAComponent>();
		currentState = defaultState;
		dataTable = new HashMap<String, DataCollection>();
		outputTable = new HashMap<String, DataCollection>();
	}

	public String getDescription() {
		return description;
	}

	public String getIconPath() {
		return iconPath;
	}

	
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
		this.iconId = getResources().getIdentifier(iconPath, "drawable", this.getPackageName());
	}

	public int getIconId() {
		return iconId;
	}
	
	public void setStartComponent(MAComponent startComponent) {
		this.startComponent = startComponent;
	}

	public MAComponent getStartComponent() {
		return startComponent;
	}
	
	public void setGesture(Gesture gesture) {
		this.gesture=gesture;
	}
	
	public Gesture getGesture() {
		return gesture;
	}
	
	public void setFlagGesture(boolean flag) {
		this.flagGesture=flag;
	}
	
	public boolean getFlagGesture() {
		return this.flagGesture;
	}
	
	public void setGestureName(String name) {
		this.gestureName=name;
	}
	
	public String getGestureName() {
		return this.gestureName;
	}

	public void initComponents(DirectedGraph<MAComponent, DirEdge> compGraph, MAComponent startComp){
		listGraph = new ArrayList<ArrayList<MAComponent>>();		
		edges = compGraph.edgeSet();
		for(DirEdge edge: edges)
		{
			MAComponent src = (MAComponent)edge.getSource();
			MAComponent dst = (MAComponent)edge.getTarget();
			Utils.verbose(src.getType() + " -> " + dst.getType());
			dst.setFirstComponentGraph(false);
			
			if(src.getFirstComponentGraph()){
				listGraph.add(new ArrayList<MAComponent>());
				listGraph.get(listGraph.size()-1).add(src);
			}
		}
		createListSubGraph();
		
		DepthFirstOrder<MAComponent, DirEdge> topord = new DepthFirstOrder<MAComponent, DirEdge>(compGraph, startComp);
		componentList = topord.reversePost();
			
		String sequence = "";
		for(MAComponent clist: componentList)
		{
			sequence = sequence.concat( clist.getType()+ " -> "); 
		}
		Utils.verbose(sequence);
				
		toStack(componentList.toArray(new MAComponent[componentList.size()]));
	}
		
	public void initComponents() throws InvalidComponentException,NoMoreSpaceException {

		DirectedGraph<MAComponent, DirEdge> compGraph = parser.createGraph();

		Log.d("INFO" , "sono qui" + compGraph);
		listGraph = new ArrayList<ArrayList<MAComponent>>();		
		edges = compGraph.edgeSet();
		int contaOutputCondition=0;
		for(DirEdge edge: edges)
		{

			MAComponent src = (MAComponent)edge.getSource();
			MAComponent dst = (MAComponent)edge.getTarget();
			Utils.verbose(src.getType() + " -> " + dst.getType());

			dst.setFirstComponentGraph(false);
			
			if(src.getFirstComponentGraph()){
				listGraph.add(new ArrayList<MAComponent>());
				listGraph.get(listGraph.size()-1).add(src);
			}

			//se sto nel condition e ho due componenti collegate dico che la seconda è collegata al false
			//altrimenti se ho una componente collegata, mi accerto che è collegata al secondo pin e glielo dico

			if (src.getType().equals("CONDITION") && (src.getBindings().size()==2)){
				if (contaOutputCondition==1){
					dst.setFirstFalseComponentCondition(true);
					contaOutputCondition=0;
				}
				else contaOutputCondition++;
			}
			else if (src.getType().equals("CONDITION") && (src.getBindings().size()==1)){

				if (src.getBindings(0).contains("1")){

					dst.setFirstFalseComponentCondition(true);
				}
			}
		}
		createListSubGraph();

		DepthFirstOrder<MAComponent, DirEdge> topord = new DepthFirstOrder<MAComponent, DirEdge>(compGraph, parser.getStartComp());
		componentList = topord.reversePost();
			
		String sequence = "";
		MAComponent compPrev=null;
		for(MAComponent clist: componentList)
		{
			sequence = sequence.concat( clist.getType()+ " -> ");
			if (compPrev != null){
				if (clist.getFirstFalseComponentCondition()){
					compPrev.setLastTrue(true);
				}

			}
			compPrev=clist;
		}
		Utils.verbose(sequence);
		toStack(componentList.toArray(new MAComponent[componentList.size()]));
	}
	

	private void createListSubGraph(){
		boolean change=true;	
		while(change){		
			change=false;	
				for(DirEdge edge: edges){
					MAComponent src1 = (MAComponent)edge.getSource();
					MAComponent dst1 = (MAComponent)edge.getTarget();
					for(int j=0; j <listGraph.size() ;j++){
						ArrayList<MAComponent> nList = listGraph.get(j);
						if(nList.get(nList.size()-1).equals(src1)){
							nList.add(dst1);
							change=true;
						}
					}	
				}	
		}
		
		int b=0;
		for(ArrayList<MAComponent> lcomp : listGraph){
			b++;
			for(int a=0; a<lcomp.size();a++){
				Utils.debug("a "+b +" "+ lcomp.get(a).getFirstComponentGraph() +" " + lcomp.get(a).getType() + " " +lcomp.get(a).getId() );
			}
		}

		for(int i=0; i<listGraph.size()-1 ;i++){
			ArrayList<MAComponent> prevList = listGraph.get(i);
			for(int j=i+1; j<listGraph.size() ;j++){
				ArrayList<MAComponent> nextList = listGraph.get(j);
				for(int k=0; k < prevList.size() ;k++){
					MAComponent prevListComp = prevList.get(k);
					for(int l=0; l < nextList.size() ;l++){
						MAComponent nextListComp = nextList.get(l);
						if(prevListComp.equals(nextListComp)){
							MAComponent fComp = nextList.get(0);
							fComp.setFirstComponentGraph(false);
							
						}
					}
				}
			}
		}	
	}
	
	public ArrayList<ArrayList<MAComponent>> getListGraph(){
		return listGraph;
	}
	public void setListGraph(ArrayList<ArrayList<MAComponent>> listGraph){
		this.listGraph = listGraph;
	}
	public Set<DirEdge> getEdges() {
		return edges;
	}
	public void setEdges(Set<DirEdge> edges) {
		this.edges = edges;
	}	
	
	public ArrayList<MAComponent> getComponentList() {
		return componentList;
	}

	public void toStack(MAComponent[] components) {
		executing = new Stack<MAComponent>();
		for (int i = components.length - 1; i >= 0; i--) {
			executing.push(components[i]);
		}
	}

	public MAComponent getCurrentState() {
		return currentState;
	}


	public void setDeployPath(String path) throws NullPointerException, SAXException, IOException {
		if (schema == null)
			schema = getResources().openRawResource(R.raw.deployschema);

		if (path != null) {
			ProjectTable table = new ProjectTable(getApplicationContext());
			table.open();;
			String query = " SELECT * " + " FROM " + ProjectTable.TABLE_PROJECT + " WHERE " + ProjectTable.NAME_PROJECT  + "=" + "'" + path + "';";

			Cursor c;
			c = table.db().rawQuery(query, null);
			c.moveToNext();
			iconPath = c.getString(c.getColumnIndex(ProjectTable.ICON_PROJECT));
			description = c.getString(c.getColumnIndex(ProjectTable.DESC_PROJECT));
		}
		this.deployPath = path;
	}


	public void setDeployPath(String path, boolean full) throws NullPointerException, SAXException, IOException {
		if (schema == null)
			schema = getResources().openRawResource(R.raw.deployschema);
		
		if (path != null) {
			if (full) {
					parser.setDeployFile(new File(path), schema);
					path = new File(path).getName();
			} else {
				parser.setDeployFile(new File(FileManagement.getLocalAppPath() + path), schema);
			}

			iconPath = parser.getIcon();
			Log.d("INFO", "iconpath:" + iconPath);
			description = parser.getDescription();
		}
		this.deployPath = path;
	}

	public String getDeployPath() {
		return deployPath;
	}

	public DeployParser getParser() {
		return parser;
	}
	
	public MAComponent nextStep() {
		executed.push(currentState);
		currentState.resetOutCounting();
		if (executing.isEmpty())
			return null;
		else
			currentState = executing.pop();
		return currentState;
	}

	public MAComponent prevStep() {
		try{
			executing.push(currentState);
			if (executed.isEmpty())
				currentState = defaultState;
			currentState = executed.pop();
			return currentState;
		}
		catch(Exception e){
			
		}
		return null;
		
	}

	public void putData(MAComponent sender, GenericData<?> data) {
		DataType type = data.getDataType();
		String sid = sender.getId();
		DataCollection dataColl = outputTable.get(sid);
		GenericData<?> dt;
		if (dataColl != null) {
			if(dataColl.getData(type) != null)
			{	
				dt = dataColl.getData(type).iterator().next();
				if (dt != null) {
					dt.copyData(data);
					sender.outputAdded();
					return;
				}
			}
		}

		Iterable<String> it = sender.getOutActivityId(data.getDataType());
		if (it == null)
		{	
			return;
		}	
		for (String actID : it) {
			dataColl = dataTable.get(actID);
			if (dataColl == null)
				dataColl = new DataCollection();
			dataTable.put(actID, dataColl);
			dataColl.addData(data);

			dataColl = outputTable.get(sid);
			if (dataColl == null) {
				dataColl = new DataCollection();
				outputTable.put(sid, dataColl);
			}

			dataColl.addData(data);
			sender.outputAdded();
		}
	}

	public void putDataInCondition(MAComponent sender, GenericData<?> data) {
		DataType type = data.getDataType();
		String sid = sender.getId();
		DataCollection dataColl = outputTable.get(sid);
		GenericData<?> dt;
		if (dataColl != null) {
			if(dataColl.getData(type) != null)
			{
				dt = dataColl.getData(type).iterator().next();
				if (dt != null) {
					dt.copyData(data);
					sender.outputAdded();
					return;
				}
			}
		}

		Iterable<String> it = sender.getOutActivityId(data.getDataType());
		if (it == null)
		{
			it = sender.getOutActivityId(DataType.CONDITION);
			if(it == null)
				return;
		}
		for (String actID : it) {
			dataColl = dataTable.get(actID);
			if (dataColl == null)
				dataColl = new DataCollection();
			dataTable.put(actID, dataColl);
			dataColl.addData(data);

			dataColl = outputTable.get(sid);
			if (dataColl == null) {
				dataColl = new DataCollection();
				outputTable.put(sid, dataColl);
			}

			dataColl.addData(data);
			sender.outputAdded();
		}
	}

	public void putDataInObject(MAComponent sender, GenericData<?> data) {
		DataType type = data.getDataType();
		String sid = sender.getId();
		DataCollection dataColl = outputTable.get(sid);
		GenericData<?> dt;
		if (dataColl != null) {
			if(dataColl.getData(type) != null)
			{	
				dt = dataColl.getData(type).iterator().next();
				if (dt != null) {
					dt.copyData(data);
					sender.outputAdded();
					return;
				}
			}
		}

		Iterable<String> it = sender.getOutActivityId(data.getDataType());
		if (it == null)
		{	
			//se presente un output OBJECT
			it = sender.getOutActivityId(DataType.OBJECT);
			if(it == null)
				return;
		}	
		for (String actID : it) {
			dataColl = dataTable.get(actID);
			if (dataColl == null)
				dataColl = new DataCollection();
			dataTable.put(actID, dataColl);
			dataColl.addData(data);

			dataColl = outputTable.get(sid);
			if (dataColl == null) {
				dataColl = new DataCollection();
				outputTable.put(sid, dataColl);
			}

			dataColl.addData(data);
			sender.outputAdded();
		}
	}	
	
	public Iterable<GenericData<?>> getData(String actID, DataType type) {
		DataCollection coll = dataTable.get(actID);
		if (coll == null)
			return null;
		Iterable<GenericData<?>> i = coll.getData(type);
		return i;
	}
		
	public int isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}
	
	public void parsingDownload(String path){
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(FileManagement.getLocalAppPath() + path));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);

			NodeList list = doc.getElementsByTagName("userdata");

			for(int i=0; i<list.getLength(); i++){
				Element e = (Element)list.item(i);
				if(e.getAttribute("datatype").equals("CONTACT")){
					if(e.getTextContent().equals(Constants.SENTENCE_EMPTY + ""))
						setDownloaded(Integer.parseInt(e.getTextContent()));
				}
				if(e.getAttribute("datatype").equals("IMAGE")){
					if(e.getTextContent().equals(""))
						setDownloaded(Integer.parseInt(e.getTextContent()));
				}
				if(e.getAttribute("datatype").equals("URI")){
					if(e.getTextContent().equals(""))
						setDownloaded(Integer.parseInt(e.getTextContent()));
				}
			}
			setDownloaded(0);
			
		}catch(Exception e){
			Utils.error("File Error", e);
		}

	}
}

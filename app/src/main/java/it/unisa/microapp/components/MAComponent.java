package it.unisa.microapp.components;

import it.unisa.microapp.components.condition.ConditionComponent;
import it.unisa.microapp.data.Condition;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.editor.PreCondition;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.entry.MAEntry;
import it.unisa.microapp.webservice.piece.WSSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ActivityNotFoundException;

public abstract class MAComponent implements Cloneable {
	private String type, id, description;
	private String location;
	protected String scaffolding_name;
	protected String comp_type;

	private Map<DataType, ArrayList<String>> outputs = new HashMap<DataType, ArrayList<String>>();
	private int counter = 0, outQty = 0;
	private Map<String, ArrayList<String>> userdata;
	private Map<DataType, List<MAEntry<String, String>>> inputs;
	protected boolean output;
	protected boolean isSimple = false;
	private List<String> bindings;
	private String condition = null;
	private PreCondition[] list = null;
	private boolean firstComponentGraph;
	private boolean firstFalseComponentCondition;
	private boolean lastTrue;
	private boolean jumpComponent;
	private String nowState = "visible"; 

	public MAComponent(String id, String description) {
		super();
		this.id = id;
		this.description = description;
		location = getLocationQName();
		type = getCompType(id);
		inputs = new HashMap<DataType, List<MAEntry<String, String>>>();

		condition = getCondition(); // mandatory,optional,false
		firstComponentGraph = true;
		firstFalseComponentCondition=false;
		jumpComponent = false;

		nowState = getNowState(); // visible, hidden, process
	}

	public MAComponent(String id, String description, String scname, String sctype) {
		super();

		this.id = id;
		this.description = description;
		scaffolding_name = scname;
		comp_type = sctype;
		location = getLocationQName();
		type = getCompType(id);
		inputs = new HashMap<DataType, List<MAEntry<String, String>>>();

		condition = getCondition(); // mandatory,optional,false
		firstComponentGraph = true;
		firstFalseComponentCondition=false;
		lastTrue=false;
		jumpComponent = false;

		nowState = getNowState(); // visible, hidden, process
	}

	public String getNowState() {
		return nowState;
	}

	public void setNowState(String state) {
		nowState = state;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String s) {
		condition = s;
	}

	public PreCondition[] getPreCondition() {
		return list;
	}

	public void setPreCondition(PreCondition[] cond) {
		this.list = cond;
	}

	public boolean getFirstComponentGraph() {
		return firstComponentGraph;
	}

	public boolean getFirstFalseComponentCondition(){ return firstFalseComponentCondition;}

	public boolean getLastTrue(){
		return lastTrue;
	}

	public void setLastTrue(boolean b){lastTrue=b;}

	public void setFirstComponentGraph(boolean s) {
		firstComponentGraph = s;
	}

	public void setFirstFalseComponentCondition(boolean s) {
		firstFalseComponentCondition = s;
	}

	public boolean getJumpComponent() {
		return jumpComponent;
	}

	public void setJumpComponent(boolean s) {
		jumpComponent = s;
	}

	protected abstract String getLocationQName();

	protected abstract String getCompType(String id);

	public abstract WSSettings getSettings();

	protected abstract void setSettings(WSSettings settings);

	protected abstract void updateSettings(boolean settingUpdate);

	public abstract boolean isUpdate();

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public boolean isOutputSimple() {
		return isSimple;
	}

	public Class<?> getActivityClass() throws ClassNotFoundException {
		try {
			return Class.forName(location);
		} catch (ActivityNotFoundException e) {
			throw new ClassNotFoundException();
		}
	}

	public void addOutput(DataType type, String comp, String binding) {
		ArrayList<String> ha = outputs.get(type);
		if (ha != null)
			ha.add(comp);
		else {
			ha = new ArrayList<String>();
			ha.add(comp);
			outputs.put(type, ha);
		}

		Utils.verbose("" + type);
		// controllo se l'output e' di tipo stringa
		// SOLO PER WEB SERVICE
		if (type.equals(DataType.STRING)) {
			Utils.verbose("true");
			isSimple = true;
		}

		if (bindings == null)
			bindings = new LinkedList<String>();
		bindings.add(binding);
		outQty++;
	}

	public List<String> getBindingsForCondition() {
		if(bindings == null) {
			bindings = new ArrayList<String>();
			bindings.add("");
		}
		return bindings;
	}

	public List<String> getBindings() {
		if(bindings == null) {
			bindings = new ArrayList<String>();
			bindings.add("");
		}
		return bindings;
	}

	public String getBindings(int pos) {
		if (bindings == null)
			return "";
		if (pos >= bindings.size())
			return "";
		return bindings.get(pos);
	}

	public Iterable<String> getOutActivityId(DataType key) {
		return outputs.get(key);
	}

	public void addUserData(String name, String value) {
		ArrayList<String> arr;
		if (userdata == null)
			userdata = new HashMap<String, ArrayList<String>>();
		if (userdata.get(name) == null) {
			arr = new ArrayList<String>();
			userdata.put(name, arr);
		} else {
			arr = userdata.get(name);
		}
		arr.add(value);
	}

	public Iterable<String> getUserData(String name) {		
		if (userdata != null)
			return userdata.get(name);
		else{
			return null;
		}

	}

	public void outputAdded() {
		counter++;
	}

	public boolean isOutFilled() {
		return counter >= outQty;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	public void resetOutCounting() {
		counter = 0;
	}

	@Override
	public String toString() {
		return "MAComponent [type=" + type + ", id=" + id + "]";
	}

	public void addInput(DataType dataType, String comp, String binding) {
		List<MAEntry<String, String>> l = inputs.get(dataType);

		if (l != null)
			l.add(new MAEntry<String, String>(comp, binding));
		else {
			l = new LinkedList<MAEntry<String, String>>();
			l.add(new MAEntry<String, String>(comp, binding));
			inputs.put(dataType, l);
		}
	}

	public List<MAEntry<String, String>> getInputComponents(DataType key) {
		return inputs.get(key);
	}

	public void setInputCompomponents(DataType key, List<MAEntry<String, String>> inputComp) {
		inputs.put(key, inputComp);
	}

}

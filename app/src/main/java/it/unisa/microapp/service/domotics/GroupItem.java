package it.unisa.microapp.service.domotics;

public class GroupItem implements Item{

	protected String type, name, state, link;
	
	public GroupItem (String Type, String Name, String State, String Link){
		type=Type;
		name=Name;
		state=State;
		link=Link;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getStatus() {
		return state;
	}

	@Override
	public String getLink() {
		return link;
	}

}

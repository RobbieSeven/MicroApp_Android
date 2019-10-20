package it.unisa.microapp.data;

import java.util.Collection;

import android.location.Location;

public class LocationData extends GenericData<Location> {
	
	
	

	public LocationData(String sourceId, Collection<Location> data) {
		super(sourceId, data);
	}

	public LocationData(String sourceId, Location data) {
		super(sourceId, data);
	}

	@Override
	public DataType getDataType() {
		return DataType.LOCATION;
	}

}

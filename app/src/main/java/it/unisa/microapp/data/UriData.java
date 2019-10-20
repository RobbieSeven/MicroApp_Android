package it.unisa.microapp.data;

import java.util.Collection;

import android.net.Uri;

public class UriData extends GenericData<Uri>{

	public UriData(String sourceId, Collection<Uri> data) {
		super(sourceId, data);
	}

	public UriData(String sourceId, Uri data) {
		super(sourceId, data);
	}

	@Override
	public DataType getDataType() {
		return DataType.URI;
	}
	
}

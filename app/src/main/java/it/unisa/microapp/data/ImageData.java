package it.unisa.microapp.data;

import java.util.Collection;

import android.graphics.Bitmap;

public class ImageData extends GenericData<Bitmap>{

	public ImageData(String sourceId, Bitmap data) {
		super(sourceId, data);
		
	}

	public ImageData(String sourceId, Collection<Bitmap> data) {
		super(sourceId, data);
	}
	
	@Override
	public DataType getDataType() {
		return DataType.IMAGE;
	}
	
}

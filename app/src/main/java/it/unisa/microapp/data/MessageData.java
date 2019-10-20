package it.unisa.microapp.data;

import java.util.Collection;

import android.os.Message;

public class MessageData extends GenericData<Message> {

	public MessageData(String sourceId, Collection<Message> data) {
		super(sourceId, data);
	}

	public MessageData(String sourceId, Message data) {
		super(sourceId, data);
	}

	@Override
	public DataType getDataType() {
		return null;
	}

}

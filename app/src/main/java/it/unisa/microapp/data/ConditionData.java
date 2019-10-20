package it.unisa.microapp.data;

import java.util.Collection;

/**
 * Created by Vincenzo on 02/12/2015.
 */
public class ConditionData extends GenericData<Condition>{

    public ConditionData(String sourceId, Collection<Condition> data) {
        super(sourceId, data);

    }

    public ConditionData(String sourceId, Condition data) {
        super(sourceId, data);
    }

    @Override
    public DataType getDataType() {
        return DataType.CONDITION;
    }

}
package it.unisa.microapp.components.condition;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

/**
 * Created by Vincenzo on 02/12/2015.
 */
public class ConditionComponent extends MAComponent {

    public ConditionComponent(String id, String description) {
        super(id, description);
    }

    @Override
    protected String getLocationQName() {
        return "it.unisa.microapp.activities.ConditionActivity";
    }

    @Override
    protected String getCompType(String id) {
        return "CONDITION";
    }

    @Override
    public WSSettings getSettings() {
        return null;
    }

    @Override
    protected void setSettings(WSSettings settings) {
    }

    @Override
    protected void updateSettings(boolean settingUpdate) {
    }

    @Override
    public boolean isUpdate() {
        return false;
    }
}

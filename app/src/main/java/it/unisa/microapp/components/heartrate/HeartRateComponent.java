package it.unisa.microapp.components.heartrate;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

/**
 * Created by martina on 15/05/2015.
 */
public class HeartRateComponent extends MAComponent {

    public HeartRateComponent(String id, String description) {
        super(id, description);
    }

    @Override
    protected String getLocationQName() {
        return "it.unisa.microapp.activities.HeartRateActivity";
    }

    @Override
    protected String getCompType(String id) {
        return "HEART_RATE";
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

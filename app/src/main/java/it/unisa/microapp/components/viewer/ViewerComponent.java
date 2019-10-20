package it.unisa.microapp.components.viewer;

import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.webservice.piece.WSSettings;

/**
 * Created by martina on 10/07/2015.
 */
public class ViewerComponent extends MAComponent {


    public ViewerComponent(String id, String description) {
        super(id, description);

    }

    protected String getLocationQName() {
        return "it.unisa.microapp.activities.ViewerActivity";
    }

    protected String getCompType(String id) {
        return "VIEWER";
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
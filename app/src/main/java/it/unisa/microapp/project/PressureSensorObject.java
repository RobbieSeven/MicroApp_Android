package it.unisa.microapp.project;

/**
 * Created by martina on 26/06/2015.
 */
public class PressureSensorObject {

    public PressureSensorObject(String value, String data) {
        this.value = value;
        this.data = data;
    }

    private String value;
    private String data;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

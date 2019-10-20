package it.unisa.microapp.project;

/**
 * Created by martina on 30/06/2015.
 */
public class AccelerometerSensorObject {

    public AccelerometerSensorObject(String valueX, String valueY, String valueZ, String data) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
        this.data = data;
    }

    private String valueX;
    private String valueY;
    private String valueZ;
    private String data;

    public String getValueX() {
        return valueX;
    }

    public void setValueX(String valueX) {
        this.valueX = valueX;
    }

    public String getValueY() {
        return valueY;
    }

    public void setValueY(String valueY) {
        this.valueY = valueY;
    }

    public String getValueZ() {
        return valueZ;
    }

    public void setValueZ(String valueZ) {
        this.valueZ = valueZ;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

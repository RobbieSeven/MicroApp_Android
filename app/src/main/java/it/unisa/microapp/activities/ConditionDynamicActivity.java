package it.unisa.microapp.activities;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import java.util.Iterator;
import java.util.Objects;

import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.utils.Utils;

/**
 * Created by Vincenzo on 26/12/2015.
 */
public class ConditionDynamicActivity extends MAActivity {
    Contact con1,con2=null;
    String str1,str2=null;
    Bitmap image1,image2=null;
    Location location1, location2=null;
    String[] condition;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        String s=this.mycomponent.getUserData("conditionid").iterator().next();
        condition=s.split(".;.");
        Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.STRING);
        if (it != null) {
            for (GenericData<?> d : it) {
                StringData stringa = (StringData) d;
                if(str1==null) str1=stringa.getSingleData();
                else str2=stringa.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.CONTACT);
        if (it != null) {
            for (GenericData<?> d : it) {
                ContactData st = (ContactData) d;
                if (con1==null) con1 = st.getSingleData();
                else con2 = st.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.IMAGE);
        if (it != null) {
            for (GenericData<?> d : it) {
                ImageData im= (ImageData) d;
                if (image1==null) image1=im.getSingleData();
                else image2=im.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.LOCATION);
        if (it != null) {
            for (GenericData<?> d : it) {
                LocationData loc= (LocationData) d;
                if (location1==null) location1=loc.getSingleData();
                else location2= loc.getSingleData();
            }
        }

        this.manageCondition();

    }

    @Override
    protected void prepare() {


    }

    @Override
    protected int onVisible() {
        return 0;
    }


    @Override
    protected View onVisibleView() {

        return null;
    }

    @Override
    protected void prepareView(View v) {

    }

    @Override
    protected void execute() {

    }


    @Override
    public void initInputs() {

    }


    @Override
    public void beforeNext() {
        if (condition[1].equals("pin 1")){
            if (con1 != null){
                ContactData c=new ContactData(mycomponent.getId(),con1);
                application.putDataInCondition(mycomponent, c);
            }
            else if (str1 != null){
                StringData s=new StringData(mycomponent.getId(),str1);
                application.putDataInCondition(mycomponent, s);
            }
            else if (image1 != null){
                ImageData i=new ImageData(mycomponent.getId(),image1);
                application.putDataInCondition(mycomponent, i);
            }
            else if (location1 != null){
                LocationData l=new LocationData(mycomponent.getId(),location1);
                application.putDataInCondition(mycomponent,l);
            }
        }
        else{
            if (con2 != null){
                ContactData c=new ContactData(mycomponent.getId(),con2);
                application.putDataInCondition(mycomponent, c);
            }
            else if (str2 != null){
                StringData s=new StringData(mycomponent.getId(),str2);
                application.putDataInCondition(mycomponent, s);
            }
            else if (image2 != null){
                ImageData i=new ImageData(mycomponent.getId(),image2);
                application.putDataInCondition(mycomponent, i);
            }
            else if (location2 != null){
                LocationData l=new LocationData(mycomponent.getId(),location2);
                application.putDataInCondition(mycomponent,l);
            }

        }

        if (condition[2].equals("pin 1")){
            if (con1 != null){
                ContactData c=new ContactData(mycomponent.getId(),con1);
                application.putDataInCondition(mycomponent, c);
            }
            else if (str1 != null){
                StringData s=new StringData(mycomponent.getId(),str1);
                application.putDataInCondition(mycomponent, s);
            }
            else if (image1 != null){
                ImageData i=new ImageData(mycomponent.getId(),image1);
                application.putDataInCondition(mycomponent, i);
            }
            else if (location1 != null){
                LocationData l=new LocationData(mycomponent.getId(),location1);
                application.putDataInCondition(mycomponent,l);
            }
        }
        else{
            if (con2 != null){
                ContactData c=new ContactData(mycomponent.getId(),con2);
                application.putDataInCondition(mycomponent, c);
            }
            else if (str2 != null){
                StringData s=new StringData(mycomponent.getId(),str2);
                application.putDataInCondition(mycomponent, s);
            }
            else if (image2 != null){
                ImageData i=new ImageData(mycomponent.getId(),image2);
                application.putDataInCondition(mycomponent, i);
            }
            else if (location2 != null){
                LocationData l=new LocationData(mycomponent.getId(),location2);
                application.putDataInCondition(mycomponent,l);
            }
        }
    }

    @Override
    protected void resume(){

    }

    private void manageCondition(){
        Utils.debug("condition[0]"+condition[0].toString());
        Utils.debug("condition[1]"+condition[1].toString());
        Utils.debug("condition[2]"+condition[2].toString());
        if (con1 != null) {
            Utils.debug("con1: "+con1.getPhone());
            Utils.debug("con2: "+con2.getPhone());
            if (condition[0].equals("equal to")) {
                if (con1.getPhone().equals(con2.getPhone())){
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
            else if (condition[0].equals("not equal to")) {
                if (!con1.getPhone().equals(con2.getPhone())) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
            else if (condition[0].equals("starts with")){
                if (con1.getPhone().startsWith(con2.getPhone())) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
        }
        else if (str1 != null){
            if (condition[0].equals("equal to")) {
                if (str1.equals(str2)) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
            else if (condition[0].equals("not equal to")) {
                if (!str1.equals(str2)) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
            else if (condition[0].equals("starts with")){
                if (str1.startsWith(str2)) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();


                }
            }
            else if (condition[0].equals("contains")){
                if (str1.contains(str2)) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    this.beforeNext();

                }
            }
        }
        else if (image1 !=null){
                if (condition[0].equals("width less")){
                    if (image1.getWidth()< image2.getWidth()){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        this.beforeNext();
                    }
                }
                else if (condition[0].equals("width longer")){
                    if (image1.getWidth() > image2.getWidth()){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        this.beforeNext();
                    }
                }
                else if (condition[0].equals("height less")){
                    if (image1.getHeight() < image2.getHeight()){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        this.beforeNext();
                    }
                }
                else if (condition[0].equals("height longer")){
                    if (image1.getHeight() > image2.getHeight()){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        this.beforeNext();

                    }
                }
        }
        else if (location1 !=null){
            if (condition[0].equals("is equal")) {
                if ((location1.getLatitude() == location2.getLatitude())&&(location1.getLongitude()==location2.getLongitude()))
                {
                    application.setConditionActivity(1);
                    next();
                }
                else{
                    application.setConditionActivity(0);
                    this.beforeNext();
                }
            }
            else if (condition[0].equals("is not equal")) {
                if ((location1.getLatitude() != location2.getLatitude())||(location2.getLongitude()!=location2.getLongitude()))
                {
                    application.setConditionActivity(1);
                    next();
                }
                else{
                    application.setConditionActivity(0);
                    this.beforeNext();
                }
            }

        }
    }

}

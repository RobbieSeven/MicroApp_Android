package it.unisa.microapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;

import it.unisa.microapp.R;
import it.unisa.microapp.components.MAComponent;
import it.unisa.microapp.data.Contact;
import it.unisa.microapp.data.ContactData;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.data.LocationData;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.support.GenericSaver;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

/**
 * Created by Vincenzo on 02/12/2015.
 */
public class ConditionActivity extends MAActivity {
    Contact con=null;
    String str=null;
    Bitmap image=null;
    Location location=null;
    String[] condition;
    String mail=null;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        String s=this.mycomponent.getUserData("conditionid").iterator().next();
        condition=s.split(".;.");
        Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.STRING);
        if (it != null) {
            for (GenericData<?> d : it) {
                StringData stringa = (StringData) d;
                str= stringa.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.CONTACT);
        if (it != null) {
            for (GenericData<?> d : it) {
                ContactData st = (ContactData) d;
                con = st.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.IMAGE);
        if (it != null) {
            for (GenericData<?> d : it) {
                ImageData im= (ImageData) d;
                image=im.getSingleData();
            }
        }

        it = application.getData(mycomponent.getId(), DataType.LOCATION);
        if (it != null) {
            for (GenericData<?> d : it) {
                LocationData loc= (LocationData) d;
                location=loc.getSingleData();
            }
        }
        it = application.getData(mycomponent.getId(), DataType.MAIL);
        if (it != null) {
            for (GenericData<?> d : it) {
                StringData m=(StringData) d;
                mail=m.getSingleData();
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
        for (DataType dt : DataType.values()) {
            Iterable<GenericData<?>> dit = application.getData(mycomponent.getId(), dt);

            if(dit != null)
            {
                for(GenericData<?> d : dit)
                    application.putDataInCondition(mycomponent, d);
            }
        }
    }

    @Override
    protected void resume(){

    }

    private void manageCondition(){
        if (con != null) {
            if (condition[1].equals("equal to")) {
                if (con.getPhone().equals(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("contains")) {
                if (con.getPhone().contains(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("starts with")){
                if (con.getPhone().startsWith(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }

        }
        else if (str != null){
            if (condition[1].equals("equal to")) {
                if (str.equals(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("contains")) {
                if (str.contains(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("starts with")){
                if (str.startsWith(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();

                }
            }
        }
        else if (image !=null){
            if(condition.length == 3){
                if (condition[2].equals("width less")){
                    if (image.getWidth()< Integer.parseInt(condition[0])){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        beforeNext();
                    }
                }
                else if (condition[2].equals("width longer")){
                    if (image.getWidth() > Integer.parseInt(condition[0])){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        beforeNext();
                    }
                }
                else if (condition[2].equals("height less")){
                    if (image.getHeight() < Integer.parseInt(condition[1])){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        beforeNext();
                    }
                }
                else if (condition[2].equals("height longer")){
                    if (image.getHeight() > Integer.parseInt(condition[1])){
                        application.setConditionActivity(1);
                        next();
                    }
                    else {
                        application.setConditionActivity(0);
                        beforeNext();
                    }
                }
            }
        }
        else if (location !=null){
            Utils.debug("location diverso da null");
            Utils.debug("condition[2]: "+condition[2]);
            Utils.debug("condition[1]: "+condition[1]);

            if (condition[2].equals("is equal")) {
                if ((location.getLatitude() == Double.parseDouble(condition[0])) && (location.getLongitude() == Double.parseDouble(condition[1])))
                {
                    application.setConditionActivity(1);
                    next();
                }
                else{
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[2].equals("is not equal")) {
                if ((location.getLatitude() != Double.parseDouble(condition[0])) || (location.getLongitude() != Double.parseDouble(condition[1])))
                {
                    application.setConditionActivity(1);
                    next();
                }
                else{
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }

        }
        else if (mail !=null){
            Utils.debug("email:    "+mail);
            if (condition[1].equals("equal to")) {
                if (mail.equals(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("contains")) {
                if (mail.contains(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();
                }
            }
            else if (condition[1].equals("starts with")){
                if (mail.startsWith(condition[0])) {
                    application.setConditionActivity(1);
                    next();
                } else {
                    application.setConditionActivity(0);
                    beforeNext();

                }
            }
        }
    }

}

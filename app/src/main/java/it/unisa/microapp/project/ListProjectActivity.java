package it.unisa.microapp.project;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unisa.microapp.MicroAppGenerator;
import it.unisa.microapp.R;
import it.unisa.microapp.utils.Utils;

/**
 * Created by martina on 17/06/2015.
 */
public class ListProjectActivity extends ListActivity {

    private Button download;
    private Button vota;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfile);

        vota = (Button)findViewById(R.id.button_vota);
        vota.setVisibility(View.GONE);
//        vota.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListFileActivity.this, MyDownloadedAppActivity.class);
//                startActivity(intent);
//            }
//        });

        download = (Button)findViewById(R.id.button_download);
        download.setVisibility(View.GONE);
//        download.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ListFileActivity.this, DownloadSearchAppActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        ProjectTable table = new ProjectTable(this);
        table.open();
        String query = " SELECT * " + " FROM " + ProjectTable.TABLE_PROJECT;

        Cursor c;
        c = table.db().rawQuery(query, null);
        List<String> files = new ArrayList<String>();
        while (c.moveToNext()) {
            files.add(c.getString(c.getColumnIndex(ProjectTable.NAME_PROJECT)));
        }

        Log.d("Files: " , String.valueOf(files));

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                MicroAppGenerator ap=(MicroAppGenerator) getApplication();
                //String msg="";
                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                try {
                    ap.setDeployPath(((TextView) view).getText().toString());
//                    ap.parsingDownload(((TextView) view).getText().toString());

                } catch (NullPointerException e) {
                } catch (SAXException e) {
                    String msg="Reading file is corrupted";
//                    Utils.errorDialog(ListProjectActivity.this, msg, e.getMessage());
                } catch (IOException e) {
                    String msg="File opening error";
//                    Utils.errorDialog(ListProjectActivity.this, msg, e.getMessage());
                }

                finish();
            }
        });


    }

    public void onBackPressed(){
        finish();
    }

}

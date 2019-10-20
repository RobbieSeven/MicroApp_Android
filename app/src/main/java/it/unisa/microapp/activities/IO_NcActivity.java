package it.unisa.microapp.activities;

import java.util.ArrayList;

import it.unisa.microapp.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class IO_NcActivity extends Activity {

	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayList<String> listItems_out=new ArrayList<String>();
	//DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
	ArrayAdapter<String> adapter,adapter_out;
	ListView list1,list2;
	ArrayList<ArrayList<String>> toResult = new ArrayList<ArrayList<String>>();

	//RECORDING HOW MUCH TIMES BUTTON WAS CLICKED
	int clickCounter=0;
	int pos = -1;
	int pos_out = -1;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.io_newcomponent);

		list1 = (ListView) this.findViewById(R.id.ListView_Inputs);

		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				listItems);
		list1.setAdapter(adapter);

		list1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				pos = myItemInt;
				list1.getChildAt(myItemInt).setBackgroundColor(Color.BLUE);
				Toast.makeText(IO_NcActivity.this, list1.getItemAtPosition(myItemInt).toString() , Toast.LENGTH_LONG).show();
			}                 
		});

		Button bnt_addi = (Button) findViewById(R.id.btn_add_input);
		bnt_addi.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				nextInput();
			}
		});

		Button bnt_deli = (Button) findViewById(R.id.btn_del_input);
		bnt_deli.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				listItems.remove(pos);
				adapter.notifyDataSetChanged();
				list1.getChildAt(pos).setBackgroundColor(Color.BLACK);
				pos = -1;
			}
		});

		list2 = (ListView) this.findViewById(R.id.ListView_Outputs);

		adapter_out=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				listItems_out);
		list2.setAdapter(adapter_out);

		list2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
				pos_out = myItemInt;
				list2.getChildAt(myItemInt).setBackgroundColor(Color.BLUE);
				Toast.makeText(IO_NcActivity.this, list2.getItemAtPosition(myItemInt).toString() , Toast.LENGTH_LONG).show();
			}                 
		});

		Button bnt_addo = (Button) findViewById(R.id.btn_add_output);
		bnt_addo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				nextOutput();
			}
		});

		Button bnt_delo = (Button) findViewById(R.id.btn_del_output);
		bnt_delo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				listItems_out.remove(pos_out);
				adapter_out.notifyDataSetChanged();
				list2.getChildAt(pos_out).setBackgroundColor(Color.BLACK);
				pos_out = -1;
			}
		});


		Button btn_save = (Button)findViewById(R.id.save_ioToNc);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toResult.add(listItems);
				toResult.add(listItems_out);

				//				for (int i=0;i<listItems.size();i++) {
					//					String input_li = listItems.get(i);
					//					toResult
					//				}

				Intent intent=new Intent();
				intent.putExtra("ComingFrom", toResult);
				setResult(RESULT_OK, intent);
				finish();
			}

		});

	}

	//METHOD WHICH WILL HANDLE DYNAMIC INSERTION
	public void addItems(String str) {
		listItems.add(str);
		adapter.notifyDataSetChanged();
	}

	public void addItems_out(String str) {
		listItems_out.add(str);
		adapter_out.notifyDataSetChanged();
	}

	public void onBackPressed(){
		Intent intent=new Intent();
		intent.putExtra("DaEditor", "backpressed");
		setResult(RESULT_OK, intent);
		finish();
	}

	public void nextInput() {
		Intent i = new Intent(this, IO_info.class);
		final int result=1;
		this.startActivityForResult(i,result);
	}

	public void nextOutput() {
		Intent i = new Intent(this, IO_info_out.class);
		final int result=1;
		this.startActivityForResult(i,result);
	}

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		String extraData=data.getStringExtra("ComingFrom");
		if(!extraData.equals("backpressed")) {
			String[] arrayExtraData = extraData.split("/");

			if(arrayExtraData[0].equals("null") ) {
				String str = arrayExtraData[1]+" - "+arrayExtraData[2];
				addItems_out(str);
			}
			else {
				String str = arrayExtraData[1]+" - "+arrayExtraData[2] + " - " + arrayExtraData[0];
				System.err.println(arrayExtraData[3]);
				if(arrayExtraData[3].equals("true")) {
					str = str + " - " + "multi";
				}
				else {
					str = str + " - " + "nomulti";
					if(!arrayExtraData[4].equals("Seleziona")) {
						str = str + " - " + arrayExtraData[4];
					}
				}
				addItems(str);
			}
		}

	}


}

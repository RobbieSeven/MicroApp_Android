package it.unisa.microapp.store;

import it.unisa.microapp.R;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AppCaricataActivity extends Activity implements OnClickListener,OnTouchListener{

	private Button menu;
	private TextView nick;
	private TextView nomeapp;
	private EditText descrizione;
	private ImageView img;
	private byte [] b;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appcaricata);

		nick = (TextView)findViewById(R.id.twap_nick);
		nomeapp = (TextView)findViewById(R.id.twap_nomea);
		descrizione = (EditText)findViewById(R.id.ap_descrizione12);
		img = (ImageView)findViewById(R.id.imw_carica);
		img.setOnTouchListener(this);

		Intent intent = getIntent();
		nick.setText(intent.getStringExtra("nick"));
		nomeapp.setText(intent.getStringExtra("nomeapp"));
		descrizione.setText(intent.getStringExtra("descrizione"));

		
		b = Base64.decode(intent.getStringExtra("img"), 0);
		ByteArrayInputStream ins = new ByteArrayInputStream(b);
		Bitmap bmp = BitmapFactory.decodeStream(ins);

		img.setImageBitmap(bmp);

		menu = (Button)findViewById(R.id.menu_carica);
		menu.setOnClickListener(this);
	}

	public void onClick(View v) {
		finish();
	}

	public boolean onTouch(View v, MotionEvent event) {
		Intent intent = new Intent(this, ZoomActivity.class);
		intent.putExtra("img", b);
		startActivity(intent);

		return false;
	}
}

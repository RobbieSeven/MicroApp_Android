package it.unisa.microapp.store;


import it.unisa.microapp.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<App>{
	private LayoutInflater inflater;
	private int resource;
	
	public CustomListViewAdapter(Context context, int resourceId, List<App> items){
		super(context,resourceId,items);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent){
		
		convertView = (RelativeLayout)inflater.inflate(resource, null);
		App app = getItem(position);
		TextView nome = (TextView)convertView.findViewById(R.id.appName);
		TextView desc = (TextView)convertView.findViewById(R.id.appDesc);
		RatingBar voto = (RatingBar)convertView.findViewById(R.id.ratingBar1search);
		ImageView img = (ImageView)convertView.findViewById(R.id.appImage);

		nome.setText(app.getName());
		desc.setText(app.getDesc());
		voto.setRating((float) app.getVoto());
		img.setImageBitmap(app.getImage());
		
		return convertView;
	}

}

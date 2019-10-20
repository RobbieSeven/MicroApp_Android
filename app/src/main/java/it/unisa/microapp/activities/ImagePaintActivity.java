package it.unisa.microapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.ImageData;

public class ImagePaintActivity extends MAActivity implements OnTouchListener
{
	private static final int BRUSH=0;
	private static final int BRUSHIMG=1;
	
	private static String[] drawables={"brush48","grass48","kiss48","soccer48","heart48","skull48"};
	
	private Bitmap image;
	private Bitmap retImage;
	private Paint p;
	private Canvas canvas;
	private ImageView img;
	private int color;
	private int stroke;
	private Bitmap brush_image;
	private float downx;
	private float downy;
	private float upx;
	private float upy;
	private int currStatus=BRUSH;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.paintimage;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void execute() {
		

	}
	
	@Override
	protected void prepareView(View v) {
		img=(ImageView) this.findViewById(R.id.imagepaint);
		
		p=new Paint(Paint.DITHER_FLAG);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC));
		
		//image=Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		//canvas=new Canvas(image);
		
		//canvas.drawRGB(123, 211, 43);
		
		ImageView back=(ImageView) this.findViewById(R.id.imageback);
		back.setImageBitmap(image);
		
		retImage=Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		canvas=new Canvas(retImage);
		
		p.setColor(Color.GREEN);
		p.setStrokeWidth(5);
		stroke=5;
		
		//Matrix m=new Matrix();
		
		//canvas.drawBitmap(image, m, p);
		
		img.setImageBitmap(retImage);
		img.setOnTouchListener(this);
		
		Button b=(Button) this.findViewById(R.id.paint_brush);
		b.setOnClickListener(new listener());
		b=(Button) this.findViewById(R.id.paint_color);
		b.setOnClickListener(new listener());
		b=(Button) this.findViewById(R.id.paint_stroke);
		b.setOnClickListener(new listener());
		b=(Button) this.findViewById(R.id.paint_erase);
		b.setOnClickListener(new listener());

	}	
	
	private class MyCustomAdapter extends ArrayAdapter<String>
	{
		private int i=1;

		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = getLayoutInflater();
			View v=inflater.inflate(R.layout.brushrow, null);
			ImageView img=(ImageView) v.findViewById(R.id.brushimg);
			
			int id=getResources().getIdentifier(this.getItem(position), "drawable", ImagePaintActivity.this.getPackageName());
			
			BitmapDrawable draw=(BitmapDrawable) getResources().getDrawable(id);
			img.setDrawingCacheEnabled(true);
			img.setImageDrawable(draw);
			
			TextView txt=(TextView) v.findViewById(R.id.brushdesc);
			
			txt.setText("brush "+i);
			
			v.setId(100+i);
			
			i++;
			
			v.setTag(this.getItem(position));
			
			return v;
		}
	}
	
	private class listener implements OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			Context con=ImagePaintActivity.this;
			if(v.getId() == R.id.paint_brush)
			{
				p.setColor(color);
				p.setXfermode(new PorterDuffXfermode(Mode.SRC));
				AlertDialog.Builder builder=new AlertDialog.Builder(con);
				
				builder.setTitle("choose brush");
				
				ListView list1=new ListView(con);
				
				list1.setAdapter(new MyCustomAdapter(con,R.layout.brushrow,drawables));
				
				list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> par, View v,int pos, long id) 
					{
						if(pos == 0)
							currStatus=BRUSH;
						else
						{
							currStatus=BRUSHIMG;
							//ImageView img=(ImageView) v.findViewById(R.id.brushimg);
							String bit=(String) v.getTag();
							
							int ids=getResources().getIdentifier(bit, "drawable", ImagePaintActivity.this.getPackageName());
							BitmapDrawable draw=(BitmapDrawable) getResources().getDrawable(ids);
							
							brush_image=draw.getBitmap();
							
						}
					}
				});
				
				builder.setView(list1);
				
				builder.setNeutralButton(android.R.string.ok, null);
				//builder.create();
				
				AlertDialog dial=builder.create();
				
				dial.show();
			}
			else if(v.getId() == R.id.paint_stroke)
			{
				AlertDialog.Builder builder=new AlertDialog.Builder(con);
				
				builder.setTitle("choose brush stroke");
				
				final RadioGroup radiog=new RadioGroup(con);
				RadioButton r=new RadioButton(con);
				r.setId(5);
				if(stroke == r.getId())
					r.setChecked(true);
				r.setText("small");
				
				radiog.addView(r);
				
				r=new RadioButton(con);
				r.setId(10);
				if(stroke == r.getId())
					r.setChecked(true);
				r.setText("medium");
				
				radiog.addView(r);
				
				r=new RadioButton(con);
				r.setId(15);
				if(stroke == r.getId())
					r.setChecked(true);
				r.setText("large");
				
				radiog.addView(r);
				
				builder.setView(radiog);
				
				builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						stroke=radiog.getCheckedRadioButtonId();
						p.setStrokeWidth(stroke);
					}
				});
				
				AlertDialog dial=builder.create();
				
				dial.show();
				
			}
			else if(v.getId() == R.id.paint_color)
			{
				AlertDialog.Builder builder=new AlertDialog.Builder(con);
				
				builder.setTitle("choose color");
				
				final View vv=ImagePaintActivity.this.getLayoutInflater().inflate(R.layout.colorchoose, null);
				builder.setView(vv);
				
				ImageView imga=(ImageView) vv.findViewById(R.id.color_image);
				
				Bitmap back=Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
				
				Paint pa=new Paint();
				Canvas c=new Canvas(back);
				
				c.drawRGB(255, 0, 0);
				
				imga.setImageBitmap(back);
				
				SeekBar s=(SeekBar) vv.findViewById(R.id.red_rgb);
				
				s.setOnSeekBarChangeListener(new barlistener(vv,pa,c,imga));
				
				s=(SeekBar) vv.findViewById(R.id.green_rgb);
				
				s.setOnSeekBarChangeListener(new barlistener(vv,pa,c,imga));
				
				s=(SeekBar) vv.findViewById(R.id.blue_rgb);
				
				s.setOnSeekBarChangeListener(new barlistener(vv,pa,c,imga));
				
				builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						int r,g,b;
						SeekBar s=(SeekBar) vv.findViewById(R.id.red_rgb);
						
						r=s.getProgress();
						
						s=(SeekBar) vv.findViewById(R.id.green_rgb);
						
						g=s.getProgress();
						
						s=(SeekBar) vv.findViewById(R.id.blue_rgb);
						
						b=s.getProgress();
						
						p.setColor(Color.rgb(r, g, b));
						
						color=p.getColor();
						
					}
				});
				//builder.create();
				
				AlertDialog dial=builder.create();
				
				dial.show();
			}
			else if(v.getId() == R.id.paint_erase)
			{
				currStatus=BRUSH;
				p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				p.setColor(Color.TRANSPARENT);
			}
		}
		
	}
	
	private class barlistener implements OnSeekBarChangeListener
	{
		private View v;
		//private Paint pa;
		private Canvas cc;
		private ImageView i;

		public barlistener(View vv, Paint pa, Canvas c, ImageView imga) 
		{
			v=vv;
			//this.pa=pa;
			cc=c;
			i=imga;
		}

		@Override
		public void onProgressChanged(SeekBar s, int progress,boolean fromUser) 
		{
			int r,g,b;
			
			if(s.getId() == R.id.red_rgb)
			{
				SeekBar sa=(SeekBar) v.findViewById(R.id.green_rgb);
				g=sa.getProgress();
				sa=(SeekBar) v.findViewById(R.id.blue_rgb);
				b=sa.getProgress();
				
				cc.drawRGB(progress, g, b);
			}
			else if(s.getId() == R.id.green_rgb)
			{
				SeekBar sa=(SeekBar) v.findViewById(R.id.red_rgb);
				r=sa.getProgress();
				sa=(SeekBar) v.findViewById(R.id.blue_rgb);
				b=sa.getProgress();
				
				cc.drawRGB(r, progress, b);
			}
			else
			{
				SeekBar sa=(SeekBar) v.findViewById(R.id.red_rgb);
				r=sa.getProgress();
				sa=(SeekBar) v.findViewById(R.id.green_rgb);
				g=sa.getProgress();
				
				cc.drawRGB(r, g, progress);
			}
			
			i.invalidate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
		
	}

	@Override
	public void initInputs() 
	{
		ImageData img=(ImageData) application.getData(mycomponent.getId(), DataType.IMAGE).iterator().next();
		image=img.getSingleData();
		
		
	}

	

	@Override
	public void beforeNext()
	{
		Bitmap bit=Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		Paint pa=new Paint();
		Canvas c=new Canvas(bit);
		
		Matrix m=new Matrix();
		
		c.drawBitmap(image, m, pa);
		
		m=new Matrix();
		
		c.drawBitmap(retImage, m, pa);
		
		ImageData img=new ImageData(mycomponent.getId(), bit);
		application.putData(mycomponent, img);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		downx = event.getX();
		downy = event.getY();
		break;
		case MotionEvent.ACTION_MOVE:
		upx = event.getX();
		upy = event.getY();
		
		if(currStatus == BRUSH)
		{
			canvas.drawLine(downx, downy, upx, upy, p);
			//canvas.drawPoint(upx, upy , p);
			//canvas.drawCircle(upx, upy, stroke, p);
		}
		else
		{
			Paint pa=new Paint();
			canvas.drawBitmap(brush_image, upx - (brush_image.getWidth()/2), upy - (brush_image.getHeight()/2), pa);
		}
		
		img.invalidate();
		downx = upx;
		downy = upy;
		break;
		case MotionEvent.ACTION_UP:
		upx = event.getX();
		upy = event.getY();
		
		if(currStatus == BRUSH)
		{
			canvas.drawLine(downx, downy, upx, upy, p);
			//canvas.drawPoint(upx, upy , p);
		}
		else
		{
			Paint pa=new Paint();
			canvas.drawBitmap(brush_image, upx - (brush_image.getWidth()/2), upy - (brush_image.getHeight()/2), pa);
		}
		
		img.invalidate();
		break;
		case MotionEvent.ACTION_CANCEL:
		break;
		default:
		break;
		}
		return true;

	}
	
	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}

}

package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import it.unisa.microapp.data.DataType;
import it.unisa.microapp.data.GenericData;
import it.unisa.microapp.data.ImageData;
import it.unisa.microapp.utils.Utils;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FaceDetectActivity extends MAActivity {

	private static final int MAX_FACES = 2;

	private Bitmap cameraBitmap = null;
	private ImageView imageView;
	private TextView textView;
	private Bitmap[] outBitamp;
	private int facesFound;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		outBitamp = new Bitmap[FaceDetectActivity.MAX_FACES];

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return R.layout.detect;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		imageView = (ImageView) findViewById(R.id.pre_img);
		textView = (TextView) findViewById(R.id.numFound);	
	}	

	@Override
	protected void execute() {
		detectFaces();

	}	
	
	private void detectFaces() {
		facesFound = 0;
		textView.setText("" + facesFound);
		if (null != cameraBitmap) {
			int width = cameraBitmap.getWidth();
			int height = cameraBitmap.getHeight();

			FaceDetector detector = new FaceDetector(width, height, FaceDetectActivity.MAX_FACES);
			Face[] faces = new Face[FaceDetectActivity.MAX_FACES];

			Bitmap bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
			Paint ditherPaint = new Paint();
			Paint drawPaint = new Paint();

			ditherPaint.setDither(true);
			drawPaint.setColor(Color.RED);
			drawPaint.setStyle(Paint.Style.STROKE);
			drawPaint.setStrokeWidth(3);

			Canvas canvas = new Canvas();
			canvas.setBitmap(bitmap565);
			canvas.drawBitmap(cameraBitmap, 0, 0, ditherPaint);

			facesFound = detector.findFaces(bitmap565, faces);
			PointF midPoint = new PointF();
			float eyeDistance = 0.0f;
			float confidence = 0.0f;

			Utils.verbose("FaceDetector Number of faces found: " + facesFound);

			if (facesFound > 0) {
				for (int index = 0; index < facesFound; ++index) {
					faces[index].getMidPoint(midPoint);
					eyeDistance = faces[index].eyesDistance();
					confidence = faces[index].confidence();

					Utils.debug("FaceDetector Confidence: " + confidence + ", Eye distance: " + eyeDistance
							+ ", Mid Point: (" + midPoint.x + ", " + midPoint.y + ")");

					canvas.drawRect((int) midPoint.x - eyeDistance, (int) midPoint.y - eyeDistance, (int) midPoint.x
							+ eyeDistance, (int) midPoint.y + eyeDistance, drawPaint);

					//TODO: bitmap crop
					outBitamp[index] = Bitmap.createBitmap(bitmap565, (int) (midPoint.x - eyeDistance),
							(int) (midPoint.y - eyeDistance), (int)(2*eyeDistance), (int)(2*eyeDistance));
				}
			}

			textView.setText("" + facesFound);
			imageView.setImageBitmap(bitmap565);
		}
	}

	@Override
	public void initInputs() {

		Iterable<GenericData<?>> it = application.getData(mycomponent.getId(), DataType.IMAGE);
		if (it != null)
			for (GenericData<?> d : it) {
				ImageData st = (ImageData) d;

				this.cameraBitmap = st.getSingleData();
			}
	}

	@Override
	public void beforeNext() {
		if (facesFound == 0) {
			ImageData im = new ImageData(mycomponent.getId(), cameraBitmap);
			application.putData(mycomponent, im);
		} else
			for (int i = 0; i < facesFound; i++) {
				if (outBitamp[i] != null) {
					ImageData im = new ImageData(mycomponent.getId(), outBitamp[i]);
					application.putData(mycomponent, im);
				}
			}
	}

	@Override
	protected void resume(){
		 //metodi per speech Vincenzo Savarese
	}

}

package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.project.SensorTable;
import it.unisa.microapp.service.domotics.DOperation;
import it.unisa.microapp.support.FileManagement;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.piece.WSContext;
import it.unisa.microapp.webservice.piece.WSOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dx.command.Main;

public class MainPanel extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback {

	class SurfaceThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private MainPanel _panel;
		private boolean _run = false;

		public SurfaceThread(SurfaceHolder surfaceHolder, MainPanel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}

		public void run() {
			Canvas c;
			while (_run) {
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						if (c != null)
							_panel.draw(c);
					}

				} finally {
					if (c != null)
						_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}

		public void setRunning(boolean run) {
			_run = run;
		}

		public SurfaceHolder getSurfaceHolder() {
			return _surfaceHolder;
		}

	}
	boolean flagCondition=false;

	Pin pinoutSelezionato;
	private SurfaceThread _thread;
	protected EditorActivity act;
	protected EditorGrid grid;

	// Offset to the upper left corner of the screen
	private int _xOffset = 0;
	private int _yOffset = 0;

	// last touch point
	private int _xTouch = 0;
	private int _yTouch = 0;

	// loop borders
	//
	private int curCellRow;
	private int curCellColumn;

	// int cellWidthGrid = 0;
	// int cellHeightGrid = 0;

	// scrolling active?
	private boolean _isMoving = false;

	// INSERIMENTO PEZZI
	Piece X, X1, Piz;
	int inserimenti;// conta gli inserimenti effettuati
	boolean entra;
	boolean selezione = false;
	int conta;
	float inizialeX, inizialeY, PrecX, PrecY;// punto(coordinate) di parteza da
												// dove si disegna il pezzo
	int codiceColoreCondition=0;
	boolean ponte;
	int cicla;// numero di colonne per il ponte
	int pont, pop; // colonna di partenza del ponte

	// Multi
	int multiconta, multico;
	boolean Vmulti, StopinseriMulti;
	String padre;

	// User
	List<Pin> list;
	static Pin lastPin = null;

	int userCont = 0;
	boolean UserIns;
	private String description = null;
	private String icon = null;
	private String activator = null;

	private GestureDetector gestures;
	private GestureListener gestureListener;

	private Bitmap screenShot;
	private Demo demos;

	public Bitmap getScreenShot() {
		return screenShot;
	}

	public void setScreenShot(Bitmap screenShot) {
		this.screenShot = screenShot;
	}

	public boolean isReadyForInserting() {
		return this.selezione && Vmulti;
	}

	/**
	 * This is the class that monitors "gestures" which are performed on the
	 * scrolling view.
	 */
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		// private DecelerateInterpolator interpolator;

		//
		// Start and end times for the fling gesture/interpolation.
		//
		private long startTime;
		private long endTime;

		//
		// The X,Y coordinate of where the screen was when the fling started.
		//
		private int startFlingX;
		private int startFlingY;

		//
		// The total amount of pixels in each direction that the scroll will go.
		//
		// private float totalAnimDx;
		// private float totalAnimDy;

		public GestureListener() {
		}

		/**
		 * This method is called for the "fling" type of scrolling." The
		 * velocity, x & y, are in pixels per second.
		 * 
		 * @param e1
		 *            The first down motion event that started the fling.
		 * @param e2
		 *            The move motion event that triggered the current onFling.
		 * @param velocityX
		 *            The velocity of this fling measured in pixels per second
		 *            along the x axis.
		 * @param velocityY
		 *            The velocity of this fling measured in pixels per second
		 *            along the y axis.
		 * 
		 * @return true (i.e. we've handled this event).
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Utils.debug("onFling ()");
			//
			// We want to have this fling last 1.5 seconds.
			//
			final float distanceTimeFactor = 1.5f;

			//
			// The total distance traveled in each direction is going to be the
			// time factor that we want to slow down in, times the velocity in
			// each
			// direction. This could likely be very large, so we decrease it by
			// an "arbitrary" scaling factor.
			//
			scrollBy(-(distanceTimeFactor * velocityX / 3), -(distanceTimeFactor * velocityY / 3), 1500);

			return true;
		}

		/**
		 * This method scrolls the screen by a certain amount over a period of
		 * time.
		 * 
		 * @param dx
		 *            The amount to scroll in the X direction.
		 * @param dy
		 *            The amount to scroll in the Y direction.
		 * @param duration
		 *            The duration of the scroll.
		 */
		public void scrollBy(float dx, float dy, long duration) {
			startFlingX = _xOffset;
			startFlingY = _yOffset;
			// totalAnimDx = dx;
			// totalAnimDy = dy;

			//
			// Get the current time, start time, and end time for the
			// interpolation.
			//
			startTime = System.currentTimeMillis();
			endTime = startTime + duration;

			//
			// Use a "DecelerateInterpolator" for this animation. This
			// interpolator
			// starts off fast, and decreases in speed as it gets closer to the
			// end of the interpolation.
			//
			// this.interpolator =
			new DecelerateInterpolator(1.0f);
			post(new Runnable() {
				@Override
				public void run() {
					checkDeceleration();
				}
			});
		}

		@Override
		public void onLongPress(MotionEvent e) {

			synchronized (_thread.getSurfaceHolder()) {

				Point lp = grid.calcLogicPosition(e.getX() + _xOffset, e.getY() + _yOffset);
				Piece pm = (Piece) grid.getMatrixPiece(lp);
				if (pm != null) {
					grid.unselectPieces();

					AlertDialog.Builder builder = new AlertDialog.Builder(act);

					builder.setTitle(R.string.details);
					builder.setIcon(android.R.drawable.ic_dialog_info);

					ScrollView scroll = new ScrollView(act);
					builder.setView(scroll);
					TextView txt = new TextView(act);
					txt.setText(pm.showInfo());
					scroll.addView(txt);
					builder.setNeutralButton(android.R.string.ok, null);
					AlertDialog dial = builder.create();
					dial.show();
				}

				if (e.getY() > Constants.SCREEN_SPLIT) {
					float WidthQ = Constants.GRID_RATIO;
					float HeightQ = Constants.GRID_RATIO;

					RectF rect = new RectF(Constants.WIDTH - (WidthQ) + 4, Constants.HEIGTH - (HeightQ), Constants.WIDTH - 4,
							Constants.HEIGTH - 4);
					if (rect.contains(e.getX(), e.getY())) {
						_xOffset = 0;
						_yOffset = 0;
						calculateLoopBorders();
						invalidate();
					}

					rect = new RectF(4, Constants.HEIGTH - (HeightQ), WidthQ - 4, Constants.HEIGTH - 4);
					if (rect.contains(e.getX(), e.getY())) {
						if (selezione && IconsPanel.X != null) {
							AlertDialog.Builder builder = new AlertDialog.Builder(act);

							builder.setTitle("Details");
							builder.setIcon(android.R.drawable.ic_dialog_info);

							ScrollView scroll = new ScrollView(act);
							builder.setView(scroll);
							TextView txt = new TextView(act);
							txt.setText(IconsPanel.X.showInfo());
							scroll.addView(txt);
							builder.setNeutralButton(android.R.string.ok, null);
							AlertDialog dial = builder.create();
							dial.show();
						}
					}
				}
			}
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			synchronized (_thread.getSurfaceHolder()) {
				Point lp = grid.calcLogicPosition(e.getX() + _xOffset, e.getY() + _yOffset);
				Piece pm = (Piece) grid.getMatrixPiece(lp);
				if (pm != null) {
					grid.unselectPieces();
					pm.setSelected(true);
					invalidate();
					pm.fireOnClick();
					if (pm.getDescription().equals("Dynamic Condition")){
						flagCondition=false;
						annotatePinsDynamicCondition(pm.getInputPin());
					}
				}

				return super.onDoubleTap(e);
			}
		}

		/**
		 * This is the method that is called by the thread that updates the
		 * screen based on a DecelerateInterpolator. It determines how far along
		 * in the interpolation process we are, and if the deceleration has
		 * completed. If the interpolator has not finished... interpolating...
		 * it spawns a new thread to continue upating.
		 */
		public void checkDeceleration() {
			//
			// How far (percentage wise) are we through the time?
			//
			long curTime = System.currentTimeMillis();
			float percentTime = (float) (curTime - startTime) / (float) (endTime - startTime);

			//
			// Calculate the percentage distance we are through the
			// interpolation
			// and use that to determine the distance (in pixels) we've
			// traveled.
			//
			// float percentDistance =
			// this.interpolator.getInterpolation(percentTime);
			// float curDx = percentDistance * totalAnimDx;
			// float curDy = percentDistance * totalAnimDy;

			//
			// Use the original starting position, and the current distance
			// traveled to get the current position to be displayed.
			//

			_xOffset = startFlingX /* + (int) curDx */;
			_yOffset = startFlingY /* + (int) curDy */;

			// Ensure that the offsets are actually on the screen.
			//

			if (grid.getNumCols() < Constants.GRID_WSIZE) {
				_xOffset = 0;
			} else if (_xOffset < 0) {
				_xOffset = 0;
			} else if (_xOffset > grid.getNumCols() * Constants.GRID_RATIO - getWidth()) {
				_xOffset = grid.getNumCols() * Constants.GRID_RATIO - getWidth();

			}

			if (grid.getNumRows() < Constants.GRID_HSIZE) {
				_yOffset = 0;
			} else if (_yOffset < 0) {
				_yOffset = 0;
			} else if (_yOffset > (grid.getNumRows() + 1) * Constants.GRID_RATIO - getHeight()) {
				_yOffset = (grid.getNumRows() + 1) * Constants.GRID_RATIO - getHeight();

			}
			_xOffset = (int) Math.floor(_xOffset / Constants.GRID_RATIO) * Constants.GRID_RATIO;
			_yOffset = (int) Math.floor(_yOffset / Constants.GRID_RATIO) * Constants.GRID_RATIO;
			calculateLoopBorders();
			//
			// If we're not done yet, repost a thread to call us again.
			//
			if (percentTime < 1.0f) {
				post(new Runnable() {
					@Override
					public void run() {
						checkDeceleration();
					}
				});
			}
		}

	}

	public MainPanel(Context context) {
		super(context);

		if (!isInEditMode())
			init(context);
	}

	public MainPanel(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (!isInEditMode())
			init(context);
	}

	public MainPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if (!isInEditMode())
			init(context);
	}

	private void setScreenVariables(int h, int w, boolean first) {
		h = h - Constants.SCREEN_OFFSET; // 1701;

		Constants.setSize(w, h, (int) (getResources().getDimension(R.dimen.bottom_margin) + 0.5f),
				(int) (getResources().getDimension(R.dimen.bottom_button) + 0.5f));

		if (first)
			Constants.initializeSize();
		else
			Constants.normalizeSize();

		grid.refresh();
	}

	public void resize(int h, int w) {
		Constants.setPhisicalHeigth(h);
		setScreenVariables(Constants.PHISICAL_HEIGTH, w, true);
		init(this.act);
	}

	public void zoomIn() {
		if (Constants.zoomIn()) {
			setScreenVariables(Constants.PHISICAL_HEIGTH, Constants.WIDTH, false);
			invalidate();
		}
	}

	public void zoomOut() {
		if (Constants.zoomOut()) {
			setScreenVariables(Constants.PHISICAL_HEIGTH, Constants.WIDTH, false);
			invalidate();
		}
	}

	public void zoomZero() {
		Constants.zoomZero();
		setScreenVariables(Constants.PHISICAL_HEIGTH, Constants.WIDTH, false);
		invalidate();
	}

	public void handleParameters() {
		// TODO: modify parameters order
	}

	public void init(Context context) {

		this.setOnTouchListener(this);
		// Create and attach the new GestureDetector and Listener.
		//
		gestureListener = new GestureListener();

		gestures = new GestureDetector(this.getContext(), gestureListener);

		getHolder().addCallback(this);
		_thread = new SurfaceThread(getHolder(), this);

		act = (EditorActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);

		// ************************************************

		// cellWidthGrid = Constants.GRID_RATIO - Constants.SPACE;
		// cellHeightGrid = Constants.GRID_RATIO - Constants.SPACE;

		demos = new Demo(this);
		demos.reset();

		synchronized (_thread.getSurfaceHolder()) {
			grid = new EditorGrid();
			grid.setActivity(act);

			if(!OptionsActivity.getBoolean(Constants.CB_WB_KEY, this.getContext())) {
				this.setBackground(getResources().getDrawable(R.drawable.background));		
			} else
			{
				this.setBackground(getResources().getDrawable(R.color.WHITE));	
			}			

			setupPieces();
		}

	}

	public void setupPieces() {

		synchronized (_thread.getSurfaceHolder()) {
			grid.clearPieces();
		}

	}

	public void demo() {
		synchronized (_thread.getSurfaceHolder()) {
			demos.next();
			//demos.set(3);
			grid.setModified(false);
		}
	}

	protected void onDraw(Canvas canvas) {
		if (!this.isInEditMode()) {
			synchronized (_thread.getSurfaceHolder()) {
				if (selezione) // showSelected)
				{
					grid.draw(canvas, this.X1, curCellColumn, curCellRow);

				} else
					grid.draw(canvas, null, curCellColumn, curCellRow);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		synchronized (_thread.getSurfaceHolder()) {
			if ((keyCode == KeyEvent.KEYCODE_X)) {
				Piece p = grid.getSelected();
				if (p != null) {
					int ncol = p.getLogicPosition().x + p.getWidthScale();
					grid.insertColumnMatrix(ncol);
					p.expand();
					grid.updatePiece(p);
					invalidate();
				}
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_U)) {
				grid.moveUp();
				invalidate();
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_D)) {
				grid.moveDown();
				invalidate();
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_G)) {
				grid.setShowGrid(!grid.isShowGrid());
				invalidate();
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_I)) {
				grid.setShowIconized(!grid.isShowIconized());
				invalidate();
				return true;
			} else if ((keyCode == KeyEvent.KEYCODE_H)) {
				grid.setShowHoles(!grid.isShowHoles());
				invalidate();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_R) {
				grid.unselectPiece();
				invalidate();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
	}

	public void undo() {
		synchronized (_thread.getSurfaceHolder()) {
			grid.undo();
			invalidate();
		}
	}

	public void shift() {
		synchronized (_thread.getSurfaceHolder()) {

			Piece p = grid.getSelected();
			if (p != null) {
				int ncol = p.getLogicPosition().x;
				grid.insertColumnMatrix(ncol);
			} else {
				if (grid.getNumCols() > 1)
					grid.insertColumnMatrix(1);
			}
			invalidate();
		}
	}

	public String getNowState() {
		Piece p = grid.getSelected();
		if (p != null) {
			return p.getNowState();
		}
		return null;
	}

	public void setNowState(String stat) {
		synchronized (_thread.getSurfaceHolder()) {

			Piece p = grid.getSelected();
			if (p != null) {
				p.setNowState(stat);
			}
			invalidate();
		}
	}

	public void setCondition(int state) {
		synchronized (_thread.getSurfaceHolder()) {

			Piece p = grid.getSelected();
			if (p != null) {
				p.setCondition(state);
			}
			invalidate();
		}
	}

	// TODO: get the condition
	public int getCondition() {
		Piece p = grid.getSelected();
		if (p != null)
			return p.getCondition();
		return -1;
	}

	public PreCondition[] getPreCondition() {
		Piece p = grid.getSelected();
		if (p != null) {
			return p.getPreCondition();
		}
		return null;
	}

	public void setPreCondition(PreCondition[] cond) {
		synchronized (_thread.getSurfaceHolder()) {

			Piece p = grid.getSelected();
			if (p != null) {
				p.setPreCondition(cond);
			}
			invalidate();
		}
	}

	public boolean isCondition() {
		Piece p = grid.getSelected();
		if (p != null)
			return p.isCondition();
		return false;
	}

	public void delete() {
		synchronized (_thread.getSurfaceHolder()) {

			Piece p = grid.getSelected();
			if (p != null) {
				grid.remove(p);
				grid.setModified(true);
			}
			invalidate();
		}
	}

	public void setInfos(String icon, String description, String activator) {
		this.icon = icon;
		this.description = description;
		this.activator = activator;
	}


	public void load(String nomefile) {
		String colorePrecedente=null;
		synchronized (_thread.getSurfaceHolder()) {
			try {
				File xmlUrl = new File(FileManagement.getLocalAppPath(), nomefile);

				if (xmlUrl.exists()) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(xmlUrl);
					doc.getDocumentElement().normalize();
					Element root = doc.getDocumentElement();

					NodeList icon = root.getElementsByTagName("icon");
					if (icon.getLength() == 1) {
						this.icon = icon.item(0).getTextContent();
					}

					NodeList description = root.getElementsByTagName("description");
					if (description.getLength() == 1) {
						this.description = description.item(0).getTextContent();
					}

					NodeList activator = root.getElementsByTagName("activator");
					if (activator.getLength() == 1) {
						this.activator = activator.item(0).getTextContent();
					}

					NodeList categ = root.getElementsByTagName("component");
					int lung = categ.getLength();
					grid.clearPieces();
					for (int i = 0; i < lung; i++) {// per ogni component

						Element component = (Element) categ.item(i);

						String cdescription = component.getElementsByTagName("description").item(0).getTextContent();

						String cid = component.getAttribute("id");
						String cstate = component.getAttribute("state");
						String callStates = component.getAttribute("allStates");
						String ctype = component.getAttribute("type");
						String clabel = component.getAttribute("label");
						String cicon = component.getAttribute("icon");
						String ccondition = component.getAttribute("condition");
						
						boolean dcondition = component.getAttribute("dcondition").equals("true");
						boolean wscondition = component.getAttribute("wscondition").equals("true");

						Element position = (Element) component.getElementsByTagName("position").item(0);
						String xpos = position.getElementsByTagName("x").item(0).getTextContent();
						String ypos = position.getElementsByTagName("y").item(0).getTextContent();

						String cwidth = component.getElementsByTagName("width").item(0).getTextContent();
						String cheight = component.getElementsByTagName("height").item(0).getTextContent();

						Piece f = null;
						if (wscondition) {
							String cwsdl = component.getAttribute("wsdl");
							String coperation = component.getAttribute("operation");
							String cservice = component.getAttribute("service");
							String cport = component.getAttribute("port");
							String ctns = component.getAttribute("tns");
							String curi = component.getAttribute("uri");

							f = new WSOperation(ctype, act.getResources(), clabel, Integer.valueOf(xpos).intValue(), Integer.valueOf(ypos)
									.intValue(), grid);
							WSOperation op = (WSOperation) f;
							op.setWsdl(cwsdl);
							op.setOperation(coperation);
							op.setService(cservice);
							op.setPort(cport);
							op.setTns(ctns);
							op.setUri(curi);

							Element context = null;
							if (component.getElementsByTagName("context").getLength() > 0) {
								context = (Element) component.getElementsByTagName("context").item(0);
								readContext(context, op);
							}

						} else if(dcondition){
							String dname = component.getAttribute("name");
							String dstate = component.getAttribute("state");
							String dlink = component.getAttribute("link");
							String dtype = component.getAttribute("type");
						
							f = new DOperation(ctype, act.getResources(), clabel, Integer.valueOf(xpos).intValue(), Integer.valueOf(ypos)
									.intValue(), grid);
							DOperation op = (DOperation) f;
							op.setName(dname);
							op.setState(dstate);
							op.setLink(dlink);
							op.setType(dtype);
							
						} 
							f = new Piece(ctype, act.getResources(), clabel, Integer.valueOf(xpos).intValue(), Integer.valueOf(ypos)
									.intValue(), grid);

						f.setDescription(cdescription);
						f.setNowState(cstate);
						f.setAllStates(callStates);

						String subcicon = cicon.substring(cicon.lastIndexOf(".") + 1);

						String uri = "@drawable/" + subcicon;
						int res = getResources().getIdentifier(uri, null, act.getPackageName());
						Log.d("ICON" , "icon uri " + uri + " " + cicon + " " + subcicon);
						if (res != 0)
							f.setIconin(cicon, res); // android.R.drawable
						else
							f.setIconin("R.drawable.icon", R.drawable.icon);

						Element condition = null;
						condition = (Element) component.getElementsByTagName("condition").item(0);

						if (ccondition != null) {
							if (ccondition.equals("optional")) {
								f.setCondition(Constants.OPTIONAL_CONDITION);
								readCondition(condition, f);

							} else if (ccondition.equals("mandatory")) {
								f.setCondition(Constants.MANDATORY_CONDITION);
								readCondition(condition, f);
							} else
								f.setCondition(Constants.NO_CONDITION);
						} else
							f.setCondition(Constants.NO_CONDITION);

						f.setWidthScale(Integer.valueOf(cwidth).intValue());
						f.setHeightScale(Integer.valueOf(cheight).intValue());

						Element holes = (Element) component.getElementsByTagName("holes").item(0);
						NodeList innerholes = holes.getElementsByTagName("item");
						// int numHoles = 0;
						for (int iholes = 0; iholes < innerholes.getLength(); iholes++) {
							Element hole = (Element) innerholes.item(iholes);
							String numhole = hole.getTextContent();
							f.modifyHole(Integer.valueOf(numhole).intValue());
							// numHoles++;
						}
						Element extinputs = (Element) component.getElementsByTagName("inputs").item(0);
						NodeList inputs = extinputs.getElementsByTagName("input");
						for (int iinput = 0; iinput < inputs.getLength(); iinput++) {
							Element input = (Element) inputs.item(iinput);
							String ikind = input.getAttribute("kind");
							String ilabel = input.getAttribute("label");
							String idatatype= input.getAttribute("datatype");
							try {
								if (f.getIcon().equals("R.drawable.condition48")){
									f.addInputPin(new Pin(PinEnumerator.valueOf(ikind), PinParamEnumerator.valueOf(colorePrecedente), ilabel));
								}
								else f.addInputPin(new Pin(PinEnumerator.valueOf(ikind), PinParamEnumerator.valueOf(idatatype), ilabel));
							} catch (IllegalArgumentException ex) {
							}
						}

						NodeList extuserdata = component.getElementsByTagName("userdata");
						for (int iuser = 0; iuser < extuserdata.getLength(); iuser++) {
							Element userdata = (Element) extuserdata.item(iuser);

							String ulabel = userdata.getAttribute("name");
							String udatatype = userdata.getAttribute("datatype");
							String uinput = userdata.getAttribute("input");

							try {
								Pin npin=null;
								if (f.getIcon().equals("R.drawable.condition48")){
									if (!f.getDescription().equals("Dynamic Condition")){
										npin=new Pin(PinEnumerator.USER, PinParamEnumerator.valueOf("STRING"), ulabel);
										npin.setInput(uinput);
										npin.setObject(userdata.getTextContent());
										f.addInputPin(npin);
									}
								}
								else{
									npin = new Pin(PinEnumerator.USER, PinParamEnumerator.valueOf(udatatype), ulabel);
									npin.setInput(uinput);
									npin.setObject(userdata.getTextContent());
									f.addInputPin(npin);
								}

							} catch (IllegalArgumentException ex) {
							}

						}

						Element extoutputs = (Element) component.getElementsByTagName("outputs").item(0);
						NodeList outputs = extoutputs.getElementsByTagName("output");
						String odatatype=null;
						for (int ioutput = 0; ioutput < outputs.getLength(); ioutput++) {
							Element output = (Element) outputs.item(ioutput);
							String otype = output.getAttribute("type");
							String okind = output.getAttribute("kind");
							odatatype = output.getAttribute("datatype");
							if ((colorePrecedente==null) && (ioutput==0)) {colorePrecedente=odatatype;}
							String obinding = output.getAttribute("binding");

							try {
								if (!otype.equals("NULL")) {
									String oid = output.getAttribute("id");
									if (f.getIcon().equals("R.drawable.condition48")) {
										f.addOutputPin(new Pin(PinEnumerator.valueOf(okind), PinParamEnumerator.valueOf(colorePrecedente), obinding,
												oid, otype));
									} else  f.addOutputPin(new Pin(PinEnumerator.valueOf(okind), PinParamEnumerator.valueOf(odatatype), obinding,
											oid, otype));
								} else {
									if (f.getIcon().equals("R.drawable.condition48")) {
										f.addOutputPin(new Pin(PinEnumerator.valueOf(okind), PinParamEnumerator.valueOf(colorePrecedente), obinding));
									} else
										f.addOutputPin(new Pin(PinEnumerator.valueOf(okind), PinParamEnumerator.valueOf(odatatype), obinding));
								}
							} catch (IllegalArgumentException ex) {
							}

						}
						colorePrecedente=odatatype;
						f.setInputPin(annotatePins(f.getInputPin()));

						f.setId(cid);
						grid.addNewPiece(f);

						/*
						 * Element outputs = (Element)
						 * c.getElementsByTagName("outputs").item(0); NodeList
						 * ou = outputs.getElementsByTagName("output"); if (ou
						 * != null) { PinOut = new ArrayList<Pin>(); for (int j
						 * = 0; j < ou.getLength(); j++) { Element winput =
						 * (Element) ou.item(j);// output // j-esimo
						 * 
						 * String type = winput.getAttribute("type"); String
						 * param = winput.getElementsByTagName("param").item(0).
						 * getTextContent(); String text =
						 * winput.getElementsByTagName
						 * ("name").item(0).getTextContent(); Pin q = new
						 * Pin(PinEnumerator.valueOf(type),
						 * PinParamEnumerator.valueOf(param), text);
						 * PinOut.add(q);
						 * 
						 * } }
						 * 
						 * Element position = (Element)
						 * c.getElementsByTagName("positionmatrix").item(0); int
						 * xx =
						 * Integer.parseInt(position.getElementsByTagName("x"
						 * ).item(0).getTextContent()); int yy =
						 * Integer.parseInt
						 * (position.getElementsByTagName("y").item
						 * (0).getTextContent());
						 * 
						 * Element ponte = (Element)
						 * c.getElementsByTagName("ponte").item(0); NodeList
						 * ponti = ponte.getElementsByTagName("item"); if (ponti
						 * != null) { wponti = new ArrayList<Integer>(); for
						 * (int k = 0; k < ponti.getLength(); k++) { Element
						 * pItem = (Element) ponti.item(k);
						 * wponti.add(Integer.parseInt(pItem.getTextContent()));
						 * } } int larg =
						 * Integer.parseInt(c.getElementsByTagName
						 * ("larghezza").item(0).getTextContent()); int alt =
						 * Integer
						 * .parseInt(c.getElementsByTagName("altezza").item
						 * (0).getTextContent());
						 * 
						 * Piece Pezzo = new Piece(tid, act.getResources(), nom,
						 * xx, yy, grid); String key = icona.substring(11); key
						 * = key.toLowerCase(); int id =
						 * getResources().getIdentifier(key, "drawable",
						 * act.getPackageName()); if (id > 0) {
						 * Pezzo.setIcon(icona, id);
						 * 
						 * } else Pezzo.setIcon("R.drawable.icon",
						 * R.drawable.icon);
						 * 
						 * Pezzo.setWidthScale(larg - wponti.size());
						 * 
						 * Pezzo.setHeightScale(alt);
						 * 
						 * if (Pinin.size() > 0) Pezzo.setInputPin(Pinin); if
						 * (PinOut.size() > 0) Pezzo.setOutputPin(PinOut); if
						 * (wponti.size() > 0) for (int z = 0; z <
						 * wponti.size(); z++) { Pezzo.addHole(wponti.get(z)); }
						 * 
						 * grid.addNewPiece(Pezzo);
						 */
					}

					invalidate();
					Toast.makeText(getContext(), act.getString(R.string.loaded), Toast.LENGTH_LONG).show();
				}

			} catch (SAXException e) {
			} catch (IOException e) {
			} catch (ParserConfigurationException e) {
			} catch (FactoryConfigurationError e) {
			}
		}
	}

	private void readCondition(Element condition, Piece f) {
		int length = condition.getElementsByTagName("precondition").getLength();

		PreCondition[] preCondition = new PreCondition[length];
		f.setPreCondition(preCondition);
		for (int k = 0; k < length; k++) {
			preCondition[k] = new PreCondition();
			Element pre = (Element) condition.getElementsByTagName("precondition").item(k);
			String check = pre.getAttribute("check");
			String cond = pre.getAttribute("condition");
			String oper = pre.getAttribute("operator");
			String value = pre.getAttribute("value");

			if (check.equals("true"))
				preCondition[k].setCheck(true);
			else
				preCondition[k].setCheck(false);
			preCondition[k].setCondition(Integer.parseInt(cond));
			preCondition[k].setOperator(Integer.parseInt(oper));
			preCondition[k].setValue(value);

		}
	}

	private void readContext(Element context, WSOperation op) {
		Element time = (Element) context.getElementsByTagName("time").item(0);
		Element location = (Element) context.getElementsByTagName("location").item(0);

		WSContext con = new WSContext();
		con.setDate(new Date(Long.parseLong(time.getTextContent())));

		String lat = location.getAttribute("latitude");
		String longi = location.getAttribute("longitude");
		String alt = location.getAttribute("altitude");

		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(Double.parseDouble(lat));
		loc.setLongitude(Double.parseDouble(longi));
		loc.setAltitude(Double.parseDouble(alt));

		con.setLocation(loc);

		op.setContext(con);

	}

	public int validate(boolean strict) {
		if (grid.getPieces().size() == 0)
			return Constants.SENTENCE_EMPTY;

		grid.unselectPieces();

		boolean check = true;
		for (Piece p : grid.getPieces()) {
			for (Pin pin : p.getInputPin()) {
				if ((pin.getType() == PinEnumerator.USER)){
					if (pin.getObject() == null || pin.getObject().equals("")) {
						p.setHighlithed(true);
						check = false;
					}
				}
			}
		}
		if (!check) {
			invalidate();
			return Constants.SENTENCE_INPUT_MISSED;
		}
		
		if(strict) {
			int count_check = 0;
			ArrayList<String> connected = new ArrayList<String>();
			for (Piece p : grid.getPieces()) {
				for (Pin pin : p.getOutputPin()) {
					if(pin.getType() != PinEnumerator.USER && pin.getIdPinAggancio() != null) {
						connected.add(new String(pin.getIdPinAggancio() + "_" + pin.getText()));
					}	
				}
			}	
			System.out.println("\n");
			for (Piece p : grid.getPieces()) {
				for (Pin pin : p.getInputPin()) {
					if (pin.getType() != PinEnumerator.USER) {
						String key = p.id+"_" + pin.getText();
						if(!connected.contains(key)) {
							pin.setHighlithed(true);
							p.setHighlithed(true);
							count_check++;
						}
					}
				}
			}	
			if (count_check > 0) {
				invalidate();
				return Constants.SENTENCE_INPUT_UNUSED;
			}		
		}
		return Constants.SENTENCE_CORRECT;
	}

	public String save(String fileName, boolean delete, boolean temporary) {

		if (temporary) {
			fileName = Constants.tryFilePrefix + fileName;
		}
		String nameOfTheFile = fileName.trim() + Constants.extension;

		synchronized (_thread.getSurfaceHolder()) {
			if (delete) {
				File f = new File(FileManagement.getDefaultPath(), nameOfTheFile);
				if (f.exists()) {
					f.delete();
				}
			} else {
				File newxmlfile = null;
				File myAppDr = new File(FileManagement.getLocalAppPath());
				try {
					newxmlfile = new File(myAppDr, nameOfTheFile);
					newxmlfile.createNewFile();

				} catch (IOException e) {
					Utils.error("Exception in createNewFile() method " + e.getMessage());
				}

				FileOutputStream fileos = null;
				try {
					fileos = new FileOutputStream(newxmlfile);
				} catch (FileNotFoundException e) {
					Utils.error("Can't create FileOutputStream " + e.getMessage());
				}
				// we create a XmlSerializer in order to write xml data
				XmlSerializer serializer = Xml.newSerializer();
				try {
					// we set the FileOutputStream as output for the serializer,
					// using UTF-8 encoding
					serializer.setOutput(fileos, "UTF-8");
					serializer.startDocument(null, null);
					// set indentation option
					serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

					// start a tag called "root"
					serializer.startTag(null, "components");
					serializer.attribute(null, "xmlns", "http://www.unisa.it/DeploySchema.xsd");
					serializer.attribute(null, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
					serializer.attribute(null, "xsi:schemaLocation", "http://www.unisa.it/DeploySchema.xsd");
					serializer.attribute(null, "app", act.getString(R.string.app_name));
					serializer.attribute(null, "version", act.getString(R.string.app_version));

					if (icon != null && !icon.equals("")) {
						serializer.startTag(null, "icon");
						serializer.text(icon);
						serializer.endTag(null, "icon");
					}
					serializer.startTag(null, "description");
					if (description != null) {
						serializer.text(description);
					}
					serializer.endTag(null, "description");

					serializer.startTag(null, "activator");
					if (activator != null) {
						serializer.text(activator);
					}
					serializer.endTag(null, "activator");

					List<Piece> Pezzi = grid.getPieces();

					for (int i = 0; i < Pezzi.size(); i++) {
						Piece p = Pezzi.get(i);
						serializer.startTag(null, "component");

						serializer.attribute(null, "id", p.getId());
						serializer.attribute(null, "type", p.getIdLogic());
						serializer.attribute(null, "state", p.getNowState());
						serializer.attribute(null, "allStates", p.getAllStates());
						serializer.attribute(null, "label", p.getText());
						serializer.attribute(null, "icon", p.getIcon());
						serializer.attribute(null, "condition",
								p.isCondition() ? (p.hasCondition == Constants.OPTIONAL_CONDITION ? "optional" : "mandatory") : "false");

						serializer.attribute(null, "wscondition", p instanceof WSOperation ? "true" : "false");
						serializer.attribute(null, "dcondition", p instanceof DOperation ? "true" : "false");
						
						if (p instanceof WSOperation) {
							WSOperation op = (WSOperation) p;
							serializer.attribute(null, "wsdl", op.getWsdl());
							serializer.attribute(null, "operation", op.getOperation());
							serializer.attribute(null, "service", op.getService());
							serializer.attribute(null, "port", op.getPort());
							serializer.attribute(null, "tns", op.getTns());
							serializer.attribute(null, "uri", op.getUri());
						} 
						else 
							if (p instanceof DOperation) {
							DOperation op = (DOperation) p;
							serializer.attribute(null, "dname", op.getName());
							serializer.attribute(null, "dstate", op.getState());
							serializer.attribute(null, "dlink", op.getLink());
							serializer.attribute(null, "dtype", op.getType());
							}

							serializer.startTag(null, "description");
							serializer.text(p.getDescription());
							serializer.endTag(null, "description");

						if (p instanceof WSOperation) {		
							WSOperation op = (WSOperation) p;					
							serializer.startTag(null, "context");	
							WSContext con = op.getContext();
							writeWSContext(serializer, con);
							serializer.endTag(null, "context");
						}

						if (p.isCondition()) {
							serializer.startTag(null, "condition");
							// TODO: save the condition

							PreCondition[] listCondition = p.getPreCondition();
							writeCondition(serializer, listCondition);

							serializer.endTag(null, "condition");
						}

						serializer.startTag(null, "inputs");

						List<Pin> in = p.getInputPin();
						for (int j = 0; j < in.size(); j++) {
							Pin pin = in.get(j);

							if (pin.getType() != PinEnumerator.USER) {
								serializer.startTag(null, "input");
								serializer.attribute(null, "kind", pin.getType().toString());
								serializer.attribute(null, "label", pin.getText());
								serializer.attribute(null, "datatype", pin.getParam().toString());
								serializer.endTag(null, "input");
							}
						}

						serializer.endTag(null, "inputs");

						serializer.startTag(null, "outputs");

						List<Pin> ou = p.getOutputPin();
						for (int j = 0; j < ou.size(); j++) {
							Pin pin = ou.get(j);

							serializer.startTag(null, "output");

							if (pin.getAggancio() != null) {
								serializer.attribute(null, "id", pin.getIdPinAggancio());
								serializer.attribute(null, "type", pin.getAggancio());
							} else {
								serializer.attribute(null, "type", "NULL");
							}

							serializer.attribute(null, "kind", pin.getType().toString());
							serializer.attribute(null, "datatype", pin.getParam().toString());
							if (p.getIcon().equals("R.drawable.condition48")){
								serializer.attribute(null, "binding", pin.getText().toString()+j);
							}
							 else serializer.attribute(null, "binding", pin.getText().toString());

							serializer.startTag(null, "param");
							serializer.text(pin.getParam().toString());
							serializer.endTag(null, "param");

							serializer.startTag(null, "name");
							serializer.text(pin.getText());
							serializer.endTag(null, "name");
							serializer.endTag(null, "output");

						}

						serializer.endTag(null, "outputs");
						boolean userCondAdded=false;
						in = p.getInputPin();
						for (int j = 0; j < in.size(); j++) {
							Pin pin = in.get(j);

							if (pin.getType() == PinEnumerator.USER) {
								serializer.startTag(null, "userdata");
								serializer.attribute(null, "name", pin.getText());
								serializer.attribute(null, "datatype", pin.getParam().toString());
								serializer.attribute(null, "input", pin.getInput().toString());

								Object obj = pin.getObject();
								if (obj != null) {
									serializer.text(obj.toString());
								}
								serializer.endTag(null, "userdata");
							}
							else if (p.getDescription().equals("Dynamic Condition")){
								if (!userCondAdded){
									userCondAdded=true;
									serializer.startTag(null, "userdata");
									serializer.attribute(null, "name", pin.getText());
									serializer.attribute(null, "datatype", pin.getParam().toString());
									serializer.attribute(null, "input", pin.getInput().toString());

									Object obj = pin.getObject();
									if (obj != null) {
										serializer.text(obj.toString());
									}
									serializer.endTag(null, "userdata");

								}
							}
						}

						serializer.startTag(null, "layout");

						serializer.startTag(null, "position");
						serializer.startTag(null, "x");
						Integer pos = (p.getLogicPosition().x);
						serializer.text(pos.toString());
						serializer.endTag(null, "x");

						serializer.startTag(null, "y");
						pos = (p.getLogicPosition().y);
						serializer.text(pos.toString());
						serializer.endTag(null, "y");

						serializer.endTag(null, "position");

						List<Integer> ponti = p.getHoles();
						serializer.startTag(null, "holes");
						for (int k = 0; k < ponti.size(); k++) {
							serializer.startTag(null, "item");
							Integer q = ponti.get(k);
							serializer.text(q.toString());
							serializer.endTag(null, "item");
						}
						serializer.endTag(null, "holes");

						serializer.startTag(null, "width");
						pos = p.getWidthScale();
						serializer.text(pos.toString());
						serializer.endTag(null, "width");

						serializer.startTag(null, "height");
						pos = p.getHeightScale();
						serializer.text(pos.toString());
						serializer.endTag(null, "height");

						serializer.endTag(null, "layout");

						serializer.endTag(null, "component");

					}

					serializer.endTag(null, "components");
					serializer.endDocument();
					// write xml data into the FileOutputStream
					serializer.flush();

					if (!temporary) {
						// File gestures=null;
						myAppDr = new File(FileManagement.getGestureDir());
						Toast.makeText(getContext(), act.getString(R.string.saved), Toast.LENGTH_LONG).show();

						grid.setModified(false);
					}
				} catch (Exception e) {
					Toast.makeText(getContext(), "Error occurred while creating the .xml file!" + e.getCause(), Toast.LENGTH_LONG).show();

					Utils.error("Error occurred while creating xml file:" + e.getMessage());
				} finally {
					try {
						if (fileos != null)
							fileos.close();
					} catch (IOException e) {
					}
				}
			}
		}

		return nameOfTheFile;
	}

	private void writeCondition(XmlSerializer serializer, PreCondition[] listCondition) throws IllegalArgumentException,
			IllegalStateException, IOException {
		int length = listCondition.length;

		for (int k = 0; k < length; k++) {
			serializer.startTag(null, "precondition");
			boolean check = listCondition[k].isCheck();
			int condition = listCondition[k].getCondition();
			int operator = listCondition[k].getOperator();
			String value = listCondition[k].getValue();

			serializer.attribute(null, "check", "" + check);
			serializer.attribute(null, "condition", "" + condition);
			serializer.attribute(null, "operator", "" + operator);
			serializer.attribute(null, "value", value);

			serializer.endTag(null, "precondition");
		}
	}

	private void writeWSContext(XmlSerializer serializer, WSContext con) throws IllegalArgumentException, IllegalStateException,
			IOException {
		// scrivo contesto time
		serializer.startTag(null, "time");
		serializer.text("" + con.getDate().getTime());
		serializer.endTag(null, "time");

		// scrivo contesto location
		serializer.startTag(null, "location");
		serializer.attribute(null, "latitude", "" + con.getLocation().getLatitude());
		serializer.attribute(null, "longitude", "" + con.getLocation().getLongitude());
		serializer.attribute(null, "altitude", "" + con.getLocation().getAltitude());
		serializer.endTag(null, "location");
	}

	public void news() {
		synchronized (_thread.getSurfaceHolder()) {
			_xOffset = 0;
			_yOffset = 0;
			calculateLoopBorders();
			invalidate();
			grid.news();

			invalidate();

		}
	}

	public void Caricamento(boolean selezione, int inserimenti, boolean entra, boolean StopinseriMulti, boolean Vmulti, int multiconta,
			int multico, int userCont, boolean UserIns, int pont, int conta, String padre, Piece X) {
		this.conta = conta;
		this.pont = pont;
		this.UserIns = UserIns;
		this.userCont = userCont;
		this.multico = multico;
		this.multiconta = multiconta;
		this.Vmulti = Vmulti;
		this.StopinseriMulti = StopinseriMulti;
		this.entra = entra;
		this.inserimenti = inserimenti;
		this.selezione = selezione;
		this.padre = padre;
		this.X1 = X;

		// this.showSelected = true;
	}

	public boolean onTouch(View view, MotionEvent event) {
		synchronized (_thread.getSurfaceHolder()) {
			if (gestures.onTouchEvent(event) == false) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// start of a new event, reset the flag
					_isMoving = false;
					// store the current touch coordinates for scroll
					// calculation
					_xTouch = (int) event.getX();
					_yTouch = (int) event.getY();

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					Utils.debug("2");
					// touch starts moving, set the flag
					// _isMoving = true;

					// get the new offset
					if (Math.abs(_xTouch - (int) event.getX()) >= Constants.GRID_RATIO) {
						_xOffset += _xTouch - (int) event.getX();
						_isMoving = true;
					}
					if (Math.abs(_yTouch - (int) event.getY()) >= Constants.GRID_RATIO) {
						_yOffset += _yTouch - (int) event.getY();
						_isMoving = true;
					}
					// secure, that the offset is never out of view bounds if

					if (grid.getNumCols() < Constants.GRID_WSIZE) {
						_xOffset = 0;
					} else if (_xOffset < 0) {
						_xOffset = 0;
					} else if (_xOffset > grid.getNumCols() * Constants.GRID_RATIO - getWidth()) {
						_xOffset = grid.getNumCols() * Constants.GRID_RATIO - getWidth();
					}

					if (grid.getNumRows() < Constants.GRID_HSIZE) {
						_yOffset = 0;
					} else if (_yOffset < 0) {
						_yOffset = 0;
					} else if (_yOffset > (grid.getNumRows() + 1) * Constants.GRID_RATIO - getHeight()) {
						_yOffset = (grid.getNumRows() + 1) * Constants.GRID_RATIO - getHeight();
					}
					// ci da l'Xoffet come multiplo della dimensione delle
					// colonne
					_xOffset = (int) Math.floor(_xOffset / Constants.GRID_RATIO) * Constants.GRID_RATIO;

					// ci da Yoffset come multiplo della dimensione delle righe
					_yOffset = (int) Math.floor(_yOffset / Constants.GRID_RATIO) * Constants.GRID_RATIO;

					// store the last position
					_xTouch = (int) event.getX();
					_yTouch = (int) event.getY();
					calculateLoopBorders();
					invalidate();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// touch released
					if (!_isMoving) {
						float wy = event.getY();
						if (wy < Constants.SCREEN_SPLIT && selezione) {

							if (UserIns) {
								// caricamento info
								Utils.debug("3");
								createObject();
								// ***************** fine codice aggiunto

								List<Pin> in = X1.getInputPin();
								X.setInputPin(annotatePins(in));

								List<Pin> ou = X1.getOutputPin();
								X.setOutputPin(ou);

								String key = X1.getIcon();
								if(key == null || key.equals("")){
									key = padre.toLowerCase(Locale.getDefault()) + "48";
								}
								int id = getResources().getIdentifier(key, "drawable", act.getPackageName());
								Log.d("ICON", "icon key 1496" + key);
								if (id > 0) {
									Piz.setIconin("R.drawable." + key, id);
									X.setIconin("R.drawable." + key, id);
								} else {
									Piz.setIconin("R.drawable.icon", R.drawable.icon);
									X.setIconin("R.drawable.icon", R.drawable.icon);
								}
							}
							int indice = -1;
							boolean soloUser = false;
							boolean NonTrovato = true;
							boolean trova = false;
							boolean NonInput = false;
							boolean ultimo = false;
							boolean colonnaLibera = false;
							int parte = 0;
							List<Integer> Aggancio = null;
							Piece pmout = null;
							pinoutSelezionato = null;
							int qp = 0;
							boolean allunga = false;
							int taglia = X.getInputPin().size();
							List<Pin> pin = X.getInputPin();

							if (UserIns) {
								Utils.debug("4");
								// single
								if (taglia == 0) {
									NonInput = true;
								} else {
									Utils.debug("5");
									// pin=X.getInputPin();
									for (int i = 0; i < taglia; i++) {
										if (pin.get(i).getType() == PinEnumerator.USER) {
											userCont++;
										}
									}
									if (userCont == taglia)
										soloUser = true;

									userCont = 0;
								}
								// end single
							}

							Point riga = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset);
							if (riga.y > 1 /* && !NonInput && !soloUser */) {
								Utils.debug("6");
								// se non siamo nella prima riga, prendo
								// l'output di sopra

								Point lpout = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset
										- Constants.GRID_RATIO);
								pmout = (Piece) grid.getMatrixPiece(lpout);

								if (pmout != null)
									Utils.debug("pmount:" + pmout.showInfo());

								if (pmout != null) {
									Utils.debug("7");
									parte = pmout.RitornaPezzo(pmout, lpout.x);
									if (pmout.getAggancioOut().size() >= parte) {
										Utils.debug("8");
										Aggancio = pmout.getAggancioOut();
										if (Aggancio.get(parte - 1) == 0) {
											Utils.debug("9");
											pinoutSelezionato = pmout.getOutputPin().get(parte - 1);

											if(X.getIcon().equals("R.drawable.condition48")){
												List<Pin> inX=X.getInputPin();
												List<Pin> ouX=X.getOutputPin();
												codiceColoreCondition=pinoutSelezionato.getColor();
												for(Pin p: inX){
													if (p.getType()==PinEnumerator.USER) p.setColor(Color.RED);
													else p.setColor(pinoutSelezionato.getColor());
												}
												for(Pin p: ouX){
													p.setColor(pinoutSelezionato.getColor());
												}
											}
											if (pinoutSelezionato != null)
												Utils.debug("pinselect:" + pinoutSelezionato.description());
										} else {
											Utils.debug("10");
											Point lPrec = grid.calcLogicPosition(PrecX, PrecY);
											if (riga.x == lPrec.x)
												ultimo = true;
										}
									} else {
										Utils.debug("11");
										Toast.makeText(getContext(), "Invalid position", Toast.LENGTH_LONG).show();
										NonTrovato = false;
										// si puo inserie il pezzo senza input
									}
								} else if (pmout == null) {// else Si vede se si
															// puo allungare il
															// pezzo
															// superiore
									Utils.debug("12");
									qp = 1;
									while (qp - riga.y != 0 && pmout == null) {
										qp++;
										lpout = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset
												- (qp * Constants.GRID_RATIO));
										pmout = (Piece) grid.getMatrixPiece(lpout);

									}
									if (pmout != null) {
										Utils.debug("13");
										// if(pmout.getWidthScale()==1){
										if (pmout.TuttiOutputLiberi()) {

											parte = pmout.RitornaPezzo(pmout, lpout.x);
											if (pmout.getAggancioOut().size() >= parte) {
												Utils.debug("14");
												Aggancio = pmout.getAggancioOut();
												if (Aggancio.get(parte - 1) == 0) {
													Utils.debug("15");
													// Aggancio.set(parte-1, 1);
													pinoutSelezionato = pmout.getOutputPin().get(parte - 1);
													allunga = true;
												} else
													Toast.makeText(getContext(), "Pin already used", Toast.LENGTH_LONG).show();

											} else {
												Utils.debug("16");
												Toast.makeText(getContext(), "Invalid position", Toast.LENGTH_LONG).show();
												NonTrovato = false;
												// si puo inserie il pezzo senza
												// input
											}
										} else {
											Utils.debug("17");
											NonTrovato = false;
											Toast.makeText(getContext(), "Some input Pin already used", Toast.LENGTH_SHORT).show();

										}

									} else {
										Utils.debug("18");
										// colonna libera il pezzo si puo
										// inserire
										// NonTrovato=false;

										if (Piz.getLogicPosition().x == 0 && Piz.getLogicPosition().y == 0) {
											Utils.debug("19");
											colonnaLibera = true;
											selezione = false;
											grid.unselectPieces();
											Piz.setSnapPosition(event.getX() + _xOffset, event.getY() + _yOffset);

											Piz.setInputPin(annotatePins(X.getInputPin()));

											Piz.setOutputPin(X.getOutputPin());
											for (int i = 0; i < taglia; i++) {
												if (pin.get(i).getType() == PinEnumerator.USER) {
													userCont++;
												}
											}

											Piz.setWidthScale(Math.max(Piz.getOutputPin().size(), Piz.getInputPin().size() - userCont));
											userCont = 0;
											Utils.vibrate(act);
											grid.addNewPiece(Piz);

											// showSelected = false;
											invalidate();

										}

									}
								}

							}

							// input User frecce
							if (UserIns && !NonInput) {
								Utils.debug("20");
								for (int i = 0; i < taglia; i++) {
									if (pin.get(i).getType() == PinEnumerator.USER) {
										userCont++;
									}
								}
								UserIns = false;
							}

							taglia = taglia - userCont;
							// ****************************************************************
							if (!colonnaLibera) {
								Utils.debug("21");
								if (NonTrovato && !NonInput && !soloUser && pinoutSelezionato != null && !ultimo) {
									Utils.debug("22");
									for (int i = conta; i < taglia; i++) {
										if (pin.get(i).getType() == PinEnumerator.NORMAL
												&& pinoutSelezionato.getType() == PinEnumerator.NORMAL) {
											if (pin.get(i).getColor() == pinoutSelezionato.getColor()) {
												indice = i;
												trova = true;
												// Aggancio.set(parte-1, 1);
												break;
											}
										}
									}
									if (!trova) {
										Utils.debug("23");
										for (int i = 0; i < taglia; i++) {
											if (pin.get(i).getType() == PinEnumerator.MULTI) {
												indice = i;
												trova = true;
												break;
											}
										}
									}

									// scambio posizione
									if (trova) {
										Utils.debug("24");
										if (allunga) {
											Utils.debug("25");
											pmout.setHeightScale(qp);
											grid.updateMatrix(pmout, true);
											grid.checkFirstRowMatrix();
											grid.updateParams();
											grid.updateGrid();
											invalidate();
										}
										Pin apin = pin.get(indice);
										pin.set(indice, pin.get(conta));
										pin.set(conta, apin);
									}
								}
							}/*
							 * else{ trova=true;
							 * 
							 * }
							 */
							// ****************************************************************
							if (ultimo)
								trova = true;

							if (conta < taglia && trova && NonTrovato && !NonInput && !soloUser) {
								Utils.debug("26");

								Vmulti = false;
								multico = 0;
								if (pin.get(conta).getType() != PinEnumerator.USER) {
									Utils.debug("27");
									if (pin.get(conta).getType() == PinEnumerator.MULTI) {
										Utils.debug("28");
										Vmulti = true;
										multiconta++;
										multico = 1;
									}

									Point lp = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset);
									Piece pm = (Piece) grid.getMatrixPiece(lp);
									// Utils.debug(pm.toString());
									if (conta > 0 || multiconta > 1 || inserimenti > 0) {
										Utils.debug("29");
										// confronto per il ponte
										Point lpAtt = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset);
										Point lpPrec = grid.calcLogicPosition(PrecX, PrecY);

										if ((lpAtt.x - lpPrec.x) > 1) {
											Utils.debug("30");

											cicla = (lpAtt.x - lpPrec.x) - 1;
											ponte = true;
										}

										if (Vmulti && (lpAtt.x - lpPrec.x) == 0 && pm != null && multiconta > 1) {
											Utils.debug("31");
											// confronto ultimo inserimeto multi
											StopinseriMulti = false;
										}
										if (ultimo)
											StopinseriMulti = false;
									}

									PrecX = event.getX() + _xOffset;
									PrecY = event.getY() + _yOffset;

									if (entra) {
										Utils.debug("32");
										inizialeX = event.getX() + _xOffset;
										inizialeY = event.getY() + _yOffset;
										entra = false;
									}
									if (pm == null) {
										Utils.debug("33");
										if (!grid.isShowIconized()) {
											Utils.debug("34");
											if (!Vmulti)
												conta++;
											if (conta + multiconta > 1)
												grid.undo();

											grid.unselectPieces();

											Piz.setSnapPosition(inizialeX, event.getY() + _yOffset);

											Utils.debug("pin 34:" + pin.get(conta - 1 + multico).description());

											Piz.addInputPin(pin.get(conta - 1 + multico));
											Piz.setSelected(true);
											int po = Piz.getInputPin().size() + pont;

											if (ponte) {
												Utils.debug("35");
												for (int cicl = 0; cicl < cicla; cicl++) {
													Piz.addHole(po);
													po++;
												}
												pont++;

											}

											if (pinoutSelezionato != null) {
												pinoutSelezionato.setAggancio(Piz.getIdLogic());
												pinoutSelezionato.setIdPinAggancio(Piz.getId());
												Pin p = pin.get(conta - 1 + multico);
												pinoutSelezionato.setText(p.getText());
												Utils.debug("pinoutModificato:" + pinoutSelezionato.description());
											}

											// screenShot = screen();
											inserimenti++;
											Utils.vibrate(act);

											grid.addNewPiece(Piz);

											ponte = false;
											invalidate();

										}
									} else if (!StopinseriMulti) {
										Utils.debug("36");
										conta++;
										grid.unselectPieces();
										Utils.vibrate(act);

										pm.setSelected(true);
										invalidate();
									} else {
										Utils.debug("37");
										grid.unselectPieces();
										pm.setSelected(true);
										Toast.makeText(getContext(), "Click again", Toast.LENGTH_SHORT).show();
										invalidate();
									}

								}// if != User

							}/* if conta */else if (!NonInput && soloUser) {
								// Toast.makeText(getContext(),
								// "Pezzo inserito con soli INPUT_USER",
								// Toast.LENGTH_SHORT).show();
							} else if (NonInput) {
								// Toast.makeText(getContext(),
								// "Pezzo inserito senza INPUT ",
								// Toast.LENGTH_SHORT).show();
							} else {
								if (!colonnaLibera) {
									Toast.makeText(getContext(), act.getString(R.string.typeMismatch), Toast.LENGTH_SHORT).show();

									List<Pin> outPr = X.getOutputPin();
									for (int i = 0; i < X.getOutputPin().size(); i++) {
										Piz.addOutputPin(outPr.get(i));
									}

									selezione = true;
								} else {
									selezione = false;
								}
								invalidate();
							}

							if (conta >= taglia) {
								Utils.debug("38");
								if ((riga.y == 1 && NonInput) || (riga.y == 1 && soloUser)) {
									Utils.debug("39");
									// inserimento alla prima riga di pezzi con
									// input User o senza input
									for (int i = 0; i < userCont; i++) {// infine
																		// vado
																		// ad
																		// inserire
																		// eventuali
																		// pin.User
																		// se ci
																		// sono
										if (!soloUser)
											grid.undo();
										grid.unselectPieces();
										if (soloUser)
											Piz.setSnapPosition(event.getX() + _xOffset, event.getY() + _yOffset);
										Pin Piin = pin.get(conta + i);

										if (pinoutSelezionato != null) {
											pinoutSelezionato.setAggancio(Piz.getIdLogic());
											pinoutSelezionato.setIdPinAggancio(Piz.getId());
											Pin p = pin.get(conta + i);
											pinoutSelezionato.setText(p.getText());
											Utils.debug("pinoutModificato:" + pinoutSelezionato.description());
										}

										Piz.addInputPin(Piin);
										Piz.setSelected(true);
										Utils.vibrate(act);

										grid.addNewPiece(Piz);
										invalidate();
									}
								} else if (riga.y > 1 && (!NonInput && !soloUser)) {
									Utils.debug("40");
									for (int i = 0; i < userCont; i++) {// infine vado
																		// ad inserire
																		// eventuali pin.User
																		// se ci sono
										if (!soloUser)
											grid.undo();
										grid.unselectPieces();
										if (soloUser)
											Piz.setSnapPosition(event.getX() + _xOffset, event.getY() + _yOffset);

										Utils.debug("pin 40:" + pin.get(conta + i).description());
										//TODO: verificare la cancellazione (scrive il text sbagliato nel pin di putput)
										/*
										if (pinoutSelezionato != null) {
											pinoutSelezionato.setAggancio(Piz.getIdLogic());
											pinoutSelezionato.setIdPinAggancio(Piz.getId());
											Pin p = pin.get(conta + i);
											pinoutSelezionato.setText(p.getText());
											Utils.debug("pinoutModificato:" + pinoutSelezionato.description());
										}*/

										Piz.addInputPin(pin.get(conta + i));
										Piz.setSelected(true);
										Utils.vibrate(act);

										grid.addNewPiece(Piz);
										invalidate();
									}
								} else if (!colonnaLibera)
									Toast.makeText(getContext(), " Invalid input", Toast.LENGTH_SHORT).show();

								List<Pin> outPr = X.getOutputPin();
								boolean combacia = true;
								if ((riga.y == 1 && NonInput) || (riga.y == 1 && soloUser)) {
									Utils.debug("41");
									// inserimento Output alla prima riga di
									// pezzi
									// con input User o senza input
									for (int qq = 0; qq < outPr.size(); qq++) {
										Point punto = grid.calcLogicPosition(event.getX() + _xOffset + (qq * Constants.GRID_RATIO),
												event.getY() + _yOffset + Constants.GRID_RATIO);
										Piece pezzosotto = (Piece) grid.getMatrixPiece(punto);
										if (pezzosotto != null) {
											int parte1 = pezzosotto.RitornaPezzo(pezzosotto, punto.x);
											List<Pin> Listainput = pezzosotto.getInputPin();
											if (Listainput.size() > 0) {
												Pin input = Listainput.get(parte1 - 1);

												if (input != null && input.getType() == outPr.get(qq).getType())
													combacia = combacia && true;
												else
													combacia = combacia && false;
											} else
												combacia = combacia && false;
										} else if (grid.getNumCols() - 1 > riga.x + qq) {
											Utils.debug("42");
											combacia = false;
											// Toast.makeText(getContext(),
											// " si crea Buco",
											// Toast.LENGTH_SHORT).show();
										}

									}

									if (combacia) {
										Utils.debug("43");
										for (int i = 0; i < X.getOutputPin().size(); i++) {
											if (!NonInput)
												grid.undo();
											grid.unselectPieces();
											if (NonInput) {
												Piz.setSnapPosition(event.getX() + _xOffset, event.getY() + _yOffset);
												NonInput = false;
											}

											Utils.debug(outPr.get(i).description());

											Piz.addOutputPin(outPr.get(i));
											Piz.setSelected(true);
											Utils.vibrate(act);

											grid.addNewPiece(Piz);
											invalidate();

											Point p = Piz.getLogicPosition();
											Utils.debug("pos:" + p.x + " " + p.y);

											Piece pi = (Piece) grid.getMatrixPiece(new Point(p.x, p.y + 1));

											if (pi != null) {
												outPr.get(i).setAggancio(pi.getIdLogic());
												outPr.get(i).setIdPinAggancio(pi.getId());
												Pin pp = pi.getInputPin().get(i);
												outPr.get(i).setText(pp.getText());

												Utils.debug("pinoutModificato:" + outPr.get(i).description());
											}
										}
									} else
										Toast.makeText(getContext(), " Invalid input", Toast.LENGTH_SHORT).show();

								} else if (riga.y > 1 && (!NonInput && !soloUser)) {
									for (int i = 0; i < X.getOutputPin().size(); i++) {
										if (!NonInput)
											grid.undo();
										grid.unselectPieces();
										if (NonInput) {
											Piz.setSnapPosition(event.getX() + _xOffset, event.getY() + _yOffset);
											NonInput = false;
										}

										Utils.debug("pin 44:" + outPr.get(i).description());

										if (pinoutSelezionato != null) {
											pinoutSelezionato.setAggancio(Piz.getIdLogic());
											pinoutSelezionato.setIdPinAggancio(Piz.getId());

											Utils.debug("pinoutModificato:" + pinoutSelezionato.description());
										}

										Piz.addOutputPin(outPr.get(i));
										Piz.setSelected(true);
										Utils.vibrate(act);

										grid.addNewPiece(Piz);
										invalidate();
									}
								} else if (!colonnaLibera)
									Toast.makeText(getContext(), " Invalid input", Toast.LENGTH_SHORT).show();

								selezione = false;

							}
						}// se non e' stata effettuata la selezione
						else {
							Point lp = grid.calcLogicPosition(event.getX() + _xOffset, event.getY() + _yOffset);
							Piece pm = (Piece) grid.getMatrixPiece(lp);
							if (pm != null) {
								grid.unselectPieces();
								pm.setSelected(true); // solo click per
														// selezione
								invalidate();
							} else {
								grid.unselectPieces(); // se clicca nel
														// vuoto
								invalidate();
							}
						}

					}

				}

			}
			return true;
		}
	}

	/**
	 * metodo usato per inizializzare gli elementi X1 oggetto selezionato
	 * dall'utente Piz pezzo da creare ed inserire nell'editor
	 */
	private void createObject() {
		// TODO:inserire qui eventuali nouvi tipi oggetti

		String tid = X1.getIdLogic();
		String te = X1.getText();

		// controllo i vari casi
		// attualmente 2
		// 1 X1 e un WebService
		// 2 e un oggetto locale

		if (X1 instanceof WSOperation)// X1 e un WS
		{
			// OK allora creo un nuovo oggetto WSOperation
			X = new Piece(tid, act.getResources(), te, 0, 0, grid);
			WSOperation x = new WSOperation(tid, act.getResources(), te, 0, 0, grid);
			WSOperation x1 = (WSOperation) X1;

			x.setWsdl(x1.getWsdl());
			x.setOperation(x1.getOperation());
			x.setService(x1.getService());
			x.setPort(x1.getPort());
			x.setTns(x1.getTns());
			x.setUri(x1.getUri());
			x.setContext(x1.getContext());
			Piz = x;
		} else 
			if(X1 instanceof DOperation){
				X = new Piece(tid, act.getResources(), te, 0, 0, grid);
				DOperation x = new DOperation(tid, act.getResources(), te, 0, 0, grid);
				DOperation x1 = (DOperation) X1;

				x.setName(x1.getName());
				x.setState(x1.getState());
				x.setLink(x1.getLink());
				x.setType(x1.getType());
				Piz = x;
			} 
		else// caso 2 e un oggetto locale
		{
			X = new Piece(tid, act.getResources(), te, 0, 0, grid);
			Piz = new Piece(tid, act.getResources(), te, 0, 0, grid);
		}

		X.setDescription(X1.getDescription());
		Piz.setDescription(X1.getDescription());
		X.setNowState(X1.getNowState());
		Piz.setNowState(X1.getNowState());
		X.setAllStates(X1.getAllStates());
		Piz.setAllStates(X1.getAllStates());
	}

	private void calculateLoopBorders() {
		//
		// Calculate the current cell column and row.
		this.curCellColumn = _xOffset / Constants.GRID_RATIO;
		this.curCellRow = _yOffset / Constants.GRID_RATIO;

		//
		// Because we're implementing a "smooth scroll" on a per pixel basis,
		// and
		// not a "per cell" basis, get the current X and Y offsets into the
		// current
		// cell.
		//
		// mXScrollOffset = _xOffset % this.cellWidth;
		// mYScrollOffset = _yOffset % this.cellHeight;

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (_thread.getState() == Thread.State.TERMINATED) {
			_thread = new SurfaceThread(getHolder(), this);
			_thread.setRunning(true);
			_thread.start();
		} else {
			_thread.setRunning(true);
			_thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		_thread.setRunning(false);
		while (retry) {
			try {
				_thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	public void setObjectPin(Pin pin, Object object) {
		pin.setObject(object);
	}

	public void annotatePinsDynamicCondition(List<Pin> p){
		//if ((p.size()!=0)&&(codiceColoreCondition==0)) codiceColoreCondition=p.get(0).getColor();
		for (final Pin pin : p) {
					if (!flagCondition){
						flagCondition=true;
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder builder = new AlertDialog.Builder(act)
											.setTitle("Condition Manager");
									final FrameLayout frameView = new FrameLayout(act);
									builder.setView(frameView);
									final AlertDialog alertDialog = builder.create();
									LayoutInflater inflater=alertDialog.getLayoutInflater();
									final View dialoglayout = inflater.inflate(R.layout.alertcondgui, frameView);
									ConditionGUIParam condGUI=new ConditionGUIParam(pin,act,builder,grid,dialoglayout);
									if (codiceColoreCondition== Color.CYAN){
										condGUI.createDynamicContactGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});
										builder.show();
									}
									if (codiceColoreCondition==Color.RED){
										condGUI.createDynamicStringGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});

										builder.show();
									}
									if (codiceColoreCondition==Color.MAGENTA){
										condGUI.createDynamicImageGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});
										builder.show();
									}
									if (codiceColoreCondition==Color.rgb(128, 0, 0)){
										condGUI.createDynamicLocationGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});
										builder.show();
									}


									// TODO: fill the user contact
								}
							}
						});
					}


		}
	}


	int mYear;
	int mMonth;
	int mDay;
	public List<Pin> annotatePins(List<Pin> p) {
		//boolean flagCondition=false;
		for (final Pin pin : p) {
			Log.d("PIN", "pin "+ pin.getParam() + pin.getInput());
			if (pin.getType() == PinEnumerator.USER) {
				switch (pin.getParam()) {
				case CONTACT:
					pin.setListener(new OnPinClickListener() {

						@Override
						public void onPinClick() {
							synchronized (_thread.getSurfaceHolder()) {
								Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
								act.startActivityForResult(intent, Constants.ID_RESULT_CONTACT);
								MainPanel.lastPin = pin;
								// TODO: fill the user contact
							}
						}
					});
					break;
				case IMAGE:
					pin.setListener(new OnPinClickListener() {

						@Override
						public void onPinClick() {
							synchronized (_thread.getSurfaceHolder()) {
								Intent intent = new Intent(Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
								act.startActivityForResult(intent, Constants.ID_SELECT_IMAGE);
								MainPanel.lastPin = pin;
								// TODO: fill the image
							}
						}
					});

					synchronized (_thread.getSurfaceHolder()) {
					}
					break;

					case CONDITION:
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder builder = new AlertDialog.Builder(act)
											.setTitle("Condition Manager");
									final FrameLayout frameView = new FrameLayout(act);
									builder.setView(frameView);
									final AlertDialog alertDialog = builder.create();
									LayoutInflater inflater=alertDialog.getLayoutInflater();
									final View dialoglayout = inflater.inflate(R.layout.alertcondgui, frameView);
									ConditionGUIParam condGUI=new ConditionGUIParam(pin,act,builder,grid,dialoglayout);
									if (codiceColoreCondition== Color.CYAN){
										condGUI.createContactGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});

										builder.show();
									}
									if (codiceColoreCondition==Color.RED){
										condGUI.createStringGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});

										builder.show();
									}
									if (codiceColoreCondition==Color.MAGENTA){
										condGUI.createImageGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});
										builder.show();
									}
									if (codiceColoreCondition==Color.rgb(128, 0, 0)){
										condGUI.createLocationGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});
										builder.show();
									}
									if (codiceColoreCondition==Color.YELLOW){
										condGUI.createEmailGUI();
										invalidate();
										builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												// Canceled.
											}
										});

										builder.show();
									}
								}
							}
						});
						break;
				case LOCATION:
					pin.setListener(new OnPinClickListener() {
						@Override
						public void onPinClick() {
							synchronized (_thread.getSurfaceHolder()) {
								AlertDialog.Builder alert = new AlertDialog.Builder(act);

								alert.setTitle("Insert location");
								alert.setMessage("Coordinates for '" + pin.getText() + "':");

								final View v = act.getLayoutInflater().inflate(R.layout.getlocation, null);
								alert.setView(v);

								Object obj = pin.getObject();
								if (obj instanceof String) {
									String lobj = (String) obj;
									EditText et = (EditText) findViewById(R.id.editText1);
									String[] strs = lobj.split(",");
									et.setText(strs[0]);
									et = (EditText) findViewById(R.id.editText2);
									et.setText(strs[1]);
								}

								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										EditText et1 = (EditText) v.findViewById(R.id.editText1);
										EditText et2 = (EditText) v.findViewById(R.id.editText2);

										String loc = et1.getText().toString() + "," + et2.getText().toString();
										pin.setObject(loc);
										invalidate();
									}
								});

								alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										// Canceled.
									}
								});

								alert.show();

							}
						}
					});
					break;
				case STRING:
					if (pin.getInput().equals("date")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {

									Calendar c = Calendar.getInstance();
									mYear = c.get(Calendar.YEAR);
									mMonth = c.get(Calendar.MONTH);
									mDay = c.get(Calendar.DAY_OF_MONTH);

									Object obPin = pin.getObject();
									if (obPin != null && obPin instanceof String) {
										SimpleDateFormat format = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());
										try {
											Date d = format.parse((String) obPin);
											c.setTime(d);
											mYear = c.get(Calendar.YEAR);
											mMonth = c.get(Calendar.MONTH);
											mDay = c.get(Calendar.DAY_OF_MONTH);
										} catch (ParseException e) {
										}
									}

									DatePickerDialog dialog = new DatePickerDialog(act, new OnDateSetListener() {

										@Override
										public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
											SimpleDateFormat format = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());
											Calendar fc = Calendar.getInstance();
											fc.set(Calendar.YEAR, year);
											fc.set(Calendar.MONTH, monthOfYear);
											fc.set(Calendar.DAY_OF_MONTH, dayOfMonth);
											String dateFormat = format.format(fc.getTime());

											pin.setObject(dateFormat);

											grid.setModified(true);
											invalidate();
										}
									}, mYear, mMonth, mDay);

									dialog.setButton(DialogInterface.BUTTON_NEGATIVE, act.getString(R.string.cancel),
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int which) {
													// Cancelled
												}
											});

									dialog.show();
								}
							}
						});
					} else if (pin.getInput().equals("string")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder alert = new AlertDialog.Builder(act);
									alert.setTitle("Insert text");
									alert.setMessage("Text for '" + pin.getText() + "':");

									// Set an EditText view to get user input
									final EditText input = new EditText(act);
									Object obj = pin.getObject();
									if (obj instanceof String) {
										input.setText((String) obj);
									}

									alert.setView(input);

									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String value = input.getText().toString();
											pin.setObject(value);
											grid.setModified(true);
											invalidate();
										}
									});

									alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
										}
									});

									alert.show();

								}
							}
						});

					} else if (pin.getInput().equals("number")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder alert = new AlertDialog.Builder(act);

									alert.setTitle("Insert number");
									alert.setMessage("Number for '" + pin.getText() + "':");

									// Set an EditText view to get user input
									final EditText input = new EditText(act);
									input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);

									Object obj = pin.getObject();
									if (obj instanceof String) {
										input.setText((String) obj);
									}

									alert.setView(input);

									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String value = input.getText().toString();
											pin.setObject(value);
											grid.setModified(true);
											invalidate();
										}
									});

									alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
										}
									});

									alert.show();

								}
							}
						});

					} else if (pin.getInput().equals("phonenumber")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder alert = new AlertDialog.Builder(act);

									alert.setTitle("Insert phone number");
									alert.setMessage("Phone number for '" + pin.getText() + "':");

									// Set an EditText view to get user input
									final EditText input = new EditText(act);
									input.setInputType(InputType.TYPE_CLASS_PHONE);

									Object obj = pin.getObject();
									if (obj instanceof String) {
										input.setText((String) obj);
									}

									alert.setView(input);

									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String value = input.getText().toString();
											pin.setObject(value);
											grid.setModified(true);
											invalidate();
										}
									});

									alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
										}
									});

									alert.show();

								}
							}
						});

					} 	else if (pin.getInput().equals("password")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder alert = new AlertDialog.Builder(act);

									alert.setTitle("Insert password");
									alert.setMessage("Text for '" + pin.getText() + "':");

									// Set an EditText view to get user input
									final EditText input = new EditText(act);
									input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
									input.setHint("Password");

									Object obj = pin.getObject();
									if (obj instanceof String) {
										input.setText((String) obj);
									}

									alert.setView(input);

									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String value = input.getText().toString();
											pin.setObject(value);
											grid.setModified(true);
											invalidate();
										}
									});

									alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
										}
									});

									alert.show();

								}
							}
						});

					}if (pin.getInput().equals("sensor")) {
					pin.setListener(new OnPinClickListener() {
						@Override
						public void onPinClick() {
							synchronized (_thread.getSurfaceHolder()) {
								AlertDialog.Builder alert = new AlertDialog.Builder(act);

								alert.setTitle("Select sensor");
//									alert.setMessage("Text for '" + pin.getText() + "':");

								final Spinner s = new Spinner(act);
								ArrayList<String> sensorList = new ArrayList<String>();
//								String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};

								LinearLayout ll_Main = new LinearLayout(act);
								ll_Main.setOrientation(LinearLayout.VERTICAL);
//								LinearLayout.MarginLayoutParams mm = new ViewGroup.MarginLayoutParams(250,150);
//								LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(mm);
////								ll.setMargins(15,5,15,5);

								SensorTable table = new SensorTable(act);
								table.open();

								String QUERY = "SELECT * FROM "+ SensorTable.TABLE_SENSOR;
								Cursor c = table.db().rawQuery(QUERY, null);
								Log.d("info", "size db " + c.getCount());
								//c.moveToFirst();

								while(c.moveToNext()){
									String nome = c.getString(c.getColumnIndex(SensorTable.NAME_PROJECT));
									sensorList.add(nome);
								}

//								s.setGravity(Gravity.CENTER);
//								s.setPadding(15,15,15,15);
//								s.setMinimumWidth(150);
//								ViewGroup.LayoutParams l = new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT);
//								ViewGroup.MarginLayoutParams vp = new ViewGroup.MarginLayoutParams(l);
//								vp.setMargins(10,2,10,2);
//								s.setLayoutParams(l);

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										act, android.R.layout.simple_spinner_item, sensorList);
								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								s.setAdapter(adapter);

								// Set an EditText view to get user input
//									final EditText input = new EditText(act);
//									Object obj = pin.getObject();
//									if (obj instanceof String) {
//										input.setText((String) obj);
//									}
								ll_Main.addView(s);
								alert.setView(ll_Main);

								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										String value = s.getSelectedItem().toString();
										pin.setObject(value);
										grid.setModified(true);
										invalidate();
									}
								});

								alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										// Canceled.
									}
								});

								alert.show();

							}
						}
					});

				}


					break;
					case MAIL:
						break;
					case URI:
					if (pin.getInput().equals("video")) {

						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									Intent intentVideo = new Intent(Intent.ACTION_PICK,
											android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

									act.startActivityForResult(intentVideo, Constants.ID_SELECT_VIDEO);
									MainPanel.lastPin = pin;
									// TODO: fill the video
								}
							}
						});
					} else if (pin.getInput().equals("audio")) {

						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									Intent intentAudio = new Intent(Intent.ACTION_PICK,
											android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
									act.startActivityForResult(intentAudio, Constants.ID_SELECT_AUDIO);
									MainPanel.lastPin = pin;
									// TODO: fill the audio
								}
							}
						});
					}
					break;
					case OBJECT:
						break;
					case VIEWER:
						if (pin.getInput().equals("sensor")) {
						pin.setListener(new OnPinClickListener() {
							@Override
							public void onPinClick() {
								synchronized (_thread.getSurfaceHolder()) {
									AlertDialog.Builder alert = new AlertDialog.Builder(act);

									alert.setTitle("Select sensor");
//									alert.setMessage("Text for '" + pin.getText() + "':");

									final Spinner s = new Spinner(act);
									String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};


									ArrayAdapter<String> adapter = new ArrayAdapter<String>(
											act, android.R.layout.simple_spinner_item, colors);
									adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
									s.setAdapter(adapter);

									// Set an EditText view to get user input
//									final EditText input = new EditText(act);
//									Object obj = pin.getObject();
//									if (obj instanceof String) {
//										input.setText((String) obj);
//									}

									alert.setView(s);

									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String value = s.getSelectedItem().toString();
											pin.setObject(value);
											grid.setModified(true);
											invalidate();
										}
									});

									alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
										}
									});

									alert.show();

								}
							}
						});

					}
						break;
				default:
					break;
				}
			}
			/*else {
					switch (pin.getParam()) {
						case CONDITION:
							if (!flagCondition){
								flagCondition=true;
								pin.setListener(new OnPinClickListener() {

									@Override
									public void onPinClick() {
										synchronized (_thread.getSurfaceHolder()) {
											AlertDialog.Builder builder = new AlertDialog.Builder(act)
													.setTitle("Condition Manager");
											final FrameLayout frameView = new FrameLayout(act);
											builder.setView(frameView);
											final AlertDialog alertDialog = builder.create();
											LayoutInflater inflater=alertDialog.getLayoutInflater();
											View dialoglayout = inflater.inflate(R.layout.alertcondgui, frameView);
											ConditionGUIParam condGUI=new ConditionGUIParam(pin,act,builder,grid,dialoglayout);
											if (codiceColoreCondition== Color.CYAN){
												condGUI.createDynamicContactGUI();
												invalidate();
												builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														// Canceled.
													}
												});
												builder.show();
											}
											if (codiceColoreCondition==Color.RED){
												condGUI.createDynamicStringGUI();
												invalidate();
												builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														// Canceled.
													}
												});

												builder.show();
											}
											if (codiceColoreCondition==Color.MAGENTA){
												condGUI.createDynamicImageGUI();
												invalidate();
												builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														// Canceled.
													}
												});
												builder.show();
											}
											if (codiceColoreCondition==Color.rgb(128, 0, 0)){
												condGUI.createDynamicLocationGUI();
												invalidate();
												builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int whichButton) {
														// Canceled.
													}
												});
												builder.show();
											}


											// TODO: fill the user contact
										}
									}
								});
							}
							break;
					}
			}*/


		}

		return p;
	}

	class ParcelableData implements Parcelable {
		public final Creator<ParcelableData> CREATOR = new Creator<ParcelableData>() {
			public ParcelableData createFromParcel(Parcel in) {
				return new ParcelableData(in);
			}

			public ParcelableData[] newArray(int size) {
				return new ParcelableData[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(_xOffset);
			dest.writeInt(_yOffset);
		}

		private ParcelableData(Parcel in) {
			_xOffset = in.readInt();
			_yOffset = in.readInt();
		}
	}

	public void onRestoreInstanceState(Parcelable state) {
		Utils.debug("onRestoreInstanceState (" + state + ")");
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Utils.debug("onSaveInstanceState ()");
		return super.onSaveInstanceState();
	}

	/*
	 * private Bitmap screen(){ LinearLayout view =
	 * (LinearLayout)findViewById(R.id.mainpanelscreen);
	 * view.setDrawingCacheEnabled(true);
	 * view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	 * MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)); view.layout(0,
	 * 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	 * view.buildDrawingCache(true); Bitmap b =
	 * Bitmap.createBitmap(view.getDrawingCache());
	 * view.setDrawingCacheEnabled(false);
	 * 
	 * return b; }
	 */

}

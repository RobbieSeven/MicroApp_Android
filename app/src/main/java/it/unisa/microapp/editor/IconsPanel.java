package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.library.DataTypeMulti;
import it.unisa.microapp.library.DataUserType;
import it.unisa.microapp.library.Library;
import it.unisa.microapp.library.LibraryComponent;
import it.unisa.microapp.library.WSLibraryComponent;
import it.unisa.microapp.library.DLibraryComponent;
import it.unisa.microapp.service.domotics.DOperation;
import it.unisa.microapp.service.domotics.DPiece;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;
import it.unisa.microapp.webservice.LevenshteinDistance;
import it.unisa.microapp.webservice.NoElementException;
import it.unisa.microapp.webservice.TypeParser2;
import it.unisa.microapp.webservice.TypesUtils;
import it.unisa.microapp.webservice.WSDLParser;
import it.unisa.microapp.webservice.piece.WSContext;
import it.unisa.microapp.webservice.piece.WSOperation;
import it.unisa.microapp.webservice.piece.WSPiece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

public class IconsPanel extends SurfaceView implements OnTouchListener,
		SurfaceHolder.Callback {

	class SurfaceThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private IconsPanel _panel;
		private boolean _run = false;

		public SurfaceThread(SurfaceHolder surfaceHolder, IconsPanel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;

		}

		// ****************************************************************

		public void run() {
			Canvas c;
			while (_run) {
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						if (c != null)
							_panel.draw(c); // onDraw(c);
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

	private SurfaceThread _thread;

	Activity act;
	IconsGrid Grid2;

	// Offset to the upper left corner of the map
	private int _xOffset = 0;
	private int _yOffset = 0;

	// last touch point
	private int _xTouch = 0;
	private int _yTouch = 0;

	// atomic offset for pixelwise scrolling
	// private int mXScrollOffset = 0;
	// private int mYScrollOffset = 0;

	// loop borders
	private int curCellRow;
	private int curCellColumn;

	int cellWidthGrid2 = 0;
	int cellHeightGrid2 = 0;
	int space = Constants.SPACE;

	// scrolling active?
	private boolean _isMoving = false;

	// long touch
	private boolean _isLongTouch = false;

	// private static String LOG_TAG = "MainPanel";

	// INSERIMENTO PEZZI
	static Piece X;// P;
	IconPiece Xpre;
	int inserimenti;// conta gli inserimenti effettuati
	boolean entra;
	boolean selezione = false;
	int conta;
	float inizialeX, inizialeY, PrecX, PrecY;// punto(coordinate) di parteza da
												// dove si disegna il pezzo

	boolean ponte;
	int cicla;// numero di colonne per il ponte
	int pont, pop; // colonna di partenza del ponte

	// Multi
	int multiconta, multico;
	boolean Vmulti, StopinseriMulti;

	// User
	List<Pin> list;
	int userCont = 0;
	boolean UserIns;

	String padre;

	private GestureDetector gestures;
	private GestureListener gestureListener;

	private boolean discovering;

	private String file;

	/**
	 * This is the class that monitors "gestures" which are performed on the
	 * scrolling map view.
	 */
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		private DecelerateInterpolator interpolator;

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
		private float totalAnimDx;
		private float totalAnimDy;

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
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
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
			scrollBy(-(distanceTimeFactor * velocityX / 5),
					-(distanceTimeFactor * velocityY / 5), 1500);

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
			totalAnimDx = dx;
			totalAnimDy = dy;

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
			this.interpolator = new DecelerateInterpolator(1.0f);
			post(new Runnable() {

				public void run() {
					checkDeceleration();
				}
			});
		}

		@Override
		public void onLongPress(MotionEvent e) {

			synchronized (_thread.getSurfaceHolder()) {
				_isLongTouch = true;

				Point lp = Grid2.calcLogicPosition(e.getX() + _xOffset,
						e.getY() + _yOffset);
				Shape pm = Grid2.getMatrixPiece(lp);
				if (pm != null) {
					Grid2.unselectPieces();

					AlertDialog.Builder builder = new AlertDialog.Builder(act);

					builder.setTitle("Details");
					builder.setIcon(android.R.drawable.ic_dialog_info);

					ScrollView scroll = new ScrollView(act);

					builder.setView(scroll);

					TextView txt = new TextView(act);

					txt.setText(pm.showInfo2());

					scroll.addView(txt);

					builder.setNeutralButton(android.R.string.ok, null);

					AlertDialog dial = builder.create();

					dial.show();
				}
			}
		}

		/**
		 * This is the method that is called by the thread that updates the map
		 * based on a DecelerateInterpolator. It determines how far along in the
		 * interpolation process we are, and if the deceleration has completed.
		 * If the interpolator has not finished... interpolating... it spawns a
		 * new thread to continue upating.
		 */
		public void checkDeceleration() {
			//
			// How far (percentage wise) are we through the time?
			//
			long curTime = System.currentTimeMillis();
			float percentTime = (float) (curTime - startTime)
					/ (float) (endTime - startTime);

			//
			// Calculate the percentage distance we are through the
			// interpolation
			// and use that to determine the distance (in pixels) we've
			// traveled.
			//
			float percentDistance = this.interpolator
					.getInterpolation(percentTime);
			float curDx = percentDistance * totalAnimDx;
			float curDy = percentDistance * totalAnimDy;

			//
			// Use the original starting position, and the current distance
			// traveled to get the current position to be displayed.
			//
			_xOffset = startFlingX + (int) curDx;
			_yOffset = startFlingY + (int) curDy;

			// Ensure that the offsets are actually on the map.
			//
			if (Grid2.getNumCols() < Constants.GRID_WSIZE) {
				_xOffset = 0;
			} else if (_xOffset < 0) {
				_xOffset = 0;
			} else if (_xOffset > Grid2.getNumCols() * Constants.GRID_RATIO
					- getWidth()) {
				_xOffset = Grid2.getNumCols() * Constants.GRID_RATIO
						- getWidth();

			}

			if (Grid2.getNumRows() < Constants.GRID_HSIZE) {
				_yOffset = 0;
			} else if (_yOffset < 0) {
				_yOffset = 0;
			} else if (_yOffset > (Grid2.getNumRows() + 1)
					* Constants.GRID_RATIO - getHeight()) {
				_yOffset = (Grid2.getNumRows() + 1) * Constants.GRID_RATIO
						- getHeight();

			}
			_xOffset = (int) Math.floor(_xOffset / Constants.GRID_RATIO)
					* Constants.GRID_RATIO;
			_yOffset = (int) Math.floor(_yOffset / Constants.GRID_RATIO)
					* Constants.GRID_RATIO;
			calculateLoopBorders();

			//
			// If we're not done yet, repost a thread to call us again.
			//
			if (percentTime < 1.0f) {
				post(new Runnable() {
					public void run() {
						checkDeceleration();
					}
				});
			}
		}

	}

	public IconsPanel(Context context) {
		super(context);
	}

	public IconsPanel(Context context, int h, int w, String padre, String f) {
		super(context);

		discovering = false;
		this.setOnTouchListener(this);
		// Create and attach the new GestureDetector and Listener.

		gestureListener = new GestureListener();
		gestures = new GestureDetector(this.getContext(), gestureListener);
		this.file = f;

		Utils.verbose("main2:" + file);

		getHolder().addCallback(this);
		_thread = new SurfaceThread(getHolder(), this);

		act = (Activity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);

		// ************************************************
		// altezza = h;
		// larghezza = w;

		cellWidthGrid2 = Constants.GRID_RATIO - Constants.SPACE; // 80-20=60
		cellHeightGrid2 = Constants.GRID_RATIO - Constants.SPACE; // 86-20=66

		synchronized (_thread.getSurfaceHolder()) {
			Grid2 = new IconsGrid(cellHeightGrid2, cellWidthGrid2,
					Constants.HEIGTH, Constants.WIDTH);

			if (!OptionsActivity.getBoolean(Constants.CB_WB_KEY,
					this.getContext())) {
				this.setBackground(getResources().getDrawable(
						R.drawable.background4));
			} else {
				this.setBackground(getResources().getDrawable(R.color.WHITE));
			}
			setupPezzi(padre, f);
		}

	}

	public void setupPezzi(String pad, String fil) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		synchronized (_thread.getSurfaceHolder()) {
			Grid2.clearIconPieces();
			padre = pad;

			if (!padre.startsWith("wsdl:") && !padre.startsWith("domotic:")) {
				Utils.verbose(file);
				Library.setFileName(file);
				ArrayList<LibraryComponent> pieces = Library
						.getFunctionByCategory(act, padre);
				int i = 0;
				for (LibraryComponent pezzo : pieces) {
					int r = (i / 4) + 1;
					int c = (i % 4) + 1;

					IconPiece p;

					if (pezzo instanceof WSLibraryComponent) {
						p = new WSPiece(pezzo.getIdLogic(), act.getResources(),
								pezzo.getName(), c, r, Grid2);

						((WSPiece) p).setWsdl(((WSLibraryComponent) pezzo)
								.getWsdl());
						((WSPiece) p).setOperation(((WSLibraryComponent) pezzo)
								.getOperation());
						((WSPiece) p).setService(((WSLibraryComponent) pezzo)
								.getService());
						((WSPiece) p).setPort(((WSLibraryComponent) pezzo)
								.getPort());
						((WSPiece) p).setTns(((WSLibraryComponent) pezzo)
								.getTns());
						((WSPiece) p).setUri(((WSLibraryComponent) pezzo)
								.getUri());
						((WSPiece) p).setContext(((WSLibraryComponent) pezzo)
								.getContext());
					} else if (pezzo instanceof DLibraryComponent) {
						p = new DPiece(pezzo.getIdLogic(), act.getResources(),
								pezzo.getName(), c, r, Grid2);

						((DPiece) p).setName(((DLibraryComponent) pezzo)
								.getName());
						((DPiece) p).setState(((DLibraryComponent) pezzo)
								.getState());
						((DPiece) p).setLink(((DLibraryComponent) pezzo)
								.getLink());
						((DPiece) p).setType(((DLibraryComponent) pezzo)
								.getType());

					} else
						p = new IconPiece(pezzo.getIdLogic(),
								act.getResources(), pezzo.getName(), c, r,
								Grid2);

					p.setDescription(pezzo.getDescription());
					p.setNowState(pezzo.getNowState());
					p.setAllStates(pezzo.getAllStates());

					String icon = pezzo.getIcon();
					if (icon.equals("")) {
						icon = padre.toLowerCase(Locale.getDefault()) + "48";
						int id = getResources().getIdentifier(icon, "drawable",
								act.getPackageName());
						p.setIconin("", id);

					} else {
						icon = icon.substring(11);
						int id = getResources().getIdentifier(icon, "drawable",
								act.getPackageName());
						p.setIconin(icon, id);
					}

					// per ogni input

					for (DataTypeMulti inputt : pezzo.getInputList()) {
						String type = inputt.getMulti();
						String param = inputt.getType().toString();
						String name = inputt.getName();
						p.addInputPin(new Pin(PinEnumerator.valueOf(type),
								PinParamEnumerator.valueOf(param), name));
					}

					for (DataTypeMulti outputt : pezzo.getOutputList()) {
						String type = outputt.getMulti();
						String param = outputt.getType().toString();
						String name = outputt.getName();
						p.addOutputPin(new Pin(PinEnumerator.valueOf(type),
								PinParamEnumerator.valueOf(param), name));
					}

					if (pezzo.getUserInputList() != null)
						for (DataUserType inputt : pezzo.getUserInputList()) {
							String type = "USER";
							String param = inputt.getType().toString();
							String name = inputt.getName();
							Pin upin = new Pin(PinEnumerator.valueOf(type),
									PinParamEnumerator.valueOf(param), name);
							upin.setInput(inputt.getInput());
							p.addInputPin(upin);
						}

					Grid2.addNewIconPiece(p);
					i++;
				}
				invalidate();

			} else {
				if (padre.startsWith("wsdl:")) {
					discovering = true;
					extractOperationFromWSDL(padre.substring("wsdl:".length()));
				} else if (padre.startsWith("domotic:")) {
					discovering = true;
					extractOperationFromDomotic(fil);

				}
				invalidate();

			}
		}

	}

	private void extractOperationFromDomotic(String domo) {
		String[] str = domo.split(";");
		// String name = str[0];
		String name = str[0];
		String state = str[1];
		String link = str[2];
		String type = str[3];
		String servername = str[4];

		int i = 0;
		String id = "DOMOTIC_" + name.toUpperCase(Locale.getDefault()) + "_"
				+ servername.toUpperCase(Locale.getDefault());
		int r = (i / 4) + 1;
		int c = (i % 4) + 1;

		DPiece p = new DPiece(id, act.getResources(), name, c, r, Grid2);
		p.setAllStates("visible/hidden/progress");

		String icon = "Domotic".toLowerCase(Locale.getDefault()) + "48";

		int _id = getResources().getIdentifier(icon, "drawable",
				act.getPackageName());
		Log.d("ICON", "icon icon " + icon);
		if (_id > 0) {
			p.setIconin(icon, _id);
		} else {
			p.setIconin("", R.drawable.icon);
		}

		// setto i vari parametri
		p.setName(name);
		p.setState(state);
		p.setLink(link);
		p.setType(type);

		// switchare sul type

					//setto il valore di output a Multi object
					p.addOutputPin(new Pin(PinEnumerator.valueOf("NORMAL"), PinParamEnumerator.valueOf("STRING"), "STRING"));
					
					//effettuo controllo sui parametri di input e li setto
					p.addInputPin(new Pin(PinEnumerator.valueOf("NORMAL"), PinParamEnumerator.valueOf("STRING"), "STRING"));


		Grid2.addNewIconPiece(p);
		i++;
	}

	private void extractOperationFromWSDL(String wsdl) {
		String servname;
		int i = 0;
		WSDLParser parser = new WSDLParser();
		parser.setallInfos();

		if (parser.parse(wsdl) == 0) {
			servname = parser.getServiceName();
			Iterator<String> it = parser.getPortList().iterator();

			while (it.hasNext()) {
				String port = it.next();
				List<Operation> list = parser.getOperationList(port);

				String endpoint = getendPoint(port, parser);

				for (Operation op : list) {
					String description;
					String id = "WEBSERVICE_"
							+ op.getName().toUpperCase(Locale.getDefault())
							+ "_" + servname.toUpperCase(Locale.getDefault());
					int r = (i / 4) + 1;
					int c = (i % 4) + 1;

					WSPiece p = new WSPiece(id, act.getResources(),
							op.getName(), c, r, Grid2);
					p.setAllStates("visible/hidden/progress");
					
					String icon = returnCategoria(p).toLowerCase(
							Locale.getDefault())
							+ "48";
					Log.d("ICON", "icon icon " + icon);
					int _id = getResources().getIdentifier(icon, "drawable",
							act.getPackageName());

					if (_id > 0) {
						p.setIconin(icon, _id);
					} else {
						p.setIconin("", R.drawable.icon);
					}

					// setto i vari parametri
					p.setOperation(op.getName());
					p.setPort(port);
					p.setService(servname);
					p.setWsdl(wsdl);
					p.setTns(parser.getTargerNameSpace());
					p.setUri(endpoint);

					// ricavo la descrizione
					if (op.getDocumentationElement() != null)
						description = op.getDocumentationElement()
								.getTextContent();
					else
						description = "" + op.getName() + " operation";

					p.setDescription(description);

					// TODO:aggiungo il contesto
					WSContext con = new WSContext();

					// TODO:DA CONTROLLARE
					LocationManager locationManager;
					String context = Context.LOCATION_SERVICE;
					locationManager = (LocationManager) act
							.getSystemService(context);
					String provider = LocationManager.GPS_PROVIDER;
					Location location = locationManager
							.getLastKnownLocation(provider);

					if (location != null)
						con.setLocation(location);
					else
						con.setLocation(new Location(provider));

					p.setContext(con);

					// setto il valore di output a Multi object
					// p.addOutputPin(new Pin(PinEnumerator.valueOf("MULTI"),
					// PinParamEnumerator.valueOf("OBJECT"), "OBJECT"));
					controllaOutput(op, p, parser);

					// effettuo controllo sui parametri di input e li setto
					controllaInput(op, p, parser);

					Grid2.addNewIconPiece(p);
					i++;
				}
			}
		} else
			Utils.errorDialog(act, "errore nel parsing del documento");
	}

	private void controllaOutput(Operation op, WSPiece p, WSDLParser parser) {
		List<?> list = (List<?>) (op.getOutput().getMessage()
				.getOrderedParts(null));

		if (list.size() == 1) {
			Part part = (Part) list.get(0);

			// 1 attributo element
			if (part.getElementName() != null) {
				String name = part.getElementName().getLocalPart();
				int ind = name.indexOf(":") + 1;
				if (parser.getTypesInformation() != null) {
					TypesUtils utils = new TypesUtils(
							parser.getTypesInformation());
					Element elem = null;

					try {
						if (parser.getTypesInformation().getTypeMap() != null
								&& parser.getTypesInformation().getTypeMap()
										.get(name.substring(ind)) != null) {
							elem = (Element) utils.Element2Tree(name)
									.getFirstChild();

							NodeList l = elem.getChildNodes();

							// esattamente un figlio
							if (l.getLength() == 1) {
								if (isSimpleString(
										parser.getTypesInformation(),
										(Element) l.item(0))) {
									p.addOutputPin(new Pin(PinEnumerator
											.valueOf("NORMAL"),
											PinParamEnumerator
													.valueOf("STRING"),
											((Element) l.item(0))
													.getAttribute("name")));
								} else
									p.addOutputPin(new Pin(PinEnumerator
											.valueOf("MULTI"),
											PinParamEnumerator
													.valueOf("OBJECT"),
											"OBJECT"));
							} else
								p.addOutputPin(new Pin(PinEnumerator
										.valueOf("MULTI"), PinParamEnumerator
										.valueOf("OBJECT"), "OBJECT"));

						} else
							p.addOutputPin(new Pin(PinEnumerator
									.valueOf("MULTI"), PinParamEnumerator
									.valueOf("OBJECT"), "OBJECT"));

					} catch (NoElementException e) {
						Utils.error("errore nel parsing del documento", e);
						Utils.errorDialog(act,
								"errore nel parsing del documento");
						return;
					}
				} else {
					Utils.errorDialog(act, "errore nel parsing del documento");
					return;
				}
			}
			// 2 attributo type
			else {
				String name = part.getTypeName().getLocalPart();
				int ind = name.indexOf(":") + 1;

				if (parser.getTypesInformation() != null) {
					TypesUtils utils = new TypesUtils(
							parser.getTypesInformation());
					Element elem = null;

					// controllo se è elemento complesso
					if (parser.getTypesInformation().getTypeMap() != null
							&& parser.getTypesInformation().getTypeMap()
									.get(name.substring(ind)) != null) {
						try {
							elem = (Element) utils.Element2Tree(name)
									.getFirstChild();

							NodeList l = elem.getChildNodes();

							// esattamente un figlio
							if (l.getLength() == 1) {
								if (isSimpleString(
										parser.getTypesInformation(),
										(Element) l.item(0))) {
									p.addOutputPin(new Pin(PinEnumerator
											.valueOf("NORMAL"),
											PinParamEnumerator
													.valueOf("STRING"),
											((Element) l.item(0))
													.getAttribute("name")));
								} else
									p.addOutputPin(new Pin(PinEnumerator
											.valueOf("MULTI"),
											PinParamEnumerator
													.valueOf("OBJECT"),
											"OBJECT"));
							} else
								p.addOutputPin(new Pin(PinEnumerator
										.valueOf("MULTI"), PinParamEnumerator
										.valueOf("OBJECT"), "OBJECT"));

						} catch (NoElementException e) {
							Utils.error("errore nel parsing del documento", e);
							Utils.errorDialog(act,
									"errore nel parsing del documento");
							return;
						}
					}
					// tipo semplice
					else {
						String type = name.substring(ind);

						if (!type.contains("boolean")
								|| !type.contains("anyType"))
							p.addOutputPin(new Pin(PinEnumerator
									.valueOf("NORMAL"), PinParamEnumerator
									.valueOf("STRING"), part.getName()));
						else
							p.addOutputPin(new Pin(PinEnumerator
									.valueOf("MULTI"), PinParamEnumerator
									.valueOf("OBJECT"), "OBJECT"));

					}
				} else {
					Utils.errorDialog(act, "errore nel parsing del documento");
					return;
				}
			}
		} else
			p.addOutputPin(new Pin(PinEnumerator.valueOf("MULTI"),
					PinParamEnumerator.valueOf("OBJECT"), "OBJECT"));
	}

	@SuppressWarnings("unchecked")
	private void controllaInput(Operation op, WSPiece p, WSDLParser parser) {
		List<Part> list = (List<Part>) op.getInput().getMessage()
				.getOrderedParts(null);

		for (Part part : list) {
			if (part.getElementName() != null) {
				String name = part.getElementName().getLocalPart();

				if (parser.getTypesInformation() != null) {
					TypesUtils utils = new TypesUtils(
							parser.getTypesInformation());
					Element elem = null;

					try {
						elem = (Element) utils.Element2Tree(name)
								.getFirstChild();

						NodeList l = elem.getChildNodes();

						for (int i = 0; i < l.getLength(); i++) {
							if (l.item(i) instanceof Element) {
								Element e = (Element) l.item(i);
								int ind = e.getAttribute("type").indexOf(":");

								String type = e.getAttribute("type").substring(
										ind + 1);
								Utils.verbose("name:" + e.getAttribute("name"));
								if (isSimpleString(
										parser.getTypesInformation(), e)) {
									p.addInputPin(new Pin(PinEnumerator
											.valueOf("NORMAL"), Pin
											.matchPinParam(type), e
											.getAttribute("name")));
								}
							}
						}

					} catch (NoElementException e) {
						Utils.error("errore nel parsing del documento", e);
						Utils.errorDialog(act,
								"errore nel parsing del documento");
						return;
					}
				} else {
					Utils.errorDialog(act, "errore nel parsing del documento");
					return;
				}
			} else {
				/*
				 * String t=part.getTypeName().getLocalPart();
				 * 
				 * int ind=t.indexOf(":"); String type=t.substring(ind+1);
				 * 
				 * if(!type.equals("boolean")) p.addInputPin(new
				 * Pin(PinEnumerator.valueOf("NORMAL"),
				 * PinParamEnumerator.valueOf("STRING"), part.getName()));
				 */

				String name = part.getTypeName().getLocalPart();
				int ind = name.indexOf(":") + 1;

				if (parser.getTypesInformation() != null) {
					TypesUtils utils = new TypesUtils(
							parser.getTypesInformation());
					Element elem = null;

					// controllo se �� elemento complesso
					if (parser.getTypesInformation().getTypeMap() != null
							&& parser.getTypesInformation().getTypeMap()
									.get(name.substring(ind)) != null) {
						try {
							elem = (Element) utils.Element2Tree(name)
									.getFirstChild();

							NodeList l = elem.getChildNodes();

							for (int i = 0; i < l.getLength(); i++) {
								if (l.item(i) instanceof Element) {
									Element e = (Element) l.item(i);

									Utils.verbose("name:"
											+ e.getAttribute("name"));
									if (isSimpleString(
											parser.getTypesInformation(), e)) {
										p.addInputPin(new Pin(PinEnumerator
												.valueOf("NORMAL"),
												PinParamEnumerator
														.valueOf("STRING"), e
														.getAttribute("name")));
									}
								}
							}

						} catch (NoElementException e) {
							Utils.error("errore nel parsing del documento", e);
							Utils.errorDialog(act,
									"errore nel parsing del documento");
							return;
						}
					}
					// tipo semplice
					else {
						String type = name.substring(ind);

						if (!type.contains("boolean")
								|| !type.contains("anyType"))
							p.addInputPin(new Pin(PinEnumerator
									.valueOf("NORMAL"), PinParamEnumerator
									.valueOf("STRING"), part.getName()));

					}
				} else {
					Utils.errorDialog(act, "errore nel parsing del documento");
					return;
				}
			}
		}
	}

	private boolean isSimpleString(TypeParser2 tp, Element e) {
		String type;
		int id;

		id = e.getAttribute("type").indexOf(":");
		type = e.getAttribute("type").substring(id + 1);

		if (!e.getAttribute("layout").isEmpty())
			return false;

		if (tp.getTypeMap().get(type) != null)
			return false;

		if (!e.getAttribute("type").isEmpty()
				&& (!e.getAttribute("type").contains("boolean") || !e
						.getAttribute("type").contains("anyType"))) {
			if (e.getAttribute("max").isEmpty()
					|| e.getAttribute("max").equals("1"))
				return true;
			else if (!e.getAttribute("max").equals("1"))
				return false;
		}

		return false;
	}

	private String getendPoint(String port, WSDLParser parser) {
		Port p = parser.getPort(port);

		if (p != null) {
			List<?> list = p.getExtensibilityElements();

			for (Object obj : list) {
				if (obj instanceof SOAPAddress) {
					SOAPAddress addr = (SOAPAddress) obj;
					return addr.getLocationURI();
				} else if (obj instanceof SOAP12Address) {
					SOAP12Address addr = (SOAP12Address) obj;
					return addr.getLocationURI();
				} else if (obj instanceof HTTPAddress) {
					HTTPAddress addr = (HTTPAddress) obj;
					return addr.getLocationURI();
				}
			}
		}

		return "";
	}

	protected void onDraw(Canvas canvas) {
		synchronized (_thread.getSurfaceHolder()) {
			Grid2.draw(canvas, curCellColumn, curCellRow);
		}
	}

	public boolean onTouch(View view, MotionEvent event) {
		synchronized (_thread.getSurfaceHolder()) {
			if (gestures.onTouchEvent(event) == false) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// start of a new event, reset the flag
					_isMoving = false;
					_isLongTouch = false;
					// store the current touch coordinates for scroll
					// calculation
					_xTouch = (int) event.getX();
					_yTouch = (int) event.getY();

				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
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

					_xOffset = 0;

					if (Grid2.getNumRows() < Constants.GRID_HSIZE
							|| _yOffset < 0) {
						_yOffset = 0;
					} else if (_yOffset > (Grid2.getNumRows() + 1)
							* Constants.GRID_RATIO - getHeight()) {
						_yOffset = (Grid2.getNumRows() + 1)
								* Constants.GRID_RATIO - getHeight();
					}
					// ci da Yoffset come multiplo della dimensione delle righe
					_yOffset = (int) Math
							.floor(_yOffset / Constants.GRID_RATIO)
							* Constants.GRID_RATIO;

					// store the last position
					_xTouch = (int) event.getX();
					_yTouch = (int) event.getY();
					calculateLoopBorders();
					invalidate();

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// touch released
					if (!_isMoving && !_isLongTouch) {
						/*
						 * 
						 * Point lp =
						 * grid.calcLogicPosition(event.getX()+_xOffset
						 * ,event.getY()+_yOffset); Piece pm =
						 * grid.getMatrixPiece(lp); if (pm != null){
						 * grid.unselectPieces(); pm.setSelected(true);
						 * invalidate();
						 */
						Point lp = Grid2.calcLogicPosition(event.getX(),
								event.getY());
						Xpre = (IconPiece) Grid2.getMatrixPiece(lp);
						if (Xpre != null) {
							Grid2.unselectPieces();
							Xpre.setSelected(true);
							invalidate();

							// pezzo montato
							createObject();
							// *********** fine codice aggiunto ***************

							List<Pin> in = Xpre.getInputPin();
							X.setInputPin(in);
							List<Pin> ou = Xpre.getOutputPin();
							X.setOutputPin(ou);

							// pezzo da comporre
							// P = new Piece(Xpre.getIdLogic(),
							// act.getResources(), Xpre.getText(), 0, 0, Grid2);

							String key = Xpre.getIcon();
							if (key == null || key.equals("")) {
								key = padre.toLowerCase(Locale.getDefault())
										+ "48";
							}
							Log.d("ICON", "icon key 1101" + key);
							int id = getResources().getIdentifier(key,
									"drawable", act.getPackageName());

							if (id > 0) {
								// P.setIcon("R.drawable." + key, id);
								X.setIconin("R.drawable." + key, id);
							} else {
								// P.setIcon("R.drawable.icon",
								// R.drawable.icon);
								X.setIconin("R.drawable.icon", R.drawable.icon);
							}
							// P da passare incompleto

							selezione = true;
							inserimenti = 0;
							entra = true;
							StopinseriMulti = true; // quando si deve fermare
													// l'iserimento multi
							Vmulti = false;// quando si deve effetuare
											// l'inserimeto multi
							multiconta = 0;// conta le volte degli inserimenti
											// multi-1
							multico = 0;// variabile per l'indice

							// per user
							userCont = 0;
							UserIns = true;

							conta = 0;
							pont = 0;

							Intent i = act.getIntent();
							Bundle b = new Bundle();

							b.putBoolean("selezione", selezione);
							// i.putExtras(b);
							b.putInt("inserimenti", inserimenti);
							// i.putExtras(b);
							b.putBoolean("entra", entra);
							// i.putExtras(b);
							b.putBoolean("StopinseriMulti", StopinseriMulti);
							// i.putExtras(b);
							b.putBoolean("Vmulti", Vmulti);
							// i.putExtras(b);
							b.putInt("multiconta", multiconta);
							// i.putExtras(b);
							b.putInt("multico", multico);
							// i.putExtras(b);
							b.putInt("userCont", userCont);
							// i.putExtras(b);
							b.putBoolean("UserIns", UserIns);
							// i.putExtras(b);
							b.putInt("pont", pont);
							// i.putExtras(b);
							b.putInt("conta", conta);
							// i.putExtras(b);
							b.putString("padre", padre);
							// i.putExtras(b);
							b.putSerializable("X", X);

							if (discovering) {
								b.putBoolean("discovering", discovering);
								// TODO:mi vado a ricavare la categoria di
								// appartenenza
								if (padre.startsWith("wsdl:")) {

									String npadre = ricavaCategoria((WSOperation) X);
									b.putString("padre", npadre);
								} else if (padre.startsWith("domotic:")) {
									if (X instanceof WSPiece)
										Log.e("ISTANZA",
												"X è istanza di WSPiece ");
									String npadre = ricavaCategoria((DOperation) X);
									b.putString("padre", npadre);
								}
							}

							i.putExtras(b);
							act.setResult(2, i);
							act.finish();
						}

					}
				}

			}
			return true;
		}
	}

	private String returnCategoria(Shape P) {
		double soglia = 1.0 - 0.40;
		double min = 100.0;
		double dist = 0.0;

		// String servname=P.getService();
		String operation = P.getText().toLowerCase(Locale.getDefault());

		String stopoperation = removeStopWord(operation);
		String catRet = "";

		String[] categories = Library.getCategories(act);

		// LevenshteinDistance lev=new LevenshteinDistance();

		for (String cat : categories) {
			String[] keywords = Library.getKeyWordForCategory(act, cat);

			for (String key : keywords) {
				int max2 = Math.max(operation.length(), key.length());
				dist = (LevenshteinDistance.getDistance(stopoperation, key) / max2);

				if (dist < min) {
					min = dist;
					catRet = cat;
				}

				Utils.debug("distanza keyword " + key + ":" + dist);
			}
		}

		if (min <= soglia) {
			Utils.debug("la categoria dell'operazione " + operation + " è "
					+ catRet + "\ncon compatibilità del " + ((1 - min) * 100)
					+ "%");
			String icon = catRet.toLowerCase(Locale.getDefault()) + "48";
			int id = getResources().getIdentifier(icon, "drawable",
					act.getPackageName());
			Log.d("ICON", "icon icon " + icon);
			if (id > 0) {
				P.setIconin("R.drawable." + icon, id);
			} else {
				P.setIconin("R.drawable.icon", R.drawable.icon);
			}

			return catRet;
		}

		Utils.debug("nessuna categoria compatibile per " + operation
				+ "\nuso la categoria di default webservice");
		String icon = "webservice48";
		int id = getResources().getIdentifier(icon, "drawable",
				act.getPackageName());

		if (id > 0) {
			P.setIconin("R.drawable." + icon, id);
		} else {
			P.setIconin("R.drawable.icon", R.drawable.icon);
		}

		return "WebService";
	}

	private String ricavaCategoria(WSOperation P) {
		String ret = returnCategoria(P);
		WSOperationSaver.save(P, ret, file, act);
		Utils.verbose("elemento WebService memorizzato");
		return ret;
	}

	private String ricavaCategoria(DOperation P) {
		String ret = "Domotic";
		DOperationSaver.save(P, ret, file, act);
		Utils.verbose("elemento Domotic memorizzato");
		return ret;
	}

	private String removeStopWord(String operation) {
		String str = new String(operation);
		String[] stop = { "get", "set", "put", "add", "remove", "all" };

		for (String s : stop) {
			if (str.startsWith(s))
				str = str.substring(s.length());
		}

		return str;
	}

	/**
	 * metodo usato per inizializzare gli elementi
	 */
	private void createObject() {
		if (Xpre instanceof WSPiece) {
			// ok e un WS
			WSOperation x = new WSOperation(Xpre.getIdLogic(),
					act.getResources(), Xpre.getText(), 0, 0, Grid2);
			WSPiece xp = (WSPiece) Xpre;
			// setto i parametri
			x.setWsdl(xp.getWsdl());
			x.setOperation(xp.getOperation());
			x.setService(xp.getService());
			x.setPort(xp.getPort());
			x.setTns(xp.getTns());
			x.setUri(xp.getUri());
			x.setContext(xp.getContext());
			X = x;
		} else if (Xpre instanceof DPiece) {
			DOperation x = new DOperation(Xpre.getIdLogic(),
					act.getResources(), Xpre.getText(), 0, 0, Grid2);
			DPiece xp = (DPiece) Xpre;
			x.setName(xp.getName());
			x.setState(xp.getState());
			x.setLink(xp.getLink());
			x.setType(xp.getType());
			X = x;

		} else
			X = new Piece(Xpre.getIdLogic(), act.getResources(),
					Xpre.getText(), 0, 0, Grid2);
		
		X.setDescription(Xpre.getDescription());
		X.setNowState(Xpre.getNowState());
		X.setAllStates(Xpre.getAllStates());
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
		// Toast.makeText(getContext(), ": " + mXScrollOffset+
		// ","+mYScrollOffset, Toast.LENGTH_SHORT).show();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

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

	class ParcelableData implements Parcelable {
		public final Creator<ParcelableData> CREATOR = new Creator<ParcelableData>() {
			public ParcelableData createFromParcel(Parcel in) {
				return new ParcelableData(in);
			}

			public ParcelableData[] newArray(int size) {
				return new ParcelableData[size];
			}
		};

		public int describeContents() {
			return 0;
		}

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

	}

	@Override
	public Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	public void setFile(String file) {
		this.file = file;
	}

}

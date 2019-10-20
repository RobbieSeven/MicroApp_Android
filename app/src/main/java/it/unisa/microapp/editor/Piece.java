package it.unisa.microapp.editor;

import it.unisa.microapp.R;
import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.BitmapFactory.Options;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

/*
 * ho implementato l'interfaccia Parcelable e i suoi relativi metodi
 */
@SuppressLint("UseValueOf")
public class Piece extends Shape implements Serializable, Parcelable {
	private static final long serialVersionUID = -5393827587518629977L;
	boolean linee=false;
	public String truePin="";
	public String falsePin="";
	protected String id;
	protected String idlogic;

	protected Resources act;
	protected float x;
	protected float y;
	protected int logicX;
	protected int logicY;

	protected int width;
	protected int height;
	protected int widthScale;
	protected int heightScale;

	protected String text;
	protected String[] multiText;

	protected List<Pin> in;
	protected List<Pin> out;
	private List<Integer> AggancioOut;// 0 se libero,1 se agganciato

	private String icon;
	protected int iconId;
	protected Bitmap image;
	protected boolean showIcon;
	protected int hasCondition;
	protected PreCondition[] condTemp = null; // new PreCondition[4] ;

	protected boolean selected;
	private boolean highlithed;

	protected List<Integer> holes;
	protected List<Integer> logicHoles;
	private List<Boolean> boolHoles;
	private int holeState;

	protected int blockSpacediv2;
	protected int radius;
	protected int radiusdiv2;
	protected int radiusmult2;
	private int heightdiv5;

	private String description;

	Paint paint80;

	Grid grid;

	public Piece(String idLogic, Resources act, String text, int x, int y, Grid grid) {
		
		this.id = new Long(new Date().getTime()).toString(); //unique id
		this.idlogic = idLogic;
		this.act = act;

		this.logicX = x;// posizione interna matrice
		this.logicY = y;

		this.grid = grid;

		this.widthScale = 1;
		this.heightScale = 1;

		selected = false;
		highlithed = false;

		refresh();

		this.setLogicPosition(x, y);
		this.setText(text);

		this.in = new ArrayList<Pin>();
		this.out = new ArrayList<Pin>();

		// LISTA aggancio
		AggancioOut = new ArrayList<Integer>();

		this.holes = new ArrayList<Integer>();
		this.logicHoles = new ArrayList<Integer>();
		this.boolHoles = new ArrayList<Boolean>();

		this.holeState = 0;
		paint80 = new Paint();
		paint80.setAntiAlias(true);
		paint80.setAlpha(80);

		setIconSampleSize(R.drawable.icon, 2);

		showIcon = true;
		hasCondition = Constants.NO_CONDITION;

		description = "";
		allStates = "";
	}

	public void refresh() {
		this.width = grid.getWidth();
		this.height = grid.getHeight();

		this.heightdiv5 = height / 5;

		this.radius = Constants.RADIUS;
		this.radiusdiv2 = radius >> 1;
		this.radiusmult2 = radius << 1;

		this.blockSpacediv2 = Constants.SPACE >> 1;

		this.setLogicPosition(this.logicX, this.logicY);
	}

	public static final Creator<Piece> CREATOR = new Creator<Piece>() {
		public Piece createFromParcel(Parcel in) {
			return new Piece(in);
		}

		public Piece[] newArray(int size) {
			return new Piece[size];
		}
	};

	protected Piece(Parcel in) {
		readFromParcel(in);

		Utils.debug(Arrays.toString(this.in.toArray()));
		Utils.debug(Arrays.toString(this.out.toArray()));

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void readFromParcel(Parcel in) {
		this.idlogic = in.readString();
		this.text = in.readString();

		int n = in.readInt();

		this.in = new ArrayList<Pin>();
		for (int i = 0; i < n; i++)
			this.in.add((Pin) in.readSerializable());

		n = in.readInt();
		this.out = new ArrayList<Pin>();
		for (int i = 0; i < n; i++)
			this.out.add((Pin) in.readSerializable());

		description = in.readString();
		nowState = in.readString();
		allStates = in.readString();
	}

	public int RitornaPezzo(Piece sopra, int Xa) {

		int b = 0;
		int partenza = (int) sopra.getLogicPosition().x;
		for (int i = Xa - 1; i > partenza; i--) {
			if (sopra.isHoled(i))
				b++;
		}

		return (Xa - partenza - b) + 1;
	}

	public void moveUp(boolean up) {
		Point lp = this.getLogicPosition();
		if (up) {
			this.setLogicPosition(lp.x, lp.y + 1);
		} else {
			if (lp.y > 0)
				this.setLogicPosition(lp.x, lp.y - 1);
		}
	}

	public void moveRight(boolean left) {
		Point lp = this.getLogicPosition();
		if (left) {
			if (lp.x > 1)
				this.setLogicPosition(lp.x - 1, lp.y);
		} else {
			this.setLogicPosition(lp.x + 1, lp.y);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isHighlithed() {
		return highlithed;
	}

	public void setHighlithed(boolean highlithed) {
		this.highlithed = highlithed;
	}

	public void fireOnClick() {
		for (Pin pin : in) {
			pin.onPinClick();
		}
		for (Pin pin : out) {
			pin.onPinClick();
		}
	}

	public void expand() {
		widthScale++;
		if (out.size() > 0) {
			Pin ppin = out.get(0);
			addOutputPin(new Pin(ppin.getType(), ppin.getParam(), ppin.getText()));
		} else {
			rescale();
		}
	}

	public void addHole(int hole) {
		if (widthScale >= 2 && hole > 1 && hole <= widthScale) {
			holes.add(hole);
			widthScale++;

			rescaleHoles();
		}
	}

	public void modifyHole(int hole) {
		if (widthScale >= 2 && hole > 1 && hole <= widthScale) {
			holes.add(hole);

			rescaleHoles();
		}
	}

	public Point getLogicSize() {
		return new Point(width, heightScale);

	}

	public boolean isHoled() {
		return (holes.size() > 0);
	}

	public boolean isHoled(int position) {
		return holes.contains(position);
	}

	public void addInputPin(Pin pin) {
		in.add(pin);
		rescale();
	}

	public void addOutputPin(Pin pin) {
		out.add(pin);
		// aggiorna aggancio
		AggancioOut.add(0);
		rescale();
	}

	public boolean hasUpperInputPin() {
		for (Pin pn : in) {
			if (pn.getType() != PinEnumerator.USER)
				return true;
		}

		return false;
	}

	public List<Integer> getAggancioOut() {
		return AggancioOut;
	}

	public List<Pin> getInputPin() {
		return in;
	}

	public void setInputPin(List<Pin> p) {
		in = p;
	}

	public void setOutputPin(List<Pin> p) {
		out = p;
		for (int i = 0; i < p.size(); i++) {
			AggancioOut.add(0);
		}
	}

	public boolean TuttiOutputLiberi() {

		if (this.getWidthScale() == 1)
			return true;
		if (this.getWidthScale() > 1) {
			List<Pin> out = this.getOutputPin();
			int x = this.getLogicPosition().x;
			int y = this.getLogicPosition().y;
			for (int i = 0; i < out.size(); i++) {
				Point p = new Point(x + i, y + 1);
				Piece sotto = (Piece) grid.getMatrixPiece(p);
				if (sotto != null)
					return false;
			}
		}

		return true;
	}

	public List<Pin> getOutputPin() {
		return out;
	}

	public void setIconin(String icon, int id) {
		this.icon = icon;
		this.iconId = id;

		image = BitmapFactory.decodeResource(act, id);

	}

	public String getIcon() {
		return this.icon;
	}

	public int getIconId() {
		return this.iconId;
	}
	
	protected void setIconSampleSize(int id, int sampleSize) {
		Options opt = new Options();
		opt.inSampleSize = sampleSize;
		image = BitmapFactory.decodeResource(act, id, opt);
	}

	public void setWidthScale(int scale) {
		if (scale > 0)
			widthScale = scale;
	}

	public void setHeightScale(int scale) {
		if (scale > 0)
			heightScale = scale;
	}

	public int getWidthScale() {
		return widthScale;
	}

	public int getHeightScale() {
		return heightScale;
	}

	public void setPosition(float x, float y) {
		if (x > 0)
			this.x = x;
		if (y > 0)
			this.y = y;
	}

	public void setSnapPosition(float x, float y) {
		Point cp = grid.calcSnapPosition(x, y);
		setLogicPosition(cp.x, cp.y);
	}

	public void setLogicPosition(int x, int y) {
		if (x > 0) {
			this.logicX = x;
			this.x = (x - 1) * (width + Constants.SPACE);
		}
		if (y > 0) {
			this.logicY = y;
			this.y = (y - 1) * (height + Constants.SPACE) + blockSpacediv2;
		}
	}

	public PointF getPosition() {
		return new PointF(this.x, this.y);
	}

	public Point getLogicPosition() {
		return new Point(this.logicX, this.logicY);
	}

	public int getWidth() {
		return width;
	}

	public void setSize(int width, int height) {
		if (width > 0)
			this.width = width;
		if (height > 0)
			this.height = height;
	}

	public String getId() {
		return id;
	}

	public String getIdLogic() {
		return idlogic;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

		this.multiText = Utils.splitter(text);
	}

	public boolean isShowIcon() {
		return showIcon;
	}

	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
	}

	public int colorCondition() {
		if (hasCondition == Constants.MANDATORY_CONDITION)
			return Color.RED;

		return Color.GREEN;
	}

	public boolean isCondition() {
		return (hasCondition != Constants.NO_CONDITION);
	}

	// get the condition
	public int getCondition() {
		return hasCondition;
	}

	public PreCondition[] getPreCondition() {
		return condTemp;
	}

	public void setPreCondition(PreCondition[] cond) {
		this.condTemp = cond;
	}

	public void setCondition(int hasCondition) {
		this.hasCondition = hasCondition;
	}

	public List<Integer> getHoles() {

		return holes;
	}

	public void setHoles(List<Integer> l) {
		holes = l;
	}

	public void rescale() {
		int hasUser = 0;
		int len = in.size();
		for (int i = 0; i < len; i++) {
			if (in.get(i).getType() == PinEnumerator.USER) {
				hasUser++;
			}
		}

		int inout = Math.max(out.size(), len - hasUser);
		widthScale = Math.max(widthScale/* +holes.size() */, inout + holes.size());

		rescaleHoles();
	}

	public void rescaleHoles() {
		boolHoles.clear();
		for (int i = 0; i < widthScale; i++) {
			boolHoles.add(holes.contains(i + 1));
		}

		logicHoles.clear();
		int count = 0;
		int i = 0;
		int holeLen = boolHoles.size();
		while (i < holeLen) {
			count = 0;
			while (i < holeLen && boolHoles.get(i) == false) {
				count++;
				i++;
			}
			logicHoles.add(count);
			count = 0;
			if (i < holeLen) {
				while (i < holeLen && boolHoles.get(i) == true) {
					count++;
					i++;
				}
				logicHoles.add(-count);
			}
		}
	}

	@Override
	public void draw(Canvas panel, Paint paint, boolean iconified, int col, int row) {
		int oldColor = paint.getColor();
		Style oldStyle = paint.getStyle();
		int oldAlpha = paint.getAlpha();
		int nWidth = width;
		int nHeight = height;

		if (!iconified)// calcola la larghezza e lungh se il pezzo occupa +
						// colonne e + righe
		{
			nWidth = width * widthScale + (widthScale - 1) * Constants.SPACE;
			nHeight = height * heightScale + (heightScale - 1) * Constants.SPACE;
		}
		float nx = x + blockSpacediv2;

		float oldw = paint.getStrokeWidth();

		if (highlithed) {
			paint.setColor(Color.RED);// colore pezzo selezionato
			paint.setStrokeWidth(Constants.SPACE / 2);
		} else {
			paint.setColor(Color.BLACK);
		}

		paint.setStyle(Style.STROKE);

		int heightblockSpacerow = (height + Constants.SPACE) * row;
		int widthblockSpacecol = (width + Constants.SPACE) * col;

		if (isHoled() && !iconified)// disegna bordirettangolo ai pezzi con
									// buchi che occupano + colonne
		{
			float basePos = nx;
			int len = logicHoles.size();
			for (int c = 0; c < len; c++) {
				int pos = logicHoles.get(c);
				int temp = (width + Constants.SPACE) * pos;
				if (pos > 0) {
					int hWidth = temp - Constants.SPACE;
					panel.drawRoundRect(new RectF(basePos - (widthblockSpacecol), y - (heightblockSpacerow), basePos - (widthblockSpacecol)
							+ hWidth, y - (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);
					basePos += temp;
				} else {
					// pos = -pos;
					basePos -= temp;
				}
			}

		} else
			// altri bordorettangoli
			panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx - (widthblockSpacecol) + nWidth, y
					- (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);

		if (highlithed) {
			paint.setStrokeWidth(oldw);
		}
		paint.setStyle(Style.FILL);

		if (selected) {
			paint.setColor(Color.GRAY);// colore pezzo selezionato
		} else
			paint.setColor(Color.rgb(210, 210, 210));// colore pezzo non
														// selezionato

		if (isHoled() && !iconified && !nowState.equals("hidden"))// riempimento
																	// rettengoli
																	// con buchi
																	// che
		// occupano piu righe
		{
			float basePos = nx;
			int len = logicHoles.size();
			for (int c = 0; c < len; c++) {
				int pos = logicHoles.get(c);
				int temp = (width + Constants.SPACE) * pos;
				if (pos > 0) {
					int hWidth = temp - Constants.SPACE;

					panel.drawRoundRect(new RectF(basePos - (widthblockSpacecol), y - (heightblockSpacerow), basePos - (widthblockSpacecol)
							+ hWidth, y - (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);

					basePos += temp;
				} else {
					basePos -= temp;
				}
			}

		} else if (!nowState.equals("hidden"))
			// rimpimento altri rettangoli
			panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx - (widthblockSpacecol) + nWidth, y
					- (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);

		int widthdiv2 = (width >> 1);
		if (!nowState.equals("hidden")) {
			paint.setColor(Color.MAGENTA);
			paint.setAlpha(25);

			// riempimento della meta' del pezzo con il colore specificato sopra
			panel.drawRoundRect(new RectF(nx - (widthblockSpacecol) + nWidth - widthdiv2, y - (heightblockSpacerow) + 3, nx
					- (widthblockSpacecol) + nWidth - 3, y - (heightblockSpacerow) + nHeight - 3), radiusdiv2, radiusdiv2, paint);

			paint.setAlpha(oldAlpha);

		}

		if (!stateMod)
			panel.save();
		if (!nowState.equals("visible") && nowState != null) {
			if (nowState.equals("hidden")) {
				if (selected) {
					paint.setColor(Color.GRAY);

					panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx - (widthblockSpacecol) + nWidth,
							y - (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);
					paint80.setColor(Color.YELLOW);
					// paint80.setStrokeWidth(Constants.BORDER_SIZE);
					panel.drawLine(nx - widthblockSpacecol + Constants.BORDER_SIZE, y - heightblockSpacerow + Constants.BORDER_SIZE, nx
							- widthblockSpacecol + nWidth - Constants.BORDER_SIZE, y - heightblockSpacerow + nHeight
							- Constants.BORDER_SIZE, paint80);
				} else {
					paint.setColor(Color.rgb(210, 210, 210));
					paint.setAlpha(80);
					panel.drawRoundRect(new RectF(nx - (widthblockSpacecol), y - (heightblockSpacerow), nx - (widthblockSpacecol) + nWidth,
							y - (heightblockSpacerow) + nHeight), radiusdiv2, radiusdiv2, paint);

					paint.setColor(Color.YELLOW);
					// paint.setStrokeWidth(Constants.BORDER_SIZE);
					panel.drawLine(nx - widthblockSpacecol + Constants.BORDER_SIZE, y - heightblockSpacerow + Constants.BORDER_SIZE, nx
							- widthblockSpacecol + nWidth - Constants.BORDER_SIZE, y - heightblockSpacerow + nHeight
							- Constants.BORDER_SIZE, paint);
				}
			} else {
				// int pink = Color.rgb(255, 160, 255);
				LinearGradient gradient = new LinearGradient(nx - (widthblockSpacecol) + nWidth - widthdiv2, y - (heightblockSpacerow) + 3,
						nx - (widthblockSpacecol) + nWidth - 3, y - (heightblockSpacerow) + nHeight - 3, new int[] { Color.GREEN,
								Color.YELLOW, Color.CYAN, Color.BLUE }, null, android.graphics.Shader.TileMode.MIRROR);
				Paint pGrad = new Paint();
				pGrad.setDither(true);
				pGrad.setShader(gradient);
				pGrad.setAlpha(50);
				Options opt = new Options();
				opt.inSampleSize = (Constants.GRID_WSIZE >> 1) - 1;

				Bitmap stimgP = BitmapFactory.decodeResource(act, R.drawable.percent4, opt);

				int iw = (stimgP.getWidth() >> 1);
				if (selected) {
					panel.drawRoundRect(new RectF(nx - (widthblockSpacecol) + nWidth - widthdiv2, y - (heightblockSpacerow) + 3, nx
							- (widthblockSpacecol) + nWidth - 3, y - (heightblockSpacerow) + nHeight - 3), radiusdiv2, radiusdiv2, pGrad);
					panel.drawBitmap(stimgP, nx - (widthblockSpacecol) + widthdiv2 - iw + 36, y - (heightblockSpacerow) + 6, paint80);
				} else {
					panel.drawRoundRect(new RectF(nx - (widthblockSpacecol) + nWidth - widthdiv2, y - (heightblockSpacerow) + 3, nx
							- (widthblockSpacecol) + nWidth - 3, y - (heightblockSpacerow) + nHeight - 3), radiusdiv2, radiusdiv2, pGrad);
					panel.drawBitmap(stimgP, nx - (widthblockSpacecol) + widthdiv2 - iw + 36, y - (heightblockSpacerow) + 6, null);
				}
			}

			if (!stateMod)
				stateMod = true;
		} else if (stateMod) {
			panel.restore();
			stateMod = false;
		}
		// fine modifica

		if (isCondition()) {

			int dradius = (radius - 3); // << 1;

			paint.setColor(colorCondition());
			paint.setStyle(Style.FILL_AND_STROKE);

			int tx = (int) nx - (widthblockSpacecol) + dradius + 1;
			int ty = (int) y - (heightblockSpacerow) + nHeight - dradius - 1;

			Path path = new Path();
			path.moveTo(tx, ty - dradius);
			path.lineTo(tx + dradius, ty);
			path.lineTo(tx, ty + dradius);
			path.lineTo(tx - dradius, ty);
			path.close();
			panel.drawPath(path, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			panel.drawPath(path, paint);
		}

		if (showIcon)// disegna icone sopra pezzi
		{
			setIconSampleSize(this.iconId, (Constants.GRID_WSIZE >> 1) - 1);

			int iw = (image.getWidth() >> 1);
			if (selected) {
				panel.drawBitmap(image, nx - (widthblockSpacecol) + widthdiv2 - iw, y - (heightblockSpacerow) + 4, paint80);
			} else
				panel.drawBitmap(image, nx - (widthblockSpacecol) + widthdiv2 - iw, y - (heightblockSpacerow) + 4, null);
		}

		if (!text.equals(""))// scrive nome sull'icona
		{
			paint.setColor(Color.BLACK);
			paint.setTextSize(Constants.TEXT_SIZE);
			paint.setTextAlign(Align.CENTER);

			int toff = -(multiText.length - 1) * Constants.TEXT_SIZE;
			for (String s : multiText) {
				panel.drawText(s, nx - (widthblockSpacecol) + widthdiv2, y - (heightblockSpacerow) + nHeight - radiusmult2 + toff, paint);
				toff = toff + Constants.TEXT_SIZE;
			}
		}

		if (iconified) {
			// input pins
			int ilen = in.size();
			if (ilen > 0) {
				int hasMulti = 0;
				int hasUser = 0;
				int multiColor = 0;
				for (Pin pin : in) {
					if (pin.getType() == PinEnumerator.MULTI) {
						multiColor = pin.getColor();
						hasMulti++;
					}
					if (pin.getType() == PinEnumerator.USER) {
						hasUser++;
					}

				}

				int Uoffset = 0;
				int innerCount = 1;
				int step = width / (ilen - hasMulti + 2 - hasUser);
				if (hasMulti == 0) {
					step = width / (ilen - hasUser + 1);
				}
				int NMoffset = 0;

				for (int i = 0; i < ilen; i++) {
					NMoffset = innerCount * step;
					Pin pin = in.get(i);
					switch (pin.getType()) {
					case NORMAL: {
						innerCount++;
						paint.setColor(pin.getColor());
						paint.setStyle(Style.FILL_AND_STROKE);
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius, paint);
						paint.setTextAlign(Align.CENTER);
						break;
					}
					case USER: { // triangle
							paint.setColor(pin.getColor());
							paint.setStyle(Style.FILL_AND_STROKE);
							Path path = new Path();
							int b4 = (Constants.SPACE >> 2);
							path.moveTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4 + Uoffset);
							path.lineTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4 + radiusmult2 + Uoffset);
							path.lineTo(nx - (widthblockSpacecol) + b4 + radiusmult2 / 3, y - (heightblockSpacerow) + b4 + radius + Uoffset);
							path.close();
							panel.drawPath(path, paint);
							Uoffset += radius + blockSpacediv2 + (Constants.BORDER_SIZE << 1);


						break;
					}
					default:
						break;
					}
				}
				if (hasMulti > 0) {
					NMoffset = innerCount * step;
					paint.setColor(multiColor);
					paint.setStyle(Style.STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius, paint);
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radius / 4 - 1, radiusdiv2, paint);

				}
			}

		} else
		// disegna alcuni doppi cerchi e triangoli doppi[INPUT PINS]
		{
			// input pins
			int Uoffset = 0;
			int innerCount = 0;
			int ilen = in.size();
			for (int i = 0; i < ilen; i++) {
				while (holes.contains(innerCount + 1)) {
					innerCount++;
				}
				int NMoffset = innerCount * (width + Constants.SPACE) + (width >> 1);
				Pin pin = in.get(i);

				switch (pin.getType()) {
				case NORMAL: {
					innerCount++;
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);

					if (pin.isHighlithed()) {
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radius, (radius << 1), paint);
						if (pin.isEmpty()) {
							paint.setStyle(Style.STROKE);
							panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radius, (radius << 1) + 4,
									paint);
						}
					} else {
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius, paint);
						if (pin.isEmpty()) {
							paint.setStyle(Style.STROKE);
							panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius + 4,
									paint);
						}
					}
					break;
				}
				case MULTI: {
					if (pin.isHighlithed()) {

					}
					innerCount++;
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE

					if (pin.isHighlithed()) {
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radius, (radius<<1), paint);
						if (pin.isEmpty()) {
							paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE
							panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radius, (radius<<1) + 4,
									paint);
						}
					} else {
						panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius, paint);
						if (pin.isEmpty()) {
							paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE
							panel.drawCircle(nx - (widthblockSpacecol) + NMoffset, y - (heightblockSpacerow) - radiusdiv2, radius + 4,
									paint);
						}
					}
					break;
				}
				case USER: { // triangle

						paint.setColor(pin.getColor());
						paint.setStyle(Style.FILL_AND_STROKE);
						Path path = new Path();
						int b4 = (Constants.SPACE >> 2);
						path.moveTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4 + Uoffset);
						path.lineTo(nx - (widthblockSpacecol) - b4, y - (heightblockSpacerow) + b4 + (radius << 1) + Uoffset);
						path.lineTo(nx - (widthblockSpacecol) + b4 + (radius << 1) / 3, y - (heightblockSpacerow) + b4 + radius + Uoffset);
						path.close();
						panel.drawPath(path, paint);

						if (pin.isEmpty()) {
							paint.setStyle(Style.STROKE);
							path = new Path();
							path.moveTo(nx - (widthblockSpacecol) - b4 - Constants.BORDER_SIZE, y - (heightblockSpacerow) + b4 + Uoffset
									- (Constants.BORDER_SIZE * 2));
							path.lineTo(nx - (widthblockSpacecol) - b4 - Constants.BORDER_SIZE, y - (heightblockSpacerow) + b4 + radius * 2
									+ Uoffset + (Constants.BORDER_SIZE * 2));
							path.lineTo(nx - (widthblockSpacecol) + b4 + (Constants.BORDER_SIZE << 1) + (radius << 1) / 3, y
									- (heightblockSpacerow) + b4 + radius + Uoffset);
							path.close();
							panel.drawPath(path, paint);
						}

						Uoffset += radius + blockSpacediv2 + (Constants.BORDER_SIZE << 1);


					break;
				}
				}

			}
		}
		// //////////////////*******************************************************************
		// output pins
		if (iconified) {
			// output pins
			int olen = out.size();
			int step = width / (olen + 1);
			for (int i = 0; i < olen; i++) {
				int Noffset = (i + 1) * step;
				Pin pin = out.get(i);
				switch (pin.getType()) {
				case NORMAL: {
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, radius,
							paint);
					break;
				}
				case MULTI: {
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, radius,
							paint);
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - (radius >> 2) + 1,
							radius / 2, paint);
					break;
				}
				default:
					break;
				}
			}
		} else
		// outputPINS
		{
			int innerCount = 0;
			int olen = out.size();
			for (int i = 0; i < olen; i++) {
				while (holes.contains(innerCount + 1)) {
					innerCount++;
				}
				int Noffset = innerCount * (width + Constants.SPACE) + (width >> 1);
				Pin pin = out.get(i);
				switch (pin.getType()) {
				case NORMAL: {
					innerCount++;
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, radius,
							paint);
					break;
				}
				case MULTI: {
					innerCount++;
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE); //TODO: STROKE
					panel.drawCircle(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, radius,
							paint);
					break;
				}
				default:
					break;
				}

				if (this.getIcon().equals("R.drawable.condition48")){
					if (i==0){
						panel.drawText("T", nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 10,
								paint);
						if (!linee){
							panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 100, paint);
						}
						else {
							if (truePin.equals("pin 1")){
								panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 100, paint);
							}
							else{
								panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, nx - (widthblockSpacecol) + Noffset+125, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 100, paint);
							}
						}
					}
					else{
						panel.drawText("F", nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 10,
								paint);
						if (!linee){
							panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2,nx - (widthblockSpacecol) + Noffset-125,y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2-100,paint);

						}
						else{
							if (falsePin.equals("pin 1")){
								panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, nx - (widthblockSpacecol) + Noffset-125, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 100, paint);
							}
							else{
								panel.drawLine(nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2, nx - (widthblockSpacecol) + Noffset, y - (heightblockSpacerow) + nHeight - radiusdiv2 + 2 - 100, paint);
							}
						}
					}

				}

			}
		}

		paint.setColor(oldColor);
		paint.setStyle(oldStyle);
		paint.setAlpha(oldAlpha);
	}

	private Bitmap resize(int ratio) {
		Options bmpFactoryOptions = new Options();
		bmpFactoryOptions.inSampleSize = ratio;
		bmpFactoryOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(act, iconId, bmpFactoryOptions);
	}

	public void drawAsIcon(Canvas panel, Paint paint, float left, float top) {

		int oldColor = paint.getColor();
		Style oldStyle = paint.getStyle();
		int oldAlpha = paint.getAlpha();
		int radius = (int) ((float) (Constants.FRADIUS << 2) / 5);
		int nWidth = Constants.FBUTTON - (Constants.FMARGIN << 1);
		int nHeight = Constants.FBUTTON - (radius << 2) + Constants.FMARGIN;
		int x = (int) left;
		int y = (int) top + (radius << 1) - 1;
		int radiusdiv2 = Constants.FRADIUS >> 1;
		int radiusmult2 = Constants.FRADIUS << 1;
		int radiusmult290 = (int) ((float) (radiusmult2 << 2) / 5);

		float nx = x + (Constants.FSPACE >> 1);

		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);

		// altri bordorettangoli
		panel.drawRoundRect(new RectF(nx, y, nx + nWidth, y + nHeight), radiusdiv2, radiusdiv2, paint);

		paint.setStyle(Style.FILL);

		paint.setColor(Color.rgb(210, 210, 210));// colore pezzo non selezionato

		// rimpimento altri rettangoli
		panel.drawRoundRect(new RectF(nx, y, nx + nWidth, y + nHeight), radiusdiv2, radiusdiv2, paint);

		paint.setColor(Color.MAGENTA);
		paint.setAlpha(25);
		int widthdiv2 = nWidth >> 1;

		// riempimento della meta' del pezzo con il colore specificato sopra
		panel.drawRoundRect(new RectF(nx + nWidth - widthdiv2, y + 3, nx + nWidth - 3, y + nHeight - 3), radiusdiv2, radiusdiv2, paint);

		paint.setAlpha(oldAlpha);

		int iw = image.getWidth() >> 2;

		panel.drawBitmap(resize(2), nx + widthdiv2 - iw, y + 4, null);

		if (!text.equals(""))// scrive nome sull'icona
		{
			paint.setColor(Color.BLACK);
			paint.setTextSize(Constants.FTEXT_SIZE - 3);
			paint.setTextAlign(Align.CENTER);

			int toff = 4 - (multiText.length - 1) * (Constants.FTEXT_SIZE - 3);
			for (String s : multiText) {
				panel.drawText(s, nx + widthdiv2, y + nHeight - radiusmult2 + toff, paint);
				toff = toff + (Constants.FTEXT_SIZE - 3);
			}
		}

		// input pins
		int ilen = in.size();
		if (ilen > 0) {
			int hasMulti = 0;
			int hasUser = 0;
			int multiColor = 0;
			for (Pin pin : in) {
				if (pin.getType() == PinEnumerator.MULTI) {
					multiColor = pin.getColor();
					hasMulti++;
				}
				if (pin.getType() == PinEnumerator.USER) {
					hasUser++;
				}

			}

			int Uoffset = 0;
			int innerCount = 1;
			int step = nWidth / (ilen - hasMulti + 2 - hasUser); // width
			if (hasMulti == 0) {
				step = nWidth / (ilen - hasUser + 1); // width
			}
			int NMoffset = 0;

			for (int i = 0; i < ilen; i++) {
				NMoffset = innerCount * step;
				Pin pin = in.get(i);
				switch (pin.getType()) {
				case NORMAL: {
					innerCount++;
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);
					panel.drawCircle(nx + NMoffset, y - radiusdiv2, radius, paint);
					paint.setTextAlign(Align.CENTER);
					break;
				}
				case USER: { // triangle
					paint.setColor(pin.getColor());
					paint.setStyle(Style.FILL_AND_STROKE);
					Path path = new Path();
					int b4 = (radius >> 2); // Constants.FSPACE >> 2;
					path.moveTo(nx - b4, y + b4 + Uoffset);
					path.lineTo(nx - b4, y + b4 + radiusmult290 + Uoffset);
					path.lineTo(nx + b4 + radiusmult290 / 3, y + b4 + radius + Uoffset);
					path.close();
					panel.drawPath(path, paint);
					Uoffset += radius + (Constants.FSPACE >> 2) + (Constants.FBORDER_SIZE << 1);
					break;
				}
				default:
					break;
				}
			}
			if (hasMulti > 0) {
				NMoffset = innerCount * step;
				paint.setColor(multiColor);
				paint.setStyle(Style.STROKE);
				panel.drawCircle(nx + NMoffset, y - radiusdiv2, radius, paint);
				paint.setStyle(Style.FILL_AND_STROKE);
				panel.drawCircle(nx + NMoffset, y - (radius >> 2) - 1, radiusdiv2, paint);

			}
		}

		nx = nx - radiusdiv2;

		// output pins
		int olen = out.size();
		int step = width / (olen + 1);
		for (int i = 0; i < olen; i++) {
			int Noffset = (i + 1) * step;
			Pin pin = out.get(i);
			switch (pin.getType()) {
			case NORMAL: {
				paint.setColor(pin.getColor());
				paint.setStyle(Style.FILL_AND_STROKE);
				panel.drawCircle(nx + Noffset, y + nHeight - radiusdiv2 + 2, radius, paint);
				break;
			}
			case MULTI: {
				paint.setColor(pin.getColor());
				paint.setStyle(Style.STROKE);
				panel.drawCircle(nx + Noffset, y + nHeight - radiusdiv2 + 2, radius, paint);
				paint.setStyle(Style.FILL_AND_STROKE);
				panel.drawCircle(nx + Noffset, y + nHeight - (radius >> 2) + 1, radiusdiv2, paint);
				break;
			}
			default:
				break;
			}

		}

		paint.setColor(oldColor);
		paint.setStyle(oldStyle);
		paint.setAlpha(oldAlpha);
	}

	public void drawHoles(Canvas panel, Paint paint, int col, int row) {
		if (isHoled()) {
			int oldColor = paint.getColor();
			Style oldStyle = paint.getStyle();
			int oldAlpha = paint.getAlpha();

			float basePos = x + blockSpacediv2;
			int nHeight = (height + Constants.SPACE) * heightScale - Constants.SPACE;

			int offset = blockSpacediv2;
			int offsetdown = 0;

			if (holeState == 0)
				offsetdown = heightdiv5 - 3;
			else if (holeState == 1)
				offsetdown = heightdiv5 + heightdiv5;
			else
				offsetdown = heightdiv5 + heightdiv5 + heightdiv5 + 3;

			int offsetup = offsetdown + heightdiv5;

			int holelen = logicHoles.size();
			for (int c = 0; c < holelen; c++) {
				int pos = logicHoles.get(c);
				if (pos > 0) {
					basePos += (width + Constants.SPACE) * pos - Constants.SPACE;
				} else {
					pos = -pos;
					int hWidth = (width + Constants.SPACE) * pos + Constants.SPACE;

					paint.setColor(Color.BLACK);
					paint.setStyle(Style.STROKE);
					Path path = new Path();
					path.moveTo(basePos - (width + Constants.SPACE) * col, y - (height + Constants.SPACE) * row);
					path.lineTo(basePos - (width + Constants.SPACE) * col + offset, y - (height + Constants.SPACE) * row + offsetdown);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth - offset, y - (height + Constants.SPACE) * row
							+ offsetdown);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth, y - (height + Constants.SPACE) * row);

					path.moveTo(basePos - (width + Constants.SPACE) * col + hWidth, y - (height + Constants.SPACE) * row + nHeight);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth - offset, y - (height + Constants.SPACE) * row
							+ offsetup);
					path.lineTo(basePos - (width + Constants.SPACE) * col + offset, y - (height + Constants.SPACE) * row + offsetup);
					path.lineTo(basePos - (width + Constants.SPACE) * col, y - (height + Constants.SPACE) * row + nHeight);
					panel.drawPath(path, paint);

					paint.setStyle(Style.FILL);
					if (selected) {
						paint.setColor(Color.GRAY);
					} else
						paint.setColor(Color.rgb(210, 210, 210));

					path = new Path();
					path.moveTo(basePos - (width + Constants.SPACE) * col, y - (height + Constants.SPACE) * row);
					path.lineTo(basePos - (width + Constants.SPACE) * col + offset, y - (height + Constants.SPACE) * row + offsetdown);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth - offset, y - (height + Constants.SPACE) * row
							+ offsetdown);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth, y - (height + Constants.SPACE) * row);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth, y - (height + Constants.SPACE) * row + nHeight);
					path.lineTo(basePos - (width + Constants.SPACE) * col + hWidth - offset, y - (height + Constants.SPACE) * row
							+ offsetup);
					path.lineTo(basePos - (width + Constants.SPACE) * col + offset, y - (height + Constants.SPACE) * row + offsetup);
					path.lineTo(basePos - (width + Constants.SPACE) * col, y - (height + Constants.SPACE) * row + nHeight);
					path.close();

					panel.drawPath(path, paint);

					basePos += (width + Constants.SPACE) * pos + Constants.SPACE;
				}
			}

			paint.setColor(oldColor);
			paint.setStyle(oldStyle);
			paint.setAlpha(oldAlpha);

		}
	}

	public int getHoleState() {
		return holeState;
	}

	public void setHoleState(int holeState) {
		this.holeState = holeState % 3;
	}

	public String showInfo() {
		String message = "Action: " + Utils.splitCamelCase(this.text) + "\n\n";

		message += "Description:\n";
		message += description + "\n\n";

		if (nowState.equals("visible")) {
			message += "State:\n";
			message += nowState + " (default)" + "\n\n";
		} else {
			message += "State:\n";
			message += nowState + "\n\n";
		}

		message += "Input(s):\n";
		for (Pin pi : in) {
			message = message + pi.description() + "\n";
		}
		if (in.size() == 0)
			message += " none\n";

		message += "\nOutput(s):\n";
		for (Pin pi : out) {
			message = message + pi.description() + "\n";
		}
		if (out.size() == 0)
			message += " none\n";
		return message;
	}

	public String showInfo2() {
		String message = "Action: " + Utils.splitCamelCase(this.text) + "\n\n";

		message += "Description:\n";

		message += description + "\n\n";

		if (!allStates.equals("visible")) {
			String[] divide = allStates.split("/");
			message += "Possible states:\n";
			for (int z = 0; z < divide.length; z++) {
				if (z == 0)
					message += " " + divide[z] + " (default)" + "\n";
				else if (z == divide.length - 1)
					message += " " + divide[z] + "\n\n";
				else
					message += " " + divide[z] + "\n";
			}
		} else {
			message += "Possible states:\n";
			message += allStates + " (default)" + "\n\n";
		}

		message += "Input(s):\n";
		for (Pin pi : in) {
			message = message + pi.description() + "\n";
		}
		if (in.size() == 0)
			message += " none\n";

		message += "\nOutput(s):\n";
		for (Pin pi : out) {
			message = message + pi.description() + "\n";
		}
		if (out.size() == 0)
			message += " none\n";

		return message;
	}

	public void shift(int column) {
		int pos = logicX;

		if (widthScale == 1) {
			if (column <= pos)
				this.moveRight(false);
		} else {
			int min = pos;
			int max = pos + widthScale - 1;
			if (column <= min)
				this.moveRight(false);
			else if (column > max) {

			} else {
				if (isHoled()) {
					int hit = column - min + 1;
					if (boolHoles.get(hit - 1) == false) {
						this.addHole(column + 1 - min);
					} else {
						int c = hit;
						while (boolHoles.get(c) == true) {
							c++;
						}
						this.addHole(c + 1);
					}

				} else
					this.addHole(column + 1 - min);

				// check hole state
				List<Shape> lp = grid.piecesForRow(logicY);
				if (lp.size() > 0) {
					int co = 0;
					for (Shape p : lp) {
						if (((Piece) p).isHoled()) {
							((Piece) p).setHoleState(co);
							co++;
						}
					}
				}

			}

		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(idlogic);
		// dest.writeList(l);
		dest.writeString(text);
		// dest.writeInt(logicX);
		// dest.writeInt(logicY);
		dest.writeInt(in.size());

		for (int i = 0; i < in.size(); i++)
			dest.writeSerializable(in.get(i));

		dest.writeInt(out.size());

		for (int i = 0; i < out.size(); i++)
			dest.writeSerializable(out.get(i));

		dest.writeString(description);
		dest.writeString(nowState);
		dest.writeString(allStates);
	}
}

package it.unisa.microapp.editor;

import java.util.ArrayList;
import java.util.List;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Paint.Style;

public class EditorGrid extends Grid {
	int startRows = 0;
	int blockSpacediv2 = Constants.SPACE >> 1;
	int blockCol;

	boolean showIconized; // mostra iconizzato
	boolean showHoles; // mostra fori

	boolean modify = false;

	List<Piece> pieces = new ArrayList<Piece>();

	int maxHdiv2;
	Paint paint;

	public EditorGrid() {
		showGrid = true;
		showIconized = false;
		showHoles = true;

		init(1, 0, 0);

		paint = new Paint();
		paint.setAntiAlias(true);

		refresh();
		setModified(false);
	}

	private void init(int numCols, int numRows, int startRow) {
		if (numCols > 0)
			this.numCols = numCols;
		if (numRows >= 0)
			this.numRows = numRows;
		if (startRow >= 0)
			startRows = startRow;

		blockCol = numCols * Constants.SPACE;
	}

	public void refresh() {
		width = Constants.GRID_RATIO - Constants.SPACE;
		height = Constants.GRID_RATIO - Constants.SPACE;

		for (Piece piece : pieces) {
			piece.refresh();
		}
	}

	// aggiornamento
	private void update(int numCols, int numRows, int startRow) {
		init(numCols, numRows, startRow);
	}

	public boolean isModified() {
		return modify;
	}

	public void setModified(boolean state) {
		modify = state;
		if (act != null)
			((EditorActivity) act).updateTitle();
	}

	// mostra iconizzata
	public boolean isShowIconized() {
		return showIconized;
	}

	public void setShowIconized(boolean showIconized) {
		this.showIconized = showIconized;
	}

	// mostra fori
	public boolean isShowHoles() {
		return showHoles;
	}

	public void setShowHoles(boolean showHoles) {
		this.showHoles = showHoles;
	}

	protected void draw(Canvas canvas, Piece selected, int col, int row) {

		if (showGrid)
			drawGrid(canvas, paint, col, row); // OK

		if (showHoles) {
			for (Piece piece : pieces)// disegna pezzi
			{
				if (showIconized) {
					piece.draw(canvas, paint, true, col, row);
				} else {
					piece.draw(canvas, paint, false, col, row);
				}
			}
			if (!showIconized)
				for (Piece piece : pieces) {
					piece.drawHoles(canvas, paint, col, row);// disegna ponte
																// Send
				}
		} else {
			if (!showIconized)
				for (Piece piece : pieces) {
					piece.drawHoles(canvas, paint, col, row);
				}

			for (Piece piece : pieces) {
				if (showIconized)
					piece.draw(canvas, paint, true, col, row);
				else {
					piece.draw(canvas, paint, false, col, row);
				}
			}
		}

		if (showGrid)// mostra il layer centrale
		{
			drawLayer(canvas, paint, col, row);
			drawSelected(canvas, paint, selected, col, row);
			drawSmallScreen(canvas, paint, col, row);
		}
	}

	private void drawLayer(Canvas panel, Paint paint, int col, int row) {
		int o = paint.getColor();
		paint.setAlpha(100);
		paint.setColor(Color.BLACK);
		panel.drawLine(0, Constants.SCREEN_SPLIT, Constants.WIDTH, Constants.SCREEN_SPLIT, paint);
		paint.setColor(Color.DKGRAY);
		paint.setAlpha(255);
		panel.drawRect(0, Constants.SCREEN_SPLIT + 1, Constants.WIDTH, Constants.HEIGTH, paint);
		paint.setColor(o);
	}

	private void drawSelected(Canvas panel, Paint paint, Piece selected, int col, int row) {
		int oldAlpha = paint.getAlpha();
		float WidthQ = Constants.BUTTON + Constants.MARGIN;
		float HeightQ = Constants.BUTTON + Constants.MARGIN;

		paint.setAlpha(80);
		panel.drawRect(Constants.MARGIN, Constants.HEIGTH - HeightQ, WidthQ, Constants.HEIGTH - Constants.MARGIN, paint);

		if (selected != null)
			if (IconsPanel.X != null) {
				IconsPanel.X.drawAsIcon(panel, paint, 0, Constants.HEIGTH - HeightQ);
			}
		paint.setAlpha(oldAlpha);
	}

	private void drawSmallScreen(Canvas panel, Paint paint, int col, int row) {
		int oldColor = paint.getColor();
		Style oldStyle = paint.getStyle();
		int oldAlpha = paint.getAlpha();
		int rig;
		float WidthQ = Constants.BUTTON + Constants.MARGIN;
		float HeightQ = Constants.BUTTON + Constants.MARGIN;
		
		paint.setAlpha(80);

		panel.drawRect(Constants.WIDTH - WidthQ, Constants.HEIGTH - HeightQ, Constants.WIDTH - Constants.MARGIN,
				Constants.HEIGTH - Constants.MARGIN, paint);

		// quadrilatero interno
		// dimensioni interne colonne e righe
		float ColW = WidthQ / numCols;
		if (numRows == 0)
			rig = 1;
		else
			rig = numRows + 1;
		float RigH = HeightQ / rig;

		// dimensione quadrilatero interno
		float Wi = (ColW * Constants.GRID_WSIZE);
		float Hi = (RigH * Constants.GRID_HSIZE);

		float Lar;
		float lung;
		float Jw = Constants.WIDTH - WidthQ + (col * ColW);
		float Kh = Constants.HEIGTH - HeightQ + (row * RigH);

		if (Constants.WIDTH - (Jw + Wi) < ColW)
			Lar = Constants.WIDTH;
		else
			Lar = Jw + Wi;

		if (Constants.HEIGTH - (Kh + Hi) < RigH)
			lung = Constants.HEIGTH;
		else
			lung = Kh + Hi;

		paint.setStyle(Style.STROKE);
		paint.setColor(Color.LTGRAY);

		float pssx = (Lar - Jw - 11) / Constants.GRID_WSIZE;
		float pssy = (lung - Kh - 7) / Constants.GRID_HSIZE;

		int px = Constants.WIDTH - ((int) WidthQ);
		for (int j = 0; j < numCols - 1; j++) {
			int py = Constants.HEIGTH - (int) HeightQ;
			for (int i = 0; i < numRows; i++) {
				if (matrix[j][i] != null) {
					panel.drawRect(px + 2, py + 2, px + pssx - 4, py + pssy - 4, paint);
				}
				py += pssy;
			}
			px += pssx;
		}

		paint.setColor(Color.YELLOW);
		paint.setAlpha(80);
		paint.setStyle(Style.FILL_AND_STROKE);
		panel.drawRect(Jw, Kh + 1, Lar - Constants.MARGIN, lung - Constants.MARGIN, paint);

				
		paint.setStyle(oldStyle);
		paint.setAlpha(oldAlpha);
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(Constants.FTEXT_SIZE);
		paint.setTextAlign(Align.LEFT);
		panel.drawText(Constants.GRID_PERCENT, Constants.WIDTH - WidthQ, Constants.HEIGTH - Constants.MARGIN - 1, paint);

		paint.setColor(oldColor);
		
	}

	@Override
	protected void drawGrid(Canvas panel, Paint paint, int col, int row) {
		int maxHdiv2 = (Constants.HEIGTH >> 1);

		// last column
		int oldColor = paint.getColor();
		Style oldStyle = paint.getStyle();
		int oldAlpha = paint.getAlpha();
		PathEffect oldpa = paint.getPathEffect();

		int cln = numCols - col;

		paint.setColor(Color.YELLOW);
		paint.setAlpha(60);
		int x = (cln - 1) * width + (Constants.SPACE * cln);
		panel.drawRect(x - Constants.SPACE, 0, x + width, Constants.HEIGTH, paint);

		// vertical line /di separazione colonne/
		x = 0;
		paint.setColor(Color.BLUE);
		for (int i = 1; i <= numCols; i++) {
			x += width + Constants.SPACE;
			panel.drawLine(x, 0, x, Constants.HEIGTH, paint);
		}

		// horizontal line down TRATTEGGIATA
		paint.setAlpha(70);
		paint.setColor(Color.RED);
		DashPathEffect dashPath = new DashPathEffect(new float[] { 10, 5 }, 1);
		Style stl = paint.getStyle();
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(dashPath);
		int yr = (numRows) * (height + Constants.SPACE);
		panel.drawLine(0, yr, x, yr, paint);

		// horizontal line up
		paint.setColor(Color.RED);
		if (startRows > 0) {
			int spo = row * (height + Constants.SPACE);
			yr = (startRows - 1) * (height + Constants.SPACE);
			panel.drawLine(0, yr - spo, x, yr - spo, paint);
		}

		paint.setStyle(stl);

		// arrow freccia
		paint.setAlpha(80);
		paint.setColor(Color.BLACK);// colore freccia
		Path path = new Path();
		x = (cln - 1) * width + (Constants.SPACE * cln);
		path.moveTo(x + Constants.SPACE, maxHdiv2 - Constants.SPACE);
		path.lineTo(x + width - (Constants.SPACE << 1), maxHdiv2 - (Constants.SPACE >> 1));
		path.lineTo(x + Constants.SPACE, maxHdiv2);
		path.close();
		panel.drawPath(path, paint);
		// paint.setAlpha(oldAlpha);

		paint.setColor(oldColor);
		paint.setStyle(oldStyle);
		paint.setAlpha(oldAlpha);
		paint.setPathEffect(oldpa);
	}

	private void setupMatrix() {
		for (int j = 0; j < this.numRows; j++)
			for (int i = 0; i < this.numCols; i++)
				matrix[i][j] = null;

		for (Piece pc : pieces) {
			updateMatrix(pc, true);
		}
	}

	private void shiftMatrix(int column) {
		for (int j = 0; j < this.numRows; j++) {
			for (int i = this.numCols - 1; i >= column; i--) {
				matrix[i][j] = matrix[i - 1][j];
			}
		}

		for (int i = 0; i < this.numRows; i++) {
			matrix[column - 1][i] = null;
		}
	}

	public void addNewPiece(Piece p) {
		pieces.add(p);
		updateMatrix(p, true);
		if (numRows == 0)
			numRows++;

		checkFirstRowMatrix();
		updateParams();
		updateGrid();

		setModified(true);
	}

	public void updatePiece(Piece p) {
		updateMatrix(p, true);
		checkFirstRowMatrix();
		updateParams();
		updateGrid();

		setModified(true);
	}

	public void checkFirstRowMatrix() {
		for (int c = 0; c < this.numCols; c++) {
			Piece fp = (Piece) matrix[c][0];
			if (fp != null) {
				if (fp.hasUpperInputPin()) {
					for (int j = 0; j < Constants.MATRIX_SIZE /* this.numRows */; j++)
						for (int i = 0; i < Constants.MATRIX_SIZE/* this.numCols */; i++)
							matrix[i][j] = null;

					for (Piece pc : pieces) {
						pc.moveUp(true);
						updateMatrix(pc, true);
					}
					return;
				}
			}
		}
	}

	public void insertColumnMatrix(int column) {
		if (column > 0 && column <= Constants.MATRIX_SIZE) {
			shiftMatrix(column);
			for (Piece pc : pieces) {
				pc.shift(column);
			}
			updateGrid();
		}

		setModified(true);
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(List<Piece> pieces) {
		this.pieces = pieces;

		setModified(true);
	}

	public void clearPieces() {
		this.pieces.clear();

		setModified(false);
	}

	private void checkSecondRowMatrix() {
		for (int c = 0; c < this.numCols; c++) {
			Piece fp = (Piece) matrix[c][0];
			if (fp != null)
				return;

			fp = (Piece) matrix[c][1];
			if (fp != null) {
				if (fp.hasUpperInputPin()) {
					return;
				}
			}
		}

		for (int j = 0; j < this.numRows; j++)
			for (int i = 0; i < this.numCols; i++)
				matrix[i][j] = null;

		for (Piece pc : pieces) {
			pc.moveUp(false);
			updateMatrix(pc, true);
		}
	}

	public void remove(Piece p) {
		if (p != null) {
			updateMatrix(p, false);

			this.pieces.remove(p);
		}
	}

	public void updateMatrix(Piece p, boolean add) {
		int x = p.getLogicPosition().x;
		int y = p.getLogicPosition().y;
		int scalex = p.getWidthScale();

		int scaley = p.getHeightScale();
		for (int i = x; i < x + scalex; i++) {
			for (int j = y; j < y + scaley; j++) {
				if (!p.isHoled(i)) {
					if (i > 0 && j > 0 && i <= Constants.MATRIX_SIZE && j <= Constants.MATRIX_SIZE) {
						if (add)
							matrix[i - 1][j - 1] = p;
						else

							matrix[i - 1][j - 1] = null;
					}
				}
			}
		}
	}

	public void updateParams() {
		// TODO: aggiornare i parametri
	}

	public Piece getSelected() {
		for (Piece piece : pieces) {
			if (piece.isSelected())
				return piece;
		}

		return null;
	}

	public boolean isSelected() {
		return (getSelected() != null);
	}

	public void unselectPieces() {
		for (Piece piece : pieces) {
			piece.setSelected(false);
			piece.setHighlithed(false);
			
			for(Pin pin : piece.getInputPin()) {
				pin.setHighlithed(false);
			}
		}
	}

	public void unselectPiece() {
		Piece selected = null;
		for (Piece pc : pieces) {
			if (pc.isSelected()) {
				selected = pc;
				break;
			}
		}

		if (selected != null) {
			selected.setSelected(false);
		}
	}

	public boolean isEmpty() {
		return (pieces.size() == 0);
	}

	public void undo() {
		if (pieces.size() > 0) {
			unselectPieces();
			Piece pr = pieces.get(pieces.size() - 1);
			pieces.remove(pieces.size() - 1);
			updateMatrix(pr, false);
			checkSecondRowMatrix();
			updateGrid();

			setModified(true);
		}
	}

	public void news() {
		if (pieces.size() > 0) {
			unselectPieces();
			clearPieces();
			setupMatrix();
			checkFirstRowMatrix();
			checkSecondRowMatrix();
			updateGrid();
		}
	}

	public void updateGrid() {
		int maxCols = 1;
		int maxRows = 0;
		int startR = Constants.MATRIX_SIZE;
		if (pieces.size() == 0) {
			startR = 1;
		} else
			for (Piece piece : pieces) {
				if (piece.getWidthScale() == 1) {
					maxCols = Math.max(maxCols, piece.getLogicPosition().x + 1);
				} else
					maxCols = Math.max(maxCols, piece.getLogicPosition().x + piece.getWidthScale());
				maxRows = Math.max(maxRows, piece.getLogicPosition().y + piece.getHeightScale() - 1);
				startR = Math.min(startR, piece.getLogicPosition().y);
			}
		update(maxCols, maxRows, startR);
	}

	public void moveUp() {
		for (Piece pc : pieces) {
			pc.moveUp(true);
		}
		setupMatrix();
		updateGrid();

		setModified(true);
	}

	public void moveDown() {
		for (Piece pc : pieces) {
			pc.moveUp(false);
		}
		setupMatrix();
		updateGrid();

		setModified(true);
	}

	public Piece showInfo() {
		Piece selected = null;
		for (Piece pc : pieces) {
			if (pc.isSelected()) {
				selected = pc;
				break;
			}
		}
		return selected;
	}

	public Point calcLogicPosition(float x, float y) {
		int lx = (int) Math.ceil(x / (width + Constants.SPACE));
		int ly = (int) Math.ceil(y / (height + Constants.SPACE));
		lx = Math.max(lx, 1);
		ly = Math.max(ly, 1);
		return new Point(lx, ly);
	}

	public Point calcSnapPosition(float x, float y) {
		Point cp = calcLogicPosition(x, y);
		int lx = cp.x;
		int ly = cp.y;
		if (lx >= this.numCols) {
			lx = this.numCols;
			if (this.startRows > 1) {
				return new Point(lx, 2);
			}
			return new Point(lx, 1);
		}

		int max = 0;
		int min = this.numRows + 1;

		// min max ly
		for (int j = 0; j <= this.numRows; j++) {
			if (matrix[lx - 1][j] != null) {
				if (min > j)
					min = j;
				if (max < j)
					max = j;
			}
		}
		if (max < min) // free column
		{
			if (this.startRows > 1) {
				return new Point(lx, 2);
			}
			return new Point(lx, 1);
		}

		// TODO: DA VERIFICARE ho sostituito < con <=
		if (ly <= this.startRows) {
			return new Point(lx, min);
		}
		return new Point(lx, max + 2);
	}

	public List<Shape> piecesForRow(int row) {
		List<Shape> lt = new ArrayList<Shape>();
		if (row > 0 && row <= numRows) {
			for (int i = 0; i < numCols; i++) {
				Piece p = (Piece) matrix[i][row - 1];
				if (p != null && !lt.contains(p)) {
					lt.add(p);
				}
			}
		}
		return lt;
	}

}

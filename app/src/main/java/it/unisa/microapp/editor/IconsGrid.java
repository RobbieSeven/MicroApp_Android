package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class IconsGrid  extends Grid
{	
	int startRows = 0;
	int blockSpacediv2 = Constants.SPACE / 2;
	int blockCol;

	List<IconPiece> pieces = new ArrayList<IconPiece>();

	Paint paint;
	//Bitmap buttonImage;

	public IconsGrid(int h, int l, int sh, int sl)
	{
		width = l;
		height = h;

		showGrid = true;

		init(1, 0, 0);

		paint = new Paint();
		paint.setAntiAlias(true);
	}

	private void init(int numCols, int numRows, int startRow)
	{
		if (numCols > 0)
			this.numCols = numCols;
		if (numRows >= 0)
			this.numRows = numRows;
		if (startRow >= 0)
			startRows = startRow;

		blockCol = numCols * Constants.SPACE;
	}

	// aggiornamento
	private void update(int numCols, int numRows, int startRow)
	{
		init(numCols, numRows, startRow);
	}

	protected void draw(Canvas canvas, int col, int row)
	{
		if (showGrid)
			drawGrid(canvas, paint, col, row);

		for (IconPiece piece : pieces)
		{
			piece.draw(canvas, paint, true, col, row);
		}
	}

	@Override
	protected void drawGrid(Canvas panel, Paint paint, int col, int row)
	{
		int oldColor = paint.getColor();

		// vertical line /di separazione colonne/	
		int x = 0; 
		paint.setColor(Color.LTGRAY);
		for (int i = 1; i < Constants.GRID_WSIZE; i++)
		{
			x += width + Constants.SPACE;
			panel.drawLine(x, 0, x, Constants.HEIGTH, paint);
		}		

		int y = 0; 
		for (int i = 1; i <= Constants.GRID_HSIZE; i++)
		{
			y += height + Constants.SPACE;
			panel.drawLine(0, y, Constants.WIDTH, y, paint);
		}		
		
		paint.setColor(oldColor);
	}



	public void addNewIconPiece(IconPiece p)
	{
		pieces.add(p);
		updateMatrix(p, true);
		if (numRows == 0)
			numRows++;

		updateGrid();
	}

	public List<IconPiece> getIconPieces()
	{
		return pieces;
	}

	public void setIconPieces(List<IconPiece> pieces)
	{
		this.pieces = pieces;
	}

	public void clearIconPieces()
	{
		this.pieces.clear();
	}

	private void updateMatrix(IconPiece p, boolean add)
	{
		int x = p.getLogicPosition().x;
		int y = p.getLogicPosition().y;
		int scalex = 1;

		int scaley = 1;
		for (int i = x; i < x + scalex; i++)
		{
			for (int j = y; j < y + scaley; j++)
			{
				if (!p.isHoled(i))
				{
					if (add)
						matrix[i - 1][j - 1] = p;
					else
						matrix[i - 1][j - 1] = null;
				}
			}
		}
	}

	public IconPiece getSelected()
	{
		for (IconPiece piece : pieces)
		{
			if (piece.isSelected())
				return piece;
		}

		return null;
	}

	public void unselectPieces()
	{
		for (IconPiece piece : pieces)
		{
			piece.setSelected(false);
		}
	}

	private void updateGrid()
	{
		int maxCols = 1;
		int maxRows = 0;
		int startR = Constants.MATRIX_SIZE;
		if (pieces.size() == 0)
		{
			startR = 1;
		} else
			for (IconPiece piece : pieces)
			{
				maxCols = Math.max(maxCols, piece.getLogicPosition().x);
				maxRows = Math.max(maxRows, piece.getLogicPosition().y + piece.getHeightScale() - 1);
				startR = Math.min(startR, piece.getLogicPosition().y);
			}
		update(maxCols, maxRows, startR);
	}

	public Point calcLogicPosition(float x, float y)
	{

		int lx = (int) Math.ceil(x / (width + Constants.SPACE));
		int ly = (int) Math.ceil(y / (height + Constants.SPACE));
		lx = Math.max(lx, 1);
		ly = Math.max(ly, 1);
		return new Point(lx, ly);
	}

	public Point calcSnapPosition(float x, float y)
	{
		Point cp = calcLogicPosition(x, y);
		int lx = cp.x;
		int ly = cp.y;
		if (lx >= this.numCols)
		{
			lx = this.numCols;
			if (this.startRows > 1)
			{
				return new Point(lx, 2);
			}
			return new Point(lx, 1);
		}

		int max = 0;
		int min = this.numRows + 1;

		// min max ly
		for (int j = 0; j <= this.numRows; j++)
		{
			if (matrix[lx - 1][j] != null)
			{
				if (min > j)
					min = j;
				if (max < j)
					max = j;
			}
		}
		if (max < min) // free column
		{
			if (this.startRows > 1)
			{
				return new Point(lx, 2);
			}
			return new Point(lx, 1);
		}

		if (ly < this.startRows)
		{
			return new Point(lx, min);
		}
		return new Point(lx, max + 2);
	}

	public List<Shape> piecesForRow(int row)
	{
		List<Shape> lt = new ArrayList<Shape>();
		if (row > 0 && row <= numRows)
		{
			for (int i = 0; i < numCols; i++)
			{
				IconPiece p = (IconPiece)matrix[i][row - 1];
				if (p != null && !lt.contains(p))
				{
					lt.add(p);
				}
			}
		}
		return lt;
	}
}

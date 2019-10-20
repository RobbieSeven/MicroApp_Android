package it.unisa.microapp.editor;

import it.unisa.microapp.utils.Constants;
import it.unisa.microapp.utils.Utils;

import java.util.List;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public abstract class Grid
{
	protected Activity act;
	
	protected int numCols = 1;
	protected int numRows = 0;
	
	protected int width = 0;
	protected int height = 0;
	
	protected boolean showGrid; // mostra griglia
	
	protected Shape matrix[][] = new Shape[Constants.MATRIX_SIZE][Constants.MATRIX_SIZE];
			
	public abstract Point calcSnapPosition(float x, float y);
	
	public abstract List<Shape> piecesForRow(int r);
		
	public abstract void unselectPieces();
	
	protected abstract void drawGrid(Canvas panel, Paint paint, int col, int row);
	
	public void setActivity(Activity act)
	{
		this.act = act;
	}	
	
	public int getNumCols()
	{
		return numCols;
	}

	public int getNumRows()
	{
		return numRows;
	}	
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
		
	public Shape[][] getMatrix()
	{
		return matrix;
	}	
	
	public Shape getMatrixPiece(Point position)
	{
		return matrix[position.x - 1][position.y - 1];
	}
	
	public void printMatrix()
	{
		Shape p;
		String temp = "";
		for (int i = 0; i < Constants.MATRIX_SIZE; i++)
		{
			for (int j = 0; j < Constants.MATRIX_SIZE; j++)
			{
				p = matrix[j][i];
				if (p != null)
				{
					temp += "X ";
				} else
					temp += "_ ";

			}
			temp += "\n";

		}
		Utils.verbose(temp);
	}	
	
	// mostra griglia
	public boolean isShowGrid()
	{
		return showGrid;
	}

	public void setShowGrid(boolean showGrid)
	{
		this.showGrid = showGrid;
	}	
	
}
